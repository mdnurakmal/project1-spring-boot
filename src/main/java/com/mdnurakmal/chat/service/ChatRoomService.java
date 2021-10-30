package com.mdnurakmal.chat.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.mdnurakmal.chat.model.ChatRoom;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class ChatRoomService {
    @Autowired
    Firestore firestore;

    public void writeDocumentFromObject(ChatRoom data,String user,String recipient) throws ExecutionException, InterruptedException {
        WriteResult writeResult = this.firestore.document("users/"+recipient+"/to/"+user).set(data).get();
        System.out.println("Update time: " + writeResult.getUpdateTime());

    }

    public ChatRoom writeDocumentFromObject(String user,String recipient) throws ExecutionException, InterruptedException {
        // Add document data with id "joe" using a custom User class
        ChatRoom data = new ChatRoom(user,recipient);

        // .get() blocks on response
        WriteResult writeResult = this.firestore.document("users/"+user+"/to/"+recipient).set(data).get();
        System.out.println("Update time: " + writeResult.getUpdateTime());

        return data;

    }

    public String sendMessage(String user,String recipient) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentSnapshot> userDocumentFuture =
                this.firestore.document("users/" + user + "/to/"+recipient).get();

        ChatRoom shared_cr;
        ChatRoom user_cr = userDocumentFuture.get().toObject(ChatRoom.class);

        if (user_cr != null)
        {
            shared_cr=user_cr;
            System.out.println("read: " + user);
        }
        else
        {
            System.out.println("no existing message");
            shared_cr = writeDocumentFromObject(user,recipient);
        }

        ApiFuture<DocumentSnapshot> recipientDocumentFuture =
                this.firestore.document("users/" + recipient + "/to/"+user).get();

        ChatRoom recipient_cr = recipientDocumentFuture.get().toObject(ChatRoom.class);

        if (recipient_cr != null)
            System.out.println("read: " + user);
        else
        {
            System.out.println("no existing message");
            writeDocumentFromObject(shared_cr,user,recipient);
        }

        return shared_cr.getTopic();

    }

    public List<ChatRoom> getAllRecipients(String user){

        CollectionReference userDocumentFuture = this.firestore.collection("users/"+user+"/to");
        ApiFuture<QuerySnapshot> querySnapshotApiFuture = userDocumentFuture.get();
        try {
            List<QueryDocumentSnapshot> queryDocumentSnapshots = querySnapshotApiFuture.get().getDocuments();

            return queryDocumentSnapshots.stream()
                    .map(queryDocumentSnapshot -> queryDocumentSnapshot.toObject(ChatRoom.class))
                    .collect(Collectors.toList());

        } catch (InterruptedException | ExecutionException e) {
            //log.error("Exception occurred while retrieving all document for {}", collectionName);
        }
        return Collections.<ChatRoom>emptyList();


    }
}
