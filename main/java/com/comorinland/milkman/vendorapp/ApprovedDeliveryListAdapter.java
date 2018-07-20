package com.comorinland.milkman.vendorapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.comorinland.milkman.R;
import com.comorinland.milkman.common.babushkatext.BabushkaText;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ApprovedDeliveryListAdapter extends BaseAdapter
{
    private LayoutInflater mInflater;
    private Context        mContext;
    private ArrayList<CustomerInfoDate> arrayListCustomerInfo;

    public ApprovedDeliveryListAdapter(Context context, ArrayList<CustomerInfoDate> values)
    {
        this.mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        arrayListCustomerInfo = new ArrayList<CustomerInfoDate>();
        arrayListCustomerInfo.addAll(values);
    }

    @Override
    public int getCount() {
        return arrayListCustomerInfo.size();
    }

    @Override
    public CustomerInfoDate getItem(int position) {
        return arrayListCustomerInfo.get(position);
    }

    //3
    @Override
    public long getItemId(int position) {
        return position;
    }

    //4
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        // Get view for row item
        View rowView = mInflater.inflate(R.layout.vendor_list_item_approved_deliveries, parent, false);

        Typeface typefaceSemiBold = Typeface.createFromAsset(mContext.getAssets(), "font/JosefinSans-SemiBoldItalic.ttf");
        Typeface detailTypeFace = Typeface.createFromAsset(mContext.getAssets(), "font/Quicksand-Bold.ttf");

        BabushkaText babushkaTextCustomerNameId = (BabushkaText) rowView.findViewById(R.id.txt_approved_delivery_customer_name_id);

        BabushkaText babushkaTextRequestDate  = (BabushkaText) rowView.findViewById(R.id.txt_day_scheduled_delivery);
        BabushkaText babushkaTextWeekDay      = (BabushkaText) rowView.findViewById(R.id.txt_weekday_scheduled_delivery);
        BabushkaText babushkaTextMonth        =  (BabushkaText) rowView.findViewById(R.id.txt_month_scheduled_delivery);

        String strSetText = "";

        babushkaTextCustomerNameId.reset();
        babushkaTextCustomerNameId.setText(strSetText);

        babushkaTextRequestDate.reset();
        babushkaTextRequestDate.setText(strSetText);

        babushkaTextWeekDay.reset();
        babushkaTextWeekDay.setText(strSetText);

        babushkaTextMonth.reset();
        babushkaTextMonth.setText(strSetText);

        final CustomerInfoDate customerInfoDate = (CustomerInfoDate) getItem(position);

        strSetText = customerInfoDate.CustomerID;
        babushkaTextCustomerNameId.addPiece(new BabushkaText.Piece.Builder("Customer ID : ").textColor(Color.parseColor("#8d8d8d")).typeFace(typefaceSemiBold).build());
        babushkaTextCustomerNameId.addPiece(new BabushkaText.Piece.Builder(strSetText + "\n").textColor(Color.parseColor("#bdbdbd")).typeFace(detailTypeFace).build());

        strSetText = customerInfoDate.CustomerName;
        babushkaTextCustomerNameId.addPiece(new BabushkaText.Piece.Builder("Customer Name : ").textColor(Color.parseColor("#8d8d8d")).typeFace(typefaceSemiBold).build());
        babushkaTextCustomerNameId.addPiece(new BabushkaText.Piece.Builder(strSetText).textColor(Color.parseColor("#bdbdbd")).typeFace(detailTypeFace).build());
        babushkaTextCustomerNameId.display();


        if (customerInfoDate.RequestDate != null) {
            Date dateRequest = customerInfoDate.RequestDate;
            SimpleDateFormat outFormat = new SimpleDateFormat("dd");

            babushkaTextRequestDate.addPiece(new BabushkaText.Piece.Builder(outFormat.format(dateRequest)).textColor(Color.parseColor("#8d8d8d")).typeFace(typefaceSemiBold).build());
            babushkaTextRequestDate.display();

            outFormat = new SimpleDateFormat("EE");
            babushkaTextWeekDay.addPiece(new BabushkaText.Piece.Builder(outFormat.format(dateRequest)).textColor(Color.parseColor("#8d8d8d")).typeFace(typefaceSemiBold).build());
            babushkaTextWeekDay.display();

            outFormat = new SimpleDateFormat("MMM");
            babushkaTextMonth.addPiece(new BabushkaText.Piece.Builder(outFormat.format(dateRequest)).textColor(Color.parseColor("#8d8d8d")).typeFace(typefaceSemiBold).build());
            babushkaTextMonth.display();
        }
        return rowView;
    }
}
