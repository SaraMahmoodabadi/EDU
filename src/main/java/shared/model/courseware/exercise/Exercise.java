package shared.model.courseware.exercise;

import shared.model.courseware.educationalMaterial.ItemType;

import java.util.List;

public class Exercise {

    private String name;
    private String openingTime;
    private String closingTime;
    private String uploadingTimeWithoutDeductingScores;
    private String fileAddress;
    private String Descriptions;
    private ItemType itemType;
    private List<Answer> answers;

    public Exercise(String name, String openingTime, String closingTime,
                    String uploadingTimeWithoutDeductingScores,
                    String fileAddress, String descriptions, ItemType itemType) {
        this.name = name;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.uploadingTimeWithoutDeductingScores = uploadingTimeWithoutDeductingScores;
        this.fileAddress = fileAddress;
        Descriptions = descriptions;
        this.itemType = itemType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(String openingTime) {
        this.openingTime = openingTime;
    }

    public String getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(String closingTime) {
        this.closingTime = closingTime;
    }

    public String getUploadingTimeWithoutDeductingScores() {
        return uploadingTimeWithoutDeductingScores;
    }

    public void setUploadingTimeWithoutDeductingScores(String uploadingTimeWithoutDeductingScores) {
        this.uploadingTimeWithoutDeductingScores = uploadingTimeWithoutDeductingScores;
    }

    public String getFileAddress() {
        return fileAddress;
    }

    public void setFileAddress(String fileAddress) {
        this.fileAddress = fileAddress;
    }

    public String getDescriptions() {
        return Descriptions;
    }

    public void setDescriptions(String descriptions) {
        Descriptions = descriptions;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }
}
