package com.moc.smartmeterapp;

import android.database.Cursor;

import com.moc.smartmeterapp.database.MeterDbHelper;

/**
 * Created by michael on 06.12.15.
 */
public class RestData {
    private long id;
    private Integer energy;
    private Integer t1;
    private Integer t2;
    private Integer currentEnergy;

    public RestData(long id, Integer energy, Integer t1, Integer t2, Integer currentEnergy){
        this.id = id;
        this.energy = energy;
        this.t1 = t1;
        this.t2 = t2;
        this.currentEnergy = currentEnergy;
    }

    public RestData(){}

    public String toString(){
        return ("id" + id + "energy" + energy);
    }

    public Integer getEnergy() {
        return energy;
    }

    public void setEnergy(Integer energy) {
        this.energy = energy;
    }

    public Integer getT1() {
        return t1;
    }

    public void setT1(Integer t1) {
        this.t1 = t1;
    }

    public Integer getT2() {
        return t2;
    }

    public void setT2(Integer t2) {
        this.t2 = t2;
    }

    public Integer getCurrentEnergy() {
        return currentEnergy;
    }

    public void setCurrentEnergy(Integer currentEnergy) {
        this.currentEnergy = currentEnergy;
    }

    public long getId() {
        return id;
    }
}
