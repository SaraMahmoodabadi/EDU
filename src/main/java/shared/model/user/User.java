package shared.model.user;

public class User {
    private String fullName;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private long nationalCode;
    private long phoneNumber;
    private String imageAddress;
    private String collegeCode;
    private String lastLogin;
    private String thisLogin;
    private String username;
    private String password;
    private UserType userType;

    public User() {}

    public User(String firstName, String lastName, long nationalCode,
                String collegeCode, String username, String password,
                UserType userType, String email, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = firstName + " " + lastName;
        this.nationalCode = nationalCode;
        this.collegeCode = collegeCode;
        this.username = username;
        this.password = password;
        this.userType = userType;
        this.emailAddress = email;
        this.phoneNumber = Long.parseLong(phoneNumber);
    }

    public User(UserType userType, String collegeCode,
                String thisLogin, String userName, String password) {
        this.userType = userType;
        this.collegeCode = collegeCode;
        this.thisLogin = thisLogin;
        this.username = userName;
        this.password = password;
    }

    public User(String firstName, String lastName, String email, String lastLogin, String image) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = firstName + " " + lastName;
        this.emailAddress = email;
        this.lastLogin = lastLogin;
        this.imageAddress = image;
    }

    public User(String fullName, String collegeCode) {
        this.fullName = fullName;
        this.collegeCode = collegeCode;
    }

    public User(String firstname, String lastname, long nationalCode,
                String college, String email, long phone, String image) {
        this.firstName = firstname;
        this.lastName = lastname;
        this.fullName = firstname + " " + lastname;
        this.nationalCode = nationalCode;
        this.collegeCode = college;
        this.emailAddress = email;
        this.phoneNumber = phone;
        this.imageAddress = image;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public long getNationalCode() {
        return nationalCode;
    }

    public void setNationalCode(long nationalCode) {
        this.nationalCode = nationalCode;
    }

    public long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getImageAddress() {
        return imageAddress;
    }

    public void setImageAddress(String imageAddress) {
        this.imageAddress = imageAddress;
    }

    public String getCollegeCode() {
        return collegeCode;
    }

    public void setCollegeCode(String collegeCode) {
        this.collegeCode = collegeCode;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getThisLogin() {
        return thisLogin;
    }

    public void setThisLogin(String thisLogin) {
        this.thisLogin = thisLogin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}