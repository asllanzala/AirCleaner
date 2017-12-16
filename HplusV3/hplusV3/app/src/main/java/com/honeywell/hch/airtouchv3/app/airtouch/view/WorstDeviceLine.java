package com.honeywell.hch.airtouchv3.app.airtouch.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.honeywell.hch.airtouchv3.R;
import com.honeywell.hch.airtouchv3.lib.util.BitmapUtil;
import com.honeywell.hch.airtouchv3.lib.util.DensityUtil;

/**
 * Line from house, show worst device data
 */
public class WorstDeviceLine extends View {

    private Context mContext;

    private float mPosition;

    private boolean isNeedDraw = false;

    public WorstDeviceLine(Context context) {
        super(context);
        mContext = context;

    }

    public WorstDeviceLine(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

    }

    public WorstDeviceLine(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int contentWidth = getWidth();
        int contentHeight = getHeight();
        if (isNeedDraw){
            drawDeviceLine(canvas, contentWidth, contentHeight);
        }
    }

//    private void drawDeviceLine(Canvas canvas, int contentWidth, int contentHeight) {
//        Paint paint = new Paint();
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setStrokeCap(Paint.Cap.ROUND);
//        paint.setStrokeWidth(2.0f);
//        paint.setAntiAlias(true);
//        paint.setColor(Color.WHITE);
//        List<Point> pointList = new ArrayList<>();
//        Point point;
//        int breakPoint_x = 0;
//        int breakPoint_y = contentHeight / 7 * 3;
//        for (int i = contentHeight, j = contentWidth; i > breakPoint_y; i = i - 6,
//                j = j - 3) {
//            breakPoint_x = j;
//            point = new Point(j, i);
//            pointList.add(point);
//        }
//        for (int i = breakPoint_y, j = breakPoint_x; j > 0; j = j - 6) {
//            point = new Point(j, i);
//            pointList.add(point);
//        }
//
//        for (int i = 1; i < pointList.size(); i++) {
//            Point prePoint = pointList.get(i - 1);
//            Point nowPoint = pointList.get(i);
//            canvas.drawLine(prePoint.x, prePoint.y, nowPoint.x, nowPoint.y, paint);
//        }
//    }

    public void setPosition(float position){
        mPosition = position;
        isNeedDraw = true;
        invalidate();
    }

    private void drawDeviceLine(Canvas canvas, int contentWidth, int contentHeight) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(2.0f);
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);

        /**
         * use BitmapUtil.getImageWith(mContext.getResources(), R.drawable.fan) as the begin of the line . because we need to the fan is out margin of the line
         */
        canvas.drawLine(BitmapUtil.getImageWith(mContext.getResources(), R.drawable.fan) + DensityUtil.dip2px(10), contentHeight / 2, (int)(mPosition * 0.83), contentHeight / 2, paint);
        canvas.drawLine((int)(mPosition * 0.83), contentHeight / 2, mPosition, contentHeight, paint);
    }
}
