package com.honeywell.hch.airtouchv3.app.airtouch.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Line from weather icon, show outdoor data
 */
public class WeatherLine extends View {

    public WeatherLine(Context context) {
        super(context);
    }

    public WeatherLine(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WeatherLine(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int contentWidth = getWidth();
        int contentHeight = getHeight();
        drawDeviceLine(canvas, contentWidth, contentHeight);
    }

    private void drawDeviceLine(Canvas canvas, int contentWidth, int contentHeight) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(2.0f);
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        List<Point> pointList = new ArrayList<>();
        Point point;
        int breakPoint_x = contentWidth / 6;
        int breakPoint_y = 0;
        for (int i = contentHeight, j = 0; j < breakPoint_x; i = i - 3, j = j + 3) {
            breakPoint_y = i;
            point = new Point(j, i);
            pointList.add(point);
        }
        for (int i = breakPoint_y, j = breakPoint_x; j < contentWidth; j = j + 3) {
            point = new Point(j, i);
            pointList.add(point);
        }

        for (int i = 1; i < pointList.size(); i++) {
            Point prePoint = pointList.get(i - 1);
            Point nowPoint = pointList.get(i);
            canvas.drawLine(prePoint.x, prePoint.y, nowPoint.x, nowPoint.y, paint);
        }
    }
}
