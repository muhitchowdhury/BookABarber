package com.codecoy.barbar.projectUtils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPref //SharedPref Class used to store the data locally
{


    //Instance of SharedPref class  which is static
    private static SharedPref instance;
    //Variable to Store PREFERENCE Name
    public static String PREFERENCE = "IceCompany";
    private Context ctx; //instance of Context class
    private SharedPreferences sharedPreferences; //Instance  of SharedPreferences Class which we can use to insert and get data


    //Variable to store key name user
    //SharedPreferences store data in form of key:Value pair so this variable is key to store user data
    private String USER = "user";


    //Constructor of this class which has one paramater which is context of calling class
    public SharedPref(Context context)
    {
        this.ctx = context; //Assigning context to ctx variable

        //Creating sharefpreference and storing reference in sharedPreferences variable
        this.sharedPreferences = context.getSharedPreferences(PREFERENCE, 0);
    }


    //getInstance method returns reference to SharedPref object
    public static SharedPref getInstance(Context context)
    {
        if (instance == null) //check if instance is already created or not
        {
            //if not then create new one
            instance = new SharedPref(context);
        }
        return instance; //return SharedPref instance
    }


    //setUSER method is used to store used data in sharedPreferences
    //it has one parameter which is user data in form of json
    public void setUSER(String json)
    {
        //putting data in the  sharedPreferences
        sharedPreferences.edit().putString(USER, json).apply();
    }

    //getUSER method used to get data user data saved in sharedprefernce
    public String getUSER()
    {
        //Get USer Data And Return
        return sharedPreferences.getString(USER, null);
    }
}
