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

    @FXML private MFXComboBox<Sport> comboDeportes;
    @FXML private TableView<Tourney> tablaTorneos;
    @FXML private TableColumn<Tourney, String> colTorneo;
    @FXML private TableColumn<Tourney, String> colEstado;
    @FXML private MFXButton btnBack;
    @FXML private VBox imgTrophey;
    @FXML private MFXButton btnPlay;
    @FXML private MFXButton btnInfo;

    private final FileManager fileManager = new FileManager();
    private ObservableList<Tourney> torneosList;
    private List<Sport> sportList = new ArrayList<>();

@Override
public void initialize(URL url, ResourceBundle rb) {
    try {
        torneosList = FXCollections.observableArrayList();
        tablaTorneos.setItems(torneosList);

        colTorneo.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEstado.setCellValueFactory(cellData -> {
            Tourney tourney = cellData.getValue();
            return new SimpleStringProperty(tourney.returnState());
        });

        // Inicialmente deshabilitados
        btnPlay.setDisable(true);
        btnInfo.setDisable(true);
        tablaTorneos.setVisible(false);

        loadSportList();
        comboDeportes.setItems(FXCollections.observableArrayList(sportList));

        tablaTorneos.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
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

 @FXML
private void mostrarTorneos(ActionEvent event) {
    Sport selectedSport = comboDeportes.getSelectionModel().getSelectedItem();
    if (selectedSport != null) {
        imgTrophey.setVisible(false);
        imgTrophey.setManaged(false);
        
        torneosList.clear();
        List<Tourney> tourneys = loadTourneyList();
        if (tourneys != null) {
            for (Tourney tourney : tourneys) {
                if (tourney.getSportType() != null && tourney.getSportType().getId() == selectedSport.getId()) {
                    torneosList.add(tourney);
                }
            }
        }
        
        boolean hayTorneos = !torneosList.isEmpty();
        tablaTorneos.setVisible(hayTorneos);
        
        // Forzar actualización de la interfaz
        Platform.runLater(() -> {
            tablaTorneos.refresh();
            if (hayTorneos) {
                // Seleccionar el primer elemento automáticamente
                tablaTorneos.getSelectionModel().selectFirst();
            }
        });
        
    } else {
        imgTrophey.setVisible(true);
        imgTrophey.setManaged(true);
        torneosList.clear();
        tablaTorneos.setVisible(false);
        btnPlay.setDisable(true);
        btnInfo.setDisable(true);
    }
}

private void actualizarBotones(Tourney torneoSeleccionado) {
    if (torneoSeleccionado == null) {
        btnPlay.setDisable(true);
        btnInfo.setDisable(true);
        return;
    }

    String estado = torneoSeleccionado.returnState();
    System.out.println("Actualizando botones para estado: " + estado); // Debug
    
    btnInfo.setDisable(false);
    
    switch(estado) {
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
    @FXML
    private void goBack(ActionEvent event) {
        FlowController.getInstance().goView("Lobby");
    }

    @FXML
    private void goPlay(ActionEvent event) {
        Tourney seleccionado = tablaTorneos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            // Lógica para jugar/continuar torneo
            AppContext.getInstance().set("SelectedTourney", seleccionado);
            // Navegar a la vista de información 
            FlowController.getInstance().goViewInStage("MatchTeams",(Stage) btnPlay.getScene().getWindow());
            System.out.println("Iniciando torneo: " + seleccionado.getName());
        }
    }


    
    @FXML
    private void showInfo(ActionEvent event) {
        Tourney seleccionado = tablaTorneos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
          // Guardar el torneo seleccionado en el contexto de la aplicación
        AppContext.getInstance().set("SelectedTourney", seleccionado);
        FlowController.getInstance().goViewInStage("TourneysInfo", (Stage) btnInfo.getScene().getWindow());
        
        System.out.println("Mostrando info de: " + seleccionado.getName());
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
    public void initialize() {
    }
}