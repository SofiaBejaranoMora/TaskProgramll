
package cr.ac.una.taskprogramll.controller;
import javafx.application.Platform;

import cr.ac.una.taskprogramll.model.FileManager;
import cr.ac.una.taskprogramll.model.Game;
import cr.ac.una.taskprogramll.model.MatchDetails;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    private Canvas globalRankingCanvas; // Nuevo Canvas para la gráfica de ranking global

    private Team updatedTeam;
    private Team selectedTeam;
    private final Mensaje message = new Mensaje();
    private FileManager fileManager = new FileManager();
    private ObservableList<Team> availableTeams = FXCollections.observableArrayList();
    private ObservableList<Tourney> availableTourneys = FXCollections.observableArrayList();
    private final List<Sport> sportList = new ArrayList<>();
    @FXML
    private VBox pointsPerTourneyLegend;

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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTableColumns();
        loadData();
        loadTourneys();
        loadSports();
        setupTeamSelectionListener();
        setupTeamFilterDisplay();
        setupFilters();
    }

    private void setupTableColumns() {
        // Configurar columnas de teamTable
        TableColumn<Team, Integer> teamIdColumn = new TableColumn<>("ID");
        teamIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        teamNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        teamPointsColumn.setCellValueFactory(cellData -> {
            Team team = cellData.getValue();
            int globalPoints = calculateGlobalPoints(team);
            return javafx.beans.binding.Bindings.createObjectBinding(() -> globalPoints);
        });
        teamRankingColumn.setCellValueFactory(cellData -> {
            Team team = cellData.getValue();
            List<Team> sortedTeams = new ArrayList<>(availableTeams);
            sortedTeams.sort((t1, t2) -> Integer.compare(calculateGlobalPoints(t2), calculateGlobalPoints(t1)));
            int rank = sortedTeams.indexOf(team) + 1;
            return new SimpleStringProperty("Posición: " + rank);
        });
        teamTable.getColumns().clear();
        teamTable.getColumns().addAll(teamIdColumn, teamNameColumn, teamPointsColumn, teamRankingColumn);

        // Configurar columnas de tourneyTable
        TableColumn<Tourney, Integer> tourneyIdColumn = new TableColumn<>("ID");
        tourneyIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        tourneyNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        tourneyMinutesColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        tourneySportColumn.setCellValueFactory(cellData -> {
            Tourney tourney = cellData.getValue();
            Sport sport = sportList.stream()
                    .filter(s -> s.getId() == tourney.getSportTypeId())
                    .findFirst()
                    .orElse(null);
            String sportName = sport != null ? sport.getName() : "Sin deporte";
            return new SimpleStringProperty(sportName);
        });
        tourneyStateColumn.setCellValueFactory(cellData -> {
            Tourney tourney = cellData.getValue();
            return new SimpleStringProperty(tourney.returnState());
        });
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

        // Configurar columnas de tourneyStatsTable con TourneyStats
        tourneyStatsNameColumn.setCellValueFactory(cellData -> cellData.getValue().tourneyNameProperty());
        tourneyStatsPointsColumn.setCellValueFactory(cellData -> cellData.getValue().pointsProperty().asObject());
        tourneyStatsGoalsColumn.setCellValueFactory(cellData -> cellData.getValue().goalsProperty().asObject());
        tourneyStatsRankingColumn.setCellValueFactory(cellData -> cellData.getValue().rankingProperty());
    }

    private void loadData() {
        if (AppContext.getInstance().get("teams") == null) {
            AppContext.getInstance().set("teams", new ArrayList<Team>());
        }
        @SuppressWarnings("unchecked")
        List<Team> teams = (List<Team>) AppContext.getInstance().get("teams");
        System.out.println("Equipos iniciales en AppContext: " + teams);
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
                try {
                    System.out.println("Deserializando Team.txt...");
                    List<Team> deserializedTeams = fileManager.deserialization("Team", Team.class);
                    if (deserializedTeams == null || deserializedTeams.isEmpty()) {
                        System.out.println("Deserialización de Team.txt devolvió null o lista vacía.");
                        message.show(Alert.AlertType.WARNING, "Sin datos", "No se encontraron equipos en Team.txt.");
                    } else {
                        teams.addAll(deserializedTeams);
                        AppContext.getInstance().set("teams", teams);
                        System.out.println("Equipos cargados y añadidos a AppContext: " + teams);
                    }
                } catch (Exception e) {
                    System.out.println("Error al deserializar Team.txt: " + e.getMessage());
                    e.printStackTrace();
                    message.show(Alert.AlertType.ERROR, "Error al Cargar Equipos", "No se pudieron cargar los equipos: " + e.getMessage());
                    return;
                }
            }
        } else {
            System.out.println("Equipos ya cargados en AppContext: " + teams);
        }

        availableTeams.clear();
        availableTeams.addAll(teams);
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

    private void setupTeamFilterDisplay() {
        teamFilter.setConverter(new StringConverter<Team>() {
            @Override
            public String toString(Team team) {
                return team != null ? team.getName() : "";
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
        String query = teamSearch.getText() != null ? teamSearch.getText().trim().toLowerCase() : "";
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
            Game game = tourney.getContinueGame();
            allTeamsInTourney.addAll(game.getRound1());
            allTeamsInTourney.addAll(game.getRound2());
            allTeamsInTourney.addAll(game.getRound3());
            allTeamsInTourney.addAll(game.getRound4());
            allTeamsInTourney.addAll(game.getRound5());
            allTeamsInTourney.addAll(game.getRound6());
            allTeamsInTourney.addAll(game.getWinner());

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
            Game game = tourney.getContinueGame();
            allTeamsInTourney.addAll(game.getRound1());
            allTeamsInTourney.addAll(game.getRound2());
            allTeamsInTourney.addAll(game.getRound3());
            allTeamsInTourney.addAll(game.getRound4());
            allTeamsInTourney.addAll(game.getRound5());
            allTeamsInTourney.addAll(game.getRound6());
            allTeamsInTourney.addAll(game.getWinner());

            for (Team tourneyTeam : allTeamsInTourney) {
                if (tourneyTeam.getId() == team.getId()) {
                    int tourneyPoints = 0;
                    List<MatchDetails> matches = tourneyTeam.getEncounterList();
                    if (matches != null) {
                        for (MatchDetails match : matches) {
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
        // --- Gráfica: Puntos por Torneo (Global) ---
        GraphicsContext gcPoints = pointsPerMatchCanvas.getGraphicsContext2D();
        gcPoints.clearRect(0, 0, pointsPerMatchCanvas.getWidth(), pointsPerMatchCanvas.getHeight());
        pointsPerTourneyLegend.getChildren().clear(); // Limpiar la leyenda

        List<String> tourneyNames = new ArrayList<>();
        List<Integer> tourneyPointsList = new ArrayList<>();

        // Recolectar los nombres de los torneos y los puntos
        for (Tourney tourney : availableTourneys) {
            List<Team> allTeamsInTourney = new ArrayList<>();
            allTeamsInTourney.addAll(tourney.getTeamList());
            allTeamsInTourney.addAll(tourney.getLoosersList());
            Game game = tourney.getContinueGame();
            allTeamsInTourney.addAll(game.getRound1());
            allTeamsInTourney.addAll(game.getRound2());
            allTeamsInTourney.addAll(game.getRound3());
            allTeamsInTourney.addAll(game.getRound4());
            allTeamsInTourney.addAll(game.getRound5());
            allTeamsInTourney.addAll(game.getRound6());
            allTeamsInTourney.addAll(game.getWinner());

            for (Team tourneyTeam : allTeamsInTourney) {
                if (tourneyTeam.getId() == updatedTeam.getId()) { // Usar el campo de clase
                    int tourneyPoints = 0;
                    List<MatchDetails> matches = tourneyTeam.getEncounterList();
                    if (matches != null) {
                        for (MatchDetails match : matches) {
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

        // Dibujar las barras y añadir la leyenda
        for (int index = 0; index < tourneyNames.size(); index++) {
            String tourneyName = tourneyNames.get(index);
            int points = tourneyPointsList.get(index);
            double barHeight = points * scale;

            gcPoints.setFill(Color.PURPLE);
            double x = 20 + index * (barWidth + 10);
            gcPoints.fillRect(x, 200 - barHeight, barWidth, barHeight);

            gcPoints.setFill(Color.BLACK);
            gcPoints.fillText(String.valueOf(points), x + barWidth / 2 - 10, 200 - barHeight - 10);

            // Añadir el nombre del torneo y los puntos al VBox como leyenda
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
                barWidth = 5; // Ancho mínimo para que se vea
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

        // Forzar el renderizado del Canvas
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
            Game game = tourney.getContinueGame();
            allTeamsInTourney.addAll(game.getRound1());
            allTeamsInTourney.addAll(game.getRound2());
            allTeamsInTourney.addAll(game.getRound3());
            allTeamsInTourney.addAll(game.getRound4());
            allTeamsInTourney.addAll(game.getRound5());
            allTeamsInTourney.addAll(game.getRound6());
            allTeamsInTourney.addAll(game.getWinner());

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
                            int teamGoals = match.getNameFirstTeam().equals(tourneyTeam.getName())
                                    ? match.getCounterFirstTeamGoals() : match.getCounterSecondTeamGoals();
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

            // Calcular la posición inicial y final del grupo de barras
            double startX = 20 + barIndex * (barWidthRound + 10);
            for (int j = 0; j < rounds.size(); j++) {
                int goals = goalsPerRound.get(j);
                double barHeight = goals * scaleGoals;

                if (barHeight < 5) {
                    barHeight = 5;
                }

                gcTourneyPoints.setFill(Color.GREEN);
                double x = 20 + barIndex * (barWidthRound + 10);
                double y = tourneyPointsPerMatchCanvas.getHeight() - barHeight - 30; // Dejar espacio para el título
                gcTourneyPoints.fillRect(x, y, barWidthRound, barHeight);

                gcTourneyPoints.setFill(Color.BLACK);
                gcTourneyPoints.fillText(String.valueOf(goals), x + barWidthRound / 2 - 10, y - 10);

                System.out.println("Barra " + (barIndex + 1) + ": x=" + x + ", y=" + y + ", ancho=" + barWidthRound + ", alto=" + barHeight);

                barIndex++;
            }
            double endX = 20 + (barIndex - 1) * (barWidthRound + 10);

            // Dibujar el nombre del torneo debajo del grupo de barras
            gcTourneyPoints.setFill(Color.BLACK);
            double titleX = startX + (endX - startX) / 2 - (tourneyName.length() * 3); // Aproximar el centro
            gcTourneyPoints.fillText(tourneyName, titleX, tourneyPointsPerMatchCanvas.getHeight() - 5);

            // Añadir un espacio visual entre torneos
            barIndex++; // Incrementar el índice para dejar un espacio
        }

        tourneyPointsPerMatchCanvas.setWidth(Math.max(300, 20 + (totalBars + tourneyNames.size()) * (barWidthRound + 10)));
        tourneyPointsPerMatchCanvas.setHeight(230); // Aumentar la altura para el título

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
            updatedTeam = null; // Asegurarnos de limpiar el campo
            return;
        }

        updatedTeam = findUpdatedTeamInTourneys(selectedTeam); // Asignar al campo de clase
        if (updatedTeam == null) {
            System.out.println("Equipo no encontrado en torneos para actualizar el tab de estadísticas.");
            tourneyStatsTable.getItems().clear();
            totalPointsLabel.setText("Puntos Totales: 0");
            totalMatchesLabel.setText("Partidos Jugados: 0");
            generalRankingLabel.setText("Ranking General: N/A");
            tournamentsLabel.setText("Torneos Participados: 0");
            clearCanvases();
            updatedTeam = null; // Asegurarnos de limpiar el campo
            return;
        }

        System.out.println("Actualizando tab de estadísticas para el equipo: " + updatedTeam.getName());

        // --- Estadísticas Generales ---
        int totalPoints = calculateGlobalPoints(updatedTeam);
        totalPointsLabel.setText("Puntos Totales: " + totalPoints);

        int totalMatches = updatedTeam.getEncounterList() != null ? updatedTeam.getEncounterList().size() : 0;
        totalMatchesLabel.setText("Partidos Jugados: " + totalMatches);

        // Cantidad de torneos participados
        Set<String> tournamentsParticipated = new HashSet<>();
        for (Tourney tourney : availableTourneys) {
            List<Team> allTeamsInTourney = new ArrayList<>();
            allTeamsInTourney.addAll(tourney.getTeamList());
            allTeamsInTourney.addAll(tourney.getLoosersList());
            Game game = tourney.getContinueGame();
            allTeamsInTourney.addAll(game.getRound1());
            allTeamsInTourney.addAll(game.getRound2());
            allTeamsInTourney.addAll(game.getRound3());
            allTeamsInTourney.addAll(game.getRound4());
            allTeamsInTourney.addAll(game.getRound5());
            allTeamsInTourney.addAll(game.getRound6());
            allTeamsInTourney.addAll(game.getWinner());

            if (allTeamsInTourney.stream().anyMatch(t -> t.getId() == updatedTeam.getId())) {
                tournamentsParticipated.add(tourney.getName());
            }
        }
        tournamentsLabel.setText("Torneos Participados: " + tournamentsParticipated.size());

        // Ranking global mejorado (para el mismo deporte)
        List<Team> allTeamsInSport = new ArrayList<>();
        for (Tourney tourney : availableTourneys) {
            if (tourney.getSportTypeId() == updatedTeam.getIdSportType()) {
                List<Team> teamsInTourney = new ArrayList<>();
                teamsInTourney.addAll(tourney.getTeamList());
                teamsInTourney.addAll(tourney.getLoosersList());
                Game game = tourney.getContinueGame();
                teamsInTourney.addAll(game.getRound1());
                teamsInTourney.addAll(game.getRound2());
                teamsInTourney.addAll(game.getRound3());
                teamsInTourney.addAll(game.getRound4());
                teamsInTourney.addAll(game.getRound5());
                teamsInTourney.addAll(game.getRound6());
                teamsInTourney.addAll(game.getWinner());
                allTeamsInSport.addAll(teamsInTourney);
            }
        }

        // Evitar duplicados sin usar Map
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

        // Actualizar las gráficas
        globalTourneyPointsGraphic();
        globalSportRankingGraphic(rankedTeams);

        // --- Estadísticas por Torneo (Tabla) ---
        tourneyStatsTable.getItems().clear();
        for (Tourney tourney : availableTourneys) {
            List<Team> allTeamsInTourney = new ArrayList<>();
            allTeamsInTourney.addAll(tourney.getTeamList());
            allTeamsInTourney.addAll(tourney.getLoosersList());
            Game game = tourney.getContinueGame();
            allTeamsInTourney.addAll(game.getRound1());
            allTeamsInTourney.addAll(game.getRound2());
            allTeamsInTourney.addAll(game.getRound3());
            allTeamsInTourney.addAll(game.getRound4());
            allTeamsInTourney.addAll(game.getRound5());
            allTeamsInTourney.addAll(game.getRound6());
            allTeamsInTourney.addAll(game.getWinner());

            for (Team tourneyTeam : allTeamsInTourney) {
                if (tourneyTeam.getId() == updatedTeam.getId()) {
                    int tourneyPoints = 0;
                    int tourneyGoals = 0;
                    List<MatchDetails> matches = tourneyTeam.getEncounterList();
                    if (matches != null) {
                        for (MatchDetails match : matches) {
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

        // Actualizar la gráfica de goles por ronda
        tourneyGoalsPerRoundGraphic();
    }
    
    private void updateMatchAccordion() {
        matchAccordion.getPanes().clear();
        if (selectedTeam == null) {
            System.out.println("No hay equipo seleccionado para actualizar matchAccordion.");
            return;
        }

        Team updatedTeam = findUpdatedTeamInTourneys(selectedTeam);
        if (updatedTeam == null) {
            System.out.println("Equipo no encontrado en torneos para actualizar matchAccordion.");
            return;
        }

        System.out.println("Actualizando matchAccordion para el equipo: " + updatedTeam.getName());
        System.out.println("Lista de partidos del equipo: " + updatedTeam.getEncounterList());

        for (Tourney tourney : availableTourneys) {
            if (tourney.getTeamList().stream().anyMatch(t -> t.getId() == updatedTeam.getId())
                    || tourney.getLoosersList().stream().anyMatch(t -> t.getId() == updatedTeam.getId())) {
                System.out.println("Equipo " + updatedTeam.getName() + " participa en torneo: " + tourney.getName());

                TitledPane tourneyPane = new TitledPane();
                tourneyPane.setText(tourney.getName());
                VBox tourneyContent = new VBox();
                Accordion roundsAccordion = new Accordion();

                List<MatchDetails> matches = updatedTeam.getEncounterList();
                System.out.println("Partidos encontrados para " + updatedTeam.getName() + " en torneo " + tourney.getName() + ": " + matches);

                Game game = tourney.getContinueGame();
                int lastRoundParticipated = 0;

                if (game.getRound1().stream().anyMatch(t -> t.getId() == updatedTeam.getId())) {
                    lastRoundParticipated = 1;
                }
                if (game.getRound2().stream().anyMatch(t -> t.getId() == updatedTeam.getId())) {
                    lastRoundParticipated = 2;
                }
                if (game.getRound3().stream().anyMatch(t -> t.getId() == updatedTeam.getId())) {
                    lastRoundParticipated = 3;
                }
                if (game.getRound4().stream().anyMatch(t -> t.getId() == updatedTeam.getId())) {
                    lastRoundParticipated = 4;
                }
                if (game.getRound5().stream().anyMatch(t -> t.getId() == updatedTeam.getId())) {
                    lastRoundParticipated = 5;
                }
                if (game.getRound6().stream().anyMatch(t -> t.getId() == updatedTeam.getId())) {
                    lastRoundParticipated = 6;
                }
                if (game.getWinner().stream().anyMatch(t -> t.getId() == updatedTeam.getId())) {
                    lastRoundParticipated = 7;
                }

                boolean isLooser = tourney.getLoosersList().stream().anyMatch(t -> t.getId() == updatedTeam.getId());

                if (matches != null && !matches.isEmpty()) {
                    List<VBox> roundContents = new ArrayList<>();
                    for (int i = 0; i < lastRoundParticipated; i++) {
                        roundContents.add(new VBox());
                    }

                    int matchIndex = 0;
                    for (MatchDetails match : matches) {
                        try {
                            if (match == null) {
                                System.out.println("Partido nulo encontrado en la lista de partidos.");
                                continue;
                            }

                            String nameFirstTeam = match.getNameFirstTeam() != null ? match.getNameFirstTeam() : "Equipo 1 Desconocido";
                            String nameSecondTeam = match.getNameSecondTeam() != null ? match.getNameSecondTeam() : "Equipo 2 Desconocido";
                            String opponentName = nameFirstTeam.equals(updatedTeam.getName()) ? nameSecondTeam : nameFirstTeam;
                            int teamGoals = nameFirstTeam.equals(updatedTeam.getName()) ? match.getCounterFirstTeamGoals() : match.getCounterSecondTeamGoals();
                            int opponentGoals = nameFirstTeam.equals(updatedTeam.getName()) ? match.getCounterSecondTeamGoals() : match.getCounterFirstTeamGoals();

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

                            String result = teamGoals > opponentGoals ? "Resultado: " + updatedTeam.getName() + " Ganó"
                                    : teamGoals < opponentGoals ? "Resultado: " + updatedTeam.getName() + " Perdió"
                                            : "Resultado: Empate";

                            int matchesPerRound = tourney.getTeamList().size() / 2;
                            if (matchesPerRound <= 0) {
                                matchesPerRound = 1;
                            }
                            int matchRound = Math.min((matchIndex / matchesPerRound) + 1, lastRoundParticipated);

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
                            roundContents.get(matchRound - 1).getChildren().add(matchDetails);
                            System.out.println("Partido añadido a la ronda " + matchRound + ": " + matchLabel.getText());
                            matchIndex++;
                        } catch (Exception e) {
                            System.out.println("Error al procesar partido: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }

                    for (int i = 0; i < roundContents.size(); i++) {
                        if (!roundContents.get(i).getChildren().isEmpty()) {
                            String roundTitle = "Ronda " + (i + 1);
                            if (isLooser && (i + 1) == lastRoundParticipated) {
                                roundTitle += " (Perdió)";
                            } else if (lastRoundParticipated == 7 && (i + 1) == lastRoundParticipated - 1) {
                                roundTitle = "Final (Ganador)";
                            }
                            TitledPane roundPane = new TitledPane(roundTitle, roundContents.get(i));
                            roundsAccordion.getPanes().add(roundPane);
                        }
                    }
                }

                if (roundsAccordion.getPanes().isEmpty()) {
                    tourneyContent.getChildren().add(new Label("No hay partidos disponibles para este torneo"));
                    System.out.println("No se añadieron rondas al roundsAccordion para el torneo " + tourney.getName());
                } else {
                    tourneyContent.getChildren().add(roundsAccordion);
                }

                tourneyPane.setContent(tourneyContent);
                matchAccordion.getPanes().add(tourneyPane);
            }
        }

        // Dibujar en matchGoalsCanvas (gráfico de pastel: goles por ronda)
        GraphicsContext gcGoals = matchGoalsCanvas.getGraphicsContext2D();
        gcGoals.clearRect(0, 0, matchGoalsCanvas.getWidth(), matchGoalsCanvas.getHeight());
        double width = matchGoalsCanvas.getWidth();
        double height = matchGoalsCanvas.getHeight();

        int lastRoundParticipated = 0;
        for (Tourney tourney : availableTourneys) {
            Game game = tourney.getContinueGame();
            if (game.getRound1().stream().anyMatch(t -> t.getId() == updatedTeam.getId())) {
                lastRoundParticipated = Math.max(lastRoundParticipated, 1);
            }
            if (game.getRound2().stream().anyMatch(t -> t.getId() == updatedTeam.getId())) {
                lastRoundParticipated = Math.max(lastRoundParticipated, 2);
            }
            if (game.getRound3().stream().anyMatch(t -> t.getId() == updatedTeam.getId())) {
                lastRoundParticipated = Math.max(lastRoundParticipated, 3);
            }
            if (game.getRound4().stream().anyMatch(t -> t.getId() == updatedTeam.getId())) {
                lastRoundParticipated = Math.max(lastRoundParticipated, 4);
            }
            if (game.getRound5().stream().anyMatch(t -> t.getId() == updatedTeam.getId())) {
                lastRoundParticipated = Math.max(lastRoundParticipated, 5);
            }
            if (game.getRound6().stream().anyMatch(t -> t.getId() == updatedTeam.getId())) {
                lastRoundParticipated = Math.max(lastRoundParticipated, 6);
            }
            if (game.getWinner().stream().anyMatch(t -> t.getId() == updatedTeam.getId())) {
                lastRoundParticipated = Math.max(lastRoundParticipated, 7);
            }
        }

        int[] goalsPerRound = new int[lastRoundParticipated];
        List<MatchDetails> matches = updatedTeam.getEncounterList();
        if (matches != null) {
            int matchIndex = 0;
            for (MatchDetails match : matches) {
                int teamGoals = match.getNameFirstTeam().equals(updatedTeam.getName())
                        ? match.getCounterFirstTeamGoals() : match.getCounterSecondTeamGoals();
                int matchesPerRound = availableTourneys.isEmpty() ? 1 : availableTourneys.get(0).getTeamList().size() / 2;
                if (matchesPerRound <= 0) {
                    matchesPerRound = 1;
                }
                int matchRound = Math.min((matchIndex / matchesPerRound) + 1, lastRoundParticipated);
                goalsPerRound[matchRound - 1] += teamGoals;
                matchIndex++;
            }
        }

        int totalGoals = 0;
        for (int goals : goalsPerRound) {
            totalGoals += goals;
        }

        if (totalGoals == 0) {
            gcGoals.setFill(Color.BLACK);
            gcGoals.fillText("No hay goles registrados", width / 4, height / 2);
        } else {
            double centerX = width / 2;
            double centerY = height / 3;
            double radius = Math.min(width, height) / 3;
            double startAngle = 0;

            Color[] colors = new Color[]{Color.LIGHTBLUE, Color.LIGHTGREEN, Color.LIGHTPINK, Color.LIGHTYELLOW, Color.LIGHTCORAL, Color.LIGHTGRAY, Color.LIGHTCYAN};

            int colorIndex = 0;
            for (int round = 1; round <= lastRoundParticipated; round++) {
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
            for (int round = 1; round <= lastRoundParticipated; round++) {
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