package com.moc.smartmeterapp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by philipp on 14.12.2015.
 */
public class DataObject {
    private List<Day> days;

    public DataObject(List<Day> days) {
        this.days = days;
    }

    public DataObject() {
        days = new ArrayList<Day>();
    }

    public List<Day> getDays() {
        return days;
    }

    public void setDays(List<Day> days) {
        this.days = days;
    }
}
