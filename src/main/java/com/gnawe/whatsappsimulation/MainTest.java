package com.gnawe.whatsappsimulation;

import com.gnawe.whatsappsimulation.model.Membre;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class MainTest {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("whatsappPU");
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Membre membre = new Membre();
            membre.setPseudo("Parfait"); // Tu peux changer ici

            em.persist(membre);
            em.getTransaction().commit();

            System.out.println("✅ Membre ajouté avec succès !");
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }
}
