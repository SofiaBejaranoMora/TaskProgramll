/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.taskprogramll.controller;

import cr.ac.una.taskprogramll.model.FileManager;
import cr.ac.una.taskprogramll.model.Sport;
import cr.ac.una.taskprogramll.model.Team;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXRadioButton;
import io.github.palexdev.materialfx.controls.MFXTextField;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class RegisterMaintenanceController implements Initializable {

    File selectedImage = null;
    private Sport newSport = null;
    private List<Sport> sportList = new ArrayList<>();
    private List<Team> teamList = new ArrayList<>();
    private FileManager fileManeger = new FileManager();
    private File file;
    private Image image = null;

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
    private MFXComboBox<Team> cmbTeam;

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
        String name = null;
        if ((!txtName.getText().trim().isEmpty()) && (mgvImage.getImage() != null)) {
            if ((RadioButton) grpFiltro.getSelectedToggle() == rbtSport) {
                name = txtName.getText();
                if (!CheckedExistsSport(name)) {
                    newSport = new Sport(name, name); //si se mantiene asi puedo ir a cambiar el contructor solo para que entre name
                    sportList.add(newSport);
                    fileManeger.serialization(sportList, "Sport");
                    RelocateImage(name);
                    //inicializar el combox deportes
                }//Cuando el profe de los mensajes debo colocar un else y un mensaje de que ese equipo ya existe
                else {
                    labHead.setText("no se puede");
                }
            } else {

            }
            newSport = null;
            ClearLeftPanel();
        }
    }

    @FXML
    void OnActionBtnCancel(ActionEvent event) {
        ClearLeftPanel();
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

    }

    @FXML
    void OnActionCmbTeam(ActionEvent event) {

    }

    @FXML
    void OnActionRbtSport(ActionEvent event) {
        EnabledTeam(true);
    }

    @FXML
    void OnActionRbtTeam(ActionEvent event) {
        EnabledTeam(false);
    }

    public void EnabledTeam(Boolean enabled) {
        cmbTeam.setDisable(!enabled);
        cmbTeam.setVisible(enabled);
        cmbTeam.setManaged(enabled);
        btnPhoto.setDisable(!enabled);
        btnPhoto.setManaged(enabled);
        btnPhoto.setVisible(enabled);
    }

    public void EnabledMaintenance(Boolean enabled) {
        btnDelete.setDisable(!enabled);
        btnDelete.setManaged(enabled);
        btnDelete.setVisible(enabled);
    }

    public void ClearLeftPanel() {
        // falta indicar el radion button con el que se inicia
        //tambien siempre iniciar en combox
        txtName.clear();
        cmbTeam.setValue(null);
        cmbTeam.getSelectionModel().clearSelection();
        mgvImage.setImage(null);
    }

    public void InitialConditionsLefPanel() {
        file = new File("Sport.txt");
        if (file.exists()) {
            sportList = fileManeger.deserialization("Sport", Sport.class);
        }

    }

    public Boolean CheckedExistsSport(String name) {
        for (Sport sport : sportList) {
            if (sport.getName().toUpperCase().replaceAll("\\s+", "").equals(name.toUpperCase().replaceAll("\\s+", ""))) {
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
        String newRute = System.getProperty("user.dir") + "/src/main/resources/cr/ac/una/taskprogramll/resources/" + name + ".png";
        try {
            Path originalSourceImage = selectedImage.toPath();
            Path newDestinationImage = new File(newRute).toPath();
            Files.copy(originalSourceImage, newDestinationImage, StandardCopyOption.REPLACE_EXISTING);
            image = new Image("File:" + newRute);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        rbtSport.setSelected(true);
        InitialConditionsLefPanel();
        OnActionRbtSport(null); //la accion del button
        EnabledMaintenance(false);
        //mgvImage.setImage( new Image("D:\\Git\\TaskProgramll\\TaskProgramll\\src\\main\\resources\\cr\\ac\\una\\taskprogramll\\resources\\balon.jpg"));

    }

}
