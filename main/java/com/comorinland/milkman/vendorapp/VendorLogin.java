package com.comorinland.milkman.vendorapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.comorinland.milkman.R;

import com.comorinland.milkman.common.Constant;
import com.comorinland.milkman.common.CustomerInfo;
import com.comorinland.milkman.common.CustomerInfoDatabase;
import com.comorinland.milkman.common.DownloadFromAmazonDBTask;
import com.comorinland.milkman.common.ResponseHandler;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class VendorLogin extends AppCompatActivity implements ResponseHandler
{
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    private VendorLogin.VendorLoginTask mAuthTask = null;
    private VendorLogin.CustomerDatabaseAsync mCustomerDatabaseAsync = null;

    // UI references.
    private EditText mEditVendorID;
    private EditText mEditVendorPassword;
    private ProgressDialog mProgressDialog;
    String mStrVendorPassword;
    String mStrVendorID;
    String mStrCityName;
    String mStrMilkDistributor;
    ArrayList<CustomerInfo> mlistCustomerInfo = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vendor_activity_login);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbarVendorLogin);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Vendor Login");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Set up the login form.
        mEditVendorID = (EditText) findViewById(R.id.edit_vendor_login_id);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String strVendorID = sharedPref.getString(getString(R.string.vendor_id), null);
        mEditVendorID.setText(strVendorID);

        mEditVendorPassword = (EditText) findViewById(R.id.edit_vendor_password);

        mEditVendorPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button btnVendorSignIn = (Button) findViewById(R.id.vendor_sign_in_button);

        btnVendorSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (IsConfigurationInfoPresent() == false)
                {
                    /* The shared preferences do not have some preference information. We need to
                       get the information from the preferences for the login to be authenticated.
                       */
                    // Create thread to get data from the server.
                    mProgressDialog = new ProgressDialog(VendorLogin.this);
                    mProgressDialog.setIndeterminate(false);
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

                    new DownloadFromAmazonDBTask(VendorLogin.this, "VendorApp/GetVendorInfo.php", mProgressDialog).execute(jsonBuildInfo());
                }
                else
                {
                    attemptLogin();
                }
            }
        });

        SpannableString ss = new SpannableString("No account yet? Register for an account here");

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                startActivity(new Intent(VendorLogin.this, VendorRegistration.class));
            }
        };
        ss.setSpan(clickableSpan, 40, 44, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextView txtVendorRegister = (TextView) findViewById(R.id.vendor_link_signup);
        txtVendorRegister.setText(ss);
        txtVendorRegister.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        mEditVendorID = (EditText) findViewById(R.id.edit_vendor_login_id);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String strVendorID = sharedPref.getString(getString(R.string.vendor_id), null);

        if (!mEditVendorID.getText().toString().equals(strVendorID))
            mEditVendorID.setText(strVendorID);
    }


    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    private JsonObject jsonBuildInfo()
    {
        JsonObject jsonCustomerObject = new JsonObject();

        mEditVendorID = (EditText) findViewById(R.id.edit_vendor_login_id);
        String strVendorID = mEditVendorID.getText().toString();

        jsonCustomerObject.addProperty("VendorID", strVendorID);

        return jsonCustomerObject;
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
            JSONObject dataObject = new JSONObject(strResponse);
            mStrVendorID = dataObject.getString("VendorID");
            mStrVendorPassword = dataObject.getString("Password");
            mStrCityName         = dataObject.getString("City");
            mStrMilkDistributor  = dataObject.getString("MilkCompany");
            JSONObject customerInfoObject = dataObject.getJSONObject("CustomerList");

            if (customerInfoObject != null)
            {
                Iterator<String> iter = customerInfoObject.keys();
                while (iter.hasNext())
                {
                    String key = iter.next();
                    String strCustomerName = customerInfoObject.getString(key);
                    mlistCustomerInfo.add(new CustomerInfo(key,strCustomerName));
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
        boolean bCustomerInformationFlag = false;  // Assume there is no customer information.
                                                   // If there is customer information, we would
                                                   // call the Database Async Task, which would call
                                                   // attemptLogin() in its onPostExecute().

        if (strReturnCode.equals(Constant.JSON_SUCCESS))
        {
           /* The registration process has been a success. We will store important customer
              related information that is going to be used throughout the app in the profile
              database.
            */

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(getString(R.string.vendor_id), mStrVendorID);
            editor.commit();
            editor.putString(getString(R.string.vendor_password), mStrVendorPassword);
            editor.commit();
            editor.putString(getString(R.string.city_name), mStrCityName);
            editor.commit();
            editor.putString(getString(R.string.distribution_company),mStrMilkDistributor);
            editor.commit();
            if (mCustomerDatabaseAsync == null)
            {
                if (mlistCustomerInfo.isEmpty() == false) {
                    bCustomerInformationFlag = true;
                    mCustomerDatabaseAsync = new VendorLogin.CustomerDatabaseAsync(VendorLogin.this);
                    mCustomerDatabaseAsync.execute(mlistCustomerInfo);
                }
            }
        }
        if (bCustomerInformationFlag == false)
            /* We login if there is no customer information here else it is onPostExecute() of DB AsyncTask */
            attemptLogin();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors ( missing fields, short passwords ), the
     * errors are presented and no actual login attempt is made.
     */

    private void attemptLogin()
    {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEditVendorID.setError(null);
        mEditVendorPassword.setError(null);

        // Store values at the time of the login attempt.
        String strVendorID = mEditVendorID.getText().toString();
        String strVendorPassword = mEditVendorPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(strVendorPassword) && !isPasswordValid(strVendorPassword)) {
            mEditVendorPassword.setError(getString(R.string.error_invalid_password));
            focusView = mEditVendorPassword;
            cancel = true;
        }

        // Check for a valid  Vendor ID.
        if (TextUtils.isEmpty(strVendorID))
        {
            mEditVendorID.setError(getString(R.string.error_field_required));
            focusView = mEditVendorID;
            cancel = true;
        }

        if (cancel)
        {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }
        else
        {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            mAuthTask = new VendorLogin.VendorLoginTask(this, strVendorID, strVendorPassword);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean IsConfigurationInfoPresent()
    {
        boolean bConfigurationInfo = true;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        bConfigurationInfo = sharedPref.contains(getString(R.string.vendor_id));

        if (bConfigurationInfo == false)
        {
            return bConfigurationInfo;
        }
        bConfigurationInfo = sharedPref.contains(getString(R.string.vendor_password));
        if (bConfigurationInfo == false)
        {
            return bConfigurationInfo;
        }
        bConfigurationInfo = sharedPref.contains(getString(R.string.city_name));
        if (bConfigurationInfo == false)
        {
            return bConfigurationInfo;
        }
        bConfigurationInfo = sharedPref.contains(getString(R.string.distribution_company));
        if (bConfigurationInfo == false)
        {
            return bConfigurationInfo;
        }
        return bConfigurationInfo;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class VendorLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mVendorID;
        private final String mVendorPassword;

        private Context mContext;

        VendorLoginTask(Context context, String VendorID, String VendorPassword) {
            mVendorID = VendorID;
            mVendorPassword = VendorPassword;
            mContext = context;
        }

        @Override
        protected Boolean doInBackground(Void... params)
        {
            // TODO: attempt authentication against a network service.
            try
            {
                // Simulate network access.
                Thread.sleep(1000);
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
                String strVendorID = sharedPref.getString(getString(R.string.vendor_id), null);
                String strVendorPassword = sharedPref.getString(getString(R.string.vendor_password), null);


                if (mVendorID.equals(strVendorID) == false)
                {
                    return false;
                }
                if (mVendorPassword.equals(strVendorPassword) == false)
                {
                    return false;
                }
            }
            catch (InterruptedException e)
            {
                return false;
            }

            // TODO: register the new account here.
            return true;

        }

        @Override
        protected void onPostExecute(final Boolean success)
        {
            mAuthTask = null;

            if (!success)
            {
                mEditVendorPassword.setError(getString(R.string.error_incorrect_password));
                mEditVendorPassword.requestFocus();
            }
            else
            {
                Intent intentVendorFeatures = new Intent(mContext, VendorMenu.class);
                startActivity(intentVendorFeatures);
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }

    private class CustomerDatabaseAsync extends AsyncTask<ArrayList<CustomerInfo>, Void, Boolean>
    {
        Context m_Context;

        protected CustomerDatabaseAsync(Context context)
        {
            m_Context = context;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            //Perform pre-adding operation here.
        }

        @Override
        protected Boolean doInBackground(ArrayList<CustomerInfo>... params)
        {
            ArrayList<CustomerInfo> e = params[0];

            CustomerInfoDatabase.getInstance(m_Context).customerInfoDao().insertAllCustomerInfo(e);

            return Boolean.TRUE;
        }

        @Override
        protected void onPostExecute(Boolean RetCode)
        {
            super.onPostExecute(RetCode);
            attemptLogin();
        }
    }
}