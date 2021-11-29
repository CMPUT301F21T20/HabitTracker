package com.example.habittracker.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * This model represents all the incoming and outgoing requests for a single user. The model is a
 * mapping with two keys: 1. 'incoming' and 2. 'outgoing'. Each of these mapping keys are associated
 * with an ArrayList of Request objects. This model facilitates interacting with a users requests.
 */
public class RequestMap {

    private Map<String, ArrayList<Request>> requestMap;
    private final ArrayList<Request> outgoingList;
    private final ArrayList<Request> incomingList;

    public RequestMap() {
        this.requestMap = new HashMap<>();
        this.incomingList = new ArrayList<>();
        this.outgoingList = new ArrayList<>();
    }

    /**
     * This function adds a Request to the incoming or outgoing lists. This function throws if the
     * type is not one of 'incoming' or 'outgoing'
     *
     * @param type    The type of request which can be either 'incoming' or 'outgoing'
     * @param request The Request object to add to the respective list
     * @throws Exception
     */
    public void addRequest(String type, Request request) throws Exception {
        if (type.equals("incoming")) {
            if (incomingList.contains(request))
                throw new Exception("Incoming request already exists");
            incomingList.add(request);
        } else if (type.equals("outgoing")) {
            if (outgoingList.contains(request))
                throw new Exception("Outgoing request already exists");
            outgoingList.add(request);
        } else {
            throw new Exception("type must be either incoming or outgoing");
        }
    }

    /**
     * This function deletes a Request from the incoming or outgoing lists. This function throws if
     * the type is not one of 'incoming' or 'outgoing'
     *
     * @param type    The type of request which can be either 'incoming' or 'outgoing'
     * @param request The Request object to delete from the respective list
     * @throws Exception
     */
    public void deleteRequest(String type, Request request) throws Exception {
        if (type.equals("incoming")) {
            if (!incomingList.contains(request))
                throw new Exception("Incoming request does not exist");
            incomingList.remove(request);
        } else if (type.equals("outgoing")) {
            if (!outgoingList.contains(request))
                throw new Exception("Outgoing request does not exist");
            outgoingList.remove(request);
        } else {
            throw new Exception("type must be either incoming or outgoing");
        }
    }

    /**
     * This function finds and returns a request at a specific index in a specific list. This
     * function throws if the index is out of bouds.
     *
     * @param type  The type of request which can be either 'incoming' or 'outgoing'
     * @param index The integer integer at which the request can be found
     * @return The Request object at index in list type.
     * @throws Exception
     */
    public Request getRequest(String type, int index) throws Exception {
        if (type.equals("incoming")) {
            return incomingList.get(index);
        } else if (type.equals("outgoing")) {
            return outgoingList.get(index);
        } else {
            throw new Exception("type must be either incoming or outgoing");
        }
    }

    /**
     * This function clears both request lists.
     */
    public void clearRequestList() {
        incomingList.clear();
        outgoingList.clear();
    }

    /**
     * This function returns a specific request list.
     *
     * @param type The type of request list which can be either 'incoming' or 'outgoing'
     * @return An ArrayList of Request objects of type [type]
     * @throws Exception
     */
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
