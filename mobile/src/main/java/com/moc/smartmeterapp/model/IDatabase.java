package com.moc.smartmeterapp.model;

import java.util.Date;
import java.util.List;

/**
 * Created by philipp on 16.12.2015.
 */
public interface IDatabase {
    Day loadDay(Date date);
    List<Day> loadMonth(Date date);
    List<Day> loadYear(Date date);

    void saveDay(Day day);
    void saveMonth(List<Day> days);
    void saveYear(List<Day> days);

    void deleteDay(Date date);
    void deleteMonth(Date date);
    void deleteYear(Date date);
    void deleteAll();
}
