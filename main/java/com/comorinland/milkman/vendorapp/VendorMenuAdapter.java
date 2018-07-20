package com.comorinland.milkman.vendorapp;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.comorinland.milkman.R;


/**
 * Created by deepak on 11/6/18.
 */

public class VendorMenuAdapter extends BaseAdapter
{
    private LayoutInflater mInflater;
    private Context mContext;
    private static int[] arrayVendorMenuId = new int[]{R.drawable.vendor_add_customer,R.drawable.vendor_change_delivery, R.drawable.vendor_approve_delivery, R.drawable.vendor_billing_information};

    public VendorMenuAdapter(Context context)
    {
        this.mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // 1
    @Override
    public int getCount()
    {
        return arrayVendorMenuId.length;
    }

    // 2
    @Override
    public Object getItem(int i)
    {
        Integer obj = arrayVendorMenuId[i];
        return obj;
    }

    // 3
    @Override
    public long getItemId(int position)
    {
        return position;
    }

    // 4
    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        // Get view for row item
        View rowView = mInflater.inflate(R.layout.vendor_list_menu_item, parent, false);

        ImageView imgView = (ImageView)rowView.findViewById(R.id.imgVendorMenuItem);
        Integer iInteger = (Integer)getItem(position);

        imgView.setImageDrawable(ContextCompat.getDrawable(mContext,iInteger.intValue()));
        return rowView;
    }
}