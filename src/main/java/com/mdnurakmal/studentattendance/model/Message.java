package com.mdnurakmal.studentattendance.model;

public class Message {
    private String msg;
    private String sender;
    private String receiver;
    private String timeDate;


    public Message(String msg,String sender,String receiver,String timeDate ) {
        this.msg = msg;
        this.sender = sender;
        this.receiver = receiver;
        this.timeDate = timeDate;
    }
}
