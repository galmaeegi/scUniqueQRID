package com.example.uniqueqridsystemsc;

public class ProfileItem {
    private String uniqueID;
    private String firstName;
    private String lastName;
    private String school;
    private String middleName;
    private String personalEmail;
    private String phoneNumber;
    private String registerDate;

    public ProfileItem(String uniqueID, String firstName, String lastName, String school, String middleName, String personalEmail, String phoneNumber, String registerDate) {
        this.uniqueID = uniqueID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.school = school;
        this.middleName = middleName;
        this.personalEmail = personalEmail;
        this.phoneNumber = phoneNumber;
        this.registerDate = registerDate;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getSchool() {
        return school;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getPersonalEmail() {
        return personalEmail;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getRegisterDate() {
        return registerDate;
    }
}
