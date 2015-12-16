package com.moc.smartmeterapp.database;

import com.moc.smartmeterapp.model.Day;

import java.util.Date;
import java.util.List;

/**
 * Created by philipp on 16.12.2015.
 */
public class DatabaseHelper implements IDatabase {
    @Override
    public Day loadDay(Date date) {
        return null;
    }

    @Override
    public List<Day> loadMonth(Date date) {
        return null;
    }

    @Override
    public List<Day> loadYear(Date date) {
        return null;
    }

    @Override
    public void saveDay(Day day) {

    }

    @Override
    public void saveMonth(List<Day> days) {

    }

    @Override
    public void saveYear(List<Day> days) {

    }

    @Override
    public void deleteDay(Date date) {

    }

    @Override
    public void deleteMonth(Date date) {

    }

    @Override
    public void deleteYear(Date date) {

    }

    @Override
    public void deleteAll() {

    }
}
