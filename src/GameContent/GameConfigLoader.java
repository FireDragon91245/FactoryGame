package GameContent;

import GameBuildings.*;
import GameConverter.GuiBuilderDeserializer;
import GameCore.*;
import GameItems.ItemConfig;
import GameItems.ItemMain;
import GameItems.Items;
import GameUtils.GameUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.*;

public class GameConfigLoader {


    public static void loadBuildingConfig(String path) {
        String jsonString = readFile(path);
        if (jsonString == null) {
            GameCore.setErrorGameStateException(new NullPointerException(String.format("Result of reading file %s returned null", path)));
            return;
        }

        JsonObject jsonObject = new Gson().fromJson(jsonString, JsonObject.class);
        for (Buildings bId : Buildings.values()) {
            JsonObject childObject = jsonObject.getAsJsonObject(bId.toString());
            Gson gson = new Gson();
            BuildingConfig config = gson.fromJson(childObject, BuildingConfig.class);
            if (config == null) {
                GameCore.setErrorGameStateException(new NullPointerException(String.format("The building Config loading result of the building: %s is null!", bId)));
                return;
            }

            BuildingCore.setBuildingConfig(bId, config);
        }
    }

    public static String readFile(String path) {
        String fullFilePath = path;
        for (String p : GameCore.getRootFolders()) {
            if (new File(p + path).exists()) {
                fullFilePath = p + path;
                break;
            }
        }
        if (fullFilePath.equalsIgnoreCase(path)) {
            GameCore.setErrorGameStateException(new FileNotFoundException(String.format("Config file was not found on any root path, root paths: %s", GameUtils.listToString(GameCore.getRootFolders()))));
        }
        String jsonString = null;
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(fullFilePath));
        } catch (FileNotFoundException e) {
            GameCore.setErrorGameStateException(e);
            return null;
        }

        try {
            StringBuilder builder = new StringBuilder();
            String line = reader.readLine();

            while (line != null) {
                builder.append(line);
                builder.append(System.lineSeparator());
                line = reader.readLine();
            }

            jsonString = builder.toString();
        } catch (IOException e) {
            GameCore.setErrorGameStateException(e);
        }
        return jsonString;
    }

    public static void loadOreConfig(String path) {
        String jsonString = readFile(path);
        if (jsonString == null) {
            GameCore.setErrorGameStateException(new NullPointerException(String.format("Result of reading file %s returned null", path)));
            return;
        }

        JsonObject jsonObject = new Gson().fromJson(jsonString, JsonObject.class);
        for (Ores oreType : Ores.values()) {
            JsonObject childObject = jsonObject.getAsJsonObject(oreType.toString());
            Gson gson = new Gson();
            OreConfig config = gson.fromJson(childObject, OreConfig.class);
            if (config == null) {
                GameCore.setErrorGameStateException(new NullPointerException(String.format("The ore Config loading result of the ore: %s is null!", oreType)));
                return;
            }

            BuildingCore.setOreConfig(oreType, config);
        }
    }

    public static void loadItemConfig(String path) {
        String jsonString = readFile(path);
        if (jsonString == null) {
            GameCore.setErrorGameStateException(new NullPointerException(String.format("Result of reading file %s returned null", path)));
            return;
        }

        JsonObject jsonObject = new Gson().fromJson(jsonString, JsonObject.class);
        for (Items item : Items.values()) {
            if (item == Items.None || item == Items.Blocked) {
                continue;
            }
            JsonObject childObject = jsonObject.getAsJsonObject(item.toString());
            ItemConfig config = Main.gson.fromJson(childObject, ItemConfig.class);
            if (config == null) {
                GameCore.setErrorGameStateException(new NullPointerException(String.format("The ore Config loading result of the item: %s is null!", item)));
                return;
            }

            ItemMain.addItemConfig(item, config);
        }
    }

    public static void loadGuiConfig(String path) {
        String jsonString = readFile(path);
        if (jsonString == null) {
            GameCore.setErrorGameStateException(new NullPointerException(String.format("Result of reading file %s returned null", path)));
            return;
        }

        Gson gson = new GsonBuilder().registerTypeAdapter(GuiBuilder.class, new GuiBuilderDeserializer()).create();
        JsonObject jsonObject = Main.gson.fromJson(jsonString, JsonObject.class);
        for (GuiTypes type : GuiTypes.values()) {
            JsonObject childObject = jsonObject.getAsJsonObject(type.toString());
            GuiBuilder builder = gson.fromJson(childObject, GuiBuilder.class);
            if (builder == null) {
                GameCore.setErrorGameStateException(new NullPointerException(String.format("The result of trying to load %s resulted in a null value", type)));
                return;
            }

            GameGui.addGuiBuilder(type, builder);
        }
    }
}
