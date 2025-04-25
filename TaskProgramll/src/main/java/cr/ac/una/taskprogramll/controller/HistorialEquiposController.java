package cr.ac.una.taskprogramll.controller;
import javafx.application.Platform;

import cr.ac.una.taskprogramll.model.FileManager;
import cr.ac.una.taskprogramll.model.Game;
import cr.ac.una.taskprogramll.model.MatchDetails;
import cr.ac.una.taskprogramll.model.Sport;
import cr.ac.una.taskprogramll.model.Team;
import cr.ac.una.taskprogramll.model.Tourney;
import cr.ac.una.taskprogramll.util.AppContext;
import cr.ac.una.taskprogramll.util.FlowController;
import cr.ac.una.taskprogramll.util.Mensaje;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/**
 * FXML Controller class
 *
 * @author Michelle Wittingham
 */
public class HistorialEquiposController extends Controller implements Initializable {

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
    private TableView<Team> teamTable;
    @FXML
    private TableColumn<Team, String> teamNameColumn;
    @FXML
    private TableColumn<Team, String> teamRankingColumn;
    @FXML
    private TableColumn<Team, Integer> teamPointsColumn;
    @FXML
    private Label selectedTeamLabel;
    @FXML
    private TableView<Tourney> tourneyTable;
    @FXML
    private TableColumn<Tourney, String> tourneyNameColumn;
    @FXML
    private TableColumn<Tourney, Integer> tourneyMinutesColumn;
    @FXML
    private TableColumn<Tourney, String> tourneySportColumn;
    @FXML
    private TableColumn<Tourney, String> tourneyStateColumn;
    @FXML
    private TableColumn<Tourney, String> tourneyPositionColumn;
    @FXML
    private Accordion matchAccordion;
    @FXML
    private Canvas matchGoalsCanvas;
    @FXML
    private Label totalPointsLabel;
    @FXML
    private Label totalMatchesLabel;
    @FXML
    private Label generalRankingLabel;
    @FXML
    private Label tournamentsLabel;
    @FXML
    private Canvas pointsPerMatchCanvas;
    @FXML
    private TableView<TourneyStats> tourneyStatsTable;
    @FXML
    private TableColumn<TourneyStats, String> tourneyStatsNameColumn;
    @FXML
    private TableColumn<TourneyStats, Integer> tourneyStatsPointsColumn;
    @FXML
    private TableColumn<TourneyStats, Integer> tourneyStatsGoalsColumn;
    @FXML
    private TableColumn<TourneyStats, String> tourneyStatsRankingColumn;
    @FXML
    private Canvas tourneyPointsPerMatchCanvas;
    @FXML
    private Canvas globalRankingCanvas; 

    private Team updatedTeam;
    private Team selectedTeam;
    private Tourney selectedTourney;
    private final Mensaje message = new Mensaje();
    private FileManager fileManager = new FileManager();
    private ObservableList<Team> availableTeams = FXCollections.observableArrayList();
    private ObservableList<Tourney> availableTourneys = FXCollections.observableArrayList();
    private final List<Sport> sportList = new ArrayList<>();
    @FXML
    private VBox pointsPerTourneyLegend;
    @FXML
    private MFXButton btnBackMenu;

    @FXML
    private void onActionBtnBackMenu(ActionEvent event) {
        FlowController.getInstance().goViewInStage("Lobby", (Stage) btnBackMenu.getScene().getWindow());
    }

    /**
     * Clase auxiliar para la tabla de estadísticas por torneo
     */
    public static class TourneyStats {

        private final StringProperty tourneyName = new SimpleStringProperty();
        private final IntegerProperty points = new SimpleIntegerProperty();
        private final IntegerProperty goals = new SimpleIntegerProperty();
        private final StringProperty ranking = new SimpleStringProperty();

        public TourneyStats(String tourneyName, int points, int goals, String ranking) {
            this.tourneyName.set(tourneyName);
            this.points.set(points);
            this.goals.set(goals);
            this.ranking.set(ranking);
        }

        public StringProperty tourneyNameProperty() {
            return tourneyName;
        }

        public IntegerProperty pointsProperty() {
            return points;
        }

        public IntegerProperty goalsProperty() {
            return goals;
        }

        public StringProperty rankingProperty() {
            return ranking;
        }
    }

    public void initialize(URL url, ResourceBundle rb) {
        setupTableColumns();
        loadData();
        loadTourneys();
        loadSports();
        setupTeamSelectionListener();
        setupTourneySelectionListener(); // Añadir el listener para la selección de torneos
        setupTeamFilterDisplay();
        setupFilters();
    }

    private void setupTableColumns() {
        setupTeamColumns();
        setupTourneyColumns();
        setupTourneyStatsColumns();
    }

    private void setupTeamColumns() {
        TableColumn<Team, Integer> teamIdColumn = new TableColumn<>("ID");
        teamIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Team, String> teamNameColumn = new TableColumn<>("Nombre");
        teamNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Team, Integer> teamPointsColumn = new TableColumn<>("Puntos");
        teamPointsColumn.setCellValueFactory(cellData -> {
            Team team = cellData.getValue();
            int globalPoints = calculateGlobalPoints(team);
            return javafx.beans.binding.Bindings.createObjectBinding(() -> globalPoints);
        });

        TableColumn<Team, String> teamRankingColumn = new TableColumn<>("Ranking");
        teamRankingColumn.setCellValueFactory(cellData -> {
            Team team = cellData.getValue();
            List<Team> sortedTeams = new ArrayList<>(availableTeams);
            sortedTeams.sort((t1, t2) -> Integer.compare(calculateGlobalPoints(t2), calculateGlobalPoints(t1)));
            int rank = sortedTeams.indexOf(team) + 1;
            return new SimpleStringProperty("Posición: " + rank);
        });

        teamTable.getColumns().clear();
        teamTable.getColumns().addAll(teamIdColumn, teamNameColumn, teamPointsColumn, teamRankingColumn);
    }

    private void setupTourneyColumns() {
        TableColumn<Tourney, Integer> tourneyIdColumn = new TableColumn<>("ID");
        tourneyIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Tourney, String> tourneyNameColumn = new TableColumn<>("Nombre");
        tourneyNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Tourney, Integer> tourneyMinutesColumn = new TableColumn<>("Minutos");
        tourneyMinutesColumn.setCellValueFactory(new PropertyValueFactory<>("time"));

        TableColumn<Tourney, String> tourneySportColumn = new TableColumn<>("Deporte");
        tourneySportColumn.setCellValueFactory(cellData -> {
            Tourney tourney = cellData.getValue();
            Sport sport = sportList.stream()
                    .filter(s -> s.getId() == tourney.getSportTypeId())
                    .findFirst()
                    .orElse(null);
            String sportName = (sport != null) ? sport.getName() : "Sin deporte";
            return new SimpleStringProperty(sportName);
        });

        TableColumn<Tourney, String> tourneyStateColumn = new TableColumn<>("Estado");
        tourneyStateColumn.setCellValueFactory(cellData -> {
            Tourney tourney = cellData.getValue();
            return new SimpleStringProperty(tourney.returnState());
        });

        TableColumn<Tourney, String> tourneyPositionColumn = new TableColumn<>("Posición");
        tourneyPositionColumn.setCellValueFactory(cellData -> {
            Tourney tourney = cellData.getValue();
            if (selectedTeam == null) {
                return new SimpleStringProperty("Selecciona un equipo");
            }
            Team updatedTeam = findUpdatedTeamInTourneys(selectedTeam);
            if (updatedTeam == null) {
                return new SimpleStringProperty("Equipo no encontrado en torneo");
            }
            return new SimpleStringProperty(tourney.returnTeamPosition(updatedTeam));
        });

        tourneyTable.getColumns().clear();
        tourneyTable.getColumns().addAll(tourneyIdColumn, tourneyNameColumn, tourneyMinutesColumn, tourneySportColumn, tourneyStateColumn, tourneyPositionColumn);
    }

    private void setupTourneyStatsColumns() {
        tourneyStatsNameColumn.setCellValueFactory(cellData -> cellData.getValue().tourneyNameProperty());
        tourneyStatsPointsColumn.setCellValueFactory(cellData -> cellData.getValue().pointsProperty().asObject());
        tourneyStatsGoalsColumn.setCellValueFactory(cellData -> cellData.getValue().goalsProperty().asObject());
        tourneyStatsRankingColumn.setCellValueFactory(cellData -> cellData.getValue().rankingProperty());
    }
    
    private void loadData() {
        ensureTeamsInitialized();
        List<Team> teams = getTeamsFromAppContext();
        handleTeamFileIfEmpty(teams);
        removeDuplicateTeams(teams);
        updateAvailableTeams(teams);
        updateTeamTable();
    }

    private void ensureTeamsInitialized() {
        if (AppContext.getInstance().get("teams") == null) {
            AppContext.getInstance().set("teams", new ArrayList<Team>());
        }
    }

    @SuppressWarnings("unchecked")
    private List<Team> getTeamsFromAppContext() {
        List<Team> teams = (List<Team>) AppContext.getInstance().get("teams");
        System.out.println("Equipos iniciales en AppContext: " + teams);
        return teams;
    }

    private void handleTeamFileIfEmpty(List<Team> teams) {
        if (teams.isEmpty()) {
            File teamFile = new File("Team.txt");
            System.out.println("Ruta absoluta del archivo Team.txt: " + teamFile.getAbsolutePath());

            if (!teamFile.exists()) {
                System.out.println("Archivo Team.txt no encontrado en: " + teamFile.getAbsolutePath());
                message.show(Alert.AlertType.WARNING, "Archivo no encontrado", "Team.txt no existe en el directorio raíz.");
            } else if (teamFile.length() == 0) {
                System.out.println("Archivo Team.txt está vacío.");
                message.show(Alert.AlertType.WARNING, "Archivo vacío", "Team.txt está vacío.");
            } else {
                deserializeTeamFile(teamFile, teams);
            }
        }
    }

    private void deserializeTeamFile(File teamFile, List<Team> teams) {
        try {
            System.out.println("Deserializando Team.txt...");
            List<Team> deserializedTeams = fileManager.deserialization("Team", Team.class);

            if (deserializedTeams == null || deserializedTeams.isEmpty()) {
                System.out.println("Deserialización de Team.txt devolvió null o lista vacía.");
                message.show(Alert.AlertType.WARNING, "Sin datos", "No se encontraron equipos en Team.txt.");
            } else {
                Set<Team> uniqueTeams = new HashSet<>(deserializedTeams);
                teams.clear();
                teams.addAll(uniqueTeams);
                AppContext.getInstance().set("teams", teams);
                System.out.println("Equipos cargados y añadidos a AppContext (sin duplicados): " + teams);
            }
        } catch (Exception e) {
            System.out.println("Error al deserializar Team.txt: " + e.getMessage());
            e.printStackTrace();
            message.show(Alert.AlertType.ERROR, "Error al Cargar Equipos", "No se pudieron cargar los equipos: " + e.getMessage());
        }
    }

    private void removeDuplicateTeams(List<Team> teams) {
        Set<Team> uniqueTeams = new HashSet<>(teams);
        teams.clear();
        teams.addAll(uniqueTeams);
        AppContext.getInstance().set("teams", teams);
        System.out.println("Equipos ya cargados en AppContext (sin duplicados): " + teams);
    }

    private void updateAvailableTeams(List<Team> teams) {
        availableTeams.clear();
        availableTeams.addAll(teams);
    }

    private void updateTeamTable() {
        teamTable.setItems(availableTeams);
        System.out.println("teamTable items después de cargar: " + teamTable.getItems());

        if (teamTable.getItems().isEmpty()) {
            System.out.println("No hay equipos para mostrar en teamTable.");
            message.show(Alert.AlertType.WARNING, "Lista vacía", "No hay equipos disponibles para mostrar.");
        }
    }
    
    private void loadTourneys() {
        if (AppContext.getInstance().get("tourneys") == null) {
            AppContext.getInstance().set("tourneys", new ArrayList<Tourney>());
        }
        @SuppressWarnings("unchecked")
        List<Tourney> tourneys = (List<Tourney>) AppContext.getInstance().get("tourneys");
        File tourneyFile = new File("Tourney.txt");
        if (tourneyFile.exists() && tourneyFile.length() > 0) {
            try {
                System.out.println("Intentando deserializar Tourney.txt...");
                List<Tourney> deserializedTourneys = fileManager.deserialization("Tourney", Tourney.class);
                if (deserializedTourneys == null || deserializedTourneys.isEmpty()) {
                    System.out.println("Tourney.txt está vacío o deserialización falló.");
                    message.show(Alert.AlertType.WARNING, "Sin datos", "No se encontraron torneos en Tourney.txt.");
                } else {
                    tourneys.clear();
                    tourneys.addAll(deserializedTourneys);
                    AppContext.getInstance().set("tourneys", tourneys);
                    System.out.println("Torneos deserializados y añadidos: " + tourneys.size());
                }
            } catch (Exception e) {
                System.out.println("Error al deserializar Tourney.txt: " + e.getMessage());
                e.printStackTrace();
                message.show(Alert.AlertType.ERROR, "Error al cargar torneos", "No se pudieron cargar los torneos: " + e.getMessage());
            }
        } else {
            System.out.println("Tourney.txt no existe o está vacío.");
            message.show(Alert.AlertType.WARNING, "Archivo no encontrado", "Tourney.txt no existe en el directorio raíz.");
        }

        System.out.println("Actualizando availableTourneys...");
        availableTourneys.clear();
        availableTourneys.addAll(tourneys);

        System.out.println("Torneos en availableTourneys: " + availableTourneys);
        tourneyTable.setItems(FXCollections.observableArrayList(availableTourneys));
        if (availableTourneys.isEmpty()) {
            System.out.println("No hay torneos para mostrar en tourneyTable.");
            message.show(Alert.AlertType.WARNING, "Lista vacía", "No hay torneos disponibles para mostrar.");
        }

        if (AppContext.getInstance().get("teams") == null) {
            AppContext.getInstance().set("teams", new ArrayList<Team>());
        }
        List<Team> existingTeams = (List<Team>) AppContext.getInstance().get("teams");
        for (Tourney tourney : tourneys) {
            List<Team> allTeamsInTourney = new ArrayList<>();
            allTeamsInTourney.addAll(tourney.getTeamList());
            allTeamsInTourney.addAll(tourney.getLoosersList());
            allTeamsInTourney.addAll(tourney.getRound1());
            allTeamsInTourney.addAll(tourney.getRound2());
            allTeamsInTourney.addAll(tourney.getRound3());
            allTeamsInTourney.addAll(tourney.getRound4());
            allTeamsInTourney.addAll(tourney.getRound5());
            allTeamsInTourney.addAll(tourney.getRound6());
            allTeamsInTourney.addAll(tourney.getWinner());

            for (Team team : allTeamsInTourney) {
                if (!existingTeams.contains(team)) {
                    System.out.println("Equipo en torneo no está en AppContext teams: " + team.getName() + " (ID: " + team.getId() + ")");
                    existingTeams.add(team);
                }
            }
        }
        Set<Team> uniqueTeams = new HashSet<>(existingTeams);
        existingTeams.clear();
        existingTeams.addAll(uniqueTeams);
        AppContext.getInstance().set("teams", existingTeams);
        System.out.println("Equipos en AppContext después de cargar torneos (sin duplicados): " + existingTeams);
    }

    private void loadSports() {
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
                        AppContext.getInstance().set("sports", sports);
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

    private void setupTeamSelectionListener() {
        teamTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedTeam = newSelection;
            if (selectedTeam != null) {
                selectedTeamLabel.setText(selectedTeam.getName());
                ObservableList<Tourney> teamTourneys = FXCollections.observableArrayList();
                for (Tourney tourney : availableTourneys) {
                    if (tourney.getTeamList().stream().anyMatch(t -> t.getId() == selectedTeam.getId())
                            || tourney.getLoosersList().stream().anyMatch(t -> t.getId() == selectedTeam.getId())) {
                        teamTourneys.add(tourney);
                    }
                }
                tourneyTable.setItems(teamTourneys);
                updateStatsTab();
                updateMatchAccordion();
            } else {
                selectedTeamLabel.setText("Ningún equipo seleccionado");
                tourneyTable.getItems().clear();
                tourneyStatsTable.getItems().clear();
                totalPointsLabel.setText("Puntos Totales: 0");
                totalMatchesLabel.setText("Partidos Jugados: 0");
                generalRankingLabel.setText("Ranking General: N/A");
                tournamentsLabel.setText("Torneos Participados: 0");
                clearCanvases();
                matchAccordion.getPanes().clear();
            }
            tourneyTable.refresh();
        });
    }
    
    private void setupTourneySelectionListener() {
        tourneyTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedTourney = newSelection;
            if (newSelection != null) {
                System.out.println("Torneo seleccionado: " + selectedTourney.getName());
                updateMatchAccordion();
            } else {
                System.out.println("Ningún torneo seleccionado.");
                matchAccordion.getPanes().clear();
                updateGoalsGraphicForTourney(null); 
            }
        });
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

    private void setupFilters() {
        teamFilter.setItems(availableTeams);
        teamSearch.textProperty().addListener((obs, oldText, newText) -> {
            if (newText == null || newText.isEmpty()) {
                teamFilter.setItems(availableTeams);
            } else {
                ObservableList<Team> filteredTeams = FXCollections.observableArrayList();
                for (Team team : availableTeams) {
                    if (team.getName().toLowerCase().contains(newText.toLowerCase())
                            || String.valueOf(team.getId()).equals(newText)) {
                        filteredTeams.add(team);
                    }
                }
                teamFilter.setItems(filteredTeams);
            }
            applyFilters();
        });
        teamFilter.getSelectionModel().selectedItemProperty().addListener((obs, oldTeam, newTeam) -> applyFilters());
        sportFilter.getSelectionModel().selectedItemProperty().addListener((obs, oldSport, newSport) -> applyFilters());
        searchButton.setOnAction(event -> applyFilters());
        clearButton.setOnAction(event -> {
            teamSearch.clear();
            teamFilter.getSelectionModel().clearSelection();
            sportFilter.getSelectionModel().clearSelection();
            teamTable.setItems(availableTeams);
            tourneyTable.setItems(availableTourneys);
        });
    }

    private void applyFilters() {
        String query;
        if (teamSearch.getText() != null) {
            query = teamSearch.getText().trim().toLowerCase();
        } else {
            query = "";
        }
        Sport selectedSport = sportFilter.getSelectionModel().getSelectedItem();
        Team selectedTeamFilter = teamFilter.getSelectionModel().getSelectedItem();

        ObservableList<Team> filteredTeams = FXCollections.observableArrayList();
        for (Team team : availableTeams) {
            boolean matchesSearch = query.isEmpty()
                    || team.getName().toLowerCase().contains(query)
                    || String.valueOf(team.getId()).equals(query);
            boolean matchesTeamFilter = selectedTeamFilter == null || team.equals(selectedTeamFilter);
            boolean matchesSportFilter = true;
            if (selectedSport != null) {
                matchesSportFilter = false;
                for (Tourney tourney : availableTourneys) {
                    if ((tourney.getTeamList().stream().anyMatch(t -> t.getId() == team.getId())
                            || tourney.getLoosersList().stream().anyMatch(t -> t.getId() == team.getId()))
                            && tourney.getSportTypeId() == selectedSport.getId()) {
                        matchesSportFilter = true;
                        break;
                    }
                }
            }
            if (matchesSearch && matchesTeamFilter && matchesSportFilter) {
                filteredTeams.add(team);
            }
        }
        teamTable.setItems(filteredTeams);
    }

    private Team findUpdatedTeamInTourneys(Team team) {
        if (team == null) {
            return null;
        }
        for (Tourney tourney : availableTourneys) {
            List<Team> allTeamsInTourney = new ArrayList<>();
            allTeamsInTourney.addAll(tourney.getTeamList());
            allTeamsInTourney.addAll(tourney.getLoosersList());
            allTeamsInTourney.addAll(tourney.getRound1());
            allTeamsInTourney.addAll(tourney.getRound2());
            allTeamsInTourney.addAll(tourney.getRound3());
            allTeamsInTourney.addAll(tourney.getRound4());
            allTeamsInTourney.addAll(tourney.getRound5());
            allTeamsInTourney.addAll(tourney.getRound6());
            allTeamsInTourney.addAll(tourney.getWinner());

            for (Team tourneyTeam : allTeamsInTourney) {
                if (tourneyTeam.getId() == team.getId()) {
                    return tourneyTeam;
                }
            }
        }
        System.out.println("Equipo con ID " + team.getId() + " no encontrado en ningún torneo.");
        return null;
    }

    private int calculateGlobalPoints(Team team) {
        int totalPoints = 0;
        for (Tourney tourney : availableTourneys) {
            List<Team> allTeamsInTourney = new ArrayList<>();
            allTeamsInTourney.addAll(tourney.getTeamList());
            allTeamsInTourney.addAll(tourney.getLoosersList());
            allTeamsInTourney.addAll(tourney.getRound1());
            allTeamsInTourney.addAll(tourney.getRound2());
            allTeamsInTourney.addAll(tourney.getRound3());
            allTeamsInTourney.addAll(tourney.getRound4());
            allTeamsInTourney.addAll(tourney.getRound5());
            allTeamsInTourney.addAll(tourney.getRound6());
            allTeamsInTourney.addAll(tourney.getWinner());

            for (Team tourneyTeam : allTeamsInTourney) {
                if (tourneyTeam.getId() == team.getId()) {
                    int tourneyPoints = 0;
                    List<MatchDetails> matches = tourneyTeam.getEncounterList();
                    if (matches != null) {
                        for (MatchDetails match : matches) {
                            // Filtrar partidos que pertenecen a este torneo
                            String opponentName = match.getNameFirstTeam().equals(tourneyTeam.getName()) ? match.getNameSecondTeam() : match.getNameFirstTeam();
                            boolean opponentInTourney = allTeamsInTourney.stream().anyMatch(t -> t.getName().equals(opponentName));
                            if (!opponentInTourney) {
                                continue;
                            }

                            // Solo contar puntos si el equipo es firstTeam
                            if (match.getNameFirstTeam().equals(tourneyTeam.getName())) {
                                int teamGoals = match.getCounterFirstTeamGoals();
                                int opponentGoals = match.getCounterSecondTeamGoals();
                                boolean isDraw = match.isDraw();
                                tourneyPoints += teamGoals;
                                if (isDraw) {
                                    tourneyPoints += 1;
                                } else if (teamGoals > opponentGoals) {
                                    tourneyPoints += 3;
                                }
                            }
                        }
                    }
                    totalPoints += tourneyPoints;
                    System.out.println("Puntos calculados para " + team.getName() + " (ID: " + team.getId() + ") en torneo " + tourney.getName() + ": " + tourneyPoints);
                    break;
                }
            }
        }
        System.out.println("Puntos globales calculados para " + team.getName() + " (ID: " + team.getId() + "): " + totalPoints);
        return totalPoints;
    }

    private void globalTourneyPointsGraphic() {
        GraphicsContext gcPoints = pointsPerMatchCanvas.getGraphicsContext2D();
        gcPoints.clearRect(0, 0, pointsPerMatchCanvas.getWidth(), pointsPerMatchCanvas.getHeight());
        pointsPerTourneyLegend.getChildren().clear();

        List<String> tourneyNames = new ArrayList<>();
        List<Integer> tourneyPointsList = new ArrayList<>();

        for (Tourney tourney : availableTourneys) {
            List<Team> allTeamsInTourney = new ArrayList<>();
            allTeamsInTourney.addAll(tourney.getTeamList());
            allTeamsInTourney.addAll(tourney.getLoosersList());
            allTeamsInTourney.addAll(tourney.getRound1());
            allTeamsInTourney.addAll(tourney.getRound2());
            allTeamsInTourney.addAll(tourney.getRound3());
            allTeamsInTourney.addAll(tourney.getRound4());
            allTeamsInTourney.addAll(tourney.getRound5());
            allTeamsInTourney.addAll(tourney.getRound6());
            allTeamsInTourney.addAll(tourney.getWinner());

            for (Team tourneyTeam : allTeamsInTourney) {
                if (tourneyTeam.getId() == updatedTeam.getId()) {
                    int tourneyPoints = 0;
                    List<MatchDetails> matches = tourneyTeam.getEncounterList();
                    if (matches != null) {
                        for (MatchDetails match : matches) {
                            // Filtrar partidos que pertenecen a este torneo
                            String opponentName = match.getNameFirstTeam().equals(tourneyTeam.getName()) ? match.getNameSecondTeam() : match.getNameFirstTeam();
                            boolean opponentInTourney = allTeamsInTourney.stream().anyMatch(t -> t.getName().equals(opponentName));
                            if (!opponentInTourney) {
                                continue;
                            }

                            // Solo contar puntos si el equipo es firstTeam
                            if (match.getNameFirstTeam().equals(tourneyTeam.getName())) {
                                int teamGoals = match.getCounterFirstTeamGoals();
                                int opponentGoals = match.getCounterSecondTeamGoals();
                                boolean isDraw = match.isDraw();
                                tourneyPoints += teamGoals;
                                if (isDraw) {
                                    tourneyPoints += 1;
                                } else if (teamGoals > opponentGoals) {
                                    tourneyPoints += 3;
                                }
                            }
                        }
                    }
                    tourneyNames.add(tourney.getName());
                    tourneyPointsList.add(tourneyPoints);
                    break;
                }
            }
        }

        // Calcular el máximo para la escala
        double barWidth = 50;
        double maxPoints = 1;
        for (int points : tourneyPointsList) {
            if (points > maxPoints) {
                maxPoints = points;
            }
        }
        maxPoints = Math.max(maxPoints, 1);
        double scale = 150.0 / maxPoints;

        for (int index = 0; index < tourneyNames.size(); index++) {
            String tourneyName = tourneyNames.get(index);
            int points = tourneyPointsList.get(index);
            double barHeight = points * scale;

            gcPoints.setFill(Color.PURPLE);
            double x = 20 + index * (barWidth + 10);
            gcPoints.fillRect(x, 200 - barHeight, barWidth, barHeight);

            gcPoints.setFill(Color.BLACK);
            gcPoints.fillText(String.valueOf(points), x + barWidth / 2 - 10, 200 - barHeight - 10);

            Label legendLabel = new Label(tourneyName + ": " + points + " puntos");
            pointsPerTourneyLegend.getChildren().add(legendLabel);
        }

        pointsPerMatchCanvas.setWidth(20 + tourneyNames.size() * (barWidth + 10));
    }
    
    private void globalSportRankingGraphic(List<Team> rankedTeams) {
        GraphicsContext gcRanking = globalRankingCanvas.getGraphicsContext2D();
        gcRanking.clearRect(0, 0, globalRankingCanvas.getWidth(), globalRankingCanvas.getHeight());

        if (rankedTeams.isEmpty()) {
            System.out.println("No hay equipos para mostrar en el ranking global.");
            gcRanking.setFill(Color.BLACK);
            gcRanking.fillText("No hay equipos en este deporte", 20, 100);
            return;
        }

        double barHeight = 30;
        double maxPointsRanking = 1;
        for (Team team : rankedTeams) {
            int teamPoints = calculateGlobalPoints(team);
            System.out.println("Equipo: " + team.getName() + ", Puntos: " + teamPoints);
            if (teamPoints > maxPointsRanking) {
                maxPointsRanking = teamPoints;
            }
        }
        maxPointsRanking = Math.max(maxPointsRanking, 1);
        double scaleRanking = 200.0 / maxPointsRanking;

        int barIndex = 0;
        for (Team team : rankedTeams) {
            int teamPoints = calculateGlobalPoints(team);
            double barWidth = teamPoints * scaleRanking;

            if (barWidth < 5) {
                barWidth = 5;
            }
            if (team.getId() == updatedTeam.getId()) {
                gcRanking.setFill(Color.GOLD);
            } else {
                gcRanking.setFill(Color.LIGHTBLUE);
            }

            double y = 20 + barIndex * (barHeight + 10);
            double x = 20;
            gcRanking.fillRect(x, y, barWidth, barHeight);

            gcRanking.setFill(Color.BLACK);
            gcRanking.fillText(team.getName() + ": " + teamPoints + " (Posición " + (barIndex + 1) + ")", x + 5, y + barHeight / 2 + 5);

            System.out.println("Barra " + (barIndex + 1) + ": x=" + x + ", y=" + y + ", ancho=" + barWidth + ", alto=" + barHeight);

            barIndex++;
        }

        globalRankingCanvas.setHeight(Math.max(200, 20 + rankedTeams.size() * (barHeight + 10)));
        Platform.runLater(() -> {
            globalRankingCanvas.requestFocus();
            globalRankingCanvas.getParent().requestLayout();
        });
    }
    
    private void tourneyGoalsPerRoundGraphic() {
        GraphicsContext gcTourneyPoints = tourneyPointsPerMatchCanvas.getGraphicsContext2D();
        gcTourneyPoints.clearRect(0, 0, tourneyPointsPerMatchCanvas.getWidth(), tourneyPointsPerMatchCanvas.getHeight());

        if (updatedTeam == null) {
            System.out.println("updatedTeam es null, no se pueden calcular goles por ronda.");
            gcTourneyPoints.setFill(Color.BLACK);
            gcTourneyPoints.fillText("Selecciona un equipo", 20, 100);
            return;
        }

        List<String> tourneyNames = new ArrayList<>();
        List<List<Integer>> roundsPerTourney = new ArrayList<>();
        List<List<Integer>> goalsPerRoundPerTourney = new ArrayList<>();

        for (Tourney tourney : availableTourneys) {
            List<Team> allTeamsInTourney = new ArrayList<>();
            allTeamsInTourney.addAll(tourney.getTeamList());
            allTeamsInTourney.addAll(tourney.getLoosersList());
            allTeamsInTourney.addAll(tourney.getRound1());
            allTeamsInTourney.addAll(tourney.getRound2());
            allTeamsInTourney.addAll(tourney.getRound3());
            allTeamsInTourney.addAll(tourney.getRound4());
            allTeamsInTourney.addAll(tourney.getRound5());
            allTeamsInTourney.addAll(tourney.getRound6());
            allTeamsInTourney.addAll(tourney.getWinner());

            for (Team tourneyTeam : allTeamsInTourney) {
                if (tourneyTeam.getId() == updatedTeam.getId()) {
                    List<Integer> rounds = new ArrayList<>();
                    List<Integer> goalsPerRound = new ArrayList<>();
                    List<MatchDetails> matches = tourneyTeam.getEncounterList();
                    System.out.println("Partidos encontrados para " + tourneyTeam.getName() + " en torneo " + tourney.getName() + ": " + (matches != null ? matches.size() : 0));
                    if (matches != null) {
                        int matchIndex = 0;
                        int matchesPerRound = tourney.getTeamList().size() / 2;
                        if (matchesPerRound <= 0) {
                            matchesPerRound = 1;
                        }

                        int currentRound = 1;
                        int goalsInCurrentRound = 0;
                        for (MatchDetails match : matches) {
                            String opponentName = match.getNameFirstTeam().equals(tourneyTeam.getName()) ? match.getNameSecondTeam() : match.getNameFirstTeam();
                            boolean opponentInTourney = allTeamsInTourney.stream().anyMatch(t -> t.getName().equals(opponentName));
                            if (!opponentInTourney) {
                                continue;
                            }

                            int teamGoals = 0;
                            if (match.getNameFirstTeam().equals(tourneyTeam.getName())) {
                                teamGoals = match.getCounterFirstTeamGoals();
                            } else {
                                continue;
                            }

                            int matchRound = (matchIndex / matchesPerRound) + 1;
                            if (matchRound != currentRound) {
                                rounds.add(currentRound);
                                goalsPerRound.add(goalsInCurrentRound);
                                System.out.println("Torneo: " + tourney.getName() + ", Ronda " + currentRound + ", Goles: " + goalsInCurrentRound);
                                goalsInCurrentRound = 0;
                                currentRound = matchRound;
                            }
                            goalsInCurrentRound += teamGoals;
                            matchIndex++;
                        }
                        if (goalsInCurrentRound > 0 || matchIndex > 0) {
                            rounds.add(currentRound);
                            goalsPerRound.add(goalsInCurrentRound);
                            System.out.println("Torneo: " + tourney.getName() + ", Ronda " + currentRound + ", Goles: " + goalsInCurrentRound);
                        }
                    }
                    tourneyNames.add(tourney.getName());
                    roundsPerTourney.add(rounds);
                    goalsPerRoundPerTourney.add(goalsPerRound);
                    break;
                }
            }
        }

        if (tourneyNames.isEmpty()) {
            System.out.println("No se encontraron torneos para el equipo " + updatedTeam.getName());
            gcTourneyPoints.setFill(Color.BLACK);
            gcTourneyPoints.fillText("No hay datos de torneos", 20, 100);
            return;
        }

        double barWidthRound = 30;
        int totalBars = 0;
        for (List<Integer> rounds : roundsPerTourney) {
            totalBars += rounds.size();
        }

        double maxGoals = 1;
        for (List<Integer> goalsPerRound : goalsPerRoundPerTourney) {
            for (int goals : goalsPerRound) {
                if (goals > maxGoals) {
                    maxGoals = goals;
                }
            }
        }
        maxGoals = Math.max(maxGoals, 1);
        double scaleGoals = 150.0 / maxGoals;

        int barIndex = 0;
        for (int i = 0; i < tourneyNames.size(); i++) {
            String tourneyName = tourneyNames.get(i);
            List<Integer> rounds = roundsPerTourney.get(i);
            List<Integer> goalsPerRound = goalsPerRoundPerTourney.get(i);

            double startX = 20 + barIndex * (barWidthRound + 10);
            for (int j = 0; j < rounds.size(); j++) {
                int goals = goalsPerRound.get(j);
                double barHeight = goals * scaleGoals;

                if (barHeight < 5) {
                    barHeight = 5;
                }

                gcTourneyPoints.setFill(Color.GREEN);
                double x = 20 + barIndex * (barWidthRound + 10);
                double y = tourneyPointsPerMatchCanvas.getHeight() - barHeight - 30;
                gcTourneyPoints.fillRect(x, y, barWidthRound, barHeight);

                gcTourneyPoints.setFill(Color.BLACK);
                gcTourneyPoints.fillText(String.valueOf(goals), x + barWidthRound / 2 - 10, y - 10);

                System.out.println("Barra " + (barIndex + 1) + ": x=" + x + ", y=" + y + ", ancho=" + barWidthRound + ", alto=" + barHeight);

                barIndex++;
            }
            double endX = 20 + (barIndex - 1) * (barWidthRound + 10);

            gcTourneyPoints.setFill(Color.BLACK);
            double titleX = startX + (endX - startX) / 2 - (tourneyName.length() * 3);
            gcTourneyPoints.fillText(tourneyName, titleX, tourneyPointsPerMatchCanvas.getHeight() - 5);

            barIndex++;
        }

        tourneyPointsPerMatchCanvas.setWidth(Math.max(300, 20 + (totalBars + tourneyNames.size()) * (barWidthRound + 10)));
        tourneyPointsPerMatchCanvas.setHeight(230);

        Platform.runLater(() -> {
            tourneyPointsPerMatchCanvas.requestFocus();
            tourneyPointsPerMatchCanvas.getParent().requestLayout();
        });
    }
    
    private void updateStatsTab() {
        if (selectedTeam == null) {
            System.out.println("No hay equipo seleccionado para actualizar el tab de estadísticas.");
            tourneyStatsTable.getItems().clear();
            totalPointsLabel.setText("Puntos Totales: 0");
            totalMatchesLabel.setText("Partidos Jugados: 0");
            generalRankingLabel.setText("Ranking General: N/A");
            tournamentsLabel.setText("Torneos Participados: 0");
            clearCanvases();
            updatedTeam = null;
            return;
        }

        updatedTeam = findUpdatedTeamInTourneys(selectedTeam);
        if (updatedTeam == null) {
            System.out.println("Equipo no encontrado en torneos para actualizar el tab de estadísticas.");
            tourneyStatsTable.getItems().clear();
            totalPointsLabel.setText("Puntos Totales: 0");
            totalMatchesLabel.setText("Partidos Jugados: 0");
            generalRankingLabel.setText("Ranking General: N/A");
            tournamentsLabel.setText("Torneos Participados: 0");
            clearCanvases();
            updatedTeam = null;
            return;
        }

        System.out.println("Actualizando tab de estadísticas para el equipo: " + updatedTeam.getName());

        int totalPoints = calculateGlobalPoints(updatedTeam);
        totalPointsLabel.setText("Puntos Totales: " + totalPoints);

        int totalMatches = 0;
        List<MatchDetails> allMatches = updatedTeam.getEncounterList();
        if (allMatches != null) {
            for (MatchDetails match : allMatches) {
                if (match.getNameFirstTeam().equals(updatedTeam.getName())) {
                    totalMatches++;
                }
            }
        }
        totalMatchesLabel.setText("Partidos Jugados: " + totalMatches);

        Set<String> tournamentsParticipated = new HashSet<>();
        for (Tourney tourney : availableTourneys) {
            List<Team> allTeamsInTourney = new ArrayList<>();
            allTeamsInTourney.addAll(tourney.getTeamList());
            allTeamsInTourney.addAll(tourney.getLoosersList());
            allTeamsInTourney.addAll(tourney.getRound1());
            allTeamsInTourney.addAll(tourney.getRound2());
            allTeamsInTourney.addAll(tourney.getRound3());
            allTeamsInTourney.addAll(tourney.getRound4());
            allTeamsInTourney.addAll(tourney.getRound5());
            allTeamsInTourney.addAll(tourney.getRound6());
            allTeamsInTourney.addAll(tourney.getWinner());

            if (allTeamsInTourney.stream().anyMatch(t -> t.getId() == updatedTeam.getId())) {
                tournamentsParticipated.add(tourney.getName());
            }
        }
        tournamentsLabel.setText("Torneos Participados: " + tournamentsParticipated.size());

        List<Team> allTeamsInSport = new ArrayList<>();
        for (Tourney tourney : availableTourneys) {
            if (tourney.getSportTypeId() == updatedTeam.getIdSportType()) {
                List<Team> teamsInTourney = new ArrayList<>();
                teamsInTourney.addAll(tourney.getTeamList());
                teamsInTourney.addAll(tourney.getLoosersList());
                teamsInTourney.addAll(tourney.getRound1());
                teamsInTourney.addAll(tourney.getRound2());
                teamsInTourney.addAll(tourney.getRound3());
                teamsInTourney.addAll(tourney.getRound4());
                teamsInTourney.addAll(tourney.getRound5());
                teamsInTourney.addAll(tourney.getRound6());
                teamsInTourney.addAll(tourney.getWinner());
                allTeamsInSport.addAll(teamsInTourney);
            }
        }

        List<Team> uniqueTeams = new ArrayList<>();
        List<Integer> teamIds = new ArrayList<>();
        for (Team team : allTeamsInSport) {
            if (!teamIds.contains(team.getId())) {
                teamIds.add(team.getId());
                uniqueTeams.add(team);
            }
        }

        List<Team> rankedTeams = new ArrayList<>(uniqueTeams);
        rankedTeams.sort((t1, t2) -> Integer.compare(calculateGlobalPoints(t2), calculateGlobalPoints(t1)));

        int globalRanking = 1;
        for (Team team : rankedTeams) {
            if (team.getId() == updatedTeam.getId()) {
                break;
            }
            globalRanking++;
        }
        generalRankingLabel.setText("Ranking General: " + globalRanking + " de " + rankedTeams.size());

        globalTourneyPointsGraphic();
        globalSportRankingGraphic(rankedTeams);

        tourneyStatsTable.getItems().clear();
        for (Tourney tourney : availableTourneys) {
            List<Team> allTeamsInTourney = new ArrayList<>();
            allTeamsInTourney.addAll(tourney.getTeamList());
            allTeamsInTourney.addAll(tourney.getLoosersList());
            allTeamsInTourney.addAll(tourney.getRound1());
            allTeamsInTourney.addAll(tourney.getRound2());
            allTeamsInTourney.addAll(tourney.getRound3());
            allTeamsInTourney.addAll(tourney.getRound4());
            allTeamsInTourney.addAll(tourney.getRound5());
            allTeamsInTourney.addAll(tourney.getRound6());
            allTeamsInTourney.addAll(tourney.getWinner());

            for (Team tourneyTeam : allTeamsInTourney) {
                if (tourneyTeam.getId() == updatedTeam.getId()) {
                    int tourneyPoints = 0;
                    int tourneyGoals = 0;
                    List<MatchDetails> matches = tourneyTeam.getEncounterList();
                    if (matches != null) {
                        for (MatchDetails match : matches) {
                            // Filtrar partidos que pertenecen a este torneo
                            String opponentName = match.getNameFirstTeam().equals(tourneyTeam.getName()) ? match.getNameSecondTeam() : match.getNameFirstTeam();
                            boolean opponentInTourney = allTeamsInTourney.stream().anyMatch(t -> t.getName().equals(opponentName));
                            if (!opponentInTourney) {
                                continue;
                            }

                            // Solo contar si el equipo es firstTeam
                            if (match.getNameFirstTeam().equals(tourneyTeam.getName())) {
                                int teamGoals = match.getCounterFirstTeamGoals();
                                int opponentGoals = match.getCounterSecondTeamGoals();
                                boolean isDraw = match.isDraw();
                                tourneyPoints += teamGoals;
                                tourneyGoals += teamGoals;
                                if (isDraw) {
                                    tourneyPoints += 1;
                                } else if (teamGoals > opponentGoals) {
                                    tourneyPoints += 3;
                                }
                            }
                        }
                    }

                    List<Team> teamsInTourney = new ArrayList<>(allTeamsInTourney);
                    teamsInTourney.sort((t1, t2) -> Integer.compare(calculateGlobalPoints(t2), calculateGlobalPoints(t1)));
                    int tourneyRanking = 1;
                    for (Team team : teamsInTourney) {
                        if (team.getId() == updatedTeam.getId()) {
                            break;
                        }
                        tourneyRanking++;
                    }

                    TourneyStats stats = new TourneyStats(
                            tourney.getName(),
                            tourneyPoints,
                            tourneyGoals,
                            "Posición: " + tourneyRanking
                    );
                    tourneyStatsTable.getItems().add(stats);
                    break;
                }
            }
        }
        tourneyGoalsPerRoundGraphic();
    }
    
    private void updateGoalsGraphicForTourney(Tourney tourney) {
        GraphicsContext gcGoals = matchGoalsCanvas.getGraphicsContext2D();
        gcGoals.clearRect(0, 0, matchGoalsCanvas.getWidth(), matchGoalsCanvas.getHeight());
        double width = matchGoalsCanvas.getWidth();
        double height = matchGoalsCanvas.getHeight();

        if (updatedTeam == null || tourney == null) {
            gcGoals.setFill(Color.BLACK);
            gcGoals.fillText("Selecciona un equipo y un torneo", width / 4, height / 2);
            return;
        }

        // Obtener todos los equipos del torneo
        List<Team> allTeamsInTourney = getAllTeamsInTourney(tourney);

        // Verificar si el equipo participa en el torneo
        boolean teamInTourney = allTeamsInTourney.stream().anyMatch(t -> t.getId() == updatedTeam.getId());
        if (!teamInTourney) {
            gcGoals.setFill(Color.BLACK);
            gcGoals.fillText("Equipo no participó en este torneo", width / 4, height / 2);
            return;
        }

        // Encontrar la versión actualizada del equipo en el torneo
        Team tourneyTeam = allTeamsInTourney.stream()
                .filter(t -> t.getId() == updatedTeam.getId())
                .findFirst()
                .orElse(null);

        if (tourneyTeam == null) {
            gcGoals.setFill(Color.BLACK);
            gcGoals.fillText("Equipo no encontrado en el torneo", width / 4, height / 2);
            return;
        }

        // Determinar el número de rondas en las que participó el equipo
        int lastRoundParticipated = determineLastRoundParticipated(tourney, tourneyTeam);
        if (lastRoundParticipated == 0) {
            gcGoals.setFill(Color.BLACK);
            gcGoals.fillText("Equipo no participó en rondas", width / 4, height / 2);
            return;
        }

        // Obtener los partidos válidos y calcular los goles por ronda
        List<MatchDetails> matches = tourneyTeam.getEncounterList();
        List<Integer> goalsPerRoundList = new ArrayList<>();
        int totalGoals = 0;
        int numberOfRounds = 0;

        if (matches != null && !matches.isEmpty()) {
            int matchIndex = 0;
            int matchesPerRound = tourney.getTeamList().size() / 2;
            if (matchesPerRound <= 0) {
                matchesPerRound = 1;
            }

            // Inicializar rondas hasta lastRoundParticipated
            for (int i = 0; i < lastRoundParticipated; i++) {
                goalsPerRoundList.add(0);
            }

            for (MatchDetails match : matches) {
                // Filtrar partidos que no pertenecen al torneo
                String opponentName = match.getNameFirstTeam().equals(tourneyTeam.getName()) ? match.getNameSecondTeam() : match.getNameFirstTeam();
                boolean opponentInTourney = allTeamsInTourney.stream().anyMatch(t -> t.getName().equals(opponentName));
                if (!opponentInTourney) {
                    continue;
                }

                // Solo contar goles si el equipo es firstTeam
                int teamGoals = 0;
                if (match.getNameFirstTeam().equals(tourneyTeam.getName())) {
                    teamGoals = match.getCounterFirstTeamGoals();
                } else {
                    continue;
                }

                // Determinar la ronda del partido
                int matchRound = Math.min((matchIndex / matchesPerRound) + 1, lastRoundParticipated);
                goalsPerRoundList.set(matchRound - 1, goalsPerRoundList.get(matchRound - 1) + teamGoals);
                totalGoals += teamGoals;
                matchIndex++;
            }

            // Contar rondas con goles
            numberOfRounds = (int) goalsPerRoundList.stream().filter(g -> g > 0).count();
            if (numberOfRounds == 0) {
                numberOfRounds = 1; // Mínimo 1 ronda si hay partidos
            }
        }

        // Caso de deducción: no hay partidos válidos
        if (totalGoals == 0) {
            // Deducir goles basados en la estructura del torneo
            numberOfRounds = tourney.getTeamList().size() == 2 ? 1 : lastRoundParticipated;
            goalsPerRoundList.clear();
            for (int i = 0; i < numberOfRounds; i++) {
                goalsPerRoundList.add(0);
            }

            boolean isWinner = tourney.getWinner().stream().anyMatch(t -> t.getId() == tourneyTeam.getId());
            if (isWinner && tourney.getTeamList().size() == 2) {
                // Torneo de 2 equipos, 1 partido, asumimos 1 gol para la victoria
                goalsPerRoundList.set(0, 1);
                totalGoals = 1;
                numberOfRounds = 1;
            } else {
                // Para torneos más grandes, podríamos deducir más goles, pero necesitamos más datos
                gcGoals.setFill(Color.BLACK);
                gcGoals.fillText("No hay goles registrados en este torneo", width / 4, height / 2);
                return;
            }
        }

        if (totalGoals == 0) {
            gcGoals.setFill(Color.BLACK);
            gcGoals.fillText("No hay goles registrados en este torneo", width / 4, height / 2);
            return;
        }

        // Convertir la lista de goles por ronda a un arreglo para la gráfica
        int[] goalsPerRound = new int[numberOfRounds];
        int roundIndex = 0;
        for (int i = 0; i < goalsPerRoundList.size() && roundIndex < numberOfRounds; i++) {
            if (goalsPerRoundList.get(i) > 0) {
                goalsPerRound[roundIndex] = goalsPerRoundList.get(i);
                roundIndex++;
            }
        }

        // Dibujar la gráfica de queso
        double centerX = width / 2;
        double centerY = height / 3;
        double radius = Math.min(width, height) / 3;
        double startAngle = 0;

        Color[] colors = new Color[]{Color.LIGHTBLUE, Color.LIGHTGREEN, Color.LIGHTPINK, Color.LIGHTYELLOW, Color.LIGHTCORAL, Color.LIGHTGRAY, Color.LIGHTCYAN};
        int colorIndex = 0;

        for (int round = 1; round <= numberOfRounds; round++) {
            int goals = goalsPerRound[round - 1];
            if (goals > 0) {
                double percentage = (double) goals / totalGoals;
                double angle = percentage * 360;

                gcGoals.setFill(colors[colorIndex % colors.length]);
                gcGoals.fillArc(centerX - radius, centerY - radius, radius * 2, radius * 2, startAngle, angle, ArcType.ROUND);
                startAngle += angle;
                colorIndex++;
            }
        }

        double legendX = centerX - 80;
        double legendY = centerY + radius + 20;
        colorIndex = 0;
        for (int round = 1; round <= numberOfRounds; round++) {
            int goals = goalsPerRound[round - 1];
            if (goals > 0) {
                gcGoals.setFill(colors[colorIndex % colors.length]);
                gcGoals.fillRect(legendX, legendY, 15, 15);
                gcGoals.setFill(Color.BLACK);
                gcGoals.fillText("Ronda " + round + ": " + goals + " goles", legendX + 20, legendY + 12);
                legendY += 20;
                colorIndex++;
            }
        }
    }
    
    private void updateMatchAccordion() {
        matchAccordion.getPanes().clear();
        updatedTeam = findUpdatedTeamInTourneys(selectedTeam);
        if (selectedTeam == null || updatedTeam == null || selectedTourney == null) {
            System.out.println("SELECCIONE TODOS LOS DATOS");
            updateGoalsGraphicForTourney(null);
            return;
        }

        System.out.println("Actualizando matchAccordion para el equipo: " + updatedTeam.getName()
                + " (ID: " + updatedTeam.getId() + ") en torneo: " + selectedTourney.getName());

        // Obtener todos los equipos del torneo
        List<Team> allTeamsInTourney = getAllTeamsInTourney(selectedTourney);
        System.out.println("Número de equipos en el torneo: " + allTeamsInTourney.size());
        allTeamsInTourney.forEach(team -> System.out.println("Equipo en torneo: " + team.getName() + " (ID: " + team.getId() + ")"));

        // Verificar si el equipo participa en el torneo
        boolean teamInTourney = allTeamsInTourney.stream().anyMatch(t -> t.getId() == updatedTeam.getId());
        if (!teamInTourney) {
            System.out.println("El equipo " + updatedTeam.getName() + " no participa en el torneo " + selectedTourney.getName());
            matchAccordion.getPanes().add(new TitledPane(selectedTourney.getName(),
                    new Label("Este equipo no participó en el torneo seleccionado")));
            updateGoalsGraphicForTourney(null);
            return;
        }

        // Crear la estructura del acordeón
        TitledPane tourneyPane = new TitledPane();
        tourneyPane.setText(selectedTourney.getName());
        VBox tourneyContent = new VBox();
        Accordion roundsAccordion = new Accordion();

        // Obtener y filtrar partidos del equipo en este torneo
        List<MatchDetails> matchesInTourney = getMatchesInTourney(updatedTeam, allTeamsInTourney);
        System.out.println("Partidos encontrados para " + updatedTeam.getName()
                + " en torneo " + selectedTourney.getName() + ": " + matchesInTourney.size());
        matchesInTourney.forEach(match -> System.out.println("Partido directo: " + match));

        // Si no encontramos partidos directamente, buscamos en los encounterList de los oponentes
        List<MatchDetails> inferredMatches = new ArrayList<>();
        if (matchesInTourney.isEmpty()) {
            inferredMatches = inferMatchesFromOpponents(updatedTeam, allTeamsInTourney);
            System.out.println("Partidos inferidos para " + updatedTeam.getName()
                    + " en torneo " + selectedTourney.getName() + ": " + inferredMatches.size());
            inferredMatches.forEach(match -> System.out.println("Partido inferido: " + match));
        }

        // Combinar partidos y evitar duplicados
        matchesInTourney.addAll(inferredMatches);
        matchesInTourney = matchesInTourney.stream()
                .distinct()
                .collect(Collectors.toList());
        System.out.println("Partidos totales después de combinar: " + matchesInTourney.size());
        matchesInTourney.forEach(match -> System.out.println("Partido combinado: " + match));

        // Determinar última ronda participada y si es ganador/perdedor
        int lastRoundParticipated = determineLastRoundParticipated(selectedTourney, updatedTeam);
        System.out.println("Última ronda participada: " + lastRoundParticipated);
        boolean isWinner = selectedTourney.getWinner().stream().anyMatch(t -> t.getId() == updatedTeam.getId());
        boolean isLooser = selectedTourney.getLoosersList().stream().anyMatch(t -> t.getId() == updatedTeam.getId());

        // Forzar un solo partido para torneos de 2 equipos
        if (allTeamsInTourney.size() == 2 && !matchesInTourney.isEmpty()) {
            matchesInTourney = matchesInTourney.subList(0, 1);
            System.out.println("Forzando un solo partido para torneo de 2 equipos: " + matchesInTourney.size());
        }

        // Procesar partidos
        if (!matchesInTourney.isEmpty()) {
            // Caso especial: torneo con solo un partido (como torneo de 2 equipos)
            if (allTeamsInTourney.size() == 2) {
                VBox finalRoundContent = new VBox();
                processMatch(matchesInTourney.get(0), finalRoundContent, updatedTeam, allTeamsInTourney);

                String roundTitle = isWinner ? "Final (Ganador)" : "Final (Perdió)";
                System.out.println("Creando TitledPane: " + roundTitle);
                roundsAccordion.getPanes().add(new TitledPane(roundTitle, finalRoundContent));

                System.out.println("Caso especial manejado, evitando flujo de múltiples rondas.");
                // No continuar al flujo de múltiples rondas
            } else {
                // Para torneos con múltiples rondas
                System.out.println("Entrando en flujo de múltiples rondas con " + lastRoundParticipated + " rondas");
                List<VBox> roundContents = new ArrayList<>();
                for (int i = 0; i < lastRoundParticipated; i++) {
                    roundContents.add(new VBox());
                }

                // Asignar partidos a rondas
                int currentRound = 0;
                for (MatchDetails match : matchesInTourney) {
                    if (currentRound >= lastRoundParticipated) {
                        break;
                    }
                    processMatch(match, roundContents.get(currentRound), updatedTeam, allTeamsInTourney);
                    System.out.println("Partido añadido: " + match.getNameFirstTeam() + " vs " + match.getNameSecondTeam()
                            + " - " + match.getCounterFirstTeamGoals() + " - " + match.getCounterSecondTeamGoals());
                    currentRound++;
                }

                // Crear paneles para cada ronda con partidos
                for (int i = 0; i < roundContents.size(); i++) {
                    VBox roundContent = roundContents.get(i);
                    if (!roundContent.getChildren().isEmpty()) {
                        String roundTitle = getRoundTitle(i + 1, lastRoundParticipated, isWinner, isLooser);
                        System.out.println("Creando TitledPane para múltiples rondas: " + roundTitle);
                        roundsAccordion.getPanes().add(new TitledPane(roundTitle, roundContent));
                    }
                }
            }
        } else {
            System.out.println("No se encontraron partidos válidos para " + updatedTeam.getName() + " en torneo " + selectedTourney.getName());
            tourneyContent.getChildren().add(new Label("No hay partidos disponibles para este torneo"));
        }

        // Depurar los TitledPane creados
        System.out.println("TitledPanes creados en roundsAccordion: " + roundsAccordion.getPanes().size());
        roundsAccordion.getPanes().forEach(pane -> System.out.println("TitledPane: " + pane.getText()));

        // Configurar el contenido final
        if (roundsAccordion.getPanes().isEmpty()) {
            System.out.println("No se añadieron rondas al acordeón para el torneo " + selectedTourney.getName());
            tourneyContent.getChildren().add(new Label("No hay partidos disponibles para este torneo"));
        } else {
            tourneyContent.getChildren().add(roundsAccordion);
        }

        tourneyPane.setContent(tourneyContent);
        matchAccordion.getPanes().add(tourneyPane);
        updateGoalsGraphicForTourney(selectedTourney);
    }
    
    private List<MatchDetails> inferMatchesFromOpponents(Team team, List<Team> allTeamsInTourney) {
        List<MatchDetails> inferredMatches = new ArrayList<>();
        Set<String> matchSignatures = new HashSet<>(); // Para evitar duplicados

        for (Team otherTeam : allTeamsInTourney) {
            if (otherTeam.getId() == team.getId()) {
                continue;
            }
            if (otherTeam.getEncounterList() != null) {
                for (MatchDetails match : otherTeam.getEncounterList()) {
                    String opponentName = match.getNameFirstTeam().equals(otherTeam.getName())
                            ? match.getNameSecondTeam() : match.getNameFirstTeam();
                    if (opponentName != null && opponentName.trim().toLowerCase().equals(team.getName().trim().toLowerCase())) {
                        // Crear una firma única para el partido
                        String matchSignature = match.getNameFirstTeam() + "-" + match.getNameSecondTeam() + "-"
                                + match.getCounterFirstTeamGoals() + "-" + match.getCounterSecondTeamGoals();
                        if (matchSignatures.add(matchSignature)) { // Solo añadir si no está duplicado
                            inferredMatches.add(match);
                            System.out.println("Partido inferido encontrado: " + team.getName() + " vs " + otherTeam.getName());
                        } else {
                            System.out.println("Partido duplicado descartado: " + matchSignature);
                        }
                    }
                }
            }
        }
        return inferredMatches;
    }

// Método auxiliar para procesar un partido
    private void processMatch(MatchDetails match, VBox roundContent, Team updatedTeam, List<Team> allTeamsInTourney) {
        try {
            if (match == null) {
                System.out.println("Partido nulo encontrado en la lista de partidos.");
                return;
            }

            String nameFirstTeam = match.getNameFirstTeam() != null ? match.getNameFirstTeam() : "Equipo 1 Desconocido";
            String nameSecondTeam = match.getNameSecondTeam() != null ? match.getNameSecondTeam() : "Equipo 2 Desconocido";

            String opponentName = nameFirstTeam.equals(updatedTeam.getName()) ? nameSecondTeam : nameFirstTeam;

            int teamGoals = nameFirstTeam.equals(updatedTeam.getName())
                    ? match.getCounterFirstTeamGoals() : match.getCounterSecondTeamGoals();
            int opponentGoals = nameFirstTeam.equals(updatedTeam.getName())
                    ? match.getCounterSecondTeamGoals() : match.getCounterFirstTeamGoals();

            int teamPoints = teamGoals;
            int opponentPoints = opponentGoals;
            if (teamGoals > opponentGoals) {
                teamPoints += 3;
            } else if (teamGoals < opponentGoals) {
                opponentPoints += 3;
            } else {
                teamPoints += 1;
                opponentPoints += 1;
            }

            String result = teamGoals > opponentGoals ? "Resultado: " + updatedTeam.getName() + " Ganó"
                    : teamGoals < opponentGoals ? "Resultado: " + updatedTeam.getName() + " Perdió"
                            : "Resultado: Empate";

            HBox matchDetails = new HBox(10);
            matchDetails.setAlignment(Pos.BOTTOM_LEFT);
            VBox textDetails = new VBox(5);
            Label matchLabel = new Label(updatedTeam.getName() + " vs " + opponentName + " - " + teamGoals + " - " + opponentGoals);
            Label opponentLabel = new Label("Contrincante: " + opponentName);
            Label teamGoalsLabel = new Label("Goles de " + updatedTeam.getName() + ": " + teamGoals);
            Label opponentGoalsLabel = new Label("Goles de " + opponentName + ": " + opponentGoals);
            Label teamPointsLabel = new Label("Puntos de " + updatedTeam.getName() + ": " + teamPoints);
            Label opponentPointsLabel = new Label("Puntos de " + opponentName + ": " + opponentPoints);
            Label drawLabel = new Label("Empate: " + (match.isDraw() ? "Sí" : "No"));
            Label resultLabel = new Label(result);

            textDetails.getChildren().addAll(
                    matchLabel, opponentLabel, teamGoalsLabel, opponentGoalsLabel,
                    teamPointsLabel, opponentPointsLabel, drawLabel, resultLabel
            );

            Canvas goalsBarCanvas = new Canvas(200, 100);
            GraphicsContext gc = goalsBarCanvas.getGraphicsContext2D();
            double maxGoals = Math.max(teamGoals, opponentGoals);
            maxGoals = Math.max(maxGoals, 1);
            double barHeightTeam = (teamGoals / maxGoals) * 80;
            double barHeightOpponent = (opponentGoals / maxGoals) * 80;
            double barWidth = 40;

            gc.setFill(Color.BLUE);
            gc.fillRect(50, 100 - barHeightTeam, barWidth, barHeightTeam);
            gc.setFill(Color.RED);
            gc.fillRect(100, 100 - barHeightOpponent, barWidth, barHeightOpponent);
            gc.setFill(Color.BLACK);
            gc.fillText(updatedTeam.getName() + ": " + teamGoals, 50, 100 - barHeightTeam - 5);
            gc.fillText(opponentName + ": " + opponentGoals, 100, 100 - barHeightOpponent - 5);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            matchDetails.getChildren().addAll(textDetails, spacer, goalsBarCanvas);
            roundContent.getChildren().add(matchDetails);
            System.out.println("Partido añadido: " + matchLabel.getText());

        } catch (Exception e) {
            System.out.println("Error al procesar partido: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void handleExceptionCase(Accordion roundsAccordion, List<Team> allTeamsInTourney, int lastRoundParticipated, boolean isWinner, boolean isLooser) {
        // Encontrar al oponente (el otro equipo en el torneo)
        String opponentName = allTeamsInTourney.stream()
                .filter(t -> t != null && t.getName() != null && !t.getName().equals(updatedTeam.getName()))
                .map(Team::getName)
                .findFirst()
                .orElse("Desconocido");

        // Deducir un solo partido (torneo de 2 equipos = 1 partido)
        int numberOfMatches = 1; // Solo un partido, ya que es un torneo de 2 equipos
        int teamGoalsTotal = 1; // Asumimos 1 gol para la victoria (resultado 1-0)
        boolean isWinnerDeduced = isWinner; // Usamos el estado del torneo (Elmolon es el ganador)

        // Mostrar el partido deducido
        int roundNumber = 1;
        int teamGoals = teamGoalsTotal; // 1 gol
        int opponentGoals = isWinnerDeduced ? 0 : teamGoals + 1; // Si ganó, oponente tiene 0; si perdió, oponente tiene más goles

        int teamPoints = teamGoals;
        int opponentPoints = opponentGoals;
        if (teamGoals > opponentGoals) {
            teamPoints += 3;
            opponentPoints += 0;
        } else if (teamGoals < opponentGoals) {
            teamPoints += 0;
            opponentPoints += 3;
        } else {
            teamPoints += 1;
            opponentPoints += 1;
        }

        String result = teamGoals > opponentGoals ? "Ganó" : teamGoals < opponentGoals ? "Perdió" : "Empate";

        // Crear los detalles del partido deducido
        HBox matchDetails = new HBox(10);
        matchDetails.setAlignment(Pos.BOTTOM_LEFT);
        VBox textDetails = new VBox(5);
        textDetails.getChildren().addAll(
                new Label(updatedTeam.getName() + " vs " + opponentName + " - " + teamGoals + " - " + opponentGoals + " (Deducido)"),
                new Label("Contrincante: " + opponentName),
                new Label("Goles de " + updatedTeam.getName() + ": " + teamGoals),
                new Label("Goles de " + opponentName + ": " + opponentGoals),
                new Label("Puntos de " + updatedTeam.getName() + ": " + teamPoints),
                new Label("Puntos de " + opponentName + ": " + opponentPoints),
                new Label("Empate: " + (teamGoals == opponentGoals ? "Sí" : "No")),
                new Label("Resultado: " + updatedTeam.getName() + " " + result)
        );

        // Crear el gráfico de goles
        Canvas goalsBarCanvas = new Canvas(200, 100);
        GraphicsContext gc = goalsBarCanvas.getGraphicsContext2D();
        double maxGoals = Math.max(teamGoals, opponentGoals);
        maxGoals = Math.max(maxGoals, 1);
        double barHeightTeam = (teamGoals / maxGoals) * 80;
        double barHeightOpponent = (opponentGoals / maxGoals) * 80;
        double barWidth = 40;

        gc.setFill(Color.BLUE);
        gc.fillRect(50, 100 - barHeightTeam, barWidth, barHeightTeam);
        gc.setFill(Color.RED);
        gc.fillRect(100, 100 - barHeightOpponent, barWidth, barHeightOpponent);
        gc.setFill(Color.BLACK);
        gc.fillText(updatedTeam.getName() + ": " + teamGoals, 50, 100 - barHeightTeam - 5);
        gc.fillText(opponentName + ": " + opponentGoals, 100, 100 - barHeightOpponent - 5);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        matchDetails.getChildren().addAll(textDetails, spacer, goalsBarCanvas);

        // Crear el TitledPane para la ronda deducida
        VBox roundContent = new VBox(matchDetails);
        String roundTitle = isWinnerDeduced ? "Final (Ganador)" : isLooser ? "Final (Perdió)" : "Final";
        TitledPane roundPane = new TitledPane(roundTitle, roundContent);
        roundsAccordion.getPanes().add(roundPane);
    }

// Métodos auxiliares:
    private List<Team> getAllTeamsInTourney(Tourney tourney) {
        List<Team> allTeams = new ArrayList<>();
        allTeams.addAll(tourney.getTeamList());
        allTeams.addAll(tourney.getLoosersList());
        allTeams.addAll(tourney.getRound1());
        allTeams.addAll(tourney.getRound2());
        allTeams.addAll(tourney.getRound3());
        allTeams.addAll(tourney.getRound4());
        allTeams.addAll(tourney.getRound5());
        allTeams.addAll(tourney.getRound6());
        allTeams.addAll(tourney.getWinner());
        return allTeams;
    }

private List<MatchDetails> getMatchesInTourney(Team team, List<Team> allTeamsInTourney) {
    List<MatchDetails> matchesInTourney = new ArrayList<>();
    if (team.getEncounterList() != null) {
        for (MatchDetails match : team.getEncounterList()) {
            // Omitimos el filtrado por tourneyId hasta que esté implementado
            // if (match.getTourneyId() != selectedTourney.getId()) {
            //     continue;
            // }
            String opponentName = match.getNameFirstTeam().equals(team.getName())
                    ? match.getNameSecondTeam()
                    : match.getNameFirstTeam();

// Normalizamos nombres para evitar problemas de formato
            final String normalizedOpponentName = opponentName != null
                    ? opponentName.trim().toLowerCase()
                    : "";

// Usamos la variable finalizada para la expresión lambda
            boolean opponentInTourney = allTeamsInTourney.stream()
                    .anyMatch(t -> t.getName() != null && t.getName().trim().toLowerCase().equals(normalizedOpponentName));
            if (opponentInTourney) {
                matchesInTourney.add(match);
                System.out.println("Partido válido encontrado: " + team.getName() + " vs " + opponentName);
            } else {
                System.out.println("Partido descartado: " + team.getName() + " vs " + opponentName + " (oponente no está en el torneo)");
            }
        }
    }
    return matchesInTourney;
}

    private int determineLastRoundParticipated(Tourney tourney, Team team) {
        // Obtener todos los equipos del torneo
        List<Team> allTeamsInTourney = getAllTeamsInTourney(tourney);
        int numberOfTeams = allTeamsInTourney.size();

        // Calcular el número máximo de rondas posibles (log2 del número de equipos, redondeado hacia arriba)
        int maxRounds = numberOfTeams <= 1 ? 0 : (int) Math.ceil(Math.log(numberOfTeams) / Math.log(2));

        // Revisar las rondas en orden descendente
        if (tourney.getWinner().stream().anyMatch(t -> t.getId() == team.getId())) {
            // El ganador participó hasta la última ronda posible
            return maxRounds;
        }
        if (tourney.getRound6().stream().anyMatch(t -> t.getId() == team.getId()) && maxRounds >= 6) {
            return 6;
        }
        if (tourney.getRound5().stream().anyMatch(t -> t.getId() == team.getId()) && maxRounds >= 5) {
            return 5;
        }
        if (tourney.getRound4().stream().anyMatch(t -> t.getId() == team.getId()) && maxRounds >= 4) {
            return 4;
        }
        if (tourney.getRound3().stream().anyMatch(t -> t.getId() == team.getId()) && maxRounds >= 3) {
            return 3;
        }
        if (tourney.getRound2().stream().anyMatch(t -> t.getId() == team.getId()) && maxRounds >= 2) {
            return 2;
        }
        if (tourney.getRound1().stream().anyMatch(t -> t.getId() == team.getId()) && maxRounds >= 1) {
            return 1;
        }
        return 0;
    }
    
    private String getRoundTitle(int roundNumber, int lastRound, boolean isWinner, boolean isLooser) {
        if (isWinner && roundNumber == lastRound - 1) {
            return "Final (Ganador)";
        }
        if (isLooser && roundNumber == lastRound) {
            return "Ronda " + roundNumber + " (Perdió)";
        }
        return "Ronda " + roundNumber;
    }

    

    private void clearCanvases() {
        matchGoalsCanvas.getGraphicsContext2D().clearRect(0, 0, matchGoalsCanvas.getWidth(), matchGoalsCanvas.getHeight());
        pointsPerMatchCanvas.getGraphicsContext2D().clearRect(0, 0, pointsPerMatchCanvas.getWidth(), pointsPerMatchCanvas.getHeight());
        tourneyPointsPerMatchCanvas.getGraphicsContext2D().clearRect(0, 0, tourneyPointsPerMatchCanvas.getWidth(), tourneyPointsPerMatchCanvas.getHeight());
        globalRankingCanvas.getGraphicsContext2D().clearRect(0, 0, globalRankingCanvas.getWidth(), globalRankingCanvas.getHeight());
    }

    @Override
    public void initialize() {
    }
}