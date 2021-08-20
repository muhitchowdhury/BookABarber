package com.codecoy.barbar.projectUtils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.kaopiz.kprogresshud.KProgressHUD;
import com.valdesekamdem.library.mdtoast.MDToast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
//This class contains the functions that as used frequently like to display different messages so they can be called easily by just creating its object
public class Utils {
    //display error message
    public static void ERROR_TOAST(Context context, String message) {
        MDToast.makeText(context, message, MDToast.LENGTH_SHORT, MDToast.TYPE_ERROR).show();
    }
    //display success message
    public static void SUCCESS_TOAST(Context context, String message) {
        MDToast.makeText(context, message, MDToast.LENGTH_SHORT, MDToast.TYPE_SUCCESS).show();
    }
    //display message
    public static void INFO_TOAST(Context context, String message) {
        MDToast.makeText(context, message, MDToast.LENGTH_SHORT, MDToast.TYPE_INFO).show();
    }

    public static void WARNING_TOAST(Context context, String message) {
        MDToast.makeText(context, message, MDToast.LENGTH_SHORT, MDToast.TYPE_WARNING).show();
    }

    //this method is used to hidekeyboard
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    //static method isNetworkAvailable is used to check whether user have active internet connection or not
    public static boolean isNetworkAvailable(Context context) {

        //Getting Instance of ConnectivityManager class
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        //Getiing Network information
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        //Checking if network is connected or not and returning the value
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    //Method to check if email is verified or not
    public static boolean isEmailValid(String email)
    {
        //Regex to check email
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        //Check pattern matches to email or not
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches(); //if email matches return tue
    }

    //Method to get progresbar it has two parameter one is Context of calling class and 2nd is the message to be shown on the progessdialog
    public static KProgressHUD getProgressDialog(Context context, String message) {

        //Creating progress bar instance
        KProgressHUD dialog = KProgressHUD.create(context)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE) //selecting progress bar type
                .setCancellable(false) //Making progress bar not cancel able
                .setLabel(message); //showing message on progressbar
        return dialog; //returning progress bar
    }
}
