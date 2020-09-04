package com.appclean.main.widget;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class AppCleanView extends View {

    private int mCircleRadius;
    private int mLinePaddingCircle;// 外圈线和内圆之间的距离

    private int mLineWidth; //外圈线的宽带
    private int mLineHeaderCircleRadius;//外圈线头的半径

    public AppCleanView(Context context) {
        this(context,null);
    }

    public AppCleanView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public AppCleanView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private void init(AttributeSet attributeSet){

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
