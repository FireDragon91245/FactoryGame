package GameCore.GuiElements;

import GameBuildings.Building;
import GameCore.Main;

import java.awt.*;

@SuppressWarnings({"record", "unused"})
public final class NonAnimatedGuiIcon implements DynamicGuiElement {

    public final int startX;
    public final int startY;
    public final int width;
    public final int height;

    public NonAnimatedGuiIcon(int startX, int startY, int width, int height) {
        this.startX = startX;
        this.startY = startY;
        this.width = width;
        this.height = height;
    }

    @Override
    public void drawDynamic(Graphics2D g2, Building b, int x, int y) {
        g2.drawImage(Main.getClient().clientGraphics().getBuildingGuiImage(b.getType()), x + startX, y + startY, width, height, Main.getClient().clientGraphics().getMainObserver());
    }

}
