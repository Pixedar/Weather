package com.pixedar.weather;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    public static final String DEVICE_ADRESS = "com.pixedar.weather.DEVICE_ADRESS";

    private static final int BLUETOOTH_DIALOG_FINISHED = 1;
    private BluetoothAdapter bluetooth;
    private ListView devicesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        devicesList = (ListView) findViewById(R.id.deviceList);

        bluetooth = BluetoothAdapter.getDefaultAdapter();
        if (bluetooth == null) {
            Toast.makeText(getApplicationContext(), "Buy device with Bluetooth lol", Toast.LENGTH_LONG).show();
            finish();
        } else {
            if (bluetooth.isEnabled()) {
                scanForDevices();
            } else {
                Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(turnBTon, BLUETOOTH_DIALOG_FINISHED);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == BLUETOOTH_DIALOG_FINISHED) {
            scanForDevices();
        }
    }

    private void scanForDevices() {

        Set<BluetoothDevice> devices = bluetooth.getBondedDevices();
        ArrayList<String> list = new ArrayList<>();

        if (devices.size() > 0) {
            for (BluetoothDevice bt : devices) {
                list.add(bt.getName() + "\n" + bt.getAddress());
            }
        } else {
            Toast.makeText(getApplicationContext(), "No paired devices found", Toast.LENGTH_LONG).show();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        devicesList.setAdapter(adapter);
        devicesList.setOnItemClickListener(myListClickListener); //Method called when the device from the list is clicked
    }

    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String info = ((TextView) view).getText().toString();
            String address = info.substring(info.length() - 17);

            Intent intent = new Intent(MainActivity.this, DataView.class);
            intent.putExtra(DEVICE_ADRESS, address);
            startActivity(intent);
        }
    };
}
