package com.exemple.controller;

import com.exemple.model.Conversation;
import com.exemple.model.Etudiant;
import com.exemple.model.Message;
import com.exemple.service.ChatService;
import com.exemple.service.EtudiantService;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    // Liste dynamique des conversations (référence, inchangée)
    private List<Conversation> conversations;

    // Liste des résumés pour affichage (wrapper)
    private List<ContactSummary> conversationSummaries = new ArrayList<>();

    // Classe wrapper interne pour les propriétés calculées
    public static class ContactSummary {
        private String otherEmail;
        private String otherNom;
        private String lastMsg;
        private Conversation conversation;  // Référence à la conversation complète

        // Constructeur
        public ContactSummary(String otherEmail, String otherNom, String lastMsg, Conversation conv) {
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

    // Mise à jour pour gérer groupes et privés séparément (MODIFIÉ : filtre pour n'afficher que les privés)
    private void loadConversations() {
        Etudiant user = authController.getEtudiantConnecte();
        if (user != null) {
            List<Conversation> convs = chatService.getUserConversations(user);  // Assumer cette méthode dans ChatService
            conversationSummaries.clear();
            for (Conversation conv : convs) {
                // MODIF : Ignorer les conversations de groupe pour n'afficher que les discussions privées (1:1)
                if ("GROUPE".equals(conv.getType())) {
                    continue;
                }

                String otherEmail = "";
                String otherNom = "";
                String lastMsg = conv.getLastMsg();

                // Pour les privés (1:1) : trouver l'autre membre unique
                List<Etudiant> others = conv.getMembres().stream()
                        .filter(m -> !m.getEmail().equals(user.getEmail()))
                        .collect(Collectors.toList());
                if (others.size() == 1) {
                    Etudiant other = others.get(0);
                    otherEmail = other.getEmail();
                    otherNom = other.getNom();
                } else {
                    // Ignorer si plus d'un autre membre (anomalie)
                    continue;
                }
                if (!otherNom.isEmpty()) {  // Seulement ajouter si nom valide
                    conversationSummaries.add(new ContactSummary(otherEmail, otherNom, lastMsg, conv));
                }
            }
            conversations = convs;  // Garder pour référence interne
        } else {
            conversationSummaries.clear();
            conversations = new ArrayList<>();
        }
    }

    // Rendue void (pour AJAX), supprime la redirection
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

    // MODIF: NOUVEAU - Méthode dédiée pour ouvrir une conversation de groupe par ID
    public void ouvrirConversationGroupe(Long convId) {
        conversation = chatService.getConversationById(convId);  // Assumer implémentée dans ChatService
        if (conversation != null && "GROUPE".equals(conversation.getType()) && isMemberOfConversation(conversation)) {
            historique = chatService.getMessages(conversation);
            // Marquer comme lue si besoin (optionnel)
            loadConversations();  // Recharger les résumés pour mettre à jour lastMsg
        } else {
            // Gérer erreur
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Accès refusé à cette conversation de groupe."));
        }
    }

    // Vérifie si l'utilisateur est membre
    private boolean isMemberOfConversation(Conversation conv) {
        Etudiant user = authController.getEtudiantConnecte();
        return conv.getMembres().stream()
                .anyMatch(m -> m.getId().equals(user.getId()));
    }

    // Méthode existante pour chats privés (inchangée)
    public void envoyerMessage() {
        if (nouveauMessage == null || nouveauMessage.trim().isEmpty()) return;

        Etudiant expediteur = authController.getEtudiantConnecte();
        chatService.envoyerMessage(conversation, expediteur, nouveauMessage);
        historique = chatService.getMessages(conversation);
        nouveauMessage = "";
        // Recharger les résumés pour mettre à jour le lastMsg
        loadConversations();
    }

    // MODIF: NOUVEAU - Méthode dédiée pour envoyer un message dans un groupe
    public void envoyerMessageGroupe() {
        if (nouveauMessage == null || nouveauMessage.trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Avertissement", "Le message ne peut pas être vide."));
            return;
        }

        if (conversation == null || !"GROUPE".equals(conversation.getType())) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Cette action est réservée aux groupes de discussion."));
            return;
        }

        Etudiant expediteur = authController.getEtudiantConnecte();
        if (!isMemberOfConversation(conversation)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Erreur", "Vous n'êtes pas membre de ce groupe."));
            return;
        }

        chatService.envoyerMessage(conversation, expediteur, nouveauMessage);
        historique = chatService.getMessages(conversation);
        nouveauMessage = "";
        loadConversations();  // Met à jour les résumés pour tous les membres (lastMsg partagé)

        // Message de succès optionnel
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Message envoyé au groupe avec succès !"));
    }

    // Getters/setters (ajouts et inchangés)
    public String getEmailDestinataire() { return emailDestinataire; }
    public void setEmailDestinataire(String emailDestinataire) { this.emailDestinataire = emailDestinataire; }

    public List<Message> getHistorique() { return historique; }

    public String getNouveauMessage() { return nouveauMessage; }
    public void setNouveauMessage(String nouveauMessage) { this.nouveauMessage = nouveauMessage; }

    public Etudiant getDestinataire() { return destinataire; }

    public Conversation getConversation() { return conversation; }

    // Getter pour les summaries (utilisé dans UI)
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

    public AuthController getAuthController() {
        return authController;
    }
}