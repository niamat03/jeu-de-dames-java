import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;

public class JeuDeDamesGUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Jeu de Dames");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(650, 700);
            frame.setMinimumSize(new Dimension(650, 700));
            
            PlateauDeJeu plateau = new PlateauDeJeu();
            frame.getContentPane().add(plateau, BorderLayout.CENTER);
            
            JPanel infoPanel = new JPanel(new BorderLayout());
            JLabel statusLabel = new JLabel("Tour du joueur: Blanc", JLabel.CENTER);
            statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
            JLabel messageLabel = new JLabel(" ", JLabel.CENTER);
            messageLabel.setForeground(Color.RED);
            
            infoPanel.add(statusLabel, BorderLayout.NORTH);
            infoPanel.add(messageLabel, BorderLayout.SOUTH);
            frame.add(infoPanel, BorderLayout.SOUTH);
            
            plateau.setStatusLabel(statusLabel);
            plateau.setMessageLabel(messageLabel);
            
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

class SoundManager {
    private static void play(String filename) {
        try {
            // Utilisation de getResourceAsStream pour charger depuis le classpath
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(
                SoundManager.class.getResourceAsStream("/sounds/" + filename));
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (Exception e) {
            System.err.println("Erreur son: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void playCapture() { play("capture.wav"); }
    public static void playVictory() { play("victoire.wav"); }
}

enum TypePiece { PION, DAME }
enum Couleur { BLANC, NOIR }

class Position {
    private final int ligne;
    private final int colonne;
    
    public Position(int ligne, int colonne) {
        this.ligne = ligne;
        this.colonne = colonne;
    }
    
    public int getLigne() { return ligne; }
    public int getColonne() { return colonne; }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Position)) return false;
        Position p = (Position)obj;
        return ligne == p.ligne && colonne == p.colonne;
    }
    
    @Override
    public String toString() {
        return "(" + ligne + "," + colonne + ")";
    }
}

abstract class Piece {
    protected Couleur couleur;
    protected TypePiece type;
    protected Position position;
    
    public Piece(Couleur couleur, Position position) {
        this.couleur = couleur;
        this.position = position;
    }
    
    public abstract List<Position> getDeplacementsPossibles(Plateau plateau);
    public abstract List<Position> getPrisesPossibles(Plateau plateau);
    
    public Couleur getCouleur() { return couleur; }
    public TypePiece getType() { return type; }
    public Position getPosition() { return position; }
    public void setPosition(Position p) { position = p; }
}

class Pion extends Piece {
    public Pion(Couleur couleur, Position position) {
        super(couleur, position);
        this.type = TypePiece.PION;
    }
    
    @Override
    public List<Position> getDeplacementsPossibles(Plateau plateau) {
        List<Position> deplacements = new ArrayList<>();
        int direction = (couleur == Couleur.BLANC) ? -1 : 1;
        
        Position[] positions = {
            new Position(position.getLigne() + direction, position.getColonne() - 1),
            new Position(position.getLigne() + direction, position.getColonne() + 1)
        };
        
        for (Position pos : positions) {
            if (plateau.estPositionValide(pos) && plateau.getPiece(pos) == null) {
                deplacements.add(pos);
            }
        }
        return deplacements;
    }
    
    @Override
    public List<Position> getPrisesPossibles(Plateau plateau) {
        List<Position> prises = new ArrayList<>();
        int direction = (couleur == Couleur.BLANC) ? -1 : 1;
        
        for (int i = -1; i <= 1; i += 2) {
            Position caseAdverse = new Position(position.getLigne() + direction, position.getColonne() + i);
            Position caseVide = new Position(position.getLigne() + 2*direction, position.getColonne() + 2*i);
            
            if (plateau.estPositionValide(caseAdverse) && plateau.estPositionValide(caseVide)) {
                Piece pieceAdverse = plateau.getPiece(caseAdverse);
                if (pieceAdverse != null && pieceAdverse.getCouleur() != couleur 
                    && plateau.getPiece(caseVide) == null) {
                    prises.add(caseVide);
                }
            }
        }
        return prises;
    }
}

class Dame extends Piece {
    public Dame(Couleur couleur, Position position) {
        super(couleur, position);
        this.type = TypePiece.DAME;
    }
    
    @Override
    public List<Position> getDeplacementsPossibles(Plateau plateau) {
        List<Position> deplacements = new ArrayList<>();
        
        for (int dirLigne = -1; dirLigne <= 1; dirLigne += 2) {
            for (int dirCol = -1; dirCol <= 1; dirCol += 2) {
                int x = position.getLigne() + dirLigne;
                int y = position.getColonne() + dirCol;
                
                while (x >= 0 && x < 8 && y >= 0 && y < 8) {
                    Position pos = new Position(x, y);
                    if (plateau.getPiece(pos) == null) {
                        deplacements.add(pos);
                    } else {
                        break;
                    }
                    x += dirLigne;
                    y += dirCol;
                }
            }
        }
        return deplacements;
    }
    
    @Override
    public List<Position> getPrisesPossibles(Plateau plateau) {
        List<Position> prises = new ArrayList<>();
        
        for (int dirLigne = -1; dirLigne <= 1; dirLigne += 2) {
            for (int dirCol = -1; dirCol <= 1; dirCol += 2) {
                int x = position.getLigne() + dirLigne;
                int y = position.getColonne() + dirCol;
                Position positionAdverse = null;
                
                while (x >= 0 && x < 8 && y >= 0 && y < 8) {
                    Position pos = new Position(x, y);
                    Piece piece = plateau.getPiece(pos);
                    
                    if (piece != null) {
                        if (piece.getCouleur() == couleur) {
                            break; // Pièce alliée, on s'arrête
                        } else {
                            if (positionAdverse == null) {
                                positionAdverse = pos; // Première pièce adverse trouvée
                            } else {
                                break; // Deuxième pièce adverse, on s'arrête
                            }
                        }
                    }
                    
                    x += dirLigne;
                    y += dirCol;
                }
                
                if (positionAdverse != null) {
                    int deltaX = positionAdverse.getLigne() - position.getLigne();
                    int deltaY = positionAdverse.getColonne() - position.getColonne();
                    int distance = Math.max(Math.abs(deltaX), Math.abs(deltaY));
                    
                    Position posApres = new Position(
                        positionAdverse.getLigne() + deltaX/distance,
                        positionAdverse.getColonne() + deltaY/distance
                    );
                    
                    while (plateau.estPositionValide(posApres) && plateau.getPiece(posApres) == null) {
                        prises.add(posApres);
                        posApres = new Position(
                            posApres.getLigne() + deltaX/distance,
                            posApres.getColonne() + deltaY/distance
                        );
                    }
                }
            }
        }
        return prises;
    }
}

class Plateau {
    private final Piece[][] cases;
    private Couleur joueurCourant;
    private Position pieceSelectionnee;
    private boolean priseMultipleEnCours;
    
    public Plateau() {
        cases = new Piece[8][8];
        joueurCourant = Couleur.BLANC;
        initialiserPlateau();
    }
    
    private void initialiserPlateau() {
        // Pions noirs
        for (int i = 0; i < 3; i++) {
            for (int j = (i % 2 == 0) ? 1 : 0; j < 8; j += 2) {
                cases[i][j] = new Pion(Couleur.NOIR, new Position(i, j));
            }
        }
        
        // Pions blancs
        for (int i = 5; i < 8; i++) {
            for (int j = (i % 2 == 0) ? 1 : 0; j < 8; j += 2) {
                cases[i][j] = new Pion(Couleur.BLANC, new Position(i, j));
            }
        }
    }
    
    public boolean estPositionValide(Position pos) {
        return pos.getLigne() >= 0 && pos.getLigne() < 8 && 
               pos.getColonne() >= 0 && pos.getColonne() < 8;
    }
    
    public Piece getPiece(Position pos) {
        return cases[pos.getLigne()][pos.getColonne()];
    }
    
    public boolean doitForcerPrise() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Position pos = new Position(i, j);
                Piece piece = getPiece(pos);
                if (piece != null && piece.getCouleur() == joueurCourant) {
                    if (!piece.getPrisesPossibles(this).isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public List<Position> getPionsQuiPeuventPrendre() {
        List<Position> positions = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Position pos = new Position(i, j);
                Piece piece = getPiece(pos);
                if (piece != null && piece.getCouleur() == joueurCourant && 
                    !piece.getPrisesPossibles(this).isEmpty()) {
                    positions.add(pos);
                }
            }
        }
        return positions;
    }
    
    public void selectionnerPiece(Position pos) {
        Piece piece = getPiece(pos);
        if (piece != null && piece.getCouleur() == joueurCourant) {
            // Désélectionner si on clique deux fois sur la même pièce
            if (pos.equals(pieceSelectionnee)) {
                pieceSelectionnee = null;
                return;
            }
            
            // Vérifier si la pièce peut bouger
            boolean peutBouger = !piece.getDeplacementsPossibles(this).isEmpty() || 
                               !piece.getPrisesPossibles(this).isEmpty();
            
            if (doitForcerPrise()) {
                if (!piece.getPrisesPossibles(this).isEmpty()) {
                    pieceSelectionnee = pos;
                    priseMultipleEnCours = false;
                }
            } else if (peutBouger) {
                pieceSelectionnee = pos;
                priseMultipleEnCours = false;
            }
        }
    }
    
    public boolean deplacerPiece(Position arrivee) {
        if (pieceSelectionnee == null) return false;
        
        Piece piece = getPiece(pieceSelectionnee);
        List<Position> prises = piece.getPrisesPossibles(this);
        List<Position> deplacements = new ArrayList<>();
        
        if (doitForcerPrise() || priseMultipleEnCours) {
            if (prises.isEmpty()) {
                return false;
            }
            deplacements = prises;
        } else {
            deplacements = piece.getDeplacementsPossibles(this);
        }
        
        if (deplacements.contains(arrivee)) {
            // Déplacement normal
            cases[arrivee.getLigne()][arrivee.getColonne()] = piece;
            cases[pieceSelectionnee.getLigne()][pieceSelectionnee.getColonne()] = null;
            piece.setPosition(arrivee);
            
            // Gestion des prises
            if (Math.abs(pieceSelectionnee.getLigne() - arrivee.getLigne()) > 1) {
                SoundManager.playCapture();
                // Calcul des pièces prises
                int dirLigne = Integer.compare(arrivee.getLigne(), pieceSelectionnee.getLigne());
                int dirCol = Integer.compare(arrivee.getColonne(), pieceSelectionnee.getColonne());
                
                Position pos = new Position(pieceSelectionnee.getLigne(), pieceSelectionnee.getColonne());
                
                while (!pos.equals(arrivee)) {
                    pos = new Position(pos.getLigne() + dirLigne, pos.getColonne() + dirCol);
                    Piece piecePrise = getPiece(pos);
                    
                    if (piecePrise != null && piecePrise.getCouleur() != piece.getCouleur()) {
                        cases[pos.getLigne()][pos.getColonne()] = null;
                    }
                }
            }
            
            // Promotion en dame
            if (piece.getType() == TypePiece.PION && 
                ((piece.getCouleur() == Couleur.BLANC && arrivee.getLigne() == 0) ||
                 (piece.getCouleur() == Couleur.NOIR && arrivee.getLigne() == 7))) {
                cases[arrivee.getLigne()][arrivee.getColonne()] = new Dame(piece.getCouleur(), arrivee);
            }
            
            // Vérifier si une autre prise est possible avec la même pièce
            Piece nouvellePiece = getPiece(arrivee);
            List<Position> nouvellesPrises = nouvellePiece.getPrisesPossibles(this);
            
            if (!nouvellesPrises.isEmpty() && Math.abs(pieceSelectionnee.getLigne() - arrivee.getLigne()) > 1) {
                // Garder le même joueur et sélectionner la pièce pour prise multiple
                priseMultipleEnCours = true;
                pieceSelectionnee = arrivee;
            } else {
                joueurCourant = (joueurCourant == Couleur.BLANC) ? Couleur.NOIR : Couleur.BLANC;
                pieceSelectionnee = null;
                priseMultipleEnCours = false;
            }
            
            return true;
        }
        return false;
    }
    public List<Position> getDeplacementsPossiblesPourPieceSelectionnee() {
        if (pieceSelectionnee == null) return new ArrayList<>();
        Piece piece = getPiece(pieceSelectionnee);
        if (piece == null) return new ArrayList<>();
    
        // Priorité aux prises si elles existent
        List<Position> prises = piece.getPrisesPossibles(this);
        if (!prises.isEmpty() || priseMultipleEnCours) {
            return prises;
        } else {
            return piece.getDeplacementsPossibles(this);
        }
    }
    
    public Position getPieceSelectionnee() { return pieceSelectionnee; }
    public Couleur getJoueurCourant() { return joueurCourant; }
    public boolean isPriseMultipleEnCours() { return priseMultipleEnCours; }
    public boolean estPartieTerminee() {
        int blancs = 0, noirs = 0;
    
        // Compter les pièces restantes
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = cases[i][j];
                if (piece != null) {
                    if (piece.getCouleur() == Couleur.BLANC) blancs++;
                    else noirs++;
                }
            }
        }
    
        // Vérifier s'il reste des pièces
        if (blancs == 0 || noirs == 0) return true;
    
        // Vérifier si un joueur peut encore bouger
        boolean blancPeutBouger = false, noirPeutBouger = false;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = cases[i][j];
                if (piece != null) {
                    boolean peutBouger = !piece.getDeplacementsPossibles(this).isEmpty() || 
                                         !piece.getPrisesPossibles(this).isEmpty();
                    if (piece.getCouleur() == Couleur.BLANC) blancPeutBouger |= peutBouger;
                    else noirPeutBouger |= peutBouger;
                }
            }
        }
    
        return !blancPeutBouger || !noirPeutBouger;
    }

    public String getResultatPartie() {
        int blancs = 0, noirs = 0;
    
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = cases[i][j];
                if (piece != null) {
                    if (piece.getCouleur() == Couleur.BLANC) blancs++;
                    else noirs++;
                }
            }
        }
    
        if (blancs == 0 && noirs == 0) return "Match nul - Aucun gagnant";
        if (blancs == 0) return "Noir gagne !";
        if (noirs == 0) return "Blanc gagne !";
    
        // Vérifier les mouvements possibles
        boolean blancPeutBouger = false, noirPeutBouger = false;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = cases[i][j];
                if (piece != null) {
                    boolean peutBouger = !piece.getDeplacementsPossibles(this).isEmpty() || 
                                         !piece.getPrisesPossibles(this).isEmpty();
                    if (piece.getCouleur() == Couleur.BLANC) blancPeutBouger |= peutBouger;
                    else noirPeutBouger |= peutBouger;
                }
            }
        }
    
        if (!blancPeutBouger && !noirPeutBouger) return "Match nul - Aucun joueur ne peut bouger";
        if (!blancPeutBouger) return "Noir gagne - Blanc ne peut plus bouger";
        if (!noirPeutBouger) return "Blanc gagne - Noir ne peut plus bouger";
    
        return "Partie en cours";
    }

    
}

class PlateauDeJeu extends JPanel {
    private Plateau plateau;
    private static final int TAILLE_CASE = 80;
    private JLabel statusLabel;
    private JLabel messageLabel;
    
    public PlateauDeJeu() {
        plateau = new Plateau();
        setPreferredSize(new Dimension(8 * TAILLE_CASE, 8 * TAILLE_CASE));
        
        addMouseListener(new MouseAdapter() {
            @Override
            
        public void mouseClicked(MouseEvent e) {
            // Vérifier si la partie est terminée
            if (plateau.estPartieTerminee()) {
                messageLabel.setText("Partie terminée ! " + plateau.getResultatPartie());
                SoundManager.playVictory();
                return;
            }

            int colonne = e.getX() / TAILLE_CASE;
            int ligne = e.getY() / TAILLE_CASE;
    
            // Vérifier que le clic est dans le plateau
            if (ligne < 0 || ligne >= 8 || colonne < 0 || colonne >= 8) return;
    
            Position pos = new Position(ligne, colonne);
            Piece pieceCliquee = plateau.getPiece(pos);
    
            // Gestion des prises multiples en cours
            if (plateau.isPriseMultipleEnCours()) {
                if (plateau.deplacerPiece(pos)) {
                    messageLabel.setText("");
                    if (plateau.estPartieTerminee()) {
                        messageLabel.setText("Partie terminée ! " + plateau.getResultatPartie());
                    }
                } else {
                    messageLabel.setText("Vous devez compléter la prise multiple!");
                }
                updateStatus();
                repaint();
                return;
            }
    
            // Si une pièce est déjà sélectionnée
            if (plateau.getPieceSelectionnee() != null) {
                // Si on reclique sur la même pièce, on la désélectionne
                if (pos.equals(plateau.getPieceSelectionnee())) {
                    plateau.selectionnerPiece(null);
                    messageLabel.setText("");
                } 
                // Si on clique sur une autre pièce du même joueur
                else if (pieceCliquee != null && pieceCliquee.getCouleur() == plateau.getJoueurCourant()) {
                    // Vérifier si la nouvelle pièce peut bouger
                    if (!pieceCliquee.getDeplacementsPossibles(plateau).isEmpty() || 
                        !pieceCliquee.getPrisesPossibles(plateau).isEmpty()) {
                        plateau.selectionnerPiece(pos);
                        messageLabel.setText("");
                    } else {
                        messageLabel.setText("Ce pion ne peut pas bouger - choisissez-en un autre");
                    }
                } 
                // Tentative de déplacement
                else {
                    if (plateau.deplacerPiece(pos)) {
                        messageLabel.setText("");
                        if (plateau.estPartieTerminee()) {
                            messageLabel.setText("Partie terminée ! " + plateau.getResultatPartie());
                            SoundManager.playVictory();
                        }
                    } else {
                        if (plateau.doitForcerPrise()) {
                            messageLabel.setText("Vous devez effectuer une prise si possible!");
                        } else {
                            messageLabel.setText("Déplacement invalide");
                        }
                    }
                }
            } 
            // Si aucune pièce n'est sélectionnée
            else {
                if (pieceCliquee != null && pieceCliquee.getCouleur() == plateau.getJoueurCourant()) {
                    // Vérifier que la pièce peut bouger avant de la sélectionner
                    if (!pieceCliquee.getDeplacementsPossibles(plateau).isEmpty() || 
                        !pieceCliquee.getPrisesPossibles(plateau).isEmpty()) {
                        plateau.selectionnerPiece(pos);
                        messageLabel.setText("");
                    } else {
                        messageLabel.setText("Ce pion ne peut pas bouger - choisissez-en un autre");
                    }
                } else if (plateau.doitForcerPrise()) {
                    messageLabel.setText("Vous devez sélectionner une pièce qui peut prendre!");
                }
            }
    
            updateStatus();
            repaint();
        }
    });
}
    
    public void setStatusLabel(JLabel label) {
        this.statusLabel = label;
        updateStatus();
    }
    
    public void setMessageLabel(JLabel label) {
        this.messageLabel = label;
    }
    
    private void updateStatus() {
        if (plateau.estPartieTerminee()) {
            statusLabel.setText("Partie terminée - " + plateau.getResultatPartie());
        } else {
            statusLabel.setText("Tour du joueur: " + 
                (plateau.getJoueurCourant() == Couleur.BLANC ? "Blanc" : "Noir") +
                (plateau.isPriseMultipleEnCours() ? " (Prise multiple en cours)" : ""));
        }
    }
    
    @Override
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
    
        // 1. Dessiner le damier
        for (int ligne = 0; ligne < 8; ligne++) {
            for (int colonne = 0; colonne < 8; colonne++) {
                if ((ligne + colonne) % 2 == 0) {
                    g.setColor(new Color(210, 180, 140)); // Beige
                } else {
                    g.setColor(new Color(139, 69, 19)); // Marron
                }
                g.fillRect(colonne * TAILLE_CASE, ligne * TAILLE_CASE, TAILLE_CASE, TAILLE_CASE);
            }
        }
        // 2. Dessiner les bordures rouges pour les pions qui doivent capturer (si nécessaire)
        if (plateau.doitForcerPrise()) {
            g2d.setStroke(new BasicStroke(4)); // Épaisseur de la bordure
            g2d.setColor(Color.RED);
        
            for (Position pos : plateau.getPionsQuiPeuventPrendre()) {
                int margin = 8; // Marge pour que la bordure ne touche pas les bords
                int x = pos.getColonne() * TAILLE_CASE + margin/2;
                int y = pos.getLigne() * TAILLE_CASE + margin/2;
                int width = TAILLE_CASE - margin;
            
                // Dessiner un rectangle arrondi
                g2d.drawRoundRect(x, y, width, width, 20, 20);
            }
        }
    
        // 2. Dessiner les cases de déplacement possibles (en vert)
        if (plateau.getPieceSelectionnee() != null) {
            List<Position> deplacements = plateau.getDeplacementsPossiblesPourPieceSelectionnee();
            for (Position pos : deplacements) {
                g.setColor(new Color(0, 255, 0, 80)); // Vert transparent
                g.fillRect(pos.getColonne() * TAILLE_CASE, 
                          pos.getLigne() * TAILLE_CASE, 
                          TAILLE_CASE, TAILLE_CASE);
            }
        }
    
        // 3. Dessiner les pièces
        for (int ligne = 0; ligne < 8; ligne++) {
            for (int colonne = 0; colonne < 8; colonne++) {
                Position pos = new Position(ligne, colonne);
                Piece piece = plateau.getPiece(pos);
            
                if (piece != null) {
                    int x = colonne * TAILLE_CASE + TAILLE_CASE/2;
                    int y = ligne * TAILLE_CASE + TAILLE_CASE/2;
                    int rayon = TAILLE_CASE/2 - 5;
                
                    g.setColor(piece.getCouleur() == Couleur.BLANC ? Color.WHITE : Color.BLACK);
                    g.fillOval(x - rayon, y - rayon, rayon * 2, rayon * 2);
                
                    if (piece.getType() == TypePiece.DAME) {
                        g.setColor(Color.YELLOW);
                        g.fillOval(x - rayon/2, y - rayon/2, rayon, rayon);
                    }
                }
            }
        }
    
        // 4. Dessiner la sélection (en dernier pour être au-dessus)
        if (plateau.getPieceSelectionnee() != null) {
            Position pos = plateau.getPieceSelectionnee();
            g.setColor(new Color(0, 255, 0, 100));
            g.fillRect(pos.getColonne() * TAILLE_CASE, 
                      pos.getLigne() * TAILLE_CASE, 
                      TAILLE_CASE, TAILLE_CASE);
        }
    }
}