package GameCore;

import GameBuildings.Building;
import GameBuildings.BuildingCore;
import GameBuildings.Buildings;
import GameInput.GameInputeMain;
import GameUtils.GameUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class GameGraphics {
    private static final JLayeredPane mainPane = new JLayeredPane();
    private static final HashMap<Buildings, BufferedImage> buildingTextures = new HashMap<>();
    private static final HashMap<Buildings, BufferedImage> buildingGuiTextures = new HashMap<>();

    public static void RegisterBuildingImage(Buildings bId, BufferedImage img){
        buildingTextures.put(bId, img);
    }

    public static void printErrorScreen(@NotNull Graphics g){
        SetBackground(g, Color.DARK_GRAY);
        g.setColor(Color.RED);
        g.setFont(new Font("TimesRoman", Font.BOLD, 20));
        int i = 0;
        for(String s : GameUtils.ChopValue( GameCore.getErrorGameStateException().toString(), 40)){
            i++;
            g.drawString(s, GameCore.windowWidth() / 2 - (g.getFontMetrics().stringWidth(s) / 2), 200 + 20 * i);
        }
    }

    public static void SetBackground(@NotNull Graphics2D g2, Color col){
        g2.setColor(col);
        g2.fillRect(0, 0, GameCore.windowWidth(), GameCore.windowHeight());
    }

    public static void SetBackground(@NotNull Graphics g, Color col){
        g.setColor(col);
        g.fillRect(0, 0, GameCore.windowWidth(), GameCore.windowHeight());
    }

    public static void printLoadingScreen(Graphics g) {
        g.drawImage(buildingTextures.get(Buildings.Mine), 200, 200,200, 200, mainPane);
    }

    public static void printMainGame(Graphics g) {
        printBuildings(g);
        if(BuildingCore.isBuildingAtPos(GameInputeMain.getCursorGridPosition())){
            Building b =  BuildingCore.getBuilding(GameInputeMain.getCursorGridPosition());
            if(b != null){
                GameGui.RenderBuildingInventoryGui(g, GameInputeMain.getCursorGridPosition(), b);
            }
        }
        GameGui.getGui(GuiTypes.PlayingMainGui).render(0, 0, g, null);
        g.setColor(new Color(0, 125, 0, 100));
        g.fillRect(GameInputeMain.getCursorGridPosition().x * 10, GameInputeMain.getCursorGridPosition().y * 10, 10, 10);
    }

    private static void printBuildings(Graphics g) {
        for(int x : BuildingCore.getAllBuildings().keySet()){
            for(int y : BuildingCore.getAllBuildings().get(x).keySet()){
                g.drawImage(buildingTextures.get(BuildingCore.getAllBuildings().get(x).get(y).getType()), x * 10, y * 10, 10, 10, mainPane);
            }
        }
    }

    public static void RegisterBuildingGuiImage(Buildings b, BufferedImage image) {
        buildingGuiTextures.put(b, image);
    }

    public static JLayeredPane getMainObserver() {
        return mainPane;
    }

    public static BufferedImage getBuildingGuiImage(Buildings b) {
        return buildingGuiTextures.get(b);
    }
}


