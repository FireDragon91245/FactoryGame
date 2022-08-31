package GameInput;

import GameUtils.Vec2i;

public class GameInputeMain {
    private static boolean isMousePressed;
    private static Vec2i cursorGridPosition = new Vec2i();
    private static Vec2i cursorPosition = new Vec2i();

    public static boolean isMousePressed() {
        return isMousePressed;
    }

    public static void setMousePressed(boolean mousePressed) {
        isMousePressed = mousePressed;
    }

    public static Vec2i getCursorGridPosition() {
        return cursorGridPosition;
    }

    public static void updateMousePosition(Vec2i pos){
        cursorGridPosition = pixelPosToGridPos(pos);
        cursorPosition = new Vec2i(pos.x, pos.y - 35);
    }

    public static Vec2i getCursorPosition() {
        return cursorPosition;
    }

    private static Vec2i pixelPosToGridPos(Vec2i pixelPos){
        return new Vec2i((pixelPos.x - 10) / 10, (pixelPos.y - 35) / 10);
    }
}
