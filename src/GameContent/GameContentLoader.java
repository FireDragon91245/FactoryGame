package GameContent;

import GameBuildings.Buildings;
import GameCore.GuiBuilder;
import GameCore.GuiTypes;
import GameCore.Main;
import GameItems.ItemMain;
import GameItems.Items;
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
                if(!Main.getClient().buildingCore().containsBuildingConfig(b)){
                    Main.getClient().setErrorGameStateException(new NullPointerException(String.format("Tried to get the config of a non loaded building ID: %s", b.toString())));
                    return;
                }
                fi = new File(getValidPath(Main.getClient().buildingCore().getBuildingConfig(b).texturePath));
                image = ImageIO.read(fi);
            }catch (IOException e){
                Main.getClient().setErrorGameStateException(e);
                return;
            }
            Main.getClient().clientGraphics().RegisterBuildingImage(b, image);
        }
    }

    public static void loadBuildingGuiTextures(){
        for(Buildings b : Buildings.values()){
            BufferedImage image;
            try {
                File fi;
                if(!Main.getClient().buildingCore().containsBuildingConfig(b)){
                    Main.getClient().setErrorGameStateException(new NullPointerException(String.format("Tried to get the config of a non loaded building ID: %s", b.toString())));
                    return;
                }
                fi = new File(getValidPath(Main.getClient().buildingCore().getBuildingConfig(b).guiTexturePath));
                image = ImageIO.read(fi);
            }catch (IOException e){
                Main.getClient().setErrorGameStateException(e);
                return;
            }
            Main.getClient().clientGraphics().RegisterBuildingGuiImage(b, image);
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
                    Main.getClient().setErrorGameStateException(new NullPointerException(String.format("Tried to get the config of a non loaded item ID: %s", item.toString())));
                    return;
                }
                fi = new File(getValidPath(ItemMain.getItemConfig(item).itemTexture));
                image = ImageIO.read(fi);
            }catch (IOException e){
                Main.getClient().setErrorGameStateException(e);
                return;
            }
            Main.getClient().clientGraphics().gui().addItemTexture(item, image);
        }
    }

    public static void loadAndMapGuiAnimationFrames(){
        for(Buildings b : Buildings.values()){
            if(!Main.getClient().buildingCore().containsBuildingConfig(b)){
                Main.getClient().setErrorGameStateException(new NullPointerException(String.format("Tried to get the config of a non loaded building ID: %s", b.toString())));
                return;
            }
            if(Main.getClient().buildingCore().getBuildingConfig(b).workConfig == null){
                continue;
            }
            if(!Main.getClient().buildingCore().getBuildingConfig(b).overwriteGuiImage){
                continue;
            }
            BufferedImage[] images = new BufferedImage[Main.getClient().buildingCore().getBuildingConfig(b).workConfig.guiImageAnimationFrames.length];
            for(int i = 0; i < images.length; i++) {
                try {
                    File fi = new File(getValidPath(Main.getClient().buildingCore().getBuildingConfig(b).workConfig.guiImageAnimationFrames[i]));
                    images[i] = ImageIO.read(fi);
                } catch (IOException e) {
                    Main.getClient().setErrorGameStateException(e);
                    return;
                }
            }
            Main.getClient().clientGraphics().gui().registerGuiAnimationForBuilding(MapAnimationFrames(images, Main.getClient().buildingCore().getBuildingConfig(b).workConfig.workInterval), b);
        }
    }

    private static HashMap<Integer, BufferedImage> MapAnimationFrames(BufferedImage[] images, int workInterval) {
        HashMap<Integer, BufferedImage> mappedImages = new HashMap<>(workInterval);
        if(workInterval % images.length != 0){
            Main.getClient().setErrorGameStateException(new IllegalArgumentException(String.format("Not able to map images because no even distribution is possible (%s / %s %% 1 != 0)", workInterval, images.length)));
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
        for(Map.Entry<GuiTypes, GuiBuilder> entry : Main.getClient().clientGraphics().gui().getGuiBuilders().entrySet()){
            Main.getClient().clientGraphics().gui().addGui(entry.getKey(), entry.getValue().build());
        }
    }

    private static String getValidPath(String tilePath){
        for(String p : Main.getClient().getRootFolders()){
            if(new File(p + tilePath).exists()){
                return p + tilePath;
            }
        }
        Main.getClient().setErrorGameStateException(new FileNotFoundException(String.format("The tile path \"%s\"was not found in any root folder, root folders: %s", tilePath, GameUtils.listToString(Main.getClient().getRootFolders()))));
        return tilePath;
    }
}
