package com.codecoy.barbar.Repository;

import android.util.Log;

import com.codecoy.barbar.dataModels.EventModel;
import com.codecoy.barbar.dataModels.SignUpModel;
import com.codecoy.barbar.database_controller.DatabaseAddresses;
import com.codecoy.barbar.listneres.OnBarbersLoadListener;
import com.codecoy.barbar.listneres.OnDatesLoadListener;
import com.codecoy.barbar.listneres.OnUserProfileLoadListeners;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;


//Repository Class to get data from database
public class Repository
{
    //Simple Class Name used to in printing logs
    private static final String TAG = Repository.class.getSimpleName();


    //getMyProfile method returns a user data paramaters are the user id and  OnUserProfileLoadListeners callback
    public static void getMyProfile(String userId, OnUserProfileLoadListeners onUserProfileLoadListeners) {


        /*
            Calling static getSingleUser method in DatabaseAddresses which returns us the reference to the
            firestore database then we are loading data
        */
        DatabaseAddresses.getSingleUser(userId).addSnapshotListener((snapshot, e) ->
        {

            if (e != null)  //Check id Exception is not null  means there is any error
            {
                //if any error
                Log.w("TAG", "Listen failed.", e); //show error on console log
                onUserProfileLoadListeners.onFailure(e.getMessage()); //call the onFailure method in OnUserProfileLoadListeners
                return;
            }

            if (snapshot.exists())  //Check if data exists
            {
                Log.d("TAG", "Current data: " + snapshot.getData()); //print data on console log

                //Call onUserProfileLoaded method and pass the data to it
                onUserProfileLoadListeners.onUserProfileLoaded(snapshot.toObject(SignUpModel.class));
            }
            else  //if data does not exists
                {
                    //print error message on the console
                Log.d("TAG", "Current data: null");
                //Call onEmpty method of  onUserProfileLoadListener
                onUserProfileLoadListeners.onEmpty("No Data Found");
            }
        });
    }

    //getBarbers method returns a list of all barbers stored in  database  paramaters are the   OnBarbersLoadListener callback
    public static void getBarbers(OnBarbersLoadListener onBarbersLoadListener)
    {


         /*
            Calling static getAllUsersCollection method in DatabaseAddresses which returns us the reference to the
            firestore database then we are loading data
        */
        DatabaseAddresses.getAllUsersCollection().get().addOnSuccessListener(queryDocumentSnapshots ->
        {
            //onSuccessListener runs when our reques is completed successfully


            //Check if Data Exists
            if (!queryDocumentSnapshots.isEmpty())
            {
                //Create a list of SignUpModel Class
                ArrayList<SignUpModel> list = new ArrayList<>();

                //Loop on every data to get all barbers
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                {
                    //Check if the id of barbar data matches the current user logged in id
                    //if matches it means the data is of current user
                    if (!documentSnapshot.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                    {
                        //put that in the list
                        list.add(documentSnapshot.toObject(SignUpModel.class));
                    }

                }
                //call onBarberLoad method and pass the data list
                onBarbersLoadListener.onBarberLoad(list);
            }
            else //if data does not exists
                {
                    //call onEmpty method
                onBarbersLoadListener.onEmpty("No Data Found");
            }

            //OnFailurelistener in case the request get failed | call the onFailure method and pass the failed message
        }).addOnFailureListener(e -> onBarbersLoadListener.onFailure(e.getLocalizedMessage()));
    }



    //getBarberEvent method returns a list of barber event  data paramaters are the user id and  OnDatesLoadListener callback
    public static void getBarberEvent(String user_id, OnDatesLoadListener onDatesLoadListener)
    {

      /*
            Calling static getUserEventCollection method in DatabaseAddresses which returns us the reference to the
            firestore database then we are referencing the database towards collection  'enteries'
            then adding data listener which gives us data
        */
        DatabaseAddresses.getUserEventCollection(user_id).collection("enteries")
                .get() //Get Data
                .addOnSuccessListener(queryDocumentSnapshots ->    //onSuccessListener runs when our reques is completed successfully
                {

                    //Check if Data Exists
                    if (!queryDocumentSnapshots.isEmpty())
                    {
                        //Create a list of EventModel Class
                        ArrayList<EventModel> list = new ArrayList<>();

                        //Loop on every data to get all barber Events
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots)
                        {
                            //Convert Data  to EventModel Object and put the data in the class
                            list.add(documentSnapshot.toObject(EventModel.class));
                        }
                        //Call onDatesLoad method and pass the event list
                        onDatesLoadListener.onDatesLoad(list);

                    } else //If data does not exist
                        {
                            //Call onEmpty method with "No Data Found" Message
                        onDatesLoadListener.onEmpty("No Data Found");
                    }
                })       //OnFailurelistener in case the request get failed | call the onFailure method and pass the failed message
                .addOnFailureListener(e -> onDatesLoadListener.onFailure(e.getLocalizedMessage()));
    }
}
