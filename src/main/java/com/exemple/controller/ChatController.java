package com.exemple.controller;

import com.exemple.model.Conversation;
import com.exemple.model.Etudiant;
import com.exemple.model.Message;
import com.exemple.service.ChatService;
import com.exemple.service.EtudiantService;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("chatBean")
@SessionScoped

public class ChatController implements Serializable {

    @EJB
    private ChatService chatService;

    @EJB
    private EtudiantService etudiantService;

    @Inject
    private AuthController authController;

    private Etudiant destinataire;
    private String emailDestinataire;
    private Conversation conversation;
    private String nouveauMessage;

    private List<Message> historique;

    @PostConstruct
    public void init() {
        nouveauMessage = "";
    }

    public String ouvrirConversation() {
        Etudiant expediteur = authController.getEtudiantConnecte();
        destinataire = etudiantService.findByEmail(emailDestinataire);

        if(destinataire == null) {
            return null;
        }

        conversation = chatService.getOrCreateConversation(expediteur, destinataire);
        historique = chatService.getMessages(conversation);

        return "chat.xhtml?faces-redirect=true";
    }

    public void envoyerMessage() {
        if(nouveauMessage == null || nouveauMessage.trim().isEmpty()) return;

        Etudiant expediteur = authController.getEtudiantConnecte();
        chatService.envoyerMessage(conversation, expediteur, nouveauMessage);
        historique = chatService.getMessages(conversation);
        nouveauMessage = "";
    }

    public String getEmailDestinataire() { return emailDestinataire; }
    public void setEmailDestinataire(String emailDestinataire) {this.emailDestinataire = emailDestinataire;}

    public List<Message> getHistorique() {return historique;}

    public String getNouveauMessage() { return nouveauMessage; }
    public void setNouveauMessage(String nouveauMessage) {this.nouveauMessage = nouveauMessage;}

    public Etudiant getDestinataire() { return destinataire; }

    private String query;
    private List<Etudiant> resultatsRecherche = new ArrayList<>();

    public void searchEtudiants() {
        if (query != null && !query.trim().isEmpty()) {
            resultatsRecherche = etudiantService.searchByNomOuEmail(query);
        } else {
            resultatsRecherche.clear();
        }
    }

    public String ouvrirConversationAvec(String email) {
        this.emailDestinataire = email;
        return ouvrirConversation(); // Utilise ta méthode existante
    }

    // Getters & setters
    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }

    public List<Etudiant> getResultatsRecherche() { return resultatsRecherche; }
    public void setResultatsRecherche(List<Etudiant> resultatsRecherche) {
        this.resultatsRecherche = resultatsRecherche;
    }

}
