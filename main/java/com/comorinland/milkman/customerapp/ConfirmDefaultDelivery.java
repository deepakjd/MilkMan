/************************************************************************************************
 File Name   : ConfirmDefaultDelivery.java
 Purpose     : This is the final screen in the activity sequence to change default monthly_delivery. The
 activities are spawned in the sequence "Change Default Delivery" -->
 "ChangeDefaultDeliveryExpandableList"  ---> "ConfirmDefaultDelivery". Clicking on the
 "Confirm Default Delivery" button will send a request for change in default monthly_delivery.
 Author      : Deepak J. Daniel
 @Copyright Grace Samadhanam Development Company.
 *************************************************************************************************
 */

package com.comorinland.milkman.customerapp;

import android.app.ProgressDialog;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;


public class ConfirmDefaultDelivery extends AppCompatActivity implements ResponseHandler
{
    private ProgressDialog mProgressDialog;
    HashMap<String, ArrayList<MilkInfo>> mPlacedOrder;
    String  mStrAppType;
    String  mStrCustomerID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_default_delivery);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarConfirmDefaultDelivery);
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

        mStrCustomerID = (String) getIntent().getExtras().getSerializable("CustomerID");
        mStrAppType  = (String) getIntent().getExtras().getSerializable("AppType");
        mPlacedOrder = (HashMap<String, ArrayList<MilkInfo>>) getIntent().getExtras().getSerializable("PlacedOrder");

        Set<String> key = mPlacedOrder.keySet();
        Iterator<String> iter = key.iterator();

        CardView cv = (CardView) findViewById(R.id.card_confirm_default_delivery);
        ListView lstPlaceOrder = (ListView) cv.findViewById(R.id.list_milk_info);
        ArrayList<MilkInfo> milkInfoList = new ArrayList<MilkInfo>();

        while (iter.hasNext()) {
            String strMilkType = iter.next();
            milkInfoList.addAll(mPlacedOrder.get(strMilkType));
        }

        lstPlaceOrder.setAdapter(new MilkInfoListAdapter(this, milkInfoList));

        Button btnConfirmDefaultDelivery = (Button) findViewById(R.id.btn_confirm_default_delivery);
        btnConfirmDefaultDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mStrAppType.equals("CustomerApp"))
                    new DownloadFromAmazonDBTask(ConfirmDefaultDelivery.this, "CustomerApp/ChangeDefaultDelivery.php", mProgressDialog).execute(jsonBuildDailyMilkInfo());
                if (mStrAppType.equals("VendorApp"))
                    new DownloadFromAmazonDBTask(ConfirmDefaultDelivery.this, "VendorApp/VendorSetDefaultDelivery.php", mProgressDialog).execute(jsonBuildDailyMilkInfo());
            }
        });
    }

    public void UpdateMilkInfoDisplay(String strReturnCode)
    {
        if (strReturnCode.equals(Constant.DB_ERROR))
        {
            SharedHelper.showAlertDialog(ConfirmDefaultDelivery.this, "There was an error in updation");
        }
        else if (strReturnCode.equals(Constant.RESPONSE_UNAVAILABLE))
        {
            SharedHelper.showAlertDialog(ConfirmDefaultDelivery.this, "Please check your internet connection");
        }
        else
        {
            if (mStrAppType.equals("CustomerApp"))
                SharedHelper.showAlertDialog(ConfirmDefaultDelivery.this, "Your change is waiting for approval");
            if (mStrAppType.equals("VendorApp")) {
                SharedHelper.showAlertDialog(ConfirmDefaultDelivery.this, "Regular monthly delivery for customer is done.");
                finish();
            }
        }
    }

    @Override
    public String HandleJsonResponse(String serverJsonResponse)
    {
        if(serverJsonResponse == null)
        {
            return Constant.RESPONSE_UNAVAILABLE;
        }
        else if (serverJsonResponse.equals("DB_EXCEPTION"))
        {
            return Constant.DB_ERROR;
        }
        else {
            return Constant.JSON_SUCCESS;
        }
    }

    private JsonObject jsonBuildDailyMilkInfo()
    {
        JsonObject jsonMilkObject = new JsonObject();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String strVendorID = sharedPref.getString(getString(R.string.vendor_id), null);

        if (mStrAppType.equals("CustomerApp"))
        {
            String strCustomerID = sharedPref.getString(getString(R.string.customer_id), null);
            jsonMilkObject.addProperty("VendorID", strVendorID);
            jsonMilkObject.addProperty("CustomerID", strCustomerID);
        }
        else if (mStrAppType.equals("VendorApp"))
        {
            jsonMilkObject.addProperty("CustomerID", mStrCustomerID);
            jsonMilkObject.addProperty("VendorID", strVendorID);
        }

        Set<String> key = mPlacedOrder.keySet();
        Iterator<String> iter = key.iterator();

        int i = 0;

        JsonObject jsonPacketObject = new JsonObject();

        while (iter.hasNext()) {
            String strMilkType = iter.next();
            ArrayList<MilkInfo> milkInfoList = mPlacedOrder.get(strMilkType);

            for (int j = 0; j < milkInfoList.size(); j++) {
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
        jsonMilkObject.add("Requested Delivery", jsonPacketObject);
        return jsonMilkObject;
    }
}