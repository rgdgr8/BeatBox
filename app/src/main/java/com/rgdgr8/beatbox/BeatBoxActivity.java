package com.rgdgr8.beatbox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.widget.Toast;

public class BeatBoxActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beatbox_activity);

        getSupportActionBar().hide();
        FragmentManager fm = getSupportFragmentManager();
        Fragment frag = fm.findFragmentById(R.id.frame);

        if (frag==null){
            //BeatBox mBeatBox = new BeatBox(this);

            frag = new BeatBoxFragment();
            fm.beginTransaction().add(R.id.frame,frag).commit();
        }
    }
}