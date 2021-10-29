package com.mdnurakmal.chat.configuration;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.mdnurakmal.chat.model.ChatRoom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FireStoreConfig {
    @Autowired
    Firestore firestore;

    
    @Bean
    void writeDocumentFromObject() throws ExecutionException, InterruptedException {
        // Add document data with id "joe" using a custom User class
        ChatRoom data = new ChatRoom("Joe", "topic");

        // .get() blocks on response
        WriteResult writeResult = this.firestore.document("users/joe").set(data).get();
        System.out.println("Update time: " + writeResult.getUpdateTime());

    }

    ChatRoom readDocumentToObject() throws ExecutionException, InterruptedException {
        ApiFuture<DocumentSnapshot> documentFuture =
                this.firestore.document("users/joe").get();

        ChatRoom user = documentFuture.get().toObject(ChatRoom.class);
        System.out.println("read: " + user);

        return user;
    }
}
