package cr.ac.una.taskprogramll.controller;

import cr.ac.una.taskprogramll.model.Sport;
import cr.ac.una.taskprogramll.model.Team;
import cr.ac.una.taskprogramll.model.Tourney;
import io.github.palexdev.materialfx.controls.MFXSlider;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;

public class CreateTourneyController implements Initializable {

    @FXML
    private Button btnCancel;
    @FXML
    private Button btnStart;
    @FXML
    private Button btnAdjustTeams;
    @FXML
    private MFXTextField txtTourneyName;
    @FXML
    private MFXTextField txtMatchTime;
    @FXML
    private ComboBox<Sport> tglLstSportType;
    @FXML
    private TableView<Team> tblTeams; // Equipos disponibles
    @FXML
    private TableView<Team> tblTeams1; // Equipos elegidos
    @FXML
    private TableColumn<Team, String> colTeamName; // Columna de equipos disponibles
    @FXML
    private TableColumn<Team, String> colTeamName1; // Columna de equipos elegidos
    @FXML
    private MFXSlider sliderTeamCount;

    private final ObservableList<Team> teamList = FXCollections.observableArrayList();
    private final ObservableList<Team> selectedTeams = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Configurar las columnas de ambas tablas
        colTeamName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colTeamName1.setCellValueFactory(new PropertyValueFactory<>("name"));

        // Asignar las listas a las tablas
        tblTeams.setItems(teamList); // Tabla de equipos disponibles
        tblTeams1.setItems(selectedTeams); // Tabla de equipos seleccionados

        // Configuración del slider
        sliderTeamCount.setMin(32);
        sliderTeamCount.setMax(64);
        sliderTeamCount.setValue(32); // Valor inicial
        sliderTeamCount.valueProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Slider value: " + newValue.intValue());
        });

        // Clic en la tabla de equipos disponibles para mover a la tabla seleccionada
        tblTeams.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                Team clickedTeam = tblTeams.getSelectionModel().getSelectedItem();
                if (clickedTeam != null && !selectedTeams.contains(clickedTeam)) {
                    selectedTeams.add(clickedTeam); // Mover a equipos seleccionados
                    teamList.remove(clickedTeam); // Remover de equipos disponibles
                }
            }
        });

        // Clic en la tabla de equipos seleccionados para devolver a disponibles
        tblTeams1.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                Team clickedTeam = tblTeams1.getSelectionModel().getSelectedItem();
                if (clickedTeam != null) {
                    teamList.add(clickedTeam); // Mover a equipos disponibles
                    selectedTeams.remove(clickedTeam); // Remover de equipos seleccionados
                }
            }
        });
    }

    @FXML
    private void adjustSelectedTeamsSlice(ActionEvent event) {
        int desiredCount = (int) sliderTeamCount.getValue();

        // Asegurar que esté dentro del rango permitido
        desiredCount = Math.max(32, Math.min(desiredCount, 64));

        // Ajustar los equipos seleccionados según el slider
        while (selectedTeams.size() > desiredCount) {
            Team removedTeam = selectedTeams.remove(selectedTeams.size() - 1); // Remover del final
            teamList.add(removedTeam); // Regresar a disponibles
        }

        for (Team team : teamList) {
            if (selectedTeams.size() >= desiredCount) {
                break;
            }
            if (!selectedTeams.contains(team)) {
                selectedTeams.add(team); // Agregar a seleccionados
                teamList.remove(team); // Remover de disponibles
            }
        }

        System.out.println("Teams adjusted to: " + selectedTeams.size());
    }

    @FXML
    private void createTourney(ActionEvent event) {
        String tourneyName = txtTourneyName.getText().trim();
        String matchTime = txtMatchTime.getText().trim();

        // Validar entradas
        if (tourneyName.isEmpty() || matchTime.isEmpty()) {
            System.out.println("All fields are required.");
            return;
        }
        if (!isValidNumericInput(matchTime)) {
            System.out.println("Match time must be a valid number.");
            return;
        }

        if (selectedTeams.isEmpty()) {
            System.out.println("You must select at least one team.");
            return;
        }

        // Crear el torneo
        Sport selectedSport = tglLstSportType.getValue();
        if (selectedSport == null) {
            System.out.println("You must select a sport type.");
            return;
        }

        int id = (int) (Math.random() * 1000); // Generar un ID de torneo único (puedes cambiar esta lógica)
        List<Team> teamsForTourney = new ArrayList<>(selectedTeams);

        Tourney newTourney = new Tourney(id, Integer.parseInt(matchTime), selectedSport, teamsForTourney);
        System.out.println("Tourney created successfully: " + tourneyName + " with " + teamsForTourney.size() + " teams.");
    }

    private boolean isValidNumericInput(String input) {
        input = input.trim();
        if (input.isEmpty()) {
            System.out.println("Input cannot be empty.");
            return false;
        }
        try {
            int number = Integer.parseInt(input);
            return number > 0;
        } catch (NumberFormatException e) {
            System.out.println("Input must be a valid number.");
            return false;
        }
    }
}
