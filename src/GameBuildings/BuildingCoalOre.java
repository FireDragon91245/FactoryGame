package GameBuildings;

import GameCore.Main;
import GameItems.Inventory;

import java.util.ArrayList;

public class BuildingCoalOre implements Building{


    private final int x;
    private final int y;

    private Inventory inv = new Inventory(getType());
    private boolean inventoryTarget = false;

    public BuildingCoalOre(int x, int y) {
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
        return Buildings.CoalOre;
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
        Main.getClient().setErrorGameStateException(new IllegalStateException(String.format("Called getWorkingProgress() on a building (%s) that isn't work Controlled!", getType())));
        return -1;
    }

    @Override
    public void neighborAdded(Buildings type) {
        inventoryTarget = false;
    }

    @Override
    public void overwriteInventory(Inventory value) {
        inv = value;
    }

    @Override
    public void updateSelfInventoryTargetAfterPlace(ArrayList<Buildings> neighborTypes) {
        inventoryTarget = false;
    }
}
