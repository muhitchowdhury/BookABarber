package com.codecoy.barbar.listneres;

import com.codecoy.barbar.dataModels.EventModel;

import java.util.ArrayList;



//Inteface OnDatesLoadListener is used to load the list of EventModel data  it has 3 abstract methods
public interface OnDatesLoadListener {


    //onDatesLoad is used to load the list of barberEvent data
    void onDatesLoad(ArrayList<EventModel> list);


    //onEmpty method is used when there is no data in the database
    void onEmpty(String message);


    //onFailure method is used when there is some error when loading the data
    void onFailure(String e);
}
