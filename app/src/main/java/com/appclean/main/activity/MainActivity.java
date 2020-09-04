package com.appclean.main.activity;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

import com.app.activity.YWBaseActivity;
import com.app.utils.PermissionReq;
import com.appclean.main.R;
import com.appclean.main.common.AppInfo;
import com.appclean.main.fragment.AppCleanFragment;
import com.appclean.main.fragment.AppMemoryFragment;
import com.appclean.main.fragment.MyFragment;
import com.appclean.main.message.AppInfoMessage;
import com.appclean.main.utils.AppSizeUtils;
import com.appclean.main.manager.FileManager;
import com.appclean.main.widget.MyBottomNavBar;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;

import java.io.File;
import java.util.List;

public class MainActivity extends YWBaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private AppCleanFragment mAppCleanFragment;
    private AppMemoryFragment mAppMemoryFragment;
    private MyFragment mMyFragment;
    private MyBottomNavBar myBottomNavBar;
    private int mSelectPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myBottomNavBar = findViewById(R.id.bottomNavBar);
        showAppCleanFragment();
        myBottomNavBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                mSelectPosition = position;
                selectFragment(mSelectPosition);
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {

            }
        });
    }

    public void queryAppInfo(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkUsageStates()) {
                //TODO 查询手机安装的APP的信息包含应用的数据和缓存大小
            } else {
                openUsagePermissionSetting();
            }
        } else {
        }
    }

    public void queryAllInstallApp(View view) {
        List<AppInfo> list = FileManager.getInstance().queryAllInstallAppInfo(this);
        AppInfoMessage appInfoMessage = new AppInfoMessage();
        appInfoMessage.mAppInfoList = list;
        EventBus.getDefault().postSticky(appInfoMessage);
        Intent intent = new Intent(this, AppListActivity.class);
        startActivity(intent);
    }

    private void selectFragment(int pos) {
        switch (pos) {
            case 0:
                showAppCleanFragment();
                break;
            case 1:
                showAppMemoryFragment();
                break;
            case 2:
                showMyFragment();
                break;
        }
    }

    /**
     * 展示应用清理的Fragment
     */
    private void showAppCleanFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        hideAllFragment(fragmentTransaction);
        if (mAppCleanFragment == null) {
            mAppCleanFragment = new AppCleanFragment();
            fragmentTransaction.add(R.id.flFragment, mAppCleanFragment);
        } else {
            fragmentTransaction.show(mAppCleanFragment);
        }
        fragmentTransaction.commit();
    }

    /**
     * 展示手机廋身的Fragment
     */
    private void showAppMemoryFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        hideAllFragment(fragmentTransaction);
        if (mAppMemoryFragment == null) {
            mAppMemoryFragment = new AppMemoryFragment();
            fragmentTransaction.add(R.id.flFragment, mAppMemoryFragment);
        } else {
            fragmentTransaction.show(mAppMemoryFragment);
        }
        fragmentTransaction.commit();
    }

    /**
     * 展示我的Fragment
     */
    private void showMyFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        hideAllFragment(fragmentTransaction);
        if (mMyFragment == null) {
            mMyFragment = new MyFragment();
            fragmentTransaction.add(R.id.flFragment, mMyFragment);
        } else {
            fragmentTransaction.show(mMyFragment);
        }
        fragmentTransaction.commit();
    }

    /**
     * 隐藏全部的fragment
     *
     * @param transaction
     */
    private void hideAllFragment(FragmentTransaction transaction) {
        if (mAppCleanFragment != null && mAppCleanFragment.isAdded()) {
            transaction.hide(mAppCleanFragment);
        }
        if (mAppMemoryFragment != null && mAppMemoryFragment.isAdded()) {
            transaction.hide(mAppMemoryFragment);
        }
        if (mMyFragment != null && mMyFragment.isAdded()) {
            transaction.hide(mMyFragment);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("position", mSelectPosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            mSelectPosition = savedInstanceState.getInt("position");
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private boolean checkUsageStates() {
        AppOpsManager opsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int appMode = opsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), getPackageName());
        boolean granted = false;
        if (appMode == AppOpsManager.MODE_DEFAULT) {
            granted = (checkCallingOrSelfPermission(Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED);
        } else {
            granted = (appMode == AppOpsManager.MODE_ALLOWED);
        }
        return granted;
    }

    /**
     * 跳转到控制用户信息访问的设置界面
     * <p>
     * 可以获取APP的缓存数据的大小
     */
    private void openUsagePermissionSetting() {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}