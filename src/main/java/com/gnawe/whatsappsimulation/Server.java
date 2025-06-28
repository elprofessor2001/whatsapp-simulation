package com.gnawe.whatsappsimulation;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) {
        final int PORT = 2001; // ✅ Nouveau port pour éviter le conflit
        System.out.println("📡 Serveur en attente de connexions sur le port " + PORT + "...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("✅ Nouveau client connecté : " + clientSocket.getInetAddress());
                ClientHandler handler = new ClientHandler(clientSocket);
                handler.start(); // Démarrer le thread du client
            }
        } catch (IOException e) {
            System.err.println("❌ Erreur serveur : " + e.getMessage());
        }
    }
}
