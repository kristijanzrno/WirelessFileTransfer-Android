package com.example.wirelessfiletransfer.Model;

import com.example.wirelessfiletransfer.Constants;

import java.util.Arrays;

public class Message {

    private String action = "";
    private String[] params = {};

    public Message(String message){
        String params[] = decodeMessage(message);
        if(params.length > 0){
            this.action = params[0];
            if(params.length > 1)
                this.params = Arrays.copyOfRange(params, 1, params.length);
        }
    }

    public String getAction() {
        return action;
    }


    public void setAction(String action) {
        this.action = action;
    }

    public String paramAt(int at){
        return params[at];
    }

    public String[] getParams() {
        return params;
    }

    public void setParams(String[] params) {
        this.params = params;
    }

    public static String[] decodeMessage(String message){
        String[] params = {};
        if(message != null && !message.isEmpty())
            params = message.split(Constants.DATA_SEPARATOR);
        return params;
    }


    public static class Builder {
        private StringBuilder message;

        public Builder(){
            message = new StringBuilder();
        }

        public Builder add(String param){
            if(!message.toString().isEmpty())
                message.append(Constants.DATA_SEPARATOR);
            message.append(param);
            return this;
        }

        public String build(){
            return message.toString();
        }
    }

}
