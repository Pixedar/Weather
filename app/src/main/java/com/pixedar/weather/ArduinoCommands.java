package com.pixedar.weather;

import java.io.UnsupportedEncodingException;

public class ArduinoCommands {
    public static final byte[] GET_DATA = createCommand("G");

    private static byte[] createCommand(String g) {
        try {
            return g.getBytes("ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            System.exit(0);
        }
        return null;
    }
}
