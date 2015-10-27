package com.whl.standardcompuse.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * Author:whl
 * Email:294084532@qq.com
 * 2015/10/26
 */
public class FullVideoView extends VideoView{
    public FullVideoView(Context context) {
        super(context);
    }

    public FullVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        从写VideoView的尺寸测量，使自身充满容器
        int wSize=MeasureSpec.getSize(widthMeasureSpec);
        int hSize=MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(wSize,hSize);
    }
}
