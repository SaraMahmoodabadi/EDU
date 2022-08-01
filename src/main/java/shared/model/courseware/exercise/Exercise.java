package shared.model.courseware.exercise;

import shared.model.courseware.educationalMaterial.ItemType;

import java.util.List;

public class Exercise {

    private String exerciseCode;
    private String name;
    private String openingTime;
    private String closingTime;
    private String uploadingTimeWithoutDeductingScores;
    private String fileAddress;
    private String descriptions;
    private ItemType itemType;
    private List<Answer> answers;

    public Exercise() {}

    public Exercise(String courseCode, String name, String openingTime,
                    String closingTime, String uploadingTimeWithoutDeductingScores,
                    String fileAddress, String descriptions, ItemType itemType) {
        this.exerciseCode = this.generateCode(courseCode);
        this.name = name;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.uploadingTimeWithoutDeductingScores = uploadingTimeWithoutDeductingScores;
        this.fileAddress = fileAddress;
        this.descriptions = descriptions;
        this.itemType = itemType;
    }

    public String getExerciseCode() {
        return exerciseCode;
    }

    public void setExerciseCode(String exerciseCode) {
        this.exerciseCode = exerciseCode;
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
        return descriptions;
    }

    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
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

    private String generateCode(String courseCode) {
        return courseCode + "_" + this.name;
    }
}
