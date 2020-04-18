package com.example.wirelessfiletransfer.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.wirelessfiletransfer.Constants;

public class PathFinder {

    public static String findPath(Context context, String filename){
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFERENCES_KEY, Context.MODE_PRIVATE);
        String extension = FileUtils.getMimeType(filename);
        String path = FileUtils.getStoragePath(context);
        String folder = prefs.getString(Constants.KEY_OTHER, "_name:" + Constants.KEY_OTHER);
        if(extension != null) {
            if (extension.startsWith("image")) {
                folder = prefs.getString(Constants.KEY_IMAGES, "_name:" + Constants.DEF_IMAGES);
            } else if (extension.startsWith("video")) {
                folder = prefs.getString(Constants.KEY_VIDEOS, "_name:" + Constants.DEF_VIDEOS);
            } else if (extension.startsWith("audio")) {
                folder = prefs.getString(Constants.KEY_AUDIO, "_name:" + Constants.DEF_AUDIO);
            } else if (extension.startsWith("text") || extension.startsWith("application")) {
                folder = prefs.getString(Constants.KEY_DOCUMENTS, "_name:" + Constants.DEF_DOCUMENTS);
            }
        }
        if(folder.startsWith("_name")){
            folder = folder.replace("_name", "");
            path = path + "/" + folder + "/" + filename;
        }else{
            if(!folder.endsWith("/"))
                folder += "/";
            path = folder + filename;
        }
        System.out.println(path);
        return path;
    }

}
