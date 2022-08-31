package GameBuildings;

import GameCore.GameCore;
import GameItems.Inventory;

import java.util.ArrayList;
import java.util.Arrays;

public class BuildingBeltLeftRight implements Building{

    private final int x;
    private final int y;

    private Inventory inv = new Inventory(getType());
    private boolean hasInventoryTarget;

    public BuildingBeltLeftRight(int x, int y) {
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
    }

    @Override
    public Buildings getType() {
        return Buildings.BeltLeftRight;
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }

    @Override
    public boolean hasInventoryTarget() {
        return hasInventoryTarget;
    }

    @Override
    public int getWorkingProgress() {
        GameCore.setErrorGameStateException(new IllegalStateException(String.format("Called getWorkingProgress() on a building (%s) that isn't work Controlled!", getType())));
        return -1;
    }

    @Override
    public void neighborAdded(Buildings type) {
        if(Arrays.stream(BuildingCore.getBuildingConfig(getType()).inventoryConfig.thisImportsFrom).anyMatch(x -> x == type)){
            hasInventoryTarget = true;
        }
        if(Arrays.stream(BuildingCore.getBuildingConfig(getType()).inventoryConfig.thisExportsTo).anyMatch(x -> x == type)){
            hasInventoryTarget = true;
        }
    }

    @Override
    public void overwriteInventory(Inventory value) {
        inv = value;
    }

    @Override
    public void updateSelfInventoryTargetAfterPlace(ArrayList<Buildings> neighborTypes) {
        if(Arrays.stream(BuildingCore.getBuildingConfig(getType()).inventoryConfig.thisImportsFrom).anyMatch(neighborTypes::contains)){
            hasInventoryTarget = true;
        }
        if(Arrays.stream(BuildingCore.getBuildingConfig(getType()).inventoryConfig.thisExportsTo).anyMatch(neighborTypes::contains)){
            hasInventoryTarget = true;
        }
    }
}
