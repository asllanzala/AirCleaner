package com.honeywell.hch.airtouchv3.lib.weather.snow;

import android.graphics.Bitmap;

public class Snow {

	Bitmap bitmap;

	float x;

	float y;

	float speed;

	float offset;

	public Snow(Bitmap bitmap, float x, float y, float speed, float offset) {
		this.bitmap = bitmap;
		this.x = x;
		this.y = y;
		this.speed = speed;
		this.offset = offset;
	}

    public void recyleTheSnowBitmap(){
		if (bitmap != null){
			bitmap.recycle();
			bitmap = null;
		}
	}
	
	
	

}
