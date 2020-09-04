package com.appclean.main.manager;

import android.content.Context;

/**
 * 微信 QQ 的适配查询
 */
public interface IFileManager {
    /**
     * 查询音频
     *
     * @param context
     * @param dirPath 需要查询的根目录
     */
    void queryAudio(Context context, String dirPath);

    /**
     * 查询视频
     */
    void queryVideo(Context context, String dirPath);

    /**
     * 查询文档
     */
    void queryText(Context context, String dirPath);

    /**
     * 查询图片
     */
    void queryImage(Context context, String dirPath);
}
