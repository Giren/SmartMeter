package com.moc.smartmeterapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.moc.smartmeterapp.com.moc.smartmeterapp.communication.ComUtils;
import com.moc.smartmeterapp.model.EntryObject;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by michael on 24.11.15.
 */
public class HelpFragment extends Fragment{
    private ComUtils.IRestTestService restService;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        restService = ComUtils.createRetrofitService(ComUtils.IRestTestService.class);

        return inflater.inflate(R.layout.help_fragment_layout,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //new HttpRequestTask().execute();

        Button restButton = (Button) view.findViewById(R.id.button_rest);
        restButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //new HttpRequestTask().execute();
                Observable.interval(1, 3, TimeUnit.SECONDS)
                        .subscribe(new Observer<Long>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(Long aLong) {
                                restService.getEntryObjectObservable()
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Subscriber<EntryObject>() {

                                            @Override
                                            public void onCompleted() {
                                                Log.d("DEBUG", "onComplete");
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                Log.d("DEBUG", "onError: " + e.getMessage());
                                            }

                                            @Override
                                            public void onNext(EntryObject entryObject) {
                                                Log.d("DEBUG", "onNext: EntryObject " + entryObject.getCurrentEnergy());
                                            }
                                        });
                            }
                        });
            }
        });
    }

    /*private class HttpRequestTask extends AsyncTask<Void, Void, RestData> {
        @Override
        protected RestData doInBackground(Void... params) {
            try {
                final String url = "http://10.0.0.20:8080/stats?accessToken=123456";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                RestData restData = restTemplate.getForObject(url, RestData.class);
                return restData;
            } catch (Exception e) {
                Log.e("HelpFragment", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(RestData restData) {
            if(restData == null){
                //TODO Meldung das Server nicht erreichbar ist
                return;
            }

            try{
                TextView greetingIdText = (TextView) getActivity().findViewById(R.id.id_value);
                TextView greetingContentText = (TextView) getActivity().findViewById(R.id.content_value);
                greetingIdText.setText(String.valueOf(restData.getId()));
                greetingContentText.setText(String.valueOf(restData.getCurrentEnergy()));
            } catch (Exception e) {
                Log.e("HelpFragment", e.getMessage(), e);
            }

        }
    }*/
}
