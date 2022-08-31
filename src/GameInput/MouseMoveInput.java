package GameInput;

import GameCore.GameGui;
import GameUtils.Vec2i;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class MouseMoveInput implements MouseMotionListener {
    @Override
    public void mouseDragged(MouseEvent e) {
        GameGui.sendMouseMoveEventAll(e);
        GameInputeMain.updateMousePosition(new Vec2i(e.getX(), e.getY()));
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        GameGui.sendMouseMoveEventAll(e);
        GameInputeMain.updateMousePosition(new Vec2i(e.getX(), e.getY()));
    }
}
