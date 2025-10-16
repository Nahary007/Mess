package com.exemple.controller;

import com.exemple.model.Conversation;
import com.exemple.model.Etudiant;
import com.exemple.model.Message;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Named("chatGroupBean")
@ViewScoped
public class ChatGroupController implements Serializable {

    @PersistenceContext
    private EntityManager em;

    @Inject
    private AuthController authController;

    private Conversation conversation;           // Groupe ouvert actuellement
    private List<Message> historique = new ArrayList<>(); // Messages du groupe
    private String nouveauMessage;               // Nouveau message à envoyer

    // --- Recherche d’étudiants pour invitation
    private String query;
    private List<Etudiant> resultatsRecherche = new ArrayList<>();

    @PostConstruct
    public void init() {
        String cidParam = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getRequestParameterMap().get("conversationId");

        if (cidParam != null) {
            try {
                Long conversationId = Long.parseLong(cidParam);
                ouvrirConversationGroupe(conversationId);
            } catch (NumberFormatException e) {
                // log or ignore
            }
        }
    }



    //  Ouvrir un groupe
    public void ouvrirConversationGroupe(Long conversationId) {
        conversation = em.find(Conversation.class, conversationId);
        chargerHistorique();
    }

    //  Charger les messages du groupe
    public void chargerHistorique() {
        if (conversation == null) return;

        historique = em.createQuery(
                        "SELECT m FROM Message m WHERE m.conversation.id = :cid ORDER BY m.date_envoi ASC",
                        Message.class)
                .setParameter("cid", conversation.getId())
                .getResultList();
    }

    //  Envoyer un message dans un groupe
    @Transactional
    public String envoyerMessageGroupe() {
        if (conversation == null || nouveauMessage == null || nouveauMessage.trim().isEmpty()) {
            return null;
        }

        Message msg = new Message();
        msg.setConversation(conversation);
        msg.setExpediteur(authController.getEtudiantConnecte());
        msg.setContenu(nouveauMessage);
        msg.setDateEnvoi(LocalDateTime.now());

        em.persist(msg);

        nouveauMessage = "";

        return "groupes.xhtml?faces-redirect=true&conversationId=" + conversation.getId();

    }


    //  Recherche d’étudiants pour l’invitation
    public void searchEtudiants() {
        if (query == null || query.trim().isEmpty()) {
            resultatsRecherche.clear();
            return;
        }

        resultatsRecherche = em.createQuery(
                        "SELECT e FROM Etudiant e WHERE LOWER(e.nom) LIKE :q OR LOWER(e.email) LIKE :q",
                        Etudiant.class)
                .setParameter("q", "%" + query.toLowerCase() + "%")
                .getResultList();
    }

    //  Réinitialisation du champ de recherche
    public void clearSearch() {
        query = "";
        resultatsRecherche.clear();
    }

    // --- Getters & Setters ---

    public Conversation getConversation() { return conversation; }
    public void setConversation(Conversation conversation) { this.conversation = conversation; }

    public List<Message> getHistorique() { return historique; }
    public void setHistorique(List<Message> historique) { this.historique = historique; }

    public String getNouveauMessage() { return nouveauMessage; }
    public void setNouveauMessage(String nouveauMessage) { this.nouveauMessage = nouveauMessage; }

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }

    public List<Etudiant> getResultatsRecherche() { return resultatsRecherche; }
    public void setResultatsRecherche(List<Etudiant> resultatsRecherche) { this.resultatsRecherche = resultatsRecherche; }

    public AuthController getAuthController() {
        return authController;
    }

}
