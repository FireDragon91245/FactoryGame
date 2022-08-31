package GameBuildings;

import GameItems.Inventory;

import java.util.ArrayList;

public interface Building {

    int getX();

    int getY();

    void work(int x, int y);

    Buildings getType();

    Inventory getInventory();

    boolean hasInventoryTarget();

    int getWorkingProgress();

    void neighborAdded(Buildings type);

    void overwriteInventory(Inventory value);

    void updateSelfInventoryTargetAfterPlace(ArrayList<Buildings> neighborTypes);
}
