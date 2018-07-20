package com.comorinland.milkman.common;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by deepak on 15/2/18.
 */
public class MilkInfo implements Parcelable {
    private String strMilkType;
    private Number numberPackets;
    private String strQuantity;

    public MilkInfo(String strMilkType, Number numberPackets, String strQuantity) {
        this.strMilkType = strMilkType;
        this.numberPackets = numberPackets;
        this.strQuantity = strQuantity;
    }

    public String getMilkType() {
        return strMilkType;
    }

    public Number getPacketNumber() {
        return numberPackets;
    }

    public String getQuantity() {
        return strQuantity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.strMilkType);
        dest.writeSerializable(this.numberPackets);
        dest.writeString(this.strQuantity);
    }

    protected MilkInfo(Parcel in)
    {
        this.strMilkType = in.readString();
        this.numberPackets = (Number) in.readSerializable();
        this.strQuantity = in.readString();
    }

    public static final Parcelable.Creator<MilkInfo> CREATOR = new Parcelable.Creator<MilkInfo>()
    {
        @Override
        public MilkInfo createFromParcel(Parcel source) {
            return new MilkInfo(source);
        }

        @Override
        public MilkInfo[] newArray(int size) {
            return new MilkInfo[size];
        }
    };
}
