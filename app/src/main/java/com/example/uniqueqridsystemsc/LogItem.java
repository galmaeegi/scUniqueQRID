package com.example.uniqueqridsystemsc;

public class LogItem {
    private String uniqueID;
    private String firstName;
    private String lastName;
    private String school;
    private String checkInTime;

    public LogItem(String uniqueID, String firstName, String lastName, String school, String checkInTime) {
        this.uniqueID = uniqueID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.school = school;
        this.checkInTime = checkInTime;
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

    public String getCheckInTime() {
        return checkInTime;
    }
}
