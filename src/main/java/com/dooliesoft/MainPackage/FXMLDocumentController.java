/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dooliesoft.MainPackage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author c158492
 */
public class FXMLDocumentController implements Initializable {

    private static final String DOSSIER_CONFIG_PROPERTIES = "c:\\temp\\";

    @FXML
    private Button buttonCopieDesFichiers;

    @FXML
    private Button buttonMiseAJourDatesFactures;

    @FXML
    private TextField nouvelleDateFacture;

    @FXML
    private TextField dossierDeSortie;

    @FXML
    private TextField dossierDOrigine;

    @FXML
    private TextArea listeFichiersATraiter;

    @FXML
    private CheckBox chekboxNouvelleDateFacture;

    @FXML
    private CheckBox chekboxConserverCheminDOrigine;

    @FXML
    private CheckBox chekboxConserverDossierDeSortie;

    @FXML
    private CheckBox chekboxAjouterDateDuJour;

    @FXML
    private Button buttonEnregistrerParametre;

    @FXML
    private Button buttonOuvrirDossierDeSortie;

    @FXML
    private Button buttonSelectionnerDossierDeSortie;

    @FXML
    private Button buttonSelectionnerDossierDOrigine;

    String sDossierDOrigine;
    String sDossierDeSortie;

    @FXML
    private void handleButtonOuvrirDossierDeSortie(ActionEvent event) throws IOException {
        Runtime rt = Runtime.getRuntime();
        Process pr = rt.exec("explorer " + dossierDeSortie.getText());
    }

    @FXML
    private void handleButtonSelectionnerDossierDeSortie(ActionEvent event) throws IOException {
        final DirectoryChooser dialog = new DirectoryChooser();
        final File directory = dialog.showDialog(buttonSelectionnerDossierDeSortie.getScene().getWindow());
        if (directory != null) {
            dossierDeSortie.setText(directory.getAbsolutePath());
        }
    }

    @FXML
    private void handleButtonSelectionDossierDOrigine(ActionEvent event) throws IOException {
        final DirectoryChooser dialog = new DirectoryChooser();
        final File directory = dialog.showDialog(buttonSelectionnerDossierDOrigine.getScene().getWindow());
        if (directory != null) {
            dossierDOrigine.setText(directory.getAbsolutePath());
        }
    }

    @FXML
    private void handleButtonCopieDesFichiers(ActionEvent event) throws IOException {

        if (listeFichiersATraiter.getText().equals("")) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Liste des fichiers à copier");
            alert.setHeaderText("La liste des fichiers à copier est vide. Il n'y a rien à copier !");
            alert.setContentText("");
            alert.showAndWait();
            return;
        }

        String[] listeFichiers = listeFichiersATraiter.getText().split("\n");

        sDossierDOrigine = dossierDOrigine.getText();
        sDossierDeSortie = dossierDeSortie.getText();

        File fDossierDOrigine = new File(sDossierDOrigine);
        File fDossierDeSortie = new File(sDossierDeSortie);

        if (!sDossierDOrigine.endsWith("\\")) {
            sDossierDOrigine = sDossierDOrigine.concat("\\");
        }

        if (!sDossierDeSortie.endsWith("\\")) {
            sDossierDeSortie = sDossierDeSortie.concat("\\");
        }

        if (!fDossierDOrigine.exists()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Dossier inexistant ...");
            alert.setHeaderText("Veuillez vérifier si le dossier d'origine existe !");
            alert.setContentText("");
            alert.showAndWait();
            return;
        }
        for (String filenameToCopy : listeFichiers) {
            FileUtils.copyFile(new File(sDossierDOrigine + filenameToCopy), new File(sDossierDeSortie + filenameToCopy));
        }

        buttonMiseAJourDatesFactures.setDisable(false);
    }

    @FXML
    private void handleButtonMiseAJourDatesFactures(ActionEvent event) throws IOException {
        System.out.println("You clicked on handleButtonMiseAJourDatesFactures!");
        String[] listeFichiers = listeFichiersATraiter.getText().split("\n");

        if (sDossierDOrigine == null || sDossierDeSortie == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Dossier d'origine ou dossier de sortie non initialisé ...");
            alert.setHeaderText("Avez-vous copié les fichiers ?");
            alert.setContentText("");
            alert.showAndWait();
            return;
        }

        if (nouvelleDateFacture.getText() == "") {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Nouvelle date de la facture...");
            alert.setHeaderText("Nouvelle date de la facture ne peut être vide !");
            alert.setContentText("");
            alert.showAndWait();
            return;
        }

        if (!nouvelleDateFacture.getText().matches("[0-9]{8}")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Nouvelle date de la facture...");
            alert.setHeaderText("Veuillez entrer une date valide !");
            alert.setContentText("");
            alert.showAndWait();
            return;
        }

        new File(sDossierDeSortie + "backup").mkdirs();

        for (String filenameToCopy : listeFichiers) {
            if (!filenameToCopy.endsWith(".xml")) {
                continue;
            }
            Path path = Paths.get(sDossierDeSortie + filenameToCopy);
            Path pathdotBak = Paths.get(sDossierDeSortie + "backup\\" + filenameToCopy + ".bak");
            Charset charset = StandardCharsets.UTF_8;

            String content = new String(Files.readAllBytes(path), charset);
            Files.write(pathdotBak, content.getBytes(charset));
            content = content.replaceAll("<InvoiceDate>.*<.*InvoiceDate>", "<InvoiceDate>" + nouvelleDateFacture.getText() + "</InvoiceDate>");
            Files.write(path, content.getBytes(charset));
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Mise à jour des Factures");
        alert.setHeaderText("La mise à jour des factures est effectuée");
        alert.setContentText("");
        alert.showAndWait();

    }

    @FXML
    private void handleCheckBoxConserverNouvelleDateFacture(ActionEvent event) throws IOException {
        if (chekboxNouvelleDateFacture.isSelected()) {
            nouvelleDateFacture.setDisable(true);
        } else {
            nouvelleDateFacture.setDisable(false);
        }
    }

    @FXML
    private void handleCheckBoxConserverCheminDOrigine(ActionEvent event) throws IOException {
        if (chekboxConserverCheminDOrigine.isSelected()) {
            dossierDOrigine.setDisable(true);
        } else {
            dossierDOrigine.setDisable(false);
        }
    }

    @FXML
    private void handleCheckBoxConserverDossierDeSortie(ActionEvent event) throws IOException {
        if (chekboxConserverDossierDeSortie.isSelected()) {
            dossierDeSortie.setDisable(true);
        } else {
            dossierDeSortie.setDisable(false);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        String sNouvelleDateFacture = "";
        sDossierDOrigine = "";
        try (InputStream input = new FileInputStream(DOSSIER_CONFIG_PROPERTIES + "config.properties")) {

            Properties prop = new Properties();
            prop.load(input);
            sNouvelleDateFacture = prop.getProperty("nouvelleDateFacture");
            sDossierDOrigine = prop.getProperty("dossierDOrigine");
            sDossierDeSortie = prop.getProperty("dossierDeSortie");

            if (sNouvelleDateFacture != null && !sNouvelleDateFacture.equals("")) {
                chekboxNouvelleDateFacture.setSelected(true);
                nouvelleDateFacture.setDisable(true);
            }
            if (sDossierDOrigine != null && !sDossierDOrigine.equals("")) {
                chekboxConserverCheminDOrigine.setSelected(true);
                dossierDOrigine.setDisable(true);
            }
            if (sDossierDeSortie != null && !sDossierDeSortie.equals("")) {
                chekboxConserverDossierDeSortie.setSelected(true);
                dossierDeSortie.setDisable(true);
            }

        } catch (IOException io) {
            io.printStackTrace();
        }

        nouvelleDateFacture.setText(sNouvelleDateFacture);
        dossierDOrigine.setText(sDossierDOrigine);
        dossierDeSortie.setText(sDossierDeSortie);
        buttonMiseAJourDatesFactures.setDisable(true);

    }

    @FXML
    public void handleButtonEnregistrerParametre() {
        try (OutputStream output = new FileOutputStream(DOSSIER_CONFIG_PROPERTIES + "config.properties")) {
            Properties prop = new Properties();

            if (chekboxNouvelleDateFacture.isSelected()) {
                prop.setProperty("nouvelleDateFacture", nouvelleDateFacture.getText());
            } else {
                prop.setProperty("nouvelleDateFacture", "");
            }
            if (chekboxConserverCheminDOrigine.isSelected()) {
                prop.setProperty("dossierDOrigine", dossierDOrigine.getText());
            } else {
                prop.setProperty("dossierDOrigine", "");
            }
            if (chekboxConserverDossierDeSortie.isSelected()) {
                prop.setProperty("dossierDeSortie", dossierDeSortie.getText());
            } else {
                prop.setProperty("dossierDeSortie", "");
            }

            prop.store(output, null);
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    @FXML
    public void handleMenuClose() {
        handleButtonEnregistrerParametre();
        Platform.exit();
    }
}
