package uk.ac.cam.km662.hackcambridge2017;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;


public class LineChartFragment extends Fragment {
    // Store instance variables
    private String title;
    private int page;


    private LineChart lineChart;
    private int mFillColor = Color.argb(150, 51, 181, 229);

    //TODO: Use real values
//    private int[] yValues = {21,26,20,19,24,21,23,29};
//    private String[] xValues = {"-7", "-6", "-5", "-4", "-3", "-2", "-1", "0"};



    // newInstance constructor for creating fragment with arguments
    public static LineChartFragment newInstance(int page, String title) {
        LineChartFragment fragment = new LineChartFragment();
        Bundle args = new Bundle();
        args.putInt("1", page);
        args.putString("Total score over time", title);
        fragment.setArguments(args);
        return fragment;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("1", 0);
        title = getArguments().getString("Total score over time");
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_line_chart, container, false);
        lineChart = (LineChart) view.findViewById(R.id.line_chart);
        lineChart.setBackgroundColor(Color.WHITE);
        lineChart.setGridBackgroundColor(mFillColor);
        lineChart.setDrawGridBackground(true);

        lineChart.setDrawBorders(true);

        // no description text
        lineChart.getDescription().setEnabled(false);

        // if disabled, scaling can be done on x- and y-axis separately
        lineChart.setPinchZoom(false);

        Legend l = lineChart.getLegend();
        l.setEnabled(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setAxisMaximum(0);
        xAxis.setAxisMinimum(-7);
        xAxis.setEnabled(true);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setAxisMaximum(100);
        leftAxis.setAxisMinimum(0);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setDrawZeroLine(false);
        leftAxis.setDrawGridLines(false);

        lineChart.getAxisRight().setEnabled(false);

        // add data
        setData(14, 50);

        lineChart.invalidate();
        return view;
    }

    private void setData(int count, float range) {

        ArrayList<Entry> yVals1 = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {
            float val = (float) (Math.random() * range) + 50;
            yVals1.add(new Entry(i, val));
        }
        LineDataSet set1;

        if (lineChart.getData() != null &&
                lineChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet)lineChart.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            lineChart.getData().notifyDataChanged();
            lineChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(yVals1, "DataSet 1");

            set1.setAxisDependency(YAxis.AxisDependency.LEFT);
            set1.setColor(Color.rgb(255, 241, 46));
            set1.setDrawCircles(false);
            set1.setLineWidth(2f);
            set1.setCircleRadius(3f);
            set1.setFillAlpha(255);
            set1.setDrawFilled(true);
            set1.setFillColor(Color.WHITE);
            set1.setHighLightColor(Color.rgb(244, 117, 117));
            set1.setDrawCircleHole(false);
            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return lineChart.getAxisLeft().getAxisMinimum();
                }
            });


            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the dataset

            // create a data object with the datasets
            LineData data = new LineData(dataSets);
            data.setDrawValues(true);

            // set data
            lineChart.setData(data);
        }
    }
}
