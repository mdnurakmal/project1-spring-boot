package com.mdnurakmal.chat.model;

public class Message {
    private String sender;
    private String receiver;
    private String content;
    private String timestamp;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Message(String sender,String receiver, String content) {
        this.sender = sender;
        this.receiver= receiver;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Message{" +
                "sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", content='" + content + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}