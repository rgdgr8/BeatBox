package com.rgdgr8.beatbox;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class SoundViewModel extends BaseObservable {
    private Sound mSound;
    private final BeatBox mBeatBox;
    private BeatBoxFragment mBeatBoxFragment;

    public SoundViewModel(BeatBox beatBox, BeatBoxFragment beatBoxFragment){
        mBeatBox = beatBox;
        mBeatBoxFragment = beatBoxFragment;
    }

    public Sound getSound() {
        return mSound;
    }

    public void setSound(Sound s){
        mSound = s;
        notifyChange();
    }

    @Bindable
    public String getName(){
        return mSound.getName();
    }

    public void onButtonClicked() {
        mBeatBox.play(mSound);
    }

    public boolean onLongClickOnHeading(View v) {
        String[] assetNames = mBeatBox.getAssetNames();
        for(String s : assetNames){
            if(getSound().getFileNameWithExtension().equals(s)) {
                Toast.makeText(v.getContext(), "Default Beats cannot be deleted", Toast.LENGTH_SHORT).show();
                return true;
            }
        }

        new AlertDialog.Builder(v.getContext())
                .setTitle("Delete this Beat?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            int pos = mBeatBox.deleteSound(getSound());
                            mBeatBoxFragment.refreshAdapterOnDelete(pos);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(mBeatBox.getContext(), e.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton("No",null)
                .create()
                .show();
        return false;
    }
}
