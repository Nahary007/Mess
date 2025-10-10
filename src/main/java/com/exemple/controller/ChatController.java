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

    // MODIF: Liste dynamique des conversations (référence, inchangée)
    private List<Conversation> conversations;

    // MODIF: NOUVEAU - Liste des résumés pour affichage (wrapper)
    private List<ContactSummary> conversationSummaries = new ArrayList<>();

    // MODIF: NOUVEAU - Classe wrapper interne pour les propriétés calculées
    public static class ContactSummary {
        private String otherEmail;
        private String otherNom;
        private String lastMsg;
        private Conversation conversation;  // Référence à la conversation complète

        // Constructeur
        public ContactSummary(String otherEmail, String otherNom,  String lastMsg, Conversation conv) {
            this.otherEmail = otherEmail;
            this.otherNom = otherNom;
            this.lastMsg = lastMsg;
            this.conversation = conv;
        }

        // Getters
        public String getOtherEmail() { return otherEmail; }
        public String getOtherNom() { return otherNom; }
        public String getLastMsg() { return lastMsg; }
        public Conversation getConversation() { return conversation; }
    }

    @PostConstruct
    public void init() {
        nouveauMessage = "";
        loadConversations();  // Chargement initial
    }

    // MODIF: Mise à jour pour créer les summaries
    private void loadConversations() {
        Etudiant user = authController.getEtudiantConnecte();
        if (user != null) {
            List<Conversation> convs = chatService.getUserConversations(user);  // Assumer cette méthode dans ChatService
            conversationSummaries.clear();
            for (Conversation conv : convs) {
                // Calcul de l'autre membre (assumant 2 membres max pour chat 1:1)
                Etudiant other = null;
                for (Etudiant membre : conv.getMembres()) {
                    if (!membre.getEmail().equals(user.getEmail())) {
                        other = membre;
                        break;
                    }
                }
                if (other != null) {
                    String otherEmail = other.getEmail();  // Assumant getEmail()
                    String otherNom = other.getNom();      // Assumant getNom()
                    String lastMsg = conv.getLastMsg();
                    conversationSummaries.add(new ContactSummary(otherEmail, otherNom , lastMsg, conv));
                }
            }
            conversations = convs;  // Garder pour référence interne
        } else {
            conversationSummaries.clear();
            conversations = new ArrayList<>();
        }
    }

    // MODIF: Rendue void (pour AJAX), supprime la redirection
    public void ouvrirConversation() {
        Etudiant expediteur = authController.getEtudiantConnecte();
        destinataire = etudiantService.findByEmail(emailDestinataire);

        if (destinataire == null) {
            return;  // Pas de redirect
        }

        conversation = chatService.getOrCreateConversation(expediteur, destinataire);
        historique = chatService.getMessages(conversation);
        // Optionnel : Recharger conversations si nouvelle créée
        loadConversations();
    }

    public void ouvrirConversationAvec(String email) {
        this.emailDestinataire = email;
        ouvrirConversation();
    }

    public void envoyerMessage() {
        if (nouveauMessage == null || nouveauMessage.trim().isEmpty()) return;

        Etudiant expediteur = authController.getEtudiantConnecte();
        chatService.envoyerMessage(conversation, expediteur, nouveauMessage);
        historique = chatService.getMessages(conversation);
        nouveauMessage = "";
    }

    // Getters/setters (ajouts et inchangés)
    public String getEmailDestinataire() { return emailDestinataire; }
    public void setEmailDestinataire(String emailDestinataire) { this.emailDestinataire = emailDestinataire; }

    public List<Message> getHistorique() { return historique; }

    public String getNouveauMessage() { return nouveauMessage; }
    public void setNouveauMessage(String nouveauMessage) { this.nouveauMessage = nouveauMessage; }

    public Etudiant getDestinataire() { return destinataire; }

    public Conversation getConversation() { return conversation; }

    // MODIF: NOUVEAU - Getter pour les summaries (utilisé dans UI)
    public List<ContactSummary> getConversationSummaries() { return conversationSummaries; }

    // Getter pour conversations (référence, inchangé)
    public List<Conversation> getConversations() { return conversations; }

    private String query;
    private List<Etudiant> resultatsRecherche = new ArrayList<>();

    public void searchEtudiants() {
        if (query != null && !query.trim().isEmpty()) {
            resultatsRecherche = etudiantService.searchByNomOuEmail(query);
        } else {
            resultatsRecherche.clear();
        }
    }

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }

    public List<Etudiant> getResultatsRecherche() { return resultatsRecherche; }
    public void setResultatsRecherche(List<Etudiant> resultatsRecherche) {
        this.resultatsRecherche = resultatsRecherche;
    }
}