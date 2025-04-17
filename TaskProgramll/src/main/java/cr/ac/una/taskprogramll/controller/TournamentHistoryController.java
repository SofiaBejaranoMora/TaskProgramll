package cr.ac.una.taskprogramll.controller;

import cr.ac.una.taskprogramll.model.FileManager;
import cr.ac.una.taskprogramll.model.Sport;
import cr.ac.una.taskprogramll.model.Team;
import cr.ac.una.taskprogramll.model.Tourney;
import cr.ac.una.taskprogramll.util.AppContext;
import cr.ac.una.taskprogramll.util.Mensaje;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

public class TournamentHistoryController extends Controller implements Initializable{

    @FXML
    private AnchorPane rootPane;
    @FXML
    private VBox mainContainer;
    @FXML
    private VBox header;
    @FXML
    private Label titleLabel;
    @FXML
    private HBox filters;
    @FXML
    private MFXTextField teamSearch;
    @FXML
    private MFXComboBox<Team> teamFilter;
    @FXML
    private MFXComboBox<Sport> sportFilter;
    @FXML
    private MFXButton searchButton;
    @FXML
    private MFXButton clearButton;
    @FXML
    private HBox content;
    @FXML
    private TableView<Tourney> tournamentTable;
    @FXML
    private TableColumn<Tourney, String> nameColumn;
    @FXML
    private TableColumn<Tourney, Integer> minutesColumn;
    @FXML
    private TableColumn<Tourney, Sport> sportColumn;
    @FXML
    private TableColumn<Tourney, String> stateColumn;
    @FXML
    private TableColumn<Tourney, String> winnerColumn;
    @FXML
    private VBox detailsPanel;
    @FXML
    private Label detailsTitle;
    @FXML
    private Label positionLabel;
    @FXML
    private Label matchesLabel;
    @FXML
    private Label statsLabel;
    @FXML
    private Label generalStatsLabel;
    @FXML
    private HBox footer;
    @FXML
    private MFXButton certificateButton;
    @FXML
    private MFXButton btnParticipants;
    @FXML
    private MFXButton btnBrackets;
    @FXML
    private MFXButton btnRefresh;
    @FXML
    private ImageView certificatePreview;
    @FXML
    private ImageView lobbyIcon;

    private ObservableList<Tourney> availableTourneys = FXCollections.observableArrayList();
    private ObservableList<Team> availableTeams = FXCollections.observableArrayList();
    private final List<Sport> sportList = new ArrayList<>();
    private FileManager fileManager = new FileManager();
    private final Mensaje message = new Mensaje();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        SetupTableColumns();
        SetupTableItems();
        LoadSportFilterList();
        LoadTeamFilterList();
        LoadTourneyList();
        setupSportFilterListener();
        setupTeamSearchListener();
        setupTeamFilterListener();
        tourneySelectionListener();
        setupActionButtons();
        setupCertificateButton();
        setupRefreshButton();
        setupTeamFilterDisplay(); // Nueva función para corregir visualización
    }

    private void setupTeamFilterDisplay() {
        teamFilter.setConverter(new StringConverter<Team>() {
            @Override
            public String toString(Team team) {
                if (team != null) {
                    return team.getName();
                } else {
                    return "";
                }
            }

            @Override
            public Team fromString(String string) {
                return availableTeams.stream()
                    .filter(team -> team.getName().equals(string))
                    .findFirst()
                    .orElse(null);
            }
        });
    }

    private void SetupSportColumns() {
        sportColumn.setCellValueFactory(cellData -> {
            Tourney tourney = cellData.getValue();
            if (tourney != null) {
                return new ReadOnlyObjectWrapper<>(tourney.searchSportType());
            }
            return new ReadOnlyObjectWrapper<>(null);
        });
    }

    private void SetupStateColumns() {
        stateColumn.setCellValueFactory(cellData -> {
            Tourney tourney = cellData.getValue();
            if (tourney != null) {
                return new ReadOnlyObjectWrapper<>(tourney.returnState());
            }
            return new ReadOnlyObjectWrapper<>(null);
        });
    }

    private void SetupWinnnerColumn() {
        winnerColumn.setCellValueFactory(cellData -> {
            Tourney tourney = cellData.getValue();
            if (tourney != null) {
                if (tourney.returnState() == "Finalizado") {
                    if (tourney.getLoosersList() != null && !tourney.getLoosersList().isEmpty()) {
                        String winner = tourney.getLoosersList().getFirst().getName();
                        return new ReadOnlyObjectWrapper<>(winner);
                    } else {
                        return new ReadOnlyObjectWrapper<>("Sin ganador");
                    }
                } else {
                    return new ReadOnlyObjectWrapper<>("Pendiente");
                }
            }
            return new ReadOnlyObjectWrapper<>(null);
        });
    }

    private void SetupTableColumns() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        minutesColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        SetupWinnnerColumn();
        SetupStateColumns();
        SetupSportColumns();
    }

    private void SetupTableItems() {
        tournamentTable.setItems(availableTourneys);
    }

    private void LoadSportFilterList() {
        if (AppContext.getInstance().get("sports") == null) {
            AppContext.getInstance().set("sports", new ArrayList<Sport>());
        } 
       @SuppressWarnings("unchecked")
        List<Sport> sports = (List<Sport>) AppContext.getInstance().get("sports");
        if (sports.isEmpty()) {
            File sportFile = new File("Sport.txt");
            if (!sportFile.exists()) {
                message.show(Alert.AlertType.WARNING, "Archivo no encontrado", "Sport.txt no existe en el directorio raíz.");
            } else if (sportFile.length() == 0) {
                message.show(Alert.AlertType.WARNING, "Archivo vacío", "Sport.txt está vacío.");
            } else {
                try {
                    List<Sport> deserializedSports = fileManager.deserialization("Sport", Sport.class);
                    if (deserializedSports.isEmpty()) {
                        message.show(Alert.AlertType.WARNING, "Sin datos", "No se encontraron deportes en Sport.txt.");
                    } else {
                        sports.addAll(deserializedSports);
                    }
                } catch (Exception e) {
                    message.show(Alert.AlertType.ERROR, "Error al Cargar Deportes", "No se pudieron cargar los deportes: " + e.getMessage());
                }
            }
        }
        sportList.clear();
        sportList.addAll(sports);
        sportFilter.setItems(FXCollections.observableArrayList(sportList));
        if (sportList.isEmpty()) {
            message.show(Alert.AlertType.WARNING, "Lista vacía", "No hay deportes disponibles para el filtro.");
        }
    }

    private void LoadTeamFilterList() {
        if (AppContext.getInstance().get("teams") == null) {
            AppContext.getInstance().set("teams", new ArrayList<Team>());
        }
        @SuppressWarnings("unchecked")
        List<Team> teams = (List<Team>) AppContext.getInstance().get("teams");
        if (teams.isEmpty()) {
            File teamFile = new File("Team.txt");
            if (!teamFile.exists()) {
                message.show(Alert.AlertType.WARNING, "Archivo no encontrado", "Team.txt no existe en el directorio raíz.");
            } else if (teamFile.length() == 0) {
                message.show(Alert.AlertType.WARNING, "Archivo vacío", "Team.txt está vacío.");
            } else {
                try {
                    List<Team> deserializedTeams = fileManager.deserialization("Team", Team.class);
                    if (deserializedTeams.isEmpty()) {
                        message.show(Alert.AlertType.WARNING, "Sin datos", "No se encontraron equipos en Team.txt.");
                    } else {
                        teams.addAll(deserializedTeams);
                    }
                } catch (Exception e) {
                    message.show(Alert.AlertType.ERROR, "Error al Cargar Equipos", "No se pudieron cargar los equipos: " + e.getMessage());
                }
            }
        }
        availableTeams.clear();
        availableTeams.addAll(teams);
        teamFilter.setItems(availableTeams);
        if (availableTeams.isEmpty()) {
            message.show(Alert.AlertType.WARNING, "Lista vacía", "No hay equipos disponibles para el filtro.");
        }
    }

    private void LoadTourneyList() {
        if (AppContext.getInstance().get("tourneys") == null) {
            AppContext.getInstance().set("tourneys", new ArrayList<Tourney>());
        }
        @SuppressWarnings("unchecked")
        List<Tourney> tourneys = (List<Tourney>) AppContext.getInstance().get("tourneys");
        if (tourneys.isEmpty()) {
            File tourneyFile = new File("Tourney.txt");
            if (!tourneyFile.exists()) {
                message.show(Alert.AlertType.WARNING, "Archivo no encontrado", "Tourney.txt no existe en el directorio raíz.");
            } else if (tourneyFile.length() == 0) {
                message.show(Alert.AlertType.WARNING, "Archivo vacío", "Tourney.txt está vacío.");
            } else {
                try {
                    List<Tourney> deserializedTourneys = fileManager.deserialization("Tourney", Tourney.class);
                    if (deserializedTourneys.isEmpty()) {
                        message.show(Alert.AlertType.WARNING, "Sin datos", "No se encontraron torneos en Tourney.txt.");
                    } else {
                        tourneys.addAll(deserializedTourneys);
                    }
                } catch (Exception e) {
                    message.show(Alert.AlertType.ERROR, "Error al Cargar Torneos", "No se pudieron cargar los torneos: " + e.getMessage());
                }
            }
        }
        availableTourneys.clear();
        availableTourneys.addAll(tourneys);
        tournamentTable.setItems(FXCollections.observableArrayList(availableTourneys));
        if (availableTourneys.isEmpty()) {
            message.show(Alert.AlertType.WARNING, "Lista vacía", "No hay torneos disponibles para mostrar.");
        }
    }

    private void setupSportFilterListener() {
        sportFilter.getSelectionModel().selectedItemProperty().addListener((obs, oldSport, newSport) -> {
            applyFilters();
        });
    }

    private void setupTeamSearchListener() {
        teamSearch.textProperty().addListener((obs, oldText, newText) -> {
            if (newText == null || newText.isEmpty()) {
                teamFilter.setItems(availableTeams);
            } else {
                ObservableList<Team> filteredTeams = FXCollections.observableArrayList();
                for (Team team : availableTeams) {
                    if (team.getName().toLowerCase().contains(newText.toLowerCase()) || 
                        String.valueOf(team.getId()).equals(newText)) {
                        filteredTeams.add(team);
                    }
                }
                teamFilter.setItems(filteredTeams);
            }
            applyFilters();
        });
        searchButton.setOnAction(event -> applyFilters());
        clearButton.setOnAction(event -> {
            teamSearch.clear();
            teamFilter.getSelectionModel().clearSelection();
            sportFilter.getSelectionModel().clearSelection();
            tournamentTable.setItems(availableTourneys);
        });
    }

    private void setupTeamFilterListener() {
        teamFilter.getSelectionModel().selectedItemProperty().addListener((obs, oldTeam, newTeam) -> {
            applyFilters();
            updateDetails();
        });
    }

    private void applyFilters() {
        String query ;
        if(teamSearch.getText() == null){
            query = "";
        }else{
            query= teamSearch.getText().trim().toLowerCase();
        }
        Sport selectedSport = sportFilter.getSelectionModel().getSelectedItem();
        Team selectedTeam = teamFilter.getSelectionModel().getSelectedItem();
        ObservableList<Tourney> filtered = FXCollections.observableArrayList();

        for (Tourney tourney : availableTourneys) {
            boolean matchesTeamSearch = query.isEmpty() || 
                tourney.getTeamList().stream().anyMatch(team -> 
                    team.getName().toLowerCase().contains(query) || 
                    String.valueOf(team.getId()).equals(query));
            
            boolean matchesTeamFilter = selectedTeam == null || 
                tourney.getTeamList().contains(selectedTeam);
            
            boolean matchesSport = selectedSport == null || 
                tourney.getSportTypeId() == selectedSport.getId();
            
            if (matchesTeamSearch && matchesTeamFilter && matchesSport) {
                filtered.add(tourney);
            }
        }
        tournamentTable.setItems(filtered);
    }

    private void tourneySelectionListener() {
        tournamentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldTourney, newTourney) -> {
            updateDetails();
        });
    }

    private void updateDetails() {
        Tourney selectedTourney = tournamentTable.getSelectionModel().getSelectedItem();
        Team selectedTeam = teamFilter.getSelectionModel().getSelectedItem();
        
        if (selectedTourney != null && selectedTeam != null) {
            showTourneyInfo(selectedTourney, selectedTeam);
        } else {
            detailsTitle.setText("Seleccioná un torneo y un equipo");
            positionLabel.setText("");
            matchesLabel.setText("");
            statsLabel.setText("");
            generalStatsLabel.setText("");
            btnParticipants.setDisable(true);
            btnBrackets.setDisable(true);
            certificateButton.setDisable(true);
        }
    }

    void showTourneyInfo(Tourney tourney, Team team) {
        detailsTitle.setText(tourney.getName() + " - " + team.getName());
        
        // Ranking por torneo
        String ranking = "N/A";
        List<Team> rankingList = tourney.returnRanking();
        if (rankingList != null && !rankingList.isEmpty()) {
            int position = rankingList.indexOf(team) + 1;
            if (position > 0) {
                ranking = position + "º";
            } else {
                ranking = "No clasificado";
            }
        }
        positionLabel.setText("Ranking en torneo: " + ranking);
        
        // Partidos del equipo
        matchesLabel.setText("Partidos: No implementado");
        
        // Estadísticas por torneo
        statsLabel.setText("Estadísticas: No implementado");
        
        // Estadísticas generales
        String generalStats = "Estadísticas generales:\n";
        int tournamentsPlayed = 0;
        for (Tourney t : availableTourneys) {
            if (t.getTeamList().contains(team)) {
                tournamentsPlayed++;
            }
        }
        generalStats += "- Torneos jugados: " + tournamentsPlayed;
        generalStatsLabel.setText(generalStats);
        
        btnParticipants.setDisable(false);
        btnBrackets.setDisable(false);
        certificateButton.setDisable(tourney.returnState() != "Finalizado");
    }

    private void setupActionButtons() {
        btnParticipants.setOnAction(event -> {
            Tourney selected = tournamentTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showParticipants(selected);
            }
        });
        btnBrackets.setOnAction(event -> {
            // Vacío por ahora
        });
    }

    private void showParticipants(Tourney tourney) {
        StringBuilder participants = new StringBuilder("Equipos participantes:\n");
        if (tourney.getTeamList() != null && !tourney.getTeamList().isEmpty()) {
            for (Team team : tourney.getTeamList()) {
                participants.append("- ").append(team.getName()).append(" (ID: ").append(team.getId()).append(")\n");
            }
        } else {
            participants.append("No hay equipos registrados.");
        }
        message.show(Alert.AlertType.INFORMATION, "Participantes - " + tourney.getName(), participants.toString());
    }

    private void showBrackets(Tourney tourney) {
        // Vacío por ahora
    }

    private void setupCertificateButton() {
        certificateButton.setOnAction(event -> {
            // Vacío por ahora
        });
    }

    private void setupRefreshButton() {
        btnParticipants.setDisable(true);
        btnBrackets.setDisable(true);
        btnRefresh.setOnAction(event -> {
            availableTourneys.clear();
            availableTeams.clear();
            sportList.clear();
            LoadTourneyList();
            LoadTeamFilterList();
            LoadSportFilterList();
            teamSearch.clear();
            teamFilter.getSelectionModel().clearSelection();
            sportFilter.getSelectionModel().clearSelection();
            message.show(Alert.AlertType.INFORMATION, "Actualizado", "Lista de torneos, equipos y deportes recargada.");
        });
    }

    @Override
    public void initialize() {
    }
}