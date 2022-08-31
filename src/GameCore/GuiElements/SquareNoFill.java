package GameCore.GuiElements;

import java.awt.*;

@SuppressWarnings({"record", "unused"})
public final class SquareNoFill implements StaticGuiElement {

    public final int strokeWeight;
    public final int startX;
    public final int startY;
    public final int width;
    public final int height;
    public final Color color;

    public SquareNoFill(int strokeWeight, int startX, int startY, int width, int height, Color color) {
        this.strokeWeight = strokeWeight;
        this.startX = startX;
        this.startY = startY;
        this.width = width;
        this.height = height;
        this.color = color;
    }

    @Override
    public void draw(Graphics2D input) {
        input.setColor(color);
        input.setStroke(new BasicStroke(strokeWeight));
        input.drawRect(startX, startY, width, height);
    }
}
