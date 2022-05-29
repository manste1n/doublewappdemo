package com.sda5.double2app.activities.Graphs;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.sda5.double2app.R;

import java.util.ArrayList;

public class MyPieChartActivity extends DemoBase implements OnChartValueSelectedListener {

    private PieChart chart;

    private ArrayList<String> catPie = new ArrayList<>();
    private ArrayList<Double> valuePie = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_my_pie_chart);

        setTitle("PieChart of All Expenses");

        chart = findViewById(R.id.chartpie);
        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(10, 10, 10, 10);

        chart.setDragDecelerationFrictionCoef(0.95f);

        chart.setCenterTextTypeface(tfLight);
        chart.setCenterText(generateCenterSpannableText());

        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);

        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);

        chart.setHoleRadius(58f);
        chart.setTransparentCircleRadius(61f);

        chart.setDrawCenterText(true);

        chart.setRotationAngle(0);
        // enable rotation of the chart by touch
        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);

        // chart.setUnit(" €");
        // chart.setDrawUnitsInChart(true);

        // add a selection listener
        chart.setOnChartValueSelectedListener(this);

        chart.animateY(1400, Easing.EaseInOutQuad);
        // chart.spin(2000, 0, 360);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(15f);
        l.setYEntrySpace(2f);
        l.setYOffset(0f);
        l.setFormSize(10f);


        // entry label styling
        chart.setEntryLabelColor(Color.BLACK);
        chart.setEntryLabelTypeface(tfRegular);
        chart.setEntryLabelTextSize(12f);


        chart.setDrawSliceText(false);

        // Get data from incoming intent
        Intent intent = getIntent();
        catPie = (ArrayList<String>)intent.getSerializableExtra("categories");
        valuePie = (ArrayList<Double>)intent.getSerializableExtra("categoriesSumAmount");



        generateDataPie(valuePie,catPie);


    }

    /**
     * generates a random ChartData object with just one DataSet
     *
     * @return Pie data
     */
    private void generateDataPie(ArrayList<Double> remoteExpensePie, ArrayList<String> remoteCategoryPie) {

        //ArrayList<Integer> remoteExpensePieInt = new ArrayList<>();
        // Transfer data to an array
        String[] labelsExpense = remoteCategoryPie.toArray(new String[0]);

        // Convert data to an array
        Double[] test = remoteExpensePie.toArray(new Double[0]);

        ArrayList<PieEntry> entries = new ArrayList<>();

        for (int i = 1; i < labelsExpense.length; i++) {
            entries.add(new PieEntry((float) (test[i].intValue()), labelsExpense[i]));
        }

        PieDataSet dPie = new PieDataSet(entries, "");

        dPie.setDrawIcons(false);

        dPie.setSliceSpace(3f);
        dPie.setIconsOffset(new MPPointF(0, 40));
        dPie.setSelectionShift(5f);

        // add a lot of colors


        final int[] MY_COLORS = {
                Color.rgb(255,192,0),
                Color.rgb(177,30,50),
                Color.rgb(146,208,80),
                Color.rgb(50,176,80),
                Color.rgb(79,129,189),
                Color.rgb(20,179,220),
                Color.rgb(100,100,100),
                Color.rgb(20,100,0)};
        ArrayList<Integer> colors = new ArrayList<>();
        for(int c: MY_COLORS) colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dPie.setColors(colors);

        PieData data = new PieData(dPie);
        data.setValueFormatter(new PercentFormatter(chart));
        data.setValueTextSize(15f);
        data.setValueTextColor(Color.WHITE);
        data.setValueTypeface(tfLight);
        chart.setData(data);


        // undo all highlights
        chart.highlightValues(null);

        chart.invalidate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pie, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.viewGithub: {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("https://github.com/walletDroid"));
                startActivity(i);
                break;
            }
            case R.id.actionToggleValues: {
                for (IDataSet<?> set : chart.getData().getDataSets())
                    set.setDrawValues(!set.isDrawValuesEnabled());

                chart.invalidate();
                break;
            }
            case R.id.actionToggleIcons: {
                for (IDataSet<?> set : chart.getData().getDataSets())
                    set.setDrawIcons(!set.isDrawIconsEnabled());

                chart.invalidate();
                break;
            }
            case R.id.actionToggleHole: {
                if (chart.isDrawHoleEnabled())
                    chart.setDrawHoleEnabled(false);
                else
                    chart.setDrawHoleEnabled(true);
                chart.invalidate();
                break;
            }
            case R.id.actionToggleMinAngles: {
                if (chart.getMinAngleForSlices() == 0f)
                    chart.setMinAngleForSlices(36f);
                else
                    chart.setMinAngleForSlices(0f);
                chart.notifyDataSetChanged();
                chart.invalidate();
                break;
            }
            case R.id.actionToggleCurvedSlices: {
                boolean toSet = !chart.isDrawRoundedSlicesEnabled() || !chart.isDrawHoleEnabled();
                chart.setDrawRoundedSlices(toSet);
                if (toSet && !chart.isDrawHoleEnabled()) {
                    chart.setDrawHoleEnabled(true);
                }
                if (toSet && chart.isDrawSlicesUnderHoleEnabled()) {
                    chart.setDrawSlicesUnderHole(false);
                }
                chart.invalidate();
                break;
            }
            case R.id.actionDrawCenter: {
                if (chart.isDrawCenterTextEnabled())
                    chart.setDrawCenterText(false);
                else
                    chart.setDrawCenterText(true);
                chart.invalidate();
                break;
            }
            case R.id.actionToggleXValues: {

                chart.setDrawEntryLabels(!chart.isDrawEntryLabelsEnabled());
                chart.invalidate();
                break;
            }
            case R.id.actionTogglePercent:
                chart.setUsePercentValues(!chart.isUsePercentValuesEnabled());
                chart.invalidate();
                break;
            case R.id.animateX: {
                chart.animateX(1400);
                break;
            }
            case R.id.animateY: {
                chart.animateY(1400);
                break;
            }
            case R.id.animateXY: {
                chart.animateXY(1400, 1400);
                break;
            }
            case R.id.actionToggleSpin: {
                chart.spin(1000, chart.getRotationAngle(), chart.getRotationAngle() + 360, Easing.EaseInOutCubic);
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
        }
        return true;
    }


    @Override
    protected void saveToGallery() {
        saveToGallery(chart, "PieChartActivity");
    }

    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("WalletDroid\n SDA5 OSMMZPA");
        s.setSpan(new RelativeSizeSpan(1.7f), 0, 11, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 11, s.length() - 12, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 11, s.length() - 12, 0);
        s.setSpan(new RelativeSizeSpan(.8f), 11, s.length() - 12, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 11, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 11, s.length(), 0);
        return s;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

        if (e == null)
            return;
        Log.i("VAL SELECTED",
                "Value: " + e.getY() + ", index: " + h.getX()
                        + ", DataSet index: " + h.getDataSetIndex());
    }

    @Override
    public void onNothingSelected() {
        Log.i("PieChart", "nothing selected");
    }


}
