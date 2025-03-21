/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.taskprogramll.controller;

import cr.ac.una.taskprogramll.model.Sport;
import cr.ac.una.taskprogramll.util.AppContext;
import cr.ac.una.taskprogramll.util.FlowController;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;


public class LobbyController extends Controller implements Initializable {

    @FXML
    private MFXComboBox<String> cmbMenu;
    
    public void comboxInitializer(){
        cmbMenu.getItems().addAll("Registro Deporte", "Registro Equipo", "Mantenimiento de Deporte", "Mantenimiento de Equipo");
    }

    @FXML
    private void OnActionCmbMenu(ActionEvent event) {
        String menu= cmbMenu.getValue();
         switch (menu) {
            case "Registro Deporte":
                AppContext.getInstance().set("isSport", true);
                AppContext.getInstance().set("isMaintenace", false);
                FlowController.getInstance().goViewInStage("RegisterModify",  (Stage) cmbMenu.getScene().getWindow());
                ((Stage) cmbMenu.getScene().getWindow()).close();
                break;
            case "Registro Equipo":            
                AppContext.getInstance().set("isSport", false);
                AppContext.getInstance().set("isMaintenace", false);
                FlowController.getInstance().goViewInStage("RegisterModify",  (Stage) cmbMenu.getScene().getWindow());
                ((Stage) cmbMenu.getScene().getWindow()).close();
                break;
            case "Mantenimiento de Deporte":

                break;
            case "Mantenimiento de Equipo":

                break;
            default:
                throw new AssertionError();
        }
    }
    
        @Override
    public void initialize(URL url, ResourceBundle rb) {
        comboxInitializer();
        
    }    

    @Override
    public void initialize() {
        
    }
    
}
