package GameBuildings;

import GameCore.Main;
import GameItems.Inventory;
import GameItems.ItemStack;
import GameItems.Items;
import GameUtils.Vec2i;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class OreGeneration {


    public void GenerateOres(BuildingCore generateTo) {
        OreDataSet[][] ores = GenerateOreMap();
        for (int i = 0; i < ores.length; i++) {
            for (int j = 0; j < ores[0].length; j++) {
                if (ores[i][j] == null) {
                    continue;
                }
                Class<? extends Building> T = Main.getClient().buildingCore().GetClassFromBuildingType(ores[i][j].type);
                Building building;
                try {
                    building = T.getConstructor(int.class, int.class).newInstance(i, j);
                } catch (Exception e) {
                    System.out.println("hallo");
                    Main.getClient().setErrorGameStateException(e);
                    return;
                }
                building.overwriteInventory(generateInventory(ores[i][j].oreType, ores[i][j].type, ores[i][j].dist));
                if(!building.getInventory().isOutputEmpty()) {
                    generateTo.addBuilding(building);
                }
            }
        }
    }

    private Inventory generateInventory(Ores ore, Buildings building, double dist) {
        Inventory inv = new Inventory(building.toString());
        if(Main.getClient().buildingCore().getOreConfig(ore).getGeneratesItem() == Items.None){
            return inv;
        }
        Random rand = new Random();
        for(int i = 0; i < Main.getClient().buildingCore().getBuildingConfig(building).getOutputSlots(); i++) {
            int amount = RandomNumber(rand, Main.getClient().buildingCore().getOreConfig(ore).getItemMinBaseAmount(), Main.getClient().buildingCore().getOreConfig(ore).getItemMaxBaseAmount()) - ((int) dist * Main.getClient().buildingCore().getOreConfig(ore).getItemDistanceFalloff());
            if(amount <= 0) {
                inv.setOutputSlot(i, new ItemStack(Items.None, 0));
            }else{
                inv.setOutputSlot(i, new ItemStack(Main.getClient().buildingCore().getOreConfig(ore).getGeneratesItem(), amount));
            }
        }
        return inv;
    }

    public OreDataSet[][] GenerateOreMap() {
        OreDataSet[][] ores = new OreDataSet[Main.getClient().windowWidth() / 10][Main.getClient().windowHeight() / 10];
        ConcurrentHashMap<Ores, ConcurrentLinkedQueue<Vec2i>> oreNodeRoots = new ConcurrentHashMap<>();
        for (Ores ore : Ores.values()) {
            if (!Main.getClient().buildingCore().containsOreConfig(ore)) {
                Main.getClient().setErrorGameStateException(new NullPointerException(String.format("Ore Config was not found in BuildingCore class for building: %s", ore.toString())));
            }
        }
        for (Ores ore : Ores.values()) {
            for (int i = 0; i < ores.length; i++) {
                for (int j = 0; j < ores[0].length; j++) {
                    if (isChance(Main.getClient().buildingCore().getOreConfig(ore).getSpawnChance())) {
                        if (oreNodeRoots.containsKey(ore)) {
                            oreNodeRoots.get(ore).add(new Vec2i(i, j));
                        } else {
                            oreNodeRoots.put(ore, new ConcurrentLinkedQueue<>());
                            oreNodeRoots.get(ore).add(new Vec2i(i, j));
                        }
                    }
                }
            }
        }
        for (Ores ore : oreNodeRoots.keySet()) {
            for (Vec2i pos : oreNodeRoots.get(ore)) {
                for (Vec2i pos2 : oreNodeRoots.get(ore)) {
                    if (!pos.compareVec(pos2)) {
                        if (pos.dist(pos2) < Main.getClient().buildingCore().getOreConfig(ore).getFreeSpaceRequired()) {
                            oreNodeRoots.get(ore).removeIf(x -> x.compareVec(pos2));
                        }
                    }
                }
            }
        }
        for (Ores ore : oreNodeRoots.keySet()) {
            for (Vec2i pos : oreNodeRoots.get(ore)) {
                ores = switch (Main.getClient().buildingCore().getOreConfig(ore).getDepositType()) {
                    case Circle -> GenerateCircularOrePatch(pos, ore, ores);
                    case Linear -> GenerateLinearOrePatch(pos, ore, ores);
                };
            }
        }
        return ores;
    }


    public int RandomNumber(Random rand, int min, int max){
        return rand.nextInt(max - min) + min;
    }

    public OreDataSet[][] GenerateLinearOrePatch(Vec2i pos, Ores ore, OreDataSet[][] ores) {
        Random rand = new Random();
        Vec2i pos2 = pos.add(RandomNumber(rand, - Main.getClient().buildingCore().getOreConfig(ore).getMaxSpread(), Main.getClient().buildingCore().getOreConfig(ore).getMaxSpread()),
                RandomNumber(rand, - Main.getClient().buildingCore().getOreConfig(ore).getMaxSpread() / 2, Main.getClient().buildingCore().getOreConfig(ore).getMaxSpread() / 2)
        );

        ConcurrentLinkedQueue<Vec2i> lineVecs = new ConcurrentLinkedQueue<>();

        int difx = pos2.x - pos.x;
        int dify = pos2.y - pos.y;

        if(pos.x < pos2.x){
            for(int i = pos.x; i < pos2.x; i++){
                int res = pos.y + dify * (i - pos.x) / difx;
                if(i >= 0 && i < ores.length && res >= 0 && res < ores[0].length) {
                    ores[i][res] = new OreDataSet(0, Main.getClient().buildingCore().getOreConfig(ore).getBuildingID(), ore);
                }
                lineVecs.add(new Vec2i(i, res));
            }
        }else{
            for(int i = pos.x; i > pos2.x; i--){
                int res = pos.y + dify * (i - pos.x) / difx;
                if(i >= 0 && i < ores.length && res >= 0 && res < ores[0].length) {
                    ores[i][res] = new OreDataSet(0, Main.getClient().buildingCore().getOreConfig(ore).getBuildingID(), ore);
                }
                lineVecs.add(new Vec2i(i, res));
            }
        }

        for(Vec2i vec : lineVecs){
            ores = GenerateCirculatOrePatchAround(vec, ores, Main.getClient().buildingCore().getOreConfig(ore).getMaxSpread() / 10, Main.getClient().buildingCore().getOreConfig(ore).getInitialOreChance(), Main.getClient().buildingCore().getOreConfig(ore).getOreChanceFalloff(), Main.getClient().buildingCore().getOreConfig(ore).getBuildingID(), ore);
        }
        return ores;
    }

    private OreDataSet[][] GenerateCirculatOrePatchAround(Vec2i pos, OreDataSet[][] ores, int spread, double initChance, double falloff, Buildings type, Ores oreType) {
        ConcurrentLinkedQueue<Vec2i> toTest = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<Vec2i> placed = new ConcurrentLinkedQueue<>();
        placed.add(pos);
        toTest.add(pos);
        while (toTest.size() != 0) {
            for (Vec2i test : toTest) {
                if (placed.contains(test)) {
                    toTest.removeIf(x -> x.compareVec(test));
                }
                if (pos.dist(test) > spread) {
                    toTest.removeIf(x -> x.compareVec(test));
                    continue;
                }
                if (!placed.contains(new Vec2i(test.x + 1, test.y))) {
                    if (isChance(initChance - pos.dist(new Vec2i(test.x + 1, test.y)) * falloff)) {
                        if (!toTest.contains(new Vec2i(test.x + 1, test.y))) {
                            toTest.add(new Vec2i(test.x + 1, test.y));
                        }
                        if (!placed.contains(new Vec2i(test.x + 1, test.y))) {
                            placed.add(new Vec2i(test.x + 1, test.y));
                        }
                    }
                }
                if (!placed.contains(new Vec2i(test.x - 1, test.y))) {
                    if (isChance(initChance - pos.dist(new Vec2i(test.x - 1, test.y)) * falloff)) {
                        if (!toTest.contains(new Vec2i(test.x - 1, test.y))) {
                            toTest.add(new Vec2i(test.x - 1, test.y));
                        }
                        if (!placed.contains(new Vec2i(test.x - 1, test.y))) {
                            placed.add(new Vec2i(test.x - 1, test.y));
                        }
                    }
                }
                if (!placed.contains(new Vec2i(test.x, test.y + 1))) {
                    if (isChance(initChance - pos.dist(new Vec2i(test.x, test.y + 1)) * falloff)) {
                        if (!toTest.contains(new Vec2i(test.x, test.y + 1))) {
                            toTest.add(new Vec2i(test.x, test.y + 1));
                        }
                        if (!placed.contains(new Vec2i(test.x, test.y + 1))) {
                            placed.add(new Vec2i(test.x, test.y + 1));
                        }
                    }
                }
                if (!placed.contains(new Vec2i(test.x, test.y - 1))) {
                    if (isChance(initChance - pos.dist(new Vec2i(test.x, test.y - 1)) * falloff)) {
                        if (!toTest.contains(new Vec2i(test.x, test.y - 1))) {
                            toTest.add(new Vec2i(test.x, test.y - 1));
                        }
                        if (!toTest.contains(new Vec2i(test.x, test.y - 1))) {
                            placed.add(new Vec2i(test.x, test.y - 1));
                        }
                    }
                }
                toTest.removeIf(x -> x.compareVec(test));
            }
        }
        for (Vec2i vec : placed) {
            if (vec.x >= 0 && vec.x < ores.length) {
                if (vec.y >= 0 && vec.y < ores[0].length) {
                    ores[vec.x][vec.y] = new OreDataSet(pos.dist(vec), type, oreType);
                }
            }
        }
        return ores;
    }

    private OreDataSet[][] GenerateCircularOrePatch(Vec2i pos, Ores ore, OreDataSet[][] ores) {
        ConcurrentLinkedQueue<Vec2i> toTest = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<Vec2i> placed = new ConcurrentLinkedQueue<>();
        placed.add(pos);
        toTest.add(pos);
        while (toTest.size() != 0) {
            for (Vec2i test : toTest) {
                if (placed.contains(test)) {
                    toTest.removeIf(x -> x.compareVec(test));
                }
                if (pos.dist(test) > Main.getClient().buildingCore().getOreConfig(ore).getMaxSpread()) {
                    toTest.removeIf(x -> x.compareVec(test));
                    continue;
                }
                if (!placed.contains(new Vec2i(test.x + 1, test.y))) {
                    if (isChance(Main.getClient().buildingCore().getOreConfig(ore).getInitialOreChance() - pos.dist(new Vec2i(test.x + 1, test.y)) * Main.getClient().buildingCore().getOreConfig(ore).getOreChanceFalloff())) {
                        if (!toTest.contains(new Vec2i(test.x + 1, test.y))) {
                            toTest.add(new Vec2i(test.x + 1, test.y));
                        }
                        if (!placed.contains(new Vec2i(test.x + 1, test.y))) {
                            placed.add(new Vec2i(test.x + 1, test.y));
                        }
                    }
                }
                if (!placed.contains(new Vec2i(test.x - 1, test.y))) {
                    if (isChance(Main.getClient().buildingCore().getOreConfig(ore).getInitialOreChance() - pos.dist(new Vec2i(test.x - 1, test.y)) * Main.getClient().buildingCore().getOreConfig(ore).getOreChanceFalloff())) {
                        if (!toTest.contains(new Vec2i(test.x - 1, test.y))) {
                            toTest.add(new Vec2i(test.x - 1, test.y));
                        }
                        if (!placed.contains(new Vec2i(test.x - 1, test.y))) {
                            placed.add(new Vec2i(test.x - 1, test.y));
                        }
                    }
                }
                if (!placed.contains(new Vec2i(test.x, test.y + 1))) {
                    if (isChance(Main.getClient().buildingCore().getOreConfig(ore).getInitialOreChance() - pos.dist(new Vec2i(test.x, test.y + 1)) * Main.getClient().buildingCore().getOreConfig(ore).getOreChanceFalloff())) {
                        if (!toTest.contains(new Vec2i(test.x, test.y + 1))) {
                            toTest.add(new Vec2i(test.x, test.y + 1));
                        }
                        if (!placed.contains(new Vec2i(test.x, test.y + 1))) {
                            placed.add(new Vec2i(test.x, test.y + 1));
                        }
                    }
                }
                if (!placed.contains(new Vec2i(test.x, test.y - 1))) {
                    if (isChance(Main.getClient().buildingCore().getOreConfig(ore).getInitialOreChance() - pos.dist(new Vec2i(test.x, test.y - 1)) * Main.getClient().buildingCore().getOreConfig(ore).getOreChanceFalloff())) {
                        if (!toTest.contains(new Vec2i(test.x, test.y - 1))) {
                            toTest.add(new Vec2i(test.x, test.y - 1));
                        }
                        if (!toTest.contains(new Vec2i(test.x, test.y - 1))) {
                            placed.add(new Vec2i(test.x, test.y - 1));
                        }
                    }
                }
                toTest.removeIf(x -> x.compareVec(test));
            }
        }
        for (Vec2i vec : placed) {
            if (vec.x >= 0 && vec.x < ores.length) {
                if (vec.y >= 0 && vec.y < ores[0].length) {
                    ores[vec.x][vec.y] = new OreDataSet(pos.dist(vec), Main.getClient().buildingCore().getOreConfig(ore).getBuildingID(), ore);
                }
            }
        }
        return ores;
    }

    public boolean isChance(double percentage) {
        double newPercentage = Math.round(percentage * 100) / 100.0;
        if (percentage < 0) {
            return false;
        }
        int percentageMultiplier = 1;
        while (newPercentage % 1 != 0) {
            newPercentage = Math.round(newPercentage * 100) / 10.0;
            percentageMultiplier *= 10;
        }

        Random rand = new Random();
        int res = rand.nextInt(percentageMultiplier * 100 + 1);
        return res < newPercentage;
    }

    public class OreDataSet {
        public final double dist;
        public final Buildings type;
        public final Ores oreType;

        public OreDataSet(double dist, Buildings type, Ores oreType) {
            this.dist = dist;
            this.type = type;
            this.oreType = oreType;
        }
    }
}
