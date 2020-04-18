package com.example.wirelessfiletransfer.Utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
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

    // Using android:requestLegacyExternalStorage="true"
    // The program will be able to write the external storage without
    // going through DocumentProvider/SAF.

    // Application only allows writing on internal storage at the moment (SD cards are not supported yet)
    // Path to URI for SDK versions above 19
    public static String getFolderPathFromUri(Context context, Uri uri){
        if(isExternalStorageDocument(uri)) {
            String uriSegment = uri.getLastPathSegment();
            String storage = getStoragePath(context);
            String[] splitURI = uriSegment.split(":");
            String type = splitURI[0];
            if("home".equalsIgnoreCase(type)){
                //home: equals to the Documents folder
                storage += "/Documents/";
            } else if ("primary".equalsIgnoreCase(type)){
                //other folders on internal storage
                storage += "/";
            }else{
                return null;
            }
            if(splitURI.length > 1)
                storage += splitURI[1];;
            return storage;
        }
        return null;
    }

    // https://github.com/HBiSoft/PickiT/blob/master/pickit/src/main/java/com/hbisoft/pickit/Utils.java
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }


}
