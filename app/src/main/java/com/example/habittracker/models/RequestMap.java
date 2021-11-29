package com.example.habittracker.models;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RequestMap {

    private Map<String, ArrayList<Request>> requestMap;
    private ArrayList<Request> outgoingList;
    private ArrayList<Request> incomingList;

    public RequestMap(ArrayList<Request> incoming, ArrayList<Request> outgoing) {
        this.requestMap = new HashMap<>();
        incomingList = incoming;
        outgoingList = outgoing;
        this.requestMap.put("incoming", incoming);
        this.requestMap.put("outgoing", outgoing);
    }

    public RequestMap() {
        this.requestMap = new HashMap<>();
        this.incomingList = new ArrayList<>();
        this.outgoingList = new ArrayList<>();
    }

    public void addRequest(String type, Request request) throws Exception {
        if (type.equals("incoming")) {
            if (incomingList.contains(request)) throw new Exception("Incoming request already exists");
            incomingList.add(request);
        } else if (type.equals("outgoing")) {
            if (outgoingList.contains(request)) throw new Exception("Outgoing request already exists");
            outgoingList.add(request);
        } else {
            throw new Exception("type must be either incoming or outgoing");
        }
    }

    public void deleteRequest(String type, Request request) throws Exception {
        if (type.equals("incoming")) {
            if (!incomingList.contains(request)) throw new Exception("Incoming request does not exist");
            incomingList.remove(request);
        } else if (type.equals("outgoing")) {
            if (!outgoingList.contains(request)) throw new Exception("Outgoing request does not exist");
            outgoingList.remove(request);
        } else {
            throw new Exception("type must be either incoming or outgoing");
        }
    }

    public Request getRequest(String type, int index) throws Exception {
        if (type.equals("incoming")) {
            return incomingList.get(index);
        } else if (type.equals("outgoing")) {
            return outgoingList.get(index);
        } else {
            throw new Exception("type must be either incoming or outgoing");
        }
    }

    public Request getRequest(String type, String uid) throws Exception {
        ArrayList<Request> listSearch;
        if (type.equals("incoming")) {
            listSearch = incomingList;
        } else if (type.equals("outgoing")) {
            listSearch = outgoingList;
        } else {
            throw new Exception("type must be either incoming or outgoing");
        }

        for (Request request : listSearch) {
            if (request.getUserId().equals(uid)) {
                return request;
            }
        }
        throw new Exception("Request does not exist");
    }

    public void clearRequestList(String type) throws Exception {
        if (type.equals("incoming")) {
            incomingList.clear();
        } else if (type.equals("outgoing")) {
            outgoingList.clear();
        } else {
            throw new Exception("type must be either incoming or outgoing");
        }
    }

    public void clearRequestList() {
        incomingList.clear();
        outgoingList.clear();
    }

    public Boolean hasRequest(String type, Request request) throws Exception {
        if (type.equals("incoming")) {
            return incomingList.contains(request);
        } else if (type.equals("outgoing")) {
            return outgoingList.contains(request);
        } else {
            throw new Exception("type must be either incoming or outgoing");
        }
    }

    public ArrayList<Request> getRequestList(String type) throws Exception {
        if (type.equals("incoming")) {
            return incomingList;
        } else if (type.equals("outgoing")) {
            return outgoingList;
        } else {
            throw new Exception("type must be either incoming or outgoing");
        }
    }


}
