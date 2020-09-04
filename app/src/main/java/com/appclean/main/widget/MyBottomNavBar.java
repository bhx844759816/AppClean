package com.appclean.main.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.BottomNavigationView;
import android.util.AttributeSet;

import com.appclean.main.R;
import com.ashokvarma.bottomnavigation.BadgeItem;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;

/**
 * 底部导航栏的
 */
public class MyBottomNavBar extends BottomNavigationBar
{

    public MyBottomNavBar(Context context) {
        this(context,null);
    }

    public MyBottomNavBar(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyBottomNavBar(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr,0);
    }

    public MyBottomNavBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initBottomNavBar();
    }

    private void initBottomNavBar(){
        BottomNavigationItem itemOne = new BottomNavigationItem(R.mipmap.icon_clear_app,"清理");
        BottomNavigationItem itemTwo = new BottomNavigationItem(R.mipmap.icon_app_memory,"手机瘦身");
        BottomNavigationItem itemThree = new BottomNavigationItem(R.mipmap.icon_my,"我的");

        setMode(BottomNavigationBar.MODE_FIXED);
        setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);
        setBackgroundColor(Color.parseColor("#FFEFE9E9"));
        setFirstSelectedPosition(0);

        addItem(itemOne).addItem(itemTwo).addItem(itemThree);
        initialise();
    }
}
