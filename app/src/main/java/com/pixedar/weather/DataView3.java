package com.pixedar.weather;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.AxisValueFormatter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DataView3 extends Fragment {
    private BarChart barChart;
    private BarChart barChart2;
    private CombinedChart combinedChart;
    private SimpleDateFormat fileDateFromat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private Prop prop = new Prop();
    private TextView textView;
    private TextView textView2;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_data_view3, container, false);

        textView = (TextView)rootView.findViewById(R.id.textView);
        SimpleDateFormat format4 = new SimpleDateFormat("yyyy.MM.dd hh:mm");
        StringBuilder builder = new StringBuilder();
        builder.append("Max temp: " + DataView.globalMaxTemp + "°C " + format4.format(DataView.maxTempCal.getTimeInMillis()).toString());
        textView.setText(builder.toString());

        SimpleDateFormat format5 = new SimpleDateFormat("yyyy.MM.dd hh:mm");
        textView2 = (TextView)rootView.findViewById(R.id.textView2);
        StringBuilder builder2 = new StringBuilder();
        builder2.append("Min temp: " +DataView.globalMinTemp + "°C " + format5.format(DataView.minTempCal.getTimeInMillis()).toString());
        textView2.setText(builder2.toString());

        combinedChart =(CombinedChart) rootView.findViewById(R.id.combinedChart);
        combinedChart.getAxis(YAxis.AxisDependency.RIGHT).setEnabled(false);
        combinedChart.setHighlightPerDragEnabled(false);
        combinedChart.setHighlightPerTapEnabled(false);
        combinedChart.setDescription("");
        combinedChart.getXAxis().setGranularity(1);
        combinedChart.getLegend().setEnabled(true);

        barChart = (BarChart) rootView.findViewById(R.id.barchart);
        barChart.setHighlightPerDragEnabled(false);
        barChart.setHighlightPerTapEnabled(false);
        barChart.setDescription("Średnia wilgotnośc");
        barChart.getXAxis().setGranularity(1);

        barChart2 = (BarChart) rootView.findViewById(R.id.barchart2);
        barChart2.setHighlightPerDragEnabled(false);
        barChart2.setHighlightPerTapEnabled(false);
        barChart2.setDescription("Średnie nasłonecznienie");
        barChart2.getXAxis().setGranularity(1);


        int index = 0;
        Calendar cal = Calendar.getInstance();
        List<BarEntry> entries = new ArrayList<>();
        List<Entry> entries2 = new ArrayList<>();
        List<BarEntry> entries3 = new ArrayList<>();
        List<BarEntry> entries4 = new ArrayList<>();

        while (true){
            try{
                if(index > 7&& Prop.b1E){
                    break;
                }
                if(index > 30&& Prop.b2E){
                    break;
                }
                cal.add(Calendar.DAY_OF_MONTH,-index);
                File file = new File(getActivity().getApplicationContext().getFilesDir(), fileDateFromat.format(cal.getTime()) +"DD" +".txt");
                BufferedReader input = new BufferedReader(new FileReader(file));
                index++;
                float t1 =1;
                float t2 =1;
                float t3 =1;
                float t4 =1;
                try{
                     t1 = Float.parseFloat(input.readLine());
                     t2 = Float.parseFloat(input.readLine());
                    t3 = Float.parseFloat(input.readLine());
                    t4 = Float.parseFloat(input.readLine())/10;
                }catch(NumberFormatException e){
                    e.printStackTrace();
               }

                entries.add(new BarEntry(index,t1));
                entries2.add(new BarEntry(index,t2));
                entries3.add(new BarEntry(index,t3));
                entries4.add(new BarEntry(index,t4));

            }catch(IOException e){
               break;
            }

        }

        BarDataSet set = new BarDataSet(entries, "Średnia");
        set.setColors(new int[]{Color.rgb(61, 165, 255), Color.rgb(23, 197, 255)});
        set.setValueTextColor(Color.rgb(61, 165, 255));
        set.setValueTextSize(10f);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);

        BarData data = new BarData(set);
        LineDataSet set2 = new LineDataSet(entries2,"Max temp");
        set2.setColor(Color.rgb(240, 238, 70));
        set2.setLineWidth(2.5f);
        set2.setCircleColor(Color.rgb(240, 238, 70));
        set2.setCircleRadius(5f);
        set2.setFillColor(Color.rgb(240, 238, 70));
        set2.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        set2.setDrawValues(true);
        set2.setValueTextSize(10f);
        set2.setValueTextColor(Color.BLACK);

        LineData data2 = new LineData(set2);
        CombinedData combinedData =  new CombinedData();

        combinedData.setData(data2);
        combinedData.setData(data);
        combinedChart.setData(combinedData);

        SimpleDateFormat f = new SimpleDateFormat("EEE");
        if(Prop.b1E){
            f = new SimpleDateFormat("EEE");
        }
        if(Prop.b2E ||Prop.b3E ){
            f = new SimpleDateFormat("MM dd");
        }
        final SimpleDateFormat format = f;

        combinedChart.getXAxis().setValueFormatter(new AxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_WEEK,(int)(value-1)*(-1));
                return format.format(cal.getTime());
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });
        combinedChart.invalidate();

        BarDataSet set3 = new BarDataSet(entries3,"");
        set3.setColors(new int[]{Color.rgb(61, 165, 255), Color.rgb(23, 197, 255)});
        set3.setValueTextColor(Color.rgb(61, 165, 255));
        set3.setValueTextSize(10f);
        set3.setAxisDependency(YAxis.AxisDependency.LEFT);
        BarData barData = new BarData(set3);
        barChart.setData(barData);
        barChart.getXAxis().setValueFormatter(new AxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_WEEK,(int)(value-1)*(-1));
                return format.format(cal.getTime());
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });
        barChart.invalidate();

        BarDataSet set4 = new BarDataSet(entries4,"");
        set4.setColors(new int[]{Color.rgb(61, 165, 255), Color.rgb(23, 197, 255)});
        set4.setValueTextColor(Color.rgb(61, 165, 255));
        set4.setValueTextSize(10f);
        set4.setAxisDependency(YAxis.AxisDependency.LEFT);
        BarData barData2 = new BarData(set4);
        barChart2.setData(barData2);
        barChart2.getXAxis().setValueFormatter(new AxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_WEEK,(int)(value-1)*(-1));
                return format.format(cal.getTime());
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });
        barChart2.invalidate();




        return rootView;
    }

}
