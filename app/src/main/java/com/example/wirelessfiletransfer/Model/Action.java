package com.example.wirelessfiletransfer.Model;

import android.net.Uri;

import androidx.annotation.Nullable;

public class Action {
    private String action;
    private String message;
    private Uri uri;

    public Action(String action, String message, @Nullable Uri uri) {
        this.action = action;
        this.message = message;
        this.uri = uri;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}
