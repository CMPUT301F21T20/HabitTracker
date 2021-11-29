package com.example.habittracker.models.Follow;

import java.util.Date;

public class Follow {

    private final String uid;
    private final String username;
    private final Date since;

    public Follow(String uid, String username, Date since) {
        this.uid = uid;
        this.username = username;
        this.since = since;
    }

    public String getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public Date getSince() {
        return since;
    }
}
