package com.mdnurakmal.chat.java;

import com.mdnurakmal.chat.model.ChatRoom;
import com.mdnurakmal.chat.service.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
class ApplicationListenerExample {

    @Autowired
    ChatRoomService chatRoomService;

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) throws ExecutionException, InterruptedException {
        //chatRoomService.writeDocumentFromObject();

//        System.out.println("starting chatroom service");
//
//        chatRoomService.sendMessage("mdnurakmal@gmail.com","tosexisted@gmail.com");
//        chatRoomService.sendMessage("mdnurakmal@gmail.com","tosexisted2@gmail.com");
//        chatRoomService.sendMessage("mdnurakmal@gmail.com","tosexisted3@gmail.com");
//        chatRoomService.sendMessage("tosexisted@gmail.com","mdnurakmal@gmail.com");
//
//        List<ChatRoom> temp = chatRoomService.getAllRecipients("mdnurakmal@gmail.com");
//        for (ChatRoom chatRoom : temp) {
//            System.out.println(chatRoom.toString());
//        }
    }
}