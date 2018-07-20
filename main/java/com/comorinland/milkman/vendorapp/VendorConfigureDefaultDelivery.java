package com.comorinland.milkman.vendorapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;

import com.comorinland.milkman.R;
import com.comorinland.milkman.common.Constant;
import com.comorinland.milkman.common.DownloadFromAmazonDBTask;
import com.comorinland.milkman.common.MilkInfo;
import com.comorinland.milkman.common.MilkTypeInfo;
import com.comorinland.milkman.common.ResponseHandler;
import com.comorinland.milkman.common.ExpandableMilkInfoListAdapter;
import com.comorinland.milkman.customerapp.ConfirmDefaultDelivery;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class VendorConfigureDefaultDelivery extends AppCompatActivity implements ResponseHandler
{
    private ProgressDialog mProgressDialog;
    ExpandableMilkInfoListAdapter mExpandableMilkInfoListAdapter;
    List<MilkTypeInfo> mListMilkTypeInfo = new ArrayList<>();
    String mStrCustomerID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_configure_default_delivery);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarConfigureDefaultDelivery);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Configure Monthly Delivery");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mStrCustomerID = (String) getIntent().getExtras().getSerializable("CustomerID");

        // Create thread to get data from the server.
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        Button btnPlaceOrder = (Button)findViewById(R.id.btn_submit_order);
        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, ArrayList<MilkInfo>> hashMapMilkInfo = new HashMap<String, ArrayList<MilkInfo>>();
                hashMapMilkInfo.putAll(mExpandableMilkInfoListAdapter.getMilkInfo());
                Intent intentConfirmDefaultDelivery = new Intent(VendorConfigureDefaultDelivery.this, ConfirmDefaultDelivery.class);
                intentConfirmDefaultDelivery.putExtra("CustomerID",mStrCustomerID);
                intentConfirmDefaultDelivery.putExtra("AppType","VendorApp");
                intentConfirmDefaultDelivery.putExtra("PlacedOrder", hashMapMilkInfo);
                startActivity(intentConfirmDefaultDelivery);
                finish();
            }
        });
        new DownloadFromAmazonDBTask(this,"Common/GetMilkInfo.php",mProgressDialog).execute(jsonBuildInfo());
    }

    private JsonObject jsonBuildInfo()
    {
        JsonObject jsonVendorObject = new JsonObject();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String mStrCityName = sharedPref.getString(getString(R.string.city_name), null);
        String mStrDistributionCompany = sharedPref.getString(getString(R.string.distribution_company), null);

        jsonVendorObject.addProperty("City", mStrCityName);
        jsonVendorObject.addProperty("DistributionCompany",mStrDistributionCompany);

        return jsonVendorObject;
    }

    /* This function is going to get the string response and store it in a string array */
    @Override
    public String HandleJsonResponse(String strResponse)
    {
        if (strResponse == null)
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
            JSONObject jsonMilkVarietiesObject = new JSONObject(strResponse);

            Iterator<String> iter = jsonMilkVarietiesObject.keys();

            while (iter.hasNext())
            {
                String strMilkType = iter.next();

                JSONArray jsonMilkQuantitiesObject = jsonMilkVarietiesObject.getJSONArray(strMilkType);

                List<String> lstMilkQuantity = new ArrayList<>();

                for (int i = 0; i < jsonMilkQuantitiesObject.length(); i++)
                {
                    lstMilkQuantity.add((String)jsonMilkQuantitiesObject.get(i));
                }

                MilkTypeInfo e = new MilkTypeInfo(strMilkType,lstMilkQuantity);
                mListMilkTypeInfo.add(e);
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
        if (strReturnCode.equals(Constant.JSON_SUCCESS))
        {
            ExpandableListView expandableMilkInfoListView;
            expandableMilkInfoListView = (ExpandableListView) findViewById(R.id.el_configure_delivery);
            mExpandableMilkInfoListAdapter = new ExpandableMilkInfoListAdapter(this, mListMilkTypeInfo);
            expandableMilkInfoListView.setAdapter(mExpandableMilkInfoListAdapter);
            expandableMilkInfoListView.setGroupIndicator(null);
        }
    }
}