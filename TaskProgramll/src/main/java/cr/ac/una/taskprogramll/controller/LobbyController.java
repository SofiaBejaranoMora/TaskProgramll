/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.taskprogramll.controller;

import cr.ac.una.taskprogramll.util.AppContext;
import cr.ac.una.taskprogramll.util.FlowController;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;


public class LobbyController extends Controller implements Initializable {

    @FXML
    private MFXComboBox<String> cmbMenu;
    
    public void comboxInitializer(){
        cmbMenu.setValue(null);
        cmbMenu.getSelectionModel().clearSelection();
        cmbMenu.getItems().addAll("Registro Deporte", "Registro Equipo", "Mantenimiento de Deporte", "Mantenimiento de Equipo", "Crear Torneo", "Ver Torneos");
    }

    @FXML
    private void OnActionCmbMenu(ActionEvent event) {
        String menu= cmbMenu.getValue();
         switch (menu) {
            case "Registro Deporte":
                AppContext.getInstance().set("isSport", true);
                AppContext.getInstance().set("isMaintenace", false);
                AppContext.getInstance().set("Title",menu);
                FlowController.getInstance().goViewInStage("RegisterModify",  (Stage) cmbMenu.getScene().getWindow());
                break;
            case "Registro Equipo":            
                AppContext.getInstance().set("isSport", false);
                AppContext.getInstance().set("isMaintenace", false);
                AppContext.getInstance().set("Title",menu);
                FlowController.getInstance().goViewInStage("RegisterModify",  (Stage) cmbMenu.getScene().getWindow());
                break;
            case "Mantenimiento de Deporte":
                AppContext.getInstance().set("isSport", true);
                AppContext.getInstance().set("isMaintenace", true);
                AppContext.getInstance().set("Title",menu);
                FlowController.getInstance().goViewInStage("Maintenance",  (Stage) cmbMenu.getScene().getWindow());
                break;
            case "Mantenimiento de Equipo":
                AppContext.getInstance().set("isSport", false);
                AppContext.getInstance().set("isMaintenace", true);
                AppContext.getInstance().set("Title",menu);
                FlowController.getInstance().goViewInStage("Maintenance",  (Stage) cmbMenu.getScene().getWindow());
                break;
            case "Crear Torneo":
                FlowController.getInstance().goViewInStage("CreateTourney", (Stage) cmbMenu.getScene().getWindow());
                break;
                
            case "Ver Torneos":
                FlowController.getInstance().goViewInStage("ViewTourneys", (Stage) cmbMenu.getScene().getWindow());
                break;
                
            default:
                throw new AssertionError();
        }
    }
    
       @FXML
    private void OnMouseClickedImgExit(MouseEvent event) {
        FlowController.getInstance().salir();
    }
    
        @Override
    public void initialize(URL url, ResourceBundle rb) {
        comboxInitializer();
        
    }    

    @Override
    public void initialize() {
        comboxInitializer();
    }
}
