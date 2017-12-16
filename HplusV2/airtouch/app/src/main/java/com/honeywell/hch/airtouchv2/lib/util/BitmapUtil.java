package com.honeywell.hch.airtouchv2.lib.util;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;

import java.io.ByteArrayOutputStream;

/**
 * The util for Screenshot and Bitmap convert.
 * Created by Qian Jin on 8/5/15.
 */
public class BitmapUtil {

    public static Bitmap convertViewToBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.RGB_565);
        view.draw(new Canvas(bitmap));
        return bitmap;
    }

    /**
     * get bitmap compressed
     *
     * @param bitmap
     * @return compressed bitmap
     */
    public static Bitmap compress(Bitmap bitmap, int quality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }
        byte[] data = baos.toByteArray();
        int cp_length = data.length;
        Log.e("AirTouch", "compressed image length = " + cp_length / 1024 + " kb");
        return BitmapFactory.decodeByteArray(data, 0, cp_length);
    }
}