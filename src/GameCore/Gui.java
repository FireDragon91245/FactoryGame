package GameCore;

import GameBuildings.Building;
import GameCore.GuiElements.DynamicGuiElement;
import GameCore.GuiElements.InteractiveGuiElement;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;


@SuppressWarnings("record")
public class Gui {

    public final BufferedImage image;

    public final DynamicGuiElement[] activeRenderers;
    public final InteractiveGuiElement[] interactiveGuiElements;

    public Gui(BufferedImage image, DynamicGuiElement[] activeRenderers, InteractiveGuiElement[] interactiveGuiElements) {
        this.image = image;
        this.activeRenderers = activeRenderers;
        this.interactiveGuiElements = interactiveGuiElements;
    }

    public void render(int startX, int startY, Graphics g, Building b) {
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(image, startX, startY, image.getWidth(), image.getHeight(), Main.getClient().clientGraphics().getMainObserver());
        for(DynamicGuiElement active : activeRenderers){
            active.drawDynamic(g2, b, startX, startY);
        }
    }

    public void sendMouseEvent(MouseEvent event) {
        for(InteractiveGuiElement element : interactiveGuiElements){
            element.mousePressed(event);
        }
    }

    public void sendMouseMoveEvent(MouseEvent event) {
        for(InteractiveGuiElement element : interactiveGuiElements){
            element.mouseMoved(event);
        }
    }
}
