import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.Set;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class FXMLDocumentController implements Initializable {

    @FXML
    private Group bonhommePendu;

    @FXML
    private Line brasDroit;

    @FXML
    private Line brasGauche;

    @FXML
    private Line corps;

    @FXML
    private Line jambeDroite;

    @FXML
    private Line jambeGauche;

    @FXML
    private Circle tete;

    @FXML
    private TextField essaiTextField;

    @FXML
    private Label motRechercheLabel;

    @FXML
    private TextField nomJoueurTextField;

    @FXML
    private Label pointageLabel;

    @FXML
    private Button resetButton;

    @FXML
    private Label resultatLabel;

    @FXML
    private Button soumettreButton;

    @FXML
    private Button startButton;

    private int pointage;

    private List<String> mots;

    private String motCache;

    private int essaisNegatifs;

    private Set<String> poubelle; // Endroit pour inséré les lettres déjà devinées.

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Récupération la liste de mots dans le dossier approprié.
        try {
            File doc = new File(
                    "C:\\Users\\Mivania\\OneDrive - Campus Support\\11. 420-P02-ID\\ProjetFinalP02\\BP\\src\\mots.txt");
            Scanner sc = new Scanner(doc);
            mots = new ArrayList<>();

            while (sc.hasNextLine()) {
                String mot = sc.nextLine();
                mots.add(mot);
            }

            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Commencer la partie avec le bonhomme pendu non-apparent.
        effacerBP();

        pointage = 0;
        majPointage(pointage);

        poubelle = new HashSet<>();
    }

    @FXML
    void debutPartie(ActionEvent event) {

        // S'assure de commencer une nouvelle partie vierge.
        effacerBP();

        nomJoueurTextField.getText();
        nomJoueurTextField.setDisable(true); // Désactivation de la boîte après l'insertion d'un nom.
        startButton.setDisable(true); // Déactivation du bouton "débuter la partie".
        resetButton.setDisable(false); // Bouton "reset" lorsque la partie est en cours.

        motCache = selectMotHasard(); // Mot sélectionné au hasard.
        String lettresCaches = motInconnu(motCache); // Génère le mot caché.
        motRechercheLabel.setText(lettresCaches); // Affiche le mot caché.
        soumettreButton.setDisable(false); // Ce bouton sera réactivé à la fin d'un jeu.
        essaiTextField.setDisable(false); // Cette boîte sera réactivé à la fin d'un jeu.
        poubelle.clear(); // S'assurer que la poubelle est vide avant toute partie.
    }

    @FXML
    void finPartie(ActionEvent event) {

        nomJoueurTextField.setDisable(false); // Réactiver la boîte pour inseertion d'un nom.
        startButton.setDisable(false); // Réactivation du bouton "débuter la partie".
        resetButton.setDisable(true); // Désactivation du bouton reset lorsqu'il est enclanché.
        motRechercheLabel.setText(""); // Effacement du mot lorsque le bouton "reset" est enclanché.

        essaisNegatifs = 0;
        majPointage(pointage);
        effacerBP();

        // Effacement de tous messages à la fin de la partie.
        resultatLabel.setText("");

        // Vider la poubelle à la fin de chaque partie.
        poubelle.clear();
    }

    @FXML
    void soumettreEssai(ActionEvent event) {

        String devineLettre = essaiTextField.getText();

        // Vérification si la lettre a déjà été sélectionné.
        if (poubelle.contains(devineLettre)) {
            String warning = "Vous avez déjà sélectionné cette lettre !";
            resultatLabel.setText(warning);
            return; // Exit the method without further execution
        }

        // Ajout de la lettre utilisé dans la poubelle.
        poubelle.add(devineLettre);

        // Effacement du message lorsque l'on continue de jouer.
        resultatLabel.setText("");

        boolean essaisPositifs = verification(devineLettre);

        // Si la lettre deviné n'est pas dans le mot masqué, on cumule l'erreur et on
        // ajoute un membre au bonhomme pendu.
        if (!essaisPositifs) {
            essaisNegatifs++;
            affichageBP(essaisNegatifs);
        } else {
            // Remplacer les lettres masquées par la lettre devinée dans le mot caché.
            StringBuilder lettresCaches = new StringBuilder(motRechercheLabel.getText());

            for (int i = 0; i < motCache.length(); i++) {
                String lettreCachee = String.valueOf(motRechercheLabel.getText().charAt(i));
                String lettreMotCache = String.valueOf(motCache.charAt(i));
                if (lettreCachee.equals("*") && lettreMotCache.equals(devineLettre)) {
                    lettresCaches.setCharAt(i, devineLettre.charAt(0));
                }
            }
            motRechercheLabel.setText(lettresCaches.toString());
        }

        // La lettre devinée disparaîtra de la boîte lorsqu'une tentative est soumise.
        essaiTextField.clear();

        // vérification du statut de la partie.
        statutPartie();
    }

    // Méthode qui maintiendra le pointage à jour.
    public void majPointage(int pointage) {

        pointageLabel.setText(String.valueOf(pointage));
    }

    // Méthode qui fournira un mot au jeu de manière aléatoire.
    public String selectMotHasard() {
        Random hasard = new Random();
        int index = hasard.nextInt(mots.size());
        return mots.get(index);
    }

    // Affichage de la mise à jour des caractères correspondant au mot à deviner.
    public String motInconnu(String motCache) {
        StringBuilder lettresCaches = new StringBuilder();
        for (int i = 0; i < motCache.length(); i++) {
            lettresCaches.append("*");
        }
        return lettresCaches.toString();
    }

    // Vérifier si la lettre devinée fait partie du mot caché.
    public boolean verification(String devineLettre) {
        return motCache.contains(devineLettre);
    }

    // Controlleur d'image du bonhomme pendu.
    public void affichageBP(int essaisNegatifs) {

        // Affichage de chacun des membres lorsque le joueur fait une erreur.
        tete.setVisible(essaisNegatifs >= 1);
        corps.setVisible(essaisNegatifs >= 2);
        brasGauche.setVisible(essaisNegatifs >= 3);
        brasDroit.setVisible(essaisNegatifs >= 4);
        jambeGauche.setVisible(essaisNegatifs >= 5);
        jambeDroite.setVisible(essaisNegatifs >= 6);

    }

    // Méthode de vérification du statut de la partie.
    public void statutPartie() {
        boolean gameOver = false;

        if (essaisNegatifs >= 6) {
            // Affichage du message au joueur qu'il a perdu la partie.
            String message = "Vous avez perdu! \n Le mot recherché était " + motCache;
            resultatLabel.setText(message);
            gameOver = true;
        } else if (motRechercheLabel.getText().equals(motCache)) {
            // Affichage du message au joueur qu'il a gagné la partie
            String message = "Félicitations! Vous avez gagné!";
            resultatLabel.setText(message);
            gameOver = true;
            pointage++; // Incrémentation des points si la partie est gagnée et que le joueur continu à
                        // jouer.
            majPointage(pointage); // Mise à jour du score.
        }

        if (gameOver) {
            // Les boutons qui seront désactivés lorsque la partie sera terminée.
            essaiTextField.setDisable(true);
            soumettreButton.setDisable(true);
        }
    }

    // Méthode permettant "d'effacer" le bonhomme pendu.
    public void effacerBP() {

        tete.setVisible(false);
        corps.setVisible(false);
        brasGauche.setVisible(false);
        brasDroit.setVisible(false);
        jambeGauche.setVisible(false);
        jambeDroite.setVisible(false);
    }
}