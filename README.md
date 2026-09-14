# Jeu de Dames

Implémentation en Java (Swing) du jeu de dames classique, avec interface graphique, prise obligatoire, prises multiples en chaîne, promotion en dame, et effets sonores.

## Aperçu

Le jeu se joue sur un plateau 8x8. Chaque case affiche l'état du plateau en temps réel : cases de déplacement possibles en surbrillance verte, pions pouvant capturer entourés en rouge, et gestion complète du tour par tour entre le joueur Blanc et le joueur Noir.

## Fonctionnalités

- Plateau 8x8 avec placement initial automatique des pions
- Sélection et déplacement des pièces à la souris
- Règle de la **prise obligatoire** : si une capture est possible, le joueur doit la jouer
- **Prises multiples en chaîne** (rafle) avec la même pièce
- **Promotion en dame** lorsqu'un pion atteint la dernière rangée
- Déplacements de la dame sur plusieurs cases (diagonales longues), avec capture à distance
- Détection de fin de partie (victoire, ou joueur bloqué) et affichage du résultat
- Effets sonores lors des captures et de la victoire

## Prérequis

- JDK 8 ou supérieur (le projet utilise uniquement `javax.swing`, `java.awt` et `javax.sound.sampled`, aucune dépendance externe)

## Lancer le jeu

Le code source et les ressources sonores se trouvent dans le dossier [`jeu de dame java/`](jeu%20de%20dame%20java/).

```bash
cd "jeu de dame java"
javac JeuDeDamesGUI.java
java JeuDeDamesGUI
```

> Le dossier `sounds/` doit rester à côté des fichiers `.class` générés, car les sons sont chargés depuis le classpath (`/sounds/capture.wav`, `/sounds/victoire.wav`).

Pas de JDK installé, ou une erreur au lancement ? Voir le guide détaillé : [`INSTALLATION.md`](INSTALLATION.md).

## Structure du projet

```
jeu-de-dames-java/
├── jeu de dame java/
│   ├── JeuDeDamesGUI.java   # Point d'entrée + toute la logique du jeu
│   └── sounds/
│       ├── capture.wav
│       └── victoire.wav
├── INSTALLATION.md          # Guide pas à pas (installation JDK, compilation, exécution)
└── README.md
```

## Architecture du code

Tout est regroupé dans `JeuDeDamesGUI.java` :

| Classe | Rôle |
|---|---|
| `Position` | Coordonnées (ligne, colonne) d'une case |
| `Piece` (abstraite), `Pion`, `Dame` | Règles de déplacement et de capture propres à chaque type de pièce |
| `Plateau` | État du jeu : plateau, joueur courant, règles de prise obligatoire, fin de partie |
| `PlateauDeJeu` | Composant Swing : rendu graphique et gestion des clics souris |
| `SoundManager` | Lecture des effets sonores |
| `JeuDeDamesGUI` | Fenêtre principale (`JFrame`) et lancement de l'application |

## Auteur

Projet réalisé par Niamat EL QASEMY
