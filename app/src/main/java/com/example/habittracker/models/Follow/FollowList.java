package com.example.habittracker.models.Follow;



import java.io.Serializable;
import java.util.ArrayList;

public class FollowList implements Serializable {

    private final ArrayList<Follow> followList;
    private final String type;

    public FollowList(ArrayList<Follow> followList, String type) throws Exception {
        this.followList = followList;
        if (!type.equals("followers") && !type.equals("following")) {
            throw new Exception("Type must be either 'followers' or 'following'.");
        }
        this.type = type;
    }

    public FollowList(String type) {
        this.followList = new ArrayList<>();
        this.type = type;
    }

    public void addFollow(Follow follow) {
        followList.add(follow);
    }

    public void deleteFollow(Follow follow) {
        followList.remove(follow);
    }

    public Follow getFollow(int index) {
        return followList.get(index);
    }

    public Follow getFollow(String uid) throws Exception {
        for (Follow follow : followList) {
            if (follow.getUid().equals(uid)) {
                return follow;
            }
        }
        throw new Exception("Follower does not exist");
    }


    public String getType() {
        return type;
    }

    public void clearFollowList() {
        this.followList.clear();
    }

    public Boolean hasFollow(Follow follow) {
        return followList.contains(follow);
    }

    public ArrayList<Follow> getFollowList() {
        return followList;
    }

    public ArrayList<String> getFollowUids() {
        ArrayList<String> list = new ArrayList<String>();
        for (Follow follow : followList) {
            list.add(follow.getUid());
        }
        return list;
    }
}
