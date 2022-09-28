package GameCore;

import GameBuildings.Building;
import GameBuildings.Buildings;
import GameInput.GameInputMain;
import GameUtils.GameUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class GameGraphics {
    private final JLayeredPane mainPane = new JLayeredPane();
    private final HashMap<Buildings, BufferedImage> buildingTextures = new HashMap<>();
    private final HashMap<Buildings, BufferedImage> buildingGuiTextures = new HashMap<>();

    private final GameGui gameGui = new GameGui();

    public void RegisterBuildingImage(Buildings bId, BufferedImage img){
        buildingTextures.put(bId, img);
    }

    public void printErrorScreen(@NotNull Graphics g){
        SetBackground(g, Color.DARK_GRAY);
        g.setColor(Color.RED);
        g.setFont(new Font("TimesRoman", Font.BOLD, 20));
        int i = 0;
        for(String s : GameUtils.ChopValue( Main.getClient().getErrorGameStateException().toString(), 40)){
            i++;
            g.drawString(s, Main.getClient().windowWidth() / 2 - (g.getFontMetrics().stringWidth(s) / 2), 200 + 20 * i);
        }
    }

    public void SetBackground(@NotNull Graphics2D g2, Color col){
        g2.setColor(col);
        g2.fillRect(0, 0, Main.getClient().windowWidth(), Main.getClient().windowHeight());
    }

    public void SetBackground(@NotNull Graphics g, Color col){
        g.setColor(col);
        g.fillRect(0, 0, Main.getClient().windowWidth(), Main.getClient().windowHeight());
    }

    public void printLoadingScreen(Graphics g) {
        g.drawImage(buildingTextures.get(Buildings.Mine), 200, 200,200, 200, mainPane);
    }

    public void printMainGame(Graphics g) {
        printBuildings(g);
        if(Main.getClient().buildingCore().isBuildingAtPos(GameInputMain.getCursorGridPosition())){
            Building b =  Main.getClient().buildingCore().getBuilding(GameInputMain.getCursorGridPosition());
            if(b != null){
                gameGui.RenderBuildingInventoryGui(g, GameInputMain.getCursorGridPosition(), b);
            }
        }
        gameGui.getGui(GuiTypes.PlayingMainGui).render(0, 0, g, null);
        g.setColor(new Color(0, 125, 0, 100));
        g.fillRect(GameInputMain.getCursorGridPosition().x * 10, GameInputMain.getCursorGridPosition().y * 10, 10, 10);
    }

    private void printBuildings(Graphics g) {
        for(int x : Main.getClient().buildingCore().getAllBuildings().keySet()){
            for(int y : Main.getClient().buildingCore().getAllBuildings().get(x).keySet()){
                g.drawImage(buildingTextures.get(Main.getClient().buildingCore().getAllBuildings().get(x).get(y).getType()), x * 10, y * 10, 10, 10, mainPane);
            }
        }
    }

    public GameGui gui(){
        return gameGui;
    }

    public void RegisterBuildingGuiImage(Buildings b, BufferedImage image) {
        buildingGuiTextures.put(b, image);
    }

    public JLayeredPane getMainObserver() {
        return mainPane;
    }

    public BufferedImage getBuildingGuiImage(Buildings b) {
        return buildingGuiTextures.get(b);
    }
}


