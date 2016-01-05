package com.moc.smartmeterapp;

import android.app.Fragment;
import android.graphics.Color;

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
    public Limit limit1;
    public Limit limit2;
    public Limit limit3;

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
        limit1 = new Limit(180,250, 0);
        limit2 = new Limit(120,180, 0);
        limit3 = new Limit(0,120, 0);
    }


    public void addData( String addData) {
        String[] dataSplit = addData.split( ";");
        switch ( dataSplit[0]) {
            case "liveData": {
                if( dataSplit.length == 2) {
                    liveDataValue = dataSplit[1];
                } else {
                    // update limits
                    limit1.setMin( Integer.valueOf( dataSplit[2]));
                    limit1.setMax( Integer.valueOf( dataSplit[3]));
                    limit1.setColor( Integer.valueOf( dataSplit[4]));

                    limit2.setMin( Integer.valueOf( dataSplit[6]));
                    limit2.setMax( Integer.valueOf( dataSplit[7]));
                    limit2.setColor( Integer.valueOf( dataSplit[8]));

                    limit3.setMin( Integer.valueOf( dataSplit[10]));
                    limit3.setMax( Integer.valueOf( dataSplit[11]));
                    limit3.setColor( Integer.valueOf( dataSplit[12]));
                }
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