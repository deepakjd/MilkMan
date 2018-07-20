package com.comorinland.milkman.vendorapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.comorinland.milkman.R;
import com.comorinland.milkman.common.babushkatext.BabushkaText;

import java.util.ArrayList;

/**
 * Created by deepak on 5/5/18.
 */

public class VendorBillingListAdapter extends BaseAdapter
{
    private LayoutInflater mInflater;
    private Context mContext;
    private ArrayList<CustomerBillInfo> arrayListCustomerBillInfo;
    ArrayList<CustomerBillInfo> arrChangedBillStatus;
    String  mStrScreenType;

    public VendorBillingListAdapter(Context context, ArrayList<CustomerBillInfo> values, String strScreenType )
    {
        this.mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        arrayListCustomerBillInfo = new ArrayList<CustomerBillInfo>();
        arrayListCustomerBillInfo.addAll(values);
        arrChangedBillStatus = new ArrayList<CustomerBillInfo>();
        arrChangedBillStatus.clear();
        mStrScreenType = strScreenType;
    }

    public ArrayList<CustomerBillInfo> getSelectedApprovals()
    {
        return arrChangedBillStatus;
    }

        // 1
    @Override
    public int getCount() {
        return arrayListCustomerBillInfo.size();
    }

    // 2
    @Override
    public CustomerBillInfo getItem(int position) {
        return arrayListCustomerBillInfo.get(position);
    }

    // 3
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 4
    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        // Get view for row item
        View rowView = mInflater.inflate(R.layout.vendor_list_item_bill, parent, false);

        Typeface typefaceSemiBold = Typeface.createFromAsset(mContext.getAssets(), "font/JosefinSans-SemiBoldItalic.ttf");
        Typeface detailTypeFace = Typeface.createFromAsset(mContext.getAssets(), "font/Quicksand-Bold.ttf");
        BabushkaText babushkaTextCustomerNameId = (BabushkaText) rowView.findViewById(R.id.txt_customer_name_id);
        BabushkaText babushkaTextBillAmount = (BabushkaText) rowView.findViewById(R.id.txt_bill_amount);

        final CheckBox cbBillStatus = (CheckBox)rowView.findViewById(R.id.cb_paid_status);

        if (mStrScreenType == "VENDOR_UPDATE_BILL")
        {
            Drawable transparentDrawable = new ColorDrawable(Color.TRANSPARENT);
            cbBillStatus.setButtonDrawable(transparentDrawable);
        }

        CustomerBillInfo customerBillInfo = getItem(position);

        String strSetText = "";

        babushkaTextCustomerNameId.reset();
        babushkaTextCustomerNameId.setText(strSetText);

        strSetText = customerBillInfo.mCustomerID;
        babushkaTextCustomerNameId.addPiece(new BabushkaText.Piece.Builder("Customer ID : ").textColor(Color.parseColor("#8d8d8d")).typeFace(typefaceSemiBold).build());
        babushkaTextCustomerNameId.addPiece(new BabushkaText.Piece.Builder(strSetText + "\n").textColor(Color.parseColor("#bdbdbd")).typeFace(detailTypeFace).build());

        strSetText = customerBillInfo.mCustomerName;
        babushkaTextCustomerNameId.addPiece(new BabushkaText.Piece.Builder("Customer Name : ").textColor(Color.parseColor("#8d8d8d")).typeFace(typefaceSemiBold).build());
        babushkaTextCustomerNameId.addPiece(new BabushkaText.Piece.Builder(strSetText).textColor(Color.parseColor("#bdbdbd")).typeFace(detailTypeFace).build());
        babushkaTextCustomerNameId.display();

        Number numPrice = customerBillInfo.mPrice;
        babushkaTextBillAmount.addPiece(new BabushkaText.Piece.Builder("Bill : \n").textColor(Color.parseColor("#8d8d8d")).typeFace(typefaceSemiBold).build());
        babushkaTextBillAmount.addPiece(new BabushkaText.Piece.Builder("₹" + numPrice.toString()).textColor(Color.parseColor("#bdbdbd")).typeFace(detailTypeFace).build());
        babushkaTextBillAmount.display();

        String strBillStatus = customerBillInfo.mStatus;

        if (strBillStatus.equals("Paid"))
            cbBillStatus.setChecked(true);
        if (strBillStatus.equals("Unpaid"))
            cbBillStatus.setChecked(false);

        cbBillStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                CustomerBillInfo customerBillInfo = (CustomerBillInfo) getItem(position);
                String strBillStatus = customerBillInfo.mStatus;

                CustomerBillInfo eCustomerBillInfo = new CustomerBillInfo(customerBillInfo);

                if (cbBillStatus.isChecked())
                {
                    if (strBillStatus.equals("Unpaid"))
                    {
                        eCustomerBillInfo.mStatus = "Paid";
                    }
                }
                else
                {
                    if (strBillStatus.equals("Paid"))
                    {
                        eCustomerBillInfo.mStatus = "Unpaid";
                    }
                }
                arrChangedBillStatus.add(eCustomerBillInfo);
            }
        });
        return rowView;
    }
}