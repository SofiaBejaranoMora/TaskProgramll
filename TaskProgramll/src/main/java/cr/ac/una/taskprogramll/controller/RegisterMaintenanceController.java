/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.taskprogramll.controller;

import cr.ac.una.taskprogramll.model.FileManager;
import cr.ac.una.taskprogramll.model.Sport;
import cr.ac.una.taskprogramll.model.Team;
import cr.ac.una.taskprogramll.util.AppContext;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXRadioButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.utils.SwingFXUtils;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class RegisterMaintenanceController implements Initializable {

    private File selectedImage = null;
    private Sport newSport = null;
    private Team newTeam = null;
    private List<Sport> sportList = new ArrayList<>();
    private List<Team> teamList = new ArrayList<>();
    private FileManager fileManeger = new FileManager();
    private File file;
    private Image image = null;
    private String ruteImage = System.getProperty("user.dir") + "/src/main/resources/cr/ac/una/taskprogramll/resources/";
    private Boolean isSport = false;

    @FXML
    private MFXTextField txtName;

    @FXML
    private MFXButton btnAccept;

    @FXML
    private MFXButton btnCancel;

    @FXML
    private MFXButton btnDelete;

    @FXML
    private MFXButton btnPhoto;

    @FXML
    private MFXButton btnSelectImage;

    @FXML
    private MFXComboBox<String> cmbSport;

    @FXML
    private ToggleGroup grpFiltro;

    @FXML
    private Label labHead;

    @FXML
    private ImageView mgvImage;

    @FXML
    private MFXRadioButton rbtSport;

    @FXML
    private MFXRadioButton rbtTeam;

    @FXML
    void OnActionAccept(ActionEvent event) {
        String name = txtName.getText().trim();
        if ((!name.isEmpty()) && (mgvImage.getImage() != null)) {
            name = txtName.getText();
            if (isSport) {
                if (!CheckedExistsSport(name)) {
                    SportRegistration(name);
                }
            } else if (cmbSport.getValue() != null) {
                Sport type = returnCmbSportValue();
                if (!CheckedExistsTeam(name, type)) {
                    TeamRegistration(name, type);
                }
            }
        }
    }

    @FXML
    void OnActionBtnCancel(ActionEvent event) {
        ClearPanel(); //Tambien tengo que poner el hasTeamList en falso cuando cierre
    }

    @FXML
    void OnActionBtnDelete(ActionEvent event) {

    }

    @FXML
    void OnActionBtnSelectImage(ActionEvent event) {
        SelectImage();
    }

    @FXML
    void OnActionBtnPhoto(ActionEvent event) {
        BufferedImage bufferedImage = (BufferedImage) AppContext.getInstance().get("bufferedImageTeam");
        if (bufferedImage != null) {
            mgvImage.setImage(SwingFXUtils.toFXImage(bufferedImage, null));
        }
    }

    @FXML
    void OnActionCmbSport(ActionEvent event) {

    }

    @FXML
    void OnActionRbtSport(ActionEvent event) {
        EnabledTeam(false);
    }

    @FXML
    void OnActionRbtTeam(ActionEvent event) {
        EnabledTeam(true);
    }

    public void SportRegistration(String name) {
        newSport = new Sport(name);
        sportList.add(newSport);
        fileManeger.serialization(sportList, "Sport");
        image = mgvImage.getImage();// revisar y quitar si es necesario
        RelocateImage(name);
        StartCmbSportType();
        ClearPanel();
    }

    public void TeamRegistration(String name, Sport type) {
        newTeam = new Team(name, type);
        teamList.add(newTeam);
        fileManeger.serialization(teamList, "Team");
        image = mgvImage.getImage();
        SaveImage(name);
        ClearPanel();
    }

    public void EnabledTeam(Boolean enabled) {
        cmbSport.setDisable(!enabled);
        cmbSport.setVisible(enabled);
        cmbSport.setManaged(enabled);
        btnPhoto.setDisable(!enabled);
        btnPhoto.setManaged(enabled);
        btnPhoto.setVisible(enabled);
    }

    public void EnabledMaintenance(Boolean enabled) {
        btnDelete.setDisable(!enabled);
        btnDelete.setManaged(enabled);
        btnDelete.setVisible(enabled);
    }

    public void ClearPanel() {
        // falta indicar el radion button con el que se inicia
        //tambien siempre iniciar en combox
        txtName.clear();
        cmbSport.setValue(null);
        cmbSport.getSelectionModel().clearSelection();
        mgvImage.setImage(null);
        newSport = null;
        newTeam = null;
        image = null;
    }

    public void InitialConditionsPanel() {
        file = new File("Sport.txt");
        if ((file.exists()) && (file.length() > 0)) {
            sportList = fileManeger.deserialization("Sport", Sport.class);
            StartCmbSportType();
        }
        file = new File("Team.txt");
        if ((file.exists()) && (file.length() > 0)) {
            teamList = fileManeger.deserialization("Team", Team.class);
        }
    }

    public Boolean CheckedExistsTeam(String name, Sport sport) {
        name = name.toUpperCase().replaceAll("\\s+", "");
        String nameCurrentTeam;
        for (Team currentTeam : teamList) {
            nameCurrentTeam = currentTeam.getName().toUpperCase().replaceAll("\\s+", "");
            if ((currentTeam.getSportType().getName().equals(sport.getName())) && (nameCurrentTeam.equals(name))) {
                return true;
            }
        }
        return false;
    }

    public Boolean CheckedExistsSport(String name) {
        for (Sport currentTeam : sportList) {
            if (currentTeam.getName().toUpperCase().replaceAll("\\s+", "").equals(name.toUpperCase().replaceAll("\\s+", ""))) {
                return true;
            }
        }
        return false;
    }

    public void SelectImage() {
        JFileChooser jFileChooser = new JFileChooser();
        FileNameExtensionFilter imageFilter = new FileNameExtensionFilter("PNG", "png");
        jFileChooser.setFileFilter(imageFilter);
        int responseData = jFileChooser.showOpenDialog(null);
        if (responseData == JFileChooser.APPROVE_OPTION) {
            selectedImage = jFileChooser.getSelectedFile();
            image = new Image(selectedImage.toURI().toString());
            mgvImage.setImage(image);
        }
    }

    public void RelocateImage(String name) {
        String newRute = ruteImage + name + ".png";
        try {
            Path originalSourceImage = selectedImage.toPath();
            Path newDestinationImage = new File(newRute).toPath();
            Files.copy(originalSourceImage, newDestinationImage, StandardCopyOption.REPLACE_EXISTING);
            image = new Image("file:" + newRute);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void SaveImage(String name) {
        String newRute = ruteImage + name + ".png";
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(mgvImage.getImage(), null), "PNG", new File(newRute));
        } catch (IOException e) {
        }
    }

    public void StartCmbSportType() {
        List<String> TypeSportList = new ArrayList<>();
        ObservableList<String> items = null;
        for (Sport currentSport : sportList) {
            TypeSportList.add(currentSport.getName());
        }
        items = FXCollections.observableArrayList(TypeSportList);
        cmbSport.setItems(items);
    }

    public Sport returnCmbSportValue() {
        Sport sport = null;
        for (Sport currentSport : sportList) {
            if (currentSport.getName().toUpperCase().replaceAll("\\s+", "").equals(cmbSport.getValue().toUpperCase().replaceAll("\\s+", ""))) {
                sport = currentSport;
                return sport;
            }
        }
        return sport;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        rbtSport.setSelected(true);
        InitialConditionsPanel();
        OnActionRbtSport(null); //la accion del button
        EnabledMaintenance(false);
    }

}
