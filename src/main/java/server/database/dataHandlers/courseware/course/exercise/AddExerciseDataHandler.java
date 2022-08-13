package server.database.dataHandlers.courseware.course.exercise;

import server.database.MySQLHandler;
import shared.model.courseware.exercise.Exercise;
import shared.util.config.Config;
import shared.util.config.ConfigType;

public class AddExerciseDataHandler {
    private final MySQLHandler databaseHandler;

    public AddExerciseDataHandler(MySQLHandler dataHandler) {
        this.databaseHandler = dataHandler;
    }

    public boolean addExerciseData(Exercise exercise) {
        String query = Config.getConfig(ConfigType.QUERY).getProperty(String.class, "updateData");
        query = String.format(query, "exercise", "openingTime = " + getStringFormat(exercise.getOpeningTime()) +
                ", closingTime = " + getStringFormat(exercise.getClosingTime()) +
                ", uploadingTimeWithoutDeductingScores = " + getStringFormat(exercise.getUploadingTimeWithoutDeductingScores()) +
                ", fileAddress = " + getStringFormat(exercise.getFileAddress()) +
                ", descriptions = " + getStringFormat(exercise.getDescriptions()) +
                ", itemType = " + getStringFormat(exercise.getItemType().toString())) +
                " exerciseCode = " + getStringFormat(exercise.getExerciseCode());
        return this.databaseHandler.updateData(query);
    }

    private String getStringFormat(String value) {
        return "'" + value + "'";
    }
}
