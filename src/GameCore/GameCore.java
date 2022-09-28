package GameCore;

import GameBuildings.BuildingCore;
import GameBuildings.OreGeneration;
import GameContent.GameConfigLoader;
import GameContent.GameContentExtractor;
import GameContent.GameContentLoader;
import GameContent.GameEvaluator;
import GameUtils.Vec2i;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
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

    private final ArrayList<String> possibleRootFolders = new ArrayList<>();
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

    public void addRootFolders(String path){
        possibleRootFolders.add(path);
    }

    public ArrayList<String> getRootFolders() {
        return possibleRootFolders;
    }

    public void StartGameLoop(){
        prepareGameContent();
        startup();
        this.gameState = GameStates.Game;
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

    private void prepareGameContent() {
        String path = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        if(path.endsWith(".jar") || path.endsWith(".zip")){
            GameContentExtractor.extractAll(path);
        }
        GameContentExtractor.findPossibleRootFolder();
    }

    public void startup() {
        startMilli = System.currentTimeMillis();

        try{
            registerEvaluatorAlias();
        }catch (NoSuchMethodException e){
            setErrorGameStateException(e);
        }

        GameConfigLoader.loadGuiConfig("\\GameCore\\Guis.json");
        GameContentLoader.BuildGuis();
        GameConfigLoader.loadBuildingConfig("\\GameContent\\BuildingConfig.json");
        GameContentLoader.loadBuildingTextures();
        GameContentLoader.loadBuildingGuiTextures();
        GameContentLoader.loadAndMapGuiAnimationFrames();
        GameConfigLoader.loadItemConfig("\\GameContent\\ItemConfig.json");
        GameContentLoader.loadItemTextures();
        GameConfigLoader.loadOreConfig("\\GameContent\\OreConfig.json");
        OreGeneration.GenerateOres();
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
}
