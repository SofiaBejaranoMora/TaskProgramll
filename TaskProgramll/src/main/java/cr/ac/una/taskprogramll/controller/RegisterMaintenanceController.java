/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.taskprogramll.controller;

import java.net.URL;
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

/**
 * FXML Controller class
 *
 * @author sofia
 */
public class RegisterMaintenanceController implements Initializable {

    @FXML
    private TextField TxtLftName;

    @FXML
    private Button btnLftAccept;

    @FXML
    private Button btnLftCancel;

    @FXML
    private Button btnLftDelete;

    @FXML
    private Button btnLftImage;

    @FXML
    private Button btnLftPhoto;

    @FXML
    private ComboBox<?> cmbLftEquipment;

    @FXML
    private ToggleGroup grpFiltro;

    @FXML
    private Label labLftHead;

    @FXML
    private RadioButton rbtnLftEquipment;

    @FXML
    private RadioButton rbtnLftSport;

    @FXML
    void OnActionBtnLftAccept(ActionEvent event) {

    }

    @FXML
    void OnActionBtnLftCancel(ActionEvent event) {

    }

    @FXML
    void OnActionBtnLftDelete(ActionEvent event) {

    }

    @FXML
    void OnActionBtnLftImage(ActionEvent event) {

    }

    @FXML
    void OnActionBtnLftPhoto(ActionEvent event) {

    }

    @FXML
    void OnActionCmbLftEquipment(ActionEvent event) {

    }

    @FXML
    void OnActionRbtnLftEquipment(ActionEvent event) {
        EnabledEquipment(true);
    }

    @FXML
    void OnActionRbtnLftSport(ActionEvent event) {
        EnabledEquipment(false);
    }
    
    public void EnabledEquipment (Boolean enabled){
        cmbLftEquipment.setDisable(!enabled);
        cmbLftEquipment.setVisible(enabled);
        btnLftPhoto.setDisable(!enabled);
        btnLftPhoto.setManaged(enabled);
        btnLftPhoto.setVisible(enabled);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
