package com.codecoy.barbar.firestorage_controller;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.codecoy.barbar.listneres.OnFileUploadListeners;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.List;


//FireStoreUploader used to upload the image in firebase storage
public class FireStoreUploader
{

    //uploadPhoto is used to upload image on the database it has two paramaters
    // 1 : Uri which is uri of the image needs to upload
    //2 : OnFileUploadListeners which is used to return the response to the calling class
    // 3: StorageReference which is reference to firebase storage where the image needs to upload
    public static void uploadPhoto(Uri uri, OnFileUploadListeners onFileUploadListeners, StorageReference storageReference) {

        //Upload the image on the firebase sorage and add on success listener
        storageReference.child(uri.getLastPathSegment()).putFile(uri).addOnSuccessListener(taskSnapshot -> onFileUploadListeners.onSuccess(taskSnapshot)).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override//onProgress method  gives the progress detail of data uploaded
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot)
            {
                //calling onProgress method in OnFileUploadListeners and passing the snapshot
                onFileUploadListeners.onProgress(snapshot);
            }

            //onFailure listener in case the firebase storage returns the error | then calling onFailure method and passing the error cause message
        }).addOnFailureListener(e -> onFileUploadListeners.onFailure(e.getMessage()));

    }

    //uploadPhoto is used to upload image on the database it has two paramaters
    // 1 : Uri which is uri of the image needs to upload
    //2 : OnFileUploadListeners which is used to return the response to the calling class
    // 3: StorageReference which is reference to firebase storage where the image needs to upload

    public static void uploadPhotos(List<Uri> uriList, OnFileUploadListeners onFileUploadListeners, StorageReference storageReference) {

        for (Uri uri : uriList) {

            //Upload the image on the firebase sorage and add on success listener
            storageReference.child(uri.getLastPathSegment()).putFile(uri).addOnSuccessListener(taskSnapshot -> onFileUploadListeners.onSuccess(taskSnapshot)).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override//onProgress method  gives the progress detail of data uploaded
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot)
                {
                    //calling onProgress method in OnFileUploadListeners and passing the snapshot
                    onFileUploadListeners.onProgress(snapshot);
                }

                //onFailure listener in case the firebase storage returns the error | then calling onFailure method and passing the error cause message
            }).addOnFailureListener(e -> onFileUploadListeners.onFailure(e.getMessage()));

        }
    }
}
