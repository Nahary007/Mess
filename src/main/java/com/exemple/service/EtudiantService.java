package com.exemple.service;

import com.exemple.model.Etudiant;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@Stateless
public class EtudiantService {

    @PersistenceContext(unitName = "MessPU")
    private EntityManager em;

    public void inscrire(Etudiant etudiant) {
        em.persist(etudiant);
    }

    public Etudiant connecter(String email, String motDePasse) {
        try {
            return em.createQuery("SELECT e FROM Etudiant e WHERE e.email = :email AND e.motDePasse = :mdp", Etudiant.class)
                    .setParameter("email", email)
                    .setParameter("mdp", motDePasse)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    // Ajout de la méthode findByEmail
    public Etudiant findByEmail(String email) {
        try {
            return em.createQuery("SELECT e FROM Etudiant e WHERE e.email = :email", Etudiant.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public List<Etudiant> searchByNomOuEmail(String terme) {
        return em.createQuery("SELECT e FROM Etudiant e WHERE LOWER(e.nom) LIKE :terme OR LOWER(e.email) LIKE :terme", Etudiant.class)
                .setParameter("terme", "%" + terme.toLowerCase() + "%")
                .getResultList();
    }

}