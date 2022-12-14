package GameBuildings;

import GameCore.Main;
import GameCore.UpdateDirections;
import GameItems.*;
import GameUtils.Vec2i;
import Packages.Vanilla.Buildings.CoalOre;
import Packages.Vanilla.Buildings.IronOre;
import Packages.Vanilla.Buildings.Mine;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BuildingCore {

    private final OreGeneration oreGenerator = new OreGeneration();
    private final HashMap<Integer, HashMap<Integer, Building>> buildings = new HashMap<>();
    private final HashMap<String, BuildingConfig> bConfig = new HashMap<>();
    private final HashMap<String, Class<? extends Building>> bClass = new HashMap<>();
    private final HashMap<Ores, OreConfig> oConfig = new HashMap<>();

    public void setBuildingConfig(Buildings type, BuildingConfig config) {
        bConfig.put(type.toString(), config);
    }

    public BuildingConfig getBuildingConfig(Buildings type) {
        return bConfig.getOrDefault(type, null);
    }

    public BuildingConfig getBuildingConfig(String bId) {
        return bConfig.getOrDefault(bId, null);
    }

    public Class<? extends Building> GetClassFromBuildingType(Buildings type) {
        return switch (type) {
            case Mine -> Mine.class;
            case IronOre -> IronOre.class;
            case CoalOre -> CoalOre.class;
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }

    public void populate(){
        oreGenerator.GenerateOres(this);
    }

    public HashMap<Integer, HashMap<Integer, Building>> getAllBuildings() {
        return buildings;
    }

    public void addBuilding(@NotNull Building building) {
        if (!buildings.containsKey(building.getX())) {
            buildings.put(building.getX(), new HashMap<>());
        }
        building.updateSelfInventoryTargetAfterPlace(getNeighborTypes(building.getX(), building.getY()));
        buildings.get(building.getX()).put(building.getY(), building);
        sendNeighborChange(building.getX(), building.getY(), building.getType());
    }

    private ArrayList<String> getNeighborTypes(int x, int y) {
        ArrayList<String> types = new ArrayList<>(4);
        if (isInBounds(x - 1, y)) {
            Building b = getBuilding(x - 1, y);
            if (b != null) {
                types.add(b.getType());
            }
        }
        if (isInBounds(x + 1, y)) {
            Building b = getBuilding(x + 1, y);
            if (b != null) {
                types.add(b.getType());
            }
        }
        if (isInBounds(x, y - 1)) {
            Building b = getBuilding(x, y - 1);
            if (b != null) {
                types.add(b.getType());
            }
        }
        if (isInBounds(x, y + 1)) {
            Building b = getBuilding(x, y + 1);
            if (b != null) {
                types.add(b.getType());
            }
        }
        return types;
    }

    private void sendNeighborChange(int x, int y, String type) {
        if (isInBounds(x - 1, y)) {
            Building b = getBuilding(x - 1, y);
            if (b != null) {
                b.neighborAdded(type);
            }
        }
        if (isInBounds(x + 1, y)) {
            Building b = getBuilding(x + 1, y);
            if (b != null) {
                b.neighborAdded(type);
            }
        }
        if (isInBounds(x, y - 1)) {
            Building b = getBuilding(x, y - 1);
            if (b != null) {
                b.neighborAdded(type);
            }
        }
        if (isInBounds(x, y + 1)) {
            Building b = getBuilding(x, y + 1);
            if (b != null) {
                b.neighborAdded(type);
            }
        }
    }

    private boolean isInBounds(int x, int y) {
        if (x < 0 || y < 0) {
            return false;
        }
        return x <= Main.getClient().windowWidth() / 10 && y <= Main.getClient().windowWidth() / 10;
    }

    public Building getBuilding(int x, int y) {
        if (buildings.containsKey(x)) {
            return buildings.get(x).getOrDefault(y, null);
        }
        return null;
    }

    public boolean isBuildingAtPos(int x, int y) {
        if (buildings.containsKey(x)) {
            return buildings.get(x).containsKey(y);
        }
        return false;
    }

    public OreConfig getOreConfig(Ores ore) {
        return oConfig.getOrDefault(ore, null);
    }

    public void setOreConfig(Ores ore, OreConfig oreConfig) {
        oConfig.put(ore, oreConfig);
    }

    public boolean containsBuildingConfig(Buildings b) {
        return bConfig.containsKey(b);
    }

    public boolean containsOreConfig(Ores ore) {
        return oConfig.containsKey(ore);
    }

    public boolean isBuildingAtPos(Vec2i gridPos) {
        if (buildings.containsKey(gridPos.x)) {
            return buildings.get(gridPos.x).containsKey(gridPos.y);
        }
        return false;
    }

    public Building getBuilding(Vec2i pos) {
        if (buildings.containsKey(pos.x)) {
            return buildings.get(pos.x).getOrDefault(pos.y, null);
        }
        return null;
    }

    public void handleDirectImportsToOutputFor(Vec2i pos, Building b) {
        for(Directions dir : bConfig.get(b.getType()).getInventoryConfig().importDirections){
            Vec2i currPos = pos.add(directionToRelativeCoordinate(dir));
            if(!isInBounds(currPos)){
                continue;
            }
            if(Arrays.stream(bConfig.get(b.getType()).getInventoryConfig().blockedInputDirections).anyMatch(x -> x == dir)){
                continue;
            }
            if(!isBuildingAtPos(currPos)){
                continue;
            }
            Building b2 = getBuilding(currPos);
            if(b2 == null){
                continue;
            }
            if(Arrays.stream(bConfig.get(b.getType()).getInventoryConfig().thisImportsFrom).noneMatch(x -> x == b2.getType())){
                continue;
            }
            if(Arrays.stream(bConfig.get(b2.getType()).getInventoryConfig().blockedOutputDirections).anyMatch(x -> x == getOppositeDirection(dir))){
                continue;
            }
            if(Arrays.stream(bConfig.get(b2.getType()).getInventoryConfig().otherBuildingsBlockedExport).anyMatch(x -> x == b.getType())){
                continue;
            }

            Inventory invBuilding1 = b.getInventory();
            Inventory invBuilding2 = b2.getInventory();
            if(invBuilding1 == null || invBuilding2 == null){
                continue;
            }
            for(int b2Slot = 0; b2Slot < bConfig.get(b2.getType()).getOutputSlots(); b2Slot++){
                if(invBuilding2.getOutputSlot(b2Slot).getItemType() == Items.None || invBuilding2.getOutputSlot(b2Slot).getItemType() == Items.Blocked){
                    continue;
                }
                boolean success = false;
                boolean success2 = false;
                for(int bSlot = 0; bSlot < bConfig.get(b.getType()).getOutputSlots(); bSlot++){
                    if(invBuilding1.getOutputSlot(bSlot).getItemType() == Items.Blocked){
                        continue;
                    }
                    if(invBuilding1.getOutputSlot(bSlot).getItemType() == invBuilding2.getOutputSlot(b2Slot).getItemType()){
                        if(this.getBuildingConfig(b.getType()).getInventoryConfig().importSpeed < invBuilding2.getOutputSlot(b2Slot).getItemCount()) {
                            invBuilding1.getOutputSlot(bSlot).addItemCount(this.getBuildingConfig(b.getType()).getInventoryConfig().importSpeed);
                            invBuilding2.getOutputSlot(b2Slot).removeItemCount(this.getBuildingConfig(b.getType()).getInventoryConfig().importSpeed);
                        }else{
                            invBuilding1.getOutputSlot(bSlot).addItemCount(invBuilding2.getOutputSlot(b2Slot).getItemCount());
                            invBuilding2.setOutputSlot(b2Slot, new ItemStack(Items.None, 0));
                        }
                        success = true;
                        success2 = true;
                        break;
                    }
                }
                if(!success){
                    for(int bSlot = 0; bSlot < bConfig.get(b.getType()).getOutputSlots(); bSlot++){
                        if(invBuilding1.getOutputSlot(bSlot).getItemType() == Items.None){
                            if(this.getBuildingConfig(b.getType()).getInventoryConfig().importSpeed < invBuilding2.getOutputSlot(b2Slot).getItemCount()) {
                                invBuilding1.setOutputSlot(bSlot, new ItemStack(invBuilding2.getOutputSlot(b2Slot).getItemType(), this.getBuildingConfig(b.getType()).getInventoryConfig().importSpeed));
                                invBuilding2.getOutputSlot(b2Slot).removeItemCount(this.getBuildingConfig(b.getType()).getInventoryConfig().importSpeed);
                            }else{
                                invBuilding1.setOutputSlot(bSlot, new ItemStack(invBuilding2.getOutputSlot(b2Slot).getItemType(), invBuilding2.getOutputSlot(b2Slot).getItemCount()));
                                invBuilding2.setOutputSlot(b2Slot, new ItemStack(Items.None, 0));
                            }
                            success2 = true;
                            break;
                        }
                    }
                }
                if(success2){
                    break;
                }
            }
        }
    }

    public void handleDirectImportsToOutputForWorkControlled(Vec2i pos, Building b) {
        for(Directions dir : bConfig.get(b.getType()).getInventoryConfig().importDirections){
            Vec2i currPos = pos.add(directionToRelativeCoordinate(dir));
            if(!isInBounds(currPos)){
                continue;
            }
            if(Arrays.stream(bConfig.get(b.getType()).getInventoryConfig().blockedInputDirections).anyMatch(x -> x == dir)){
                continue;
            }
            if(!isBuildingAtPos(currPos)){
                continue;
            }
            Building b2 = getBuilding(currPos);
            if(b2 == null){
                continue;
            }
            if(Arrays.stream(bConfig.get(b.getType()).getInventoryConfig().thisImportsFrom).noneMatch(x -> x == b2.getType())){
                continue;
            }
            if(Arrays.stream(bConfig.get(b2.getType()).getInventoryConfig().blockedOutputDirections).anyMatch(x -> x == getOppositeDirection(dir))){
                continue;
            }
            if(Arrays.stream(bConfig.get(b2.getType()).getInventoryConfig().otherBuildingsBlockedExport).anyMatch(x -> x == b.getType())){
                continue;
            }

            Inventory invBuilding1;
            invBuilding1 = b.getInventory();
            Inventory invBuilding2;
            invBuilding2 = b2.getInventory();
            if(invBuilding1 == null || invBuilding2 == null){
                continue;
            }
            for(int b2Slot = 0; b2Slot < bConfig.get(b2.getType()).getOutputSlots(); b2Slot++){
                if(invBuilding2.getOutputSlot(b2Slot).getItemType() == Items.None || invBuilding2.getOutputSlot(b2Slot).getItemType() == Items.Blocked){
                    continue;
                }
                boolean success = false;
                boolean success2 = false;
                for(int bSlot = 0; bSlot < bConfig.get(b.getType()).getOutputSlots(); bSlot++){
                    if(invBuilding1.getOutputSlot(bSlot).getItemType() == Items.Blocked){
                        continue;
                    }
                    if(invBuilding1.getOutputSlot(bSlot).getItemType() == invBuilding2.getOutputSlot(b2Slot).getItemType()){
                        if(this.getBuildingConfig(b.getType()).getInventoryConfig().importSpeed < invBuilding2.getOutputSlot(b2Slot).getItemCount()) {
                            invBuilding1.getOutputSlot(bSlot).addItemCount(this.getBuildingConfig(b.getType()).getInventoryConfig().importSpeed);
                            invBuilding2.getOutputSlot(b2Slot).removeItemCount(this.getBuildingConfig(b.getType()).getInventoryConfig().importSpeed);
                        }else{
                            invBuilding1.getOutputSlot(bSlot).addItemCount(invBuilding2.getOutputSlot(b2Slot).getItemCount());
                            invBuilding2.setOutputSlot(b2Slot, new ItemStack(Items.None, 0));
                        }
                        success = true;
                        success2 = true;
                        break;
                    }
                }
                if(!success){
                    for(int bSlot = 0; bSlot < bConfig.get(b.getType()).getOutputSlots(); bSlot++){
                        if(invBuilding1.getOutputSlot(bSlot).getItemType() == Items.None){
                            if(this.getBuildingConfig(b.getType()).getInventoryConfig().importSpeed < invBuilding2.getOutputSlot(b2Slot).getItemCount()) {
                                invBuilding1.setOutputSlot(bSlot, new ItemStack(invBuilding2.getOutputSlot(b2Slot).getItemType(), this.getBuildingConfig(b.getType()).getInventoryConfig().importSpeed));
                                invBuilding2.getOutputSlot(b2Slot).removeItemCount(this.getBuildingConfig(b.getType()).getInventoryConfig().importSpeed);
                            }else{
                                invBuilding1.setOutputSlot(bSlot, new ItemStack(invBuilding2.getOutputSlot(b2Slot).getItemType(), invBuilding2.getOutputSlot(b2Slot).getItemCount()));
                                invBuilding2.setOutputSlot(b2Slot, new ItemStack(Items.None, 0));
                            }
                            success2 = true;
                            break;
                        }
                    }
                }
                if(success2){
                    break;
                }
            }
        }
    }

    private void handleImportsFor(Vec2i pos, Building b) {
        for(Directions dir : bConfig.get(b.getType()).getInventoryConfig().importDirections){
            Vec2i currPos = pos.add(directionToRelativeCoordinate(dir));
            if(!isInBounds(currPos)){
                continue;
            }
            if(Arrays.stream(bConfig.get(b.getType()).getInventoryConfig().blockedInputDirections).anyMatch(x -> x == dir)){
                continue;
            }
            if(!isBuildingAtPos(currPos)){
                continue;
            }
            Building b2 = getBuilding(currPos);
            if(b2 == null){
                continue;
            }
            if(Arrays.stream(bConfig.get(b.getType()).getInventoryConfig().thisImportsFrom).noneMatch(x -> x == b2.getType())){
                continue;
            }
            if(Arrays.stream(bConfig.get(b2.getType()).getInventoryConfig().blockedOutputDirections).anyMatch(x -> x == getOppositeDirection(dir))){
                continue;
            }
            if(Arrays.stream(bConfig.get(b2.getType()).getInventoryConfig().otherBuildingsBlockedExport).anyMatch(x -> x == b.getType())){
                continue;
            }

            Inventory invBuilding1 = b.getInventory();
            Inventory invBuilding2 = b2.getInventory();
            if(invBuilding1 == null || invBuilding2 == null){
                continue;
            }
            for(int b2Slot = 0; b2Slot < bConfig.get(b2.getType()).getOutputSlots(); b2Slot++){
                if(invBuilding2.getOutputSlot(b2Slot).getItemType() == Items.None || invBuilding2.getOutputSlot(b2Slot).getItemType() == Items.Blocked){
                    continue;
                }
                boolean success = false;
                boolean success2 = false;
                for(int bSlot = 0; bSlot < bConfig.get(b.getType()).getInputSlots(); bSlot++){
                    if(invBuilding1.getInputSlot(bSlot).getItemType() == Items.Blocked){
                        continue;
                    }
                    if(invBuilding1.getInputSlot(bSlot).getItemType() == invBuilding2.getOutputSlot(b2Slot).getItemType()){
                        if(this.getBuildingConfig(b.getType()).getInventoryConfig().importSpeed < invBuilding2.getOutputSlot(b2Slot).getItemCount()) {
                            invBuilding1.getInputSlot(bSlot).addItemCount(this.getBuildingConfig(b.getType()).getInventoryConfig().importSpeed);
                            invBuilding2.getOutputSlot(b2Slot).removeItemCount(this.getBuildingConfig(b.getType()).getInventoryConfig().importSpeed);
                        }else{
                            invBuilding1.getInputSlot(bSlot).addItemCount(invBuilding2.getOutputSlot(b2Slot).getItemCount());
                            invBuilding2.setOutputSlot(b2Slot, new ItemStack(Items.None, 0));
                        }
                        success = true;
                        success2 = true;
                        break;
                    }
                }
                if(!success){
                    for(int bSlot = 0; bSlot < bConfig.get(b.getType()).getInputSlots(); bSlot++){
                        if(invBuilding1.getInputSlot(bSlot).getItemType() == Items.None){
                            if(this.getBuildingConfig(b.getType()).getInventoryConfig().importSpeed < invBuilding2.getOutputSlot(b2Slot).getItemCount()) {
                                invBuilding1.setInputSlot(bSlot, new ItemStack(invBuilding2.getOutputSlot(b2Slot).getItemType(), this.getBuildingConfig(b.getType()).getInventoryConfig().importSpeed));
                                invBuilding2.getOutputSlot(b2Slot).removeItemCount(this.getBuildingConfig(b.getType()).getInventoryConfig().importSpeed);
                            }else{
                                invBuilding1.setInputSlot(bSlot, new ItemStack(invBuilding2.getOutputSlot(b2Slot).getItemType(), invBuilding2.getOutputSlot(b2Slot).getItemCount()));
                                invBuilding2.setOutputSlot(b2Slot, new ItemStack(Items.None, 0));
                            }
                            success2 = true;
                            break;
                        }
                    }
                }
                if(success2){
                    break;
                }
            }
        }
    }

    private double getItemStackVolume(ItemStack stack){
        return stack.getItemCount() * ItemMain.getItemConfig(stack.getItemType()).itemVolume;
    }

    private Directions getOppositeDirection(Directions dir) {
        return switch (dir) {
            case Top -> Directions.Bottom;
            case Bottom -> Directions.Top;
            case Left -> Directions.Right;
            case Right -> Directions.Left;
        };
    }

    private boolean isInBounds(Vec2i pos) {
        if (pos.x < 0 || pos.y < 0) {
            return false;
        }
        return pos.x <= Main.getClient().windowWidth() / 10 && pos.y <= Main.getClient().windowWidth() / 10;
    }

    private Vec2i directionToRelativeCoordinate(Directions dir) {
        return switch (dir) {
            case Top -> new Vec2i(0, -1);
            case Bottom -> new Vec2i(0, 1);
            case Left -> new Vec2i(-1, 0);
            case Right -> new Vec2i(1, 0);
        };
    }

    public void updateWorkAll() {
        for(int x : buildings.keySet()){
            for(Map.Entry<Integer, Building> entry : buildings.get(x).entrySet()){
                if(bConfig.get(entry.getValue().getType()).getItemControllerType() == ItemControllerTypes.ItemSystemWorkControlled){
                    entry.getValue().work(x, entry.getKey());
                }
            }
        }
    }

    public void updateLeftRightTopBottom(){
        for(int x : buildings.keySet()){
            for(Map.Entry<Integer, Building> entry : buildings.get(x).entrySet()){
                if(bConfig.get(entry.getValue().getType()).getUpdateDirection() == UpdateDirections.LeftRightTopBottom){
                    if(bConfig.get(entry.getValue().getType()).getItemControllerType() == ItemControllerTypes.ItemSystemNormal){
                        handleImportsFor(new Vec2i(x, entry.getKey()), entry.getValue());
                    }else if(bConfig.get(entry.getValue().getType()).getItemControllerType() == ItemControllerTypes.ItemSystemMoveDirectToOutput){
                        handleDirectImportsToOutputFor(new Vec2i(x, entry.getKey()), entry.getValue());
                    }
                }
            }
        }
    }

    public void updateRightLeftBottomTop(){
        for(int x = Main.getClient().getGridWidth(); x >= 0; x--){
            if(!buildings.containsKey(x)){
                continue;
            }
            for(int y = Main.getClient().getGridHeight(); y >= 0; y--){
                if(!buildings.get(x).containsKey(y)){
                    continue;
                }
                Building b = buildings.get(x).get(y);
                if(bConfig.get(b.getType()).getUpdateDirection() == UpdateDirections.RightLeftBottomTop){
                    if(bConfig.get(b.getType()).getItemControllerType() == ItemControllerTypes.ItemSystemNormal){
                        handleImportsFor(new Vec2i(x, y), b);
                    }else if(bConfig.get(b.getType()).getItemControllerType() == ItemControllerTypes.ItemSystemMoveDirectToOutput){
                        handleDirectImportsToOutputFor(new Vec2i(x, y), b);
                    }
                }
            }
        }
    }

    public void registerBuildingConfig(BuildingConfig config) {
        bConfig.put(config.getBuildingId(), config);
    }

    public void registerBuildingClass(String buildingId, Class<? extends Building> buildingClass) {
        bClass.put(buildingId, buildingClass);
    }

    public boolean isBuildingRegistered(String buildingId){
        return bClass.containsKey(buildingId);
    }

    public Building getBuildingInstance(String buildingId, int x, int y){
        try {
            /*for (Constructor<?> c : bClass.get(buildingId).getDeclaredConstructors()) {
                System.out.println(String.format("%s", c.toString()));
                for (Class<?> par : c.getParameterTypes()) {
                    System.out.println(par.getName());
                    System.out.println(par == int.class);
                }

            }

            System.out.println(bClass.get(buildingId).getDeclaredConstructor(int.class, int.class));
            */
            Constructor<?> c = bClass.get(buildingId).getDeclaredConstructor(int.class, int.class);
            c.setAccessible(true);
            return (Building) c.newInstance(x, y);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            Main.getClient().setErrorGameStateException(new IllegalStateException(String.format("While creating a new building instance for building id: %s a error cured! (x: %d;y: %d)", buildingId, x, y), e));
        }
        return null;
    }

    public Building getBuildingInstance(String buildingId, Vec2i vec) {
        return getBuildingInstance(buildingId, vec.x, vec.y);
    }
}
