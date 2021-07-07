package com.rgdgr8.beatbox;

public class Sound {
    private String mPath;
    private String mName;
    private Integer id;

    public Sound(String fileName, String path){
        mName = fileName.substring(0,fileName.length()-4);
        mPath = path;
    }
    public Sound(String path){
        String[] pathToFile = path.split("/");
        String fileName = pathToFile[pathToFile.length-1];
        mName = fileName.substring(0,fileName.length()-4);
        mPath = path;
    }
    public String getFileNameWithExtension(){
        String[] pathToFile = mPath.split("/");
        return pathToFile[pathToFile.length-1];
    }

    public void setSoundId(Integer i) { id = i; }
    public String getPath() {
        return mPath;
    }
    public Integer getSoundId() { return id; }
    public String getName() {
        return mName;
    }
}
