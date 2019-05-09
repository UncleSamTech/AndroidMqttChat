package com.handcarryapp.ustech.seamfixchat;

public class MessageObject {

    // a simple pojo class created for our message class
    public String message;
    public String mqqt_topic;
    public String time;


    public MessageObject(String message, String mqqt_topic, String time) {
        this.message = message;
        this.mqqt_topic = mqqt_topic;
    }

    public MessageObject(String message) {
        this.message = message;
    }



    public String getMessage() {
        return message;
    }

    public String getMqqt_topic() {
        return mqqt_topic;
    }

    public String getTime() {
        return time;
    }
}
