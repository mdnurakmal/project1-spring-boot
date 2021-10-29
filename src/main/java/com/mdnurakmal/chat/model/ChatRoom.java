package com.mdnurakmal.chat.model;

public class ChatRoom {
    private String name;
    private String topic;

    public ChatRoom() {
    }

    public ChatRoom(String name, String topic) {
        this.name = name;
        this.topic = topic;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTopic() {
        return this.topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    @Override
    public String toString() {
        return "User{" + "name='" + this.name + '\'' + ", topic=" + this.topic + '}';
    }
}
