package com.comorinland.milkman.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by deepak on 17/5/18.
 */

public class MilkTypeInfo {

    public String mStrMilkType;
    public List<String> mListMilkQuantity;

    public MilkTypeInfo(String strMilkType,List<String> lstMilkQuantity)
    {
        mStrMilkType = strMilkType;
        mListMilkQuantity = new ArrayList<String>();
        mListMilkQuantity.addAll(lstMilkQuantity);
    }

}
