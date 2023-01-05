package Packages.Vanilla.Gui;

import GameCore.GuiElements.Interactable;
import GameCore.Main;
import GameInput.GameInputMain;
import GameUtils.Vec2i;

public class VanillaMainGuiInteract implements Interactable {
    @Override
    public void interact() {
        Vec2i curr = GameInputMain.getCursorGridPosition();
        System.out.println("test12341");
        Main.getClient().buildingCore().addBuilding(Main.getClient().buildingCore().getBuildingInstance("Vanilla:Mine", curr));
    }
}
