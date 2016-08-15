package com.pixedar.weather;

public class DataParser {
    private boolean dataValid;
    private float pressure;
    private float temperature;
    private float humidity;
    private float light;

    public void parseLine(String line) {
        if (line == null) {
            dataValid = false;
            return;
        }
        String[] parts = line.split(";");


        try {
            pressure = Float.parseFloat(parts[0]);
            temperature = Float.parseFloat(parts[1]);
            humidity = Float.parseFloat(parts[2]);
            light = Float.parseFloat(parts[3]);
            dataValid = true;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            dataValid = false;
        }
    }

    public boolean isDataValid() {
        return dataValid;
    }

    public float getPressure() {
        return pressure;
    }

    public float getTemperature() {
        return temperature;
    }

    public float getHumidity(){
        return humidity;
    }

    public float getLight(){
        return light;
    }

    public String currentToString() {
        return "Pressure " + pressure + " Temperature " + temperature + " Humidity " + humidity + " Light " + light;
    }
}
