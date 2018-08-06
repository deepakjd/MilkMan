/************************************************************************************************
    File Name   : MilkInfoFragment.java
    Purpose     : The fragment implements a recycler adapter for milk information card views.
                  It also implements an async task that downloads milk information for seven
                  consecutive days inclusive of the current selected date from the server.
                  This file implements two classes "MilkInfoCardAdapter" and "MilkInfoFragment".
    Author      : Deepak J. Daniel
                  @Copyright Grace Samadhanam Development Company.
 *************************************************************************************************
 */

package com.comorinland.milkman.common;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.comorinland.milkman.R;
import com.comorinland.milkman.customerapp.DailyMilkInfoModify;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import com.comorinland.milkman.common.babushkatext.BabushkaText;

class MilkInfoCardAdapter extends RecyclerView.Adapter<MilkInfoCardAdapter.MilkInfoCardHolder>
{
    private HashMap<String, ArrayList<MilkInfo>> mapCloneMilkInfo;
    private ArrayList<String> key;
    private Context m_context;
    private String mAppType;
    private String mCustomerID;

    public MilkInfoCardAdapter(Context context, HashMap<String, ArrayList<MilkInfo>> info, String appType,String customerID)
    {
        this.m_context = context;
        mapCloneMilkInfo = new HashMap<>();
        mapCloneMilkInfo.putAll(info);
        key = new ArrayList<>();
        key.addAll(mapCloneMilkInfo.keySet());
        Collections.sort(key);
        mAppType = appType;
        mCustomerID = customerID;
    }

    public static class MilkInfoCardHolder extends RecyclerView.ViewHolder
    {
        CardView cv;

        MilkInfoCardHolder(CardView v)
        {
            super(v);
            cv = v;
        }
    }

    @Override
    public MilkInfoCardHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        CardView cv;
        cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_milk_info, parent, false);
        return new MilkInfoCardHolder(cv);
    }

    @Override
    public void onBindViewHolder(MilkInfoCardHolder holder, int position)
    {
        CardView cvMilkInfo = holder.cv;

        final String keyDate = key.get(position);

        ArrayList<MilkInfo> arrayListMilkInfo = mapCloneMilkInfo.get(keyDate);
        ListView lstMilkInfo = (ListView) cvMilkInfo.findViewById(R.id.list_milk_info);
        lstMilkInfo.setAdapter(new MilkInfoListAdapter(m_context, arrayListMilkInfo));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        Date dateQueryRequest = null;

        try
        {
            dateQueryRequest = sdf.parse(keyDate);
        }
        catch (ParseException e)
        {
            return;
        }

        sdf = new SimpleDateFormat("MMM dd, yyyy");
        String strQueryDate = sdf.format(dateQueryRequest);

        BabushkaText babushkaTextDateHeader = (BabushkaText) cvMilkInfo.findViewById(R.id.txt_milk_info_date);
        babushkaTextDateHeader.reset();
        babushkaTextDateHeader.setText("");
        babushkaTextDateHeader.addPiece(new BabushkaText.Piece.Builder(strQueryDate).style(Typeface.NORMAL).build());
        babushkaTextDateHeader.display();

        FloatingActionButton fabMilkInfo = (FloatingActionButton) cvMilkInfo.findViewById(R.id.fab_milk_info);

        Date dateCurrentDate = Calendar.getInstance().getTime();
        if (dateQueryRequest.before(dateCurrentDate))
            fabMilkInfo.setVisibility(View.INVISIBLE);

        fabMilkInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentDailyMilkInfo = new Intent(m_context, DailyMilkInfoModify.class);
                intentDailyMilkInfo.putExtra("DateOfModification", keyDate);
                intentDailyMilkInfo.putExtra("AppType",mAppType);
                intentDailyMilkInfo.putExtra("CustomerID",mCustomerID);
                m_context.startActivity(intentDailyMilkInfo);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return mapCloneMilkInfo.keySet().size();
    }

}

/**
 * A simple {@link Fragment} subclass.
 */

public class MilkInfoFragment extends Fragment implements ResponseHandler
{
    private HashMap<String, ArrayList<MilkInfo>> mapMilkInfo;
    private ProgressDialog mProgressDialog;

    public MilkInfoFragment()
    {
        // Required empty public constructor
        mapMilkInfo = new HashMap<>();
    }

    public static MilkInfoFragment newInstance(String date, String customerID, String appType)
    {
        MilkInfoFragment milkInfoFragment = new MilkInfoFragment();

        Bundle args = new Bundle();

        args.putString("Date", date);
        args.putString("CustomerID", customerID);
        args.putString("AppType", appType);

        milkInfoFragment.setArguments(args);

        return milkInfoFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Create thread to get data from the server.
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        new DownloadFromAmazonDBTask(this, "CustomerApp/GetMilkDelivery.php", mProgressDialog).execute(jsonBuildInfo());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        RecyclerView DisplayRecyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_display_milk_info, container, false);
        return DisplayRecyclerView;
    }

    private JsonObject jsonBuildInfo()
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String strVendorID = sharedPref.getString(getString(R.string.vendor_id), null);

        // Get the arguments
        Bundle args = getArguments();

        String strDate = args.getString("Date");

        String strCustomerID = args.getString("CustomerID");

        JsonObject jsonCustomerObject = new JsonObject();

        jsonCustomerObject.addProperty("VendorID", strVendorID);
        jsonCustomerObject.addProperty("CustomerID", strCustomerID);
        jsonCustomerObject.addProperty("QueryDate", strDate);

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
            JSONArray responseArray = new JSONArray(strResponse);

            for (int i = 0; i < responseArray.length(); i++)
            {

                JSONObject dataObject = responseArray.getJSONObject(i);

                String strDate = dataObject.getString("Date");

                if (strDate.isEmpty() || strDate.equals(null))
                {
                    return Constant.JSON_EXCEPTION;
                }

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                Date dateSelected = null;

                try
                {
                    dateSelected = sdf.parse(strDate);
                }
                catch (ParseException e)
                {
                    return Constant.JSON_EXCEPTION;
                }

                String strDisplayDateString = sdf.format(dateSelected);

                JSONObject deliveryObject = dataObject.getJSONObject("Delivery");

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

                mapMilkInfo.put(strDisplayDateString, arrayListMilkInfo);
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
        // Get the arguments
        Bundle args = getArguments();

        String strAppType = args.getString("AppType");
        String strCustomerID = args.getString("CustomerID");

        if (strReturnCode.equals(Constant.JSON_SUCCESS) == Boolean.FALSE)
        {
            if (strReturnCode.equals(Constant.INFO_NOT_FOUND))
            {
                SharedHelper.showAlertDialog(getActivity(), "Milk Information not available for this date",null);
            }
            else if (strReturnCode.equals(Constant.RESPONSE_UNAVAILABLE))
            {
                SharedHelper.showAlertDialog(getActivity(), "Please check your connection",null);
            }
            else    // Setting Dialog Message
                SharedHelper.showAlertDialog(getActivity(), "Sorry for the problem. Please contact your adminstrator", null);
            return;
        }

        RecyclerView DisplayRecyclerView = (RecyclerView) getView().findViewById(R.id.recycler_display);
        MilkInfoCardAdapter adapter;

        adapter = new MilkInfoCardAdapter(getActivity(), mapMilkInfo, strAppType, strCustomerID);
        DisplayRecyclerView.setAdapter(adapter);

        DisplayRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
    }
}