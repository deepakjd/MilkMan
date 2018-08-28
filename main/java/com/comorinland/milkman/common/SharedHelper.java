package com.comorinland.milkman.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.comorinland.milkman.R;
import com.comorinland.milkman.common.babushkatext.BabushkaText;

/**
 * Created by deepak on 5/2/18.
 */

public class SharedHelper
{

    static AlertDialog alertDialog;

    public static Boolean showAlertDialog(final Activity activity, String strMessage, final Intent intentStart)
    {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(activity, R.style.Theme_AppCompat_DayNight_Dialog_Alert);

        // Get the layout inflater
        LayoutInflater inflater = activity.getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.alert_dialog_screen,null);

        TextView title = new TextView(activity);

        // You Can Customise your Title here
        title.setText("MilkMan");
        title.setBackgroundColor(Color.GRAY);
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout

        builder.setView(dialogView).setCustomTitle(title).setPositiveButton(R.string.mdtp_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (intentStart != null) {
                            intentStart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            activity.startActivity(intentStart);
                        }
                    }
                });

        BabushkaText babushkaTextVersion = (BabushkaText)dialogView.findViewById(R.id.message_alert_screen);
        babushkaTextVersion.setText(strMessage);

        // Showing Alert Message
        builder.show();
        return Boolean.TRUE;
    }
}
