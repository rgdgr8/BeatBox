package com.rgdgr8.beatbox;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BeatBox {
    public static final String SOUNDS_FOLDER = "sample_sounds";
    private static final String TAG = "BeatBox";
    public static final int MAX_SOUNDS = 5;

    private AssetManager mAssetManager;
    private List<Sound> soundList;
    private SoundPool mSoundPool;

    public BeatBox(Context ctx){
        mAssetManager = ctx.getAssets();
        soundList = new ArrayList<>();
        mSoundPool = new SoundPool(MAX_SOUNDS, AudioManager.STREAM_MUSIC,0);
        String[] assets = null;

        try {
            assets = mAssetManager.list(SOUNDS_FOLDER);
            Log.i(TAG, "BeatBox: "+assets.length+" assets found");
            for (String s: assets) {
                //Log.d(TAG, "BeatBox: "+s);
                String path = SOUNDS_FOLDER+"/"+s;
                Sound sound = new Sound(s,path);
                soundList.add(sound);
                load(sound);
            }
        }catch (Exception e){
            Log.e(TAG, "BeatBox: No assets found");
        }
    }

    private void load(Sound s) throws IOException {
        AssetFileDescriptor afd = mAssetManager.openFd(s.getAssetPath());
        int id = mSoundPool.load(afd,1);
        s.setSoundId(id);
    }

    public void play(Sound sound) {
        Integer soundId = sound.getSoundId();
        if (soundId == null) {
            return; }
        mSoundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public List<Sound> getSounds(){
        return soundList;
    }
    public void release(){
        mSoundPool.release();
    }
}
