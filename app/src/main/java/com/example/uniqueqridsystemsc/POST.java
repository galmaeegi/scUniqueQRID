package com.example.uniqueqridsystemsc;

public class POST {
    String firstName;
    String lastName;
    String middleName;
    String personalEmail;
    String school;
    String RegisterDate;
    String phoneNumber;
    public POST(String firstName, String lastName, String middleName, String personalEmail, String school, String RegisterDate, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.personalEmail = personalEmail;
        this.school = school;
        this.RegisterDate = RegisterDate;
        this.phoneNumber = phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMiddleName() {
        return middleName;
    }
    public String getPersonalEmail() {
        return personalEmail;
    }
    public String getSchool() {
        return school;
    }
    public String getRegisterDate() {
        return RegisterDate;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
}
