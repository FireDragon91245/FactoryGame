package GameCore;

import GameBuildings.Building;
import GameBuildings.BuildingCore;
import GameBuildings.Buildings;
import GameItems.Items;
import GameUtils.Vec2i;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class GameGui {

    private static final Font numberFont = new Font("Century", Font.PLAIN, 20);
    private static final HashMap<Items, BufferedImage> itemTextures = new HashMap<>();
    private static final HashMap<Buildings, HashMap<Integer, BufferedImage>> mappedAnimationFrames = new HashMap<>();
    private static final HashMap<GuiTypes, GuiBuilder> guiBuilders = new HashMap<>();
    private static final HashMap<GuiTypes, Gui> guis = new HashMap<>();

    public static void RenderBuildingInventoryGui(Graphics g, Vec2i buildingPos, Building building){
        int pixelPosX = buildingPos.x * 10;
        if(pixelPosX > GameCore.windowWidth() / 2){
            guis.get(BuildingCore.getBuildingConfig(building.getType()).guiType).render(pixelPosX - 400, buildingPos.y * 5, g, building);
        }else{
            guis.get(BuildingCore.getBuildingConfig(building.getType()).guiType).render(pixelPosX + 10, buildingPos.y * 5, g, building);
        }
    }

    public static Font getNumberedFont(){
        return numberFont;
    }

    public static HashMap<GuiTypes, GuiBuilder> getGuiBuilders() {
        return guiBuilders;
    }
    public static void addGuiBuilder(GuiTypes type, GuiBuilder builder){
        guiBuilders.put(type, builder);
    }

    private static void drawBuildingGuiTextureFrom(int startX, int startY, Building b, Graphics g) {
        if(!BuildingCore.getBuildingConfig(b.getType()).overwriteGuiImage) {
            g.drawImage(GameGraphics.getBuildingGuiImage(b.getType()), startX, startY, BuildingCore.getBuildingConfig(b.getType()).guiTextureDimensions, BuildingCore.getBuildingConfig(b.getType()).guiTextureDimensions, GameGraphics.getMainObserver());
        }else{
            g.drawImage(mappedAnimationFrames.get(b.getType()).get(b.getWorkingProgress()), startX, startY, BuildingCore.getBuildingConfig(b.getType()).guiTextureDimensions, BuildingCore.getBuildingConfig(b.getType()).guiTextureDimensions, GameGraphics.getMainObserver());
        }
    }

    public static BufferedImage getItemTexture(Items item){
        return itemTextures.get(item);
    }

    public static void addItemTexture(Items item, BufferedImage texture) {
        itemTextures.put(item, texture);
    }

    public static void registerGuiAnimationForBuilding(HashMap<Integer, BufferedImage> mapAnimationFrames, Buildings b) {
        mappedAnimationFrames.put(b, mapAnimationFrames);
    }

    public static void addGui(GuiTypes type, Gui gui) {
        guis.put(type, gui);
    }

    public static void sendMouseEventAll(MouseEvent event){
        for(Gui gui : guis.values()){
            gui.sendMouseEvent(event);
        }
    }

    public static Gui getGui(GuiTypes type) {
        return guis.getOrDefault(type, null);
    }

    public static boolean hasGui(GuiTypes type) {
        return guis.containsKey(type);
    }

    public static void sendMouseMoveEventAll(MouseEvent event) {
        for(Gui gui : guis.values()){
            gui.sendMouseMoveEvent(event);
        }
    }
}
