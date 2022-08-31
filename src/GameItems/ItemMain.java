package GameItems;

import java.util.HashMap;

public class ItemMain {

    private static final HashMap<Items, ItemConfig> iConfig = new HashMap<>();

    public static void addItemConfig(Items items, ItemConfig config) {
        iConfig.put(items, config);
    }

    public static ItemConfig getItemConfig(Items items){
        return iConfig.get(items);
    }

    public static boolean containsItemConfig(Items item) {
        return iConfig.containsKey(item);
    }
}
