package com.moc.smartmeterapp.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by philipp on 16.12.2015.
 */
public class Day {
    private List<Hour> hours;
    private Date date;

    public Day() {
        hours = new ArrayList<Hour>();
        date = new Date();
    }

    public Day(List<Hour> hours, Date date) {
        this.hours = hours;
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public MinMeanMax getMmm() {
        int mean = 0;
        int max = 0;
        int totalSum = 0;

        int temp = 0;

        for(Hour h : hours) {
            //TODO: Handle Min

            if((temp = h.getMmm().getMax()) > max)
                max = temp;

            mean += h.getMmm().getMean();

            totalSum += h.getMmm().getTotalSum();
        }

        mean /= hours.size();

        return new MinMeanMax(totalSum, 0, mean, max);
    }
}
