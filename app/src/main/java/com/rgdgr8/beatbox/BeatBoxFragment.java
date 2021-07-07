package com.rgdgr8.beatbox;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rgdgr8.beatbox.databinding.BeatboxFragmentBinding;
import com.rgdgr8.beatbox.databinding.ListItemBinding;

import java.io.File;
import java.util.List;

public class BeatBoxFragment extends Fragment {
    private static final String TAG = "BeatBoxFrag";
    private static final int ADD_AUDIO_REQUEST_CODE = 1;
    private BeatBox mBeatBox;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private SoundAdapter adapter;

    public String getPath(Uri uri) {
        // just some safety built in
        if( uri == null ) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Audio.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        // this is our fallback here
        return uri.getPath();
    }

    public boolean isUsableAudio(String path){
        String[] pathToFile = path.split("/");
        String fileName = pathToFile[pathToFile.length-1];
        String extension = fileName.substring(fileName.length()-3);
        return extension.equals("wav") || extension.equals("mp3");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBeatBox = new BeatBox(getActivity());
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getData()!=null && result.getResultCode() == Activity.RESULT_OK){
                            Uri uri = result.getData().getData();
                            Log.d(TAG, "onActivityResult: "+uri.getPath());
                            if (!isUsableAudio(uri.getPath())){
                                Toast.makeText(getActivity(), "Add a .wav or .mp3 file only !!", Toast.LENGTH_LONG).show();
                                return;
                            }

                            String path = getPath(uri);
                            Log.d(TAG, "onActivityResult: "+path);
                            try {
                                mBeatBox.addSound(path);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                            }
                            adapter.notifyItemInserted((mBeatBox.getSounds().size()-1));
                        }
                    }
                });
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.beat_box_frag_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.new_beat:
                try {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},ADD_AUDIO_REQUEST_CODE);
                    } else {
                        startAudioIntent();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;

            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ADD_AUDIO_REQUEST_CODE) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startAudioIntent();
            }
        }
    }

    public void startAudioIntent(){
        Intent audioIntent = new Intent();
        audioIntent.setAction(Intent.ACTION_GET_CONTENT);
        audioIntent.setType("audio/*");
        activityResultLauncher.launch(audioIntent);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        BeatboxFragmentBinding binding = DataBindingUtil.inflate(inflater,R.layout.beatbox_fragment,container,false);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
        adapter = new SoundAdapter(mBeatBox.getSounds());
        binding.recyclerView.setAdapter(adapter);
        return binding.getRoot();
    }

    private class SoundHolder extends RecyclerView.ViewHolder {
        ListItemBinding lib;

        public SoundHolder(ListItemBinding lib) {
            super(lib.getRoot());
            this.lib = lib;
            lib.setViewModel(new SoundViewModel(mBeatBox, BeatBoxFragment.this));
        }

        public void bind(Sound s){
            lib.getViewModel().setSound(s);
            lib.executePendingBindings();
        }
    }

    private class SoundAdapter extends RecyclerView.Adapter<SoundHolder>{
        private List<Sound> mSounds;

        public SoundAdapter(List<Sound> sounds){
            mSounds = sounds;
        }

        @NonNull
        @Override
        public SoundHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            ListItemBinding lib = DataBindingUtil.inflate(inflater,R.layout.list_item,parent,false);
            return new SoundHolder(lib);
        }

        @Override
        public void onBindViewHolder(@NonNull SoundHolder holder, int position) {
            Sound s = mSounds.get(position);
            holder.bind(s);
        }

        @Override
        public int getItemCount() {
            return mSounds.size();
        }
    }

    public void refreshAdapterOnDelete(int pos){
        if(pos<0){
            adapter.notifyDataSetChanged();
        }else {
            adapter.notifyItemRemoved(pos);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBeatBox.release();
    }

    @Override
    public void onPause() {
        super.onPause();
        mBeatBox.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mBeatBox.resume();
    }
}
