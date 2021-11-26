package com.example.habittracker.models;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Objects;

public class Request {
    private String userId;
    private String status;
    private String userName;

    // If enum doesn't works, use string with following constants
    private static final String pending = "Pending";
    private static final String accepted = "Accepted";
    private static final String refused = "Refused";

    public Request(String userId, String status, String username) {}

    public Request() {}

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) throws Exception {
        if (status.equals(pending) || status.equals(accepted) || status.equals(refused)) {
            this.status = status;
        } else {
            throw new Exception("Status must be one of 'Pending', 'Accepted', or 'Refused'.");
        }
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Override the equal operation for better comparison
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Request that = (Request) o;
        return that.getUserId().equals(this.userId) && that.getUserName().equals(this.userName)
                && that.getStatus().equals(this.status);
    }

    /**
     * Generate a unique number identifier
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int hashCode() {
        return Objects.hash(this);
    }

}

