package com.moc.smartmeterapp.model;

import java.util.ArrayList;

/**
 * Created by philipp on 14.12.2015.
 */
public class DataObject {
    private MinMeanMax characteristics;
    private ArrayList<DataObject> data;

    public MinMeanMax getCharacteristics() {
        return characteristics;
    }
    public void setCharacteristics(MinMeanMax characteristics) {
        this.characteristics = characteristics;
    }
    public ArrayList<DataObject> getData() {
        return data;
    }
    public void setData(ArrayList<DataObject> data) {
        this.data = data;
    }
}
