package GameCore;

import GameContent.GamePackage;
import GameCore.GuiElements.DynamicGuiElement;
import GameCore.GuiElements.InteractiveGuiElement;
import GameCore.GuiElements.StaticGuiElement;
import GameUtils.GameUtils;

import java.awt.*;
import java.awt.image.BufferedImage;

@SuppressWarnings("record")
public class GuiBuilder {

    public final int width;
    public final int height;
    public final Color backgroundColor;
    public final Font numberFont;
    public final StaticGuiElement[] staticGuiComponents;
    public final DynamicGuiElement[] activeGuiComponents;
    public final InteractiveGuiElement[] interactiveGuiComponents;
    private String guiId;

    public GuiBuilder(int width, int height, Color backgroundColor, Font numberFont, StaticGuiElement[] staticGuiComponents, DynamicGuiElement[] activeGuiComponents, InteractiveGuiElement[] interactiveGuiComponents, String guiId) {
        this.width = width;
        this.height = height;
        this.backgroundColor = backgroundColor;
        this.numberFont = numberFont;
        this.staticGuiComponents = staticGuiComponents;
        this.activeGuiComponents = activeGuiComponents;
        this.interactiveGuiComponents = interactiveGuiComponents;
        this.guiId = guiId;
    }

    public Gui build(GamePackage conf) {
        BufferedImage gui = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g2 = (Graphics2D) gui.getGraphics();
        g2.setColor(backgroundColor);
        g2.fillRect(0, 0, width, height);
        for(StaticGuiElement element : staticGuiComponents){
            element.draw(g2);
        }
        for(InteractiveGuiElement element : interactiveGuiComponents){
            boolean compile = true;
            for(char forbidden : GameUtils.getForbiddenFileNameChars()){
                if(GameUtils.containsChar(element.getClassName(), forbidden)){
                    Main.getClient().setErrorGameStateException(new IllegalArgumentException("The runtime compiled class: " + element.getClassName() + " contains illegal characters that cannot be applied to file names!"));
                    compile = false;
                }
            }
            if(compile) {
                element.compile(conf, guiId);
            }
        }
        return new Gui(gui, activeGuiComponents, interactiveGuiComponents, guiId);
    }

    public void setGuiId(String guiId) {
        this.guiId = guiId;
    }
}
