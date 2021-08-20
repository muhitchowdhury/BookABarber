package com.codecoy.barbar.database_controller;

import com.codecoy.barbar.dataModels.EventModel;
import com.codecoy.barbar.dataModels.SignUpModel;
import com.codecoy.barbar.listneres.OnTaskListeners;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;


//DatabaseUploader Class is used to upload the data on the database
public class DatabaseUploader
{
    // setUserRecord method is used to upload user profile data on the database
    public static void setUserRecord(SignUpModel userProfile, OnTaskListeners onTaskListeners) {

        //Calling getSingleUser Method in DatabaseAddresses class to get reference to the Users Database
        //then uploading the data on database and adding on complete listener
        DatabaseAddresses.getSingleUser(userProfile.getUser_id())
                .set(userProfile).addOnCompleteListener(task ->
        {
            //OnCompleteListener runs when the request to database gets completed

            //checking if the data is uploaded or not
            if (task.isSuccessful())  //if request is successfull
            {

                //call on onTaskSuccess method
                onTaskListeners.onTaskSuccess();
            }
            else  //if data not uploaded
                {
                    //Call onTaskFail method and pass the  message
                onTaskListeners.onTaskFail(task.getException().getMessage());
            }
        });
    }


    // setUserDate method is used to upload user Event data on the database
    public static void setUserDate(String user_id, EventModel eventModel, OnTaskListeners onTaskListeners) {


        //Calling getUserDates Method in DatabaseAddresses class to get reference to the Event Database
        //then uploading the data on database and adding on complete listener
        DatabaseAddresses.getUserDates(user_id, eventModel.getEvent_id())
                .set(eventModel, SetOptions.merge()).addOnCompleteListener(task -> {

            //OnCompleteListener runs when the request to database gets completed

            //checking if the data is uploaded or not
            if (task.isSuccessful()) //if request is successfull
            {
                //call on onTaskSuccess method
                onTaskListeners.onTaskSuccess();
            }
            else //if data not uploaded
                {

                    //Call onTaskFail method and pass the  message
                onTaskListeners.onTaskFail(task.getException().getMessage());
            }
        });
    }

    //updateUserToken used to update user token on the  database
    public static void updateUserToken(String token, String user_id, OnTaskListeners onTaskListeners) {

        //Creating hashmap object
        Map<String, Object> data = new HashMap<>();
        //putting token in the hashmap
        data.put("device_token", token);


        //Calling getSingleUser Method in DatabaseAddresses class to get reference to the Users Database
        //then uploading the data on database and adding on complete listener
        DatabaseAddresses.getSingleUser(user_id)
                .set(data, SetOptions.merge())
                .addOnCompleteListener(task -> {
                    //OnCompleteListener runs when the request to database gets completed

                    //checking if the data is uploaded or not
                    if (task.isSuccessful()) //if request is successfull
                    {
                        //call on onTaskSuccess method
                        onTaskListeners.onTaskSuccess();
                    }
                    else //if data not uploaded
                    {

                        //Call onTaskFail method and pass the  message
                        onTaskListeners.onTaskFail(task.getException().getMessage());
                    }
                });
    }

    // updateImages used to update image url of user profile
    public static void updateImages(String url, String user_id, OnTaskListeners onTaskListeners) {

        //Calling getSingleUser Method in DatabaseAddresses class to get reference to the Users Database
        //then uploading the data on database and adding on complete listener
        DatabaseAddresses.getSingleUser(user_id)
                .update("images_list", FieldValue.arrayUnion(url))
                .addOnCompleteListener(task -> {
                    //OnCompleteListener runs when the request to database gets completed

                    //checking if the data is uploaded or not
                    if (task.isSuccessful()) //if request is successfull
                    {
                        //call on onTaskSuccess method
                        onTaskListeners.onTaskSuccess();
                    }
                    else //if data not uploaded
                    {

                        //Call onTaskFail method and pass the  message
                        onTaskListeners.onTaskFail(task.getException().getMessage());
                    }
                });
    }
}
