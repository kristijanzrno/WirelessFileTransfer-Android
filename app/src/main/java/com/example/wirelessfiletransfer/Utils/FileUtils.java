package com.example.wirelessfiletransfer.Utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;

import java.io.File;

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

    // https://developer.android.com/training/secure-file-sharing/retrieve-info#java
    public static long getFileSize(Uri uri, Activity activity){
        long result = 0;
        Cursor cursor = activity.getContentResolver().query(uri, null, null, null, null);
        if(cursor != null && cursor.moveToFirst())
            result = cursor.getLong(cursor.getColumnIndex(OpenableColumns.SIZE));
        return result;
    }

    public static String getStoragePath(Context context){
        String storagePath = "";
        // Android 10 makes it harder to access the root of the internal storage,
        // The only initial file access point is the application directory
        // Therefore, the application directory needs to be exited couple of times to get to the root
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            storagePath = context.getExternalFilesDir(null).getParentFile().
                    getParentFile().getParentFile().getParentFile().getAbsolutePath();
        }else{
            // Else use the old convenient Environment
            storagePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return storagePath;
    }

    public static String getMimeType(String filename){
        String mimeType = "";
        int in = filename.lastIndexOf(".");
        String extension = filename.substring(in + 1).toLowerCase();
        mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        System.out.println("MIME TYPE: " + mimeType + " - Filename: " + filename);
        return mimeType;
    }

    public static void addToMediaStore(Context context, File file) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
        values.put(MediaStore.Images.Media.MIME_TYPE, getMimeType(file.getName()));
        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

}
