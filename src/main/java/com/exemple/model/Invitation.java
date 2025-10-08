package com.exemple.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "invitation")
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Etudiant expediteur;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private Etudiant destinataire;

    @ManyToOne
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    private String statut;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_envoi")
    private Date dateEnvoi = new Date();

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Etudiant getExpediteur() {
        return expediteur;
    }

    public void setExpediteur(Etudiant expediteur) {
        this.expediteur = expediteur;
    }

    public Etudiant getDestinataire() {
        return destinataire;
    }

    public void setDestinataire(Etudiant destinataire) {
        this.destinataire = destinataire;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Date getDateEnvoi() {
        return dateEnvoi;
    }

    public void setDateEnvoi(Date dateEnvoi) {
        this.dateEnvoi = dateEnvoi;
    }
}
