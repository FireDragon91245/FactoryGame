package GameBuildings;

import GameItems.Inventory;

public abstract class BuildingTemplate implements Building {

    protected final int x;
    protected final int y;
    protected final String bId;
    protected Inventory inv;

    public BuildingTemplate(int x, int y, String bId){
        this.inv = new Inventory(bId);
        this.x = x;
        this.y = y;
        this.bId = bId;
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
    public Inventory getInventory() {
        return inv;
    }

    @Override
    public void overwriteInventory(Inventory value) {
        inv = value;
    }

    @Override
    public String getType() {
        return bId;
    }

}
