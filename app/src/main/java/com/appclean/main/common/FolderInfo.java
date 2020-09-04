package com.appclean.main.common;

import java.util.List;

/**
 * 文件夹下的所有文件对象
 */
public class FolderInfo {
    private String folderName; // 文件夹名称

    private List<FileInfo> fileInfoList;


    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public List<FileInfo> getFileInfoList() {
        return fileInfoList;
    }

    public void setFileInfoList(List<FileInfo> fileInfoList) {
        this.fileInfoList = fileInfoList;
    }


}
