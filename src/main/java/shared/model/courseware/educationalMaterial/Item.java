package shared.model.courseware.educationalMaterial;

public class Item {

    private String itemCode;
    private ItemType itemType;
    private String text;
    private String mediaFileAddress;

    public Item(ItemType itemType, String eduMaterialCode, int itemCount) {
        this.itemCode = this.generateCode(eduMaterialCode, itemCount);
        this.itemType = itemType;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getMediaFileAddress() {
        return mediaFileAddress;
    }

    public void setMediaFileAddress(String mediaFileAddress) {
        this.mediaFileAddress = mediaFileAddress;
    }

    private String generateCode(String eduMaterialCode, int itemCount) {
        return eduMaterialCode + "_" + (itemCount + 1);
    }
}
