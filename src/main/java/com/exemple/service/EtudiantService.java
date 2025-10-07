package com.exemple.service;

import com.exemple.model.Etudiant;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class EtudiantService {

    @PersistenceContext(unitName = "MessPU")
    private EntityManager em;

    public void inscrire(Etudiant etudiant) {
        em.persist(etudiant);
    }
}
