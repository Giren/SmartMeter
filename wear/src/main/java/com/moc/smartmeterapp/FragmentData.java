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

//    FragmentData( String fragmentData) {
//        String[] fragmentDataSplit = fragmentData.split(";");
//
//        liveDataValue = fragmentDataSplit[1];
//        limitWeekCurrentValue = fragmentDataSplit[2];
//        limitWeekLimitValue = fragmentDataSplit[4];
//        limitMonthCurrentValue  = fragmentDataSplit[5];
//        limitMonthLimitValue  = fragmentDataSplit[6];
//        limitYearCurrentValue = fragmentDataSplit[8];
//        limitYearLimitValue  = fragmentDataSplit[9];
//    }

    FragmentData() {
        this.liveDataValue = "0";
        this.limitWeekCurrentValue = "0";
        this.limitWeekLimitValue = "0";
        this.limitMonthCurrentValue  = "0";
        this.limitMonthLimitValue  = "0";
        this.limitYearCurrentValue = "0";
        this.limitYearLimitValue  = "0";
    }


    public void addData( String addData) {
        String[] dataSplit = addData.split( ";");
        switch ( dataSplit[0]) {
            case "liveData": {
                liveDataValue = dataSplit[1];
                break;
            }
            case "limitWeek": {
                limitWeekCurrentValue = dataSplit[1];
                limitWeekLimitValue = dataSplit[2];
                break;
            }
            case "limitMonth": {
                limitMonthCurrentValue  = dataSplit[1];
                limitMonthLimitValue  = dataSplit[2];
                break;
            }
            case "limitYear": {
                limitYearCurrentValue = dataSplit[1];
                limitYearLimitValue  = dataSplit[2];
                break;
            }
            case "Default": {
                break;
            }
        }
    }

    public void addLiveData( String currentValue) {
        liveDataValue = currentValue;
    }

    public void addLimitWeekData( String currentValue, String limitWeekValue) {
        limitWeekCurrentValue = currentValue;
        limitWeekLimitValue = limitWeekValue;
    }

    public void addLimitMonthData( String currentValue, String limitMonthValue) {
        limitMonthCurrentValue  = currentValue;
        limitMonthLimitValue  = limitMonthValue;
    }

    public void addLimitYearData( String currentValue, String limitYearValue) {
        limitYearCurrentValue = currentValue;
        limitYearLimitValue  = limitYearValue;
    }

    public String getLiveDataString() {
        return "livedata;" + this.liveDataValue;
    }

    public String getLimitWeekString() {
        return "limitweek;" + limitWeekCurrentValue + ";" + limitWeekLimitValue;
    }

    public String getLimitMonthString() {
        return "limitmonth;" + limitMonthCurrentValue + ";" + limitMonthLimitValue;
    }

    public String getLimitYearString() {
        return "limityear;" + limitYearCurrentValue + ";" + limitYearLimitValue;
    }

    public String getLiveDataValue() {
        return liveDataValue;
    }

    public void setLiveDataValue(String liveDataValue) {
        this.liveDataValue = liveDataValue;
    }

    public String getLimitWeekCurrentValue() {
        return limitWeekCurrentValue;
    }

    public void setLimitWeekCurrentValue(String limitWeekCurrentValue) {
        this.limitWeekCurrentValue = limitWeekCurrentValue;
    }

    public String getLimitWeekLimitValue() {
        return limitWeekLimitValue;
    }

    public void setLimitWeekLimitValue(String limitWeekLimitValue) {
        this.limitWeekLimitValue = limitWeekLimitValue;
    }

    public String getLimitMonthCurrentValue() {
        return limitMonthCurrentValue;
    }

    public void setLimitMonthCurrentValue(String limitMonthCurrentValue) {
        this.limitMonthCurrentValue = limitMonthCurrentValue;
    }

    public String getLimitMonthLimitValue() {
        return limitMonthLimitValue;
    }

    public void setLimitMonthLimitValue(String limitMonthLimitValue) {
        this.limitMonthLimitValue = limitMonthLimitValue;
    }

    public String getLimitYearCurrentValue() {
        return limitYearCurrentValue;
    }

    public void setLimitYearCurrentValue(String limitYearCurrentValue) {
        this.limitYearCurrentValue = limitYearCurrentValue;
    }

    public String getLimitYearLimitValue() {
        return limitYearLimitValue;
    }

    public void setLimitYearLimitValue(String limitYearLimitValue) {
        this.limitYearLimitValue = limitYearLimitValue;
    }

}