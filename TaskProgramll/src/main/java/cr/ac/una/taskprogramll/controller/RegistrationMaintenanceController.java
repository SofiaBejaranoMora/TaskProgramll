/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.taskprogramll.controller;

import cr.ac.una.taskprogramll.util.AppContext;
import cr.ac.una.taskprogramll.util.FlowController;
import cr.ac.una.taskprogramll.util.Mensaje;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class RegistrationMaintenanceController extends Controller implements Initializable {

    private File file;
    private Mensaje message = new Mensaje();


    @FXML
    private MFXButton btnRegistrationSport;
    @FXML
    private MFXButton btnRegistrationTeam;
    @FXML
    private MFXButton btnMaintenanceSport;
    @FXML
    private MFXButton btnMaintenanceTeam;
    @FXML
    private ImageView LobbyIcon;

    @FXML
    private void OnActionBtnRegistrationSport(ActionEvent event) {
        AppContext.getInstance().set("isSport", true);
        AppContext.getInstance().set("isMaintenace", false);
        AppContext.getInstance().set("Title", "Registro de " + btnRegistrationSport.getText());
        FlowController.getInstance().goView("RegistrationModify");
    }

    @FXML
    private void OnActionBtnRegistrationTeam(ActionEvent event) {
        file = new File("Sport.txt");
        if ((file.exists()) && (file.length() > 0)) {
            AppContext.getInstance().set("isSport", false);
            AppContext.getInstance().set("isMaintenace", false);
            AppContext.getInstance().set("Title", "Registro de " + btnRegistrationTeam.getText());
            FlowController.getInstance().goView("RegistrationModify");
        }
        else{
            message.show(Alert.AlertType.INFORMATION, "Aviso", "No puede registrar equipos porque aún no ha registrado ningún deporte. Por favor, registre al menos un deporte primero.");
        }
    }

    @FXML
    private void OnActionBtnMaintenanceSport(ActionEvent event) {
        file = new File("Sport.txt");
        if ((file.exists()) && (file.length() > 0)) {
            AppContext.getInstance().set("isSport", true);
            AppContext.getInstance().set("isMaintenace", true);
            AppContext.getInstance().set("Title", "Mantenimiento de " + btnMaintenanceSport.getText());
            FlowController.getInstance().goView("Maintenance");
        }
        else{
            message.show(Alert.AlertType.INFORMATION, "Aviso", "No hay deportes registrados para realizar mantenimiento. Por favor, registre al menos un deporte primero.");
        }
    }

    @FXML
    private void OnActionBtnMaintenanceTeam(ActionEvent event) {
        file = new File("Team.txt");
        if ((file.exists()) && (file.length() > 0)) {
            AppContext.getInstance().set("isSport", false);
            AppContext.getInstance().set("isMaintenace", true);
            AppContext.getInstance().set("Title", "Mantenimiento de " + btnMaintenanceTeam.getText());
            FlowController.getInstance().goView("Maintenance");
        }
        else{
            message.show(Alert.AlertType.INFORMATION, "Aviso", "No hay equipos registrados para realizar mantenimiento. Por favor, registre al menos un equipo primero.");
        }
    }

    @FXML
    private void OnMouseClickedLobbyIcon(MouseEvent event) {
        FlowController.getInstance().goViewInStage("Lobby", (Stage) btnMaintenanceTeam.getScene().getWindow());
    }

    @Override
    public void initialize() {

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

}
