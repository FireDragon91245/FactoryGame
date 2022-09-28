package GameInput;

import GameCore.Main;
import GameUtils.Vec2i;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class MouseMoveInput implements MouseMotionListener {
    @Override
    public void mouseDragged(MouseEvent e) {
        Main.getClient().clientGraphics().gui().sendMouseMoveEventAll(e);
        GameInputMain.updateMousePosition(new Vec2i(e.getX(), e.getY()));
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Main.getClient().clientGraphics().gui().sendMouseMoveEventAll(e);
        GameInputMain.updateMousePosition(new Vec2i(e.getX(), e.getY()));
    }
}
