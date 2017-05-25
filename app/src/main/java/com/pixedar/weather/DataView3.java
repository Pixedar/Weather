package com.pixedar.weather;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
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
    private BarChart barChart3;
    private BarChart barChart4;
    private BarChart barChart5;
    private SimpleDateFormat fileDateFromat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private Prop prop = new Prop();
    private TextView textView;
    private TextView textView2;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_data_view3, container, false);

        textView = (TextView)rootView.findViewById(R.id.textView);
        SimpleDateFormat format4 = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        StringBuilder builder = new StringBuilder();
        builder.append("Najwyższa temperatura: " + MainDataView.globalMaxTemp + "°C, Data:" + format4.format(MainDataView.maxTempCal.getTimeInMillis()).toString());
        textView.setText(builder.toString());
        textView.setTextSize(13);

        SimpleDateFormat format5 = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        textView2 = (TextView)rootView.findViewById(R.id.textView2);
        StringBuilder builder2 = new StringBuilder();
        builder2.append("Najniższa temperatura:  " +MainDataView.globalMinTemp + "°C, Data: " + format5.format(MainDataView.minTempCal.getTimeInMillis()).toString());
        textView2.setText(builder2.toString());
        textView2.setTextSize(13);

        barChart4 = (BarChart) rootView.findViewById(R.id.maxTempChart);
        barChart4.setHighlightPerDragEnabled(false);
        barChart4.setHighlightPerTapEnabled(false);
        barChart4.setDescription("Maksymalna temperatura");
        barChart4.getXAxis().setGranularity(1);
        barChart4.setFitBars(true);

        barChart5 = (BarChart) rootView.findViewById(R.id.amplTempChart);
        barChart5.setHighlightPerDragEnabled(false);
        barChart5.setHighlightPerTapEnabled(false);
        barChart5.setDescription("Aplituda temperatury");
        barChart5.getXAxis().setGranularity(1);
        barChart5.setFitBars(true);

        barChart = (BarChart) rootView.findViewById(R.id.humChart);
        barChart.setHighlightPerDragEnabled(false);
        barChart.setHighlightPerTapEnabled(false);
        barChart.setDescription("Średnia wilgotność");
        barChart.getXAxis().setGranularity(1);
        barChart.setFitBars(true);

        barChart2 = (BarChart) rootView.findViewById(R.id.lightChart);
        barChart2.setHighlightPerDragEnabled(false);
        barChart2.setHighlightPerTapEnabled(false);
        barChart2.setDescription("Średnie nasłonecznienie");
        barChart2.getXAxis().setGranularity(1);
        barChart2.setFitBars(true);

        barChart3 = (BarChart) rootView.findViewById(R.id.avTempChart);
        barChart3.setHighlightPerDragEnabled(false);
        barChart3.setHighlightPerTapEnabled(false);
        barChart3.setDescription("Średnia temperatury");
        barChart3.getXAxis().setGranularity(1);
        barChart3.setFitBars(true);
        barChart3.setFitBars(true);


        int index = 0;
        Calendar cal = Calendar.getInstance();
        List<BarEntry> entries = new ArrayList<>();
        List<BarEntry> entries2 = new ArrayList<>();
        List<BarEntry> entries3 = new ArrayList<>();
        List<BarEntry> entries4 = new ArrayList<>();

        List<BarEntry> entries5 = new ArrayList<>();
        File file;
        while (true){
            try{
                if(index > 7&& Prop.b1E){
                    break;
                }
                if(index > 30&& Prop.b2E){
                    break;
                }

                 file = new File(getActivity().getApplicationContext().getFilesDir(), fileDateFromat.format(cal.getTime()) +"DD" +".txt");
                cal.add(Calendar.DAY_OF_YEAR,-1);
                BufferedReader input = new BufferedReader(new FileReader(file));
                index++;
                float t1 =1;
                float t2 =1;
                float t3 =1;
                float t4 =1;
                float t5 =1;
                try{
                     try{
                     t1 = Float.parseFloat(input.readLine());
                     t2 = Float.parseFloat(input.readLine());
                    t3 = Float.parseFloat(input.readLine());
                    t4 = Float.parseFloat(input.readLine())/10;
                        t5 = Float.parseFloat(input.readLine());

                    }catch (NullPointerException e){
                       e.printStackTrace();
                    }

                }catch(NumberFormatException e){
                    e.printStackTrace();


               }

                entries.add(new BarEntry(index,t1));
                entries2.add(new BarEntry(index,t2));
                entries3.add(new BarEntry(index,t3));
                entries4.add(new BarEntry(index,t4));
                entries5.add(new BarEntry(index,t2 - t5));


            }catch(IOException e){

               break;

            }

        }

        SimpleDateFormat f = new SimpleDateFormat("EEE");
        if(Prop.b1E){
            f = new SimpleDateFormat("EEE");
        }
        if(Prop.b2E ||Prop.b3E ){
            f = new SimpleDateFormat("MM dd");
        }
        final SimpleDateFormat format = f;

        BarDataSet set = new BarDataSet(entries2, "MaxTemp");
        set.setColors(new int[]{Color.rgb(250 -20, 200 -20, 70- 20), Color.rgb(250 , 200, 70)});
        set.setValueTextColor(Color.BLACK);
        set.setValueTextSize(10f);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setDrawValues(false);
        BarData data = new BarData(set);
        barChart4.setData(data);

        barChart4.getXAxis().setValueFormatter(new AxisValueFormatter() {
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
        barChart4.invalidate();

        BarDataSet set2 = new BarDataSet(entries5, "Ampl");
        set2.setColors(new int[]{Color.rgb(200, 210, 120), Color.rgb(200-20, 210-20, 120-20)});
        set2.setValueTextColor(Color.BLACK);
        set2.setValueTextSize(10f);
        set2.setAxisDependency(YAxis.AxisDependency.LEFT);
        set2.setDrawValues(false);
        BarData data2 = new BarData(set2);
        barChart5.setData(data2);

        barChart5.getXAxis().setValueFormatter(new AxisValueFormatter() {
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
        barChart5.invalidate();

        BarDataSet set3 = new BarDataSet(entries3,"");
        set3.setColors(new int[]{Color.rgb(128 +5, 210, 220), Color.rgb(148+5, 230, 240)});
       // set3.setValueTextColor(Color.rgb(128 +5, 210, 220));
        set3.setDrawValues(false);
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
        set4.setColors(new int[]{Color.rgb(240-15, 238-15, 70-15), Color.rgb(240-35, 238-35, 70-35)});
       // set4.setValueTextColor(Color.BLACK);
        set4.setValueTextSize(10f);
        set4.setDrawValues(false);
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

        BarDataSet set5 = new BarDataSet(entries,"");
        set5.setColors(new int[]{Color.rgb(150, 210, 120), Color.rgb(150-20, 210-20, 120-20)});
      //  set5.setValueTextColor(Color.BLACK);
        set5.setDrawValues(false);
        set5.setValueTextSize(10f);
        set5.setAxisDependency(YAxis.AxisDependency.LEFT);
        BarData barData3 = new BarData(set5);
        barChart3.setData(barData3);
        barChart3.getXAxis().setValueFormatter(new AxisValueFormatter() {
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
        barChart3.invalidate();



        return rootView;
    }

}
