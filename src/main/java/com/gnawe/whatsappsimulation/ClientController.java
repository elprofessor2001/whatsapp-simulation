package com.gnawe.whatsappsimulation;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ClientController {

    @FXML private VBox chatArea;
    @FXML private ListView<String> membreListView;
    @FXML private TextField messageField;
    @FXML private Button sendButton;
    @FXML private Button quitButton;
    @FXML private Button exportButton;
    @FXML private Label statsMembres;
    @FXML private Label statsMessages;

    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private final ObservableList<String> membresConnectes = FXCollections.observableArrayList();
    private String pseudo;
    private int totalMessages = 0;
    private final StringBuilder historiqueMessages = new StringBuilder();

    public void initialize() {
        membreListView.setItems(membresConnectes);
        sendButton.setOnAction(e -> envoyerMessage());
        quitButton.setOnAction(e -> quitterGroupe());
        exportButton.setOnAction(e -> exporterMessagesVersFichier());
        demanderPseudoEtSeConnecter();
    }

    private void demanderPseudoEtSeConnecter() {
        Platform.runLater(() -> {
            TextInputDialog dialog = new TextInputDialog("User" + (int)(Math.random() * 1000));
            dialog.setTitle("Connexion");
            dialog.setHeaderText("Entrez votre pseudo pour rejoindre le groupe");
            dialog.setContentText("Pseudo :");

            dialog.showAndWait().ifPresentOrElse(nom -> {
                if (nom.trim().isEmpty()) {
                    ajouterMessage("❌ Pseudo vide. Connexion annulée.", true);
                    return;
                }
                pseudo = nom.trim();
                new Thread(this::seConnecter).start();
            }, () -> ajouterMessage("❌ Connexion annulée.", true));
        });
    }

    private void seConnecter() {
        try {
            socket = new Socket("localhost", 2001); // Port serveur
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

            writer.println(pseudo);
            String reponse = reader.readLine();
            if (reponse != null && reponse.startsWith("❌")) {
                ajouterMessage(reponse, true);
                socket.close();
                return;
            }

            ajouterMessage("✔ Connecté au serveur en tant que " + pseudo, true);

            String ligne;
            while ((ligne = reader.readLine()) != null) {
                if (ligne.startsWith("MEMBRES:")) {
                    String[] membres = ligne.substring(8).split(",");
                    Platform.runLater(() -> {
                        membresConnectes.setAll(membres);
                        statsMembres.setText("👥 Membres connectés : " + membresConnectes.size());
                    });
                } else {
                    boolean moi = ligne.startsWith(pseudo + " :");
                    ajouterMessage(ligne, moi);
                }
            }
        } catch (IOException e) {
            ajouterMessage("❌ Impossible de se connecter au serveur.", true);
        }
    }

    private void envoyerMessage() {
        String texte = messageField.getText();
        if (!texte.isEmpty() && writer != null) {
            writer.println(texte);
            messageField.clear();
        }
    }

    private void quitterGroupe() {
        try {
            if (writer != null) writer.println("QUIT");
            if (socket != null) socket.close();
            Platform.exit();
        } catch (IOException e) {
            ajouterMessage("❌ Erreur lors de la déconnexion.", true);
        }
    }

    private void ajouterMessage(String contenu, boolean moi) {
        Platform.runLater(() -> {
            totalMessages++;
            historiqueMessages.append(contenu).append("\n");

            HBox box = new HBox();
            box.setStyle(moi ? "-fx-alignment: center-right;" : "-fx-alignment: center-left;");

            VBox bubble = new VBox();
            bubble.getStyleClass().add(moi ? "bubble-green" : "bubble-gray");

            Text texte = new Text(contenu);
            Text heure = new Text(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
            heure.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");

            bubble.getChildren().addAll(texte, heure);
            box.getChildren().add(bubble);

            chatArea.getChildren().add(box);
            statsMessages.setText("💬 Messages échangés : " + totalMessages);
        });
    }

    private void exporterMessagesVersFichier() {
        Platform.runLater(() -> {
            File fichier = new File("messages_exportes_" + pseudo + ".txt");
            try (BufferedWriter writerFichier = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(fichier), StandardCharsets.UTF_8))) {
                writerFichier.write(historiqueMessages.toString());
                ajouterMessage("📤 Messages exportés dans : " + fichier.getAbsolutePath(), true);
            } catch (IOException e) {
                ajouterMessage("❌ Erreur lors de l'exportation.", true);
            }
        });
    }
}
