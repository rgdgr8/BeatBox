package com.rgdgr8.beatbox;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

public class SoundViewModel extends BaseObservable {
    private Sound mSound;
    private BeatBox mBeatBox;

    public SoundViewModel(BeatBox beatBox){
        mBeatBox = beatBox;
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
        return mSound.getAssetName();
    }

    public void onButtonClicked() {
        mBeatBox.play(mSound);
    }
}
