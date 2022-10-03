package GameCore;

import GameBuildings.BuildingCore;
import GameContent.*;
import GameUtils.Vec2i;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameCore extends JPanel {
    public GameCore(int windowWidth, int windowHeight) {
        this.setPreferredSize(new Dimension(windowWidth,windowHeight));
        this.setBackground(new Color(255,255,255));
        this.setDoubleBuffered(true);
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.gridWidth = windowWidth / 10;
        this.gridHeight = windowHeight / 10;
    }

    private final GameGraphics graphics = new GameGraphics();
    private final BuildingCore buildingCore = new BuildingCore();
    private final GameConfigLoader configLoader = new GameConfigLoader();
    private final GameContentLoader contentLoader = new GameContentLoader();
    private final GamePackageLoader packageLoader = new GamePackageLoader();

    private final HashMap<String, GamePackage> packageConfigs = new HashMap<>();
    private final HashMap<String, PackageCore> packageCores = new HashMap<>();
    private final ArrayList<PackageCore> packageTickUpdated = new ArrayList<>();
    private final ArrayList<PackageCore> packageFrameUpdated = new ArrayList<>();

    private int windowWidth = 0;
    private int windowHeight = 0;
    private int gridWidth = 0;
    private int gridHeight = 0;
    private GameStates gameState = GameStates.Loading;
    private Exception errorGameStateException;

    public Vec2i getWindowDimensions() {
        return new Vec2i(windowWidth, windowHeight);
    }

    public Vec2i getGridDimensions() {
        return new Vec2i(gridWidth, gridHeight);
    }

    public BuildingCore buildingCore() {
        return buildingCore;
    }

    public GameGraphics clientGraphics() {
        return graphics;
    }

    public GameStates getGameState() {
        return gameState;
    }

    public void setErrorGameStateException(Exception errorGameStateException) {
        if(gameState != GameStates.Error) {
            this.gameState = GameStates.Error;
            this.errorGameStateException = errorGameStateException;
        }
    }

    public Exception getErrorGameStateException() {
        return errorGameStateException;
    }

    public int getGridHeight() {
        return gridHeight;
    }

    public int getGridWidth() {
        return gridWidth;
    }

    public void StartGameLoop(){
        this.gameState = GameStates.Game;
        startup();
        ScheduledExecutorService graphicsLoop = Executors.newSingleThreadScheduledExecutor();
        graphicsLoop.scheduleAtFixedRate(() -> {
                currentFrame++;
                repaint();
        }, 0, 16, TimeUnit.MILLISECONDS);

        ScheduledExecutorService itemLoop = Executors.newSingleThreadScheduledExecutor();
        itemLoop.scheduleAtFixedRate(() -> {
            if(gameState != GameStates.Game){
                return;
            }
            buildingCore.updateLeftRightTopBottom();
            buildingCore.updateRightLeftBottomTop();
        }, 0, 1000, TimeUnit.MILLISECONDS);

        ScheduledExecutorService workLoop = Executors.newSingleThreadScheduledExecutor();
        workLoop.scheduleAtFixedRate(() -> {
            if(gameState != GameStates.Game){
                return;
            }
            buildingCore.updateWorkAll();
        }, 0, 50, TimeUnit.MILLISECONDS);
    }

    public void startup() {
        //average fps calculation setup
        startMilli = System.currentTimeMillis();

        //prepare config loading (setting evaluated string aliases)
        try{
            registerEvaluatorAlias();
        }catch (NoSuchMethodException e){
            setErrorGameStateException(e);
        }

        //load configs
        configLoader.loadGuiConfig("\\GameCore\\Guis.json");
        configLoader.loadBuildingConfig("\\GameContent\\BuildingConfig.json");
        configLoader.loadItemConfig("\\GameContent\\ItemConfig.json");
        configLoader.loadOreConfig("\\GameContent\\OreConfig.json");

        //load game content from configs
        contentLoader.BuildGuis(this);
        contentLoader.loadBuildingTextures(this);
        contentLoader.loadBuildingGuiTextures(this);
        contentLoader.loadAndMapGuiAnimationFrames(this);
        contentLoader.loadItemTextures(this);

        //ready playing field (temporary until main menu)
        buildingCore.populate();

        packageLoader.loadPackages();

        for(Map.Entry<String, PackageCore> entry : packageCores.entrySet()){
            entry.getValue().initialize();

            if(packageConfigs.get(entry.getKey()).tickUpdates){
                packageFrameUpdated.add(entry.getValue());
            }
            if(packageConfigs.get(entry.getKey()).frameUpdates){
                packageFrameUpdated.add(entry.getValue());
            }
        }
    }

    private void registerEvaluatorAlias() throws NoSuchMethodException {
        GameEvaluator.registerAlias(Main.getClient(), GameCore.class.getDeclaredMethod("windowWidth"), "{windowWidth}");
        GameEvaluator.registerAlias(Main.getClient(), GameCore.class.getDeclaredMethod("windowHeight"), "{windowHeight}");
    }

    private long startMilli;
    private long currentFrame = 0;

    public float getAverageFps(){
        return currentFrame / ((System.currentTimeMillis() - startMilli) / 1000f);
    }

    public void update(){

    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        for(PackageCore core : packageFrameUpdated){
            core.frame(g);
        }
        switch (gameState){

            case Error:
                graphics.printErrorScreen(g);
                break;
            case Loading:
                graphics.printLoadingScreen(g);
                break;
            case MainMenu:
                break;
            case Game:
                graphics.printMainGame(g);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + gameState);
        }

        g2.dispose();
    }

    public int windowWidth() {
        return windowWidth;
    }

    public int windowHeight() {
        return windowHeight;
    }

    public void registerPackage(String name, GamePackage packageConfig, Class<? extends PackageCore> packageCoreClass) {
        packageConfigs.put(name, packageConfig);
        try {
            PackageCore coreInstance = packageCoreClass.getDeclaredConstructor().newInstance();
            packageCores.put(name, coreInstance);
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            setErrorGameStateException(e);
        }
    }
}
