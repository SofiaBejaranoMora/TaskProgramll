/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.taskprogramll.controller;

import cr.ac.una.taskprogramll.model.FileManager;
import cr.ac.una.taskprogramll.model.Sport;
import cr.ac.una.taskprogramll.model.Team;
import cr.ac.una.taskprogramll.util.FlowController;
import cr.ac.una.taskprogramll.util.Mensaje;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class LobbyController extends Controller implements Initializable {

    private File file = new File("Sport.txt");
    private File file2 = new File("Team.txt");

    private Mensaje message = new Mensaje();
    private List<Sport> sportList = new ArrayList<>();
    private List<Team> teamList = new ArrayList<>();
    private FileManager fileManeger = new FileManager();

    @FXML
    private MFXComboBox<String> cmbMenu;

    public void comboxInitializer() {
        cmbMenu.getItems().addAll("Registro y Mantenimiento", "Reseñas de Equipos", "Crear Torneo", "Ver Torneos", "Mini Juego Menu", "Historial de Equipos");
    }

    public void clean() {
        cmbMenu.getSelectionModel().clearSelection();
        cmbMenu.setValue(null);
    }

    @FXML
    private void OnActionCmbMenu(ActionEvent event) throws IOException {
        String menu = cmbMenu.getValue();
        switch (menu) {
            case "Registro y Mantenimiento":
                FlowController.getInstance().goMain();
                break;
            case "Reseñas de Equipos":
                file = new File("Team.txt");
                if ((file.exists()) && (file.length() > 0)) {
                    String content = new String(Files.readAllBytes(Paths.get(file.getPath()))).trim();
                    if (!content.equals("[]") && !content.isEmpty()) {
                        FlowController.getInstance().goViewInStage("Review", (Stage) cmbMenu.getScene().getWindow());
                    } else {
                        message.show(Alert.AlertType.INFORMATION, "Aviso", "No hay equipos registrados para realizar mantenimiento. Por favor, registre al menos un equipo primero.");
                    }
                } else {
                    message.show(Alert.AlertType.INFORMATION, "Aviso", "No hay equipos registrados para realizar mantenimiento. Por favor, registre al menos un equipo primero.");
                }
                break;
            case "Crear Torneo":
                if (EnableTourney()) {
                    CreateTourneyController controller = (CreateTourneyController) FlowController.getInstance().getController("CreateTourney");
                    controller.InitialConditionsPanel();
                    FlowController.getInstance().goViewInStage("CreateTourney", (Stage) cmbMenu.getScene().getWindow());
                } else {
                    message.show(Alert.AlertType.INFORMATION, "Aviso", "No puede registrar torneos porque aún no ha registrado ningún equipo. Por favor, registre al menos dos equipos del mismo deporte primero.");
                }
                break;
            case "Ver Torneos":
                file = new File("Tourney.txt");
                if ((file.exists()) && (file.length() > 0)) {
                    String content = new String(Files.readAllBytes(Paths.get(file.getPath()))).trim();
                    if (!content.equals("[]") && !content.isEmpty()) {
                        ViewTourneysController controller = (ViewTourneysController) FlowController.getInstance().getController("ViewTourneys");
                        controller.initialPanelConditions();
                        FlowController.getInstance().goViewInStage("ViewTourneys", (Stage) cmbMenu.getScene().getWindow());
                    } else {
                        message.show(Alert.AlertType.INFORMATION, "Aviso", "No puede ver o iniciar torneos porque aún no ha registrado ningún torneo. Por favor, registre al menos un torneo primero.");
                    }
                } else {
                    message.show(Alert.AlertType.INFORMATION, "Aviso", "No puede ver o iniciar torneos porque aún no ha registrado ningún torneo. Por favor, registre al menos un torneo primero.");
                }
                break;
            case "Mini Juego Menu":
                FlowController.getInstance().goViewInStage("MenuGame", (Stage) cmbMenu.getScene().getWindow());
                break;
            case "Historial de Equipos":
                file = new File("Team.txt");
                if ((file.exists()) && (file.length() > 0)) {
                    String content = new String(Files.readAllBytes(Paths.get(file.getPath()))).trim();
                    if (!content.equals("[]") && !content.isEmpty()) {
                        FlowController.getInstance().goViewInStage("TeamsHistory", (Stage) cmbMenu.getScene().getWindow());
                    } else {
                        message.show(Alert.AlertType.INFORMATION, "Aviso", "No se puede ver el historial equipo porque aún no ha registrado ningún equipo. Por favor, registre al menos un equipo primero.");
                    }
                } else {
                    message.show(Alert.AlertType.INFORMATION, "Aviso", "No se puede ver el historial equipo porque aún no ha registrado ningún equipo. Por favor, registre al menos un equipo primero.");
                }
                break;
            default:
                throw new AssertionError();
        }
    }

    public Boolean EnableTourney() {
        if (((file.exists()) && (file.length() > 0)) && ((file2.exists()) && (file2.length() > 0))) {
            teamList = fileManeger.deserialization("Team", Team.class);
            sportList = fileManeger.deserialization("Sport", Sport.class);
            int couter = 0;
            if (teamList.size() >= 2) {
                for (Sport currentSport : sportList) {
                    for (Team currentTeam : teamList) {
                        if (currentSport.getId() == currentTeam.getIdSportType()) {
                            couter++;
                        }
                        if (couter >= 2) {
                            return true;
                        }
                    }
                    couter = 0;
                }
            }
        }
        return false;
    }

    @FXML
    private void OnMouseClickedImgExit(MouseEvent event) {
        FlowController.getInstance().salir();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        comboxInitializer();
        clean();
    }

    @Override
    public void initialize() {
        clean();
    }
}
