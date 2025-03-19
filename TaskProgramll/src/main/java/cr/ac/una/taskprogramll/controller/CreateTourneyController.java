package cr.ac.una.taskprogramll.controller;

import cr.ac.una.taskprogramll.model.FileManager;
import cr.ac.una.taskprogramll.model.Sport;
import cr.ac.una.taskprogramll.model.Team;
import cr.ac.una.taskprogramll.model.Tourney;
import io.github.palexdev.materialfx.controls.MFXSlider;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.io.File;
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

public class CreateTourneyController implements Initializable {

    @FXML private Button btnCancel;
    @FXML private Button btnStart;
    @FXML private Button btnAdjustTeams;
    @FXML private MFXTextField txtTourneyName;
    @FXML private MFXTextField txtMatchTime;
    @FXML private ComboBox<Sport> tglLstSportType;
    @FXML private TableView<Team> tblTeams; // Equipos disponibles
    @FXML private TableView<Team> tblTeams1; // Equipos elegidos
    @FXML private TableColumn<Team, String> colTeamName; // Columna de equipos disponibles
    @FXML private TableColumn<Team, String> colTeamName1; // Columna de equipos elegidos
    @FXML private MFXSlider sliderTeamCount;

    private final FileManager fileManager = new FileManager();
    private final ObservableList<Team> allTeams = FXCollections.observableArrayList(); // Todos los equipos sin filtrar
    private final ObservableList<Team> availableTeams = FXCollections.observableArrayList();
    private final ObservableList<Team> selectedTeams = FXCollections.observableArrayList();
    private final List<Sport> sportList = new ArrayList<>();
    private final List<Tourney> tourneyList = new ArrayList<>();
    private File sportFile;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            setupTableColumns();
            setupTableItems();
            configureSlider();
            configureTeamSelection();
            loadSportList();
            loadTeamList();
            setupSportFilter();
        } catch (Exception e) {
            handleError(e); // Corrección aquí
        }
    }

    private void setupTableColumns() {
        colTeamName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colTeamName1.setCellValueFactory(new PropertyValueFactory<>("name"));
    }

    private void setupTableItems() {
        tblTeams.setItems(availableTeams);
        tblTeams1.setItems(selectedTeams);
    }

    private void configureSlider() {
        sliderTeamCount.setMin(32);
        sliderTeamCount.setMax(64);
        sliderTeamCount.setValue(32);
        sliderTeamCount.valueProperty().addListener((observable, oldValue, newValue) -> {
            // Debug opcional: System.out.println("Slider value changed to: " + newValue.intValue());
        });
    }

    private void configureTeamSelection() {
        tblTeams.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                Team selectedTeam = tblTeams.getSelectionModel().getSelectedItem();
                if (selectedTeam != null && !selectedTeams.contains(selectedTeam)) {
                    selectedTeams.add(selectedTeam);
                    availableTeams.remove(selectedTeam);
                }
            }
        });

        tblTeams1.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                Team selectedTeam = tblTeams1.getSelectionModel().getSelectedItem();
                if (selectedTeam != null) {
                    availableTeams.add(selectedTeam);
                    selectedTeams.remove(selectedTeam);
                }
            }
        });
    }

    private void loadSportList() {
        sportFile = new File("Sport.txt");
        if (sportFile.exists() && sportFile.length() > 0) {
            try {
                sportList.addAll(fileManager.deserialization("Sport", Sport.class));
                tglLstSportType.setItems(FXCollections.observableArrayList(sportList));
                // Debug opcional: System.out.println("Sport list loaded successfully: " + sportList.size() + " sports.");
            } catch (Exception e) {
                handleError(e);
            }
        } else {
            System.err.println("Sport.txt file does not exist or is empty.");
        }
    }

    private void loadTeamList() {
        File teamFile = new File("Team.txt");
        if (teamFile.exists() && teamFile.length() > 0) {
            try {
                allTeams.addAll(fileManager.deserialization("Team", Team.class));
                filterTeamsBySport(null); // Inicializa con todos los equipos
                // Debug opcional: System.out.println("Team list loaded successfully: " + allTeams.size() + " teams.");
            } catch (Exception e) {
                handleError(e);
            }
        } else {
            System.err.println("Team.txt file does not exist or is empty.");
        }
    }

    private void filterTeamsBySport(Sport selectedSport) {
    // Lista temporal para los equipos seleccionados que cumplen el criterio
    ObservableList<Team> filteredSelectedTeams = FXCollections.observableArrayList();
    
    // Filtrar selectedTeams: agregar solo los equipos que coincidan con el deporte seleccionado
    if (selectedSport != null) {
        int selectedSportId = selectedSport.getId(); // Asumiendo que getId() devuelve un int
        for (Team team : selectedTeams) {
            if (team.getSportType() != null && team.getSportType().getId() == selectedSportId) {
                filteredSelectedTeams.add(team);
            }
        }
        selectedTeams.setAll(filteredSelectedTeams); // Reemplazar la lista con los equipos filtrados
    }

    // Actualizar availableTeams
    ObservableList<Team> filteredAvailableTeams = FXCollections.observableArrayList();
    for (Team team : allTeams) {
        // Si no hay deporte seleccionado, agregar todos los equipos que no estén en selectedTeams
        if (selectedSport == null) {
            if (!selectedTeams.contains(team)) {
                filteredAvailableTeams.add(team);
            }
        } else {
            // Si hay un deporte seleccionado, agregar solo los equipos que coincidan y no estén en selectedTeams
            int teamSportId = (team.getSportType() != null) ? team.getSportType().getId() : -1; // Usar -1 como valor por defecto si es null
            if (teamSportId == selectedSport.getId() && !selectedTeams.contains(team)) {
                filteredAvailableTeams.add(team);
            }
        }
    }
    availableTeams.setAll(filteredAvailableTeams);

    // Refrescar las tablas
    tblTeams.refresh();
    tblTeams1.refresh();
}
    private void setupSportFilter() {
        tglLstSportType.valueProperty().addListener((observable, oldValue, newValue) -> {
            filterTeamsBySport(newValue);
        });
    }

    @FXML
    private void adjustSelectedTeamsSlice(ActionEvent event) {
        try {
            int desiredCount = Math.clamp((int) sliderTeamCount.getValue(), 32, 64);

            while (selectedTeams.size() > desiredCount) {
                availableTeams.add(selectedTeams.remove(selectedTeams.size() - 1));
            }

            for (Team team : new ArrayList<>(availableTeams)) {
                if (selectedTeams.size() >= desiredCount) break;
                if (!selectedTeams.contains(team)) {
                    selectedTeams.add(team);
                    availableTeams.remove(team);
                }
            }
            // Debug opcional: System.out.println("Teams adjusted to: " + selectedTeams.size());
        } catch (Exception e) {
            handleError(e);
        }
        //Michelle
    }

    private String checkInputs(String name, String time, ObservableList<Team> teams, Sport selectedSport) {
        if (name.isEmpty() || time.isEmpty()) return "All fields are required.";
        if (!isValidNumericInput(time)) return "Match time must be a valid number.";
        if (teams.isEmpty()) return "You must select at least one team.";
        if (selectedSport == null) return "You must select a sport type.";
        return null;
    }

    @FXML
    private void createTourney(ActionEvent event) {
        try {
            String tourneyName = txtTourneyName.getText().trim();
            String matchTime = txtMatchTime.getText().trim();
            Sport selectedSport = tglLstSportType.getValue();

            String errorMessage = checkInputs(tourneyName, matchTime, selectedTeams, selectedSport);
            if (errorMessage != null) {
                System.err.println(errorMessage);
                return;
            }

            int id = generateTourneyId();
            Tourney newTourney = new Tourney(id, Integer.parseInt(matchTime), selectedSport, new ArrayList<>(selectedTeams));
            tourneyList.add(newTourney);
            System.out.println("Tourney created successfully: " + tourneyName + " with " + selectedTeams.size() + " teams.");
            resetInputs();
        } catch (Exception e) {
            handleError(e);
        }
    }

    private int generateTourneyId() {
        if(tourneyList.isEmpty())
            return 1;
        return tourneyList.size() + 1;
    }

    private boolean isValidNumericInput(String input) {
        if (input.trim().isEmpty()) {
            System.err.println("Input cannot be empty.");
            return false;
        }
        try {
            return Integer.parseInt(input) > 0;
        } catch (NumberFormatException e) {
            System.err.println("Input must be a valid number.");
            return false;
        }
    }

    private void resetInputs() {
        txtTourneyName.clear();
        txtMatchTime.clear();
        selectedTeams.clear();
        tglLstSportType.getSelectionModel().clearSelection();
        System.out.println("Fields reset after tourney creation.");
    }

    private void handleError(Exception e) {
        System.err.println("Error: " + e.getMessage());
        e.printStackTrace();
    }
}