package GameContent;

import GameBuildings.Buildings;
import GameCore.GameCore;
import GameCore.GuiBuilder;
import GameCore.GuiTypes;
import GameCore.Main;
import GameItems.ItemMain;
import GameItems.Items;
import GameUtils.GameUtils;
import GameCore.Gui;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GameContentLoader {

    public void loadBuildingTextures(GameCore loadTo){
        for(Buildings b : Buildings.values()){
            BufferedImage image;
            try {
                File fi;
                if(!loadTo.buildingCore().containsBuildingConfig(b)){
                    loadTo.setErrorGameStateException(new NullPointerException(String.format("Tried to get the config of a non loaded building ID: %s", b.toString())));
                    return;
                }
                fi = new File(getValidPath(loadTo.buildingCore().getBuildingConfig(b).texturePath, loadTo));
                image = ImageIO.read(fi);
            }catch (IOException e){
                loadTo.setErrorGameStateException(e);
                return;
            }
            loadTo.clientGraphics().RegisterBuildingImage(b, image);
        }
    }

    public void loadBuildingGuiTextures(GameCore loadTo){
        for(Buildings b : Buildings.values()){
            BufferedImage image;
            try {
                File fi;
                if(!loadTo.buildingCore().containsBuildingConfig(b)){
                    loadTo.setErrorGameStateException(new NullPointerException(String.format("Tried to get the config of a non loaded building ID: %s", b.toString())));
                    return;
                }
                fi = new File(getValidPath(loadTo.buildingCore().getBuildingConfig(b).guiTexturePath, loadTo));
                image = ImageIO.read(fi);
            }catch (IOException e){
                loadTo.setErrorGameStateException(e);
                return;
            }
            loadTo.clientGraphics().RegisterBuildingGuiImage(b, image);
        }
    }

    public void loadItemTextures(GameCore loadTo){
        for(Items item : Items.values()){
            if(item == Items.None || item == Items.Blocked){
                continue;
            }
            BufferedImage image;
            try {
                File fi;
                if(!ItemMain.containsItemConfig(item)){
                    loadTo.setErrorGameStateException(new NullPointerException(String.format("Tried to get the config of a non loaded item ID: %s", item.toString())));
                    return;
                }
                fi = new File(getValidPath(ItemMain.getItemConfig(item).itemTexture, loadTo));
                image = ImageIO.read(fi);
            }catch (IOException e){
                loadTo.setErrorGameStateException(e);
                return;
            }
            loadTo.clientGraphics().gui().addItemTexture(item, image);
        }
    }

    public void loadAndMapGuiAnimationFrames(GameCore loadTo){
        for(Buildings b : Buildings.values()){
            if(!loadTo.buildingCore().containsBuildingConfig(b)){
                loadTo.setErrorGameStateException(new NullPointerException(String.format("Tried to get the config of a non loaded building ID: %s", b.toString())));
                return;
            }
            if(loadTo.buildingCore().getBuildingConfig(b).workConfig == null){
                continue;
            }
            if(!loadTo.buildingCore().getBuildingConfig(b).overwriteGuiImage){
                continue;
            }
            BufferedImage[] images = new BufferedImage[loadTo.buildingCore().getBuildingConfig(b).workConfig.guiImageAnimationFrames.length];
            for(int i = 0; i < images.length; i++) {
                try {
                    File fi = new File(getValidPath(loadTo.buildingCore().getBuildingConfig(b).workConfig.guiImageAnimationFrames[i], loadTo));
                    images[i] = ImageIO.read(fi);
                } catch (IOException e) {
                    loadTo.setErrorGameStateException(e);
                    return;
                }
            }
            loadTo.clientGraphics().gui().registerGuiAnimationForBuilding(MapAnimationFrames(images, loadTo.buildingCore().getBuildingConfig(b).workConfig.workInterval, loadTo), b);
        }
    }

    private HashMap<Integer, BufferedImage> MapAnimationFrames(BufferedImage[] images, int workInterval, GameCore loadTo) {
        HashMap<Integer, BufferedImage> mappedImages = new HashMap<>(workInterval);
        if(workInterval % images.length != 0){
            loadTo.setErrorGameStateException(new IllegalArgumentException(String.format("Not able to map images because no even distribution is possible (%s / %s %% 1 != 0)", workInterval, images.length)));
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

    public void BuildGuis(GameCore loadTo){
        for(Map.Entry<GuiTypes, GuiBuilder> entry : loadTo.clientGraphics().gui().getGuiBuilders().entrySet()){
            Gui build = entry.getValue().build();
            if(build == null){
                continue;
            }
            loadTo.clientGraphics().gui().addGui(entry.getKey(), build);
        }
    }

    private String getValidPath(String tilePath, GameCore loadTo){
        for(String p : Main.getRootFolders()){
            if(new File(p + tilePath).exists()){
                return p + tilePath;
            }
        }
        loadTo.setErrorGameStateException(new FileNotFoundException(String.format("The tile path \"%s\"was not found in any root folder, root folders: %s", tilePath, GameUtils.listToString(Main.getRootFolders()))));
        return tilePath;
    }
}
