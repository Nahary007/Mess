package com.exemple.controller;

import com.exemple.model.Etudiant;
import com.exemple.service.EtudiantService;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

import java.io.Serializable;

@Named("authBean")
@SessionScoped
public class AuthController implements Serializable {

    private String email;
    private String motDePasse;
    private Etudiant etudiantConnecte;

    @EJB
    private EtudiantService etudiantService;

    // Getters et Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public Etudiant getEtudiantConnecte() {
        return etudiantConnecte;
    }

    public String login() {
        Etudiant e = etudiantService.connecter(email, motDePasse);

        if(e != null) {
            etudiantConnecte = e;
            return "/pages/home.xhtml?faces-redirect=true";
        } else {
            FacesMessage msg = new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Erreur de connexion",
                    "Email ou mot de passe incorrect"
            );
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        }
    }

    public String logout() {
        etudiantConnecte = null;
        FacesContext.getCurrentInstance()
                .getExternalContext()
                .invalidateSession();
        return "/index?faces-redirect=true";
    }

    public boolean isConnecte() {
        return etudiantConnecte != null;
    }
}