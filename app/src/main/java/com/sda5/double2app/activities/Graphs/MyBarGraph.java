package com.sda5.double2app.activities.Graphs;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.sda5.double2app.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class MyBarGraph extends DemoBase implements
        OnChartGestureListener, OnChartValueSelectedListener {

    private BarChart chart;

    private String categoryIntent;

    private HashMap<String, Double> expenseMaps = new HashMap<>();

    public static final String TAG = "My Bar Graph";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_my_bar_graph);

        setTitle("Bar plot for One Expense");

        chart = findViewById(R.id.chartMyBar);
        chart.setOnChartValueSelectedListener(this);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawLabels(true);

        //xAxis.mAxisMinimum=0;
        //xAxis.mAxisMaximum=20;

        YAxis leftAxis = chart.getAxisLeft();
        //leftAxis.setTypeface(mTf);
        //leftAxis.setLabelCount(5, false);
        leftAxis.setSpaceTop(20f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = chart.getAxisRight();
        //rightAxis.setTypeface(mTf);
        //rightAxis.setLabelCount(5, false);
        rightAxis.setSpaceTop(20f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        chart.setDrawGridBackground(false);
        chart.getDescription().setEnabled(false);
        chart.setDrawBorders(false);

        chart.getAxisLeft().setEnabled(false);
        chart.getAxisRight().setDrawAxisLine(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getXAxis().setDrawAxisLine(true);
        chart.getXAxis().setDrawGridLines(false);

        // enable touch gestures
        chart.setTouchEnabled(true);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(true);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setTextSize(12);

        // Get  HashMap from the incoming intent
        Intent intent = getIntent();
        expenseMaps = (HashMap<String, Double>) intent.getSerializableExtra("map");
        categoryIntent = (intent.getStringExtra("category"));
        //generateDataLine();
        getRemoteData(expenseMaps, categoryIntent);
    }

    private final int[] colors = new int[]{
            ColorTemplate.VORDIPLOM_COLORS[0],
            ColorTemplate.VORDIPLOM_COLORS[1],
            ColorTemplate.VORDIPLOM_COLORS[2]
    };


    /**
     * @param expenseRemote
     */
    private void getRemoteData(HashMap<String, Double> expenseRemote, String c1) {


        Set<String> keySet = expenseRemote.keySet();//Getting Set of keys Categories/months from HashMap
        String[] labelsExpense = keySet.toArray(new String[0]);// Transfer data to an array
        final String[] labelsExpense2 = keySet.toArray(new String[0]);//Making a copy of labelsExpense
        final String[] sortedString = labelsExpense;// Storing labels in another array that will be sorted

        // Getting values from HashMap
        Collection<Double> values = expenseRemote.values();
        Double[] valuesRemote = values.toArray(new Double[0]);// Convert data to an array

        // This array will contain the values sorted based on sorting index of labels
        Double[] sortedA = new Double[labelsExpense.length];

        // BarEntry
        ArrayList<BarEntry> valuesExpense = new ArrayList<>();

        if (true) {

            Arrays.sort(sortedString); // sort the labels

            // For loops for Printing the results just to confirm the sorting. Will be removed once sorting done.
            for (String labelss : labelsExpense2) {
                System.out.println("labelsOriginal " + labelss);
            }
            for (String labelSorted : sortedString) {
                System.out.println("labelsSorted " + labelSorted);
            }
            // Values before sorting
            for (Double valuess : valuesRemote) {
                System.out.println("valuesBeforeSorting " + valuess);
            }

            // Loop for sorting the data based on dates.
            for (int i = 0; i < labelsExpense.length; i++) {
                for (int j = 0; j < valuesRemote.length; j++) {
                    if (sortedString[i] == labelsExpense2[j]) {
                        sortedA[i] = valuesRemote[j];
                    }
                }
            }
            for (Double valuesSort : sortedA) {
                System.out.println("valuessAfterSorting " + valuesSort);
            }
        }
        //Printing values after sorting
        for (int i = 0; i < valuesRemote.length; i++) {
            valuesExpense.add(new BarEntry(i, sortedA[i].intValue()));
        }

        //
        BarDataSet d4 = new BarDataSet(valuesExpense, c1);
        d4.setColors(ColorTemplate.VORDIPLOM_COLORS);
        d4.setHighLightAlpha(255);


        //xAxis.setAxisMinimum(0);
        //xAxis.setAxisMaximum(labelsExpense.length);
        //xAxis.mAxisMaximum=labelsExpense.length;
        //xAxis.setValueFormatter(new IndexAxisValueFormatter(labelsExpense));

        //xAxis.setAvoidFirstLastClipping(true);

        //xAxis.setAxisMaximum(40f);

        BarData data = new BarData(d4);
        data.setBarWidth(0.9f); // TO SET BAR WIDTH
        data.setValueTextSize(14f); // SET VALUE SIZE DISPLAYED ABOVE THE BAR
        chart.setData((BarData) data);

        XAxis xAxis = chart.getXAxis();
        xAxis.setTextSize(10); // Set size of x labels
        xAxis.setLabelCount(labelsExpense.length); // set how many labels you want to see
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labelsExpense)); // set user defined labels
        chart.animateXY(2000, 2000);
        chart.invalidate();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.line, menu);
        menu.removeItem(R.id.actionToggleIcons);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

          /*
            case R.id.actionToggleIcons: { break; }
             */
            case R.id.actionTogglePinch: {
                if (chart.isPinchZoomEnabled())
                    chart.setPinchZoom(false);
                else
                    chart.setPinchZoom(true);

                chart.invalidate();
                break;
            }
            case R.id.actionToggleAutoScaleMinMax: {
                chart.setAutoScaleMinMaxEnabled(!chart.isAutoScaleMinMaxEnabled());
                chart.notifyDataSetChanged();
                break;
            }
            case R.id.actionToggleHighlight: {
                if (chart.getData() != null) {
                    chart.getData().setHighlightEnabled(!chart.getData().isHighlightEnabled());
                    chart.invalidate();
                }
                break;
            }

            case R.id.actionSave: {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    saveToGallery();
                } else {
                    requestStoragePermission(chart);
                }
                break;
            }
            case R.id.animateX: {
                chart.animateX(2000);
                break;
            }
            case R.id.animateY: {
                chart.animateY(2000);
                break;
            }
            case R.id.animateXY: {
                chart.animateXY(2000, 2000);
                break;
            }
        }
        return true;
    }

    @Override
    protected void saveToGallery() {
        saveToGallery(chart, "MultiLineChartActivity");
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "START, x: " + me.getX() + ", y: " + me.getY());
    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "END, lastGesture: " + lastPerformedGesture);

        // un-highlight values after the gesture is finished and no single-tap
        if (lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP)
            chart.highlightValues(null); // or highlightTouch(null) for callback to onNothingSelected(...)
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        Log.i("LongPress", "Chart long pressed.");
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        Log.i("DoubleTap", "Chart double-tapped.");
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        Log.i("SingleTap", "Chart single-tapped.");
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
        Log.i("Fling", "Chart fling. VelocityX: " + velocityX + ", VelocityY: " + velocityY);
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        Log.i("Scale / Zoom", "ScaleX: " + scaleX + ", ScaleY: " + scaleY);
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        Log.i("Translate / Move", "dX: " + dX + ", dY: " + dY);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("VAL SELECTED",
                "Value: " + e.getY() + ", xIndex: " + e.getX()
                        + ", DataSet index: " + h.getDataSetIndex());
    }

    @Override
    public void onNothingSelected() {
    }

}
