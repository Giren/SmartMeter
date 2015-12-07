package com.moc.smartmeterapp.com.moc.smartmeterapp.communication;

/**
 * Created by philipp on 28.11.2015.
 */
public interface IDataEventHandler {
    public boolean onLiveDataReceived(int value);
}
