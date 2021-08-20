package com.codecoy.barbar.dataModels;


//EventModel Class used to store data about Barber Events
public class EventModel
{

    private String event_id; //String Variable To Store  Event Id
    private String date; //String Variable To Store Event Date
    private String note; //String Variable To Store Note About Event

    public EventModel(){ } //Empty Constructor

    //EvenModel Constructor used to assign values
    public EventModel(String event_id, String date, String note)
    {
        this.event_id = event_id; //Assigning  Event id
        this.date = date; //Assigning Date
        this.note = note; //Assigning Not
    }


    //GETTER AND SETTER METHODS



    //Get Method to get  Event ID
    public String getEvent_id()
    {
        return event_id;
    }


    //Set  Method to Assign   EventID
    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }


    //Get Method to get Date
    public String getDate() {
        return date;
    }


    //Set  Method to Assign  Date
    public void setDate(String date) {
        this.date = date;
    }


    //Get Method to get  Note
    public String getNote() {
        return note;
    }


    //Set  Method to Assign  Note
    public void setNote(String note) {
        this.note = note;
    }
}
