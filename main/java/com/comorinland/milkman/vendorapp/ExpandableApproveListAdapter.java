package com.comorinland.milkman.vendorapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import com.comorinland.milkman.R;
import com.comorinland.milkman.common.MilkInfo;
import com.comorinland.milkman.common.MilkInfoListAdapter;
import com.comorinland.milkman.common.babushkatext.BabushkaText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by deepak on 20/2/18.
*/

public class ExpandableApproveListAdapter extends BaseExpandableListAdapter
{
    Context m_context;
    HashMap<CustomerInfoDate,ArrayList<MilkInfo>> m_MilkInfoHashMap;
    ArrayList<CustomerInfoDate> arrSelectedApprovals = new ArrayList<>();

    public ExpandableApproveListAdapter(Context context, HashMap<CustomerInfoDate,ArrayList<MilkInfo>> milkInfo)
    {
        this.m_context = context;
        m_MilkInfoHashMap = new HashMap<>();
        m_MilkInfoHashMap.clear();
        m_MilkInfoHashMap.putAll(milkInfo);
        arrSelectedApprovals.clear();
    }

    public ArrayList<CustomerInfoDate> getSelectedApprovals()
    {
        return arrSelectedApprovals;
    }

    @Override
    public int getGroupCount()
    {
        return m_MilkInfoHashMap.size();
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition)
    {
        Set<CustomerInfoDate> key = m_MilkInfoHashMap.keySet();
        Iterator<CustomerInfoDate> iter = key.iterator();
        int count = 0;
        CustomerInfoDate customerInfoDate = null;

        while (iter.hasNext())
        {
            customerInfoDate = iter.next();

            if (count == groupPosition)
            {
                break;
            }
            count++;
        }
        return customerInfoDate;
    }

    public int getChildCount(int groupPosition)
    {
        int count;

        ArrayList<MilkInfo> arrListMilkInfo;

        CustomerInfoDate customerInfoDate = (CustomerInfoDate)getGroup(groupPosition);

        arrListMilkInfo = m_MilkInfoHashMap.get(customerInfoDate);

        count = arrListMilkInfo.size();

        return count;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        ArrayList<MilkInfo> arrListMilkInfo;

        CustomerInfoDate customerInfoDate = (CustomerInfoDate)getGroup(groupPosition);

        arrListMilkInfo = m_MilkInfoHashMap.get(customerInfoDate);

        return arrListMilkInfo;
    }

    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return childPosition;
    }

    @Override
    public boolean hasStableIds()
    {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, final boolean isExpanded, View convertView, final ViewGroup viewGroup)
    {
        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.vendor_expandable_approve_group, null);
        }

        Typeface typefaceSemiBold = Typeface.createFromAsset(m_context.getAssets(), "font/JosefinSans-SemiBoldItalic.ttf");
        Typeface detailTypeFace = Typeface.createFromAsset(m_context.getAssets(), "font/Quicksand-Bold.ttf");

        BabushkaText babushkaTextCustomerNameId = (BabushkaText) convertView.findViewById(R.id.txt_approve_customer_name_id);

        BabushkaText babushkaTextRequestDate  = (BabushkaText) convertView.findViewById(R.id.txt_day_approval_delivery);
        BabushkaText babushkaTextWeekDay      = (BabushkaText) convertView.findViewById(R.id.txt_weekday_approval_delivery);
        BabushkaText babushkaTextMonth        =  (BabushkaText) convertView.findViewById(R.id.txt_month_approval_delivery);

        String strSetText = "";

        babushkaTextCustomerNameId.reset();
        babushkaTextCustomerNameId.setText(strSetText);

        babushkaTextRequestDate.reset();
        babushkaTextRequestDate.setText(strSetText);

        babushkaTextWeekDay.reset();
        babushkaTextWeekDay.setText(strSetText);

        babushkaTextMonth.reset();
        babushkaTextMonth.setText(strSetText);

        final CustomerInfoDate customerInfoDate = (CustomerInfoDate) getGroup(groupPosition);

        strSetText = customerInfoDate.CustomerID;
        babushkaTextCustomerNameId.addPiece(new BabushkaText.Piece.Builder("Customer ID : ").textColor(Color.parseColor("#8d8d8d")).typeFace(typefaceSemiBold).build());
        babushkaTextCustomerNameId.addPiece(new BabushkaText.Piece.Builder(strSetText + "\n").textColor(Color.parseColor("#bdbdbd")).typeFace(detailTypeFace).build());

        strSetText = customerInfoDate.CustomerName;
        babushkaTextCustomerNameId.addPiece(new BabushkaText.Piece.Builder("Customer Name : ").textColor(Color.parseColor("#8d8d8d")).typeFace(typefaceSemiBold).build());
        babushkaTextCustomerNameId.addPiece(new BabushkaText.Piece.Builder(strSetText).textColor(Color.parseColor("#bdbdbd")).typeFace(detailTypeFace).build());
        babushkaTextCustomerNameId.display();

        if (customerInfoDate.RequestDate != null)
        {
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

        final TextView tv = (TextView) convertView.findViewById(R.id.txt_expndable_view);

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExpandableListView expandableListView = (ExpandableListView) viewGroup;
                if (!isExpanded)
                {
                    tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down_black_24dp, 0);
                    expandableListView.expandGroup(groupPosition);
                }
                else
                {
                    tv.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_up_black_24dp, 0);
                    expandableListView.collapseGroup(groupPosition);
                }
            }
        });

        final CheckBox cb = (CheckBox) convertView.findViewById(R.id.cb_approve_milk);

        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (cb.isChecked())
                {
                    arrSelectedApprovals.add(customerInfoDate);
                }
                else
                {
                    arrSelectedApprovals.remove(customerInfoDate);
                }
            }
        });

        String tag = (String) ((CustomerInfoDate) getGroup(groupPosition)).CustomerID;
        convertView.setTag(tag);

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean b, View convertView, final ViewGroup viewGroup)
    {
        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.vendor_expandable_approve_child, null);
        }

        ListView lstMilkInfo = (ListView) convertView.findViewById(R.id.list_milk_info);

        lstMilkInfo.setAdapter(new MilkInfoListAdapter(m_context,(ArrayList<MilkInfo>) getChild(groupPosition,childPosition)));
        MilkInfoListAdapter listadp = (MilkInfoListAdapter)lstMilkInfo.getAdapter();

        if (listadp != null)
        {
            int totalHeight = 0;

            for (int i = 0; i < listadp.getCount(); i++)
            {
                View listItem = listadp.getView(i, null, lstMilkInfo);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }

            ViewGroup.LayoutParams params = lstMilkInfo.getLayoutParams();
            params.height = totalHeight + (lstMilkInfo.getDividerHeight() * (listadp.getCount() - 1));
            lstMilkInfo.setLayoutParams(params);
            lstMilkInfo.requestLayout();
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return true;
    }
}