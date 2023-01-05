package GameBuildings;

import GameItems.Inventory;

import java.util.ArrayList;

public interface Building {

    int getX();

    int getY();

    void work(int x, int y);

    String getType();

    Inventory getInventory();

    boolean hasInventoryTarget();

    int getWorkingProgress();

    void neighborAdded(String type);

    void overwriteInventory(Inventory value);

    void updateSelfInventoryTargetAfterPlace(ArrayList<String> neighborTypes);
}
