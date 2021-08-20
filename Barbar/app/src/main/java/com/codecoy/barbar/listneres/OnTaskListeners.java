package com.codecoy.barbar.listneres;


//Inteface OnTaskListeners is used to check the tas is successfull or not
public interface OnTaskListeners {

    //onTaskSuccess runs when task is loaded successfully
    void onTaskSuccess();


    //onFailure method is used when there is some error when loading the data
    void onTaskFail(String e);
}
