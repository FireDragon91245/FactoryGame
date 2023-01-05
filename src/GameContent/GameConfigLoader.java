package GameContent;

import GameBuildings.BuildingConfig;
import GameBuildings.OreConfig;
import GameBuildings.Ores;
import GameConverter.GuiBuilderDeserializer;
import GameCore.GuiBuilder;
import GameCore.Main;
import GameItems.ItemConfig;
import GameItems.ItemMain;
import GameItems.Items;
import GameUtils.GameUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Map;

public class GameConfigLoader {


    public ArrayList<BuildingConfig> loadBuildingConfig(GamePackage packageConfig) {
        File configFile = new File(Main.getGamePath() + "\\" + packageConfig.buildingCfg);
        if(!configFile.exists()){
            Main.getClient().setErrorGameStateException(new FileNotFoundException(String.format("Did not find the Building config for package %s (%s) at the expected location: %s", packageConfig.packageDisplayName, packageConfig.getPackageId(), configFile.getPath())));
            return null;
        }

        String jsonString = null;
        try {
            jsonString = GameUtils.readFile(configFile.getAbsolutePath());
        } catch (IOException ignored) {}

        if (jsonString == null) {
            Main.getClient().setErrorGameStateException(new NullPointerException(String.format("Result of reading building config for package %s (%s) file %s returned null", packageConfig.packageDisplayName, packageConfig.getPackageId(), configFile.getAbsolutePath())));
            return null;
        }

        JsonObject jsonObject = new Gson().fromJson(jsonString, JsonObject.class);
        ArrayList<BuildingConfig> ret = new ArrayList<>();
        for (Map.Entry<String, JsonElement> jsonEntry : jsonObject.entrySet()) {
            BuildingConfig config = Main.getGsonMaster().fromJson(jsonEntry.getValue(), BuildingConfig.class);
            if (config == null) {
                Main.getClient().setErrorGameStateException(new NullPointerException(String.format("The building Config loading result of the building: %s is null! Package %s (%s)", jsonEntry.getKey(), packageConfig.packageDisplayName, packageConfig.getPackageId())));
                return null;
            }

            config.setBuildingId(jsonEntry.getKey());
            ret.add(config);
        }
        return ret;
    }

    public void loadOreConfig(String path) {
        String jsonString = null;
        try {
            jsonString = GameUtils.readFile(path);
        } catch (IOException ignored) {}

        if (jsonString == null) {
            Main.getClient().setErrorGameStateException(new NullPointerException(String.format("Result of reading file %s returned null", path)));
            return;
        }

        JsonObject jsonObject = new Gson().fromJson(jsonString, JsonObject.class);
        for (Ores oreType : Ores.values()) {
            JsonObject childObject = jsonObject.getAsJsonObject(oreType.toString());
            Gson gson = new Gson();
            OreConfig config = gson.fromJson(childObject, OreConfig.class);
            if (config == null) {
                Main.getClient().setErrorGameStateException(new NullPointerException(String.format("The ore Config loading result of the ore: %s is null!", oreType)));
                return;
            }

            Main.getClient().buildingCore().setOreConfig(oreType, config);
        }
    }

    public void loadItemConfig(String path) {
        String jsonString = null;
        try {
            jsonString = GameUtils.readFile(path);
        } catch (IOException ignored) {}

        if (jsonString == null) {
            Main.getClient().setErrorGameStateException(new NullPointerException(String.format("Result of reading file %s returned null", path)));
            return;
        }

        JsonObject jsonObject = new Gson().fromJson(jsonString, JsonObject.class);
        for (Items item : Items.values()) {
            if (item == Items.None || item == Items.Blocked) {
                continue;
            }
            JsonObject childObject = jsonObject.getAsJsonObject(item.toString());
            ItemConfig config = Main.getGsonMaster().fromJson(childObject, ItemConfig.class);
            if (config == null) {
                Main.getClient().setErrorGameStateException(new NullPointerException(String.format("The ore Config loading result of the item: %s is null!", item)));
                return;
            }

            ItemMain.addItemConfig(item, config);
        }
    }

    public ArrayList<GuiBuilder> loadGuiConfig(GamePackage packageConfig) {
        System.out.println(packageConfig.guiCfg);
        File guiConfig = new File(Main.getGamePath() + "\\" + packageConfig.guiCfg);
        if(!guiConfig.exists()){
            Main.getClient().setErrorGameStateException(new FileNotFoundException(String.format("The gui config for the package %s (%s) was not found at the location %s!", packageConfig.packageDisplayName, packageConfig.getPackageId(), guiConfig.getPath())));
            return null;
        }

        String jsonString = null;
        try {
            jsonString = GameUtils.readFile(guiConfig.getAbsolutePath());
        } catch (IOException e) {
            Main.getClient().setErrorGameStateException(e);
        }
        if (jsonString == null) {
            Main.getClient().setErrorGameStateException(new NullPointerException(String.format("Result of reading file %s returned null", guiConfig.getAbsolutePath())));
            return null;
        }

        Gson gson = new GsonBuilder().registerTypeAdapter(GuiBuilder.class, new GuiBuilderDeserializer()).create();
        JsonObject jsonObject = Main.getGsonMaster().fromJson(jsonString, JsonObject.class);
        ArrayList<GuiBuilder> ret = new ArrayList<>();
        for (Map.Entry<String, JsonElement> jsonEntry : jsonObject.entrySet()) {
            GuiBuilder builder = gson.fromJson(jsonEntry.getValue(), GuiBuilder.class);
            if (builder == null) {
                Main.getClient().setErrorGameStateException(new NullPointerException(String.format("The result of trying to load gui %s resulted in a null value", jsonEntry.getKey())));
                return null;
            }

            builder.setGuiId(jsonEntry.getKey());
            ret.add(builder);
        }
        return ret;
    }

    public GamePackage loadPackageConfig(File currentPackageFolder) {
        File packageConfig = new File(currentPackageFolder.getAbsolutePath() + "\\" + currentPackageFolder.getName() + ".json");

        if(!packageConfig.exists()){
            Main.getClient().setErrorGameStateException(new FileNotFoundException(String.format("The package config file was not found for package %s, the file was expected at %s", currentPackageFolder.getName(), packageConfig.getAbsolutePath())));
        }

        GamePackage pack = null;
        try {
            pack = Main.getGsonMaster().fromJson(GameUtils.readFile(packageConfig.getAbsolutePath()), GamePackage.class);
            System.out.println(GameUtils.readFile(packageConfig.getAbsolutePath()));
            pack.setPackageId(packageConfig.getName().replaceFirst("[.][^.]+$", ""));
        } catch (IOException e) {
            Main.getClient().setErrorGameStateException(new NullPointerException(String.format("While loading the package config from %s a error occurred", currentPackageFolder.getAbsolutePath())));
        }
        return pack;
    }
}
