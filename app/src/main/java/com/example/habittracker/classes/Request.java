package com.example.habittracker.classes;

public class Request {
    private String userId;
    private Status status;
    private String userName;

    // If enum doesn't works, use string with following constants
//    private static final String pending = "Pending";
//    private static final String accepted = "Accepted";
//    private static final String refused = "Refused";

    enum Status{
        PENDING,
        ACCEPTED,
        REFUSED
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
