package com.moc.smartmeterapp.model;

/**
 * Created by philipp on 14.12.2015.
 */
public class MinMeanMax {
    private Integer period;
    private Integer totalSum;
    private Integer min;
    private Integer mean;
    private Integer max;

    public Integer getPeriod() {
        return period;
    }
    public void setPeriod(Integer period) {
        this.period = period;
    }
    public Integer getMin() {
        return min;
    }
    public void setMin(Integer min) {
        this.min = min;
    }
    public Integer getTotalSum() {
        return totalSum;
    }
    public void setTotalSum(Integer totalSum) {
        this.totalSum = totalSum;
    }
    public Integer getMean() {
        return mean;
    }
    public void setMean(Integer mean) {
        this.mean = mean;
    }
    public Integer getMax() {
        return max;
    }
    public void setMax(Integer max) {
        this.max = max;
    }
}
