package GameInput;

import GameCore.Main;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MouseInput implements MouseListener {
    @Override
    public void mouseClicked(MouseEvent e) {
        Main.getClient().clientGraphics().gui().sendMouseEventAll(e);
        //Main.getClient().buildingCore().addBuilding(Main.getClient().buildingCore().getBuildingInstance("Mine", GameInputMain.getCursorGridPosition().x, GameInputMain.getCursorGridPosition().y));
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
