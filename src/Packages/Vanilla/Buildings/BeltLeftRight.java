package Packages.Vanilla.Buildings;

import GameBuildings.Building;
import GameCore.Main;
import GameItems.Inventory;

import java.util.ArrayList;
import java.util.Arrays;

@SuppressWarnings("unused")
public class BeltLeftRight implements Building{

    private final int x;
    private final int y;

    private Inventory inv = new Inventory(getType());
    private boolean hasInventoryTarget;

    public BeltLeftRight(int x, int y) {
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
    public String getType() {
        return "BeltLeftRight";
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
        Main.getClient().setErrorGameStateException(new IllegalStateException(String.format("Called getWorkingProgress() on a building (%s) that isn't work Controlled!", getType())));
        return -1;
    }

    public void neighborAdded(String type) {
        if(Arrays.asList(Main.getClient().buildingCore().getBuildingConfig(getType()).getInventoryConfig().thisImportsFrom).contains(type)){
            hasInventoryTarget = true;
        }
        if(Arrays.asList(Main.getClient().buildingCore().getBuildingConfig(getType()).getInventoryConfig().thisExportsTo).contains(type)){
            hasInventoryTarget = true;
        }
    }

    @Override
    public void overwriteInventory(Inventory value) {
        inv = value;
    }

    @Override
    public void updateSelfInventoryTargetAfterPlace(ArrayList<String> neighborTypes) {
        if(Arrays.stream(Main.getClient().buildingCore().getBuildingConfig(getType()).getInventoryConfig().thisImportsFrom).anyMatch(neighborTypes::contains)){
            hasInventoryTarget = true;
        }
        if(Arrays.stream(Main.getClient().buildingCore().getBuildingConfig(getType()).getInventoryConfig().thisExportsTo).anyMatch(neighborTypes::contains)){
            hasInventoryTarget = true;
        }
    }
}
