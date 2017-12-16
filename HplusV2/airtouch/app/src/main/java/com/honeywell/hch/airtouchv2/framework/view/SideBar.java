package com.honeywell.hch.airtouchv2.framework.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.SectionIndexer;

import com.honeywell.hch.airtouchv2.R;
import com.honeywell.hch.airtouchv2.lib.util.DensityUtil;

/**
 * Created by nan.liu on 2/2/15.
 */
public class SideBar extends View {
    private char[] l;
    private SectionIndexer sectionIndexter = null;
    private ListView list;
    private int width = 0;
    private int hight = 0;

    public SideBar(Context context) {
        super(context);
        init();
    }

    public SideBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        l = new char[]{'@', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    }

    public SideBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setListView(ListView _list) {
        list = _list;
        sectionIndexter = (SectionIndexer) _list.getAdapter();
    }

    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        int i = (int) event.getY();
        if (hight <= 0) {
            hight = getHeight();
        }
        int idx = i / (hight / 26);
        if (idx >= l.length) {
            idx = l.length - 1;
        } else if (idx < 0) {
            idx = 0;
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            if (sectionIndexter == null) {
                sectionIndexter = (SectionIndexer) list.getAdapter();
            }
            int position = sectionIndexter.getPositionForSection(l[idx]);
            if (position == -1) {
                return true;
            }
            requestFocusFromTouch();
            list.setSelection(position);
        }
        return true;
    }

    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setTextSize(DensityUtil.dip2px(13));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(this.getResources().getColor(R.color.list_index));
//		paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setAntiAlias(true);
        hight = getHeight();
        // float widthCenter = getMeasuredWidth() / 2;
        for (int i = 0; i < l.length; i++) {
            float xPos = getWidth() / 2 /*- paint.measureText(String.valueOf(l[i])) / 2*/;
            float yPos = (getHeight() / l.length) * i + (getHeight() / l.length);
            canvas.drawText(String.valueOf(l[i]), xPos, yPos, paint);
            // canvas.drawText(String.valueOf(l[i]), widthCenter, m_nItemHeight
            // + (i * m_nItemHeight), paint);
        }
        super.onDraw(canvas);
    }
}