package com.exemple.websocket;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/chat/{email}")
public class ChatEndpoint {

    private static final Map<String, Session> utilisateurs = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("email") String email) {
        utilisateurs.put(email, session);
        System.out.println(email + "connecté");
    }


    @OnMessage
    public void onMessage(String message, Session session, @PathParam("email") String email) throws IOException {
        String[] parts = message.split("\\|", 2);
        if(parts.length < 2) return;

        String destinataireEmail = parts[0];
        String contenu = parts[1];

        //Envoyer au destinataire
        Session destSession = utilisateurs.get(destinataireEmail);
        if(destSession != null && destSession.isOpen()) {
            destSession.getBasicRemote().sendText(email+" : " + contenu);
        }

        session.getBasicRemote().sendText("Vous : " + contenu);
    }

    @OnClose
    public void onClose(Session session, @PathParam("email") String email) {
        utilisateurs.remove(email);
        System.out.println(email + " déconnecté");
    }

}
