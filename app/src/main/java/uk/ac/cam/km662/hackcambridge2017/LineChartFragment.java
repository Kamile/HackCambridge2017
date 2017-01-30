package uk.ac.cam.km662.hackcambridge2017;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
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

        lineChart.setDrawBorders(true);

        // no description text
        lineChart.getDescription().setEnabled(false);

        // if disabled, scaling can be done on x- and y-axis separately
        lineChart.setPinchZoom(false);

        Legend l = lineChart.getLegend();
        l.setEnabled(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setAxisMaximum(0);
        xAxis.setAxisMinimum(5);
        xAxis.setEnabled(true);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setAxisMaximum(50);
        leftAxis.setAxisMinimum(0);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setDrawZeroLine(false);
        leftAxis.setDrawGridLines(false);

        lineChart.getAxisRight().setEnabled(false);

        List<Entry> entries = new ArrayList<Entry>();
        entries.add(new Entry(0,30));
        entries.add(new Entry(1, 27));
        entries.add(new Entry(2, 20));
        entries.add(new Entry(3, 35));
        entries.add(new Entry(4, 47));

        // Get the paint renderer to create the line shading.
        Paint paint = lineChart.getRenderer().getPaintRender();
        int height = lineChart.getHeight();

        LinearGradient linGrad = new LinearGradient(0, 0, 0, height,
                getResources().getColor(R.color.lineGraphHigh),
                getResources().getColor(R.color.lineGraphLow),
                Shader.TileMode.REPEAT);
        paint.setShader(linGrad);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        lineChart.getAxisRight().setEnabled(false);


        LineDataSet dataset = new LineDataSet(entries, "day");
        //LineData data = new LineData(dataset);
        //lineChart.setData(data); // set the data and list of lables into chart
        //dataset.setDrawFilled(true); // to fill the below area of line in graph


        //lineChart.animateX(2500);

        // get the legend (only possible after setting data)
        l = lineChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);


        lineChart.invalidate();
        return view;
    }


}

