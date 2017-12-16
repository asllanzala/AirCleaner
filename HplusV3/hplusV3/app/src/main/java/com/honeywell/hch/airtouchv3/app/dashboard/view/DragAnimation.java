package com.honeywell.hch.airtouchv3.app.dashboard.view;

import android.content.ClipData;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Qian Jin on 10/18/15.
 */
public class DragAnimation {
    private View mDragView;
    private View mDragArea;
    private View mTargetArea;
    private DragCallback mDragInCallback;
    private Boolean mIsDragEnable = false;

    public interface DragCallback {

        void dragInCallback(View view);

        void dragBeginCallback(View view);
    }

    public void setDragInCallback(DragCallback dragCallback) {
        mDragInCallback = dragCallback;
    }

    public void setDragView(View dragView) {
        if (!mIsDragEnable)
            return;

        mDragView = dragView;
        mDragView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, MotionEvent event) {
                if (!mIsDragEnable)
                    return false;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        ClipData data = ClipData.newPlainText("", "");
                        View.DragShadowBuilder shadowBuilder
                                = new View.DragShadowBuilder(view);
                        view.startDrag(data, shadowBuilder, view, 0);
                        mDragInCallback.dragBeginCallback(view);
                        view.setVisibility(View.INVISIBLE);
                        return true;

                    default:
                        return false;
                }
            }
        });
    }

    public void setDragArea(View dragArea) {
        mDragArea = dragArea;
        mDragArea.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DROP:
                        View view = (View) event.getLocalState();
                        view.setVisibility(View.VISIBLE);
                        break;

                    case DragEvent.ACTION_DRAG_EXITED:
                        View view2 = (View) event.getLocalState();
                        view2.setVisibility(View.VISIBLE);
                        break;

                    default:
                        break;
                }
                return true;
            }
        });
    }

    public void setTargetArea(View targetArea) {
        mTargetArea = targetArea;
        mTargetArea.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                if (!mIsDragEnable)
                    return true;

                switch (event.getAction()) {
                    case DragEvent.ACTION_DROP:
                        mDragInCallback.dragInCallback(v);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    public void setDragEnable(Boolean isDragEnable) {
        mIsDragEnable = isDragEnable;
        if (!isDragEnable) {
            if (mDragView != null) {
                mDragView.setOnTouchListener(null);
                mDragView = null;
            }
        }
    }

}
