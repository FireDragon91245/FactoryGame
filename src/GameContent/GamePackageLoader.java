package GameContent;

import GameCore.Main;
import GameUtils.GameUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class GamePackageLoader {


    public void loadPackages(){
        File[] packageFolders = new File(Main.gamePath + "Packages\\").listFiles(File::isDirectory);

        if(packageFolders == null){
            return;
        }

        for(File currentPackageFolder : packageFolders){
            loadPackage(currentPackageFolder);
        }
    }

    private void loadPackage(File currentPackageFolder) {
        GamePackage packageConfig = loadPackageConfig(currentPackageFolder);
        Class<? extends PackageCore> packageCoreClass = loadPackageCoreClass(currentPackageFolder);

        if(packageConfig == null){
            Main.getClient().setErrorGameStateException(new NullPointerException(String.format("Core Loading And Initializing of package %s failed do to package Config not loading correctly!", currentPackageFolder.getName())));
        }
        if(packageCoreClass == null){
            Main.getClient().setErrorGameStateException(new NullPointerException(String.format("Core Loading And Initializing of package %s failed do to package core class error!", currentPackageFolder.getName())));
            return;
        }

        Main.getClient().registerPackage(currentPackageFolder.getName(), packageConfig, packageCoreClass);

        loadPackageGuis(packageConfig);
    }

    private void loadPackageGuis(GamePackage packageConfig) {
        File guiConfig = new File(Main.gamePath + "\\" + packageConfig.guiGfg);
    }

    @SuppressWarnings("unchecked")
    private Class<? extends PackageCore> loadPackageCoreClass(File currentPackageFolder) {
        File packageCoreClass = new File(currentPackageFolder.getAbsolutePath() + "\\" + currentPackageFolder.getName() + ".class");

        if(packageCoreClass.exists()){
            Class<?> cls = GameCompiler.loadClass(packageCoreClass, currentPackageFolder.getAbsolutePath(), "Packages." + currentPackageFolder.getName() + "." + currentPackageFolder.getName());
            if(cls == null){
                Main.getClient().setErrorGameStateException(new NullPointerException(String.format("While loading the package %s the result of loading the core class was null!", currentPackageFolder.getName())));
                return null;
            }
            if(PackageCore.class.isAssignableFrom(cls)){
                return (Class<? extends PackageCore>) cls;
            }else{
                Main.getClient().setErrorGameStateException(new ClassCastException(String.format("The loading of the core class for package %s resulted in a class that does not implement the %s interface", currentPackageFolder.getName(), PackageCore.class.getCanonicalName())));
                return null;
            }
        }else{
            File packageCoreSource = new File(currentPackageFolder.getAbsolutePath() + "\\" + currentPackageFolder.getName() + ".java");
            if(!packageCoreSource.exists()){
                Main.getClient().setErrorGameStateException(new FileNotFoundException(String.format("Trying to find core class file ore core class source code for package %s failed!", currentPackageFolder.getName())));
                return null;
            }

            Class<?> cls = GameCompiler.compileFile(packageCoreSource, currentPackageFolder.getAbsolutePath(), currentPackageFolder.getName());
            if(cls == null){
                Main.getClient().setErrorGameStateException(new NullPointerException(String.format("loading the package core class of package %s result in null!", currentPackageFolder.getName())));
                return null;
            }
            if(!cls.isAssignableFrom(PackageCore.class)){
                Main.getClient().setErrorGameStateException(new ClassCastException(String.format("The source class of the package %s does not implement %s, Source File: %s", currentPackageFolder.getName(), PackageCore.class.getCanonicalName(), packageCoreSource.getAbsolutePath())));
                return null;
            }

            return (Class<? extends PackageCore>) cls;
        }
    }

    private GamePackage loadPackageConfig(File currentPackageFolder) {
        File packageConfig = new File(currentPackageFolder.getAbsolutePath() + "\\" + currentPackageFolder.getName() + ".json");

        if(!packageConfig.exists()){
            Main.getClient().setErrorGameStateException(new FileNotFoundException(String.format("The package config file was not found for package %s, the file was expected at %s", currentPackageFolder.getName(), packageConfig.getAbsolutePath())));
        }

        try {
            return Main.getGsonMaster().fromJson(GameUtils.readFile(packageConfig.getAbsolutePath()), GamePackage.class);
        }catch(IOException e){
            Main.getClient().setErrorGameStateException(e);
        }
        return null;
    }

}
