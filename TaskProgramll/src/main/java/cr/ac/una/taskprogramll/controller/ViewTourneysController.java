/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.taskprogramll.controller;

import cr.ac.una.taskprogramll.model.FileManager;
import cr.ac.una.taskprogramll.model.Sport;
import cr.ac.una.taskprogramll.model.Tourney;
import cr.ac.una.taskprogramll.util.AppContext;
import cr.ac.una.taskprogramll.util.FlowController;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ViewTourneysController extends Controller implements Initializable {

    private final FileManager fileManager = new FileManager();
    private ObservableList<Tourney> torneosList;
    private List<Sport> sportList = new ArrayList<>();
    
    @FXML
    private MFXComboBox<Sport> sportsComboBox;
    @FXML
    private TableView<Tourney> tourneysTable;
    @FXML
    private TableColumn<Tourney, String> colTourney;
    @FXML
    private TableColumn<Tourney, String> colState;
    @FXML
    private MFXButton btnBack;
    @FXML
    private VBox imgTrophey;
    @FXML
    private MFXButton btnPlay;
    @FXML
    private MFXButton btnInfo;
    
        @FXML
    private void goBack(ActionEvent event) {
        FlowController.getInstance().goViewInStage("Lobby", (Stage) btnBack.getScene().getWindow());
    }

    @FXML
    private void goPlay(ActionEvent event) {
        Tourney selectedTourney = tourneysTable.getSelectionModel().getSelectedItem();
        if (selectedTourney != null) {
            // Lógica para jugar/continuar torneo
            AppContext.getInstance().set("SelectedTourney", selectedTourney);
            // Navegar a la vista de información 
            MatchTeamsController controller = (MatchTeamsController) FlowController.getInstance().getController("MatchTeams");
            controller.initializeFromAppContext();
            FlowController.getInstance().goViewInStage("MatchTeams", (Stage) btnPlay.getScene().getWindow());
            System.out.println("Iniciando torneo: " + selectedTourney.getName());
        }
    }

    @FXML
    private void showInfo(ActionEvent event) {
        Tourney selectedTourney = tourneysTable.getSelectionModel().getSelectedItem();
        if (selectedTourney != null) {
            // Guardar el torneo seleccionado en el contexto de la aplicación
            AppContext.getInstance().set("SelectedTourney", selectedTourney);
            TourneysInfoController controller = (TourneysInfoController) FlowController.getInstance().getController("TourneysInfo");
            controller.initialPanelConditions();
            FlowController.getInstance().goViewInStage("TourneysInfo", (Stage) btnInfo.getScene().getWindow());

            System.out.println("Mostrando info de: " + selectedTourney.getName());
        }
    }
   
    @FXML
    private void mostrarTorneos(ActionEvent event) {
        Sport selectedSport = sportsComboBox.getSelectionModel().getSelectedItem();
        if (selectedSport != null) {
            imgTrophey.setVisible(false);
            imgTrophey.setManaged(false);

            torneosList.clear();
            List<Tourney> tourneys = loadTourneyList();
            if (tourneys != null) {
                for (Tourney tourney : tourneys) {
                    if (tourney.getSportTypeId()== selectedSport.getId()) {
                        torneosList.add(tourney);
                    }
                }
            }

            boolean state = !torneosList.isEmpty();
            tourneysTable.setVisible(state);

            // Forzar actualización de la interfaz
            Platform.runLater(() -> {
                tourneysTable.refresh();
                if (state) {
                    // Seleccionar el primer elemento automáticamente
                    tourneysTable.getSelectionModel().selectFirst();
                }
            });

        } else {
            imgTrophey.setVisible(true);
            imgTrophey.setManaged(true);
            torneosList.clear();
            tourneysTable.setVisible(false);
            btnPlay.setDisable(true);
            btnInfo.setDisable(true);
        }
    }
 
    public void initialPanelConditions() {
                try {
            torneosList = FXCollections.observableArrayList();
            tourneysTable.setItems(torneosList);

            colTourney.setCellValueFactory(new PropertyValueFactory<>("name"));
            colState.setCellValueFactory(cellData -> {
                Tourney tourney = cellData.getValue();
                return new SimpleStringProperty(tourney.returnState());
            });

            // Inicialmente deshabilitados
            btnPlay.setDisable(true);
            btnInfo.setDisable(true);
            tourneysTable.setVisible(false);

            loadSportList();
            sportsComboBox.setItems(FXCollections.observableArrayList(sportList));

            tourneysTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    System.out.println("Torneo seleccionado: " + newVal.getName() + " - Estado: " + newVal.returnState());
                    actualizarBotones(newVal);
                } else {
                    btnPlay.setDisable(true);
                    btnInfo.setDisable(true);
                }
            });

        } catch (Exception e) {
            System.err.println("Error en initialize: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void actualizarBotones(Tourney selectedTourney) {
        if (selectedTourney == null) {
            btnPlay.setDisable(true);
            btnInfo.setDisable(true);
            return;
        }

        String state = selectedTourney.returnState();
        System.out.println("Actualizando botones para estado: " + state); // Debug

        btnInfo.setDisable(false);

        switch (state) {
            case "Sin Empezar":
                btnPlay.setDisable(false);
                btnPlay.setText("Jugar");
                break;
            case "En Proceso":
                btnPlay.setDisable(false);
                btnPlay.setText("Continuar");
                break;
            case "Finalizado":
                btnPlay.setDisable(true);
                btnPlay.setText("Finalizado");
                break;
            default:
                btnPlay.setDisable(true);
        }
    }



    private void loadSportList() {
        @SuppressWarnings("unchecked")
        List<Sport> sports = (List<Sport>) AppContext.getInstance().get("sports");
        if (sports == null || sports.isEmpty()) {
            try {
                sports = fileManager.deserialization("Sport", Sport.class);
                AppContext.getInstance().set("sports", sports);
            } catch (Exception e) {
                System.err.println("Error al deserializar deportes: " + e.getMessage());
            }
        }
        if (sports != null) {
            sportList.addAll(sports);
        }
    }

    private List<Tourney> loadTourneyList() {
        @SuppressWarnings("unchecked")
        List<Tourney> tourneys = (List<Tourney>) AppContext.getInstance().get("tourneys");
        if (tourneys == null || tourneys.isEmpty()) {
            File tourneyFile = new File("Tourney.txt");
            if (tourneyFile.exists() && tourneyFile.length() > 0) {
                try {
                    tourneys = fileManager.deserialization("Tourney", Tourney.class);
                    if (tourneys != null) {
                        AppContext.getInstance().set("tourneys", tourneys);
                        System.out.println("Torneos cargados desde Tourney.txt: " + tourneys.size());
                    } else {
                        System.out.println("Error al leer Tourney.txt, reiniciando archivo.");
                        tourneyFile.delete();
                        tourneys = new ArrayList<>();
                        AppContext.getInstance().set("tourneys", tourneys);
                    }
                } catch (Exception e) {
                    System.err.println("Error al deserializar torneos: " + e.getMessage());
                    System.out.println("Borrando Tourney.txt por incompatibilidad.");
                    tourneyFile.delete();
                    tourneys = new ArrayList<>();
                    AppContext.getInstance().set("tourneys", tourneys);
                }
            } else {
                tourneys = new ArrayList<>();
                AppContext.getInstance().set("tourneys", tourneys);
                System.out.println("No hay Tourney.txt, iniciando con lista vacía.");
            }
        }
        return tourneys;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {}
    
    @Override
    public void initialize() {}
}
