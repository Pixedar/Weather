package com.pixedar.weather;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

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
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class DataView extends AppCompatActivity {
    private static final UUID SERIAL_PORT_COMMUNICATION_TYPE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private String deviceAddress;
    private LineChart chart;
    private LineChart chart2;
    private LineChart chart3;


    private DataParser dataParser = new DataParser();

    private TextView dataTextView;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.US);
    private SimpleDateFormat fileDateFromat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    Handler handler = new Handler();
   public Runnable refresh;



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_view);
        refresh = new Runnable() {
            public void run() {


        Intent intent = getIntent();
        deviceAddress = intent.getStringExtra(MainActivity.DEVICE_ADRESS);


        dataTextView = (TextView) findViewById(R.id.dataTextView);

        chart = (LineChart) findViewById(R.id.chart);
        chart.setDescription("Temperature");

        chart2 = (LineChart) findViewById(R.id.chart2);
        chart2.setDescription("Pressure");


        chart3 = (LineChart) findViewById(R.id.chart3);
        chart3.setDescription("humidity");



        chart.getAxis(YAxis.AxisDependency.RIGHT).setEnabled(false);
        chart2.getAxis(YAxis.AxisDependency.RIGHT).setEnabled(false);
        chart3.getAxis(YAxis.AxisDependency.RIGHT).setEnabled(false);



        List<Entry> entries = new ArrayList<>();
        List<Entry> entries2 = new ArrayList<>();
        List<Entry> entries3 = new ArrayList<>();



        int tempIndex = 1;
        try {
            File file = new File(getApplicationContext().getFilesDir(), fileDateFromat.format(Calendar.getInstance().getTime()) + ".txt");
            BufferedReader input = new BufferedReader(new FileReader(file));

            int pressTimer = 1;
            int pressIndex = 1;


            while (true) {
                String date = input.readLine();
                String sensorData = input.readLine();

                if (date == null || sensorData == null) break;
                dataParser.parseLine(sensorData);
                if (dataParser.isDataValid()) {
                    // map.put(tempIndex,date)
                    entries.add(new Entry(tempIndex, dataParser.getTemperature()));
                    if(pressTimer == 60){
                        pressIndex++;
                        entries2.add(new Entry(pressIndex, dataParser.getPressure()));
                        pressTimer = 0;
                    }
                    entries3.add(new Entry(tempIndex, dataParser.getHumidity()));
                    tempIndex++;
                    pressTimer++;

                }
            }
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        final int lastTempIndex = tempIndex;
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new AxisValueFormatter() {

            private SimpleDateFormat format = new SimpleDateFormat("HH:mm");

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MINUTE, (int)(lastTempIndex -value)*(-1));
                return format.format(cal.getTime());

            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });

        XAxis xAxis2 = chart2.getXAxis();
        xAxis2.setValueFormatter(new AxisValueFormatter() {

            private SimpleDateFormat format = new SimpleDateFormat("HH:mm");

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.HOUR_OF_DAY, (int) value * (-1));
                return format.format(cal.getTime());
            }

            @Override
            public int getDecimalDigits() {
                return 0;
            }
        });

        XAxis xAxis3 = chart3.getXAxis();
        xAxis3.setValueFormatter(new AxisValueFormatter() {

            private SimpleDateFormat format = new SimpleDateFormat("HH:mm");

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MINUTE, (int) value * (-1));
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
        LineData data = new LineData(set);
        chart.setData(data);
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


       // chart.animateXY(1500, 1500);
       // chart2.animateXY(1500, 1500);
      //  chart3.animateXY(1500, 1500);

        new DeviceConnectionTask().execute();

                handler.postDelayed(refresh, 60000);
            }
        };
        handler.post(refresh);

    }

    private void processNewData(List<String> dataSet) {
        StringBuilder builder = new StringBuilder();

        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.MINUTE, -dataSet.size());
        for (String data : dataSet) {
            dataParser.parseLine(data);
            if (!dataParser.isDataValid()) continue;

            try {
                File file = new File(getApplicationContext().getFilesDir(), fileDateFromat.format(Calendar.getInstance().getTime()) + ".txt");
                file.createNewFile();
                BufferedWriter output = new BufferedWriter(new FileWriter(file, true));

                output.write(String.valueOf(calendar.getTimeInMillis()) + "\n");
                output.write(data + "\n");
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            builder.append(dateFormat.format(calendar.getTime()));
            builder.append(" ");
            builder.append(dataParser.currentToString());
            builder.append("\n");
            calendar.add(Calendar.MINUTE, 1);
        }

        dataTextView.setText(builder.toString());
    }

    private class DeviceConnectionTask extends AsyncTask<Void, Void, List<String>> {
        //private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            //progressDialog = ProgressDialog.show(getApplicationContext(), "Connecting...", "Please wait!");
        }

        @Override
        protected List<String> doInBackground(Void... unused) {
            try {
                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                BluetoothDevice device = adapter.getRemoteDevice(deviceAddress);
                BluetoothSocket socket = device.createInsecureRfcommSocketToServiceRecord(SERIAL_PORT_COMMUNICATION_TYPE);
                socket.connect();
                socket.getOutputStream().write(ArduinoCommands.GET_DATA);
                BufferedReader inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                List<String> rawData = readBtData(inputStream);
                socket.close();
                return rawData;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        private List<String> readBtData(BufferedReader inputStream) throws IOException {
            List<String> rawData = new ArrayList<>();
            while (true) {
                String data = inputStream.readLine();
                if (data.startsWith("E")) {
                    break;
                }
                if (data.startsWith("-")) {
                    continue;
                }
                rawData.add(data);
            }
            return rawData;
        }

        @Override
        protected void onPostExecute(List<String> data) {
            if (data == null) {
                showMessage("Transfer error");
                //finish();
                return;
            }
            if (data.size() == 0) {
                showMessage("No data received");
                return;
            }
            //progressDialog.dismiss();

            processNewData(data);
        }
    }

    private void showMessage(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }
}
