package com.ota.jimmychen.ota;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;

public class ViewTempActivity extends Activity {
    private BarChart tempChart = null;
    private BarData tempData = null;
    private final static int PORT_NUMBER = 8080;

    private Networking network = new Networking(PORT_NUMBER);
    private String ip_address = null;
    private List<Double> temp_list = new ArrayList<>();
    private Thread get_temp_thread = null;
    private static final int TEAL_COLOR = Color.parseColor("#008080");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewtemp);

        tempChart = (BarChart)findViewById(R.id.bar_chart);

        Intent intent = getIntent();
        ip_address = intent.getStringExtra("ip_address");

        new Thread(new Runnable() {
            @Override
            public void run() {
                getTemp();
                initData((ArrayList<Double>) temp_list);
                initBarChart();
            }
        }).start();
    }

    private void getTemp() {
        if (get_temp_thread != null) { get_temp_thread.interrupt(); }
        get_temp_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                temp_list = network.getTemp();
            }
        });
        get_temp_thread.run();
    }

    private float double_to_float(double d) {
        return Float.valueOf(String.valueOf(d));
    }

    private void initData(ArrayList<Double> data) {
        ArrayList<BarEntry> yValues = new ArrayList<>();
        for (int x = 0; x < data.size(); x++) {
            yValues.add(new BarEntry(x, double_to_float(data.get(x))));
        }
        BarDataSet barDataSet = new BarDataSet(yValues, "Temperature Record");
        barDataSet.setColor(TEAL_COLOR);
        tempData = new BarData(barDataSet);
    }

    private void initBarChart() {
        if (temp_list.size() == 0) return;
        float barWidth = 0.45f;
        tempData.setBarWidth(barWidth);
        tempChart.setData(tempData);
        tempChart.setScaleEnabled(true);

        XAxis xAxis = tempChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelRotationAngle(10f);
        xAxis.setAxisMinimum(0);

        YAxis RAxis = tempChart.getAxisRight();
        RAxis.setEnabled(false);

        YAxis LAxis = tempChart.getAxisLeft();
        RAxis.setDrawAxisLine(false);
        LAxis.setDrawAxisLine(false);
        LAxis.setDrawAxisLine(false);
        LAxis.setDrawGridLines(false);
        LAxis.setAxisMinimum(0f);

        tempChart.setScaleEnabled(false);
        tempChart.setScaleXEnabled(true);
        tempChart.setScaleYEnabled(false);
        tempChart.setDoubleTapToZoomEnabled(false);
        tempChart.notifyDataSetChanged();
        tempChart.invalidate();
    }


}
