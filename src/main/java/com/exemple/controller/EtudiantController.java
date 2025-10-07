package com.exemple.controller;

import com.exemple.model.Etudiant;
import com.exemple.service.EtudiantService;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

@Named("etudiantBean")
@RequestScoped
public class EtudiantController {

    private Etudiant etudiant = new Etudiant();

    @EJB
    private EtudiantService etudiantService;

    public Etudiant getEtudiant() {
        return etudiant;
    }

    public String inscrire() {
        etudiantService.inscrire(etudiant);
        return "success.xhtml?faces-redirect=true";
    }
}
