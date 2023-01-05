package GameContent;

import GameBuildings.Building;
import GameBuildings.BuildingConfig;
import GameBuildings.Buildings;
import GameCore.GameCore;
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
                fi = new File(getValidPath(loadTo.buildingCore().getBuildingConfig(b).getTexturePath(), loadTo));
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
                fi = new File(getValidPath(loadTo.buildingCore().getBuildingConfig(b).getGuiTexturePath(), loadTo));
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
            if(loadTo.buildingCore().getBuildingConfig(b).getWorkConfig() == null){
                continue;
            }
            if(!loadTo.buildingCore().getBuildingConfig(b).isOverwriteGuiImage()){
                continue;
            }
            BufferedImage[] images = new BufferedImage[loadTo.buildingCore().getBuildingConfig(b).getWorkConfig().guiImageAnimationFrames.length];
            for(int i = 0; i < images.length; i++) {
                try {
                    File fi = new File(getValidPath(loadTo.buildingCore().getBuildingConfig(b).getWorkConfig().guiImageAnimationFrames[i], loadTo));
                    images[i] = ImageIO.read(fi);
                } catch (IOException e) {
                    loadTo.setErrorGameStateException(e);
                    return;
                }
            }
            loadTo.clientGraphics().gui().registerGuiAnimationForBuilding(MapAnimationFrames(images, loadTo.buildingCore().getBuildingConfig(b).getWorkConfig().workInterval, loadTo), b);
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

    private String getValidPath(String tilePath, GameCore loadTo){
        for(String p : Main.getRootFolders()){
            if(new File(p + tilePath).exists()){
                return p + tilePath;
            }
        }
        loadTo.setErrorGameStateException(new FileNotFoundException(String.format("The tile path \"%s\"was not found in any root folder, root folders: %s", tilePath, GameUtils.listToString(Main.getRootFolders()))));
        return tilePath;
    }

    @SuppressWarnings("unchecked")
    public Class<? extends PackageCore> loadPackageCoreClass(File currentPackageFolder) {
        File packageCoreClass = new File(currentPackageFolder.getAbsolutePath() + "\\" + currentPackageFolder.getName() + ".class");
        File packageCoreJava = new File(currentPackageFolder.getAbsolutePath() + "\\" + currentPackageFolder.getName() + ".java");

        Class<?> cls;
        if(packageCoreClass.exists()){
            cls = GameCompiler.loadClass(packageCoreClass, currentPackageFolder.getAbsolutePath(), "Packages." + currentPackageFolder.getName() + "." + currentPackageFolder.getName());
        }else if(packageCoreJava.exists()){
            cls = GameCompiler.compileFile(packageCoreJava, currentPackageFolder.getAbsolutePath(), "Packages." + currentPackageFolder.getName() + "." + currentPackageFolder.getName());
        }else{
            Main.getClient().setErrorGameStateException(new FileNotFoundException(String.format("For the package %s there has not ben found a valid package core class in the format .java or .class", currentPackageFolder.getName())));
            return null;
        }

        if(cls == null){
            Main.getClient().setErrorGameStateException(new NullPointerException(String.format("loading the package core class of package %s result in null!", currentPackageFolder.getName())));
            return null;
        }
        if(!PackageCore.class.isAssignableFrom(cls)){
            Main.getClient().setErrorGameStateException(new ClassCastException(String.format("The source class of the package %s does not implement %s, Source File: %s", currentPackageFolder.getName(), PackageCore.class.getCanonicalName(), currentPackageFolder.getAbsolutePath())));
            return null;
        }

        return (Class<? extends PackageCore>) cls;
    }

    @SuppressWarnings("unchecked")
    public Class<? extends Building> loadBuildingClass(BuildingConfig config, GamePackage packageConfig) {
        if(new File(Main.getGamePath() + "\\" + config.getBuildingClass()).exists()) {
            if (config.getBuildingClass().endsWith(".class")) {
                Class<?> cls = GameCompiler.loadClass(new File(Main.getGamePath() + "\\" + config.getBuildingClass()), config.getJavaClassPath());
                if (InvalidBuildingClass(config, packageConfig, cls)) return null;
                return (Class<? extends Building>) cls;
            }
            if (config.getBuildingClass().endsWith(".java")) {
                Class<?> cls = GameCompiler.compileFile(new File(Main.getGamePath() + "\\" + config.getBuildingClass()), config.getJavaClassPath());
                if (InvalidBuildingClass(config, packageConfig, cls)) return null;
                return (Class<? extends Building>) cls;
            }
        }
        if (new File(Main.getGamePath() + "\\" + config.getBuildingClass() + ".class").exists()) {
            Class<?> cls = GameCompiler.loadClass(new File(Main.getGamePath() + "\\" + config.getBuildingClass() + ".class"), config.getJavaClassPath());
            if (InvalidBuildingClass(config, packageConfig, cls)) return null;
            return (Class<? extends Building>) cls;
        }
        if (new File(Main.getGamePath() + "\\" + config.getBuildingClass() + ".java").exists()) {
            Class<?> cls = GameCompiler.compileFile(new File(Main.getGamePath() + "\\" + config.getBuildingClass() + ".java"), config.getJavaClassPath());
            if (InvalidBuildingClass(config, packageConfig, cls)) return null;
            return (Class<? extends Building>) cls;
        }
        Main.getClient().setErrorGameStateException(new FileNotFoundException(String.format("The building class for building %s in package %s (%s) was not found at %s +- (.java / .class)!", config.getBuildingId(), packageConfig.packageDisplayName, packageConfig.getPackageId(), Main.getGamePath() + "\\" + config.getBuildingClass())));
        return null;
    }

    private boolean InvalidBuildingClass(BuildingConfig config, GamePackage packageConfig, Class<?> cls) {
        if(cls == null){
            Main.getClient().setErrorGameStateException(new NullPointerException(String.format("The loading of building class for building %s in package %s (%s) resulted in null!", config.getBuildingId(), packageConfig.packageDisplayName, packageConfig.getPackageId())));
            return true;
        }
        if(!Building.class.isAssignableFrom(cls)){
            Main.getClient().setErrorGameStateException(new ClassCastException(String.format("The building class for building %s in package %s (%s) did not implement the building Interface!", config.getBuildingId(), packageConfig.packageDisplayName, packageConfig.getPackageId())));
            return true;
        }
        return false;
    }

    public BufferedImage loadBuildingTexture(BuildingConfig config, GamePackage packageConfig) {
        File fi = new File(Main.getGamePath() + config.getTexturePath());
        if(!fi.exists()){
            Main.getClient().setErrorGameStateException(new FileNotFoundException(String.format("The texture for the Building %s in the package %s (%s) was not found at %s", config.getBuildingId(), packageConfig.packageDisplayName, packageConfig.getPackageId(), Main.getGamePath() + config.getTexturePath())));
        }

        try {
            return ImageIO.read(fi);
        }catch (IOException e){
            Main.getClient().setErrorGameStateException(new IOException(String.format("While loading the texture for building %s in package %s (%s) the file was not in the right format or was accessed by another program, File: %s", config.getBuildingId(), packageConfig.packageDisplayName, packageConfig.getPackageId(), Main.getGamePath() + config.getTexturePath()), e));
        }
        return null;
    }
}
