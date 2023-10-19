package com.example.uniqueqridsystemsc;

public class CheckItem {
    private String uniqueID;
    private String firstName;
    private String lastName;
    private String school;
    private String date; // Date in the format "October 14, 2023"
    private String timedIn;
    private String timedOut;

    public CheckItem(String uniqueID, String firstName, String lastName, String school, String date, String timedIn, String timedOut) {
        this.uniqueID = uniqueID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.school = school;
        this.date = date;
        this.timedIn = timedIn;
        this.timedOut = timedOut;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setDate(String date) {
        this.date = date;
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

    public String getDate() {
        return date;
    }

    public String getTimedIn() {
        return timedIn;
    }

    public String getTimedOut() {
        return timedOut;
    }
}


