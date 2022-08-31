package GameCore.GuiElements;

import GameBuildings.Building;
import GameCore.GameGraphics;
import GameCore.GameGui;
import GameItems.Items;
import GameUtils.GameUtils;

import java.awt.*;

@SuppressWarnings({"record", "unused"})
public final class InputItemTexture implements DynamicGuiElement {

    public final int slot;
    public final int startX;
    public final int startY;
    public final int width;
    public final int height;
    public final boolean cacheFontMetrics;
    public final int textOffsetX;
    public final int textOffsetY;

    private int charCount = 0;

    private int textStartX = 0;
    private int textStartY = 0;

    public InputItemTexture(int slot, int startX, int startY, int width, int height, boolean cacheFontMetrics, int textOffsetX, int textOffsetY) {
        this.slot = slot;
        this.startX = startX;
        this.startY = startY;
        this.width = width;
        this.height = height;
        this.cacheFontMetrics = cacheFontMetrics;
        this.textOffsetX = textOffsetX;
        this.textOffsetY = textOffsetY;
    }

    @Override
    public void drawDynamic(Graphics2D g2, Building b, int x, int y) {
        if(b.getInventory().getInputSlot(slot).getItemType() != Items.None && b.getInventory().getInputSlot(slot).getItemType() != Items.Blocked){
            g2.drawImage(GameGui.getItemTexture(b.getInventory().getInputSlot(slot).getItemType()), x + startX, y + startY, width, height, GameGraphics.getMainObserver());
            g2.setFont(GameGui.getNumberedFont());
            if (cacheFontMetrics) {
                if (charCount != GameUtils.GetCharCountOfInt(b.getInventory().getInputSlot(slot).getItemCount())) {
                    charCount = GameUtils.GetCharCountOfInt(b.getInventory().getInputSlot(slot).getItemCount());

                    textStartX = startX + width - g2.getFontMetrics(GameGui.getNumberedFont()).stringWidth("0".repeat(charCount)) + textOffsetX;
                    textStartY = startY + height + textOffsetY;
                }
                g2.drawString(String.valueOf(b.getInventory().getInputSlot(slot).getItemCount()), x + textStartX, y + textStartY);
            } else {
                g2.drawString(String.valueOf(b.getInventory().getInputSlot(slot).getItemCount()), x + startX + width - g2.getFontMetrics(GameGui.getNumberedFont()).stringWidth("0".repeat(charCount)) + textOffsetX, y + startY + height + textOffsetY);
            }
        }
    }
}
