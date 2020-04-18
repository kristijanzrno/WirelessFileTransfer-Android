package com.example.wirelessfiletransfer.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wirelessfiletransfer.Constants;
import com.example.wirelessfiletransfer.R;
import com.example.wirelessfiletransfer.Utils.FileUtils;

import java.io.File;
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
    private String storage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        prefs = getSharedPreferences(Constants.PREFERENCES_KEY, MODE_PRIVATE);
        storage = FileUtils.getStoragePath(this);
        setUI();
    }

    private void setUI(){
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        ButterKnife.bind(this);

        pathTextViews.get(0).setText(prefs.getString(Constants.KEY_IMAGES, storage + "/" + Constants.DEF_IMAGES));
        pathTextViews.get(1).setText(prefs.getString(Constants.KEY_AUDIO, storage + "/" + Constants.KEY_AUDIO));
        pathTextViews.get(2).setText(prefs.getString(Constants.KEY_VIDEOS, storage + "/" + Constants.KEY_VIDEOS));
        pathTextViews.get(3).setText(prefs.getString(Constants.KEY_DOCUMENTS, storage + "/" + Constants.KEY_DOCUMENTS));
        pathTextViews.get(4).setText(prefs.getString(Constants.KEY_OTHER, storage + "/" + Constants.KEY_OTHER));

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
        filePicker.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        filePicker.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        filePicker.putExtra(DocumentsContract.EXTRA_INITIAL_URI, Uri.parse(storage));
        filePicker.putExtra("android.content.extra.SHOW_ADVANCED", true);
        startActivityForResult(filePicker, Constants.FILE_PICKER_REQUEST);
    }

    private void changeMediaUri(Uri uri){
        String path = FileUtils.getFolderPathFromUri(this, uri);
        if(path != null) {
            if (!toChange.isEmpty() && position != -1) {
                prefs.edit().putString(toChange, path).apply();
                pathTextViews.get(position).setText(path);
            }
            toChange = "";
            position = -1;
        }else{
            Toast.makeText(this, "Could not select the chosen folder.", Toast.LENGTH_SHORT).show();
        }
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
