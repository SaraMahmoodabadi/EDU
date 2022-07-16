package server.logic.model.university.lesson.score;

public class Score {

    private final String lessonCode;
    private final int lessonGroup;
    private final String studentCode;
    private final String professorCode;
    private double score;
    private ScoreType type;
    private String protest;
    private String protestAnswer;

    public Score(String lessonCode, int lessonGroup, String studentCode,
                 String professorCode ,double score, ScoreType type) {
        this.lessonCode = lessonCode;
        this.lessonGroup = lessonGroup;
        this.studentCode = studentCode;
        this.professorCode = professorCode;
        this.score = score;
        this.type = type;
    }

    public String getLessonCode() {
        return lessonCode;
    }

    public int getLessonGroup() {
        return lessonGroup;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public String getProfessorCode() {
        return professorCode;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public ScoreType getType() {
        return type;
    }

    public void setType(ScoreType type) {
        this.type = type;
    }

    public String getProtest() {
        return protest;
    }

    public void setProtest(String protest) {
        this.protest = protest;
    }

    public String getProtestAnswer() {
        return protestAnswer;
    }

    public void setProtestAnswer(String protestAnswer) {
        this.protestAnswer = protestAnswer;
    }
}
