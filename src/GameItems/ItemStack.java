package GameItems;

public class ItemStack {

    private Items itemType;
    private int itemCount;

    public ItemStack(Items type, int count){
        itemType = type;
        itemCount = count;
    }

    public Items getItemType() {
        return itemType;
    }

    public void setItemType(Items itemType) {
        this.itemType = itemType;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public void addItemCount(int add){
        this.itemCount += add;
    }

    public void removeItemCount(int remove) {
        this.itemCount -= remove;
    }
}
