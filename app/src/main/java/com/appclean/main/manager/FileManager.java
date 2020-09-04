package com.appclean.main.manager;

import android.app.Activity;
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
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.RemoteException;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.appclean.main.common.AppInfo;
import com.appclean.main.common.FileInfo;
import com.appclean.main.common.FolderInfo;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 查询和管理文件的单例类
 */
public class FileManager {

    private static volatile FileManager mInstance;
    private static Object mLock = new Object();

    private FileManager() {
    }

    public static FileManager getInstance() {
        if (mInstance == null) {
            synchronized (mLock) {
                if (mInstance == null) {
                    mInstance = new FileManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 查询当前应用中所有已安装App的缓存数据和app大小
     *
     * @param context
     * @return
     */
    public List<AppInfo> queryAllInstallAppDataInfo(Context context) {
        List<AppInfo> appInfoList = new ArrayList<>();
        Context applicationContext = context.getApplicationContext();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager packageManager = applicationContext.getPackageManager();
        List<ResolveInfo> rInfos = applicationContext.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo rInfo : rInfos) {
            AppInfo appInfo = getAppInfo(rInfo, packageManager);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                queryInstallAddDataSizeO(applicationContext, appInfo);
            } else {
                queryInstallAppDataSize(appInfo, packageManager);
            }
            appInfoList.add(appInfo);
        }
        return appInfoList;
    }

    /**
     * 根据查询的App信息添加组装成AppInfo
     *
     * @param info       intentFilter过滤后的信息
     * @param pkgManager
     * @return
     */
    private AppInfo getAppInfo(ResolveInfo info, PackageManager pkgManager) {
        AppInfo appInfo = new AppInfo();
        String appName = info.activityInfo.loadLabel(pkgManager).toString();
        appInfo.setAppName(appName);
        appInfo.setAppIcon(info.activityInfo.loadIcon(pkgManager));
        appInfo.setPkgName(info.activityInfo.packageName);
        return appInfo;
    }

    /**
     * Android O版本以下查询app的缓存大小
     *
     * @param appInfo    需要查询APP的信息对象
     * @param pkgManager 包管理器
     * @return
     */
    private void queryInstallAppDataSize(AppInfo appInfo, PackageManager pkgManager) {
        try {
            Method getPackageSizeInfo = PackageManager.class.getDeclaredMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
            getPackageSizeInfo.invoke(pkgManager, appInfo.getPkgName(), new IPackageStatsObserver.Stub() {
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
     * android O版本查询app的缓存大小
     *
     * @param context
     * @param appInfo 需要查询的app的信息
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void queryInstallAddDataSizeO(Context context, AppInfo appInfo) {
        Context applicationContext = context.getApplicationContext();
        StorageStatsManager statsManager = (StorageStatsManager) applicationContext.getSystemService(Context.STORAGE_STATS_SERVICE);
        try {
            int uid = applicationContext.getPackageManager().getApplicationInfo(appInfo.getPkgName(), PackageManager.GET_META_DATA).uid;
            StorageStats storageStats = statsManager.queryStatsForUid(StorageManager.UUID_DEFAULT, uid);
            Log.i("TAG", String.format("storageStats %s AppData=%s,CacheData=%s,DataBytes=%s", appInfo.getAppName(), storageStats.getAppBytes(), storageStats.getCacheBytes(), storageStats.getDataBytes()));
            appInfo.setAppSize(formatSize(storageStats.getAppBytes()));
            appInfo.setDataSize(formatSize(storageStats.getDataBytes()));
            appInfo.setCacheSize(formatSize(storageStats.getCacheBytes()));
        } catch (PackageManager.NameNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取全部安装的外部的应用信息
     *
     * @param context
     */
    public List<AppInfo> queryAllInstallAppInfo(Context context) {
        List<AppInfo> appInfoList = new ArrayList<>();
        Context applicationContext = context.getApplicationContext();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager packageManager = applicationContext.getPackageManager();
        List<ResolveInfo> rInfos = applicationContext.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo rInfo : rInfos) {
            if ((rInfo.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                // 第三方应用
                AppInfo appInfo = getAppInfo(rInfo, packageManager);
                File file = new File(rInfo.activityInfo.applicationInfo.sourceDir);
                if (file.exists()) {
                    appInfo.setAppSize(formatSize(file.length()));
                    appInfo.setSourceFilePath(rInfo.activityInfo.applicationInfo.sourceDir);
                }
                appInfoList.add(appInfo);
            }
        }
        return appInfoList;
    }

    /**
     * 删除指定包名的APP
     *
     * @param context
     * @param packageName 需要删除APP的包名
     */
    public boolean unInstallApp(Activity context, String packageName) {
        Log.i("TAG", "卸载app packageName：" + packageName);
        /**
         * 调用系统的卸载
         */
        Uri uri = Uri.fromParts("package", packageName, null);
        Intent intent = new Intent(Intent.ACTION_DELETE, uri);
        context.startActivity(intent);
        return true;
    }

    /**
     * 获取全部图片的
     *
     * @param context
     */
    public List<FolderInfo> queryAllPhotos(Context context) {
        Cursor cursor = context.getApplicationContext().getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, null, null,
                MediaStore.Images.Media.DATE_ADDED + " DESC"
        );
        Map<String, List<FileInfo>> mapInfo = new HashMap<>();
        List<FolderInfo> folderInfos = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String fileName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
                String filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                long fileDataTime = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED));
                long fileSize = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE));
                //当前文件的父文件夹名称
                String bucketDisplayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                FileInfo fileInfo = new FileInfo();
                fileInfo.setFileType(FileInfo.FileType.IMG);
                fileInfo.setFileName(fileName);
                fileInfo.setFileAbsPath(filePath);
                fileInfo.setFileSize(fileSize);
                fileInfo.setFileAddDataTime(fileDataTime);
                if (mapInfo.containsKey(bucketDisplayName)) {
                    mapInfo.get(bucketDisplayName).add(fileInfo);
                } else {
                    List<FileInfo> list = new ArrayList<>();
                    list.add(fileInfo);
                    mapInfo.put(bucketDisplayName, list);
                }
                Log.i("TAG", String.format("image fileName= %s bucketDisplayName = %s,filePath= %s fileDataTime = %s fileSize = %s",
                        fileName, bucketDisplayName, filePath, fileDataTime, fileSize));
            }
            for (Map.Entry<String, List<FileInfo>> entry : mapInfo.entrySet()) {
                String key = entry.getKey();
                List<FileInfo> value = entry.getValue();
                FolderInfo folderInfo = new FolderInfo();
                folderInfo.setFolderName(key);
                folderInfo.setFileInfoList(value);
                folderInfos.add(folderInfo);
            }
            cursor.close();
        }
        return folderInfos;
    }

    /**
     * 查询全部的文档
     *
     * @return
     */
    public List<FileInfo> queryAllText(Context context, FileInfo.FileType fileType) {
        List<FileInfo> fileInfoList = new ArrayList<>();
        String selection = null;
        String[] selectionArgs = null;
        switch (fileType) {
            case APK:
                selection = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
                selectionArgs = new String[]{"application/vnd.android.package-archive"};
                break;
            case DOC:
                selection = MediaStore.Files.FileColumns.MIME_TYPE + "=? or " + MediaStore.Files.FileColumns.DATA + " like?";
                selectionArgs = new String[]{"application/msword", "%.doc"};
                break;
            case PDF:
                selection = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
                selectionArgs = new String[]{"application/pdf"};
                break;
            case XLS:
                selection = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
                selectionArgs = new String[]{"application/vnd.ms-excel"};
                break;
            case PPT:
                selection = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
                selectionArgs = new String[]{"application/vnd.ms-powerpoint"};
                break;
            case TXT:
                selection = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
                selectionArgs = new String[]{"text/plain"};
                break;
            case ZIP:
                selection = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
                selectionArgs = new String[]{"application/zip"};
                break;
        }
        Cursor cursor = context.getApplicationContext().getContentResolver().query(
                MediaStore.Files.getContentUri("external"),
                null, selection, selectionArgs,
                MediaStore.Images.Media.DATE_ADDED + " DESC"
        );
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String fileName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME));
                String filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA));
                long fileSize = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE));
                long fileAddDataTime = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED));
                FileInfo fileInfo = new FileInfo();
                fileInfo.setFileName(fileName);
                fileInfo.setFileAddDataTime(fileAddDataTime);
                fileInfo.setFileSize(fileSize);
                fileInfo.setFileAbsPath(filePath);
                fileInfo.setFileType(fileType);
                fileInfoList.add(fileInfo);
            }
            cursor.close();
        }
        return fileInfoList;
    }

    /**
     * 查询全部的音频文件
     *
     * @param context
     * @return
     */
    public List<FileInfo> queryAllAudio(Context context) {
        List<FileInfo> fileInfoList = new ArrayList<>();
        Cursor cursor = context.getApplicationContext().getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null, null, null, MediaStore.Audio.Media.DATE_ADDED + " DESC"
        );
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String fileName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
                String filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                long fileSize = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
                long fileAddDataTime = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED));
                FileInfo fileInfo = new FileInfo();
                fileInfo.setFileType(FileInfo.FileType.AUDIO);
                fileInfo.setFileAbsPath(filePath);
                fileInfo.setFileSize(fileSize);
                fileInfo.setFileName(fileName);
                fileInfo.setFileAddDataTime(fileAddDataTime);
                fileInfoList.add(fileInfo);
            }
        }
        cursor.close();
        return fileInfoList;
    }

    /**
     * 查询全部的视频文件
     *
     * @param context
     * @return
     */
    public List<FileInfo> queryAllVideo(Context context) {
        List<FileInfo> fileInfoList = new ArrayList<>();
        Cursor cursor = context.getApplicationContext().getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null, null, null, MediaStore.Video.Media.DATE_ADDED + " DESC"
        );
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String fileName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
                String filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                long fileSize = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
                long fileAddDataTime = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED));
                FileInfo fileInfo = new FileInfo();
                fileInfo.setFileAddDataTime(fileAddDataTime);
                fileInfo.setFileName(fileName);
                fileInfo.setFileSize(fileSize);
                fileInfo.setFileAbsPath(filePath);
                fileInfo.setFileType(FileInfo.FileType.VIDEO);
                fileInfoList.add(fileInfo);
            }
        }
        cursor.close();
        return fileInfoList;
    }


    public List<FileInfo> queryAllWxImages(Context context) {
        List<FileInfo> fileInfoList = new ArrayList<>();
        /**
         * 传一个MimeType的List
         */
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Files.getContentUri("external"),
                null, MediaStore.Files.FileColumns.DATA + " like? and " + MediaStore.Files.FileColumns.MIME_TYPE + " =?",
                new String[]{"%com.tencent.mm/MicroMsg%", "audio/x-wav"}, null
        );
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String fileName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME));
                String filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA));


                Log.i("TAG", String.format("文件名称 = %s, 文件路径 = %s", fileName, filePath));
            }
            cursor.close();
        }

        return fileInfoList;
    }

    /**
     * 微信和qq的单独遍历
     * 视频 音频 文档 DOC PDF  PPT XLS TXT
     * <p>
     * 获得指定文件夹下的所有文件
     */
    public void getFolderAllFiles(String dirPath, List<FolderInfo> folderInfos) {

    }


    /**
     * 将文件大小显示为GB,MB等形式
     */
    public String formatSize(long size) {
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
}
