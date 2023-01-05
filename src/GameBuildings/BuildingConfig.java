package GameBuildings;

import GameConverter.JSONExclude;
import GameCore.GuiTypes;
import GameCore.UpdateDirections;
import GameItems.InventoryConfig;
import GameItems.ItemControllerTypes;
import GameItems.WorkConfig;

public class BuildingConfig {

    private final UpdateDirections updateDirection;
    private final GuiTypes guiType;
    private final int inputSlots;
    private final int outputSlots;
    private final String texturePath;
    private final String guiTexturePath;
    private final int guiTextureDimensions;
    private final InventoryConfig inventoryConfig;
    private final ItemControllerTypes itemControllerType;
    private final WorkConfig workConfig;
    private final boolean overwriteGuiImage;
    private final String buildingClass;
    @JSONExclude
    private String buildingId;

    public BuildingConfig(UpdateDirections updateDirection, GuiTypes guiType, int inputSlots, int outputSlots, String texturePath, String guiTexturePath, int guiTextureDimensions, InventoryConfig inventoryConfig, ItemControllerTypes controllerType, WorkConfig workConfig, boolean overwriteGuiImage, String buildingClass) {
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
    }

    @Override
    public String toString(){
        return String.format("In Slots: %s, Out Slots: %s, Image Path: %s", getInputSlots(), getOutputSlots(), getTexturePath());
    }

    public void setBuildingId(String buildingId) {
        this.buildingId = buildingId;
    }

    public String getBuildingId(){
        return buildingId;
    }

    public String getJavaClassPath() {
        return this.getBuildingClass().replaceFirst("[.][^.]+$", "").replaceAll("[/\\\\]", ".");
    }

    public UpdateDirections getUpdateDirection() {
        return updateDirection;
    }

    public GuiTypes getGuiType() {
        return guiType;
    }

    public int getInputSlots() {
        return inputSlots;
    }

    public int getOutputSlots() {
        return outputSlots;
    }

    public String getTexturePath() {
        return texturePath;
    }

    public String getGuiTexturePath() {
        return guiTexturePath;
    }

    public int getGuiTextureDimensions() {
        return guiTextureDimensions;
    }

    public InventoryConfig getInventoryConfig() {
        return inventoryConfig;
    }

    public ItemControllerTypes getItemControllerType() {
        return itemControllerType;
    }

    public WorkConfig getWorkConfig() {
        return workConfig;
    }

    public boolean isOverwriteGuiImage() {
        return overwriteGuiImage;
    }

    public String getBuildingClass() {
        return buildingClass;
    }
}
