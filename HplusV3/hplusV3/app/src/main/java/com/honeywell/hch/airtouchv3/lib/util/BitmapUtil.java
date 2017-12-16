package com.honeywell.hch.airtouchv3.lib.util;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;

import com.honeywell.hch.airtouchv3.HPlusApplication;
import com.honeywell.hch.airtouchv3.R;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * The util for Screenshot and Bitmap convert.
 * Created by Qian Jin on 8/5/15.
 */
public class BitmapUtil {
    public static final Bitmap   mGlassBitmap = BitmapUtil.createBitmapEffectly(
            HPlusApplication.getInstance().getApplicationContext(), R.drawable.glass);

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

    /**
     * get the bimap with, with just return size
     * @param resouceId
     * @return
     */
    public static int getImageWith(Resources res,int resouceId){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resouceId, options);
        return options.outWidth;
    }

    /**
     * create bitmap use less memory
     * @param mContext
     * @param backgroundResourceId
     * @return
     */
    public static Bitmap createBitmapEffectly(Context mContext,int backgroundResourceId){

        InputStream is = mContext.getResources().openRawResource(backgroundResourceId);

        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inPurgeable = true;// 允许可清除
        options.inInputShareable = true;// 以上options的两个属性必须联合使用才会有效果

        Bitmap resultBitmap = BitmapFactory.decodeStream(is, null, options);

        try{
            is.close();
            is = null;
        }
        catch (Exception e){

        }
        return resultBitmap;
    }


    /**
     * create bitmap use less memory
     * @param mContext
     * @param path
     * @return
     */
    public static Bitmap createBitmapEffectlyFromPath(Context mContext,String path){

        InputStream is = null;
        Bitmap resultBitmap = null;
        try{
            is = new FileInputStream(path);

            BitmapFactory.Options options = new BitmapFactory.Options();

            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inPurgeable = true;// 允许可清除
            options.inInputShareable = true;// 以上options的两个属性必须联合使用才会有效果

            resultBitmap = BitmapFactory.decodeStream(is, null, options);
        }catch (Exception e){

        }
        finally {
            try{
                is.close();
                is = null;
            }
            catch (Exception e){

            }
        }

        return resultBitmap;
    }


    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                    int reqWidth, int reqHeight)
    {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
    {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

}