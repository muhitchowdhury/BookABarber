package com.codecoy.barbar.listneres;

import com.codecoy.barbar.dataModels.SignUpModel;

import java.util.ArrayList;



//Inteface OnBarbersLoadListener is used to load data about barber data it has 3 abstract methods
public interface OnBarbersLoadListener {

    //onBarberLoad is used to load the list of barbers data
    void onBarberLoad(ArrayList<SignUpModel> list);

    //onEmpty method is used when there is no data in the database
    void onEmpty(String message);

    //onFailure method is used when there is some error when loading the data
    void onFailure(String e);
}
