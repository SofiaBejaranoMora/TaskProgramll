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

    private List<Tourney> tourneyList = new ArrayList<>();

    @FXML
    private Button btnCancel;
    @FXML
    private Button btnStart;
    @FXML
    private Button btnAdjustTeams; // Nuevo botón para ajustar los equipos seleccionados
    @FXML
    private MFXTextField txtTourneyName;
    @FXML
    private MFXTextField txtMatchTime;
    private MFXTextField txtTeamCount; // Nuevo campo para ingresar la cantidad deseada de equipos
    @FXML
    private ComboBox<Sport> tglLstSportType;
    @FXML
    private TableView<Team> tblTeams;
    @FXML
    private TableColumn<Team, String> colTeamName;
    @FXML
    private TableColumn<Team, String> colSelected;

    private final ObservableList<Team> teamList = FXCollections.observableArrayList();
    private final ObservableList<Team> selectedTeams = FXCollections.observableArrayList(); // Lista de equipos seleccionados
    @FXML
    private MFXSlider sliderTeamCount;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Configurar las columnas
        colTeamName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colSelected.setCellValueFactory(cellData -> {
            Team team = cellData.getValue(); // Obtener el equipo
            String status;

            if (selectedTeams.contains(team)) {
                status = "Seleccionado";
            } else {
                status = "No seleccionado";
            }

            return new SimpleStringProperty(status); // Retornar un texto observable
        });

        // Asignar lista observable a la tabla
        tblTeams.setItems(teamList);

        // Clic para seleccionar
        tblTeams.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                Team clickedTeam = tblTeams.getSelectionModel().getSelectedItem();
                if (clickedTeam != null && !selectedTeams.contains(clickedTeam)) {
                    selectedTeams.add(clickedTeam);
                    tblTeams.refresh();
                    System.out.println("Equipo movido a 'Seleccionados': " + clickedTeam.getName());
                }
            }
        });

        // Doble clic para deseleccionar
        tblTeams.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Team clickedTeam = tblTeams.getSelectionModel().getSelectedItem();
                if (clickedTeam != null && selectedTeams.contains(clickedTeam)) {
                    selectedTeams.remove(clickedTeam);
                    tblTeams.refresh();
                    System.out.println("Equipo movido a 'Seleccionables': " + clickedTeam.getName());
                }
            }
        });
    }

    // Botón para seleccionar todos
    private void selectAllTeams(ActionEvent event) {
        for (Team team : teamList) {
            if (!selectedTeams.contains(team)) {
                selectedTeams.add(team);
            }
        }
        tblTeams.refresh();
        System.out.println("Todos los equipos han sido seleccionados.");
    }

    // Botón para ajustar la cantidad de equipos seleccionados
    private void adjustSelectedTeams(ActionEvent event) {
        String input = txtTeamCount.getText().trim(); // Obtener el número ingresado
        if (isValidNumericInput(input)) {
            int desiredCount = Integer.parseInt(input);

            // Si ya hay demasiados equipos seleccionados, eliminar los sobrantes
            while (selectedTeams.size() > desiredCount) {
                selectedTeams.remove(selectedTeams.size() - 1); // Remover del final
            }

            // Si faltan equipos, agregar desde los disponibles
            for (Team team : teamList) {
                if (selectedTeams.size() >= desiredCount) {
                    break; // Detener si ya se alcanzó la cantidad deseada
                }
                if (!selectedTeams.contains(team)) {
                    selectedTeams.add(team);
                }
            }

            tblTeams.refresh(); // Refrescar la tabla
            System.out.println("Ajuste completado: " + selectedTeams.size() + " equipos seleccionados.");
        } else {
            System.out.println("Por favor, ingresa un número válido para ajustar.");
        }
    }

    // Método para crear el torneo
    @FXML
    private void createTourney(ActionEvent event) {
        String tourneyName = txtTourneyName.getText().trim();
        String matchTime = txtMatchTime.getText().trim();

        // Validar entradas
        if (tourneyName.isEmpty() || matchTime.isEmpty()) {
            System.out.println("Advertencia: Todos los campos son obligatorios.");
            return;
        }
        if (!isValidNumericInput(matchTime)) {
            System.out.println("Advertencia: El tiempo debe ser un número válido.");
            return;
        }

        if (selectedTeams.isEmpty()) {
            System.out.println("Advertencia: Debes seleccionar al menos un equipo.");
            return;
        }

        // Crear el torneo
        int id = tourneyList.size() + 1;
        Sport selectedSport = tglLstSportType.getValue();
        List<Team> teamsForTourney = new ArrayList<>(selectedTeams);

        Tourney newTourney = new Tourney(id, Integer.parseInt(matchTime), selectedSport, teamsForTourney);
        tourneyList.add(newTourney);

        System.out.println("Torneo creado con éxito: " + tourneyName + ", Equipos seleccionados: " + teamsForTourney.size());
    }

    // Método auxiliar para validar números
    private boolean isValidNumericInput(String input) {
        input = input.trim();
        if (input.isEmpty()) {
            System.out.println("Advertencia: La entrada no puede estar vacía.");
            return false;
        }
        try {
            int number = Integer.parseInt(input);
            return number > 0; // Solo permitimos números mayores a 0
        } catch (NumberFormatException e) {
            System.out.println("Advertencia: La entrada debe ser un número válido.");
            return false;
        }
    }

    @FXML
    private void adjustSelectedTeamsSlice(ActionEvent event) {
    }
}
