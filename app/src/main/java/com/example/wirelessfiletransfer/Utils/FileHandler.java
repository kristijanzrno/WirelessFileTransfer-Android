package com.example.wirelessfiletransfer.Utils;

import android.content.Context;
import android.net.Uri;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FileHandler{


    public static boolean writeFile(Context context, String filename, long fileSize, InputStream inputStream){
        String path = PathFinder.findPath(context, filename);
        System.out.println("PATH: " + path);
        File file = new File(PathFinder.findPath(context, filename));
        try {
            // Create directories if needed
            file.getParentFile().mkdirs();
            file.createNewFile();

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

            byte[] buffer = new byte[8192];
            int count = 0;
            while (fileSize > 0 && (count = inputStream.read(buffer, 0, (int) Math.min(buffer.length, fileSize))) > 0) {
                bufferedOutputStream.write(buffer, 0, count);
                fileSize -= count;
            }
            bufferedOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        System.out.println("done = " + file.getAbsolutePath());
        FileUtils.addToMediaStore(context, file);
        return true;
    }

    public static boolean readFile(Context context, Uri fileUri, DataOutputStream output){
        System.out.println("sending...");
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(fileUri);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(output);
            output.flush();
            byte[] buffer = new byte[8192];
            int count;
            while((count = bufferedInputStream.read(buffer)) > 0){
                bufferedOutputStream.write(buffer, 0, count);
            }
            bufferedOutputStream.flush();
            bufferedInputStream.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("done...");
        return true;
    }
}
