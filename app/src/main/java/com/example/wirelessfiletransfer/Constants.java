package com.example.wirelessfiletransfer;

public class Constants {
    public static final int FILE_PICKER_REQUEST = 4;
    public static final String FILE_SEND_MESSAGE = "_transferring";
    public static final String FILE_NAME_MESSAGE = "_filename";
    public static final String FILE_RECEIVED = "_received";
    public static final String DATA_SEPARATOR = ":";
    public static final String CONNECTION_TERMINATOR = "_terminate";
    public static final String CONNECTION_REQUEST = "_connect";
    public static final String CONNECTION_ACCEPTED = "_accepted";
    public static final String CONNECTION_REFUSED = "_refused";


    // Network Broadcasts
    public static final int BROADCAST_WAIT_TIME = 3000;
    // Shared Preferences Keys
    public static final String DEF_IMAGES = "Pictures";
    public static final String DEF_AUDIO = "Audio";
    public static final String DEF_VIDEOS = "Videos";
    public static final String DEF_DOCUMENTS = "Documents";
    public static final String DEF_OTHER = "Other";
    public static final String KEY_IMAGES = "imagePath";
    public static final String KEY_AUDIO = "audioPath";
    public static final String KEY_VIDEOS  = "videosPath";
    public static final String KEY_DOCUMENTS = "documentsPath";
    public static final String KEY_OTHER = "otherPath";
    public static final String PREFERENCES_KEY = "default";
}
