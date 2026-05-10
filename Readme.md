# MCP Weather Tool 🌦️

Un serveur MCP (Model Context Protocol) permettant aux assistants IA d’accéder à des données météo en temps réel via Open météo API.

## ✨ Fonctionnalités

- 🌍 Récupération de la météo en temps réel
- 📍 Recherche météo par ville
- ⚡  Réponses rapides et structurées pour les agents IA
- 🔌 Compatible avec les clients MCP modernes
- 🛠️ Facile à intégrer dans Claude Desktop, Cursor, VS Code, etc.
- 📦 Architecture légère et extensible

## 🧠 À propos du protocole MCP

Le **Model Context Protocol (MCP)** est un standard permettant aux modèles IA d’interagir avec des outils externes de manière structurée et sécurisée.

---

# 🚀 Installation

## 1. Cloner le dépôt

```bash
git clone https://github.com/mohamedraguig/MCP-Weather-Tool.git
cd MCP-Weather-Tool
```

## 2. Installer les dépendances

Assurez-vous d’avoir installé :

- Java 21+
- Maven 3+

Puis exécutez :

```bash
mvn clean install
```

---

# ▶️ Lancer le serveur

## Mode développement

```bash
mvn spring-boot:run
```

## Ou lancer le JAR compilé

```bash
java -jar target/mcp-weather-tool.jar
```

---

# ⚙️ Configuration MCP

Exemple de configuration pour un client MCP :

```json
{
  "mcpServers": {
    "weather-tool": {
      "command": "java",
      "args": [
        "-jar",
        "target/mcp-weather-tool.jar"
      ]
    }
  }
}
```

---

# 🛠️ Outils disponibles

## `get_weather`

Retourne les informations météo pour une ville donnée.

### Paramètres

| Nom | Type | Description |
|---|---|---|
| `city` | `string` | Nom de la ville |

### Exemple

```json
{
  "city": "Paris"
}
```

### Réponse

```json
{
  "city": "Paris",
  "date": "2026-05-10",
  "lat": 48.8566,
  "lon": 2.3522,
  "daily": {
    "temperatureMax": 24,
    "temperatureMin": 15,
    "weatherCode": 3
  }
}
```

---

# 🧪 Exemple d’utilisation avec un agent IA

```text
Quelle est la météo actuelle à Lyon ?
```

L’agent utilisera automatiquement l’outil MCP pour récupérer les données météo en temps réel.

---

# 📁 Structure du projet

```bash
MCP-Weather-Tool/
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   └── test/
├── pom.xml
├── target/
└── README.md
```

---

# 🧰 Stack technique

- Java 21
- Maven
- Spring Boot
- MCP SDK
- API météo externe

---

# 🔗 Compatibilité

Ce projet peut être utilisé avec :

- ChatGPT
- Claude Desktop
- Cursor
- VS Code MCP
- Cline
- Autres clients compatibles MCP

---

# 📦 Commandes utiles

```bash
mvn clean install     # Installer les dépendances + build
mvn test              # Lancer les tests
mvn spring-boot:run   # Démarrer le serveur
```

---

# 🧑‍💻 Contribution

Les contributions sont les bienvenues !

1. Fork le projet
2. Crée une branche :

```bash
git checkout -b feature/ma-feature
```

3. Commit :

```bash
git commit -m "Ajout d'une nouvelle fonctionnalité"
```

4. Push :

```bash
git push origin feature/ma-feature
```

5. Ouvre une Pull Request

---

# 📄 Licence

Ce projet est distribué sous licence MIT.

---

# ⭐ Support

Si ce projet vous aide :

- laissez une ⭐ sur GitHub
- partagez le projet
- contribuez au développement

---

## 🔗 Repository

https://github.com/mohamedraguig/MCP-Weather-Tool