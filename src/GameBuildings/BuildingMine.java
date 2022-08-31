package GameBuildings;

import GameItems.Inventory;
import GameUtils.Vec2i;

import java.util.ArrayList;
import java.util.Arrays;

public class BuildingMine implements Building {

    private final int x;
    private final int y;

    private int currentWorkingProgress = 0;
    private Inventory inv = new Inventory(getType());
    private boolean inventoryTarget = false;

    public BuildingMine(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public void work(int x, int y) {
        currentWorkingProgress++;
        if(currentWorkingProgress >= BuildingCore.getBuildingConfig(getType()).workConfig.workInterval){
            Building b = BuildingCore.getBuilding(x, y);
            if(b != null && inventoryTarget){
                BuildingCore.handleDirectImportsToOutputForWorkControlled(new Vec2i(x, y), b);
            }
            currentWorkingProgress = 0;
        }
    }


    @Override
    public Buildings getType() {
        return Buildings.Mine;
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }

    @Override
    public boolean hasInventoryTarget() {
        return inventoryTarget;
    }

    @Override
    public int getWorkingProgress() {
        return currentWorkingProgress;
    }

    @Override
    public void neighborAdded(Buildings type) {
        if(inventoryTarget){
            return;
        }
        if(Arrays.stream(BuildingCore.getBuildingConfig(getType()).inventoryConfig.thisImportsFrom).anyMatch(x -> x == type)){
            inventoryTarget = true;
        }
    }

    @Override
    public void overwriteInventory(Inventory value) {
        inv = value;
    }

    @Override
    public void updateSelfInventoryTargetAfterPlace(ArrayList<Buildings> neighborTypes) {
        for(Buildings building : neighborTypes){
            if(Arrays.stream(BuildingCore.getBuildingConfig(getType()).inventoryConfig.thisImportsFrom).anyMatch(x -> x == building)){
                inventoryTarget = true;
            }
        }
    }
}
