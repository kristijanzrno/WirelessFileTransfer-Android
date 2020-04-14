package com.example.wirelessfiletransfer.Utils;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;

public class FileUtils {

    // https://developer.android.com/guide/topics/providers/document-provider.html
    public static String getFileName(Uri uri, Activity activity){
        String result = null;
        if(uri.getScheme().equals("content")){
            Cursor cursor = activity.getContentResolver().query(uri, null, null, null, null);
            try{
                if(cursor != null && cursor.moveToFirst()){
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }finally{
                cursor.close();
            }
        }
        if(result == null){
            result = uri.getPath();
            int remove = result.lastIndexOf("/");
            if(remove != -1) {
                result = result.substring(remove + 1);
            }
        }
        return result;
    }
}
