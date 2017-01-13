package com.yy.msdkquality.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.ycloud.live.video.YCVideoViewLayout;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Hui on 2015/5/27.
 */
public class DoubleLayout extends RelativeLayout {
    public DoubleLayout(Context context) {
        super(context);
    }

    public DoubleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DoubleLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        this.post(new Runnable() {
            @Override
            public void run() {
                reArrageLayout();
            }
        });
    }

    public void reArrageLayout() {
        int w = this.getWidth();
        int h = this.getHeight();
        if (w <= 0 || h <= 0) {
            return;
        }
        int childCount = getChildCount();
        List<YCVideoViewLayout> yList = new LinkedList<YCVideoViewLayout>();
        for (int i = 0; i < childCount; i++) {
            if (getChildAt(i) instanceof YCVideoViewLayout) {
                YCVideoViewLayout child = (YCVideoViewLayout) getChildAt(i);
                if (child.getVisibility() == View.VISIBLE) {
                    yList.add(child);
                }
            }
        }
        if (yList.size() == 1) {
            adjustSize(yList.get(0), w, h, 0, true);
        } else if (yList.size() == 2) {
            adjustSize(yList.get(0), w, h, 0, false);
            adjustSize(yList.get(1), w, h, 1, false);
        }
    }

    private void adjustSize(YCVideoViewLayout childView, int sw, int sh, int idx, boolean single) {
        LayoutParams lp = (LayoutParams) childView.getLayoutParams();
        if (single) {
            lp.topMargin = 0;
            lp.bottomMargin = 0;
            lp.leftMargin = 0;
            lp.rightMargin = 0;
            lp.width = sw;
            lp.height = sh;
        } else {
            lp.topMargin = 0;
            lp.bottomMargin = 0;
            lp.leftMargin = sw * idx / 2;
            lp.rightMargin = sw * (1 - idx) / 2;
            lp.width = sw / 2;
            lp.height = sh;
        }
        childView.setLayoutParams(lp);
    }
}
