package com.moc.smartmeterapp.communication;

import android.content.Context;

import com.google.gson.GsonBuilder;
import com.moc.smartmeterapp.model.DataObject;
import com.moc.smartmeterapp.model.Global;
import com.moc.smartmeterapp.model.Limit;
import com.moc.smartmeterapp.preferences.MyPreferences;
import com.moc.smartmeterapp.preferences.PreferenceHelper;
import com.moc.smartmeterapp.utils.DateConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
    public static final DateFormat DAY_FORMAT = new SimpleDateFormat("dd");
    public static final DateFormat MONTH_FORMAT = new SimpleDateFormat("MM");
    public static final DateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy");

    public final static String PORT = "8080";
    public final static String PROTOCOL = "http://";
    public final static String DUMMY = "127.0.0.1";

    public final static String GET_GLOBBAL_PATH = "";
    public final static String GET_LIMITS_PATH = "";
    public final static String GET_SINCE_DATA_PATH = "since";
    public final static String GET_YEAR_DATA_PATH = "year";
    public final static String GET_MONTH_DATA_PATH = "month";

    private Context context;

    private Map<String, String> GLOBAL_PARAMS;

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
    }

    private IRestService restService;

    public RestCommunication(Context context) {
        this.context = context;
        restService = createRetrofitService(IRestService.class);

        GLOBAL_PARAMS = new HashMap<String, String>();
        GLOBAL_PARAMS.put("accessToken", "123456");
    }

    private String getServiceEndpoint() {
        //TODO: Sicherheitsabfragen
        if(context != null) {
            MyPreferences prefs = PreferenceHelper.getPreferences(context);

            if(prefs != null && prefs.getIpAddress() != null)
                return PROTOCOL + prefs.getIpAddress() + ":" + PORT;
        }

        return PROTOCOL + DUMMY + ":" + PORT;
    }

    public void fetchLimits(final ILimitsReceiver limitsReceiver) {
        Map<String, String> LOCAL_PARAMS = GLOBAL_PARAMS;

        restService.getLimitsObservable(GET_LIMITS_PATH, LOCAL_PARAMS)
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

    public void fetchSinceData(Date date, final IDataReceiver dataReceiver) {

        Map<String, String> LOCAL_PARAMS = GLOBAL_PARAMS;
        LOCAL_PARAMS.put("year", YEAR_FORMAT.format(date));
        LOCAL_PARAMS.put("month", MONTH_FORMAT.format(date));
        LOCAL_PARAMS.put("day", DAY_FORMAT.format(date));

        fetchData(GET_SINCE_DATA_PATH, LOCAL_PARAMS, dataReceiver);
    }

    private void fetchData(String path, Map<String, String> params, final IDataReceiver dataReceiver) {

        restService.getDataObjectsObservable("since", params)
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

    public <T> T createRetrofitService(final Class<T> c) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new DateConverter());

        final RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(getServiceEndpoint())
                .setConverter(new GsonConverter(gsonBuilder.create()))
                .build();
        T service = restAdapter.create(c);

        return service;
    }
}
