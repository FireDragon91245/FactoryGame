package GameCore;

import GameBuildings.Building;
import GameBuildings.Buildings;
import GameItems.Items;
import GameUtils.Vec2i;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class GameGui {

    private final Font numberFont = new Font("Century", Font.PLAIN, 20);
    private final HashMap<Items, BufferedImage> itemTextures = new HashMap<>();
    private final HashMap<Buildings, HashMap<Integer, BufferedImage>> mappedAnimationFrames = new HashMap<>();
    private final HashMap<GuiTypes, GuiBuilder> guiBuilders = new HashMap<>();
    private final HashMap<GuiTypes, Gui> guis = new HashMap<>();

    public void RenderBuildingInventoryGui(Graphics g, Vec2i buildingPos, Building building){
        int pixelPosX = buildingPos.x * 10;
        if(pixelPosX > Main.getClient().windowWidth() / 2){
            guis.get(Main.getClient().buildingCore().getBuildingConfig(building.getType()).guiType).render(pixelPosX - 400, buildingPos.y * 5, g, building);
        }else{
            guis.get(Main.getClient().buildingCore().getBuildingConfig(building.getType()).guiType).render(pixelPosX + 10, buildingPos.y * 5, g, building);
        }
    }

    public Font getNumberedFont(){
        return numberFont;
    }

    public HashMap<GuiTypes, GuiBuilder> getGuiBuilders() {
        return guiBuilders;
    }
    public void addGuiBuilder(GuiTypes type, GuiBuilder builder){
        guiBuilders.put(type, builder);
    }

    private void drawBuildingGuiTextureFrom(int startX, int startY, Building b, Graphics g) {
        if(!Main.getClient().buildingCore().getBuildingConfig(b.getType()).overwriteGuiImage) {
            g.drawImage(Main.getClient().clientGraphics().getBuildingGuiImage(b.getType()), startX, startY, Main.getClient().buildingCore().getBuildingConfig(b.getType()).guiTextureDimensions, Main.getClient().buildingCore().getBuildingConfig(b.getType()).guiTextureDimensions, Main.getClient().clientGraphics().getMainObserver());
        }else{
            g.drawImage(mappedAnimationFrames.get(b.getType()).get(b.getWorkingProgress()), startX, startY, Main.getClient().buildingCore().getBuildingConfig(b.getType()).guiTextureDimensions, Main.getClient().buildingCore().getBuildingConfig(b.getType()).guiTextureDimensions, Main.getClient().clientGraphics().getMainObserver());
        }
    }

    public BufferedImage getItemTexture(Items item){
        return itemTextures.get(item);
    }

    public void addItemTexture(Items item, BufferedImage texture) {
        itemTextures.put(item, texture);
    }

    public void registerGuiAnimationForBuilding(HashMap<Integer, BufferedImage> mapAnimationFrames, Buildings b) {
        mappedAnimationFrames.put(b, mapAnimationFrames);
    }

    public void addGui(GuiTypes type, Gui gui) {
        guis.put(type, gui);
    }

    public void sendMouseEventAll(MouseEvent event){
        for(Gui gui : guis.values()){
            gui.sendMouseEvent(event);
        }
    }

    public Gui getGui(GuiTypes type) {
        return guis.getOrDefault(type, null);
    }

    public boolean hasGui(GuiTypes type) {
        return guis.containsKey(type);
    }

    public void sendMouseMoveEventAll(MouseEvent event) {
        for(Gui gui : guis.values()){
            gui.sendMouseMoveEvent(event);
        }
    }
}
