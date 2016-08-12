package com.pixedar.weather;

public class DataParser {
    private boolean dataValid;
    private float pressure;
    private float temperature;

    public void parseLine(String line) {
        if (line == null) {
            dataValid = false;
            return;
        }
        String[] parts = line.split(";");
        try {
            pressure = Float.parseFloat(parts[0]);
            temperature = Float.parseFloat(parts[1]);
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

    public String currentToString() {
        return "Pressure " + pressure + " Temperature " + temperature;
    }
}
