package com.moc.smartmeterapp.com.moc.smartmeterapp.communication;

import com.moc.smartmeterapp.model.DataObject;
import com.moc.smartmeterapp.model.EntryObject;
import com.moc.smartmeterapp.model.Global;
import com.moc.smartmeterapp.model.Limit;

import java.util.List;

import retrofit.RestAdapter;
import retrofit.http.GET;

/**
 * Created by philipp on 14.12.2015.
 */
public class ComUtils {
    public final static String SERVICE_ENDPOINT = "http://10.0.0.20:8080";

    public final static int LIVE_DATA = 0;
    public final static int GLOBAL_DATA = 1;
    public final static int LIMITS = 2;
    public final static int METER_DATA = 3;
    public final static int TEST = 4;

    public final static int RECIVED_LIVE_DATA = 10;
    public final static int RECIVED_GLOBAL_DATA = 11;
    public final static int RECIVED_LIMITS = 12;
    public final static int RECIVED_METER_DATA = 13;
    public final static int RECIVED_TEST = 14;

    public interface IRestTestService {
        @GET("/stats?accessToken=123456")
        rx.Observable<EntryObject> getEntryObjectObservable();
    }

    public interface IGlobalRestService {
        @GET("/stats?accessToken=123456")
        rx.Observable<Global> getGlobalObservable();
    }

    public interface ILimitListRestService {
        @GET("/stats?accessToken=123456")
        rx.Observable<List<Limit>> getLimitListObservable();
    }

    public interface IDataObjectRestService {
        @GET("/stats?accessToken=123456")
        rx.Observable<DataObject> getDataObjectObservable();
    }

    public static <T> T createRetrofitService(final Class<T> c) {
        final RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(SERVICE_ENDPOINT)
                .build();
        T service = restAdapter.create(c);

        return service;
    }
}
