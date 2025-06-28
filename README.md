# 💬 Projet Simulation WhatsApp Java

Ce projet est une simulation d’un groupe WhatsApp, développé en **Java** avec **JavaFX** et **JPA (Hibernate)** pour la gestion des membres et des messages.

Il permet à plusieurs utilisateurs de rejoindre un groupe, échanger des messages, voir les membres connectés, et bénéficier de fonctionnalités comme l’historique, l’exportation, les statistiques et plus.

## 👨‍🎓 Réalisé par

- **Nom :** Gnawé Parfait
- **Classe :** L3 IAGE
- **Année :** 2024-2025
- **Professeur :** Abdoulaye GAYE

---

## 🧠 Fonctionnalités principales

✅ Interface graphique moderne (JavaFX)  
✅ Liste dynamique des membres connectés  
✅ Affichage en bulles comme WhatsApp avec heure  
✅ Enregistrement des messages dans la base de données  
✅ Historique des 15 derniers messages à la connexion  
✅ Système de bannissement automatique (messages interdits)  
✅ Statistiques dynamiques (membres connectés + messages échangés)  
✅ Exportation des messages dans un fichier texte

---

## ▶️ Comment exécuter le projet

### 1. Démarrer le serveur

```bash
# Lancer Server.java dans IntelliJ ou tout IDE
```

### 2. Lancer un client

```bash
# Lancer ClientApplication.java
# Un client s’ouvre dans une interface graphique
# Répéter pour simuler plusieurs utilisateurs
```

---

## ⚙️ Configuration technique

- Java 17
- JavaFX 17
- Hibernate (Jakarta Persistence API)
- MySQL
- Maven

💾 Base de données : Configurée dans `persistence.xml`  
📦 Dépendances : Gérées via `pom.xml` (Maven)

---

## 📁 Arborescence simplifiée

```
src/
├── main/
│   ├── java/
│   │   └── com.gnawe.whatsappsimulation/
│   │       ├── ClientHandler.java
│   │       ├── ClientController.java
│   │       ├── ClientApplication.java
│   │       ├── Server.java
│   │       └── model/
│   │           ├── Membre.java
│   │           └── Message.java
│   └── resources/
│       ├── client.fxml
│       ├── style.css
│       └── META-INF/persistence.xml
```
