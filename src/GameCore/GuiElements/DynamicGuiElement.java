package GameCore.GuiElements;

import GameBuildings.Building;

import java.awt.*;

public interface DynamicGuiElement {

    void drawDynamic(Graphics2D g2, Building b, int x, int y);

}
