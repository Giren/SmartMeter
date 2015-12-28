package com.moc.smartmeterapp.communication;

import com.google.gson.GsonBuilder;
import com.moc.smartmeterapp.model.DataObject;
import com.moc.smartmeterapp.model.Global;
import com.moc.smartmeterapp.model.Limit;
import com.moc.smartmeterapp.utils.DateConverter;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.QueryMap;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by philipp on 23.12.2015.
 */
public class RestCommunication {

    public final static String SERVICE_ENDPOINT = "http://10.0.0.20:8080";

    public final static String GET_GLOBBAL_PATH = "";
    public final static String GET_LIMITS_PATH = "";
    public final static String GET_YEAR_DATA_PATH = "year";
    public final static String GET_MONTH_DATA_PATH = "month";

    private Map<String, String> PARAMS;

    public interface IGlobalDataReceiver {
        void onGlobalDataReceived(Global global);
        void onError(String message);
        void onComplete();
    }

    public interface ILimitsReceiver {
        void onLimitsReceived(List<Limit> limits);
        void onError(String message);
        void onComplete();
    }

    public interface IDataReceiver {
        void onDataReceived(DataObject dataObject);
        void onError(String message);
        void onComplete();
    }

    public interface IRestService {
        @GET("/{path}")
        rx.Observable<DataObject> getDataObjectsObservable(@Path("path") String path, @QueryMap Map<String, String> params);

        @GET("/{path}")
        rx.Observable<List<Limit>> getLimitsObservable(@Path("path") String path, @QueryMap Map<String, String> params);

        @GET("/{path}")
        rx.Observable<Global> getGlobalObservable(@Path("path") String path, @QueryMap Map<String, String> params);
    }

    private IRestService restService = createRetrofitService(IRestService.class);

    public RestCommunication() {
        PARAMS = new HashMap<String, String>();
        PARAMS.put("accessToken", "123456");
    }

    public void fetchGlobalData(final IGlobalDataReceiver globalDataReceiver) {

        restService.getGlobalObservable(GET_GLOBBAL_PATH, PARAMS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Global>() {
                    @Override
                    public void onCompleted() {
                        globalDataReceiver.onComplete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        globalDataReceiver.onError(e.getMessage());
                    }

                    @Override
                    public void onNext(Global global) {
                        globalDataReceiver.onGlobalDataReceived(global);
                    }
                });
    }

    public void fetchLimits(final ILimitsReceiver limitsReceiver) {

        restService.getLimitsObservable(GET_LIMITS_PATH, PARAMS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Limit>>() {
                    @Override
                    public void onCompleted() {
                        limitsReceiver.onComplete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        limitsReceiver.onError(e.getMessage());
                    }

                    @Override
                    public void onNext(List<Limit> limits) {
                        limitsReceiver.onLimitsReceived(limits);
                    }
                });
    }

    public void saveLimits(List<Limit> limits) {
    }

    public void fetchMonthData(int month, final IDataReceiver dataReceiver) {
        fetchData(GET_MONTH_DATA_PATH, month, dataReceiver);
    }

    public void fetchYearData(int year, final IDataReceiver dataReceiver) {
        fetchData(GET_YEAR_DATA_PATH, year, dataReceiver);
    }

    private void fetchData(String param, int value, final IDataReceiver dataReceiver) {

        restService.getDataObjectsObservable(param, PARAMS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DataObject>() {
                    @Override
                    public void onCompleted() {
                        dataReceiver.onComplete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        dataReceiver.onError(e.getMessage());
                    }

                    @Override
                    public void onNext(DataObject dataObject) {
                        dataReceiver.onDataReceived(dataObject);
                    }
                });
    }

    public static <T> T createRetrofitService(final Class<T> c) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new DateConverter());

        final RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(SERVICE_ENDPOINT)
                .setConverter(new GsonConverter(gsonBuilder.create()))
                .build();
        T service = restAdapter.create(c);

        return service;
    }
}
