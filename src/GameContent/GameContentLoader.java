package GameContent;

import GameBuildings.BuildingCore;
import GameBuildings.Buildings;
import GameCore.GameCore;
import GameCore.GameGraphics;
import GameCore.GameGui;
import GameItems.ItemMain;
import GameItems.Items;
import GameCore.GuiBuilder;
import GameCore.GuiTypes;
import GameUtils.GameUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GameContentLoader {

    public static void loadBuildingTextures(){
        for(Buildings b : Buildings.values()){
            BufferedImage image;
            try {
                File fi;
                if(!BuildingCore.containsBuildingConfig(b)){
                    GameCore.setErrorGameStateException(new NullPointerException(String.format("Tried to get the config of a non loaded building ID: %s", b.toString())));
                    return;
                }
                fi = new File(getValidPath(BuildingCore.getBuildingConfig(b).texturePath));
                image = ImageIO.read(fi);
            }catch (IOException e){
                GameCore.setErrorGameStateException(e);
                return;
            }
            GameGraphics.RegisterBuildingImage(b, image);
        }
    }

    public static void loadBuildingGuiTextures(){
        for(Buildings b : Buildings.values()){
            BufferedImage image;
            try {
                File fi;
                if(!BuildingCore.containsBuildingConfig(b)){
                    GameCore.setErrorGameStateException(new NullPointerException(String.format("Tried to get the config of a non loaded building ID: %s", b.toString())));
                    return;
                }
                fi = new File(getValidPath(BuildingCore.getBuildingConfig(b).guiTexturePath));
                image = ImageIO.read(fi);
            }catch (IOException e){
                GameCore.setErrorGameStateException(e);
                return;
            }
            GameGraphics.RegisterBuildingGuiImage(b, image);
        }
    }

    public static void loadItemTextures(){
        for(Items item : Items.values()){
            if(item == Items.None || item == Items.Blocked){
                continue;
            }
            BufferedImage image;
            try {
                File fi;
                if(!ItemMain.containsItemConfig(item)){
                    GameCore.setErrorGameStateException(new NullPointerException(String.format("Tried to get the config of a non loaded item ID: %s", item.toString())));
                    return;
                }
                fi = new File(getValidPath(ItemMain.getItemConfig(item).itemTexture));
                image = ImageIO.read(fi);
            }catch (IOException e){
                GameCore.setErrorGameStateException(e);
                return;
            }
            GameGui.addItemTexture(item, image);
        }
    }

    public static void loadAndMapGuiAnimationFrames(){
        for(Buildings b : Buildings.values()){
            if(!BuildingCore.containsBuildingConfig(b)){
                GameCore.setErrorGameStateException(new NullPointerException(String.format("Tried to get the config of a non loaded building ID: %s", b.toString())));
                return;
            }
            if(BuildingCore.getBuildingConfig(b).workConfig == null){
                continue;
            }
            if(!BuildingCore.getBuildingConfig(b).overwriteGuiImage){
                continue;
            }
            BufferedImage[] images = new BufferedImage[BuildingCore.getBuildingConfig(b).workConfig.guiImageAnimationFrames.length];
            for(int i = 0; i < images.length; i++) {
                try {
                    File fi = new File(getValidPath(BuildingCore.getBuildingConfig(b).workConfig.guiImageAnimationFrames[i]));
                    images[i] = ImageIO.read(fi);
                } catch (IOException e) {
                    GameCore.setErrorGameStateException(e);
                    return;
                }
            }
            GameGui.registerGuiAnimationForBuilding(MapAnimationFrames(images, BuildingCore.getBuildingConfig(b).workConfig.workInterval), b);
        }
    }

    private static HashMap<Integer, BufferedImage> MapAnimationFrames(BufferedImage[] images, int workInterval) {
        HashMap<Integer, BufferedImage> mappedImages = new HashMap<>(workInterval);
        if(workInterval % images.length != 0){
            GameCore.setErrorGameStateException(new IllegalArgumentException(String.format("Not able to map images because no even distribution is possible (%s / %s %% 1 != 0)", workInterval, images.length)));
            return mappedImages;
        }
        int framesPerImage = workInterval / images.length;
        for(int x = 0; x < images.length; x++){
            for(int y = 0; y < framesPerImage; y++){
                mappedImages.put(x * framesPerImage + y, images[x]);
            }
        }
        return mappedImages;
    }

    public static void BuildGuis(){
        for(Map.Entry<GuiTypes, GuiBuilder> entry : GameGui.getGuiBuilders().entrySet()){
            GameGui.addGui(entry.getKey(), entry.getValue().build());
        }
    }

    private static String getValidPath(String tilePath){
        for(String p : GameCore.getRootFolders()){
            if(new File(p + tilePath).exists()){
                return p + tilePath;
            }
        }
        GameCore.setErrorGameStateException(new FileNotFoundException(String.format("The tile path \"%s\"was not found in any root folder, root folders: %s", tilePath, GameUtils.listToString(GameCore.getRootFolders()))));
        return tilePath;
    }
}
