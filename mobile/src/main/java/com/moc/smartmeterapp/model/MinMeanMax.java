package com.moc.smartmeterapp.model;

/**
 * Created by philipp on 14.12.2015.
 */
public class MinMeanMax {
    private Integer totalSum;
    private Integer min;
    private Integer mean;
    private Integer max;

    public MinMeanMax(int totalSum, int min, int mean, int max) {
        this.totalSum = totalSum;
        this.min = min;
        this.mean = mean;
        this.max = max;
    }

    public MinMeanMax() {
        totalSum = 0;
        min = 0;
        mean = 0;
        max = 0;
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
