package com.appclean.main.manager;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;

public class WxFileManager implements IFileManager
{
    @Override
    public void queryAudio(Context context, String dirPath) {
        Uri contentUri = MediaStore.Files.getContentUri(dirPath);
    }

    @Override
    public void queryVideo(Context context, String dirPath) {

    }

    @Override
    public void queryText(Context context, String dirPath) {

    }

    @Override
    public void queryImage(Context context, String dirPath) {

    }
}
