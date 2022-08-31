package GameCore.GuiElements;

import GameContent.GameCompiler;
import GameCore.GameCore;

import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;

public class SimpleButton implements InteractiveGuiElement {

    public final int startX;
    public final int startY;
    public final int width;
    public final int height;
    public final String interactionCode;
    public final String className;

    private Intractable instance;

    public SimpleButton(int startX, int startY, int width, int height, String interactionCode, String className) {
        this.className = className;
        this.startX = startX;
        this.startY = startY;
        this.interactionCode = interactionCode;
        this.width = width;
        this.height = height;
    }

    @Override
    public void interact() {
        if (instance == null) {
            return;
        }
        instance.interact();
    }

    @Override
    public void compile() {
        Class<? extends Intractable> interactionClass = GameCompiler.compileCode(interactionCode, className);
        if (interactionClass == null) {
            GameCore.setErrorGameStateException(new NullPointerException("The result of the runtime compilation for class name: " + className + " resulted in a null value"));
        } else {
            try {
                instance = interactionClass.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                GameCore.setErrorGameStateException(e);
            }
        }
        if (instance == null) {
            GameCore.setErrorGameStateException(new NullPointerException("The result of the instantiation of the runtime compiled class: " + className + " resulted in a null instance"));
        }
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public void mouseMoved(MouseEvent event) {
    }

    @Override
    public void mousePressed(MouseEvent event) {
        if (event.getButton() != 1) {
            return;
        }
        if (event.getX() > startX && event.getY() - 35 > startY) {
            if (event.getX() < startX + width && event.getY() - 35 < startY + height) {
                interact();
            }
        }
    }
}