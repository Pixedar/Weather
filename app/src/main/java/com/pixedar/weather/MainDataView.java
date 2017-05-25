package com.pixedar.weather;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;

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

public class MainDataView extends FragmentActivity {
    private String deviceAddress;
    private static final UUID SERIAL_PORT_COMMUNICATION_TYPE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    Handler handler = new Handler();
    private DataParser dataParser = new DataParser();
    public Runnable refresh;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.US);
    private SimpleDateFormat fileDateFromat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    public static ViewPager viewPager;
    public static TabsPagerAdapter mAdapter;

    private boolean dataProcessed = false;
    private boolean dataRecieved = false;

    public static float globalMaxTemp = -50;
    public static float globalMinTemp =50;
    public static Calendar maxTempCal = Calendar.getInstance();
    public static Calendar minTempCal = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_data_view);

        try{
            File file = new File(getApplicationContext().getFilesDir(), "records.txt");
            BufferedReader input = new BufferedReader(new FileReader(file));
            globalMaxTemp = Float.valueOf(input.readLine());
            globalMinTemp = Float.valueOf(input.readLine());
            maxTempCal.setTimeInMillis(Long.valueOf(input.readLine()));
            minTempCal.setTimeInMillis(Long.valueOf(input.readLine()));
        }catch(IOException e){
        Log.d("nie udalo sie","");
        }

        viewPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        viewPager.setCurrentItem(1);


        Intent intent = getIntent();
        deviceAddress = intent.getStringExtra(MainActivity.DEVICE_ADRESS);


        refresh = new Runnable() {
            public void run() {
                if(!dataRecieved){
                    new DeviceConnectionTask().execute();
                }
                if(dataProcessed && dataRecieved) {
                    FragmentInterface fragment = (FragmentInterface) mAdapter.instantiateItem(viewPager, 1);
                    fragment.fragmentBecameVisible();
                    dataProcessed = false;
                    dataRecieved = false;
                }
                handler.postDelayed(refresh,60002);
            }
        };
        handler.post(refresh);

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
              //  Log.d("PP","Do in Background  ");
                return null;
            }
        }

        private List<String> readBtData(BufferedReader inputStream) throws IOException {
            List<String> rawData = new ArrayList<>();
            while (true) {
                String data = inputStream.readLine();
                if (data.startsWith("E")) {
                    dataRecieved =true;
                    break;
                }
                if (data.startsWith("-")) {
                    continue;
                }
                rawData.add(data);
              //  Log.d("PPd ",data);
            }
            return rawData;
        }

        @Override
        protected void onPostExecute(List<String> data) {
            if (data == null) {
                //  showMessage("Transfer error");
              //  Log.d("PP","Tranfer error");
                return;
            }
            if (data.size() == 0) {
                // showMessage("No data received");
               // Log.d("PP","no data recieved");
                return;
            }
            //progressDialog.dismiss();
           // dataProcessed = true;
            processNewData(data);
        }
    }

    private void processNewData(List<String> dataSet) {
        StringBuilder builder = new StringBuilder();

         Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.MINUTE, -dataSet.size());

        for (String data : dataSet) {
            dataParser.parseLine(data);
            if (!dataParser.isDataValid()) continue;

            try {
               // File file = new File(getApplicationContext().getFilesDir(), fileDateFromat.format(Calendar.getInstance().getTime()) + ".txt");
                File file = new File(getApplicationContext().getFilesDir(), fileDateFromat.format(calendar.getTime()) + ".txt");
                file.createNewFile();
                BufferedWriter output = new BufferedWriter(new FileWriter(file, true));

                output.write(String.valueOf(calendar.getTimeInMillis()) + "\n");
                output.write(data + "\n");
                output.close();
            } catch (IOException e) {
              //  Log.d("PP", "file error");
                e.printStackTrace();
            }

            builder.append(dateFormat.format(calendar.getTime()));
            builder.append(" ");
            builder.append(dataParser.currentToString());
            builder.append("\n");
            calendar.add(Calendar.MINUTE, 1);
        }
       // dataTextView.setText(builder.toString());
        dataProcessed = true;
    }

}
