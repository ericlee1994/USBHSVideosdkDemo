package com.shgbit.android.heyshareuvc.bean;

import java.util.ArrayList;

/**
 * Created by Eric on 2017/12/8.
 */

public class Mqtt {
//    public String events;
    public String resource;
    public String sender;
    public ArrayList<String> receiver;
    public ArrayList<String> events;


    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public ArrayList<String> getReceiver() {
        return receiver;
    }

    public void setReceiver(ArrayList<String> receiver) {
        this.receiver = receiver;
    }

    public ArrayList<String> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<String> events) {
        this.events = events;
    }
}
