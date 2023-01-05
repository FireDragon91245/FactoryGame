package GameItems;

import GameCore.Main;

import java.util.ArrayList;

public class Inventory {

    private static final ItemStack blocked = new ItemStack(Items.Blocked, 0);

    private final String bId;
    private final ArrayList<ItemStack> inputItems = new ArrayList<>();
    private final ArrayList<ItemStack> outputItems = new ArrayList<>();

    public ItemStack getInputSlot(int slot){
        if(slot >= inputItems.size() || slot < 0){
            return blocked;
        }else {
            return inputItems.get(slot);
        }
    }

    public ArrayList<ItemStack> getInputItems(){
        return inputItems;
    }

    public ArrayList<ItemStack> getOutputItems(){
        return outputItems;
    }

    public String getBuildingId(){
        return bId;
    }

    public void setInputSlot(int slot, ItemStack stack) {
        inputItems.set(slot, stack);
    }

    public ItemStack getOutputSlot(int slot){
        if(slot >= outputItems.size() || slot < 0) {
            return blocked;
        }else{
            return outputItems.get(slot);
        }
    }

    public void setOutputSlot(int slot, ItemStack stack) {
        outputItems.set(slot, stack);
    }

    public Inventory(String bId) {
        this.bId = bId;
        init();
    }

    private void init() {
        int slotsIn = Main.getClient().buildingCore().getBuildingConfig(bId).getInputSlots();
        int slotsOut = Main.getClient().buildingCore().getBuildingConfig(bId).getOutputSlots();
        for(int i = 0; i < slotsIn; i++){
            inputItems.add(i, new ItemStack(Items.None, 0));
        }
        for(int i = 0;i < slotsOut; i++){
            outputItems.add(i, new ItemStack(Items.None, 0));
        }
    }

    public boolean isInputEmpty() {
        int empty = 0;
        for(int i = 0; i < 6; i++){
            if(inputItems.get(i).getItemType() == Items.None || inputItems.get(i).getItemType() == Items.Blocked){
                empty++;
            }
        }
        return empty == 6;
    }

    public boolean isOutputEmpty() {
        int empty = 0;
        for(int i = 0; i < 6; i++){
            if(outputItems.get(i).getItemType() == Items.None || outputItems.get(i).getItemType() == Items.Blocked){
                empty++;
            }
        }
        return empty == 6;
    }
}
