/************************************************************************************************
 File Name   : ExpandableMilkInfoListAdapter.java
 Purpose     : An expandable list adapter for selecting milk information. This adapter is used by
 both "Change Default Delivery" and "Modify Milk Info" feature actions.
 Author      : Deepak J. Daniel
 @Copyright Grace Samadhanam Development Company.
 *************************************************************************************************
 */

package com.comorinland.milkman.common;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.comorinland.milkman.R;
import com.comorinland.milkman.common.babushkatext.BabushkaText;


/**
 * Created by deepak on 11/8/17.
 */

public class ExpandableMilkInfoListAdapter extends BaseExpandableListAdapter
{
    private Context mContext;
    private List<MilkTypeInfo> mListMilkType;
    private HashMap<String, ArrayList<MilkInfo>> mHashmapMilkInfo;
    private String mStrPreviouslyCheckedTag;

    public ExpandableMilkInfoListAdapter(Context context, List<MilkTypeInfo> listMilktype)
    {
        mContext = context;
        mListMilkType = new ArrayList<>();
        mListMilkType.addAll(listMilktype);

        mHashmapMilkInfo = new HashMap<String, ArrayList<MilkInfo>>();

        for (int i = 0; i < mListMilkType.size(); i++)
        {
            ArrayList<MilkInfo> e = new ArrayList<MilkInfo>();
            mHashmapMilkInfo.put(mListMilkType.get(i).mStrMilkType, e);
        }
        mStrPreviouslyCheckedTag="";
    }

    public boolean isMilkDeliveryPresent()
    {
        boolean entryAvailable = false;

        for (int i = 0; i < mListMilkType.size(); i++)
        {
            ArrayList<MilkInfo> e = mHashmapMilkInfo.get(getGroup(i));
            if (e.isEmpty() == false)
                entryAvailable = true;
        }
        return entryAvailable;
    }

    public HashMap<String, ArrayList<MilkInfo>> getMilkInfo() {
        return mHashmapMilkInfo;
    }

    private int getGroupPositionFromTag(String strTag)
    {
        int groupPosition = -1;
        for(int i = 0; i< mListMilkType.size(); i++)
        {
            if (strTag.equals(mListMilkType.get(i).mStrMilkType))
            {
                groupPosition = i;
                break;
            }
        }
        return groupPosition;
    }
    @Override
    public int getGroupCount() {
        return mListMilkType.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mListMilkType.get(groupPosition).mStrMilkType;
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
    public View getGroupView(final int groupPosition, final boolean isExpanded, View convertView, final ViewGroup viewGroup)
    {
        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.expandable_list_milk_type, null);
        }

        RadioButton radioButtonMilkType = (RadioButton) convertView.findViewById(R.id.rb_milk_type);

        FloatingActionButton floatingActionButton = (FloatingActionButton) convertView.findViewById(R.id.fab_expandable_milk_info);

        BabushkaText babushkaTextDisplayInfo = (BabushkaText) convertView.findViewById(R.id.text_expandable_milk_info);

        String strModifyMilkInfo = "";
        babushkaTextDisplayInfo.reset();
        babushkaTextDisplayInfo.setText(strModifyMilkInfo);

        final ArrayList<MilkInfo> e = mHashmapMilkInfo.get(getGroup(groupPosition));

        for (int i = 0; i < e.size(); i++)
        {
            String strMilkInfo;
            MilkInfo milkInfo = e.get(i);

            strMilkInfo = milkInfo.getPacketNumber() + " Packet of " + milkInfo.getQuantity() + "\n";
            strModifyMilkInfo += strMilkInfo;
        }

        babushkaTextDisplayInfo.addPiece(new BabushkaText.Piece.Builder(strModifyMilkInfo).textColor(Color.parseColor("#8d8d8d")).build());
        babushkaTextDisplayInfo.display();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BabushkaText txtView = (BabushkaText) view.getTag();
                txtView.setText("");
                e.clear();
                view.setVisibility(View.INVISIBLE);
            }
        });

        radioButtonMilkType.setText((String)getGroup(groupPosition));
        radioButtonMilkType.setChecked(false);
        if(mStrPreviouslyCheckedTag.equals(getGroup(groupPosition)))
        {
            radioButtonMilkType.setChecked(true);
        }
        radioButtonMilkType.setTextColor(Color.parseColor("#8d8d8d"));

        radioButtonMilkType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExpandableListView expandableListView = (ExpandableListView) viewGroup;
                if (!isExpanded) {
                    expandableListView.expandGroup(groupPosition);

                    if (mStrPreviouslyCheckedTag.isEmpty() == false) {
                        View viewGroup = expandableListView.findViewWithTag(mStrPreviouslyCheckedTag);
                        if (viewGroup != null)
                        {
                            int tagPosition = getGroupPositionFromTag(mStrPreviouslyCheckedTag);
                            if (tagPosition != -1)
                                expandableListView.collapseGroup(tagPosition);
                            RadioButton radioButtonChecked = (RadioButton) viewGroup.findViewById(R.id.rb_milk_type);
                            if (radioButtonChecked.isChecked())
                                radioButtonChecked.setChecked(false);
                        }
                    }
                    mStrPreviouslyCheckedTag = (String) getGroup(groupPosition);
                }
                else {
                    expandableListView.collapseGroup(groupPosition);
                    RadioButton rbMilkType = (RadioButton)view.findViewById(R.id.rb_milk_type);
                    rbMilkType.setChecked(false);
                    mStrPreviouslyCheckedTag = "";
                }
            }
        });

        String tag = (String) getGroup(groupPosition);
        convertView.setTag(tag);

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, int childPosition, boolean b, View convertView, final ViewGroup viewGroup)
    {
        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.expandable_list_milk_item, null);
        }

        ElegantNumberButton elegantNumberButton = (ElegantNumberButton) convertView.findViewById(R.id.elegant_button_number);
        elegantNumberButton.setRange(1, 5);

        Spinner spinnerQuantity = (Spinner) convertView.findViewById(R.id.spinner_milk_quantity);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mContext, R.layout.sample_spinner_item, mListMilkType.get(groupPosition).mListMilkQuantity);
        arrayAdapter.setDropDownViewResource(R.layout.sample_spinner_item);
        spinnerQuantity.setAdapter(arrayAdapter);

        Button buttonPlaceOrder = (Button) convertView.findViewById(R.id.button_modify_order);

        buttonPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ExpandableListView expandableListView = (ExpandableListView) viewGroup;
                View viewGroup = expandableListView.findViewWithTag(getGroup(groupPosition));
                ViewGroup viewChild = (ViewGroup) view.getParent();

                BabushkaText babushkaTextDisplayInfo = (BabushkaText) viewGroup.findViewById(R.id.text_expandable_milk_info);

                if (viewChild != null)
                {
                    ElegantNumberButton elegantNumberButton = (ElegantNumberButton) viewChild.findViewById(R.id.elegant_button_number);
                    Spinner spinnerQuantity = (Spinner) viewChild.findViewById(R.id.spinner_milk_quantity);

                    int numMilkPackets = elegantNumberButton.getNumberInt();
                    String strMilkQuantity = (String) spinnerQuantity.getSelectedItem();

                    MilkInfo milkInfo = new MilkInfo((String) getGroup(groupPosition), numMilkPackets, strMilkQuantity);

                    ArrayList<MilkInfo> e = mHashmapMilkInfo.get(getGroup(groupPosition));
                    e.add(milkInfo);

                    mHashmapMilkInfo.put((String) getGroup(groupPosition), e);
                }

                ArrayList<MilkInfo> e = mHashmapMilkInfo.get(getGroup(groupPosition));
                String strModifyMilkInfo = "";
                babushkaTextDisplayInfo.reset();
                babushkaTextDisplayInfo.setText(strModifyMilkInfo);

                for (int i = 0; i < e.size(); i++) {
                    String strMilkInfo;
                    MilkInfo milkInfo = e.get(i);
                    strMilkInfo = milkInfo.getPacketNumber() + " Packet of " + milkInfo.getQuantity() + "\n";
                    strModifyMilkInfo += strMilkInfo;
                }
                babushkaTextDisplayInfo.setText(strModifyMilkInfo);

                FloatingActionButton floatingActionButton = (FloatingActionButton) viewGroup.findViewById(R.id.fab_expandable_milk_info);
                if (strModifyMilkInfo.isEmpty() == false)
                {
                    floatingActionButton.setTag(babushkaTextDisplayInfo);
                    floatingActionButton.setVisibility(View.VISIBLE);
                }
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}