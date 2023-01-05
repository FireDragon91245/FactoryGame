package GameCore.GuiElements;

import GameContent.GamePackage;

import java.awt.event.MouseEvent;

public interface InteractiveGuiElement {

    void interact();

    void compile(GamePackage conf, String guiId);

    String getClassName();

    void mouseMoved(MouseEvent event);

    void mousePressed(MouseEvent event);


}
