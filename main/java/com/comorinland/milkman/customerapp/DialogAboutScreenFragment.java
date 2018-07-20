package com.comorinland.milkman.customerapp;

import android.app.Dialog;
import android.content.DialogInterface;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.comorinland.milkman.R;
import com.comorinland.milkman.common.babushkatext.BabushkaText;

public class DialogAboutScreenFragment extends DialogFragment
{

    public DialogAboutScreenFragment()
    {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.fragment_dialog_about_screen,null);

        TextView title = new TextView(getActivity());
        // You Can Customise your Title here
        title.setText("About");
        title.setBackgroundColor(Color.DKGRAY);
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout

        builder.setView(dialogView)
                .setCustomTitle(title)
                .setPositiveButton(R.string.mdtp_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // sign in the user ...
                }
            });

        BabushkaText babushkaTextVersion = (BabushkaText)dialogView.findViewById(R.id.version_about_screen);
        babushkaTextVersion.setText(" Version : 1.0 ");

        BabushkaText babushkaTextAbout = (BabushkaText)dialogView.findViewById(R.id.description_about_screen);
        babushkaTextAbout.setText(" This is an app which facilitates the interaction between customer and supplier for milk delivery ");

        BabushkaText babushkaTextDevelopers = (BabushkaText) dialogView.findViewById(R.id.developer_about_screen);
        babushkaTextDevelopers.setText("Copyright \0169 2017-2019 Comorinland Developers");

        BabushkaText babushkaTextCredits = (BabushkaText) dialogView.findViewById(R.id.credits_about_screen);
        babushkaTextCredits.addPiece(new BabushkaText.Piece.Builder("Credits : \n").build());
        babushkaTextCredits.addPiece(new BabushkaText.Piece.Builder("Icons made by Freepik from www.flaticon.com ").build());
        babushkaTextCredits.display();

        return builder.create();
    }
}
