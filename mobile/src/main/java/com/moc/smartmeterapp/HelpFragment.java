package com.moc.smartmeterapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Created by michael on 24.11.15.
 */
public class HelpFragment extends Fragment{
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
    }

    private class HttpRequestTask extends AsyncTask<Void, Void, RestData> {
        @Override
        protected RestData doInBackground(Void... params) {
            try {
                final String url = "http://10.0.0.104:8080/stats?accessToken=123456";
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
