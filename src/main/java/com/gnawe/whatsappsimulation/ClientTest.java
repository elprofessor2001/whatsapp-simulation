package com.gnawe.whatsappsimulation;

import java.io.IOException;
import java.net.Socket;

public class ClientTest {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 25000;

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
            System.out.println("✅ Connecté au serveur avec succès !");
            socket.close();
        } catch (IOException e) {
            System.out.println("❌ Erreur lors de la connexion au serveur : " + e.getMessage());
        }
    }
}
