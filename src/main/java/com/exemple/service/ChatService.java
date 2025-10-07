package com.exemple.service;

import com.exemple.model.Conversation;
import com.exemple.model.Etudiant;
import com.exemple.model.Message;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class ChatService {

    @PersistenceContext(unitName = "MessPU")
    private EntityManager em;

    public Conversation getOrCreateConversation(Etudiant e1, Etudiant e2) {
        List<Conversation> result = em.createQuery("""
                SELECT c FROM Conversation c
                JOIN c.membres m1
                JOIN c.membres m2
                WHERE m1 = :e1 AND m2 = :e2
                """, Conversation.class
        )
                .setParameter("e1", e1)
                .setParameter("e2", e2)
                .getResultList();

        if (!result.isEmpty()) {
            return result.get(0);
        }

        Conversation conv = new Conversation();
        conv.setDate_creation(LocalDateTime.now());
        conv.setType("privée");
        conv.setNom("Conv_" + e1.getEmail() + "_" + e2.getEmail());
        conv.setMembres(List.of(e1, e2));
        em.persist(conv);
        return conv;
    }

    public List<Message> getMessages(Conversation conversation) {
        return em.createQuery("SELECT m FROM Message m WHERE m.conversation= :conv ORDER BY m.date_envoi ASC", Message.class)
                .setParameter("conv", conversation)
                .getResultList();
    }


    public void envoyerMessage(Conversation conversation, Etudiant expediteur, String contenu) {
        Message msg = new Message(contenu, conversation, expediteur);
        em.persist(msg);
    }
}
