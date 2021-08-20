package com.codecoy.barbar.listneres;

import com.codecoy.barbar.dataModels.SignUpModel;


//Inteface OnUserProfileLoadListeners is used to load data about UserProfile  data it has 3 abstract methods
public interface OnUserProfileLoadListeners {

    //onUserProfileLoaded is used to load the data of SignUpModel
    void onUserProfileLoaded(SignUpModel userProfile);


    //onFailure method is used when there is some error when loading the data
    void onFailure(String e);

    //onEmpty method is used when there is no data in the database
    void onEmpty(String message);
}
