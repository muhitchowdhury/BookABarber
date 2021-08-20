package com.codecoy.barbar.listneres;

import com.google.firebase.storage.UploadTask;




//Inteface OnDatesLoadListener is used to load snapshot of  data it has 3 abstract methods
public interface OnFileUploadListeners {



    //onSuccess is used to get on taskSnapshot object
    void onSuccess(UploadTask.TaskSnapshot taskSnapshot);


    //onProgress method is used when the data load is in progress
    void onProgress(UploadTask.TaskSnapshot taskSnapshot);


    //onFailure method is used when there is some error when loading the data
    void onFailure(String e);
}
