package com.gnawe.whatsappsimulation;

import com.gnawe.whatsappsimulation.model.Membre;
import com.gnawe.whatsappsimulation.model.Message;

import jakarta.persistence.*;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientHandler extends Thread {

    private static final List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("whatsappPU");

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private Membre membre;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);

            String pseudo = reader.readLine();

            EntityManager em = emf.createEntityManager();

            // ❌ Vérification pseudo déjà utilisé
            TypedQuery<Membre> query = em.createQuery("SELECT m FROM Membre m WHERE m.pseudo = :pseudo", Membre.class);
            query.setParameter("pseudo", pseudo);
            if (!query.getResultList().isEmpty()) {
                writer.println("❌ Pseudo déjà utilisé. Veuillez redémarrer avec un autre nom.");
                socket.close();
                return;
            }

            // ❌ Vérification du nombre de membres
            if (clients.size() >= 7) {
                writer.println("❌ Le groupe est complet. Maximum 7 membres.");
                socket.close();
                return;
            }

            // ✅ Créer nouveau membre et le sauvegarder
            membre = new Membre();
            membre.setPseudo(pseudo);
            membre.setConnecte(true);

            em.getTransaction().begin();
            em.persist(membre);
            em.getTransaction().commit();

            // ✅ Ajouter au groupe
            clients.add(this);

            // ✅ Envoyer l’historique des 15 derniers messages
            envoyerHistoriqueMessages(em);

            // ✅ Envoyer la liste des membres à tous
            envoyerListeMembres();
            broadcast("✔ " + pseudo + " a rejoint le groupe.");

            em.close();

            String message;
            while ((message = reader.readLine()) != null) {
                if (message.equalsIgnoreCase("QUIT")) break;

                if (!contientMotInterdit(message)) {
                    enregistrerMessage(message);
                    broadcast(pseudo + " : " + message);
                } else {
                    writer.println("⚠ Vous avez été banni pour message inapproprié !");
                    broadcast("❌ " + pseudo + " a été banni du groupe.");
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clients.remove(this);
                socket.close();

                if (membre != null) {
                    membre.setConnecte(false);
                    broadcast("❌ " + membre.getPseudo() + " a quitté le groupe.");
                    envoyerListeMembres();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void envoyerListeMembres() {
        StringBuilder liste = new StringBuilder("MEMBRES:");
        for (ClientHandler client : clients) {
            liste.append(client.membre.getPseudo()).append(",");
        }
        for (ClientHandler client : clients) {
            client.writer.println(liste.toString());
        }
    }

    private void broadcast(String msg) {
        for (ClientHandler client : clients) {
            client.writer.println(msg);
        }
    }

    private boolean contientMotInterdit(String msg) {
        String[] interdits = {"génocide", "attentat", "terrorisme", "chelsea", "java nékhoul"};
        String lower = msg.toLowerCase();
        for (String mot : interdits) {
            if (lower.contains(mot)) return true;
        }
        return false;
    }

    private void enregistrerMessage(String contenu) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Message msg = new Message();
        msg.setContenu(contenu);
        msg.setDateEnvoi(LocalDateTime.now());
        msg.setMembre(membre);

        em.persist(msg);
        em.getTransaction().commit();
        em.close();
    }

    private void envoyerHistoriqueMessages(EntityManager em) {
        List<Message> derniers = em.createQuery(
                        "SELECT m FROM Message m ORDER BY m.dateEnvoi DESC", Message.class)
                .setMaxResults(15)
                .getResultList();

        Collections.reverse(derniers); // Pour affichage chronologique

        for (Message m : derniers) {
            writer.println(m.getMembre().getPseudo() + " : " + m.getContenu());
        }
    }
}
