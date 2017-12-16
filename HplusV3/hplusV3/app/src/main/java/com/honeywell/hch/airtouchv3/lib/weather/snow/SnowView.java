package com.honeywell.hch.airtouchv3.lib.weather.snow;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.lib.util.BitmapUtil;

import java.util.ArrayList;
import java.util.Random;

public class SnowView extends View
{


	Bitmap bitmap_snows[] = new Bitmap[5];

	public  boolean isRunning = true;

	private float screenWidth;
	private float screenHeiht;


	private static Random random = new Random();


	private ArrayList<Snow> snowflake_xxl = new ArrayList<Snow>();
	private ArrayList<Snow> snowflake_xl = new ArrayList<Snow>();
	private ArrayList<Snow> snowflake_m = new ArrayList<Snow>();
	private ArrayList<Snow> snowflake_s = new ArrayList<Snow>();
	private ArrayList<Snow> snowflake_l = new ArrayList<Snow>();


	private Context mContext;

	public SnowView(Context context) {
		super(context);
		mContext = context;
		this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams
				.MATCH_PARENT));

		getViewSize(context);
		LoadSnowImage();
		addRandomSnow();
		SnowThread snowThread = new SnowThread();
		snowThread.start();
	}


	@Override
	protected void onDraw(Canvas canvas) {
		drawMovingSnow(canvas);
	}

	private void getViewSize(Context context) {
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(metrics);
		this.screenHeiht = metrics.heightPixels;
		this.screenWidth = metrics.widthPixels;
	}


	public void LoadSnowImage() {
		bitmap_snows[0] = BitmapUtil.createBitmapEffectly(mContext, R.drawable.snowflake_l);
		bitmap_snows[1] = BitmapUtil.createBitmapEffectly(mContext, R.drawable.snowflake_s);
		bitmap_snows[2] = BitmapUtil.createBitmapEffectly(mContext, R.drawable.snowflake_m);
		bitmap_snows[3] = BitmapUtil.createBitmapEffectly(mContext, R.drawable.snowflake_xl);
		bitmap_snows[4] = BitmapUtil.createBitmapEffectly(mContext, R.drawable.snowflake_xxl);
//		bitmap_bg = BitmapUtils.createBitmapEffectly(mContext, R.drawable.bg14_day_snow);
	}

	public void addRandomSnow() {

		for (int i = 0; i < 20; i++) {
			snowflake_xxl.add(new Snow(bitmap_snows[4], random.nextFloat() * screenWidth, random
					.nextFloat() * screenHeiht, 7f, 1 - random.nextFloat() * 2));
			snowflake_xl.add(new Snow(bitmap_snows[3], random.nextFloat()
					* screenWidth, random.nextFloat() * screenHeiht, 5f,
					1 - random.nextFloat() * 2));
			snowflake_m.add(new Snow(bitmap_snows[2], random.nextFloat() * screenWidth, random
					.nextFloat() * screenHeiht, 3f, 1 - random.nextFloat() * 2));
			snowflake_s.add(new Snow(bitmap_snows[1], random.nextFloat()
					* screenWidth, random.nextFloat() * screenHeiht, 2f,
					1 - random.nextFloat() * 2));
			snowflake_l.add(new Snow(bitmap_snows[0], random.nextFloat() * screenWidth, random
					.nextFloat() * screenHeiht, 2f, 1 - random.nextFloat() * 2));
		}

	}

	private void drawSnow(Canvas canvas) {
		Paint paint = new Paint();

		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);

		Snow snow = null;
		for (int i = 0; i < 20; i++) {
			snow = snowflake_xxl.get(i);
			canvas.drawBitmap(snow.bitmap, snow.x, snow.y, paint);
			snow = snowflake_xl.get(i);
			canvas.drawBitmap(snow.bitmap, snow.x, snow.y, paint);
			snow = snowflake_m.get(i);
			canvas.drawBitmap(snow.bitmap, snow.x, snow.y, paint);
			snow = snowflake_s.get(i);
			canvas.drawBitmap(snow.bitmap, snow.x, snow.y, paint);
			snow = snowflake_l.get(i);
			canvas.drawBitmap(snow.bitmap, snow.x, snow.y, paint);

		}

	}

	private class SnowThread extends Thread{


		public SnowThread() {
			isRunning = true;
		}

		@Override
		public void run()
		{
			while(isRunning)
			{
				postInvalidate();
				try {
					Thread.sleep(15);

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}


	private void drawMovingSnow(Canvas canvas){
		drawSnow(canvas);
		Snow snow;

		for (int i = 0; i < 20; i++) {
			snow = snowflake_xxl.get(i);
			SnowDown(snow);

			snow = snowflake_xl.get(i);
			SnowDown(snow);

			snow = snowflake_m.get(i);
			SnowDown(snow);

			snow = snowflake_s.get(i);
			SnowDown(snow);

			snow = snowflake_l.get(i);
			SnowDown(snow);

		}
	}



	private void SnowDown(Snow snow) {
		if (snow.x > screenWidth || snow.y > screenHeiht) {
			snow.y = 0;
			snow.x = random.nextFloat() * screenWidth;
		}
		snow.x += snow.offset;
		snow.y += snow.speed;
	}


	public void recycleSnow(){
		isRunning = false;
        for (Snow snow : snowflake_l){
			snow.recyleTheSnowBitmap();
		}
		for (Snow snow : snowflake_s){
			snow.recyleTheSnowBitmap();
		}
		for (Snow snow : snowflake_m){
			snow.recyleTheSnowBitmap();
		}
		for (Snow snow : snowflake_xl){
			snow.recyleTheSnowBitmap();
		}
		for (Snow snow : snowflake_xxl){
			snow.recyleTheSnowBitmap();
		}

		for (Bitmap bitmap : bitmap_snows){
			if (bitmap != null){
				bitmap.recycle();
				bitmap = null;
			}
		}
	}

}
