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
    public static String SERVICE_ENDPOINT = "http://10.0.0.20:8080";

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
