package com.comorinland.milkman.vendorapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;

import com.comorinland.milkman.R;
import com.comorinland.milkman.common.Constant;
import com.comorinland.milkman.common.CustomerDatabaseAsync;
import com.comorinland.milkman.common.CustomerInfo;
import com.comorinland.milkman.common.DataHandler;
import com.comorinland.milkman.common.DownloadFromAmazonDBTask;
import com.comorinland.milkman.common.MilkInfo;
import com.comorinland.milkman.common.ResponseHandler;
import com.comorinland.milkman.common.SharedHelper;
import com.comorinland.milkman.common.babushkatext.BabushkaText;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class VendorFragmentApproveDefaultDelivery extends Fragment implements DataHandler,ResponseHandler
{
    private ProgressDialog mProgressDialog;
    private HashMap<CustomerInfoDate, ArrayList<MilkInfo>> mapMilkInfo;
    ExpandableListView elApproveDeliveryListView;
    ArrayList<CustomerInfo> mlistCustomerInfo = new ArrayList<>();
    PlaceDefaultOrderListener mPlaceOrderListener;

    public VendorFragmentApproveDefaultDelivery()
    {
        // Required empty public constructor
        mapMilkInfo = new HashMap<>();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment VendorFragmentApproveDefaultDelivery.
     */

    // TODO: Rename and change types and number of parameters
    public static VendorFragmentApproveDefaultDelivery newInstance()
    {
        VendorFragmentApproveDefaultDelivery fragment = new VendorFragmentApproveDefaultDelivery();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.vendor_fragment_approve_default_delivery, container, false);
        return rootView;
    }

    private JsonObject jsonBuildInfo()
    {
        JsonObject jsonVendorObject = new JsonObject();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String strVendorId = sharedPref.getString(getString(R.string.vendor_id), null);

        jsonVendorObject.addProperty("VendorID", strVendorId);
        return jsonVendorObject;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        int actionBarHeight = 50;
        int marginInPx;

        super.onActivityCreated(savedInstanceState);

        Button btnApproveDelivery = (Button)getActivity().findViewById(R.id.btn_vendor_approve_default_delivery);
        mPlaceOrderListener = (PlaceDefaultOrderListener)getActivity();

        TypedValue tv = new TypedValue();

        if (getActivity().getTheme().resolveAttribute(R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) btnApproveDelivery.getLayoutParams();

        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        marginInPx = Math.round( 8 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        lp.setMargins(marginInPx,marginInPx,marginInPx,marginInPx + actionBarHeight);
        btnApproveDelivery.setLayoutParams(lp);

        btnApproveDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExpandableApproveListAdapter expandableApproveListAdapter = (ExpandableApproveListAdapter)elApproveDeliveryListView.getExpandableListAdapter();
                if (expandableApproveListAdapter != null)
                {
                    ArrayList<CustomerInfoDate> e = expandableApproveListAdapter.getSelectedApprovals();
                    if (e.isEmpty())
                    {
                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();

                        // Setting Dialog Title
                        alertDialog.setTitle("Alert");

                        // Setting Dialog Message
                        alertDialog.setMessage("You have not selected any items. Please select atleast one");

                        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Write your code here to execute after dialog    closed

                            }
                        });
                        alertDialog.show();
                    }
                    else
                        mPlaceOrderListener.HandlePlaceDefaultOrderClick(e);                }
            }
        });

        CheckBox cbApproveAllDefaultDeliveries = (CheckBox) getActivity().findViewById(R.id.cb_approve_all_default_deliveries);

        cbApproveAllDefaultDeliveries.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    int count = elApproveDeliveryListView.getChildCount();
                    for (int i = 0; i < count; i++) {
                        View v = elApproveDeliveryListView.getChildAt(i);
                        CheckBox cbApproveDelivery = (CheckBox) v.findViewById(R.id.cb_approve_milk);
                        cbApproveDelivery.setChecked(true);
                    }
                    ExpandableApproveListAdapter expandableApproveListAdapter = (ExpandableApproveListAdapter) elApproveDeliveryListView.getExpandableListAdapter();
                    if (expandableApproveListAdapter != null) {
                        expandableApproveListAdapter.ApproveAllDeliveries();
                    }
                }
                if (isChecked == false)
                {
                    int count = elApproveDeliveryListView.getChildCount();
                    for (int i = 0; i < count; i++)
                    {
                        View v = elApproveDeliveryListView.getChildAt(i);
                        CheckBox cbApproveDelivery = (CheckBox) v.findViewById(R.id.cb_approve_milk);
                        cbApproveDelivery.setChecked(false);
                    }
                    ExpandableApproveListAdapter expandableApproveListAdapter = (ExpandableApproveListAdapter) elApproveDeliveryListView.getExpandableListAdapter();
                    if (expandableApproveListAdapter != null)
                    {
                        expandableApproveListAdapter.RemoveAllDeliveries();
                    }
                }
            }
        });
        new CustomerDatabaseAsync(this, getActivity()).execute();
    }

    public void SaveCustomerInfo(List<CustomerInfo> lstCustomerInfo)
    {
        mlistCustomerInfo.addAll(lstCustomerInfo);

        // Create thread to get data from the server.
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        new DownloadFromAmazonDBTask(this, "VendorApp/GetDefaultDelivery.php",mProgressDialog).execute(jsonBuildInfo());
    }

    public String getCustomerNameFromId(String strCustomerId)
    {
        String strCustomerName = null;

        if (mlistCustomerInfo.isEmpty())
        {
            strCustomerName = "Name Unknown";
            return strCustomerName;
        }
        for (CustomerInfo xCustomerInfo : mlistCustomerInfo)
        {
            if (xCustomerInfo.CustomerID.equals(strCustomerId)) {
                strCustomerName = xCustomerInfo.CustomerName;
                break;
            }
            else
                strCustomerName = "Name Unknown";
        }

        return strCustomerName;
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
            JSONArray responseArray = new JSONArray(strResponse);

            for (int i = 0; i < responseArray.length(); i++)
            {
                JSONObject approveDeliveryObject = responseArray.getJSONObject(i);

                String strCustomerID = approveDeliveryObject.getString("CustomerID");

                String strCustomerName = getCustomerNameFromId(strCustomerID);

                CustomerInfoDate customerInfoDate = new CustomerInfoDate(strCustomerID, strCustomerName, null);

                JSONObject deliveryObject = approveDeliveryObject.getJSONObject("RequestedDelivery");

                Iterator<String> iter = deliveryObject.keys();

                ArrayList<MilkInfo> arrayListMilkInfo = new ArrayList<>();

                while (iter.hasNext())
                {
                    String key = iter.next();
                    if (key.contains("Packet"))
                    {
                        JSONObject jsonObjMilk = deliveryObject.getJSONObject(key);
                        MilkInfo milkInfoObject = new MilkInfo(jsonObjMilk.getString("Type"), jsonObjMilk.getInt("PacketNo"), jsonObjMilk.getString("Quantity"));
                        arrayListMilkInfo.add(milkInfoObject);
                    }
                }
                mapMilkInfo.put(customerInfoDate, arrayListMilkInfo);
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
            ExpandableApproveListAdapter expandableApproveListAdapter;

            elApproveDeliveryListView = (ExpandableListView) getActivity().findViewById(R.id.el_vendor_approve_default_delivery);

            expandableApproveListAdapter = new ExpandableApproveListAdapter(getActivity(), mapMilkInfo);

            elApproveDeliveryListView.setAdapter(expandableApproveListAdapter);

            elApproveDeliveryListView.setGroupIndicator(null);

            mapMilkInfo.clear();

            BabushkaText babushkaTextDefaultDelivery = (BabushkaText) getActivity().findViewById(R.id.txt_approve_default_delivery);
            babushkaTextDefaultDelivery.setText("Change in regular monthly delivery");

            CheckBox cbApproveDefaultDeliveries = (CheckBox) getActivity().findViewById(R.id.cb_approve_all_default_deliveries);
            cbApproveDefaultDeliveries.setVisibility(View.VISIBLE);
        }
        else if (strReturnCode.equals(Constant.INFO_NOT_FOUND))
        {
            CheckBox cbApproveDefaultDeliveries = (CheckBox) getActivity().findViewById(R.id.cb_approve_all_default_deliveries);
            BabushkaText babushkaTextDefaultDelivery = (BabushkaText) getActivity().findViewById(R.id.txt_approve_default_delivery);
            Button btnApproveDefaultDelivery = (Button)getActivity().findViewById(R.id.btn_vendor_approve_default_delivery);

            cbApproveDefaultDeliveries.setVisibility(View.INVISIBLE);
            babushkaTextDefaultDelivery.setText("There are no approval requests right now");
            btnApproveDefaultDelivery.setEnabled(false);
        }
        else
        {
            Intent intentSharedHelper = new Intent(getActivity(),VendorMenu.class);
            SharedHelper.showAlertDialog(getActivity(), "Problem in network connection", intentSharedHelper );
        }
    }
}
