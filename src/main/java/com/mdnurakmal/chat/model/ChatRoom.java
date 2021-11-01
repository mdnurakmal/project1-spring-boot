package com.mdnurakmal.chat.model;

public class ChatRoom {

    private String user;
    private String recipient;
    private String topic;

    public ChatRoom() {
    }

    public ChatRoom( String user, String recipient) {

        this.user = user;
        this.recipient = recipient;
        this.topic = user+"."+recipient;
    }

    public String getUser() {
        return this.user;
    }

    public String getTopic() {
        return this.topic;
    }

    public void setUser(String user) {
        this.user = user;
    }


    public String getRecipient() {
        return this.recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }



    @Override
    public String toString() {
        return "Chatroom : " +  ", topic=" + this.topic  +  ", user=" + this.user +   ", recipient=" + this.recipient;
    }
}
