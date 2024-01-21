package com.chessproject.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class FileUtils {
    static public ArrayList<HashMap<String, String>> readCSV(Context context, String fileName) {
        ArrayList<HashMap<String, String>> result = new ArrayList<>();

        try {
            InputStreamReader is = new InputStreamReader(context.getAssets().open(fileName));

            BufferedReader bufferedReader = new BufferedReader(is);
            String headerString = bufferedReader.readLine();
            String[] headers = headerString.split(",");
            String buffer;
            while ((buffer = bufferedReader.readLine()) != null) {
                String[] values = buffer.split(",");
                if (values.length != headers.length) {
                    continue;
                }
                HashMap<String, String> record = new HashMap<>();
                for (int i = 0; i < headers.length; ++i) {
                    record.put(headers[i], values[i]);
                }
                result.add(record);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
