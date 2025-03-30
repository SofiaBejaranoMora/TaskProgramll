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
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import static javax.swing.UIManager.get;

/**
 * FXML Controller class
 *
 * @author Michelle Wittingham
 */
public class ViewTourneysController extends Controller implements Initializable {

    @FXML
    private MFXComboBox<Sport> comboDeportes;
    @FXML
    private TableView<Tourney> tablaTorneos;
    @FXML
    private TableColumn<Tourney, String> colTorneo;
    @FXML
    private TableColumn<Tourney, String> colEstado;
    @FXML
    private MFXButton btnBack;

    private final FileManager fileManager = new FileManager();
    private ObservableList<Tourney> torneosList;
    private List<Sport> sportList = new ArrayList<>();

   @Override
public void initialize(URL url, ResourceBundle rb) {
    try {
        torneosList = FXCollections.observableArrayList();
        tablaTorneos.setItems(torneosList);

        colTorneo.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadSportList();
        comboDeportes.setItems(FXCollections.observableArrayList(sportList));
    } catch (Exception e) {
        System.err.println("Error en initialize: " + e.getMessage());
        e.printStackTrace();
    }
}

    @FXML
    private void mostrarTorneos(ActionEvent event) {
        Sport selectedSport = comboDeportes.getSelectionModel().getSelectedItem();
        if (selectedSport != null) {
            // Limpiar la lista anterior
            torneosList.clear();

            // Cargar torneos desde AppContext o deserializar
            List<Tourney> tourneys = loadTourneyList();
            if (tourneys != null) {
                // Filtrar torneos por el deporte seleccionado
                for (Tourney tourney : tourneys) {
                    if (tourney.getSport() != null && tourney.getSportType().getId() == selectedSport.getId()) {
                        torneosList.add(tourney);
                    }
                }
            }

            // Mostrar la tabla si hay torneos, ocultarla si no
            tablaTorneos.setVisible(!torneosList.isEmpty());
        } else {
            // Si no hay deporte seleccionado, ocultar la tabla
            torneosList.clear();
            tablaTorneos.setVisible(false);
        }
    }

    @FXML
    private void goBack(ActionEvent event) {
        FlowController.getInstance().goView("Lobby");
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
            try {
                tourneys = fileManager.deserialization("Tourney", Tourney.class);
                AppContext.getInstance().set("tourneys", tourneys);
            } catch (Exception e) {
                System.err.println("Error al deserializar torneos: " + e.getMessage());
            }
        }
        return tourneys;
    }

    @Override
    public void initialize() {
    }
}