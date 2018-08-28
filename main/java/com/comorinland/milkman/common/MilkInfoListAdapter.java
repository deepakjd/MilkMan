package com.comorinland.milkman.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.comorinland.milkman.R;
import com.comorinland.milkman.common.babushkatext.BabushkaText;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by deepak on 27/2/18.
 */

public class MilkInfoListAdapter extends BaseAdapter
{
    private final Context mContext;
    private final ArrayList<MilkInfo> arrayListMilkInfo;
    private LayoutInflater mInflater;
    private InternalFileStorageUtils mInternalFileStorageUtils;
    private String mStrZipFileName;

    public MilkInfoListAdapter(Context context, ArrayList<MilkInfo> values)
    {
        this.mContext = context;
        arrayListMilkInfo = new ArrayList<MilkInfo>();
        arrayListMilkInfo.addAll(values);
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mInternalFileStorageUtils = new InternalFileStorageUtils(mContext);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        mStrZipFileName = sharedPref.getString(mContext.getString(R.string.storage_s3_key), null);
    }

    @Override
    public int getCount() {
        return arrayListMilkInfo.size();
    }

    @Override
    public MilkInfo getItem(int position) {
        return arrayListMilkInfo.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View rowView;

        MilkInfo objMilkInfo = getItem(position);

        if (mInternalFileStorageUtils.AreMilkVarietyPhotosAvailable(mStrZipFileName) == Boolean.FALSE)
        {
            // Get view for row item
            rowView = mInflater.inflate(R.layout.listview_milkinfo_item, parent, false);
        }
        else
        {
            rowView = mInflater.inflate(R.layout.listview_milkinfo_item_image, parent, false);
            ImageView imgMilkVariety = (ImageView) rowView.findViewById(R.id.img_milk_variety);
            String strMilkVarietyName = objMilkInfo.getMilkType();
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
            String strImageName = sharedPref.getString(strMilkVarietyName,null);

            String strFullPathToImageFile = mInternalFileStorageUtils.GetFullPathFromImageName(mStrZipFileName,strImageName);
            File fileImage = new File(strFullPathToImageFile);

            if (fileImage.exists())
                Picasso.get().load(fileImage).fit().centerCrop().into(imgMilkVariety);
        }

        Typeface typefaceType = Typeface.createFromAsset(mContext.getAssets(), "font/JosefinSans-Bold.ttf");
        Typeface typefaceQuantity = Typeface.createFromAsset(mContext.getAssets(), "font/JosefinSans-SemiBoldItalic.ttf");
        Typeface detailTypeFace = Typeface.createFromAsset(mContext.getAssets(), "font/Quicksand-Bold.ttf");

        BabushkaText babushkaTextTypeView = (BabushkaText) rowView.findViewById(R.id.txt_milk_type_info);
        babushkaTextTypeView.addPiece(new BabushkaText.Piece.Builder(objMilkInfo.getMilkType()).typeFace(typefaceType).build());
        babushkaTextTypeView.display();

        BabushkaText babushkaTextQuantity = (BabushkaText) rowView.findViewById(R.id.txt_milk_quantity);
        babushkaTextQuantity.addPiece(new BabushkaText.Piece.Builder("Quantity :  ").textColor(Color.parseColor("#8d8d8d")).typeFace(typefaceQuantity).build());
        babushkaTextQuantity.addPiece(new BabushkaText.Piece.Builder(objMilkInfo.getQuantity()).textColor(Color.parseColor("#bdbdbd")).typeFace(detailTypeFace).build());
        babushkaTextQuantity.display();

        BabushkaText babushkaTextNumber = (BabushkaText) rowView.findViewById(R.id.txt_milk_packet_no);
        babushkaTextNumber.addPiece(new BabushkaText.Piece.Builder("No of packets :  ").textColor(Color.parseColor("#8d8d8d")).typeFace(typefaceQuantity).build());
        babushkaTextNumber.addPiece(new BabushkaText.Piece.Builder(String.valueOf(objMilkInfo.getPacketNumber())).textColor(Color.parseColor("#bdbdbd")).typeFace(detailTypeFace).build());
        babushkaTextNumber.display();

        return rowView;
        /* Just Checking the HEAD branch in git */
    }

}