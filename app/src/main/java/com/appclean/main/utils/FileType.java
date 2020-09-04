package com.appclean.main.utils;

import android.webkit.MimeTypeMap;

import com.appclean.main.common.FileInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * 通过后缀判断是什么文件
 */
public class FileType {


    public final static Map<String, String> FILE_TYPE_MAP = new HashMap<String, String>();

    private FileType() {
    }

    static {
        //IMAGE
        FILE_TYPE_MAP.put("ffd8ffe000104a464946", "jpg"); //JPEG (jpg)
        FILE_TYPE_MAP.put("89504e470d0a1a0a0000", "png"); //PNG (png)
        FILE_TYPE_MAP.put("47494638396126026f01", "gif"); //GIF (gif)
        FILE_TYPE_MAP.put("49492a00227105008037", "tif"); //TIFF (tif)
        FILE_TYPE_MAP.put("424d228c010000000000", "bmp"); //16色位图(bmp)
        FILE_TYPE_MAP.put("424d8240090000000000", "bmp"); //24位位图(bmp)
        FILE_TYPE_MAP.put("424d8e1b030000000000", "bmp"); //256色位图(bmp)
        //视频
        FILE_TYPE_MAP.put("2e524d46000000120001", "rmvb"); //rmvb/rm相同
        FILE_TYPE_MAP.put("464c5601050000000900", "flv"); //flv与f4v相同
        FILE_TYPE_MAP.put("00000020667479706d70", "mp4");
        FILE_TYPE_MAP.put("000001ba210001000180", "mpg"); //
        FILE_TYPE_MAP.put("3026b2758e66cf11a6d9", "wmv"); //wmv与asf相同
        FILE_TYPE_MAP.put("52494646e27807005741", "wav"); //Wave (wav)
        //音频
        FILE_TYPE_MAP.put("49443303000000002176", "mp3");
    }

    /**
     * 音频
     * wav audio/x-wav
     * mp3 audio/mpeg
     * 视频
     * 3gp video/3gpp
     * MP4 video/mp4
     * avi video/x-msvideo
     *
     *
     */


}
