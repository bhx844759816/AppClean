package com.appclean.main.utils;

import android.app.usage.StorageStats;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.os.Build;
import android.os.Environment;
import android.os.RemoteException;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.annotation.RequiresPermission;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.app.imagePicker.adapter.ImagePageAdapter;
import com.appclean.main.common.AppInfo;
import com.appclean.main.common.FileInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

public class AppSizeUtils {


    /**
     * 获取所有缓存的信息
     */
    public static void getAllAppFileInfo(Context context) {
        Context applicationContext = context.getApplicationContext();
        getAllAppInfos(applicationContext);

    }

    public static List<AppInfo> getAllAppInfos(Context context) {
        List<AppInfo> appInfoList = new ArrayList<>();
        Context applicationContext = context.getApplicationContext();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager packageManager = applicationContext.getPackageManager();

        List<ResolveInfo> rInfos = applicationContext.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo rInfo : rInfos) {
            AppInfo appInfo = null;
            if ((rInfo.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                // 第三方应用
                appInfo = getAppInfo(rInfo, packageManager);
                appInfo.setType(1);
            } else {
//                //系统应用
                appInfo = getAppInfo(rInfo, packageManager);
                appInfo.setType(0);
            }
            if (appInfo == null) {
                continue;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                queryAppSize(applicationContext, appInfo.getPkgName(), appInfo);
            } else {
                queryPkgSize(appInfo.getPkgName(), appInfo, packageManager);
            }
            appInfoList.add(appInfo);
        }
        return appInfoList;
    }

    private static AppInfo getAppInfo(ResolveInfo info, PackageManager packageManager) {
        AppInfo appInfo = new AppInfo();
        String appName = info.activityInfo.loadLabel(packageManager).toString();
        appInfo.setAppName(appName);
        appInfo.setAppIcon(info.activityInfo.loadIcon(packageManager));
        appInfo.setPkgName(info.activityInfo.packageName);

        return appInfo;

    }

    /**
     * 通过反射去获取APP包中对应的文件大小
     */
    private static void queryPkgSize(String pkgName, AppInfo appInfo, PackageManager packageManager) {
        try {
            Method getPackageSizeInfo = PackageManager.class.getDeclaredMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
            getPackageSizeInfo.invoke(packageManager, pkgName, new IPackageStatsObserver.Stub() {
                @Override
                public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
                    appInfo.setCacheSize(formatSize(pStats.cacheSize));
                    appInfo.setDataSize(formatSize(pStats.dataSize));
                    appInfo.setAppSize(formatSize(pStats.codeSize));

                }
            });
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    /**
     * 需要系统权限 android.permission.PACKAGE_USAGE_STATS
     *
     * @param context
     * @param pkgName
     * @param appInfo
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void queryAppSize(Context context, String pkgName, AppInfo appInfo) {
        Context applicationContext = context.getApplicationContext();
        StorageStatsManager statsManager = (StorageStatsManager) applicationContext.getSystemService(Context.STORAGE_STATS_SERVICE);
        try {
            int uid = applicationContext.getPackageManager().getApplicationInfo(pkgName, PackageManager.GET_META_DATA).uid;
            StorageStats storageStats = statsManager.queryStatsForUid(StorageManager.UUID_DEFAULT, uid);
            Log.i("TAG", String.format("storageStats %s AppData=%s,CacheData=%s,DataBytes=%s", appInfo.getAppName(), storageStats.getAppBytes(), storageStats.getCacheBytes(), storageStats.getDataBytes()));
            appInfo.setAppSize(formatSize(storageStats.getAppBytes()));
            appInfo.setDataSize(formatSize(storageStats.getDataBytes()));
            appInfo.setCacheSize(formatSize(storageStats.getCacheBytes()));
        } catch (PackageManager.NameNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }


    public static void queryAllFileByType(String filePath, String fileType, JSONArray array) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        File[] listFiles = file.listFiles();
        if (listFiles == null) {
            return;
        }
        for (File listFile : listFiles) {
            if (listFile.isFile() && listFile.canRead()) {
                Log.i("TAG", String.format("fileName = %s,filePath= %s", listFile.getName(), listFile.getAbsolutePath()));
            }
            if (listFile.isFile() && listFile.getName().endsWith(fileType)) {
                String _fileName = listFile.getName();
                String _filePath = listFile.getAbsolutePath();
                JSONObject obj = new JSONObject();
                try {
                    obj.put("fileName", _fileName);
                    obj.put("filePath", _filePath);
                    array.put(obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (listFile.isDirectory()) {
                queryAllFileByType(listFile.getAbsolutePath(), fileType, array);
            } else {

            }
        }
    }

    /**
     * 将文件大小显示为GB,MB等形式
     */
    public static String formatSize(long size) {
        if (size / (1024 * 1024 * 1024) > 0) {
            float tmpSize = (float) (size) / (float) (1024 * 1024 * 1024);
            DecimalFormat df = new DecimalFormat("#.##");
            return "" + df.format(tmpSize) + "GB";
        } else if (size / (1024 * 1024) > 0) {
            float tmpSize = (float) (size) / (float) (1024 * 1024);
            DecimalFormat df = new DecimalFormat("#.##");
            return "" + df.format(tmpSize) + "MB";
        } else if (size / 1024 > 0) {
            return "" + (size / (1024)) + "KB";
        } else
            return "" + size + "B";
    }


    /**
     * 搜索全部的图片
     *
     * @param context
     */
    public static void queryAllImage(Context context) {
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, null, null, null
        );
        while (cursor.moveToNext()) {
            String fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
            String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            Log.i("TAG", String.format("image fileName= %s filePath= %s", fileName, filePath));
        }
        cursor.close();
    }

    /**
     * 搜索外部存储全部的音频文件
     *
     * @param context
     */
    public static void queryAllMusic(Context context) {
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null, null, null, null
        );
        if (cursor == null) {
            return;
        }
        while (cursor.moveToNext()) {
            String musicName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
            String musicPath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            Log.i("TAG", String.format("查询到音频文件 = %s ,路径= %s ", musicName, musicPath));
        }
        cursor.close();
    }

    /**
     * 搜索外部存储的全部视频文件
     *
     * @param context
     */
    public static void queryAllVideo(Context context) {
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null, null, null, MediaStore.Video.Media.DEFAULT_SORT_ORDER
        );
        if (cursor == null) {
            return;
        }
        while (cursor.moveToNext()) {
            String videoName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
            String videoPath = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
            File file = new File(videoPath);
            long fileSize = 0L;
            if (file.exists() && file.canRead()) {
                fileSize = file.length();
            }
            Log.i("TAG", String.format("查询到视频文件 = %s ,路径 = %s, 大小 = %s", videoName, videoPath, fileSize));
        }
        cursor.close();
    }

    /**
     * 查询所有的文档
     */
    public static void queryAllDoc(Context context) {
        Cursor cursor = context.getApplicationContext().getContentResolver().query(
                MediaStore.Files.getContentUri("external"),
                null, MediaStore.Files.FileColumns.MIME_TYPE + "=?", new String[]{"application/zip"}, null);
        if (cursor == null) {
            return;
        }
        while (cursor.moveToNext()) {
            String docPath = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA));
            String docName = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME));
            String mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE));
            Log.i("TAG", String.format("查询到文档文件 = %s,mimeType =%s ,路径 = %s", docName,mimeType,docPath));
        }
        cursor.close();

//        c = context.getApplicationContext().getContentResolver().query(MediaStore.Files.getContentUri("external"), null,
//                MediaStore.Images.Media.MIME_TYPE + "= ? or " + MediaStore.Images.Media.MIME_TYPE + "= ?",
//                new String[]{"image/jpeg", "image/png"}, MediaStore.Images.Media.DATE_MODIFIED);
    }
}
