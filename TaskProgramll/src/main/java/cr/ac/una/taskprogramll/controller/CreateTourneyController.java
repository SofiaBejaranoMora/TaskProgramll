package cr.ac.una.taskprogramll.controller;

import cr.ac.una.taskprogramll.model.FileManager;
import cr.ac.una.taskprogramll.model.Sport;
import cr.ac.una.taskprogramll.model.Team;
import cr.ac.una.taskprogramll.model.Tourney;
import cr.ac.una.taskprogramll.util.AppContext;
import cr.ac.una.taskprogramll.util.FlowController;
import cr.ac.una.taskprogramll.util.Mensaje;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXSlider;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class CreateTourneyController extends Controller implements Initializable {

    private final FileManager fileManager = new FileManager();
    private final Mensaje message = new Mensaje();
    private final ObservableList<Team> allTeams = FXCollections.observableArrayList();
    private final ObservableList<Team> availableTeams = FXCollections.observableArrayList();
    private final ObservableList<Team> selectedTeams = FXCollections.observableArrayList();
    private final List<Sport> sportList = new ArrayList<>();
    
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
    private MFXComboBox<Sport> tglLstSportType;
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

    @FXML
    private void OnActionBtnCancel(ActionEvent event) {
        ClearPanel();
        FlowController.getInstance().goViewInStage("Lobby", (Stage) btnCancel.getScene().getWindow());
    }

    @FXML
    private void adjustSelectedTeamsSlice(ActionEvent event) {
        try {
            int desiredCount = Math.clamp((int) sliderTeamCount.getValue(), 2, 64);
            int totalTeamsAvailable = selectedTeams.size() + availableTeams.size();
            if (totalTeamsAvailable < desiredCount) {
                message.show(Alert.AlertType.ERROR, "Error de Equipos", String.format(
                        "No hay suficientes equipos disponibles. Se necesitan %d equipos, pero solo hay %d.",
                        desiredCount, totalTeamsAvailable
                ));
                return;
            }

            while (selectedTeams.size() > desiredCount) {
                availableTeams.add(selectedTeams.remove(selectedTeams.size() - 1));
            }

            Random random = new Random();
            while (selectedTeams.size() < desiredCount && !availableTeams.isEmpty()) {
                int randomIndex = random.nextInt(availableTeams.size());
                Team randomTeam = availableTeams.get(randomIndex);
                selectedTeams.add(randomTeam);
                availableTeams.remove(randomIndex);
            }

            System.out.println("Teams adjusted to: " + selectedTeams.size());
        } catch (Exception e) {
            message.show(Alert.AlertType.ERROR, "Error al Ajustar Equipos", "No se pudieron ajustar los equipos: " + e.getMessage());
        }
    }

    @FXML
    private void createTourney(ActionEvent event) {
        try {
            String tourneyName = txtTourneyName.getText().trim();
            String matchTime = txtMatchTime.getText().trim();
            Sport selectedSport = tglLstSportType.getValue();
            String errorMessage = CheckInputs(tourneyName, matchTime, selectedTeams, selectedSport);
            if (errorMessage != null) {
                message.show(Alert.AlertType.ERROR, "Error de Validación", errorMessage);
                return;
            }

            int id = GenerateTourneyId();
            Tourney newTourney = new Tourney(id, tourneyName, Integer.parseInt(matchTime), selectedSport.getId(), new ArrayList<>(selectedTeams));
            List<Tourney> tourneys = (List<Tourney>) AppContext.getInstance().get("tourneys");
            if (tourneys == null) {
                tourneys = new ArrayList<>();
            }
            tourneys.add(newTourney);
            AppContext.getInstance().set("tourneys", tourneys);
            fileManager.serialization(tourneys, "Tourney");
            System.out.println("Tourney created and saved: " + tourneyName + " with " + selectedTeams.size() + " teams.");
            ClearPanel();
        } catch (Exception e) {
            message.show(Alert.AlertType.ERROR, "Error al Crear Torneo", "No se pudo crear el torneo: " + e.getMessage());
        }
    }

    private boolean IsIdUsed(int id) {
        @SuppressWarnings("unchecked")
        List<Tourney> tourneys = (List<Tourney>) AppContext.getInstance().get("tourneys");
        if (tourneys == null) {
            return false;
        }
        for (Tourney tourney : tourneys) {
            if (tourney.getId() == id) {
                return true;
            }
        }
        return false;
    }

    private boolean IsValidNumericInput(String input) {
        if (input.trim().isEmpty()) {
            return false;
        }
        try {
            int value = Integer.parseInt(input);
            return value > 0 && value <= 180;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private String CheckInputs(String name, String time, ObservableList<Team> teams, Sport selectedSport) {
        if (name.isEmpty() || time.isEmpty()) {
            return "Todos los campos son obligatorios.";
        }
        if (!IsValidNumericInput(time)) {
            return "El tiempo del partido debe ser un número válido entre 1 y 180 minutos.";
        }
        if (teams.isEmpty()) {
            return "Debes seleccionar al menos un equipo.";
        }
        if (teams.size() < 1) {
            return "El minimo de equipos en un torneo es 2.";
        }
        if (selectedSport == null) {
            return "Debes seleccionar un tipo de deporte.";
        }
        return null;
    }

    private int GenerateTourneyId() {
        Random random = new Random();
        int newId = Math.abs(random.nextInt());
        while (IsIdUsed(newId)) {
            newId = Math.abs(random.nextInt());
        }
        return newId;
    }

    private void LoadSportList() {
        if (AppContext.getInstance().get("sports") == null) {
            AppContext.getInstance().set("sports", new ArrayList<Sport>());
        }
        @SuppressWarnings("unchecked")
        List<Sport> sports = (List<Sport>) AppContext.getInstance().get("sports");
        if (sports.isEmpty()) {
            File sportFile = new File("Sport.txt");
            if (sportFile.exists() && sportFile.length() > 0) {
                try {
                    sports.addAll(fileManager.deserialization("Sport", Sport.class));
                } catch (Exception e) {
                    message.show(Alert.AlertType.ERROR, "Error al Cargar Deportes", "No se pudieron cargar los deportes: " + e.getMessage());
                }
            }
        }
        sportList.addAll(sports);
        tglLstSportType.setItems(FXCollections.observableArrayList(sportList));
    }

    private void LoadTeamList() {
        if (AppContext.getInstance().get("teams") == null) {
            AppContext.getInstance().set("teams", new ArrayList<Team>());
        }
        @SuppressWarnings("unchecked")
        List<Team> teams = (List<Team>) AppContext.getInstance().get("teams");
        if (teams.isEmpty()) {
            File teamFile = new File("Team.txt");
            if (teamFile.exists() && teamFile.length() > 0) {
                try {
                    teams.addAll(fileManager.deserialization("Team", Team.class));
                } catch (Exception e) {
                    message.show(Alert.AlertType.ERROR, "Error al Cargar Equipos", "No se pudieron cargar los equipos: " + e.getMessage());
                }
            }
        }
        allTeams.addAll(teams);
        FilterTeamsBySport(null);
    }

    private void LoadTourneyList() {
        File tourneyFile = new File("Tourney.txt");
        if (tourneyFile.exists() && tourneyFile.length() > 0) {
            List<Tourney> loadedTourneys = fileManager.deserialization("Tourney", Tourney.class);
            if (loadedTourneys != null) {
                AppContext.getInstance().set("tourneys", loadedTourneys);
                System.out.println("Torneos cargados desde Tourney.txt: " + loadedTourneys.size());
            } else {
                AppContext.getInstance().set("tourneys", new ArrayList<Tourney>());
                System.out.println("No se pudieron cargar torneos, iniciando con lista vacía.");
            }
        } else {
            AppContext.getInstance().set("tourneys", new ArrayList<Tourney>());
            System.out.println("No hay archivo Tourney.txt, iniciando con lista vacía.");
        }
    }

    private void FilterTeamsBySport(Sport selectedSport) {
        if (selectedSport != null) {
            selectedTeams.removeIf(team -> team.getIdSportType() != selectedSport.getId());
        }

        ObservableList<Team> filteredAvailableTeams = FXCollections.observableArrayList();
        for (Team team : allTeams) {
            if (selectedSport == null) {
                if (!selectedTeams.contains(team)) {
                    filteredAvailableTeams.add(team);
                }
            } else {
                int teamSportId = team.getIdSportType();
                if (teamSportId == selectedSport.getId() && !selectedTeams.contains(team)) {
                    filteredAvailableTeams.add(team);
                }
            }
        }
        availableTeams.setAll(filteredAvailableTeams);

        tblTeams.refresh();
        tblTeams1.refresh();
    }

    private void SetupSportFilter() {
        tglLstSportType.valueProperty().addListener((observable, oldValue, newValue) -> {
            FilterTeamsBySport(newValue);
        });
    }

    private void SetupTableColumns() {
        colTeamName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colTeamName1.setCellValueFactory(new PropertyValueFactory<>("name"));
    }

    private void SetupTableItems() {
        tblTeams.setItems(availableTeams);
        tblTeams1.setItems(selectedTeams);
    }

    private void ConfigureSlider() {
        sliderTeamCount.setMin(2);
        sliderTeamCount.setMax(64);
        sliderTeamCount.setValue(2);
    }

    private void ConfigureTeamSelection() {
        tblTeams.setOnMouseClicked(event -> {
            Team selectedTeam = tblTeams.getSelectionModel().getSelectedItem();
            if (selectedTeam != null && !selectedTeams.contains(selectedTeam)) {
                selectedTeams.add(selectedTeam);
                availableTeams.remove(selectedTeam);
            }
        });

        tblTeams1.setOnMouseClicked(event -> {
            Team selectedTeam = tblTeams1.getSelectionModel().getSelectedItem();
            if (selectedTeam != null) {
                availableTeams.add(selectedTeam);
                selectedTeams.remove(selectedTeam);
            }
        });
    }

    private void ClearPanel() {
        txtTourneyName.clear();
        txtMatchTime.clear();
        selectedTeams.clear();
        tglLstSportType.getSelectionModel().clearSelection();
        FilterTeamsBySport(null);
        System.out.println("Fields reset after tourney creation.");
    }

    public void InitialConditionsPanel() {
        SetupTableColumns();
        SetupTableItems();
        ConfigureSlider();
        ConfigureTeamSelection();
        LoadSportList();
        LoadTeamList();
        LoadTourneyList(); // Carga simple de torneos
        SetupSportFilter();
    }

    public void customInitialize() {
        InitialConditionsPanel();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {}

    @Override
    public void initialize() {
    }
}
