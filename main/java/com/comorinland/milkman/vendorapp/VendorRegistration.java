package com.comorinland.milkman.vendorapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.comorinland.milkman.R;
import com.comorinland.milkman.common.Constant;
import com.comorinland.milkman.common.DownloadFromAmazonDBTask;
import com.comorinland.milkman.common.ResponseHandler;
import com.comorinland.milkman.customerapp.SharedHelper;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import me.srodrigo.androidhintspinner.HintAdapter;
import me.srodrigo.androidhintspinner.HintSpinner;

public class VendorRegistration extends AppCompatActivity implements ResponseHandler
{
    private HashMap<String, List<String>> mHashMapCityDistributorInfo;
    private List<String> mListCities;
    private List<String> mDistributionCompanies = null;
    private ProgressDialog mProgressDialog;
    private String mStrVendorID, mStrVendorPassword,mStrVendorCity,mStrMilkCompany,mStrBillDay,mStrVendoName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        mHashMapCityDistributorInfo = new HashMap<>();
        mListCities = new ArrayList<String>();
        mDistributionCompanies = new ArrayList<>();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.vendor_activity_registration);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarVendorRegistration);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Vendor Registration");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        Button btnVendorRegister = (Button) findViewById(R.id.btn_vendor_register);
        btnVendorRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String strReturnCode = AuthenticateVendorInfo();
                if (strReturnCode.equals("SUCCESS"))
                {
                    JsonObject jsonVendorOject = buildVendorInfoJson();
                    new DownloadFromAmazonDBTask(VendorRegistration.this, "VendorApp/SetVendorInfo.php", mProgressDialog).execute(jsonVendorOject);
                }
            }
        });

        new DownloadFromAmazonDBTask(VendorRegistration.this, "VendorApp/VendorGetDistributionCompany.php", mProgressDialog).execute(jsonBuildInfo());
    }

    private String AuthenticateVendorInfo()
    {
        EditText edtVendorID = (EditText) findViewById(R.id.vendor_registration_mobile_no);
        EditText edtVendorPassword = (EditText) findViewById(R.id.vendor_registration_password);
        Spinner spinnerCity = (Spinner) findViewById(R.id.vendor_registration_city);
        Spinner spinnerMilkCompany = (Spinner) findViewById(R.id.vendor_agency_name);
        EditText edtMonthlyBillDay = (EditText) findViewById(R.id.vendor_customer_billing_date);
        EditText edtVendorName = (EditText) findViewById(R.id.vendor_registration_full_name);

        /* Return JsonObject back */
        mStrVendorID = edtVendorID.getText().toString();
        mStrVendorPassword = edtVendorPassword.getText().toString();
        mStrVendorCity = spinnerCity.getSelectedItem().toString();
        mStrMilkCompany = spinnerMilkCompany.getSelectedItem().toString();
        mStrBillDay = edtMonthlyBillDay.getText().toString();
        mStrVendoName = edtVendorName.getText().toString();

        /* Do your authentication here */
        return "SUCCESS";
    }

    private JsonObject buildVendorInfoJson()
    {
        JsonObject jsonVendorInfoObject;

        jsonVendorInfoObject = new JsonObject();
        jsonVendorInfoObject.addProperty("VendorID",mStrVendorID);
        jsonVendorInfoObject.addProperty("VendorPassword",mStrVendorPassword);
        jsonVendorInfoObject.addProperty("Vendor Name", mStrVendoName);
        jsonVendorInfoObject.addProperty("City", mStrVendorCity);
        jsonVendorInfoObject.addProperty("DistributionCompany",mStrMilkCompany);
        jsonVendorInfoObject.addProperty("MonthlyBillDay", mStrBillDay);

        return jsonVendorInfoObject;
    }

    /* This function is going to get the string response and store it in a string array */
    @Override
    public String HandleJsonResponse(String serverJsonResponse)
    {
        if (serverJsonResponse == null)
        {
            return Constant.RESPONSE_UNAVAILABLE;
        }
        if (serverJsonResponse.equals("VENDOR_INFO_SUCCESS"))
        {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

            SharedPreferences.Editor editor = sharedPref.edit();

            editor.putString(getString(R.string.vendor_id), mStrVendorID);
            editor.commit();
            editor.putString(getString(R.string.city_name), mStrVendorCity);
            editor.commit();
            editor.putString(getString(R.string.distribution_company),mStrMilkCompany);
            editor.commit();
            editor.putString(getString(R.string.vendor_password), mStrVendorPassword);
            editor.commit();

            return Constant.VENDOR_WRITE_SUCCESS;
        }
        if (serverJsonResponse.equals("QUERY_EMPTY"))
        {
            return Constant.INFO_NOT_FOUND;
        }
        if (serverJsonResponse.equals("DB_EXCEPTION"))
        {
            return Constant.DB_ERROR;
        }

        try
        {
            JSONObject jsonCityAndDistributor = new JSONObject(serverJsonResponse);

            Iterator<String> iter = jsonCityAndDistributor.keys();
            while (iter.hasNext())
            {
                String key = iter.next();

                mListCities.add(key);

                JSONObject jsonArrayCompaniesObject = jsonCityAndDistributor.getJSONObject(key);

                List<String> e = new ArrayList<String>();

                for (Integer i = 0; i < jsonArrayCompaniesObject.length(); i++) {
                    e.add(jsonArrayCompaniesObject.getString(i.toString()));
                }
                mHashMapCityDistributorInfo.put(key, e);
            }
        }
        catch (JSONException e)
        {
            return Constant.JSON_EXCEPTION;
        }
        return Constant.JSON_SUCCESS;
    }

    private JsonObject jsonBuildInfo() {
        JsonObject jsonCustomerObject = new JsonObject();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String strVendorID = sharedPref.getString(getString(R.string.vendor_id), null);

        jsonCustomerObject.addProperty("VendorID", strVendorID);

        return jsonCustomerObject;
    }

    public void CallbackDistributionCompanySpinner()
    {
        Spinner spinnerDealer = (Spinner) findViewById(R.id.vendor_agency_name);

        HintSpinner<String> hintSpinner1 = new HintSpinner<>(
                spinnerDealer,
                new HintAdapter<>(this, "Select your dealer", mDistributionCompanies),
                new HintSpinner.Callback<String>() {
                    @Override
                    public void onItemSelected(int position, String itemAtPosition) {
                        // Here you handle the on item selected event (this skips the hint selected event)
                    }
                });

        hintSpinner1.init();

    }

    public void UpdateMilkInfoDisplay(String strReturnCode)
    {
        if (strReturnCode.equals(Constant.VENDOR_WRITE_SUCCESS))
        {
            SharedHelper.showAlertDialog(this, "You have been sucessfully registered");
        }
        else if (strReturnCode.equals(Constant.JSON_SUCCESS))
        {
            Spinner spinnerCity = (Spinner) findViewById(R.id.vendor_registration_city);

            HintSpinner<String> hintSpinner = new HintSpinner<>(
                    spinnerCity,
                    // Default layout - You don't need to pass in any layout id, just your hint text and
                    // your list data
                    new HintAdapter<>(this, "Select your city", mListCities),

                    new HintSpinner.Callback<String>() {
                        @Override
                        public void onItemSelected(int position, String itemAtPosition) {
                            // Here you handle the on item selected event (this skips the hint selected event)

                            mDistributionCompanies.addAll(mHashMapCityDistributorInfo.get(mListCities.get(position)));
                            CallbackDistributionCompanySpinner();
                        }
                    });

            hintSpinner.init();

        }
        else
        {
            SharedHelper.showAlertDialog(this, "Problem in accessing data from server");
        }
    }
}