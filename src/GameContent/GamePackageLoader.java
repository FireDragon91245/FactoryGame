package GameContent;

import GameBuildings.Building;
import GameBuildings.BuildingConfig;
import GameCore.GuiBuilder;
import GameCore.Main;

import java.io.File;
import java.util.ArrayList;

public class GamePackageLoader {

    GameConfigLoader configLoader = new GameConfigLoader();
    GameContentLoader contentLoader = new GameContentLoader();
    GameContentValidation contentValidation = new GameContentValidation();

    public void loadPackages(){
        File[] packageFolders = new File(Main.getGamePath() + "Packages\\").listFiles(File::isDirectory);

        if(packageFolders == null){
            return;
        }

        for(File currentPackageFolder : packageFolders){
            loadPackage(currentPackageFolder);
        }
    }

    private void loadPackage(File currentPackageFolder) {
        GamePackage packageConfig = configLoader.loadPackageConfig(currentPackageFolder);
        if(packageConfig == null){
            Main.getClient().setErrorGameStateException(new NullPointerException(String.format("Core Loading And Initializing of package %s failed do to package Config not loading correctly!", currentPackageFolder.getName())));
            return;
        }

        Class<? extends PackageCore> packageCoreClass = contentLoader.loadPackageCoreClass(currentPackageFolder);
        if(packageCoreClass == null){
            Main.getClient().setErrorGameStateException(new NullPointerException(String.format("Core Loading And Initializing of package %s (%s) failed do to package core class error!", packageConfig.packageDisplayName, packageConfig.getPackageId())));
            return;
        }
        Main.getClient().registerPackage(packageConfig.getPackageId(), packageConfig, packageCoreClass);

        ArrayList<GuiBuilder> guiBuilders = configLoader.loadGuiConfig(packageConfig);
        if(guiBuilders == null){
            Main.getClient().setErrorGameStateException(new NullPointerException(String.format("The result of loading the guis for package %s (%s) resulted in null!", packageConfig.packageDisplayName, packageConfig.getPackageId())));
            return;
        }
        for (GuiBuilder builder : guiBuilders){
            Main.getClient().clientGraphics().gui().registerGui(builder.build());
        }

        ArrayList<BuildingConfig> buildingConfigs = configLoader.loadBuildingConfig(packageConfig);
        if(buildingConfigs == null){
            Main.getClient().setErrorGameStateException(new NullPointerException(String.format("The result of loading building configs for package %s (%s) resulted in null!", packageConfig.packageDisplayName, packageConfig.getPackageId())));
            return;
        }
        for (BuildingConfig config : buildingConfigs) {
            Main.getClient().buildingCore().registerBuildingConfig(config);
            Class<? extends Building> bClass = contentLoader.loadBuildingClass(config, packageConfig);
            if(bClass == null){
                Main.getClient().setErrorGameStateException(new NullPointerException(String.format("The result of loading the building class for building %s in the package %s (%s) resulted in null", config.getBuildingId(), packageConfig.packageDisplayName, packageConfig.getPackageId())));
                return;
            }
            Main.getClient().buildingCore().registerBuildingClass(config.getBuildingId(), contentLoader.loadBuildingClass(config, packageConfig));
            Main.getClient().clientGraphics().registerBuildingTexture(config.getBuildingId(), contentLoader.loadBuildingTexture(config, packageConfig));

            //TODO: Error on loading package config locations for other configs == null
            //TODO: Building Custom content revisit later not implemented for now
        }
    }

}
