package com.moc.smartmeterapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ViewportChangeListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PreviewLineChartView;

/**
 * Created by michael on 24.11.15.
 */
public class StatisticFragment extends Fragment{

    private ArrayList<String> test;
    private ArrayList<String> test2;

    private LineChartView chart;
    private PreviewLineChartView previewChart;
    private LineChartData data;
    private LineChartData previewData;
    private Line dataLine;
    private ArrayList<String> liveList;
    private int maxValueOfLineChartData;

    private Dialog dialog;
    private NumberPicker numberPicker;
    private String dialogTilte = "Anzahl sichtbarer Punkte";
    private int MAX_WHEEL_NUMBER = 100;
    private int MIN_WHEEL_NUMBER = 10;
    private int maxNumberToShow;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        test = new ArrayList<String>();
        for(int i=0; i<MAX_WHEEL_NUMBER; i++){
            test.add(String.valueOf(i));
        }
        test2 = new ArrayList<String>();
        for(int i=0; i<MAX_WHEEL_NUMBER; i++){
            test2.add(String.valueOf(100-i));
        }

        liveList = new ArrayList<String>();
        return inflater.inflate(R.layout.statistic_fragment_layout,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        maxNumberToShow = MAX_WHEEL_NUMBER;

        chart = (LineChartView) view.findViewById(R.id.chart);
        previewChart = (PreviewLineChartView) view.findViewById(R.id.chart_preview);

        chart.setLineChartData(data);
        chart.setZoomEnabled(false);
        chart.setScrollEnabled(true);

        previewChart.setLineChartData(previewData);
        previewChart.setViewportChangeListener(new ViewportListener());

        addDataToChart(test);

        Button settingButton = (Button)view.findViewById(R.id.button_statistic_settings);
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random r = new Random();
                int i1 = r.nextInt(3000 - 0 + 1);
                addValueToChart(String.valueOf(i1));
            }
        });

        Button adjustButton = (Button)view.findViewById(R.id.button_adjust);
        adjustButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog == null) {
                    dialog = new Dialog(v.getContext());
                }

                dialog.setContentView(R.layout.dialog);
                dialog.setTitle(dialogTilte);

                numberPicker = (NumberPicker) dialog.findViewById(R.id.number_picker);
                numberPicker.setMinValue(MIN_WHEEL_NUMBER);
                numberPicker.setMaxValue(MAX_WHEEL_NUMBER);
                numberPicker.setWrapSelectorWheel(true);

                Button dialogButton = (Button) dialog.findViewById(R.id.apply_button);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        maxNumberToShow = numberPicker.getValue();
                        updateChartView();
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        previewX(false);
    }

    public void setMaxValueOfLineChartData(int maxValueOfLineChartData){
        this.maxValueOfLineChartData = maxValueOfLineChartData;
    }

    public void addValueToChart(String value) {
        if(liveList.size() >= MAX_WHEEL_NUMBER){
            liveList.remove(0);
        }
        liveList.add(value);
        addDataToChart(liveList);
    }

    public void addDataToChart(ArrayList<String> values){
        int maxValue = 0;
        int currentValue;
        int size = values.size();
        List<PointValue> valuePoints = new ArrayList<PointValue>();

        for(int i=0; i<size; i++){
            currentValue = Integer.parseInt(values.get(i));
            if(currentValue > maxValue){
                maxValue = currentValue;
            }
            valuePoints.add(new PointValue(i, currentValue));
        }

        dataLine = new Line(valuePoints);
        dataLine.setColor(ChartUtils.COLOR_ORANGE);
        dataLine.setHasPoints(false);

        List<Line> lines = new ArrayList<Line>();
        lines.add(dataLine);

        data = new LineChartData(lines);
        data.setAxisXBottom(new Axis());
        data.setAxisYLeft(new Axis().setHasLines(true));

        previewData = new LineChartData(data);
        previewData.getLines().get(0).setColor(ChartUtils.DEFAULT_DARKEN_COLOR);

        chart.setLineChartData(data);
        previewChart.setLineChartData(previewData);

        setMaxValueOfLineChartData(maxValue);
        updateChartView();
    }

    private void previewY() {
        Viewport tempViewport = new Viewport(chart.getMaximumViewport());
        float dy = tempViewport.height() / 4;
        tempViewport.inset(0, dy);
        previewChart.setCurrentViewportWithAnimation(tempViewport);
        previewChart.setZoomType(ZoomType.VERTICAL);
    }

    private void previewX(boolean animate) {
        Viewport tempViewport = new Viewport(chart.getMaximumViewport());
        float dx = tempViewport.width() / 4;
        tempViewport.inset(dx, 0);
        if (animate) {
            previewChart.setCurrentViewportWithAnimation(tempViewport);
        } else {
            previewChart.setCurrentViewport(tempViewport);
        }
        previewChart.setZoomType(ZoomType.HORIZONTAL);
    }

    private void previewXY() {
        // Better to not modify viewport of any chart directly so create a copy.
        Viewport tempViewport = new Viewport(chart.getMaximumViewport());
        // Make temp viewport smaller.
        float dx = tempViewport.width() / 4;
        float dy = tempViewport.height() / 4;
        tempViewport.inset(dx, dy);
        previewChart.setCurrentViewportWithAnimation(tempViewport);
    }

    private class ViewportListener implements ViewportChangeListener {

        @Override
        public void onViewportChanged(Viewport newViewport) {
            // don't use animation, it is unnecessary when using preview chart.
            chart.setCurrentViewport(newViewport);
        }

    }

    private void updateChartView(){
        Viewport viewport = new Viewport(0, maxValueOfLineChartData, maxNumberToShow, 0);
        chart.setMaximumViewport(viewport);
        chart.setCurrentViewport(viewport);
        previewChart.setMaximumViewport(viewport);
        previewX(false);
    }

}
