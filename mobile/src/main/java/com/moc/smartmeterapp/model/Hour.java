package com.moc.smartmeterapp.model;

/**
 * Created by philipp on 16.12.2015.
 */
public class Hour {
    private MinMeanMax mmm;
    private int hour;

    public Hour() {
        mmm = new MinMeanMax();
        hour = 1;
    }

    public Hour(MinMeanMax mmm, int hour) {
        this.hour = hour;
        this.mmm = mmm;
    }

    public MinMeanMax getMmm() {
        return mmm;
    }

    public void setMmm(MinMeanMax mmm) {
        this.mmm = mmm;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }
}
