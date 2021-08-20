package com.codecoy.barbar.database_controller;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

//DatabaseAddresses is used to create refernce to the database
public class DatabaseAddresses
{

     //getAllUsersCollection Returns reference to the collections of Users Data
    public static CollectionReference getAllUsersCollection() {
        //Returns reference to the collections of Users Data
        return FirebaseFirestore.getInstance().collection("Users");
    }


    //getSingleUser Returns reference to the Document  of Specific  User Data
    public static DocumentReference getSingleUser(String docId) {

        //Returning reference to the Document  of Specific  User Data
        return FirebaseFirestore.getInstance().collection("Users")
                .document(docId);
    }


    //getUserDates Returns reference to the Document  of Specific  Event Entry
    public static DocumentReference getUserDates(String user_id, String entery_id)
    {
        // Returning  reference to the Document  of Specific  Event Entry
        return FirebaseFirestore.getInstance().collection("Event").document(user_id).collection("enteries").document(entery_id);
    }


    //getUserEventCollection Returns reference to the Document  of All   Event Entry of A user
    public static DocumentReference getUserEventCollection(String user_id) {

        // Returns reference to the Document  of All   Event Entry of A user
        return FirebaseFirestore.getInstance().collection("Event").document(user_id);
    }
}
