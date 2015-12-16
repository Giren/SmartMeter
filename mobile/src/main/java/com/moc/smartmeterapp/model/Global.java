package com.moc.smartmeterapp.model;

import java.util.ArrayList;

/**
 * Created by philipp on 14.12.2015.
 */
public class Global {
    private MinMeanMax characteristics;
    private ArrayList<Limit> limits;

    public Global() {
        characteristics = new MinMeanMax();
        limits = new ArrayList<Limit>();
    }

    public Integer getMean() {
        return characteristics.getMean();
    }
    public void setMean(Integer mean) {
        characteristics.setMean(mean);
    }
    public Integer getMax() {
        return characteristics.getMax();
    }
    public void setMax(Integer max) {
        characteristics.setMax(max);
    }
    public ArrayList<Limit> getLimits() {
        return limits;
    }
    public void setLimits(ArrayList<Limit> limits) {
        this.limits = limits;
    }
}
