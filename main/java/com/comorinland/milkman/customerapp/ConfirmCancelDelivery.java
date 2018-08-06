/************************************************************************************************
 File Name   : ConfirmCancelDelivery.java
 Purpose     : This file implements an expandable list with a button at the bottom called
 "Cancel Delivery". The expandable list has two option items. A customer can customer_cancel
 milk for particular days or/and he can customer_cancel permanently.Clicking on the
 "Cancel Delivery" button will send a request for cancellation.
 Author      : Deepak J. Daniel
 @Copyright Grace Samadhanam Development Company.
 *************************************************************************************************
 */

package com.comorinland.milkman.customerapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.comorinland.milkman.R;
import com.comorinland.milkman.common.Constant;
import com.comorinland.milkman.common.DownloadFromAmazonDBTask;
import com.comorinland.milkman.common.ResponseHandler;
import com.comorinland.milkman.common.SharedHelper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ConfirmCancelDelivery extends AppCompatActivity implements ResponseHandler, CalendarFragment.OnFragmentInteractionListener
{
    private String strCancelStartDate, strCancelEndDate;
    private Boolean bDailyCancel = false;
    ExpandableListView el_ConfirmCancelDelivery;
    ExpandableCancelDeliveryListAdapter expandableCancelDeliveryListAdapter;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_cancel_delivery);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarCancelDelivery);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        expandableCancelDeliveryListAdapter = new ExpandableCancelDeliveryListAdapter(this);

        el_ConfirmCancelDelivery = (ExpandableListView) findViewById(R.id.el_CancelMilkDelivery);
        el_ConfirmCancelDelivery.setAdapter(expandableCancelDeliveryListAdapter);
        el_ConfirmCancelDelivery.setGroupIndicator(null);

        Button btnConfirmCancel = (Button) findViewById(R.id.btn_cancel_delivery);
        btnConfirmCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if ((expandableCancelDeliveryListAdapter.getMonthlyCancelInfo()) || (bDailyCancel == true))
                    new DownloadFromAmazonDBTask(ConfirmCancelDelivery.this, "CustomerApp/CustomerCancelDelivery.php", mProgressDialog).execute(jsonBuildCancelInformation());
                else
                    SharedHelper.showAlertDialog(ConfirmCancelDelivery.this, "You have not chosen to customer_cancel anything.",null);
            }
        });
    }

    private JsonObject jsonBuildCancelInformation()
    {
        String strCancellationType ="";

        JsonObject jsonCustomerObject = new JsonObject();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String strCustomerID = sharedPref.getString(getString(R.string.customer_id), null);
        String strVendorID = sharedPref.getString(getString(R.string.vendor_id),null);

        if (expandableCancelDeliveryListAdapter.getMonthlyCancelInfo() == true)
        {
            strCancellationType = "Monthly";
        }
        if (bDailyCancel == true)
        {
            strCancellationType = "Daily";
        }
        if ((expandableCancelDeliveryListAdapter.getMonthlyCancelInfo() == true) && (bDailyCancel == true))
        {
            strCancellationType = "Both";
        }

        try
        {
            jsonCustomerObject.addProperty("CustomerID", strCustomerID);
            jsonCustomerObject.addProperty("VendorID", strVendorID);
            jsonCustomerObject.addProperty("CancellationType",strCancellationType);

            if ((strCancellationType.equals("Both")) || (strCancellationType.equals("Daily")))
            {

                jsonCustomerObject.addProperty("FromDate", strCancelStartDate);
                jsonCustomerObject.addProperty("ToDate", strCancelEndDate);
            }
        }
        catch (JsonParseException e)
        {
            e.printStackTrace();
        }

        return jsonCustomerObject;
    }

    @Override
    public void UpdateMilkInfoDisplay(String strReturnCode)
    {
        if (strReturnCode.equals(Constant.DB_ERROR))
        {
            SharedHelper.showAlertDialog(ConfirmCancelDelivery.this, "Sorry for the problem. Please contact your adminstrator",null);
        }
        else if (strReturnCode.equals(Constant.RESPONSE_UNAVAILABLE))
        {
            SharedHelper.showAlertDialog(ConfirmCancelDelivery.this, "Please check your connection",null);
        }
        else
        {
            Intent e = new Intent(ConfirmCancelDelivery.this,MainMenuFeatures.class);
            SharedHelper.showAlertDialog(ConfirmCancelDelivery.this, "Your cancellation request is waiting for approval", e);
        }
    }

    @Override
    public String HandleJsonResponse(String serverJsonResponse)
    {
        if (serverJsonResponse == null)
        {
            return Constant.RESPONSE_UNAVAILABLE;
        }

        if (serverJsonResponse.equals("DB_EXCEPTION"))
        {
            return Constant.DB_ERROR;
        }
        else
            return Constant.JSON_SUCCESS;

    }

    @Override
    public void onFragmentInteraction(List<Date> dateList)
    {
        ArrayList<String> strCancelFormatDates;

        if (dateList.isEmpty())
            return;

        strCancelFormatDates = new ArrayList<String>();

        DateFormat dfDisplay = new SimpleDateFormat("MMM dd");
        DateFormat dfServer = new SimpleDateFormat("yyyy/MM/dd");

        bDailyCancel = true;

        strCancelStartDate = dfServer.format(dateList.get(0));
        strCancelEndDate = dfServer.format(dateList.get(dateList.size() - 1));

        strCancelFormatDates.add(0, dfDisplay.format(dateList.get(0)));
        strCancelFormatDates.add(1, dfDisplay.format(dateList.get(dateList.size() - 1)));

        TextView txtCancelInfo = (TextView) el_ConfirmCancelDelivery.findViewById(R.id.txt_Cancel_Info);

        if (dateList.size() == 1)
        {
            txtCancelInfo.setText("Cancel milk delivery on  : " + strCancelFormatDates.get(0));
        }
        else
            txtCancelInfo.setText("Cancel milk delivery from " + strCancelFormatDates.get(0) + " to " + strCancelFormatDates.get(1));

        txtCancelInfo.setVisibility(View.VISIBLE);
    }
}