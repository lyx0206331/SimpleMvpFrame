package com.adrian.pickerlib.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class AssetsUtils {

    /**
     * 从assert文件夹中读取省市区的json文件
     */
    public static String readText(Context context, String assetPath) {
        try {
            BufferedReader bufReader = new BufferedReader(new InputStreamReader(context.getResources().getAssets().open(assetPath)));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = bufReader.readLine()) != null) {
                sb.append(line);
            }
            bufReader.close();
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }

}
