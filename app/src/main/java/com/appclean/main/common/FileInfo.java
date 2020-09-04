package com.appclean.main.common;

public class FileInfo {

    private String fileName;
    private String fileAbsPath;
    private long fileSize;
    private FileType fileType;
    private long fileAddDataTime; // 文件添加的时间


    @Override
    public String toString() {
        return "FileInfo{" +
                "fileName='" + fileName + '\'' +
                ", fileAbsPath='" + fileAbsPath + '\'' +
                ", fileSize=" + fileSize +
                ", fileType=" + fileType +
                ", fileAddDataTime=" + fileAddDataTime +
                '}';
    }

    public long getFileAddDataTime() {
        return fileAddDataTime;
    }

    public void setFileAddDataTime(long fileAddDataTime) {
        this.fileAddDataTime = fileAddDataTime;
    }

    public enum FileType {
        IMG, VIDEO, AUDIO, APK, DOC, PDF, PPT, XLS, TXT,ZIP
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileAbsPath() {
        return fileAbsPath;
    }

    public void setFileAbsPath(String fileAbsPath) {
        this.fileAbsPath = fileAbsPath;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }
}
