package com.honeywell.hch.airtouchv3.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by lynnliu on 22/9/6.
 * util for load mock http data
 */
public class HPlusFileUtils {
    private static final String PATH_TO_DATA = "src/test/resource/";
    private static final String PATH_TO_DATA_2 = "app/src/test/resource/";

    public static String readFileFromWebTestsAsString(String fileName) {
        InputStream input;
        BufferedReader reader;
        try {
            File file = new File(PATH_TO_DATA + fileName);
            if (!file.exists()) {
                file = new File(PATH_TO_DATA_2 + fileName);
            }
            input = new FileInputStream(file);
            byte[] fileData = new byte[input.available()];
            input.read(fileData);
            String dataFromFile = new String(fileData, "UTF-8");
            input.close();
            return dataFromFile;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}
