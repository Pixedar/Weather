package com.pixedar.weather;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Switch;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.AxisValueFormatter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

public class DataView extends Fragment implements FragmentInterface{
    private static final UUID SERIAL_PORT_COMMUNICATION_TYPE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private String deviceAddress;
    private LineChart chart;
    private LineChart chart2;
    private LineChart chart3;
    private LineChart chart4;


    private DataParser dataParser = new DataParser();

    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.US);
    private SimpleDateFormat fileDateFromat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    Handler handler = new Handler();
    public Runnable refresh;
    private boolean anim = true;

    private DatePicker datePicker;
    private Switch setMulti;

    private Prop prop = new Prop();

    Random random = new Random();
    String date ="100000000";
    public static float globalMaxTemp = -50;
    public static float globalMinTemp =50;
    public static Calendar maxTempCal = Calendar.getInstance();
    public static Calendar minTempCal = Calendar.getInstance();

    private TextView textView;
    private TextView textView2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.activity_data_view, container, false);

        textView = (TextView) rootView.findViewById(R.id.textView3);
        textView2 = (TextView) rootView.findViewById(R.id.textView4);

        chart = (LineChart) rootView.findViewById(R.id.chart);
        chart.setDescription("Temperatura");
        chart.getLegend().setEnabled(false);


        chart2 = (LineChart) rootView.findViewById(R.id.chart2);
        chart2.setDescription("Ćiśnienie");
        chart2.getLegend().setEnabled(false);


        chart3 = (LineChart) rootView.findViewById(R.id.chart3);
        chart3.setDescription("Wilgotność");
        chart3.getLegend().setEnabled(false);

        chart4 = (LineChart) rootView.findViewById(R.id.chart4);
        chart4.setDescription("Natężenie światła");
        chart4.getLegend().setEnabled(false);


        chart.getAxis(YAxis.AxisDependency.RIGHT).setEnabled(false);
        chart2.getAxis(YAxis.AxisDependency.RIGHT).setEnabled(false);
        chart3.getAxis(YAxis.AxisDependency.RIGHT).setEnabled(false);
        chart4.getAxis(YAxis.AxisDependency.RIGHT).setEnabled(false);
        anim = true;
        try{
            File file = new File(getActivity().getApplicationContext().getFilesDir(), "records.txt");
            BufferedReader input = new BufferedReader(new FileReader(file));
            globalMaxTemp = Float.valueOf(input.readLine());
            globalMinTemp = Float.valueOf(input.readLine());
            maxTempCal.setTimeInMillis(Long.valueOf(input.readLine()));
            minTempCal.setTimeInMillis(Long.valueOf(input.readLine()));
        }catch(IOException e){

        }

        refreshData();
        return rootView;
    }


    private void refreshData() {

        List<Entry> entries = new ArrayList<>();
        List<Entry> entries2 = new ArrayList<>();
        List<Entry> entries3 = new ArrayList<>();
        List<Entry> entries4 = new ArrayList<>();

        if (prop.multiIsChecked) {
            chart.getLegend().setEnabled(true);
        } else {
           chart.getLegend().setEnabled(false);
        }

        int index = 0;
        int pressTimer = 1;
        int pressIndex = 1;
        float temp;
        float lastTemp = 20;
        float sumTemp = 0;
        float maxTemp = -50;
        float sumHum =0;
        float sumLight =0;
        float minTemp = 50;
        File file;
        try {
            //rootView.getContext().getApplicationContext()
            file = new File(getActivity().getApplicationContext().getFilesDir(), fileDateFromat.format(prop.calendar.getTime()) + ".txt");
            BufferedReader input = new BufferedReader(new FileReader(file));


            while (true) {
                 String da = input.readLine();
                String sensorData = input.readLine();

                if (da == null || sensorData == null) {
                    if (!prop.multiIsChecked) {
                        break;
                    } else {
                        SimpleDateFormat sdf = new SimpleDateFormat("MM dd");
                        String d = sdf.format(prop.calendar.getTime()).toString();
                        LineDataSet set = new LineDataSet(entries, d);
                        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                        set.setDrawCircles(false);
                        set.setDrawValues(false);
                        set.setDrawFilled(true);
                        int c =Color.argb(255, random.nextInt(252) + 1, random.nextInt(252) + 1, random.nextInt(252) + 1);
                        set.setColor(c);
                        set.setFillColor(c);
                        set.setFillAlpha(100);
                            try{
                                prop.dataSets.set(1,set);
                            }catch(IndexOutOfBoundsException e){
                                prop.dataSets.add(set);
                            }
                        break;
                    }
                }
                date = da;
                dataParser.parseLine(sensorData);
                if (dataParser.isDataValid()) {
                    temp = dataParser.getTemperature();
                    if (temp != 85) {
                        entries.add(new Entry(index, temp));
                        lastTemp = temp;
                        sumTemp = sumTemp + temp;
                        if(temp > maxTemp){
                            maxTemp = temp;
                        }
                        if(temp < minTemp){
                            minTemp = temp;
                        }
                    } else {
                        entries.add(new Entry(index, lastTemp));
                    }

                    if (pressTimer == 60) {
                        pressIndex++;
                        entries2.add(new Entry(pressIndex, dataParser.getPressure()));
                        pressTimer = 0;
                    }
                    entries3.add(new Entry(index, dataParser.getHumidity()));
                    entries4.add(new Entry(index, dataParser.getLight() / 10));
                    index++;
                    pressTimer++;
                    sumHum = sumHum + dataParser.getHumidity();
                    sumLight = sumLight + dataParser.getLight();
                }
            }
            input.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder st = new StringBuilder();
        st.append(String.valueOf(dataParser.getTemperature()) + "°C   " + String.valueOf(dataParser.getHumidity()) + "%   " + String.valueOf(dataParser.getPressure()) + "Hpa");
        textView.setText(st.toString());


        StringBuilder st2 = new StringBuilder();
        st2.append("Max " + String.valueOf(maxTemp) + "°C " +" Min " + String.valueOf(minTemp) + "°C");
        textView2.setText(st2.toString());
        textView2.setTextSize(10.0f);
        try {
            File file2 = new File(getActivity().getApplicationContext().getFilesDir(), fileDateFromat.format(prop.calendar.getTime()) + "DD"+".txt");
            file2.createNewFile();
            BufferedWriter output = new BufferedWriter(new FileWriter(file2, false));
            output.write(sumTemp/index+ "\n");
            output.write(maxTemp+ "\n");
            output.write(sumHum/index+ "\n");
            output.write(sumLight/index+ "\n");

            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(maxTemp > globalMaxTemp|| minTemp < globalMinTemp){
            try{
                File file3 = new File(getActivity().getApplicationContext().getFilesDir(),"records.txt");
                file3.createNewFile();
                BufferedWriter output = new BufferedWriter(new FileWriter(file3, false));
                output.write(maxTemp+ "\n");
                output.write(minTemp+ "\n");
                if(maxTemp > globalMaxTemp){
                    output.write(String.valueOf(Prop.calendar.getTimeInMillis())+ "\n");
                    globalMaxTemp = maxTemp;
                    maxTempCal.setTime(Prop.calendar.getTime());
                }else{
                    output.write(String.valueOf(maxTempCal.getTimeInMillis())+ "\n");
                }
                if(minTemp < globalMinTemp){
                    output.write(String.valueOf(Prop.calendar.getTimeInMillis())+ "\n");
                    globalMinTemp = minTemp;
                    minTempCal.setTime(Prop.calendar.getTime());
                }else{
                    output.write(String.valueOf(minTempCal.getTimeInMillis())+ "\n");

                }


                output.close();
            }catch(IOException e){

            }
        }



        final int lastIndex = index;
        final int lastPressIndex = pressIndex;
        XAxis xAxis = chart.getXAxis();
        XAxis xAxis2 = chart2.getXAxis();
        XAxis xAxis3 = chart3.getXAxis();
        XAxis xAxis4 = chart4.getXAxis();

        xAxis2.setGranularity(1);
        final SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(Long.valueOf(date));


        xAxis.setValueFormatter(new AxisValueFormatter() {

            // private SimpleDateFormat format = new SimpleDateFormat("HH:mm");

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(c.getTime());
                cal.add(Calendar.MINUTE, (int) (lastIndex - value) * (-1));
                return format.format(cal.getTime());

            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });


        xAxis2.setValueFormatter(new AxisValueFormatter() {


            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(c.getTime());
                cal.add(Calendar.HOUR_OF_DAY, (int) (lastPressIndex - value) * (-1));
                return format.format(cal.getTime());
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });


        xAxis3.setValueFormatter(new AxisValueFormatter() {


            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(c.getTime());
                cal.add(Calendar.MINUTE, (int) (lastIndex - value) * (-1));
                return format.format(cal.getTime());
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });

        xAxis4.setValueFormatter(new AxisValueFormatter() {


            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(c.getTime());
                cal.add(Calendar.MINUTE, (int) (lastIndex - value) * (-1));
                return format.format(cal.getTime());
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });

        LineDataSet set = new LineDataSet(entries, "Label");
        set.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        set.setDrawFilled(true);
        set.setDrawCircles(false);
        set.setDrawValues(false);

        if (!prop.multiIsChecked) {
            LineData data = new LineData(set);
            chart.setData(data);
        } else {
            LineData data = new LineData(prop.dataSets);

            chart.setData(data);
        }

        chart.invalidate();


        LineDataSet set2 = new LineDataSet(entries2, "Label21");
        set2.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        set2.setDrawCircles(false);

        LineData data2 = new LineData(set2);
        chart2.setData(data2);

        chart2.invalidate();


        LineDataSet set3 = new LineDataSet(entries3, "Label21");
        set3.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        set3.setDrawCircles(false);
        set3.setDrawFilled(true);
        set3.setDrawValues(false);

        LineData data3 = new LineData(set3);
        chart3.setData(data3);

        chart3.invalidate();

        LineDataSet set4 = new LineDataSet(entries4, "Label21");
        set4.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        set4.setDrawCircles(false);
        set4.setDrawFilled(true);
        set4.setDrawValues(false);

        LineData data4 = new LineData(set4);
        chart4.setData(data4);

        chart4.invalidate();

        if (anim) {
            chart.animateXY(1500, 1500);
            chart2.animateXY(1500, 1500);
            chart3.animateXY(1500, 1500);
            anim = false;
        }

    }

    @Override
    public void fragmentBecameVisible() {
       refreshData();
    }

}
