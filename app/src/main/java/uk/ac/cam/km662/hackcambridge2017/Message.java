package uk.ac.cam.km662.hackcambridge2017;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {

    private String message;
//
//  public static final int DIRECTION_BOT = 0;
//  public static final int DIRECTION_USER = 1;
    private boolean isUser;
    private long id;
    private String dateTime;

    public Message(String message, boolean isUser) {
        this.id = 1;
        this.dateTime = new Date().toString();
        this.message = message;
        this.isUser = isUser;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public boolean getIsUser() {
        return isUser;
    }
    public void setIsUser(boolean isUser) {
        this.isUser = isUser;
    }
    public String getDate() {
        return dateTime;
    }
    public void setDate(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getMessage() {return message;}
    public void setMessage(String message) {this.message = message;}


 }
