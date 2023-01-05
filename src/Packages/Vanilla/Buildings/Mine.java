package Packages.Vanilla.Buildings;

import GameBuildings.Building;
import GameCore.Main;
import GameItems.Inventory;
import GameUtils.Vec2i;

import java.util.ArrayList;
import java.util.Arrays;

public class Mine implements Building {

    private final int x;
    private final int y;
    private Inventory inv = new Inventory(getType());

    private int currentWorkingProgress = 0;
    private boolean inventoryTarget = false;

    public Mine(int x, int y) {
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
        if(currentWorkingProgress >= Main.getClient().buildingCore().getBuildingConfig(getType()).getWorkConfig().workInterval){
            Building b = Main.getClient().buildingCore().getBuilding(x, y);
            if(b != null && inventoryTarget){
                Main.getClient().buildingCore().handleDirectImportsToOutputForWorkControlled(new Vec2i(x, y), b);
            }
            currentWorkingProgress = 0;
        }
    }

    @Override
    public String getType() {
        return "Vanilla:Mine";
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
    public void neighborAdded(String type) {
        if(inventoryTarget){
            return;
        }
        if(Arrays.asList(Main.getClient().buildingCore().getBuildingConfig(getType()).getInventoryConfig().thisImportsFrom).contains(type)){
            inventoryTarget = true;
        }
    }

    @Override
    public void overwriteInventory(Inventory value) {
        inv = value;
    }

    @Override
    public void updateSelfInventoryTargetAfterPlace(ArrayList<String> neighborTypes) {
        for(String building : neighborTypes){
            if(Arrays.asList(Main.getClient().buildingCore().getBuildingConfig(getType()).getInventoryConfig().thisImportsFrom).contains(building)){
                inventoryTarget = true;
            }
        }
    }
}
