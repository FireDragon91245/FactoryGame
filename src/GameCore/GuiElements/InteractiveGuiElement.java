package GameCore.GuiElements;

import java.awt.event.MouseEvent;

public interface InteractiveGuiElement {

    void interact();

    void compile();

    String getClassName();

    void mouseMoved(MouseEvent event);

    void mousePressed(MouseEvent event);


}
