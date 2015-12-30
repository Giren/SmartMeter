package com.moc.smartmeterapp;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.moc.smartmeterapp.database.MeterDbHelper;
import com.moc.smartmeterapp.model.Day;

import com.moc.smartmeterapp.database.IDatabase;
import com.moc.smartmeterapp.database.MeterDataSource;
import com.moc.smartmeterapp.database.MeterDbHelper;
import com.moc.smartmeterapp.model.Day;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

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

    private LineChartView chart;
    private PreviewLineChartView previewChart;
    private LineChartData data;
    private LineChartData previewData;
    private Line dataLine;
    private ArrayList<Day> liveList;
    private int maxValueOfLineChartData;

    private Button dateButton;
    private Dialog dialog;
    private Dialog dialogSelect;
    private DatePicker datePicker;
    private NumberPicker numberPicker;

    private String dialogSelectTilte = "Welcher Zeitraum ?";
    private String[] selectString = { "Tag", "Woche", "Monat", "Jahr" };
    private int DEFAULT_NUMBER_TO_SHOW = 31;
    private int maxNumberToShow;

    private final int DAY = 0;
    private final int WEEK = 1;
    private final int MONTH = 2;
    private final int YEAR = 3;
    private int userChoice = 0;

    private MeterDbHelper meterDbHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(liveList == null) {
            liveList = new ArrayList<Day>();
        }
        if(meterDbHelper == null){
            meterDbHelper = new MeterDbHelper(getActivity().getBaseContext());
        }

        return inflater.inflate(R.layout.statistic_fragment_layout,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chart = (LineChartView) view.findViewById(R.id.chart);
        chart.setLineChartData(data);
        chart.setZoomEnabled(true);
        chart.setScrollEnabled(true);

        previewChart = (PreviewLineChartView) view.findViewById(R.id.chart_preview);
        previewChart.setLineChartData(previewData);
        previewChart.setViewportChangeListener(new ViewportListener());

        final Button selectButton = (Button)view.findViewById(R.id.button_left);
        selectButton.setText(selectString[userChoice]);
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogSelect == null) {
                    dialogSelect = new Dialog(v.getContext());
                }
                dialogSelect.setContentView(R.layout.dialog_select);
                dialogSelect.setTitle(dialogSelectTilte);

                numberPicker = (NumberPicker) dialogSelect.findViewById(R.id.number_picker);
                numberPicker.setMinValue(0);
                numberPicker.setMaxValue(selectString.length - 1);
                numberPicker.setDisplayedValues(selectString);
                numberPicker.setWrapSelectorWheel(true);

                Button dialogButton = (Button) dialogSelect.findViewById(R.id.apply_button_select);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        userChoice = numberPicker.getValue();
                        selectButton.setText(selectString[userChoice]);
                        dialogSelect.dismiss();
                        dateButton.callOnClick();
                    }
                });

                dialogSelect.show();
            }
        });

        dateButton = (Button)view.findViewById(R.id.button_right);
        if(datePicker == null){
            dateButton.setText("Datum");
        } else{
            dateButton.setText(dateToString(getPickedDate().getTime()));
        }
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog == null) {
                    dialog = new Dialog(v.getContext());
                }
                dialog.setContentView(R.layout.dialog);
                datePicker = (DatePicker) dialog.findViewById(R.id.dpResult);

                Button dialogButton = (Button) dialog.findViewById(R.id.apply_button);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handleUserChoice();
                        dateButton.setText(dateToString(getPickedDate().getTime()));
                        updateChartView();
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
        updateChartView();
    }

    public void addListToChart(List<Day> days){
        int currentValue;
        int size = days.size();
        maxValueOfLineChartData = 0;
        List<PointValue> valuePoints = new ArrayList<PointValue>();

        if(size == 1){
            for(int i=0; i<24; i++){
                currentValue = days.get(0).getHours().get(i).getMmm().getMean();
                if(currentValue > maxValueOfLineChartData){
                    maxValueOfLineChartData = currentValue;
                }
                valuePoints.add(new PointValue(i, currentValue));
            }
        } else {
            for(int i=0; i<size; i++){
                currentValue = days.get(i).getMmm().getMean();
                if(currentValue > maxValueOfLineChartData){
                    maxValueOfLineChartData = currentValue;
                }
                valuePoints.add(new PointValue(i, currentValue));
            }
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

        updateChartView();
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

    private String dateToString(Date date){
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    private void handleUserChoice(){
        Day d;
        List<Day> datalist = new ArrayList<Day>();
        Calendar ca = getPickedDate();

        meterDbHelper.openDatabase();
        switch (userChoice){
            case DAY:
                maxNumberToShow = 24;
                d = meterDbHelper.loadDay(ca.getTime());
                if(d != null){
                    datalist.add(d);
                }
                break;
            case WEEK:
                maxNumberToShow = 7;
                for(int i=0; i<maxNumberToShow; i++){
                    d = meterDbHelper.loadDay(ca.getTime());
                    if(d != null){
                        datalist.add(d);
                    }
                    ca.set(Calendar.DAY_OF_MONTH,ca.get(Calendar.DAY_OF_MONTH)+1);
                }
                break;
            case MONTH:
                maxNumberToShow = 31;
                datalist = meterDbHelper.loadMonth(ca.getTime());
                break;
            case YEAR:
                maxNumberToShow = 365;
                datalist = meterDbHelper.loadYear(ca.getTime());
                break;
        }

        if(datalist.size() != 0){
            addListToChart(datalist);
        } else {
            cleanChart();
            Toast.makeText(getActivity(),"Keine Daten für gewähltes Datum gefunden", Toast.LENGTH_SHORT).show();
        }

        meterDbHelper.closeDatabase();
    }

    private void cleanChart(){
        chart.setLineChartData(null);
    }

    private Calendar getPickedDate(){
        return new GregorianCalendar (
                datePicker.getYear(),
                datePicker.getMonth(),
                datePicker.getDayOfMonth()
        );
    }

}
