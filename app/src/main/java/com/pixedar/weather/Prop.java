package com.pixedar.weather;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Switch;

import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Calendar;

public class Prop extends Fragment  {
    private DatePicker datePicker;
    private Switch setMulti;
    private Button b1;
    private Button b2;
    private Button b3;
    public static boolean b1E;
    public static boolean b2E;
    public static boolean b3E;
    public static boolean multiIsChecked;
    public static Calendar calendar = Calendar.getInstance();
    public static ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_prop, container, false);

        b1 = (Button)  rootView.findViewById(R.id.button);
        b2 = (Button)  rootView.findViewById(R.id.button2);
        b3 = (Button)  rootView.findViewById(R.id.button3);
        b1.setBackgroundColor(Color.rgb(240,50,110));
        b1.setTextColor(Color.WHITE);
        b2.setBackgroundColor(Color.rgb(240,50,110));
        b2.setTextColor(Color.WHITE);
        b3.setBackgroundColor(Color.rgb(240,50,110));
        b3.setTextColor(Color.WHITE);
        b1.setText("Okres tygodniowy");
        b2.setText("Okres miesięczny");
        b3.setText("Okres roczny");
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b1E =true;
                b2E = false;
                b3E = false;
                MainDataView.viewPager.setCurrentItem(2);
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b1E =false;
                b2E = true;
                b3E = false;
                MainDataView.viewPager.setCurrentItem(2);
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b1E =false;
                b2E = false;
                b3E = true;
                MainDataView.viewPager.setCurrentItem(2);
            }
        });


        datePicker = (DatePicker) rootView.findViewById(R.id.datePicker2);
        Calendar c = Calendar.getInstance();



        datePicker.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                if(view.isShown()) {
                    calendar.set(year, monthOfYear, dayOfMonth);
                    FragmentInterface fragment = (FragmentInterface) MainDataView.mAdapter.instantiateItem(MainDataView.viewPager, 1);
                    fragment.fragmentBecameVisible();
                   // if(!multiIsChecked){
                        MainDataView.viewPager.setCurrentItem(1);
                    //}

                }
            }
        });

        datePicker.setMaxDate(c.getTimeInMillis() - 1000);

        c.add(Calendar.DAY_OF_MONTH, -7);
        datePicker.setMinDate(c.getTimeInMillis());




        setMulti = (Switch) rootView.findViewById(R.id.switch2);
        setMulti.setTextColor(Color.WHITE);
        setMulti.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                multiIsChecked = isChecked;

                if (!isChecked) {
                    dataSets = new ArrayList<ILineDataSet>();
                }
                FragmentInterface fragment = (FragmentInterface) MainDataView.mAdapter.instantiateItem(MainDataView.viewPager, 1);
                fragment.fragmentBecameVisible();
            }
        });

        return rootView;
    }

}
