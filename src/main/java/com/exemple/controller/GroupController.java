package com.exemple.controller;

import com.exemple.model.Conversation;
import com.exemple.model.Etudiant;
import com.exemple.model.Invitation;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.io.Serializable;
import java.util.List;

@Named("groupeBean")
@SessionScoped
public class GroupController implements Serializable {

    @PersistenceContext
    private EntityManager em;

    @Inject
    private AuthController authController;

    private String nomGroupe;
    private List<Conversation> groupes;
    private List<Invitation> invitationsRecues;

    @PostConstruct
    public void init() {
        chargerGroupes();
        chargerInvitations();
    }

    public void chargerGroupes() {
        groupes = em.createQuery(
                "SELECT c FROM Conversation c JOIN c.membres m WHERE m.id = :id AND c.type = 'GROUPE'",
                Conversation.class)
                .setParameter("id", authController.getEtudiantConnecte().getId())
                .getResultList()
        ;
    }

    public void chargerInvitations() {
        invitationsRecues = em.createQuery(
                "SELECT i FROM Invitation i WHERE i.destinataire.id = :id AND i.statut = 'EN_ATTENTE'",
                Invitation.class)
                .setParameter("id", authController.getEtudiantConnecte().getId())
                .getResultList()
        ;
    }

    @Transactional
    public void creerGroupe() {
        Conversation c = new Conversation();
        c.setNom(nomGroupe);
        c.setType("GROUPE");

        Etudiant createur = em.find(Etudiant.class, authController.getEtudiantConnecte().getId());
        c.setMembres(List.of(createur));

        em.persist(c);

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Groupe crée avec succès !"));
        nomGroupe = "";
        chargerGroupes();
    }

    @Transactional
    public  void inviter(long etudiantId, Long conversationId) {
        Etudiant expediteur = em.find(Etudiant.class, authController.getEtudiantConnecte().getId());
        Etudiant destinataire = em.find(Etudiant.class, etudiantId);
        Conversation conv = em.find(Conversation.class, conversationId);

        Invitation inv = new Invitation();
        inv.setExpediteur(expediteur);
        inv.setDestinataire(destinataire);
        inv.setConversation(conv);
        inv.setStatut("EN_ATTENTE");

        em.persist(inv);

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Invitation envoyée"));
    }

    @Transactional
    public void accepteerInvitation(Long invitationId) {
        Invitation inv = em.find(Invitation.class, invitationId);
        inv.setStatut("ACCEPTEE");

        Conversation conv = inv.getConversation();
        Etudiant etu = inv.getDestinataire();
        conv.getMembres().add(etu);

        em.merge(conv);
        em.merge(inv);

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Invitation acceptée !"));
        chargerInvitations();
        chargerGroupes();
    }

    @Transactional
    public void refuserInvitation(Long invitationId) {
        Invitation inv = em.find(Invitation.class, invitationId);
        inv.setStatut("REFUSEE");
        em.merge(inv);

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage("Invitation refusée."));
        chargerInvitations();
    }

    public String getNomGroupe() {
        return nomGroupe;
    }

    public void setNomGroupe(String nomGroupe) {
        this.nomGroupe = nomGroupe;
    }

    public List<Conversation> getGroupes() {
        return groupes;
    }

    public void setGroupes(List<Conversation> groupes) {
        this.groupes = groupes;
    }

    public List<Invitation> getInvitationsRecues() {
        return invitationsRecues;
    }

    public void setInvitationsRecues(List<Invitation> invitationsRecues) {
        this.invitationsRecues = invitationsRecues;
    }

}
