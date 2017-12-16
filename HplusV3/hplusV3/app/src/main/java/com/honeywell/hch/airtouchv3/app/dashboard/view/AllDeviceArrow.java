package com.honeywell.hch.airtouchv3.app.dashboard.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.view.View;

import com.honeywell.hch.airtouchv3.lib.util.DensityUtil;

import java.util.ArrayList;

/**
 * Created by Qian Jin on 10/24/15.
 */
public class AllDeviceArrow extends View {
    private Canvas myCanvas;
    private Paint myPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint myPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
    private ArrayList<int[]> arrayList = new ArrayList<int[]>();

    public AllDeviceArrow(Context context) {
        super(context);
    }

    public AllDeviceArrow(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public AllDeviceArrow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.myCanvas = canvas;
        for(int i = 0;i<arrayList.size();i++){
            drawAL(arrayList.get(i)[0],arrayList.get(i)[1],arrayList.get(i)[2],arrayList.get(i)[3]);
        }

    }
    /**
     * set default paint style
     */
    public void setPaintDefaultStyle() {
        myPaint.setAntiAlias(true);
        myPaint.setColor(Color.WHITE);
        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeWidth(3);

        myPaint2.setStyle(Paint.Style.STROKE);
        myPaint2.setStrokeWidth(3);
        myPaint2.setColor(Color.WHITE);
    }

    public void setXY (int a ,int b,int c,int d){
        int [] axis  = new int[]{a,b,c,d};
        arrayList.add(axis);
    }

    public void clearCanvas() {
        arrayList.clear();
    }

    public void drawAL(int sx, int sy, int ex, int ey)
    {
        setPaintDefaultStyle();
        double H = DensityUtil.dip2px(10); // arrow height
        double L = DensityUtil.dip2px(7); // half bottom edge
        int x3 = 0;
        int y3 = 0;
        int x4 = 0;
        int y4 = 0;
        double awrad = Math.atan(L / H); //  arrow angle
        double arraow_len = Math.sqrt(L * L + H * H); // arrow length
        double[] arrXY_1 = rotateVec(ex - sx, ey - sy, awrad, true, arraow_len);
        double[] arrXY_2 = rotateVec(ex - sx, ey - sy, -awrad, true, arraow_len);
        double x_3 = ex - arrXY_1[0]; // (x3,y3)the first coordinate
        double y_3 = ey - arrXY_1[1];
        double x_4 = ex - arrXY_2[0]; // (x4,y4)the second coordinate
        double y_4 = ey - arrXY_2[1];
        Double X3 = new Double(x_3);
        x3 = X3.intValue();
        Double Y3 = new Double(y_3);
        y3 = Y3.intValue();
        Double X4 = new Double(x_4);
        x4 = X4.intValue();
        Double Y4 = new Double(y_4);
        y4 = Y4.intValue();

        // draw line
        Path path = new Path();
        path.moveTo(sx, sy);
        path.lineTo(ex, ey);
        PathEffect effects = new DashPathEffect(new float[] { DensityUtil.dip2px(1), DensityUtil.dip2px(2),DensityUtil.dip2px(4),DensityUtil.dip2px(8)}, DensityUtil.dip2px(1));
        myPaint2.setPathEffect(effects);
        myCanvas.drawPath(path, myPaint2);

        Path triangle = new Path();
        triangle.moveTo(ex, ey);
        triangle.lineTo(x4, y4);
        myCanvas.drawPath(triangle, myPaint);
        triangle.moveTo(ex, ey);
        triangle.lineTo(x3, y3);
        triangle.close();
        myCanvas.drawPath(triangle, myPaint);
    }

    public double[] rotateVec(int px, int py, double ang, boolean isChLen, double newLen)
    {
        double mathstr[] = new double[2];
        // 矢量旋转函数，参数含义分别是x分量、y分量、旋转角、是否改变长度、新长度
        double vx = px * Math.cos(ang) - py * Math.sin(ang);
        double vy = px * Math.sin(ang) + py * Math.cos(ang);
        if (isChLen) {
            double d = Math.sqrt(vx * vx + vy * vy);
            vx = vx / d * newLen;
            vy = vy / d * newLen;
            mathstr[0] = vx;
            mathstr[1] = vy;
        }
        return mathstr;
    }
}
