package com.moc.smartmeterapp.model;

/**
 * Created by philipp on 16.12.2015.
 */
public class Hour {
    private MinMeanMax mmm;

    public Hour() {
        mmm = new MinMeanMax();
    }

    public Hour(MinMeanMax mmm) {
        this.mmm = mmm;
    }

    public MinMeanMax getMmm() {
        return mmm;
    }

    public void setMmm(MinMeanMax mmm) {
        this.mmm = mmm;
    }
}
