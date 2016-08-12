package com.pixedar.weather;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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

    private DataParser dataParser = new DataParser();

    private TextView dataTextView;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.US);
    private SimpleDateFormat fileDateFromat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_view);

        Intent intent = getIntent();
        deviceAddress = intent.getStringExtra(MainActivity.DEVICE_ADRESS);

        dataTextView = (TextView) findViewById(R.id.dataTextView);
        new DeviceConnectionTask().execute();
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

                int hours = calendar.get(Calendar.HOUR_OF_DAY);
                int minutes = calendar.get(Calendar.MINUTE);

                output.write(Integer.toString(hours) + ":" + Integer.toString(minutes) + "\n");
                output.write(data + "\n");
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
                finish();
                return;
            }
            if (data.size() == 0) {
                showMessage("No data received");
                finish();
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
