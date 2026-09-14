# Guide d'installation et d'exécution

Ce guide explique, étape par étape, comment installer ce qu'il faut et lancer le jeu, même si tu n'as jamais utilisé Java.

## 1. Installer le JDK (Java Development Kit)

Le projet a besoin du JDK (pas seulement du JRE) car on doit **compiler** le code avant de le lancer.

- **Windows** : télécharge le JDK depuis [adoptium.net](https://adoptium.net/) (choisis la version *LTS* la plus récente, ex. JDK 21), lance l'installeur `.msi` et laisse les options par défaut (elles ajoutent Java au `PATH` automatiquement).
- **macOS** : `brew install openjdk` (avec [Homebrew](https://brew.sh/)), ou installeur depuis adoptium.net.
- **Linux (Debian/Ubuntu)** : `sudo apt install default-jdk`

### Vérifier l'installation

Ouvre un terminal (PowerShell sur Windows, ou le terminal intégré de VS Code) et tape :

```bash
java -version
javac -version
```

Si les deux commandes affichent un numéro de version (ex. `21.0.x`), c'est bon. Si tu as une erreur du type "commande introuvable", redémarre le terminal (ou l'ordinateur) après l'installation.

## 2. Récupérer le projet

```bash
git clone https://github.com/<ton-pseudo>/jeu-de-dames-java.git
cd jeu-de-dames-java
```

(Ou simplement télécharger le ZIP depuis GitHub et l'extraire.)

## 3. Compiler le code

```bash
cd "jeu de dame java"
javac JeuDeDamesGUI.java
```

Cette commande génère des fichiers `.class` à côté du fichier source. C'est normal, ils ne sont pas suivis par Git (voir `.gitignore`).

## 4. Lancer le jeu

Toujours depuis le dossier `jeu de dame java` :

```bash
java JeuDeDamesGUI
```

Une fenêtre s'ouvre avec le plateau de dames. Les pièces blanches jouent en premier.

## Règles rapides

- Clique sur une pièce pour la sélectionner : les cases où elle peut aller s'affichent en vert.
- Si une capture est possible, elle est **obligatoire** (les pions concernés sont entourés en rouge).
- Après une capture, si la même pièce peut reprendre, la prise multiple continue automatiquement.
- Un pion qui atteint la dernière rangée devient une dame (marquée en jaune).

## Problèmes fréquents

**"Erreur son" dans la console au lancement**
Le programme cherche les fichiers `.wav` via le classpath (`/sounds/...`). Assure-toi de lancer `java JeuDeDamesGUI` depuis l'intérieur du dossier `jeu de dame java` (là où se trouve aussi le dossier `sounds/`), pas depuis un autre dossier.

**`javac` ou `java` non reconnu**
Le JDK n'est pas dans le `PATH`. Réinstalle-le en laissant l'option "Add to PATH" cochée, ou redémarre ton terminal après l'installation.

**La fenêtre ne s'affiche pas / rien ne se passe**
Vérifie qu'aucune erreur n'apparaît dans le terminal. Le jeu utilise Swing, qui nécessite un environnement graphique (impossible de le lancer sur un serveur sans interface graphique).
