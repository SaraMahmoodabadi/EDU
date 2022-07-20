package shared.model.courseware.educationalMaterial;

public class Item {

    private ItemType itemType;
    private String text;
    private String mediaFileAddress;

    public Item(ItemType itemType) {
        this.itemType = itemType;
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
}
