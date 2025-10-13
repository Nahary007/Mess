package com.exemple.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String contenu;
    private LocalDateTime date_envoi;

    @ManyToOne
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Etudiant expediteur;

    public Message() {}

    public Message(String contenu, Conversation conversation, Etudiant expediteur) {
        this.contenu = contenu;
        this.conversation = conversation;
        this.expediteur = expediteur;
        this.date_envoi = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getContenu() { return contenu; }

    public Etudiant getExpediteur() {
        return expediteur;
    }
    public void setExpediteur(Etudiant expediteur) {
        this.expediteur = expediteur;
    }

    public LocalDateTime getDate_envoi() {
        return date_envoi;
    }

}
