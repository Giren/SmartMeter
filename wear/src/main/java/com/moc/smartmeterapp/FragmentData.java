package com.moc.smartmeterapp;

import android.app.Fragment;

/**
 * Created by David on 28.12.2015.
 */
public class FragmentData {

    private String liveDataValue;
    private String limitWeekCurrentValue;
    private String limitWeekLimitValue;
    private String limitMonthCurrentValue;
    private String limitMonthLimitValue;
    private String limitYearCurrentValue;
    private String limitYearLimitValue;

    FragmentData( String fragmentData) {
        String[] fragmentDataSplit = fragmentData.split(";");

        liveDataValue = fragmentDataSplit[1];
        limitWeekCurrentValue = fragmentDataSplit[2];
        limitWeekLimitValue = fragmentDataSplit[4];
        limitMonthCurrentValue  = fragmentDataSplit[5];
        limitMonthLimitValue  = fragmentDataSplit[6];
        limitYearCurrentValue = fragmentDataSplit[8];
        limitYearLimitValue  = fragmentDataSplit[9];
    }
}
