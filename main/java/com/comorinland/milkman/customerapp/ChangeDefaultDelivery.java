/************************************************************************************************
 File Name   : ChangeDefaultDelivery.java
 Purpose     : This file implements the functionality to request a change in default monthly_delivery for
 a particular user. The activity layout screen, displays the default monthly_delivery.
 Clicking on the "Change Default Delivery" button at the bottom of the screen, opens
 an expandable list screen.
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
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.comorinland.milkman.R;
import com.comorinland.milkman.common.Constant;
import com.comorinland.milkman.common.DownloadFromAmazonDBTask;
import com.comorinland.milkman.common.MilkInfo;
import com.comorinland.milkman.common.MilkInfoListAdapter;
import com.comorinland.milkman.common.ResponseHandler;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;


public class ChangeDefaultDelivery extends AppCompatActivity implements ResponseHandler
{
    private ArrayList<MilkInfo> arrayListMilkInfo;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_default_delivery);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarChangeDelivery);
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

        new DownloadFromAmazonDBTask(this, "CustomerApp/GetDefaultDelivery.php", mProgressDialog).execute(jsonBuildInfo());

        Button btnChangeDefaultDelivery = (Button) findViewById(R.id.btn_change_default_delivery);
        btnChangeDefaultDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentExpandableListDefaultDelivery = new Intent(ChangeDefaultDelivery.this, ChangeDefaultDeliveryExpandableList.class);
                startActivity(intentExpandableListDefaultDelivery);
            }
        });
    }

    private JsonObject jsonBuildInfo()
    {
        JsonObject jsonCustomerObject = new JsonObject();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String strCustomerID = sharedPref.getString(getString(R.string.customer_id), null);
        String strVendorID = sharedPref.getString(getString(R.string.vendor_id),null);

        jsonCustomerObject.addProperty("CustomerID", strCustomerID);
        jsonCustomerObject.addProperty("VendorID",strVendorID);
        return jsonCustomerObject;
    }

    /* This function is going to get the string response and store it in a string array */
    @Override
    public String HandleJsonResponse(String strResponse)
    {
        if(strResponse == null)
        {
            return Constant.RESPONSE_UNAVAILABLE;
        }
        if (strResponse.equals("QUERY_EMPTY"))
        {
            return Constant.INFO_NOT_FOUND;
        }
        if (strResponse.equals("DB_EXCEPTION"))
        {
            return Constant.DB_ERROR;
        }

        try
        {
            JSONObject dataObject = new JSONObject(strResponse);

            JSONObject jsonObjDefault = dataObject.getJSONObject("Delivery");

            arrayListMilkInfo = new ArrayList<>();

            Iterator<String> iter = jsonObjDefault.keys();

            while (iter.hasNext())
            {
                String key = iter.next();
                if (key.contains("Packet"))
                {
                    JSONObject jsonObjMilk = jsonObjDefault.getJSONObject(key);
                    MilkInfo milkInfoObject = new MilkInfo(jsonObjMilk.getString("Type"), jsonObjMilk.getInt("PacketNo"), jsonObjMilk.getString("Quantity"));
                    arrayListMilkInfo.add(milkInfoObject);
                }
            }
        }
        catch (JSONException e)
        {
            return Constant.JSON_EXCEPTION;
        }
        return Constant.JSON_SUCCESS;
    }

    public void UpdateMilkInfoDisplay(String strReturnCode)
    {
        if (strReturnCode.equals(Constant.JSON_SUCCESS) == Boolean.FALSE)
        {
            if (strReturnCode.equals(Constant.INFO_NOT_FOUND))
            {
                SharedHelper.showAlertDialog(ChangeDefaultDelivery.this, "Milk Information not available for this date");
            }
            else
            {
                // Setting Dialog Message
                SharedHelper.showAlertDialog(ChangeDefaultDelivery.this, "Problem in getting information");
            }
        }
        else
        {
            CardView cv = (CardView) findViewById(R.id.card_change_default_order);
            ListView lstMilkInfo = (ListView) cv.findViewById(R.id.list_milk_info);
            lstMilkInfo.setAdapter(new MilkInfoListAdapter(this, arrayListMilkInfo));
        }
    }
}