package com.codecoy.barbar.dataModels;

import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.util.ArrayList;



//SignUpModel Class used to store data about UserData Also its implements Serializable which means we can pass this class in Intent As Extra
public class SignUpModel implements Serializable {


    private String device_token; //String Variable To Store  Device Token
    private String user_image; //String Variable To Store  User Image
    private String user_id; //String Variable To Store  User Id
    private String user_first_name;  //String Variable To Store First Name
    private String user_last_name; //String Variable To Store Last Name
    private String user_email; //String Variable To Store Email
    private String user_password; //String Variable To Store PAssword
    private String user_contact_number; //String Variable To Store Contact Number
    private String user_address; //String Variable To Store Address
    private String user_as; //String Variable To Store User Type wheather  he is customer or barber
    private GeoPoint user_geopoint; //Geopoint to store user location
    private ArrayList<String> images_list; //String Array List   To Store User Images

    //Empty Constructor
    public SignUpModel() {
    }


    //SignUpModel Constructor To store assign values
    public SignUpModel(String device_token, String user_image, String user_id, String user_first_name, String user_last_name, String user_email, String user_password, String user_contact_number, String user_address, String user_as, GeoPoint user_geopoint, ArrayList<String> images_list) {
        this.device_token = device_token;
        this.user_image = user_image;
        this.user_id = user_id;
        this.user_first_name = user_first_name;
        this.user_last_name = user_last_name;
        this.user_email = user_email;
        this.user_password = user_password;
        this.user_contact_number = user_contact_number;
        this.user_address = user_address;
        this.user_as = user_as;
        this.user_geopoint = user_geopoint;
        this.images_list = images_list;
    }



    //GETTER AND SETTER METHODS


    //Get Method to get  DeviceToken
    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }


    //Get Method to get link to  userImage
    public String getUser_image() {
        return user_image;
    }

    public void setUser_image(String user_image) {
        this.user_image = user_image;
    }


    //Get Method to get UserId
    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }


    //Get Method to get  User First Name
    public String getUser_first_name() {
        return user_first_name;
    }


    //Set Method to set
    public void setUser_first_name(String user_first_name) {
        this.user_first_name = user_first_name;
    }


    //Get Method to get  User Last Name
    public String getUser_last_name() {
        return user_last_name;
    }

    //Set Method to set User Last Name
    public void setUser_last_name(String user_last_name) {
        this.user_last_name = user_last_name;
    }


    //Get Method to get  User EMail
    public String getUser_email() {
        return user_email;
    }

    //Set Method to set User EMail
    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    //Get Method to get  User Password
    public String getUser_password() {
        return user_password;
    }

    //Set Method to set User Password
    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }


    //Get Method to get  User Contact Number
    public String getUser_contact_number() {
        return user_contact_number;
    }

    //Set Method to set User Contact Number
    public void setUser_contact_number(String user_contact_number) {
        this.user_contact_number = user_contact_number;
    }


    //Get Method to get  User Address
    public String getUser_address() {
        return user_address;
    }

    //Set Method to set  User Address
    public void setUser_address(String user_address) {
        this.user_address = user_address;
    }


    //Get Method to get  User Type
    public String getUser_as() {
        return user_as;
    }

    //Set Method to set User Type
    public void setUser_as(String user_as) {
        this.user_as = user_as;
    }


    //Get Method to get  User GeoPoint which is then  used to get user  location
    public GeoPoint getUser_geopoint() {
        return user_geopoint;
    }

    //Set Method to set User GeoPoint which is then  used to get user  location
    public void setUser_geopoint(GeoPoint user_geopoint) {
        this.user_geopoint = user_geopoint;
    }


    //Get Method to get user images
    public ArrayList<String> getImages_list() {
        return images_list;
    }

    //Set Method to set  user images
    public void setImages_list(ArrayList<String> images_list) {
        this.images_list = images_list;
    }
}
