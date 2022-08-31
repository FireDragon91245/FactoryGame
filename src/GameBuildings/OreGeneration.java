package GameBuildings;

import GameCore.GameCore;
import GameItems.Inventory;
import GameItems.ItemStack;
import GameItems.Items;
import GameUtils.Vec2i;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class OreGeneration {


    public static void GenerateOres() {
        OreDataSet[][] ores = GenerateOreMap();
        for (int i = 0; i < ores.length; i++) {
            for (int j = 0; j < ores[0].length; j++) {
                if (ores[i][j] == null) {
                    continue;
                }
                Class<? extends Building> T = BuildingCore.GetClassFromBuildingType(ores[i][j].type);
                Building building;
                try {
                    building = T.getConstructor(int.class, int.class).newInstance(i, j);
                } catch (Exception e) {
                    System.out.println("hallo");
                    GameCore.setErrorGameStateException(e);
                    return;
                }
                building.overwriteInventory(generateInventory(ores[i][j].oreType, ores[i][j].type, ores[i][j].dist));
                if(!building.getInventory().isOutputEmpty()) {
                    BuildingCore.addBuilding(i, j, building);
                }
            }
        }
    }

    private static Inventory generateInventory(Ores ore, Buildings building, double dist) {
        Inventory inv = new Inventory(building);
        if(BuildingCore.getOreConfig(ore).generatesItem == Items.None){
            return inv;
        }
        Random rand = new Random();
        for(int i = 0; i < BuildingCore.getBuildingConfig(building).outputSlots; i++) {
            int amount = RandomNumber(rand, BuildingCore.getOreConfig(ore).itemMinBaseAmount, BuildingCore.getOreConfig(ore).itemMaxBaseAmount) - ((int) dist * BuildingCore.getOreConfig(ore).itemDistanceFalloff);
            if(amount <= 0) {
                inv.setOutputSlot(i, new ItemStack(Items.None, 0));
            }else{
                inv.setOutputSlot(i, new ItemStack(BuildingCore.getOreConfig(ore).generatesItem, amount));
            }
        }
        return inv;
    }

    public static OreDataSet[][] GenerateOreMap() {
        OreDataSet[][] ores = new OreDataSet[GameCore.windowWidth() / 10][GameCore.windowHeight() / 10];
        ConcurrentHashMap<Ores, ConcurrentLinkedQueue<Vec2i>> oreNodeRoots = new ConcurrentHashMap<>();
        for (Ores ore : Ores.values()) {
            if (!BuildingCore.containsOreConfig(ore)) {
                GameCore.setErrorGameStateException(new NullPointerException(String.format("Ore Config was not found in BuildingCore class for building: %s", ore.toString())));
            }
        }
        for (Ores ore : Ores.values()) {
            for (int i = 0; i < ores.length; i++) {
                for (int j = 0; j < ores[0].length; j++) {
                    if (isChance(BuildingCore.getOreConfig(ore).spawnChance)) {
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
                        if (pos.dist(pos2) < BuildingCore.getOreConfig(ore).freeSpaceRequired) {
                            oreNodeRoots.get(ore).removeIf(x -> x.compareVec(pos2));
                        }
                    }
                }
            }
        }
        for (Ores ore : oreNodeRoots.keySet()) {
            for (Vec2i pos : oreNodeRoots.get(ore)) {
                switch (BuildingCore.getOreConfig(ore).depositType) {

                    case Circle:
                        ores = GenerateCircularOrePatch(pos, ore, ores);
                        break;
                    case Linear:
                        ores = GenerateLinearOrePatch(pos, ore, ores);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + BuildingCore.getOreConfig(ore).depositType);
                }
            }
        }
        return ores;
    }


    public static int RandomNumber(Random rand, int min, int max){
        return rand.nextInt(max - min) + min;
    }

    public static OreDataSet[][] GenerateLinearOrePatch(Vec2i pos, Ores ore, OreDataSet[][] ores) {
        Random rand = new Random();
        Vec2i pos2 = pos.add(RandomNumber(rand, -BuildingCore.getOreConfig(ore).maxSpread, BuildingCore.getOreConfig(ore).maxSpread),
                RandomNumber(rand, -BuildingCore.getOreConfig(ore).maxSpread / 2, BuildingCore.getOreConfig(ore).maxSpread / 2)
        );

        ConcurrentLinkedQueue<Vec2i> lineVects = new ConcurrentLinkedQueue<>();

        int difx = pos2.x - pos.x;
        int dify = pos2.y - pos.y;

        if(pos.x < pos2.x){
            for(int i = pos.x; i < pos2.x; i++){
                int res = pos.y + dify * (i - pos.x) / difx;
                if(i >= 0 && i < ores.length && res >= 0 && res < ores[0].length) {
                    ores[i][res] = new OreDataSet(0, BuildingCore.getOreConfig(ore).buildingID, ore);
                }
                lineVects.add(new Vec2i(i, res));
            }
        }else{
            for(int i = pos.x; i > pos2.x; i--){
                int res = pos.y + dify * (i - pos.x) / difx;
                if(i >= 0 && i < ores.length && res >= 0 && res < ores[0].length) {
                    ores[i][res] = new OreDataSet(0, BuildingCore.getOreConfig(ore).buildingID, ore);
                }
                lineVects.add(new Vec2i(i, res));
            }
        }

        for(Vec2i vec : lineVects){
            ores = GenerateCirculatOrePatchAround(vec, ores, BuildingCore.getOreConfig(ore).maxSpread / 10, BuildingCore.getOreConfig(ore).initialOreChance, BuildingCore.getOreConfig(ore).oreChanceFalloff, BuildingCore.getOreConfig(ore).buildingID, ore);
        }
        return ores;
    }

    private static OreDataSet[][] GenerateCirculatOrePatchAround(Vec2i pos, OreDataSet[][] ores, int spread, double initChance, double falloff, Buildings type, Ores oreType) {
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

    private static OreDataSet[][] GenerateCircularOrePatch(Vec2i pos, Ores ore, OreDataSet[][] ores) {
        ConcurrentLinkedQueue<Vec2i> toTest = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<Vec2i> placed = new ConcurrentLinkedQueue<>();
        placed.add(pos);
        toTest.add(pos);
        while (toTest.size() != 0) {
            for (Vec2i test : toTest) {
                if (placed.contains(test)) {
                    toTest.removeIf(x -> x.compareVec(test));
                }
                if (pos.dist(test) > BuildingCore.getOreConfig(ore).maxSpread) {
                    toTest.removeIf(x -> x.compareVec(test));
                    continue;
                }
                if (!placed.contains(new Vec2i(test.x + 1, test.y))) {
                    if (isChance(BuildingCore.getOreConfig(ore).initialOreChance - pos.dist(new Vec2i(test.x + 1, test.y)) * BuildingCore.getOreConfig(ore).oreChanceFalloff)) {
                        if (!toTest.contains(new Vec2i(test.x + 1, test.y))) {
                            toTest.add(new Vec2i(test.x + 1, test.y));
                        }
                        if (!placed.contains(new Vec2i(test.x + 1, test.y))) {
                            placed.add(new Vec2i(test.x + 1, test.y));
                        }
                    }
                }
                if (!placed.contains(new Vec2i(test.x - 1, test.y))) {
                    if (isChance(BuildingCore.getOreConfig(ore).initialOreChance - pos.dist(new Vec2i(test.x - 1, test.y)) * BuildingCore.getOreConfig(ore).oreChanceFalloff)) {
                        if (!toTest.contains(new Vec2i(test.x - 1, test.y))) {
                            toTest.add(new Vec2i(test.x - 1, test.y));
                        }
                        if (!placed.contains(new Vec2i(test.x - 1, test.y))) {
                            placed.add(new Vec2i(test.x - 1, test.y));
                        }
                    }
                }
                if (!placed.contains(new Vec2i(test.x, test.y + 1))) {
                    if (isChance(BuildingCore.getOreConfig(ore).initialOreChance - pos.dist(new Vec2i(test.x, test.y + 1)) * BuildingCore.getOreConfig(ore).oreChanceFalloff)) {
                        if (!toTest.contains(new Vec2i(test.x, test.y + 1))) {
                            toTest.add(new Vec2i(test.x, test.y + 1));
                        }
                        if (!placed.contains(new Vec2i(test.x, test.y + 1))) {
                            placed.add(new Vec2i(test.x, test.y + 1));
                        }
                    }
                }
                if (!placed.contains(new Vec2i(test.x, test.y - 1))) {
                    if (isChance(BuildingCore.getOreConfig(ore).initialOreChance - pos.dist(new Vec2i(test.x, test.y - 1)) * BuildingCore.getOreConfig(ore).oreChanceFalloff)) {
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
                    ores[vec.x][vec.y] = new OreDataSet(pos.dist(vec), BuildingCore.getOreConfig(ore).buildingID, ore);
                }
            }
        }
        return ores;
    }

    public static boolean isChance(double percentage) {
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

    public static class OreDataSet {
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
