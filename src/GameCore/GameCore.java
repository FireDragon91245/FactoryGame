package GameCore;

import GameBuildings.BuildingCore;
import GameBuildings.OreGeneration;
import GameContent.GameConfigLoader;
import GameContent.GameContentExtractor;
import GameContent.GameContentLoader;
import GameContent.GameEvaluator;

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
        GameCore.windowWidth = windowWidth;
        GameCore.windowHeight = windowHeight;
        GameCore.gridWidth = windowWidth / 10;
        GameCore.gridHeight = windowHeight / 10;
    }

    private static final ArrayList<String> possibleRootFolders = new ArrayList<>();
    private static int windowWidth = 0;
    private static int windowHeight = 0;
    private static int gridWidth = 0;
    private static int gridHeight = 0;
    private static GameStates gameState = GameStates.Loading;
    private static Exception errorGameStateException;

    public static GameStates getGameState() {
        return gameState;
    }

    public static void setErrorGameStateException(Exception errorGameStateException) {
        if(gameState != GameStates.Error) {
            GameCore.gameState = GameStates.Error;
            GameCore.errorGameStateException = errorGameStateException;
        }
    }

    public static Exception getErrorGameStateException() {
        return errorGameStateException;
    }

    public static int getGridHeight() {
        return gridHeight;
    }

    public static int getGridWidth() {
        return gridWidth;
    }

    public static void addRootFolders(String path){
        possibleRootFolders.add(path);
    }

    public static ArrayList<String> getRootFolders() {
        return possibleRootFolders;
    }

    public void StartGameLoop(){
        prepareGameContent();
        startup();
        gameState = GameStates.Game;
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
            BuildingCore.updateLeftRightTopBottom();
            BuildingCore.updateRightLeftBottomTop();
        }, 0, 1000, TimeUnit.MILLISECONDS);

        ScheduledExecutorService workLoop = Executors.newSingleThreadScheduledExecutor();
        workLoop.scheduleAtFixedRate(() -> {
            if(gameState != GameStates.Game){
                return;
            }
            BuildingCore.updateWorkAll();
        }, 0, 50, TimeUnit.MILLISECONDS);
    }

    private void prepareGameContent() {
        String path = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        if(path.endsWith(".jar") || path.endsWith(".zip")){
            GameContentExtractor.extractAll(path);
        }
        GameContentExtractor.findPossibleRootFolder();
    }

    public static void startup() {
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

    private static void registerEvaluatorAlias() throws NoSuchMethodException {
        GameEvaluator.registerAlias(Main.game, GameCore.class.getDeclaredMethod("windowWidth"), "{windowWidth}");
        GameEvaluator.registerAlias(Main.game, GameCore.class.getDeclaredMethod("windowHeight"), "{windowHeight}");
    }

    private static long startMilli;
    private static long currentFrame = 0;

    public static float getAverageFps(){
        return currentFrame / ((System.currentTimeMillis() - startMilli) / 1000f);
    }

    public void update(){

    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        switch (gameState){

            case Error:
                GameGraphics.printErrorScreen(g);
                break;
            case Loading:
                GameGraphics.printLoadingScreen(g);
                break;
            case MainMenu:
                break;
            case Game:
                GameGraphics.printMainGame(g);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + gameState);
        }

        g2.dispose();
    }

    public static int windowWidth() {
        return windowWidth;
    }

    public static int windowHeight() {
        return windowHeight;
    }
}
