/************************************************************************************************
 File Name   : ExpandableCancelDeliveryListAdapter.java
 Purpose     : An expandable list adapter to customer_cancel monthly_delivery.
 Author      : Deepak J. Daniel
 @Copyright Grace Samadhanam Development Company.
 *************************************************************************************************
 */

package com.comorinland.milkman.customerapp;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.comorinland.milkman.R;

import java.util.ArrayList;
import java.util.List;


public class ExpandableCancelDeliveryListAdapter extends BaseExpandableListAdapter {
    private Context m_context;
    private List<Integer> listCancelChildViews;

    private static final int CHILD_TYPE_1 = 0;
    private static final int CHILD_TYPE_2 = 1;
    private static final int CHILD_TYPE_UNDEFINED = 2;

    // 3 Group types
    private static final int GROUP_TYPE_1 = 0;
    private static final int GROUP_TYPE_2 = 1;
    private static final int GROUP_TYPE_UNDEFINED = 2;

    // Boolean type
    private static Boolean bMonthlyCancel = false;

    public ExpandableCancelDeliveryListAdapter(Context m_context) {
        this.m_context = m_context;
        listCancelChildViews = new ArrayList<Integer>();
        listCancelChildViews.add(0, R.layout.activity_confirm_cancel_delivery_child_1);
        listCancelChildViews.add(1, R.layout.activity_confirm_cancel_delivery_child_2);
    }

    @Override
    public int getGroupCount() {
        return 2;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupPosition;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, final boolean isExpanded, View convertView, final ViewGroup viewGroup) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.expandable_list_cancel_delivery, null);
        }

        final TextView textView = (TextView) convertView.findViewById(R.id.txt_expandablelist_cancel);

        if (groupPosition == GROUP_TYPE_1) {
            textView.setText("Cancel for selected dates");
        } else if (groupPosition == GROUP_TYPE_2)
            textView.setText("Cancel monthly monthly_delivery");

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExpandableListView expandableListView = (ExpandableListView) viewGroup;
                if (!isExpanded) {
                    textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down_black_24dp, 0);
                    expandableListView.expandGroup(groupPosition);
                } else {
                    textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_up_black_24dp, 0);
                    expandableListView.collapseGroup(groupPosition);
                }
            }
        });

        convertView.setTag(getGroupType(groupPosition));
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean b, View convertView, final ViewGroup viewGroup) {
        Integer childType = new Integer(getChildType(groupPosition, childPosition));

        LayoutInflater inflater = (LayoutInflater) m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if ((convertView == null) || (convertView.getTag() != childType)) {
            switch (childType) {
                case CHILD_TYPE_1:
                    convertView = inflater.inflate(R.layout.activity_confirm_cancel_delivery_child_1, null);
                    TextView txtCancelText = (TextView) convertView.findViewById(R.id.txt_Cancel_Text);
                    txtCancelText.setText("Please select your dates: ");
                    convertView.setTag(childType);
                    break;

                case CHILD_TYPE_2:
                    convertView = inflater.inflate(R.layout.activity_confirm_cancel_delivery_child_2, null);
                    convertView.setTag(childType);
                    break;

                default:
                    break;
            }
        }

        ImageView imgView = (ImageView) convertView.findViewById(R.id.imgCalendarView);

        if (imgView != null) {
            imgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogFragment fragmentCancelDates = new CalendarFragment();
                    fragmentCancelDates.show(((Activity) m_context).getFragmentManager(), "Select Dates");
                }
            });
        }

        final Switch swSelector = (Switch) convertView.findViewById(R.id.switch_confirm_cancel_permanent);

        if (swSelector != null) {
            swSelector.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ExpandableListView expandableListView = (ExpandableListView) viewGroup;
                    View viewGroupCancel = expandableListView.findViewWithTag(getGroupType(groupPosition));
                    TextView txtCancelText = (TextView) viewGroupCancel.findViewById(R.id.txt_Cancel_Info);

                    if (swSelector.isChecked()) {
                        txtCancelText.setText("Cancel milk monthly_delivery from 5th Jan ");
                        txtCancelText.setVisibility(View.VISIBLE);
                        bMonthlyCancel = true;
                    } else {
                        txtCancelText.setVisibility(View.INVISIBLE);
                        bMonthlyCancel = false;
                    }
                }
            });
        }
        return convertView;
    }

    public Boolean getMonthlyCancelInfo() {
        return bMonthlyCancel;
    }

    @Override
    public int getChildType(int groupPosition, int childPosition) {
        int childType;

        switch (groupPosition) {
            case GROUP_TYPE_1:

                switch (childPosition) {
                    case 0:
                        childType = CHILD_TYPE_1;
                        break;

                    default:
                        childType = CHILD_TYPE_UNDEFINED;
                        break;
                }
                break;

            case GROUP_TYPE_2:

                switch (childPosition) {
                    case 0:
                        childType = CHILD_TYPE_2;
                        break;

                    default:
                        childType = CHILD_TYPE_UNDEFINED;
                        break;
                }
                break;

            default:
                childType = CHILD_TYPE_UNDEFINED;
                break;
        }
        return childType;
    }

    @Override
    public int getChildTypeCount() {
        return 2;       // Defined 2 child types (CHILD_TYPE_1, CHILD_TYPE_2)
    }

    @Override
    public int getGroupTypeCount() {
        return 2;       // Defined 2 groups types (GROUP_TYPE_1, GROUP_TYPE_2)
    }

    @Override
    public int getGroupType(int groupPosition) {
        switch (groupPosition) {
            case 0:
                return GROUP_TYPE_1;
            case 1:
                return GROUP_TYPE_2;
            default:
                return GROUP_TYPE_UNDEFINED;
        }
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
