package com.example.wirelessfiletransfer.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.wirelessfiletransfer.Constants;

public class PathFinder {

    public static String findPath(Context context, String filename){
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFERENCES_KEY, Context.MODE_PRIVATE);
        String extension = FileUtils.getMimeType(filename);
        String path = FileUtils.getStoragePath(context);
        String folder = prefs.getString(Constants.KEY_OTHER, Constants.KEY_OTHER);
        if(extension != null) {
            if (extension.startsWith("image")) {
                folder = prefs.getString(Constants.KEY_IMAGES, Constants.DEF_IMAGES);
            } else if (extension.startsWith("video")) {
                folder = prefs.getString(Constants.KEY_VIDEOS, Constants.DEF_VIDEOS);
            } else if (extension.startsWith("audio")) {
                folder = prefs.getString(Constants.KEY_AUDIO, Constants.DEF_AUDIO);
            } else if (extension.startsWith("text") || extension.startsWith("application")) {
                folder = prefs.getString(Constants.KEY_DOCUMENTS, Constants.DEF_DOCUMENTS);
            }
        }
        path = path + "/" + folder + "/" + filename;
        return path;
    }


}
