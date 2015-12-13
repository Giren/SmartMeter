package com.moc.smartmeterapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.moc.smartmeterapp.database.MeterDataSource;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Created by michael on 24.11.15.
 */
public class HelpFragment extends Fragment{

    private MeterDataSource meterDataSource;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.help_fragment_layout,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new HttpRequestTask().execute();

        Button restButton = (Button) view.findViewById(R.id.button_rest);
        restButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new HttpRequestTask().execute();
            }
        });

        ListView listView = (ListView) view.findViewById(R.id.listView);
        RestData test1 = new RestData(4711,1,1,1,1);
        RestData test2 = new RestData(4711,2,1,1,1);
        RestData test3 = new RestData(4711,3,1,1,1);
        RestData test4 = new RestData(4711,4,1,1,1);
        RestData test5 = new RestData(4711,5,1,1,1);
        meterDataSource = new MeterDataSource(getActivity().getBaseContext());
        meterDataSource.openDataBase();
        RestData dbData = meterDataSource.createRestData(test1);
        dbData = meterDataSource.createRestData(test2);
        dbData = meterDataSource.createRestData(test3);
        dbData = meterDataSource.createRestData(test4);
        dbData = meterDataSource.createRestData(test5);
        listView.setAdapter(showAllDBEntries());
        meterDataSource.closeDataBase();
    }

    private ArrayAdapter showAllDBEntries(){
        List<RestData> dataList = meterDataSource.getAllRestData();

        ArrayAdapter<RestData> restDataArrayAdapter = new ArrayAdapter<RestData>(
                getActivity().getBaseContext(),
                android.R.layout.simple_list_item_multiple_choice,
                dataList);
        return restDataArrayAdapter;
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, RestData> {
        @Override
        protected RestData doInBackground(Void... params) {
            try {
                //final String url = "http://10.0.0.104:8080/stats?accessToken=123456";
                final String url = "http://192.168.178.26:8080/stats?accessToken=123456";
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
    }
}
