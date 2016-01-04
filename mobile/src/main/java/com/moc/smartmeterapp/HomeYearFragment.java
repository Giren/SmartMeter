package com.moc.smartmeterapp;

/**
 * Created by michael on 23.11.15.
 */
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moc.smartmeterapp.preferences.MyPreferences;
import com.moc.smartmeterapp.preferences.PreferenceHelper;
import com.moc.smartmeterapp.ui.Limiter;
import com.moc.smartmeterapp.ui.MeterView;
import com.moc.smartmeterapp.utils.HomeHelper;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HomeYearFragment extends Fragment implements PreferenceHelper.PrefReceive{

    private final float OFFSET = 0.1f;

    private MyPreferences prefs;
    private PreferenceHelper preferenceHelper;

    private HomeHelper homeHelper;

    private MeterView meterView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        preferenceHelper = new PreferenceHelper();
        prefs = PreferenceHelper.getPreferences(getActivity());

        homeHelper = new HomeHelper(getActivity());

        return inflater.inflate(R.layout.home_year_fragment_layout,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        meterView = (MeterView) view.findViewById(R.id.year_meterview);
        meterView.setOffsetAngle(45);
        meterView.setLimiter(getLimiter());

        Observable.just(homeHelper.getConsumption(HomeHelper.YEAR))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new rx.Observer<Integer>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Integer consumption) {
                        if (consumption != null) {
                            meterView.setMax((int) (consumption + consumption * OFFSET));
                            meterView.setValue(consumption);
                            meterView.invalidate();
                        }
                    }
                });
    }

    private Limiter getLimiter() {
        Limiter limiter = new Limiter();
        limiter.addLimit(prefs.getLimit3());
        limiter.addLimit(prefs.getLimit2());
        limiter.addLimit(prefs.getLimit1());
        return limiter;
    }

    @Override
    public void onPrefReceive(MyPreferences pref) {
        this.prefs = pref;
        meterView.setLimiter(getLimiter());
    }
}
