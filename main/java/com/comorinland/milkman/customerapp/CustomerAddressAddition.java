package com.comorinland.milkman.customerapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.comorinland.milkman.R;
import com.comorinland.milkman.common.Constant;
import com.comorinland.milkman.common.DownloadFromAmazonDBTask;
import com.comorinland.milkman.common.ResponseHandler;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

public class CustomerAddressAddition extends AppCompatActivity implements ResponseHandler
{
    private ProgressDialog mProgressDialog;

    String mStrCustomerName;
    String mStrCustomerID;
    String mStrCustomerPassword;
    String mStrVendorID;
    String mStrCityName;
    String mStrPostalAddress;

    Boolean mbRegisterCustomer;   /* An internal flag to check if it is query for registering
                                     a customer or for querying the City name
                                  */

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_address_addition);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarCustomerAddressAddition);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Enter Your Address");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final RelativeLayout contentView = (RelativeLayout) findViewById(R.id.root_address_content_view);

        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {

                Rect r = new Rect();

                contentView.getWindowVisibleDisplayFrame(r);

                int screenHeight = contentView.getRootView().getHeight();

                Button btnAddressRegister = (Button) findViewById(R.id.btn_customer_register_with_address);

                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r.bottom;

                if (keypadHeight > screenHeight * 0.15)
                { // 0.15 ratio is perhaps enough to determine keypad height.
                    // keyboard is opened
                    btnAddressRegister.setVisibility(View.INVISIBLE);
                }
                else
                {
                    // keyboard is closed
                    btnAddressRegister.setVisibility(View.VISIBLE);
                }
            }
        });

        mbRegisterCustomer = Boolean.FALSE;

        mStrCustomerName = (String) getIntent().getExtras().getSerializable("CustomerName");
        mStrCustomerID = (String) getIntent().getExtras().getSerializable("CustomerID");
        mStrCustomerPassword = (String) getIntent().getExtras().getSerializable("CustomerPassword");
        mStrVendorID = (String) getIntent().getExtras().getSerializable("VendorID");

        mProgressDialog = new ProgressDialog(CustomerAddressAddition.this);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        /**
         * The Vendor database is queried to get the City Name.
         */

        new DownloadFromAmazonDBTask(CustomerAddressAddition.this, "CustomerApp/GetCityName.php", mProgressDialog).execute(jsonBuildInfo());

        Button btnCustomerRegister = (Button) findViewById(R.id.btn_customer_register_with_address);

        btnCustomerRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                boolean result = authenticateCustomerAddress();

                if (result == true)
                {
                    mProgressDialog = new ProgressDialog(CustomerAddressAddition.this);
                    mProgressDialog.setIndeterminate(false);
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

                    new DownloadFromAmazonDBTask(CustomerAddressAddition.this, "CustomerApp/SetCustomerInfo.php", mProgressDialog).execute(jsonBuildCustomerInfo());
                }
            }
        });
    }

    private boolean authenticateCustomerAddress()
    {
        EditText edtCustomerHomeNo;
        EditText edtCustomerStreet;
        EditText edtCustomerLocality;

        String   strCustomerHomeNo;
        String   strCustomerStreet;
        String   strCustomerLocality;

        boolean bAuthenticateOK = true;

        edtCustomerHomeNo = (EditText) findViewById(R.id.edtCustomerHomeNo);
        edtCustomerStreet = (EditText) findViewById(R.id.edtCustomerStreet);
        edtCustomerLocality = (EditText) findViewById(R.id.edtCustomerLocality);

        strCustomerHomeNo = edtCustomerHomeNo.getText().toString();
        strCustomerStreet = edtCustomerStreet.getText().toString();
        strCustomerLocality = edtCustomerLocality.getText().toString();

        View focusView = null;

        // Check for a valid Customer Home/ Apartment number.
        if (TextUtils.isEmpty(strCustomerHomeNo))
        {
            edtCustomerHomeNo.setError(getString(R.string.error_field_required));
            focusView = edtCustomerHomeNo;
            bAuthenticateOK = false;
        }

        // Check for a valid Street Name.
        if (TextUtils.isEmpty(strCustomerStreet))
        {
            edtCustomerStreet.setError(getString(R.string.error_field_required));
            focusView = edtCustomerStreet;
            bAuthenticateOK = false;
        }

        // Check for a valid Customer locality
        if (TextUtils.isEmpty(strCustomerLocality))
        {
            edtCustomerStreet.setError(getString(R.string.error_field_required));
            focusView = edtCustomerLocality;
            bAuthenticateOK = false;
        }

        if (bAuthenticateOK == false)
        {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }

        mStrPostalAddress = strCustomerHomeNo + ',' + strCustomerStreet + ',' + strCustomerLocality;
        return bAuthenticateOK;

    }

    private JsonObject jsonBuildInfo()
    {
        JsonObject jsonCustomerObject = new JsonObject();

        jsonCustomerObject.addProperty("VendorID", mStrVendorID);

        return jsonCustomerObject;
    }

    private JsonObject jsonBuildCustomerInfo()
    {
        JsonObject jsonCustomerObject = new JsonObject();

        jsonCustomerObject.addProperty("CustomerName", mStrCustomerName);
        jsonCustomerObject.addProperty("CustomerID", mStrCustomerID);
        jsonCustomerObject.addProperty("Password", mStrCustomerPassword);
        jsonCustomerObject.addProperty("VendorID", mStrVendorID);
        jsonCustomerObject.addProperty("PostalAddress",mStrPostalAddress);

        return  jsonCustomerObject;
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

        if (strResponse.equals("QUERY_SUCCESS"))     /* This is a response for setting successfully
                                                        customer information into the DB */
        {
            mbRegisterCustomer = Boolean.TRUE;
            return Constant.JSON_SUCCESS;
        }

        try
        {
            mbRegisterCustomer = Boolean.FALSE;
            JSONObject jsonCityNameObject = new JSONObject(strResponse);
            mStrCityName = jsonCityNameObject.getString("City");
        }
        catch (JSONException e)
        {
            return Constant.JSON_EXCEPTION;
        }
        return Constant.JSON_SUCCESS;

    }

    public void UpdateMilkInfoDisplay(String strReturnCode)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(CustomerAddressAddition.this).create();

        // Setting Dialog Title
        alertDialog.setTitle("Alert");


        if (strReturnCode.equals(Constant.DB_ERROR))
        {
            // Setting Dialog Message
            alertDialog.setMessage("Sorry there was a problem. Please contact your vendor");
            // Showing Alert Message
            alertDialog.show();
        }
        if (strReturnCode.equals(Constant.RESPONSE_UNAVAILABLE))
        {
            alertDialog.setMessage("Please check your connection");
            // Showing Alert Message
            alertDialog.show();
        }

        if (strReturnCode.equals(Constant.JSON_SUCCESS))
        {
            if (mbRegisterCustomer == Boolean.TRUE)
            {
                alertDialog.setMessage("Registration succesful. You can login with your password.");

                // Setting OK Button
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(CustomerAddressAddition.this, CustomerLogin.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                });
                // Showing Alert Message
                alertDialog.show();
            }
            else
            {
                EditText edtCustomerCity = (EditText) findViewById(R.id.edtCustomerCity);
                edtCustomerCity.setText(mStrCityName);
            }
        }
    }
}