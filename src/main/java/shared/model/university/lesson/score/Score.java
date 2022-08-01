package shared.model.university.lesson.score;

public class Score {

    private String lessonCode;
    private int lessonGroup;
    private String studentCode;
    private String professorCode;
    private String score;
    private ScoreType type;
    private String protest;
    private String protestAnswer;

    public Score() {}

    public Score(String lessonCode, int lessonGroup, String studentCode,
                 String professorCode , String score, ScoreType type) {
        this.lessonCode = lessonCode;
        this.lessonGroup = lessonGroup;
        this.studentCode = studentCode;
        this.professorCode = professorCode;
        this.score = score;
        this.type = type;
    }

    public Score(String lessonCode, String studentCode, String score,
                 String protest, String protestAnswer) {
        this.lessonCode = lessonCode;
        this.studentCode = studentCode;
        this.score = score;
        this.protest = protest;
        this.protestAnswer = protestAnswer;
    }

    public Score(String lessonCode, String studentCode, String score) {
        this.lessonCode = lessonCode;
        this.studentCode = studentCode;
        this.score = score;
    }

    public Score(String lessonCode, String studentCode, String professorCode,
                 String score, String protest, String protestAnswer) {
        this.lessonCode = lessonCode;
        this.studentCode = studentCode;
        this.professorCode = professorCode;
        this.score = score;
        this.protest = protest;
        this.protestAnswer = protestAnswer;
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

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
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
