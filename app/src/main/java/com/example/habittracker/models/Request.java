package com.example.habittracker.models;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Request {
    private String userId;
    private String status;
    private String userName;
    private Date createdDate;

    // If enum doesn't works, use string with following constants
    private static final String pending = "Pending";
    private static final String accepted = "Accepted";
    private static final String refused = "Refused";

    public Request(String userId, String status, String username, Date createdDate) {
        this.userId = userId;
        this.status = status;
        this.userName = username;
        this.createdDate = createdDate;
    }

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

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date date) {
        this.createdDate = date;
    }

    public Map<String, Object> getRequestMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("status", this.status);
        map.put("username", this.userName);
        map.put("createdDate", this.createdDate);
        return map;
    }

    public Request cloneRequest() {
        return new Request(this.userId, this.status, this.userName, this.createdDate);
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
                && that.getStatus().equals(this.status) && that.getCreatedDate().equals(this.createdDate);
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

