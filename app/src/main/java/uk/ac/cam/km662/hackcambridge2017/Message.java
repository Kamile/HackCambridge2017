package uk.ac.cam.km662.hackcambridge2017;

public class Message {

    private String message;
//
//    public static final int DIRECTION_BOT = 0;
//    public static final int DIRECTION_USER = 1;
    private int direction;

    public Message(String message, int direction) {
        this.message = message;
        this.direction = direction;
    }
    
    
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getDirection() { return direction;}
    public void setDirection(int direction) {this.direction = direction;}

 }
