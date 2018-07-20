package com.comorinland.milkman.customerapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

/**
 * Created by deepak on 5/2/18.
 */

public class SharedHelper
{

    static AlertDialog alertDialog;

    public static Boolean showAlertDialog(final Activity activity, String strMessage)
    {

        alertDialog = new AlertDialog.Builder(activity).create();

        // Setting Dialog Title
        alertDialog.setTitle("Alert");

        alertDialog.setMessage(strMessage);

        alertDialog.setCanceledOnTouchOutside(false);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
            }
        });
        // Showing Alert Message
        alertDialog.show();
        return Boolean.TRUE;
    }
}
