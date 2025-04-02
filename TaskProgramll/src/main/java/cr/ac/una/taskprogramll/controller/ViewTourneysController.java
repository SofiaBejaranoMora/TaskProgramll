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
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.SimpleStyleableObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

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
            colEstado.setCellValueFactory(cellData->{
                Tourney tourney=cellData.getValue();
                return new SimpleStringProperty(tourney.returnState());
            });

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
            torneosList.clear();
            List<Tourney> tourneys = loadTourneyList();
            if (tourneys != null) {
                for (Tourney tourney : tourneys) {
                    if (tourney.getSportType() != null && tourney.getSportType().getId() == selectedSport.getId()) {
                        torneosList.add(tourney);
                    }
                }
            }
            tablaTorneos.setVisible(!torneosList.isEmpty());
        } else {
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