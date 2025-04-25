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
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class RegistrationMaintenanceController extends Controller implements Initializable {

    private File file;
    private Mensaje message = new Mensaje();
    private Boolean existImage;

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
    private MFXButton btnMaintenanceTourney;
    @FXML
    private MFXButton btnMassRemovalTeam;
    @FXML
    private MFXButton btnMassRemovalTourney;
    @FXML
    private BorderPane root;

    @FXML
    private void OnActionBtnRegistrationSport(ActionEvent event) {
        existImage = (Boolean) AppContext.getInstance().get("ExistImage");
        if (existImage) {
            AppContext.getInstance().set("isSport", true);
            AppContext.getInstance().set("isMaintenace", false);
            AppContext.getInstance().set("isTeam", false);
            AppContext.getInstance().set("isTourney", false);
            AppContext.getInstance().set("Title", "Registro de " + btnRegistrationSport.getText());
            FlowController.getInstance().goView("RegistrationModify");
        } else {
            message.show(Alert.AlertType.WARNING, "Advertencia", "No puede realizar ninguna acción porque aún no ha seleccionado una imagen. Por favor, coloque una imagen primero.");
        }
        EnabledButton(existImage);
    }

    @FXML
    private void OnActionBtnRegistrationTeam(ActionEvent event) throws IOException {
        existImage = (Boolean) AppContext.getInstance().get("ExistImage");
        if (existImage) {
            file = new File("Sport.txt");
            if ((file.exists()) && (file.length() > 0)) {
                String content = new String(Files.readAllBytes(Paths.get(file.getPath()))).trim();
                if (!content.equals("[]") && !content.isEmpty()) {
                    AppContext.getInstance().set("isSport", false);
                    AppContext.getInstance().set("isMaintenace", false);
                    AppContext.getInstance().set("isTeam", true);
                    AppContext.getInstance().set("isTourney", false);
                    AppContext.getInstance().set("Title", "Registro de " + btnRegistrationTeam.getText());
                    FlowController.getInstance().goView("RegistrationModify");
                } else {
                    message.show(Alert.AlertType.INFORMATION, "Aviso", "No puede registrar equipos porque aún no ha registrado ningún deporte. Por favor, registre al menos un deporte primero.");
                }
            } else {
                message.show(Alert.AlertType.INFORMATION, "Aviso", "No puede registrar equipos porque aún no ha registrado ningún deporte. Por favor, registre al menos un deporte primero.");
            }
        } else {
            message.show(Alert.AlertType.WARNING, "Advertencia", "No puede realizar ninguna acción porque aún no ha seleccionado una imagen. Por favor, coloque una imagen primero.");
        }
        EnabledButton(existImage);
    }

    @FXML
    private void OnActionBtnMaintenanceSport(ActionEvent event) throws IOException {
        existImage = (Boolean) AppContext.getInstance().get("ExistImage");
        if (existImage) {
            file = new File("Sport.txt");
            if ((file.exists()) && (file.length() > 0)) {
                String content = new String(Files.readAllBytes(Paths.get(file.getPath()))).trim();
                if (!content.equals("[]") && !content.isEmpty()) {
                    AppContext.getInstance().set("isSport", true);
                    AppContext.getInstance().set("isMaintenace", true);
                    AppContext.getInstance().set("isTeam", false);
                    AppContext.getInstance().set("isTourney", false);
                    AppContext.getInstance().set("Title", "Mantenimiento de " + btnMaintenanceSport.getText());
                    FlowController.getInstance().goView("Maintenance");
                } else {
                    message.show(Alert.AlertType.INFORMATION, "Aviso", "No hay deportes registrados para realizar mantenimiento. Por favor, registre al menos un deporte primero.");
                }
            } else {
                message.show(Alert.AlertType.INFORMATION, "Aviso", "No hay deportes registrados para realizar mantenimiento. Por favor, registre al menos un deporte primero.");
            }
        } else {
            message.show(Alert.AlertType.WARNING, "Advertencia", "No puede realizar ninguna acción porque aún no ha seleccionado una imagen. Por favor, coloque una imagen primero.");
        }
        EnabledButton(existImage);
    }

    @FXML
    private void OnActionBtnMaintenanceTeam(ActionEvent event) throws IOException {
        existImage = (Boolean) AppContext.getInstance().get("ExistImage");
        if (existImage) {
            file = new File("Team.txt");
            if ((file.exists()) && (file.length() > 0)) {
                String content = new String(Files.readAllBytes(Paths.get(file.getPath()))).trim();
                if (!content.equals("[]") && !content.isEmpty()) {
                    AppContext.getInstance().set("isTeam", true);
                    AppContext.getInstance().set("isSport", false);
                    AppContext.getInstance().set("isMaintenace", true);
                    AppContext.getInstance().set("isTourney", false);
                    AppContext.getInstance().set("Title", "Mantenimiento de " + btnMaintenanceTeam.getText());
                    FlowController.getInstance().goView("Maintenance");
                } else {
                    message.show(Alert.AlertType.INFORMATION, "Aviso", "No hay equipos registrados para realizar mantenimiento. Por favor, registre al menos un equipo primero.");
                }
            } else {
                message.show(Alert.AlertType.INFORMATION, "Aviso", "No hay equipos registrados para realizar mantenimiento. Por favor, registre al menos un equipo primero.");
            }
        } else {
            message.show(Alert.AlertType.WARNING, "Advertencia", "No puede realizar ninguna acción porque aún no ha seleccionado una imagen. Por favor, coloque una imagen primero.");
        }
        EnabledButton(existImage);
    }

    @FXML
    private void OnActionBtnMaintenanceTourney(ActionEvent event) throws IOException {
        existImage = (Boolean) AppContext.getInstance().get("ExistImage");
        if (existImage) {
            file = new File("Tourney.txt");
            if ((file.exists()) && (file.length() > 0)) {
                String content = new String(Files.readAllBytes(Paths.get(file.getPath()))).trim();
                if (!content.equals("[]") && !content.isEmpty()) {
                    AppContext.getInstance().set("isSport", false);
                    AppContext.getInstance().set("isTourney", true);
                    AppContext.getInstance().set("isTeam", false);
                    AppContext.getInstance().set("isMaintenace", true);
                    AppContext.getInstance().set("Title", "Mantenimiento de " + btnMaintenanceTourney.getText());
                    FlowController.getInstance().goView("Maintenance");
                } else {
                    message.show(Alert.AlertType.INFORMATION, "Aviso", "No hay torneos registrados para realizar mantenimiento. Por favor, registre al menos un torneos primero.");
                }
            } else {
                message.show(Alert.AlertType.INFORMATION, "Aviso", "No hay torneos registrados para realizar mantenimiento. Por favor, registre al menos un torneos primero.");
            }
        } else {
            message.show(Alert.AlertType.WARNING, "Advertencia", "No puede realizar ninguna acción porque aún no ha seleccionado una imagen. Por favor, coloque una imagen primero.");
        }
        EnabledButton(existImage);
    }

    @FXML
    private void OnActionBtnMassRemovalTeam(ActionEvent event) throws IOException {
        existImage = (Boolean) AppContext.getInstance().get("ExistImage");
        if (existImage) {
        file = new File("Team.txt");
        if ((file.exists()) && (file.length() > 0)) {
            String content = new String(Files.readAllBytes(Paths.get(file.getPath()))).trim();
            if (!content.equals("[]") && !content.isEmpty()) {
                AppContext.getInstance().set("isTourney", false);
                AppContext.getInstance().set("Title", "Eliminación Masiva De " + btnMassRemovalTeam.getText());
                FlowController.getInstance().goView("MassRemoval");
            } else {
                message.show(Alert.AlertType.INFORMATION, "Aviso", "No hay equipos registrados para poder eliminar.");
            }
        } else {
            message.show(Alert.AlertType.INFORMATION, "Aviso", "No hay equipos registrados para poder eliminar.");
        }
        } else {
            message.show(Alert.AlertType.WARNING, "Advertencia", "No puede realizar ninguna acción porque aún no ha seleccionado una imagen. Por favor, coloque una imagen primero.");
        }
    }

    @FXML
    private void OnActionBtnMassRemovalTourney(ActionEvent event) throws IOException {
         existImage = (Boolean) AppContext.getInstance().get("ExistImage");
        if (existImage) {
        file = new File("Tourney.txt");
        if ((file.exists()) && (file.length() > 0)) {
            String content = new String(Files.readAllBytes(Paths.get(file.getPath()))).trim();
            if (!content.equals("[]") && !content.isEmpty()) {
                AppContext.getInstance().set("isTourney", true);
                AppContext.getInstance().set("Title", "Eliminación Masiva De " + btnMassRemovalTourney.getText());
                FlowController.getInstance().goView("MassRemoval");
            } else {
                message.show(Alert.AlertType.INFORMATION, "Aviso", "No hay torneos registrados para poder eliminar.");
            }
        } else {
            message.show(Alert.AlertType.INFORMATION, "Aviso", "No hay torneos registrados para poder eliminar.");
        }
        } else {
            message.show(Alert.AlertType.WARNING, "Advertencia", "No puede realizar ninguna acción porque aún no ha seleccionado una imagen. Por favor, coloque una imagen primero.");
        }
    }

    @FXML
    private void OnMouseClickedLobbyIcon(MouseEvent event) {
        existImage = (Boolean) AppContext.getInstance().get("ExistImage");
        if (existImage) {
            FlowController.getInstance().goViewInStage("Lobby", (Stage) root.getScene().getWindow());
        } else {
            message.show(Alert.AlertType.WARNING, "Advertencia", "No puede realizar ninguna acción porque aún no ha seleccionado una imagen. Por favor, coloque una imagen primero.");
        }
        EnabledButton(existImage);
    }

    public void EnabledButton(Boolean Enabled) {
        LobbyIcon.setDisable(!Enabled);
        btnMaintenanceSport.setDisable(!Enabled);
        btnMaintenanceTeam.setDisable(!Enabled);
        btnRegistrationSport.setDisable(!Enabled);
        btnRegistrationTeam.setDisable(!Enabled);
    }

    @Override
    public void initialize() {
        AppContext.getInstance().set("ExistImage", true);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        AppContext.getInstance().set("ExistImage", true);
    }

}
