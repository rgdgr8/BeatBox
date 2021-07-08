package com.rgdgr8.beatbox;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BeatBox {
    public static final String SOUNDS_FOLDER = "sample_sounds";
    private static final String TAG = "BeatBox";
    public static final int MAX_SOUNDS = 5;

    private String[] assets;

    public String[] getAssetNames() {
        return assets;
    }

    private AssetManager mAssetManager;
    private List<Sound> soundList;
    private SoundPool mSoundPool;
    private Context mCtx;

    public BeatBox(Context ctx) {
        mCtx = ctx;
        mAssetManager = ctx.getAssets();
        soundList = new ArrayList<>();
        mSoundPool = new SoundPool(MAX_SOUNDS, AudioManager.STREAM_MUSIC, 0);

        try {
            assets = mAssetManager.list(SOUNDS_FOLDER);
            Log.i(TAG, "BeatBox: " + assets.length + " assets found");
            for (String s : assets) {
                //Log.d(TAG, "BeatBox: "+s);
                String path = SOUNDS_FOLDER + "/" + s;
                Sound sound = new Sound(s, path);
                soundList.add(sound);
                loadAsset(sound);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "BeatBox: No assets found");
        }

        try {
            File pvtDir = ctx.getFilesDir();
            String[] userBeats = pvtDir.list();
            Log.i(TAG, "BeatBox: " + userBeats.length + " user beat files found");
            for (String s : userBeats) {
                String path = pvtDir.getAbsolutePath() + "/" + s;
                Sound sound = new Sound(s, path);
                soundList.add(sound);
                sound.setSoundId(load(path));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "BeatBox: No user beat files found");
        }

    }

    public void addSound(String path) throws Exception{
        File srcFile = new File(path);
        String[] pathToFile = path.split("/");
        String fileName = pathToFile[pathToFile.length-1];
        File destFile = new File(mCtx.getFilesDir(),fileName);
        Files.copy(srcFile.toPath(),destFile.toPath());

        int id = mSoundPool.load(destFile.getPath(), 1);

        Sound s = new Sound(fileName,destFile.getPath());
        s.setSoundId(id);
        soundList.add(s);
    }

    public int deleteSound(Sound sound) throws Exception {
        mSoundPool.unload(sound.getSoundId());

        int pos = soundList.indexOf(sound);
        soundList.remove(pos);

        File file = new File(sound.getPath());
        Files.delete(file.toPath());

        return pos;
    }

    private void loadAsset(Sound s) throws IOException {
        AssetFileDescriptor afd = mAssetManager.openFd(s.getPath());
        int id = mSoundPool.load(afd, 1);
        s.setSoundId(id);
    }

    private int load(String path) throws IOException {
        return mSoundPool.load(path, 1);
    }

    public void play(Sound sound) {
        Integer soundId = sound.getSoundId();
        if (soundId == null) {
            return;
        }
        mSoundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f);
    }

    public void pause() {
        mSoundPool.autoPause();
    }

    public void resume() {
        mSoundPool.autoResume();
    }

    public List<Sound> getSounds() {
        return soundList;
    }

    public void release() {
        mSoundPool.release();
    }
}
