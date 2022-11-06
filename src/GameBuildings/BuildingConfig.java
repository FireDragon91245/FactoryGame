package GameBuildings;

import GameCore.GuiTypes;
import GameCore.UpdateDirections;
import GameItems.InventoryConfig;
import GameItems.ItemControllerTypes;
import GameItems.WorkConfig;

public class BuildingConfig {

    public final UpdateDirections updateDirection;
    public final GuiTypes guiType;
    public final int inputSlots;
    public final int outputSlots;
    public final String texturePath;
    public final String guiTexturePath;
    public final int guiTextureDimensions;
    public final InventoryConfig inventoryConfig;
    public final ItemControllerTypes itemControllerType;
    public final WorkConfig workConfig;
    public final boolean overwriteGuiImage;
    public final String buildingClass;
    public final String buildingClassImport;
    private String buildingId;

    public BuildingConfig(UpdateDirections updateDirection, GuiTypes guiType, int inputSlots, int outputSlots, String texturePath, String guiTexturePath, int guiTextureDimensions, InventoryConfig inventoryConfig, ItemControllerTypes controllerType, WorkConfig workConfig, boolean overwriteGuiImage, String buildingClass, String buildingClassImport) {
        this.updateDirection = updateDirection;
        this.guiType = guiType;
        this.inputSlots = inputSlots;
        this.outputSlots = outputSlots;
        this.guiTexturePath = guiTexturePath;
        this.texturePath = texturePath;
        this.guiTextureDimensions = guiTextureDimensions;
        this.inventoryConfig = inventoryConfig;
        this.itemControllerType = controllerType;
        this.workConfig = workConfig;
        this.overwriteGuiImage = overwriteGuiImage;
        this.buildingClass = buildingClass;
        this.buildingClassImport = buildingClassImport;
    }

    @Override
    public String toString(){
        return String.format("In Slots: %s, Out Slots: %s, Image Path: %s", inputSlots, outputSlots, texturePath);
    }

    public void setBuildingId(String buildingId) {
        this.buildingId = buildingId;
    }

    public String getBuildingId(){
        return buildingId;
    }
}
