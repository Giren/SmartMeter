package com.moc.smartmeterapp.model;

/**
 * Created by michael on 22.12.15.
 */

public class MyPreferences {
    private Integer weekLimit;
    private String weekLimitColor;
    private Integer monthLimit;
    private String monthLimitColor;
    private Integer yearLimit;
    private String yearLimitColor;

    private String ipAddress;
    private Boolean notification;

    public MyPreferences(Integer weekLimit, String weekLimitColor, Integer monthLimit,
                         String monthLimitColor, Integer yearLimit, String yearLimitColor,
                         String ipAddress, Boolean notification){
        this.weekLimit = weekLimit;
        this.weekLimitColor = weekLimitColor;
        this.monthLimit = monthLimit;
        this.monthLimitColor = monthLimitColor;
        this.yearLimit = yearLimit;
        this.yearLimitColor = yearLimitColor;
        this.ipAddress = ipAddress;
        this.notification = notification;
    }

    public MyPreferences(){
    }

    public String getWeekLimitColor() {
        return weekLimitColor;
    }

    public String getMonthLimitColor() {
        return monthLimitColor;
    }

    public String getYearLimitColor() {
        return yearLimitColor;
    }

    public void setWeekLimitColor(String weekLimitColor) {
        this.weekLimitColor = weekLimitColor;
    }

    public void setMonthLimitColor(String monthLimitColor) {
        this.monthLimitColor = monthLimitColor;
    }

    public void setYearLimitColor(String yearLimitColor) {
        this.yearLimitColor = yearLimitColor;
    }

    public Integer getWeekLimit() {
        return weekLimit;
    }

    public Integer getMonthLimit() {
        return monthLimit;
    }

    public Integer getYearLimit() {
        return yearLimit;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public Boolean getNotification() {
        return notification;
    }

    public void setWeekLimit(Integer weekLimit) {
        this.weekLimit = weekLimit;
    }

    public void setMonthLimit(Integer monthLimit) {
        this.monthLimit = monthLimit;
    }

    public void setYearLimit(Integer yearLimit) {
        this.yearLimit = yearLimit;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setNotification(Boolean notification) {
        this.notification = notification;
    }
}
