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
    public CommandLineRunner commandLineRunner() {
        return args -> {
            writeDocumentFromMap();
            writeDocumentFromObject();
            readDocumentToMap();
            readDocumentToObject();
            removeDocuments();

        };
    }

    private void writeDocumentFromMap() throws InterruptedException, java.util.concurrent.ExecutionException {
        DocumentReference docRef = this.firestore.collection("users").document("ada");
        // Add document data with id "ada" using a hashmap
        Map<String, Object> data = new HashMap<>();
        data.put("name", "Ada");
        data.put("phones", Arrays.asList(123, 456));

        // asynchronously write data
        ApiFuture<WriteResult> result = docRef.set(data);

        // result.get() blocks on response
        System.out.println("Update time: " + result.get().getUpdateTime());
    }

    private void writeDocumentFromObject() throws ExecutionException, InterruptedException {
        // Add document data with id "joe" using a custom User class
        ChatRoom data = new ChatRoom("Joe", "topic");

        // .get() blocks on response
        WriteResult writeResult = this.firestore.document("users/joe").set(data).get();

        System.out.println("Update time: " + writeResult.getUpdateTime());
    }

    private void readDocumentToMap() throws ExecutionException, InterruptedException {
        DocumentReference docRef = this.firestore.document("users/ada");

        ApiFuture<DocumentSnapshot> documentSnapshotApiFuture = docRef.get();

        DocumentSnapshot document = documentSnapshotApiFuture.get();

        System.out.println("read: " + document.getData());
    }

    private void readDocumentToObject() throws ExecutionException, InterruptedException {
        ApiFuture<DocumentSnapshot> documentSnapshotApiFuture = this.firestore.document("users/joe").get();

        ChatRoom user = documentSnapshotApiFuture.get().toObject(ChatRoom.class);

        System.out.println("read: " + user);
    }

    private void removeDocuments() {
        //Warning: Deleting a document does not delete its subcollections!
        //
        //If you want to delete documents in subcollections when deleting a document, you must do so manually.
        //See https://firebase.google.com/docs/firestore/manage-data/delete-data#collections
        CollectionReference users = this.firestore.collection("users");
        Iterable<DocumentReference> documentReferences = users.listDocuments();
        documentReferences.forEach(documentReference -> {
            System.out.println("removing: " + documentReference.getId());
            try {
                documentReference.delete().get();
            }
            catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
    }
}
