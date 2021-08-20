package com.codecoy.barbar.firestorage_controller;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


//FireStorageAddresses is used to create refernce to the database
public class FireStorageAddresses {
    //getUsersProfiles method returns the reference to users profile data
    public static StorageReference getUsersProfiles()
    {

        //Creating FirebaseStorage Reference  then getting Reference to the users -> Profiles
        return FirebaseStorage.getInstance().getReference("Users").child("Profiles");
    }
}
