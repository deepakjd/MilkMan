package com.comorinland.milkman.vendorapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import com.comorinland.milkman.R;
import com.comorinland.milkman.common.babushkatext.BabushkaText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by deepak on 3/3/18.
 */

public class VendorCancelListAdapter extends ArrayAdapter<CustomerCancelInformation>
{
    ArrayList<CustomerCancelInformation> mArrCustomerCancelInformation;
    ArrayList<CustomerCancelInformation> arrSelectedApprovals = new ArrayList<>();
    Context mContext;
    Boolean mIsCheckBoxVisible = Boolean.TRUE;

    public VendorCancelListAdapter(Context context, ArrayList<CustomerCancelInformation> arrCancelInfo)
    {
        super(context, 0, arrCancelInfo);
        this.mContext = context;
        mArrCustomerCancelInformation = new ArrayList<CustomerCancelInformation>();
        mArrCustomerCancelInformation.addAll(arrCancelInfo);
        arrSelectedApprovals.clear();
        mIsCheckBoxVisible=Boolean.TRUE;
    }

    // Total number of types is the number of enum values
    @Override
    public int getViewTypeCount()
    {
        return 3;
    }

    public ArrayList<CustomerCancelInformation> getSelectedApprovals()
    {
        return arrSelectedApprovals;
    }

    public void ApproveAllCancellations()
    {
        arrSelectedApprovals.addAll(mArrCustomerCancelInformation);
    }

    public void RemoveAllCancellations()
    {
        arrSelectedApprovals.removeAll(mArrCustomerCancelInformation);
    }

    public void MakeCheckBoxesInvisible()
    {
        mIsCheckBoxVisible = Boolean.FALSE;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        int type;

        // Get the data item for this position
        final CustomerCancelInformation cancelInformation = getItem(position);
        type = cancelInformation.getType();

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null)
        {
            // Get the data item type for this position
            // Inflate XML layout based on the type
            convertView = getInflatedLayoutForType(type);

            if (type == 1)
            {
                convertView = FillViewSingleDateCancel(convertView, position);
            }
            if (type == 2)
            {
                convertView = FillViewMultipleDateCancel(convertView, position);
            }
            if (type == 3)
            {
                convertView = FillViewCancelPermanent(convertView, position);
            }
        }

        final CheckBox cb = (CheckBox) convertView.findViewById(R.id.cb_cancel_approve);

        if (!mIsCheckBoxVisible)
            cb.setVisibility(View.INVISIBLE);

        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (cb.isChecked())
                {
                    arrSelectedApprovals.add(cancelInformation);
                }
                else
                {
                    arrSelectedApprovals.remove(cancelInformation);
                }
            }
        });

        return convertView;
    }

    // Given the item type, responsible for returning the correct inflated XML layout file
    private View getInflatedLayoutForType(int type)
    {
        if (type == 1)
        {
            return LayoutInflater.from(getContext()).inflate(R.layout.vendor_list_item_cancel_single_date,null);
        }
        if (type == 2)
        {
            return LayoutInflater.from(getContext()).inflate(R.layout.vendor_list_item_cancel_multiple_date,null);
        }
        if (type == 3)
        {
            return LayoutInflater.from(getContext()).inflate(R.layout.vendor_list_item_cancel_permanent,null);
        }
        return null;
    }

    private View FillViewCancelPermanent(View convertView, int position)
    {
        Typeface typefaceJSBold = Typeface.createFromAsset(mContext.getAssets(), "font/JosefinSans-Bold.ttf");
        Typeface typefaceJSSemiBold = Typeface.createFromAsset(mContext.getAssets(), "font/JosefinSans-SemiBoldItalic.ttf");
        Typeface typeFaceQBold = Typeface.createFromAsset(mContext.getAssets(), "font/Quicksand-Bold.ttf");

        BabushkaText babushkaTextCustomerNameId   = (BabushkaText) convertView.findViewById(R.id.txt_approve_cancel_permanent_customer_id);

        CustomerCancelInformation cancelInformation = getItem(position);

        String strSetText = "";
        babushkaTextCustomerNameId.reset();
        babushkaTextCustomerNameId.setText(strSetText);

        strSetText=cancelInformation.CustomerID;
        babushkaTextCustomerNameId.addPiece(new BabushkaText.Piece.Builder("Customer ID : ").textColor(Color.parseColor("#8d8d8d")).typeFace(typefaceJSSemiBold).build());
        babushkaTextCustomerNameId.addPiece(new BabushkaText.Piece.Builder(strSetText + "\n").textColor(Color.parseColor("#bdbdbd")).typeFace(typeFaceQBold).build());

        strSetText = cancelInformation.CustomerName;
        babushkaTextCustomerNameId.addPiece(new BabushkaText.Piece.Builder("Customer Name : ").textColor(Color.parseColor("#8d8d8d")).typeFace(typefaceJSSemiBold).build());
        babushkaTextCustomerNameId.addPiece(new BabushkaText.Piece.Builder(strSetText).textColor(Color.parseColor("#bdbdbd")).typeFace(typeFaceQBold).build());
        babushkaTextCustomerNameId.display();

        BabushkaText babushkaTextDisplayStatus = (BabushkaText) convertView.findViewById(R.id.txt_approve_cancel_permanet);
        babushkaTextDisplayStatus.addPiece(new BabushkaText.Piece.Builder("Permanent").typeFace(typefaceJSBold).build());
        babushkaTextDisplayStatus.display();

        return convertView;
    }

    private View FillViewSingleDateCancel(View convertView, int position)
    {
        Typeface typefaceJSBold = Typeface.createFromAsset(mContext.getAssets(), "font/JosefinSans-Bold.ttf");
        Typeface typefaceSemiBold = Typeface.createFromAsset(mContext.getAssets(), "font/JosefinSans-SemiBoldItalic.ttf");
        Typeface detailTypeFace = Typeface.createFromAsset(mContext.getAssets(), "font/Quicksand-Bold.ttf");

        BabushkaText babushkaTextCustomerNameId   = (BabushkaText) convertView.findViewById(R.id.txt_approve_cancel_customer_name_id);

        BabushkaText babushkaTextRequestDate  = (BabushkaText) convertView.findViewById(R.id.txt_day_scheduled_delivery);
        BabushkaText babushkaTextWeekDay      = (BabushkaText) convertView.findViewById(R.id.txt_weekday_scheduled_delivery);
        BabushkaText babushkaTextMonth        =  (BabushkaText) convertView.findViewById(R.id.txt_month_scheduled_delivery);

        String strSetText = "";
        babushkaTextCustomerNameId.reset();
        babushkaTextCustomerNameId.setText(strSetText);

        babushkaTextRequestDate.reset();
        babushkaTextRequestDate.setText(strSetText);

        babushkaTextWeekDay.reset();
        babushkaTextWeekDay.setText(strSetText);

        babushkaTextMonth.reset();
        babushkaTextMonth.setText(strSetText);

        CustomerCancelInformation e = mArrCustomerCancelInformation.get(position);

        strSetText = e.CustomerID;
        babushkaTextCustomerNameId.addPiece(new BabushkaText.Piece.Builder("Customer ID : ").textColor(Color.parseColor("#8d8d8d")).typeFace(typefaceSemiBold).build());
        babushkaTextCustomerNameId.addPiece(new BabushkaText.Piece.Builder(strSetText + "\n").textColor(Color.parseColor("#bdbdbd")).typeFace(detailTypeFace).build());

        strSetText = e.CustomerName;
        babushkaTextCustomerNameId.addPiece(new BabushkaText.Piece.Builder("Customer Name : ").textColor(Color.parseColor("#8d8d8d")).typeFace(typefaceSemiBold).build());
        babushkaTextCustomerNameId.addPiece(new BabushkaText.Piece.Builder(strSetText).textColor(Color.parseColor("#bdbdbd")).typeFace(detailTypeFace).build());
        babushkaTextCustomerNameId.display();

        Date dateRequest = e.FromDate;
        SimpleDateFormat outFormat = new SimpleDateFormat("dd");

        babushkaTextRequestDate.addPiece(new BabushkaText.Piece.Builder(outFormat.format(dateRequest)).typeFace(typefaceJSBold).build());
        babushkaTextRequestDate.display();

        outFormat = new SimpleDateFormat("EE");
        babushkaTextWeekDay.addPiece(new BabushkaText.Piece.Builder(outFormat.format(dateRequest)).typeFace(typefaceJSBold).build());
        babushkaTextWeekDay.display();

        outFormat = new SimpleDateFormat("MMM");
        babushkaTextMonth.addPiece(new BabushkaText.Piece.Builder(outFormat.format(dateRequest)).typeFace(typefaceJSBold).build());
        babushkaTextMonth.display();

        return convertView;
    }

    private View FillViewMultipleDateCancel(View convertView, int position)
    {
        Typeface typefaceJSBold = Typeface.createFromAsset(mContext.getAssets(), "font/JosefinSans-Bold.ttf");
        Typeface typefaceSemiBold = Typeface.createFromAsset(mContext.getAssets(), "font/JosefinSans-SemiBoldItalic.ttf");
        Typeface typeFaceQBold = Typeface.createFromAsset(mContext.getAssets(), "font/Quicksand-Bold.ttf");

        BabushkaText babushkaTextCustomerNameId   = (BabushkaText) convertView.findViewById(R.id.txt_approve_cancel_customer_name_id);

        BabushkaText babushkaTextCancelFromDate  = (BabushkaText) convertView.findViewById(R.id.txt_cancel_approve_from_date);

        BabushkaText babushkaTextCancelToDate  = (BabushkaText) convertView.findViewById(R.id.txt_cancel_approve_to_date);

        String strSetText = "";
        babushkaTextCustomerNameId.reset();
        babushkaTextCustomerNameId.setText(strSetText);

        babushkaTextCancelFromDate.reset();
        babushkaTextCancelFromDate.setText(strSetText);

        babushkaTextCancelToDate.reset();
        babushkaTextCancelToDate.setText(strSetText);

        CustomerCancelInformation e = mArrCustomerCancelInformation.get(position);

        strSetText = e.CustomerID;
        babushkaTextCustomerNameId.addPiece(new BabushkaText.Piece.Builder("Customer ID : ").textColor(Color.parseColor("#8d8d8d")).typeFace(typefaceSemiBold).build());
        babushkaTextCustomerNameId.addPiece(new BabushkaText.Piece.Builder(strSetText + "\n").textColor(Color.parseColor("#bdbdbd")).typeFace(typeFaceQBold).build());

        strSetText = e.CustomerName;
        babushkaTextCustomerNameId.addPiece(new BabushkaText.Piece.Builder("Customer Name : ").textColor(Color.parseColor("#8d8d8d")).typeFace(typefaceSemiBold).build());
        babushkaTextCustomerNameId.addPiece(new BabushkaText.Piece.Builder(strSetText).textColor(Color.parseColor("#bdbdbd")).typeFace(typeFaceQBold).build());
        babushkaTextCustomerNameId.display();

        Date dateRequest = e.FromDate;
        SimpleDateFormat outFormat = new SimpleDateFormat("dd");

        strSetText = outFormat.format(dateRequest);
        outFormat = new SimpleDateFormat("MMM");
        strSetText = strSetText + "," + outFormat.format(dateRequest);

        babushkaTextCancelFromDate.addPiece(new BabushkaText.Piece.Builder(" From: ").typeFace(typefaceJSBold).build());
        babushkaTextCancelFromDate.addPiece(new BabushkaText.Piece.Builder(strSetText).textColor(Color.parseColor("#8d8d8d")).typeFace(typefaceSemiBold).build());

        babushkaTextCancelFromDate.display();

        dateRequest = e.ToDate;
        outFormat = new SimpleDateFormat("dd");

        strSetText = outFormat.format(dateRequest);
        outFormat = new SimpleDateFormat("MMM");
        strSetText = strSetText + "," + outFormat.format(dateRequest);

        babushkaTextCancelToDate.addPiece(new BabushkaText.Piece.Builder(" To:    ").typeFace(typefaceJSBold).build());
        babushkaTextCancelToDate.addPiece(new BabushkaText.Piece.Builder(strSetText).textColor(Color.parseColor("#8d8d8d")).typeFace(typefaceSemiBold).build());
        babushkaTextCancelToDate.display();

        return convertView;
    }
}