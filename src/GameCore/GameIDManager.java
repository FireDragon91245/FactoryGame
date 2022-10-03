package GameCore;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class GameIDManager {

    private final HashMap<Integer, String> buildingIds = new HashMap<>();
    private final HashMap<Integer, String> oreIds = new HashMap<>();
    private final HashMap<Integer, String> guiIds = new HashMap<>();
    private final HashMap<Integer, String> itemIds = new HashMap<>();

    private int currentBuildingId = 0;
    private int currentOreId = 0;
    private int currentGuiId = 0;
    private int currentItemId = 0;

    public void registerBuilding(String name){
        buildingIds.put(currentBuildingId, name);
        currentBuildingId++;
    }

    public void registerOre(String name){
        oreIds.put(currentOreId, name);
        currentOreId++;
    }

    public void registerGui(String name){
        guiIds.put(currentGuiId, name);
        currentGuiId++;
    }

    public void registerItem(String name){
        itemIds.put(currentItemId, name);
        currentItemId++;
    }

    public int resolveBuildingName(String name) throws NoSuchElementException {
        return buildingIds.entrySet().stream().
                filter(entry -> entry.getValue().equals(name)).
                findFirst().
                map(Map.Entry::getKey).
                orElseThrow();
    }
}
