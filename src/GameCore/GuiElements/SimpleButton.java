package GameCore.GuiElements;

import GameContent.GameCompiler;
import GameContent.GamePackage;
import GameCore.Main;
import GameUtils.GameUtils;

import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

@SuppressWarnings("unused")
public class SimpleButton implements InteractiveGuiElement {

    public final int startX;
    public final int startY;
    public final int width;
    public final int height;
    public final String interactionCode;
    public final String interactionFile;
    public final String className;

    private Interactable instance;

    public SimpleButton(int startX, int startY, int width, int height, String interactionCode, String interactionFile, String className) {
        this.interactionFile = interactionFile;
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

    @SuppressWarnings("unchecked")
    @Override
    public void compile(GamePackage conf, String guiID) {
        Class<?> interactionClass = null;
        if(interactionCode == null){
            File fJava = new File(Main.getGamePath() + "\\" + interactionFile + ".java");
            File fClass = new File(Main.getGamePath() + "\\" + interactionFile + ".class");
            if(fJava.exists()){
                try {
                    interactionClass = GameCompiler.compileCode(GameUtils.readFile(fJava), className);
                } catch (IOException e) {
                    Main.getClient().setErrorGameStateException(new FileNotFoundException(String.format("The file containing interaction code for %s in gui: %s from package %s (%s) was not found at %s!", SimpleButton.class.getSimpleName(), guiID, conf.packageDisplayName, conf.getPackageId(), fJava.getAbsolutePath())));
                    return;
                }
            } else if (fClass.exists()) {
                interactionClass = GameCompiler.loadClass(fClass, className);
            }else {
                Main.getClient().setErrorGameStateException(new FileNotFoundException(String.format("The file containing the interaction code for %s in gui: %s from package %s (%s) expected at %s OR %s was not found!", SimpleButton.class.getSimpleName(), guiID, conf.packageDisplayName, conf.getPackageId(), fJava.getPath(), fClass.getPath())));
                return;
            }
        }else {
            interactionClass = GameCompiler.compileCode(interactionCode, className);
        }
        if (interactionClass == null) {
            Main.getClient().setErrorGameStateException(new NullPointerException(String.format("Loading %s for gui: %s in package %s (%s)", SimpleButton.class.getSimpleName(), guiID, conf.packageDisplayName, conf.getPackageId())));
            return;
        }
        if (!Interactable.class.isAssignableFrom(interactionClass)) {
            Main.getClient().setErrorGameStateException(new ClassCastException(String.format("The class %s for a simple button did not implement %s!", className, Interactable.class.getCanonicalName())));
            return;
        }

        try {
            instance = ((Class<? extends Interactable>)interactionClass).getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            Main.getClient().setErrorGameStateException(e);
        }

        if (instance == null) {
            Main.getClient().setErrorGameStateException(new NullPointerException("The result of the instantiation of the runtime compiled class: " + className + " resulted in a null instance"));
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
