/************************************************************************************************
 File Name   : DailyMilkPlaceOrder.java
 Purpose     : This is the final confirmation screen for placing an order request to the server.
 This is the last activity in the sequence of activities that are called when an
 order is modified for a particular day. "ModifyDelivery" --> "MilkInfoFragment" -->
 "DailyMilkInfoModify" --> "DailyMilkPlaceOrder". This class also implements an
 Async task that sends modified milk information to the server.
 Author      : Deepak J. Daniel
 @Copyright Grace Samadhanam Development Company.
 *************************************************************************************************
 */

package com.comorinland.milkman.customerapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.comorinland.milkman.R;
import com.comorinland.milkman.common.Constant;
import com.comorinland.milkman.common.DownloadFromAmazonDBTask;
import com.comorinland.milkman.common.MilkInfo;
import com.comorinland.milkman.common.MilkInfoListAdapter;
import com.comorinland.milkman.common.ResponseHandler;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;


public class DailyMilkPlaceOrder extends AppCompatActivity implements ResponseHandler
{
    private ProgressDialog mProgressDialog;
    private HashMap<String, ArrayList<MilkInfo>> mPlacedOrder;
    private String mStrDateOfModification;
    private String mStrAppType;
    private String mStrCustomerID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_milk_place_order);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarPlaceOrder);
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

        mStrDateOfModification = (String) getIntent().getExtras().getSerializable("DateOfModification");
        mStrAppType            = (String) getIntent().getExtras().getSerializable("AppType");
        mStrCustomerID         = (String) getIntent().getExtras().getSerializable("CustomerID");

        TextView txtPlaceOrderDate = (TextView) findViewById(R.id.txt_milk_place_order_date);
        txtPlaceOrderDate.setText(mStrDateOfModification);

        mPlacedOrder = (HashMap<String, ArrayList<MilkInfo>>) getIntent().getExtras().getSerializable("PlacedOrder");

        Set<String> key = mPlacedOrder.keySet();
        Iterator<String> iter = key.iterator();

        CardView cv = (CardView) findViewById(R.id.card_place_order);
        ListView lstPlaceOrder = (ListView) cv.findViewById(R.id.list_milk_info);
        ArrayList<MilkInfo> milkInfoList = new ArrayList<MilkInfo>();

        while (iter.hasNext())
        {
            String strMilkType = iter.next();
            milkInfoList.addAll(mPlacedOrder.get(strMilkType));
        }

        lstPlaceOrder.setAdapter(new MilkInfoListAdapter(this, milkInfoList));

        Button btnConfirmOrder = (Button) findViewById(R.id.btn_confirm_order);

        btnConfirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mStrAppType.equals("CustomerApp"))
                    new DownloadFromAmazonDBTask(DailyMilkPlaceOrder.this, "CustomerApp/ChangeMilkDelivery.php", mProgressDialog).execute(jsonBuildDailyMilkInfo());
                if (mStrAppType.equals("VendorApp"))
                    new DownloadFromAmazonDBTask(DailyMilkPlaceOrder.this, "VendorApp/VendorSetMilkDelivery.php",mProgressDialog).execute(jsonBuildDailyMilkInfo());
            }
        });
    }

    private JsonObject jsonBuildDailyMilkInfo()
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String strVendorID = sharedPref.getString(getString(R.string.vendor_id),null);

        JsonObject jsonMilkObject = new JsonObject();
        jsonMilkObject.addProperty("CustomerID", mStrCustomerID);
        jsonMilkObject.addProperty("VendorID",strVendorID);
        jsonMilkObject.addProperty("Date", mStrDateOfModification);

        Set<String> key = mPlacedOrder.keySet();
        Iterator<String> iter = key.iterator();

        int i = 0;

        JsonObject jsonPacketObject = new JsonObject();

        while (iter.hasNext())
        {
            String strMilkType = iter.next();
            ArrayList<MilkInfo> milkInfoList = mPlacedOrder.get(strMilkType);

            for (int j = 0; j < milkInfoList.size(); j++)
            {
                MilkInfo objMilkInfo = milkInfoList.get(j);
                JsonObject jsonTemp = new JsonObject();
                jsonTemp.addProperty("PacketNo", objMilkInfo.getPacketNumber());
                jsonTemp.addProperty("Quantity", objMilkInfo.getQuantity());
                jsonTemp.addProperty("Type", strMilkType);
                int k = i + j;
                jsonPacketObject.add("Packet" + k, jsonTemp);
            }
            i = i + 1;
        }

        jsonMilkObject.add("RequestedDelivery",jsonPacketObject);
        return jsonMilkObject;
    }

    public void UpdateMilkInfoDisplay(String strReturnCode)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(DailyMilkPlaceOrder.this).create();

        // Setting Dialog Title
        alertDialog.setTitle("Alert");

        if (strReturnCode.equals(Constant.DB_ERROR))
        {
            // Setting Dialog Message
            alertDialog.setMessage("Sorry there was a problem. Please contact your vendor");
        }
        else if (strReturnCode.equals(Constant.RESPONSE_UNAVAILABLE))
        {
            alertDialog.setMessage("Please check your connection");
        }
        else
        {
            if (mStrAppType.equals("VendorApp"))
            {
                alertDialog.setMessage("Your change is successful");
            }
            if (mStrAppType.equals("CustomerApp"))
            {
                // Setting Dialog Message
                alertDialog.setMessage("Your change is waiting for approval");
            }
        }

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog    closed
                finish();
            }
        });
        // Showing Alert Message
        alertDialog.show();

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
}
