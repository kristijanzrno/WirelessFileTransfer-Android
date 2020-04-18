package com.example.wirelessfiletransfer.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.wirelessfiletransfer.Constants;
import com.example.wirelessfiletransfer.R;

import java.util.List;

import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Settings extends AppCompatActivity {

    @BindViews({R.id.imagesPath, R.id.audioPath, R.id.videoPath, R.id.documentsPath, R.id.otherPath})
    List<TextView> pathTextViews;

    private String toChange = "";
    private SharedPreferences prefs;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        prefs = getSharedPreferences(Constants.PREFERENCES_KEY, MODE_PRIVATE);
    }

    @OnClick(R.id.imagesCard)
    public void changeImagesPath(View v){
        changePath(Constants.KEY_IMAGES, 0);
    }

    @OnClick(R.id.audioCard)
    public void changeAudioPath(View v){
        changePath(Constants.KEY_AUDIO, 1);
    }

    @OnClick(R.id.videosCard)
    public void changeVideosPath(View v){
        changePath(Constants.KEY_VIDEOS,2);
    }

    @OnClick(R.id.documentsCard)
    public void changeDocumentsPath(View v){
        changePath(Constants.KEY_DOCUMENTS,3);
    }

    @OnClick(R.id.otherCard)
    public void changeOtherPath(View v){
        changePath(Constants.KEY_OTHER, 4);
    }

    private void changePath(String mediaType, int position){
        toChange = mediaType;
        this.position = position;
        Intent filePicker = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        filePicker.addCategory(Intent.CATEGORY_OPENABLE);
        filePicker.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        filePicker.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(filePicker, Constants.FILE_PICKER_REQUEST);
    }

    private void changeMediaUri(Uri uri){
        if(!toChange.isEmpty() && position != -1){
            prefs.edit().putString(toChange, uri.toString()).apply();
            pathTextViews.get(position).setText(uri.toString());
        }
        toChange = "";
        position = -1;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case Constants.FILE_PICKER_REQUEST:
                    Uri pickedUri = data.getData();
                    if(pickedUri != null)
                        changeMediaUri(pickedUri);
                    break;
            }
        }
    }
}
