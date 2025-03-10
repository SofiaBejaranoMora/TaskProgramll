/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.taskprogramll.controller;

import cr.ac.una.taskprogramll.model.FileManager;
import cr.ac.una.taskprogramll.model.Sport;
import cr.ac.una.taskprogramll.model.Team;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
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
    private TextField txtLftName;

    @FXML
    private Button btnLftAccept;

    @FXML
    private Button btnLftCancel;

    @FXML
    private Button btnLftDelete;

    @FXML
    private Button btnLftSelectImage;

    @FXML
    private Button btnLftPhoto;

    @FXML
    private ComboBox<String> cmbLftTeam;

    @FXML
    private ToggleGroup grpFiltro;

    @FXML
    private Label labLftHead;

    @FXML
    private ImageView mgvImage;

    @FXML
    private RadioButton rbtnLftTeam;

    @FXML
    private RadioButton rbtnLftSport;

    @FXML
    void OnActionBtnLftAccept(ActionEvent event) {
        String nameImage = null;
        String name = null;
        if ((!txtLftName.getText().trim().isEmpty())/*&&(mgvImage.getImage() != null)*/) {
            if ((RadioButton) grpFiltro.getSelectedToggle() == rbtnLftSport) {
                // nameImage=Paths.get(mgvImage.getImage().getUrl()).getFileName().toString();
                name = txtLftName.getText();
                if (!CheckedExistsSport(name)) {
                    newSport = new Sport(name, name); //si se mantiene asi puedo ir a cambiar el contructor solo para que entre name
                    sportList.add(newSport);
                    fileManeger.serialization(sportList, "Sport");
                    RelocateImage(name);
                    //inicializar el combox deportes
                }//Cuando el profe de los mensajes debo colocar un else y un mensaje de que ese equipo ya existe
                else {
                    labLftHead.setText("no se puede");
                }
            } else {

            }
            newSport = null;
            ClearLeftPanel();
        }
    }

    @FXML
    void OnActionBtnLftCancel(ActionEvent event) {
        ClearLeftPanel();
    }

    @FXML
    void OnActionBtnLftDelete(ActionEvent event) {

    }

    @FXML
    void OnActionBtnLftSelectImage(ActionEvent event) {
        SelectImage();
    }

    @FXML
    void OnActionBtnLftPhoto(ActionEvent event) {

    }

    @FXML
    void OnActionCmbLftTeam(ActionEvent event) {

    }

    @FXML
    void OnActionRbtnLftTeam(ActionEvent event) {
        EnabledTeam(true);
    }

    @FXML
    void OnActionRbtnLftSport(ActionEvent event) {
        EnabledTeam(false);
    }

    public void EnabledTeam(Boolean enabled) {
        cmbLftTeam.setDisable(!enabled);
        cmbLftTeam.setVisible(enabled);
        cmbLftTeam.setManaged(enabled);
        btnLftPhoto.setDisable(!enabled);
        btnLftPhoto.setManaged(enabled);
        btnLftPhoto.setVisible(enabled);
    }

    public void EnabledMaintenance(Boolean enabled) {
        btnLftDelete.setDisable(!enabled);
        btnLftDelete.setManaged(enabled);
        btnLftDelete.setVisible(enabled);
    }

    public void ClearLeftPanel() {
        // falta indicar el radion button con el que se inicia
        //tambien siempre iniciar en combox
        txtLftName.clear();
        cmbLftTeam.setValue(null);
        cmbLftTeam.getSelectionModel().clearSelection();
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
        rbtnLftSport.setSelected(true);
        InitialConditionsLefPanel();
        OnActionRbtnLftSport(null); //la accion del button
        EnabledMaintenance(false);
        //mgvImage.setImage( new Image("D:\\Git\\TaskProgramll\\TaskProgramll\\src\\main\\resources\\cr\\ac\\una\\taskprogramll\\resources\\balon.jpg"));

    }

}
