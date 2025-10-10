package com.exemple.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "conversation")
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String type;
    private LocalDateTime date_creation;

    @ManyToMany
    @JoinTable(
            name = "conversation_member",
            joinColumns = @JoinColumn(name = "conversation_id"),
            inverseJoinColumns = @JoinColumn(name = "etudiant_id")
    )
    private List<Etudiant> membres;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL)
    private List<Message> messages;

    // Constructeurs (inchangés)
    public Conversation() {
    }

    public Conversation(String nom, String type, LocalDateTime date_creation) {
        this.nom = nom;
        this.type = type;
        this.date_creation = date_creation;
    }

    // MODIF: Ajout de getLastMsg() pour le dernier message (texte court)
    public String getLastMsg() {
        if (messages == null || messages.isEmpty()) {
            return "Conversation vide";
        }
        Message last = messages.get(messages.size() - 1);
        String text = last.getContenu();  // Assumant que Message a getContenu()
        return text.length() > 50 ? text.substring(0, 50) + "..." : text;
    }

    // Getters et Setters (inchangés)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getDate_creation() {
        return date_creation;
    }

    public void setDate_creation(LocalDateTime date_creation) {
        this.date_creation = date_creation;
    }

    public List<Etudiant> getMembres() {
        return membres;
    }

    public void setMembres(List<Etudiant> membres) {
        this.membres = membres;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}