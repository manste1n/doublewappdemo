package com.sda5.double2app.activities.Graphs;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MyBarGraphComparison extends DemoBase implements
        OnChartGestureListener, OnChartValueSelectedListener {

    private BarChart chart;


    private HashMap<String, Double> expenseMaps = new HashMap<>();
    private HashMap<String, Double> expenseMaps2 = new HashMap<>();
    private String categoryIntent1;
    private String categoryIntent2;
    private String selectedTimePeriod;


    public static final String TAG = "My Bar Graph";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_my_bar_graph_comparison);

        setTitle("Comparison of 2 categories");

        chart = findViewById(R.id.chartMyBarComp);
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
        chart.getAxisRight().setDrawGridLines(true);
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

        // Get  HashMap from the incoming intent
        Intent intent = getIntent();
        expenseMaps = (HashMap<String, Double>) intent.getSerializableExtra("map");
        expenseMaps2 = (HashMap<String, Double>) intent.getSerializableExtra("map2");
        categoryIntent1 = (intent.getStringExtra("category1"));
        categoryIntent2 = (intent.getStringExtra("category2"));
        selectedTimePeriod = (intent.getStringExtra("selectedTimePeriod"));
        String startDate = (intent.getStringExtra("startDate"));
        String endDate = (intent.getStringExtra("endDate"));

        int timeIntent = Integer.parseInt(selectedTimePeriod);
        //generateDataLine();
        getRemoteData(expenseMaps, expenseMaps2, categoryIntent1, categoryIntent2, timeIntent, startDate, endDate);
    }

    private final int[] colors = new int[]{
            ColorTemplate.VORDIPLOM_COLORS[0],
            ColorTemplate.VORDIPLOM_COLORS[1],
            ColorTemplate.VORDIPLOM_COLORS[2]
    };


    /**
     * @param expenseRemote
     */
    private void getRemoteData(HashMap<String, Double> expenseRemote, HashMap<String, Double> expenseRemote2, String c1, String c2, Integer noOfTimePoints, String startDate, String endDate) {

        try {

            List<String> uniqueLabelsFrom2DataSets = new ArrayList<>(); // This list will have unique labels from list2
            List<String> list1 = new ArrayList<>(expenseRemote.keySet()); // DATASET 1
            List<String> list2 = new ArrayList<>(expenseRemote2.keySet()); // DATASET 2

            // Finding the unique labels from list 2 that are not present in list 1
            uniqueLabelsFrom2DataSets.addAll(
                    list2.stream()
                            .filter(str -> !list1.contains(str))
                            .collect(Collectors.toList()));

            // Adding the unique label from list2 to list 1
            list1.addAll(uniqueLabelsFrom2DataSets);
            Collections.sort(list1); // Sorting the unique collection


            //DATESET 1 ANALYSIS
            Set<String> keySet = expenseRemote.keySet();//Getting Set of keys Categories/months from HashMap
            String[] labelsExpense = keySet.toArray(new String[0]);// Transfer data to an array
            final String[] labelsExpenseCopy = keySet.toArray(new String[0]);//Making a copy of labelsExpense
            final String[] sortedString = labelsExpense;// Storing labels in another array that will be sorted

            // Getting values from HashMap
            Collection<Double> values = expenseRemote.values();
            Double[] valuesRemote = values.toArray(new Double[0]);// Convert data to an array

            // This array will contain the values sorted based on sorting index of labels
            Double[] sortedA = new Double[labelsExpense.length];


            // This list will store values for BarEntry
            ArrayList<BarEntry> valuesExpense = new ArrayList<>();

            // TO SORT THE DATA BASED ON DATES IN THE ORDER (JAN-DEC)
            if (true) {

                Arrays.sort(sortedString); // sort the labels

                // For loops for Printing the results just to confirm the sorting. Will be removed once sorting done.
                for (String labelss : labelsExpenseCopy) {
                    System.out.println("labelsOriginal " + labelss);
                }

                // Loop for sorting the data based on dates.
                for (int i = 0; i < labelsExpense.length; i++) {
                    for (int j = 0; j < valuesRemote.length; j++) {
                        if (sortedString[i] == labelsExpenseCopy[j]) {
                            sortedA[i] = valuesRemote[j];
                        }
                    }
                }
            }

            // This array will contain the values sorted based on sorting index of their labels
            // We are padding them with 0
            Double[] sortedAndUniqueA = new Double[list1.size()];
            for (int i = 0; i < sortedAndUniqueA.length; i++) {
                sortedAndUniqueA[i] = 0.0;
            }

            for (int i = 0; i < list1.size(); i++) {// outer loop for going through the unique labels from list1 and list2
                for (int j = 0; j < labelsExpense.length; j++) {//inner loop for going through dataset1
                    if (list1.get(i).equals(labelsExpense[j])) {
                        sortedAndUniqueA[i] = sortedA[j];
                        break;
                    }
                }
            }

            // DATESET 2 ANALYSIS
            Set<String> keySet2 = expenseRemote2.keySet();//Getting Set of keys Categories/months from HashMap
            String[] labelsExpense2 = keySet2.toArray(new String[0]);// Transfer data to an array
            final String[] labelsExpenseCopy2 = keySet2.toArray(new String[0]);//Making a copy of labelsExpense
            final String[] sortedString2 = labelsExpense2;// Storing labels in another array that will be sorted

            // Getting values from HashMap
            Collection<Double> values2 = expenseRemote2.values();
            Double[] valuesRemote2 = values2.toArray(new Double[0]);// Convert data to an array

            // This array will contain the values sorted based on sorting index of labels
            Double[] sortedA2 = new Double[labelsExpense2.length];

            // BarEntry
            ArrayList<BarEntry> valuesExpense2 = new ArrayList<>();

            if (true) {

                Arrays.sort(sortedString2); // sort the labels

                // Loop for sorting the data based on dates.
                for (int i = 0; i < labelsExpense2.length; i++) {
                    for (int j = 0; j < valuesRemote2.length; j++) {
                        if (sortedString2[i] == labelsExpenseCopy2[j]) {
                            sortedA2[i] = valuesRemote2[j];
                        }
                    }
                }
            }

            // This array will contain the values sorted based on sorting index of labels
            // Pad the array with 0
            Double[] sortedAndUniqueA2 = new Double[list1.size()];
            for (int i = 0; i < sortedAndUniqueA2.length; i++) {
                sortedAndUniqueA2[i] = 0.0;
            }
            // Match the labels from unique labels list with dataset2
            for (int i = 0; i < list1.size(); i++) {// outer loop for going through the unique labels from list1 and list2
                for (int j = 0; j < labelsExpense2.length; j++) {//inner loop for going through DATASET2
                    if (list1.get(i).equals(labelsExpense2[j])) {
                        sortedAndUniqueA2[i] = sortedA2[j];
                        break;
                    }
                }
            }

            //Storing data as BarEntry in the ArrayList for each data set
            // DATASET 2
            for (int i = 0; i < list1.size(); i++) {
                valuesExpense2.add(new BarEntry(i, sortedAndUniqueA2[i].intValue()));
            }
            //DATASET 1
            for (int i = 0; i < list1.size(); i++) {
                valuesExpense.add(new BarEntry(i, sortedAndUniqueA[i].intValue()));
            }

            //BARDATASET
            //DATASET 1
            BarDataSet d4 = new BarDataSet(valuesExpense, c1);
            //d4.setColors(ColorTemplate.VORDIPLOM_COLORS);
            d4.setColors(Color.rgb(164, 228, 251));
            d4.setHighLightAlpha(255);

            //DATADET2
            BarDataSet d5 = new BarDataSet(valuesExpense2, c2);
            //d5.setColors(ColorTemplate.VORDIPLOM_COLORS);
            d5.setColors(Color.rgb(104, 241, 175));
            d5.setHighLightAlpha(255);

            // GET THE LABELS
            String[] theFinalLabels = list1.toArray(new String[0]);


            BarData data = new BarData(d4, d5);
            data.setBarWidth(0.15f); // TO SET BAR WIDTH
            data.setValueTextSize(14f); // SET VALUE SIZE DISPLAYED ABOVE THE BAR
            chart.setData((BarData) data);

            XAxis xAxis = chart.getXAxis();
            xAxis.setTextSize(10); // Set size of x labels
            xAxis.setLabelCount(list1.size()); // set how many labels you want to see
            xAxis.setValueFormatter(new IndexAxisValueFormatter(theFinalLabels)); // set user defined labels
            xAxis.setCenterAxisLabels(true);
            xAxis.setGranularity(1);

            float barSpace = 0.1f;
            float groupSpace = 0.5f;

            chart.setDragEnabled(true);
            chart.getXAxis().setAxisMinimum(0);
            chart.getXAxis().setAxisMaximum(0 + chart.getBarData().getGroupWidth(groupSpace, barSpace) * list1.size());
            chart.getAxisLeft().setAxisMinimum(0);
            chart.groupBars(0, groupSpace, barSpace);


            chart.animateXY(2000, 2000);
            chart.invalidate();

        } catch (NullPointerException ignored) {

        }

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
        saveToGallery(chart, "Bar Graph Comparison");
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
