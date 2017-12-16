package com.honeywell.hch.airtouchv3.lib.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * Util for Drawable
 * Created by lynnliu on 10/23/15.
 */
public class DrawableUtil {

    public static Bitmap zoomClipBitmap(Bitmap bitmap, float startX, float startY, float
            widthScale, float heightScale, float width, float height) {
        Matrix matrix = new Matrix();
        matrix.postScale(widthScale, heightScale);
        int mapHeight = (int) (height / heightScale + 0.5f);
        if (mapHeight == 0){
            return null;
        }
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, (int) startX, (int) startY, (int) (width /
                        widthScale), (int) (height / heightScale + 0.5f), matrix, true);
        return newBitmap;
    }
}
