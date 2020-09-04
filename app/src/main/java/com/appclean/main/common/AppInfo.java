package com.appclean.main.common;

import android.graphics.drawable.Drawable;

import com.j256.ormlite.stmt.query.In;

import java.io.Serializable;

/**
 * 保存应用信息
 */
public class AppInfo implements Serializable {

    private String pkgName;
    private String appName;
    private Drawable appIcon;
    private int type;//0 是系统应用 1 是第三方应用
    private String cacheSize; //缓存大小
    private String dataSize; //数据大小
    private String appSize;//app数据大小
    private String totalSize; // 总的大小
    private String sourceFilePath;//apk安装包路径

    @Override
    public String toString() {
        return "AppInfo{" +
                "pkgName='" + pkgName + '\'' +
                ", appName='" + appName + '\'' +
                ", appIcon=" + appIcon +
                ", type=" + type +
                ", cacheSize='" + cacheSize + '\'' +
                ", dataSize='" + dataSize + '\'' +
                ", appSize='" + appSize + '\'' +
                ", totalSize='" + totalSize + '\'' +
                '}';
    }

    public String getSourceFilePath() {
        return sourceFilePath;
    }

    public void setSourceFilePath(String sourceFilePath) {
        this.sourceFilePath = sourceFilePath;
    }

    public String getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(String totalSize) {
        this.totalSize = totalSize;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(String cacheSize) {
        this.cacheSize = cacheSize;
    }

    public String getDataSize() {
        return dataSize;
    }

    public void setDataSize(String dataSize) {
        this.dataSize = dataSize;
    }

    public String getAppSize() {
        return appSize;
    }

    public void setAppSize(String appSize) {
        this.appSize = appSize;
    }
}
