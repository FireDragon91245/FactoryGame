package GameCore;

import GameBuildings.Building;
import GameConverter.*;
import GameCore.GuiElements.DynamicGuiElement;
import GameCore.GuiElements.InteractiveGuiElement;
import GameCore.GuiElements.StaticGuiElement;
import GameInput.MouseInput;
import GameInput.MouseMoveInput;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Objects;

public class Main {

    public static final JFrame window = new JFrame();
    public static final GameCore game = new GameCore(800, 800);
    public static final Gson gson = createGson();

    public static void main(String[] args) {
        readArgs(args);

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("FactoryGame");

        window.addMouseMotionListener(new MouseMoveInput());
        window.addMouseListener(new MouseInput());
        //window.addKeyListener(new KeyHandler());

        window.add(game);
        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);

        game.StartGameLoop();
    }

    private static void readArgs(String[] args) {
        if (Arrays.stream(args).anyMatch(y -> Objects.equals(y.toLowerCase(), "--dev"))) {
        }
    }

    private static Gson createGson() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Color.class, new ColorDeserializer());
        builder.registerTypeAdapter(Font.class, new FontDeserializer());
        builder.registerTypeAdapter(StaticGuiElement.class, new StaticGuiElementDeserializer());
        builder.registerTypeAdapter(DynamicGuiElement.class, new DynamicGuiElementDeserializer());
        builder.registerTypeHierarchyAdapter(Building.class, new BuildingSerializer());
        builder.registerTypeAdapter(InteractiveGuiElement.class, new InteractiveGuiElementDeserializer());
        return builder.create();
    }
}
