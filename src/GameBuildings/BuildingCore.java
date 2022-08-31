package GameBuildings;

import GameCore.GameCore;
import GameCore.UpdateDirections;
import GameItems.*;
import GameUtils.Vec2i;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class BuildingCore {

    private static final HashMap<Integer, HashMap<Integer, Building>> buildings = new HashMap<>();
    private static final HashMap<Buildings, BuildingConfig> bConfig = new HashMap<>();
    private static final HashMap<Ores, OreConfig> oConfig = new HashMap<>();

    public static void setBuildingConfig(Buildings type, BuildingConfig config) {
        bConfig.put(type, config);
    }

    public static BuildingConfig getBuildingConfig(Buildings type) {
        return bConfig.getOrDefault(type, null);
    }

    public static Class<? extends Building> GetClassFromBuildingType(Buildings type) {
        return switch (type) {
            case Mine -> BuildingMine.class;
            case IronOre -> BuildingIronOre.class;
            case CoalOre -> BuildingCoalOre.class;
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }

    public static HashMap<Integer, HashMap<Integer, Building>> getAllBuildings() {
        return buildings;
    }

    public static void addBuilding(int x, int y, Building building) {
        if (!buildings.containsKey(x)) {
            buildings.put(x, new HashMap<>());
        }
        building.updateSelfInventoryTargetAfterPlace(getNeighborTypes(x, y));
        buildings.get(x).put(y, building);
        sendNeighborChange(x, y, building.getType());
    }

    private static ArrayList<Buildings> getNeighborTypes(int x, int y) {
        ArrayList<Buildings> types = new ArrayList<>(4);
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

    private static void sendNeighborChange(int x, int y, Buildings type) {
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

    private static boolean isInBounds(int x, int y) {
        if (x < 0 || y < 0) {
            return false;
        }
        return x <= GameCore.windowWidth() / 10 && y <= GameCore.windowWidth() / 10;
    }

    public static Building getBuilding(int x, int y) {
        if (buildings.containsKey(x)) {
            return buildings.get(x).getOrDefault(y, null);
        }
        return null;
    }

    public static boolean isBuildingAtPos(int x, int y) {
        if (buildings.containsKey(x)) {
            return buildings.get(x).containsKey(y);
        }
        return false;
    }

    public static OreConfig getOreConfig(Ores ore) {
        return oConfig.getOrDefault(ore, null);
    }

    public static void setOreConfig(Ores ore, OreConfig oreConfig) {
        oConfig.put(ore, oreConfig);
    }

    public static boolean containsBuildingConfig(Buildings b) {
        return bConfig.containsKey(b);
    }

    public static boolean containsOreConfig(Ores ore) {
        return oConfig.containsKey(ore);
    }

    public static boolean isBuildingAtPos(Vec2i gridPos) {
        if (buildings.containsKey(gridPos.x)) {
            return buildings.get(gridPos.x).containsKey(gridPos.y);
        }
        return false;
    }

    public static Building getBuilding(Vec2i pos) {
        if (buildings.containsKey(pos.x)) {
            return buildings.get(pos.x).getOrDefault(pos.y, null);
        }
        return null;
    }

    public static void handleDirectImportsToOutputFor(Vec2i pos, Building b) {
        for(Directions dir : bConfig.get(b.getType()).inventoryConfig.importDirections){
            Vec2i currPos = pos.add(directionToRelativeCoordinate(dir));
            if(!isInBounds(currPos)){
                continue;
            }
            if(Arrays.stream(bConfig.get(b.getType()).inventoryConfig.blockedInputDirections).anyMatch(x -> x == dir)){
                continue;
            }
            if(!isBuildingAtPos(currPos)){
                continue;
            }
            Building b2 = getBuilding(currPos);
            if(b2 == null){
                continue;
            }
            if(Arrays.stream(bConfig.get(b.getType()).inventoryConfig.thisImportsFrom).noneMatch(x -> x == b2.getType())){
                continue;
            }
            if(Arrays.stream(bConfig.get(b2.getType()).inventoryConfig.blockedOutputDirections).anyMatch(x -> x == getOppositeDirection(dir))){
                continue;
            }
            if(Arrays.stream(bConfig.get(b2.getType()).inventoryConfig.otherBuildingsBlockedExport).anyMatch(x -> x == b.getType())){
                continue;
            }

            Inventory invBuilding1 = b.getInventory();
            Inventory invBuilding2 = b2.getInventory();
            if(invBuilding1 == null || invBuilding2 == null){
                continue;
            }
            for(int b2Slot = 0; b2Slot < bConfig.get(b2.getType()).outputSlots; b2Slot++){
                if(invBuilding2.getOutputSlot(b2Slot).getItemType() == Items.None || invBuilding2.getOutputSlot(b2Slot).getItemType() == Items.Blocked){
                    continue;
                }
                boolean success = false;
                boolean success2 = false;
                for(int bSlot = 0; bSlot < bConfig.get(b.getType()).outputSlots; bSlot++){
                    if(invBuilding1.getOutputSlot(bSlot).getItemType() == Items.Blocked){
                        continue;
                    }
                    if(invBuilding1.getOutputSlot(bSlot).getItemType() == invBuilding2.getOutputSlot(b2Slot).getItemType()){
                        if(BuildingCore.getBuildingConfig(b.getType()).inventoryConfig.importSpeed < invBuilding2.getOutputSlot(b2Slot).getItemCount()) {
                            invBuilding1.getOutputSlot(bSlot).addItemCount(BuildingCore.getBuildingConfig(b.getType()).inventoryConfig.importSpeed);
                            invBuilding2.getOutputSlot(b2Slot).removeItemCount(BuildingCore.getBuildingConfig(b.getType()).inventoryConfig.importSpeed);
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
                    for(int bSlot = 0; bSlot < bConfig.get(b.getType()).outputSlots; bSlot++){
                        if(invBuilding1.getOutputSlot(bSlot).getItemType() == Items.None){
                            if(BuildingCore.getBuildingConfig(b.getType()).inventoryConfig.importSpeed < invBuilding2.getOutputSlot(b2Slot).getItemCount()) {
                                invBuilding1.setOutputSlot(bSlot, new ItemStack(invBuilding2.getOutputSlot(b2Slot).getItemType(), BuildingCore.getBuildingConfig(b.getType()).inventoryConfig.importSpeed));
                                invBuilding2.getOutputSlot(b2Slot).removeItemCount(BuildingCore.getBuildingConfig(b.getType()).inventoryConfig.importSpeed);
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

    public static void handleDirectImportsToOutputForWorkControlled(Vec2i pos, Building b) {
        for(Directions dir : bConfig.get(b.getType()).inventoryConfig.importDirections){
            Vec2i currPos = pos.add(directionToRelativeCoordinate(dir));
            if(!isInBounds(currPos)){
                continue;
            }
            if(Arrays.stream(bConfig.get(b.getType()).inventoryConfig.blockedInputDirections).anyMatch(x -> x == dir)){
                continue;
            }
            if(!isBuildingAtPos(currPos)){
                continue;
            }
            Building b2 = getBuilding(currPos);
            if(b2 == null){
                continue;
            }
            if(Arrays.stream(bConfig.get(b.getType()).inventoryConfig.thisImportsFrom).noneMatch(x -> x == b2.getType())){
                continue;
            }
            if(Arrays.stream(bConfig.get(b2.getType()).inventoryConfig.blockedOutputDirections).anyMatch(x -> x == getOppositeDirection(dir))){
                continue;
            }
            if(Arrays.stream(bConfig.get(b2.getType()).inventoryConfig.otherBuildingsBlockedExport).anyMatch(x -> x == b.getType())){
                continue;
            }

            Inventory invBuilding1;
            invBuilding1 = b.getInventory();
            Inventory invBuilding2;
            invBuilding2 = b2.getInventory();
            if(invBuilding1 == null || invBuilding2 == null){
                continue;
            }
            for(int b2Slot = 0; b2Slot < bConfig.get(b2.getType()).outputSlots; b2Slot++){
                if(invBuilding2.getOutputSlot(b2Slot).getItemType() == Items.None || invBuilding2.getOutputSlot(b2Slot).getItemType() == Items.Blocked){
                    continue;
                }
                boolean success = false;
                boolean success2 = false;
                for(int bSlot = 0; bSlot < bConfig.get(b.getType()).outputSlots; bSlot++){
                    if(invBuilding1.getOutputSlot(bSlot).getItemType() == Items.Blocked){
                        continue;
                    }
                    if(invBuilding1.getOutputSlot(bSlot).getItemType() == invBuilding2.getOutputSlot(b2Slot).getItemType()){
                        if(BuildingCore.getBuildingConfig(b.getType()).inventoryConfig.importSpeed < invBuilding2.getOutputSlot(b2Slot).getItemCount()) {
                            invBuilding1.getOutputSlot(bSlot).addItemCount(BuildingCore.getBuildingConfig(b.getType()).inventoryConfig.importSpeed);
                            invBuilding2.getOutputSlot(b2Slot).removeItemCount(BuildingCore.getBuildingConfig(b.getType()).inventoryConfig.importSpeed);
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
                    for(int bSlot = 0; bSlot < bConfig.get(b.getType()).outputSlots; bSlot++){
                        if(invBuilding1.getOutputSlot(bSlot).getItemType() == Items.None){
                            if(BuildingCore.getBuildingConfig(b.getType()).inventoryConfig.importSpeed < invBuilding2.getOutputSlot(b2Slot).getItemCount()) {
                                invBuilding1.setOutputSlot(bSlot, new ItemStack(invBuilding2.getOutputSlot(b2Slot).getItemType(), BuildingCore.getBuildingConfig(b.getType()).inventoryConfig.importSpeed));
                                invBuilding2.getOutputSlot(b2Slot).removeItemCount(BuildingCore.getBuildingConfig(b.getType()).inventoryConfig.importSpeed);
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

    private static void handleImportsFor(Vec2i pos, Building b) {
        for(Directions dir : bConfig.get(b.getType()).inventoryConfig.importDirections){
            Vec2i currPos = pos.add(directionToRelativeCoordinate(dir));
            if(!isInBounds(currPos)){
                continue;
            }
            if(Arrays.stream(bConfig.get(b.getType()).inventoryConfig.blockedInputDirections).anyMatch(x -> x == dir)){
                continue;
            }
            if(!isBuildingAtPos(currPos)){
                continue;
            }
            Building b2 = getBuilding(currPos);
            if(b2 == null){
                continue;
            }
            if(Arrays.stream(bConfig.get(b.getType()).inventoryConfig.thisImportsFrom).noneMatch(x -> x == b2.getType())){
                continue;
            }
            if(Arrays.stream(bConfig.get(b2.getType()).inventoryConfig.blockedOutputDirections).anyMatch(x -> x == getOppositeDirection(dir))){
                continue;
            }
            if(Arrays.stream(bConfig.get(b2.getType()).inventoryConfig.otherBuildingsBlockedExport).anyMatch(x -> x == b.getType())){
                continue;
            }

            Inventory invBuilding1 = b.getInventory();
            Inventory invBuilding2 = b2.getInventory();
            if(invBuilding1 == null || invBuilding2 == null){
                continue;
            }
            for(int b2Slot = 0; b2Slot < bConfig.get(b2.getType()).outputSlots; b2Slot++){
                if(invBuilding2.getOutputSlot(b2Slot).getItemType() == Items.None || invBuilding2.getOutputSlot(b2Slot).getItemType() == Items.Blocked){
                    continue;
                }
                boolean success = false;
                boolean success2 = false;
                for(int bSlot = 0; bSlot < bConfig.get(b.getType()).inputSlots; bSlot++){
                    if(invBuilding1.getInputSlot(bSlot).getItemType() == Items.Blocked){
                        continue;
                    }
                    if(invBuilding1.getInputSlot(bSlot).getItemType() == invBuilding2.getOutputSlot(b2Slot).getItemType()){
                        if(BuildingCore.getBuildingConfig(b.getType()).inventoryConfig.importSpeed < invBuilding2.getOutputSlot(b2Slot).getItemCount()) {
                            invBuilding1.getInputSlot(bSlot).addItemCount(BuildingCore.getBuildingConfig(b.getType()).inventoryConfig.importSpeed);
                            invBuilding2.getOutputSlot(b2Slot).removeItemCount(BuildingCore.getBuildingConfig(b.getType()).inventoryConfig.importSpeed);
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
                    for(int bSlot = 0; bSlot < bConfig.get(b.getType()).inputSlots; bSlot++){
                        if(invBuilding1.getInputSlot(bSlot).getItemType() == Items.None){
                            if(BuildingCore.getBuildingConfig(b.getType()).inventoryConfig.importSpeed < invBuilding2.getOutputSlot(b2Slot).getItemCount()) {
                                invBuilding1.setInputSlot(bSlot, new ItemStack(invBuilding2.getOutputSlot(b2Slot).getItemType(), BuildingCore.getBuildingConfig(b.getType()).inventoryConfig.importSpeed));
                                invBuilding2.getOutputSlot(b2Slot).removeItemCount(BuildingCore.getBuildingConfig(b.getType()).inventoryConfig.importSpeed);
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

    private static double getItemStackVolume(ItemStack stack){
        return stack.getItemCount() * ItemMain.getItemConfig(stack.getItemType()).itemVolume;
    }

    private static Directions getOppositeDirection(Directions dir) {
        return switch (dir) {
            case Top -> Directions.Bottom;
            case Bottom -> Directions.Top;
            case Left -> Directions.Right;
            case Right -> Directions.Left;
        };
    }

    private static boolean isInBounds(Vec2i pos) {
        if (pos.x < 0 || pos.y < 0) {
            return false;
        }
        return pos.x <= GameCore.windowWidth() / 10 && pos.y <= GameCore.windowWidth() / 10;
    }

    private static Vec2i directionToRelativeCoordinate(Directions dir) {
        return switch (dir) {
            case Top -> new Vec2i(0, -1);
            case Bottom -> new Vec2i(0, 1);
            case Left -> new Vec2i(-1, 0);
            case Right -> new Vec2i(1, 0);
        };
    }

    public static void updateWorkAll() {
        for(int x : buildings.keySet()){
            for(Map.Entry<Integer, Building> entry : buildings.get(x).entrySet()){
                if(bConfig.get(entry.getValue().getType()).itemControllerType == ItemControllerTypes.ItemSystemWorkControlled){
                    entry.getValue().work(x, entry.getKey());
                }
            }
        }
    }

    public static void updateLeftRightTopBottom(){
        for(int x : buildings.keySet()){
            for(Map.Entry<Integer, Building> entry : buildings.get(x).entrySet()){
                if(bConfig.get(entry.getValue().getType()).updateDirection == UpdateDirections.LeftRightTopBottom){
                    if(bConfig.get(entry.getValue().getType()).itemControllerType == ItemControllerTypes.ItemSystemNormal){
                        handleImportsFor(new Vec2i(x, entry.getKey()), entry.getValue());
                    }else if(bConfig.get(entry.getValue().getType()).itemControllerType == ItemControllerTypes.ItemSystemMoveDirectToOutput){
                        handleDirectImportsToOutputFor(new Vec2i(x, entry.getKey()), entry.getValue());
                    }
                }
            }
        }
    }

    public static void updateRightLeftBottomTop(){
        for(int x = GameCore.getGridWidth(); x >= 0; x--){
            if(!buildings.containsKey(x)){
                continue;
            }
            for(int y = GameCore.getGridHeight(); y >= 0; y--){
                if(!buildings.get(x).containsKey(y)){
                    continue;
                }
                Building b = buildings.get(x).get(y);
                if(bConfig.get(b.getType()).updateDirection == UpdateDirections.RightLeftBottomTop){
                    if(bConfig.get(b.getType()).itemControllerType == ItemControllerTypes.ItemSystemNormal){
                        handleImportsFor(new Vec2i(x, y), b);
                    }else if(bConfig.get(b.getType()).itemControllerType == ItemControllerTypes.ItemSystemMoveDirectToOutput){
                        handleDirectImportsToOutputFor(new Vec2i(x, y), b);
                    }
                }
            }
        }
    }
}
