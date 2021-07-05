package com.rgdgr8.beatbox;

public class Sound {
    private String mAssetPath;
    private String mAssetName;
    private Integer id;

    public Sound(String fileName, String path){
        mAssetName = fileName.replace(".wav","");
        mAssetPath = path;
    }

    public void setSoundId(Integer i) { id = i; }
    public String getAssetPath() {
        return mAssetPath;
    }
    public Integer getSoundId() { return id; }
    public String getAssetName() {
        return mAssetName;
    }
}
