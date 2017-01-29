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
import com.github.mikephil.charting.components.Description;


public class LineChartFragment extends Fragment {
    // Store instance variables
    private String title;
    private int page;


    private LineChart lineChart;
    private int mFillColor = Color.argb(150, 51, 181, 229);

    //TODO: Use real values
    private int[] yValues = {21,26,20,19,24,21,23,29};
    private String[] xValues = {"-7", "-6", "-5", "-4", "-3", "-2", "-1", "0"};



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
        lineChart = (LineChart) view.findViewById(R.id.pie_chart);
        return view;
    }
}
