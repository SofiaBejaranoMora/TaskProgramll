package cr.ac.una.taskprogramll.controller;

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
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.paint.Color;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
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
    private Canvas pointsPerMatchCanvas;
    @FXML
    private TableView<Tourney> tourneyStatsTable;
    @FXML
    private TableColumn<Tourney, String> tourneyStatsNameColumn;
    @FXML
    private TableColumn<Tourney, Integer> tourneyStatsPointsColumn;
    @FXML
    private TableColumn<Tourney, Integer> tourneyStatsGoalsColumn;
    @FXML
    private TableColumn<Tourney, String> tourneyStatsRankingColumn;
    @FXML
    private Canvas tourneyPointsPerMatchCanvas;

    private Team selectedTeam; // Para almacenar el equipo seleccionado en teamTable
    private final Mensaje message = new Mensaje();
    private FileManager fileManager = new FileManager();
    private ObservableList<Team> availableTeams = FXCollections.observableArrayList();
    private ObservableList<Tourney> availableTourneys = FXCollections.observableArrayList();
    private final List<Sport> sportList = new ArrayList<>();

    /**
     * Initializes the controller class.
     */
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

        teamPointsColumn.setCellValueFactory(new PropertyValueFactory<>("points"));

        teamRankingColumn.setCellValueFactory(cellData -> {
            Team team = cellData.getValue();
            List<Team> sortedTeams = new ArrayList<>(availableTeams);
            sortedTeams.sort((t1, t2) -> Integer.compare(t2.getPoints(), t1.getPoints())); // Ordenar de mayor a menor
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
            String sportName;
            if (sport != null) {
                sportName = sport.getName();
            } else {
                sportName = "Sin deporte";
            }
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
            // Buscar la versión actualizada del equipo en el torneo
            Team updatedTeam = findUpdatedTeamInTourneys(selectedTeam);
            if (updatedTeam == null) {
                return new SimpleStringProperty("Equipo no encontrado en torneo");
            }
            return new SimpleStringProperty(tourney.returnTeamPosition(updatedTeam));
        });

        tourneyTable.getColumns().clear();
        tourneyTable.getColumns().addAll(tourneyIdColumn, tourneyNameColumn, tourneyMinutesColumn, tourneySportColumn, tourneyStateColumn, tourneyPositionColumn);

        // Configurar columnas de tourneyStatsTable
        tourneyStatsNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        tourneyStatsPointsColumn.setCellValueFactory(cellData -> {
            Tourney tourney = (Tourney) cellData.getValue();
            if (selectedTeam == null) {
                return new ReadOnlyObjectWrapper<>(0);
            }
            Team updatedTeam = findUpdatedTeamInTourneys(selectedTeam);
            if (updatedTeam == null) {
                return new ReadOnlyObjectWrapper<>(0);
            }
            return new ReadOnlyObjectWrapper<>(updatedTeam.getPoints());
        });
        tourneyStatsGoalsColumn.setCellValueFactory(cellData -> {
            Tourney tourney = (Tourney) cellData.getValue();
            if (selectedTeam == null) {
                return new ReadOnlyObjectWrapper<>(0);
            }
            Team updatedTeam = findUpdatedTeamInTourneys(selectedTeam);
            if (updatedTeam == null) {
                return new ReadOnlyObjectWrapper<>(0);
            }
            return new ReadOnlyObjectWrapper<>(updatedTeam.getGoals());
        });
        tourneyStatsRankingColumn.setCellValueFactory(cellData -> {
            Tourney tourney = (Tourney) cellData.getValue();
            if (selectedTeam == null) {
                return new SimpleStringProperty("Selecciona un equipo");
            }
            Team updatedTeam = findUpdatedTeamInTourneys(selectedTeam);
            if (updatedTeam == null) {
                return new SimpleStringProperty("Equipo no encontrado en torneo");
            }
            return new SimpleStringProperty(tourney.returnTeamPosition(updatedTeam));
        });
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
                tourneyStatsTable.setItems(teamTourneys);
                updateStatsLabels();
                drawTeamStats(); // Esto asegura que los gráficos se actualicen
                updateMatchAccordion();
            } else {
                selectedTeamLabel.setText("Ningún equipo seleccionado");
                tourneyTable.getItems().clear();
                tourneyStatsTable.getItems().clear();
                totalPointsLabel.setText("Puntos: 0");
                totalMatchesLabel.setText("Partidos: 0");
                generalRankingLabel.setText("Ranking: -");
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
                if (team != null) {
                    return team.getName();
                }
                return "";
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
                    if (team.getName().toLowerCase().contains(newText.toLowerCase()) ||
                        String.valueOf(team.getId()).equals(newText)) {
                        filteredTeams.add(team);
                    }
                }
                teamFilter.setItems(filteredTeams);
            }
            applyFilters();
        });
        teamFilter.getSelectionModel().selectedItemProperty().addListener((obs, oldTeam, newTeam) -> {
            applyFilters();
        });
        sportFilter.getSelectionModel().selectedItemProperty().addListener((obs, oldSport, newSport) -> {
            applyFilters();
        });
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

        // Filtrar equipos
        ObservableList<Team> filteredTeams = FXCollections.observableArrayList();
        for (Team team : availableTeams) {
            boolean matchesSearch = query.isEmpty() ||
                team.getName().toLowerCase().contains(query) ||
                String.valueOf(team.getId()).equals(query);
            boolean matchesTeamFilter = selectedTeamFilter == null || team.equals(selectedTeamFilter);
            boolean matchesSportFilter = true;
            if (selectedSport != null) {
                matchesSportFilter = false;
                for (Tourney tourney : availableTourneys) {
                    if ((tourney.getTeamList().stream().anyMatch(t -> t.getId() == team.getId()) ||
                         tourney.getLoosersList().stream().anyMatch(t -> t.getId() == team.getId())) &&
                        tourney.getSportTypeId() == selectedSport.getId()) {
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

    // Nuevo método para buscar el equipo actualizado en los torneos
    private Team findUpdatedTeamInTourneys(Team team) {
        if (team == null) {
            return null;
        }
        for (Tourney tourney : availableTourneys) {
            // Buscar en teamList
            for (Team tourneyTeam : tourney.getTeamList()) {
                if (tourneyTeam.getId() == team.getId()) {
                    return tourneyTeam; // Devolvemos la primera versión encontrada
                }
            }
            // Buscar en loosersList
            for (Team tourneyTeam : tourney.getLoosersList()) {
                if (tourneyTeam.getId() == team.getId()) {
                    return tourneyTeam;
                }
            }
            // Buscar en continueGame (rondas y ganador)
            Game game = tourney.getContinueGame();
            if (game != null) {
                // Round 1
                for (Team tourneyTeam : game.getRound1()) {
                    if (tourneyTeam.getId() == team.getId()) {
                        return tourneyTeam;
                    }
                }
                // Round 2
                for (Team tourneyTeam : game.getRound2()) {
                    if (tourneyTeam.getId() == team.getId()) {
                        return tourneyTeam;
                    }
                }
                // Round 3
                for (Team tourneyTeam : game.getRound3()) {
                    if (tourneyTeam.getId() == team.getId()) {
                        return tourneyTeam;
                    }
                }
                // Round 4
                for (Team tourneyTeam : game.getRound4()) {
                    if (tourneyTeam.getId() == team.getId()) {
                        return tourneyTeam;
                    }
                }
                // Round 5
                for (Team tourneyTeam : game.getRound5()) {
                    if (tourneyTeam.getId() == team.getId()) {
                        return tourneyTeam;
                    }
                }
                // Round 6
                for (Team tourneyTeam : game.getRound6()) {
                    if (tourneyTeam.getId() == team.getId()) {
                        return tourneyTeam;
                    }
                }
                // Winner
                for (Team tourneyTeam : game.getWinner()) {
                    if (tourneyTeam.getId() == team.getId()) {
                        return tourneyTeam;
                    }
                }
            }
        }
        System.out.println("Equipo con ID " + team.getId() + " no encontrado en ningún torneo.");
        return null;
    }

    private void updateStatsLabels() {
        if (selectedTeam == null) {
            totalPointsLabel.setText("Puntos: 0");
            totalMatchesLabel.setText("Partidos: 0");
            generalRankingLabel.setText("Ranking: -");
            return;
        }
        // Usar la versión actualizada del equipo desde los torneos
        Team updatedTeam = findUpdatedTeamInTourneys(selectedTeam);
        if (updatedTeam == null) {
            totalPointsLabel.setText("Puntos: 0");
            totalMatchesLabel.setText("Partidos: 0");
            generalRankingLabel.setText("Ranking: -");
            return;
        }
        int totalPoints = updatedTeam.getPoints();
        int totalMatches = updatedTeam.getEncounterList() != null ? updatedTeam.getEncounterList().size() : 0;
        List<Team> sortedTeams = new ArrayList<>();
        // Recolectar todos los equipos de los torneos para el ranking
        for (Tourney tourney : availableTourneys) {
            sortedTeams.addAll(tourney.getTeamList());
            sortedTeams.addAll(tourney.getLoosersList());
        }
        // Eliminar duplicados por ID
        List<Team> uniqueTeams = new ArrayList<>();
        List<Integer> seenIds = new ArrayList<>();
        for (Team t : sortedTeams) {
            if (!seenIds.contains(t.getId())) {
                seenIds.add(t.getId());
                uniqueTeams.add(t);
            }
        }
        uniqueTeams.sort((t1, t2) -> Integer.compare(t2.getPoints(), t1.getPoints()));
        int rank = uniqueTeams.indexOf(updatedTeam) + 1;

        totalPointsLabel.setText("Puntos: " + totalPoints);
        totalMatchesLabel.setText("Partidos: " + totalMatches);
        generalRankingLabel.setText("Ranking: " + (rank >= 0 ? rank : "-"));
    }

    private void drawTeamStats() {
        // Limpiar los canvas
        clearCanvases();

        if (selectedTeam == null) {
            System.out.println("No hay equipo seleccionado para dibujar estadísticas.");
            return;
        }

        // Usar la versión actualizada del equipo desde los torneos
        Team updatedTeam = findUpdatedTeamInTourneys(selectedTeam);
        if (updatedTeam == null) {
            System.out.println("Equipo no encontrado en torneos para dibujar estadísticas.");
            return;
        }

        System.out.println("Dibujando estadísticas para el equipo: " + updatedTeam.getName());

        // Preparar datos para los gráficos
        List<Integer> goalsPerMatch = new ArrayList<>();
        List<Integer> pointsPerMatch = new ArrayList<>();
        List<Integer> pointsPerTourney = new ArrayList<>();

        // Obtener los torneos en los que participa el equipo
        List<Tourney> teamTourneys = new ArrayList<>();
        for (Tourney tourney : availableTourneys) {
            if (tourney.getTeamList().stream().anyMatch(t -> t.getId() == updatedTeam.getId())
                    || tourney.getLoosersList().stream().anyMatch(t -> t.getId() == updatedTeam.getId())) {
                teamTourneys.add(tourney);
                pointsPerTourney.add(0); // Inicializar los puntos del torneo
            }
        }
        System.out.println("Torneos en los que participa " + updatedTeam.getName() + ": " + teamTourneys);

        // Obtener los partidos del equipo actualizado
        List<MatchDetails> matches = updatedTeam.getEncounterList();
        System.out.println("Partidos del equipo " + updatedTeam.getName() + ": " + matches);

        // Determinar la última ronda en la que participó el equipo
        int lastRoundParticipated = 0;
        for (Tourney tourney : teamTourneys) {
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

        // Usar un arreglo para almacenar los goles por ronda
        int[] goalsPerRound = new int[lastRoundParticipated];

        if (matches != null && !matches.isEmpty()) {
            int matchIndex = 0;
            for (MatchDetails match : matches) {
                // Goles del equipo seleccionado en este partido
                int teamGoals = match.getNameFirstTeam().equals(updatedTeam.getName()) ? match.getCounterFirstTeamGoals() : match.getCounterSecondTeamGoals();
                int opponentGoals = match.getNameFirstTeam().equals(updatedTeam.getName()) ? match.getCounterSecondTeamGoals() : match.getCounterFirstTeamGoals();
                goalsPerMatch.add(teamGoals);

                // Calcular puntos del partido (3 por victoria, 1 por empate, 0 por derrota)
                int points = 0;
                if (teamGoals > opponentGoals) {
                    points = 3; // Victoria
                } else if (teamGoals == opponentGoals) {
                    points = 1; // Empate
                }
                pointsPerMatch.add(points);

                // Estimar la ronda del partido
                int matchesPerRound = teamTourneys.isEmpty() ? 1 : teamTourneys.get(0).getTeamList().size() / 2;
                if (matchesPerRound <= 0) {
                    matchesPerRound = 1;
                }
                int matchRound = Math.min((matchIndex / matchesPerRound) + 1, lastRoundParticipated);

                // Sumar goles a la ronda correspondiente usando el arreglo
                goalsPerRound[matchRound - 1] += teamGoals;

                // Distribuir los puntos entre los torneos (estimación)
                if (!teamTourneys.isEmpty()) {
                    int tourneyIndex = Math.min(matchIndex / (matches.size() / teamTourneys.size() + 1), teamTourneys.size() - 1);
                    pointsPerTourney.set(tourneyIndex, pointsPerTourney.get(tourneyIndex) + points);
                }
                matchIndex++;
            }
        } else {
            System.out.println("No se encontraron partidos para dibujar estadísticas de " + updatedTeam.getName());
        }

        // Dibujar en matchGoalsCanvas (gráfico de pastel: goles por ronda)
        GraphicsContext gcGoals = matchGoalsCanvas.getGraphicsContext2D();
        double width = matchGoalsCanvas.getWidth();
        double height = matchGoalsCanvas.getHeight();

        // Calcular el total de goles para el gráfico de pastel
        int totalGoals = 0;
        for (int goals : goalsPerRound) {
            totalGoals += goals;
        }

        if (totalGoals == 0) {
            gcGoals.setFill(Color.BLACK);
            gcGoals.fillText("No hay goles registrados", width / 4, height / 2);
        } else {
            double centerX = width / 2;
            double centerY = height / 3; // Mover el pastel un poco hacia arriba para dejar espacio a la leyenda
            double radius = Math.min(width, height) / 3;
            double startAngle = 0;

            // Colores para cada ronda
            Color[] colors = new Color[]{Color.LIGHTBLUE, Color.LIGHTGREEN, Color.LIGHTPINK, Color.LIGHTYELLOW, Color.LIGHTCORAL, Color.LIGHTGRAY, Color.LIGHTCYAN};

            // Dibujar el pastel
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

            // Dibujar la leyenda debajo del pastel
            double legendX = centerX - 80; // Centrar la leyenda horizontalmente
            double legendY = centerY + radius + 20; // Posicionar debajo del pastel
            colorIndex = 0;
            for (int round = 1; round <= lastRoundParticipated; round++) {
                int goals = goalsPerRound[round - 1];
                if (goals > 0) {
                    gcGoals.setFill(colors[colorIndex % colors.length]);
                    gcGoals.fillRect(legendX, legendY, 15, 15);
                    gcGoals.setFill(Color.BLACK);
                    gcGoals.fillText("Ronda " + round + ": " + goals + " goles", legendX + 20, legendY + 12);
                    legendY += 20; // Apilar verticalmente
                    colorIndex++;
                }
            }
        }

        // Dibujar en pointsPerMatchCanvas (puntos por partido)
        GraphicsContext gcPoints = pointsPerMatchCanvas.getGraphicsContext2D();
        if (pointsPerMatch.isEmpty()) {
            gcPoints.setFill(Color.BLACK);
            gcPoints.fillText("No hay datos de puntos", width / 4, height / 2);
        } else {
            double barWidth = width / pointsPerMatch.size() * 0.8;
            double maxPoints = Collections.max(pointsPerMatch);
            maxPoints = Math.max(maxPoints, 1); // Evitar división por cero
            for (int i = 0; i < pointsPerMatch.size(); i++) {
                double barHeight = (pointsPerMatch.get(i) / maxPoints) * (height * 0.8);
                gcPoints.setFill(Color.GREEN);
                gcPoints.fillRect(i * barWidth * 1.25, height - barHeight, barWidth, barHeight);
                gcPoints.setFill(Color.BLACK);
                gcPoints.fillText(String.valueOf(pointsPerMatch.get(i)), i * barWidth * 1.25 + barWidth / 4, height - barHeight - 5);
            }
        }

        // Dibujar en tourneyPointsPerMatchCanvas (puntos por torneo)
        GraphicsContext gcTourneyPoints = tourneyPointsPerMatchCanvas.getGraphicsContext2D();
        if (pointsPerTourney.isEmpty()) {
            gcTourneyPoints.setFill(Color.BLACK);
            gcTourneyPoints.fillText("No hay datos de torneos", width / 4, height / 2);
        } else {
            double barWidth = width / pointsPerTourney.size() * 0.8;
            double maxPoints = Collections.max(pointsPerTourney);
            maxPoints = Math.max(maxPoints, 1); // Evitar división por cero
            for (int i = 0; i < pointsPerTourney.size(); i++) {
                double barHeight = (pointsPerTourney.get(i) / maxPoints) * (height * 0.8);
                gcTourneyPoints.setFill(Color.BLUE);
                gcTourneyPoints.fillRect(i * barWidth * 1.25, height - barHeight, barWidth, barHeight);
                gcTourneyPoints.setFill(Color.BLACK);
                gcTourneyPoints.fillText(String.valueOf(pointsPerTourney.get(i)), i * barWidth * 1.25 + barWidth / 4, height - barHeight - 5);
            }
        }
    }

    private void clearCanvases() {
        matchGoalsCanvas.getGraphicsContext2D().clearRect(0, 0, matchGoalsCanvas.getWidth(), matchGoalsCanvas.getHeight());
        pointsPerMatchCanvas.getGraphicsContext2D().clearRect(0, 0, pointsPerMatchCanvas.getWidth(), pointsPerMatchCanvas.getHeight());
        tourneyPointsPerMatchCanvas.getGraphicsContext2D().clearRect(0, 0, tourneyPointsPerMatchCanvas.getWidth(), tourneyPointsPerMatchCanvas.getHeight());
    }
    
    private void updateMatchAccordion() {
        matchAccordion.getPanes().clear();
        if (selectedTeam == null) {
            System.out.println("No hay equipo seleccionado para actualizar matchAccordion.");
            return;
        }

        // Usar la versión actualizada del equipo desde los torneos
        Team updatedTeam = findUpdatedTeamInTourneys(selectedTeam);
        if (updatedTeam == null) {
            System.out.println("Equipo no encontrado en torneos para actualizar matchAccordion.");
            return;
        }

        System.out.println("Actualizando matchAccordion para el equipo: " + updatedTeam.getName());
        System.out.println("Lista de partidos del equipo: " + updatedTeam.getEncounterList());

        // Buscar torneos en los que participa el equipo actualizado
        for (Tourney tourney : availableTourneys) {
            if (tourney.getTeamList().stream().anyMatch(t -> t.getId() == updatedTeam.getId())
                    || tourney.getLoosersList().stream().anyMatch(t -> t.getId() == updatedTeam.getId())) {
                System.out.println("Equipo " + updatedTeam.getName() + " participa en torneo: " + tourney.getName());

                // Crear un TitledPane para el torneo
                TitledPane tourneyPane = new TitledPane();
                tourneyPane.setText(tourney.getName());
                VBox tourneyContent = new VBox();
                Accordion roundsAccordion = new Accordion();

                List<MatchDetails> matches = updatedTeam.getEncounterList();
                System.out.println("Partidos encontrados para " + updatedTeam.getName() + " en torneo " + tourney.getName() + ": " + matches);

                Game game = tourney.getContinueGame();
                int lastRoundParticipated = 0;

                // Determinar en qué rondas participó el equipo
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

                // Si el equipo está en loosersList y no en las rondas posteriores, perdió
                boolean isLooser = tourney.getLoosersList().stream().anyMatch(t -> t.getId() == updatedTeam.getId());

                // Agrupar partidos por ronda
                if (matches != null && !matches.isEmpty()) {
                    // Crear una lista de VBox para cada ronda (hasta lastRoundParticipated)
                    List<VBox> roundContents = new ArrayList<>();
                    for (int i = 0; i < lastRoundParticipated; i++) {
                        roundContents.add(new VBox());
                    }

                    int matchIndex = 0;
                    for (MatchDetails match : matches) {
                        try {
                            System.out.println("Procesando partido: " + match);
                            if (match == null) {
                                System.out.println("Partido nulo encontrado en la lista de partidos.");
                                continue;
                            }

                            // Obtener nombres y goles con manejo de null
                            String nameFirstTeam = match.getNameFirstTeam() != null ? match.getNameFirstTeam() : "Equipo 1 Desconocido";
                            String nameSecondTeam = match.getNameSecondTeam() != null ? match.getNameSecondTeam() : "Equipo 2 Desconocido";
                            String opponentName = nameFirstTeam.equals(updatedTeam.getName()) ? nameSecondTeam : nameFirstTeam;
                            int teamGoals = nameFirstTeam.equals(updatedTeam.getName()) ? match.getCounterFirstTeamGoals() : match.getCounterSecondTeamGoals();
                            int opponentGoals = nameFirstTeam.equals(updatedTeam.getName()) ? match.getCounterSecondTeamGoals() : match.getCounterFirstTeamGoals();

                            // Calcular puntos
                            int teamPoints = 0;
                            int opponentPoints = 0;
                            if (teamGoals > opponentGoals) {
                                teamPoints = 3; // Victoria
                                opponentPoints = 0;
                            } else if (teamGoals < opponentGoals) {
                                teamPoints = 0;
                                opponentPoints = 3; // Derrota
                            } else {
                                teamPoints = 1; // Empate
                                opponentPoints = 1;
                            }

                            // Determinar el resultado
                            String result;
                            if (teamGoals > opponentGoals) {
                                result = "Resultado: " + updatedTeam.getName() + " Ganó";
                            } else if (teamGoals < opponentGoals) {
                                result = "Resultado: " + updatedTeam.getName() + " Perdió";
                            } else {
                                result = "Resultado: Empate";
                            }

                            System.out.println("Partido procesado - Oponente: " + opponentName + ", Goles: " + teamGoals + " - " + opponentGoals);

                            // Estimar la ronda del partido basándonos en el número de partidos por ronda
                            int matchesPerRound = tourney.getTeamList().size() / 2; // Aproximación simple
                            if (matchesPerRound <= 0) {
                                matchesPerRound = 1;
                            }
                            int matchRound = Math.min((matchIndex / matchesPerRound) + 1, lastRoundParticipated);
                            String roundInfo = "Ronda " + matchRound;

                            // Crear los detalles del partido
                            VBox matchDetails = new VBox();
                            Label matchLabel = new Label(updatedTeam.getName() + " vs " + opponentName + " - " + teamGoals + " - " + opponentGoals);
                            Label opponentLabel = new Label("Contrincante: " + opponentName);
                            Label teamGoalsLabel = new Label("Goles de " + updatedTeam.getName() + ": " + teamGoals);
                            Label opponentGoalsLabel = new Label("Goles de " + opponentName + ": " + opponentGoals);
                            Label teamPointsLabel = new Label("Puntos de " + updatedTeam.getName() + ": " + teamPoints);
                            Label opponentPointsLabel = new Label("Puntos de " + opponentName + ": " + opponentPoints);
                            Label drawLabel = new Label("Empate: " + (match.isDraw()? "Sí" : "No"));
                            Label resultLabel = new Label(result);

                            // Crear un Canvas para el gráfico de barras
                            Canvas goalsBarCanvas = new Canvas(200, 100);
                            GraphicsContext gc = goalsBarCanvas.getGraphicsContext2D();
                            double maxGoals = Math.max(teamGoals, opponentGoals);
                            maxGoals = Math.max(maxGoals, 1); // Evitar división por cero
                            double barHeightTeam = (teamGoals / maxGoals) * 80;
                            double barHeightOpponent = (opponentGoals / maxGoals) * 80;
                            double barWidth = 40;

                            // Dibujar barras
                            gc.setFill(Color.BLUE);
                            gc.fillRect(50, 100 - barHeightTeam, barWidth, barHeightTeam); // Barra del jugador
                            gc.setFill(Color.RED);
                            gc.fillRect(100, 100 - barHeightOpponent, barWidth, barHeightOpponent); // Barra del contrincante
                            // Etiquetas
                            gc.setFill(Color.BLACK);
                            gc.fillText(updatedTeam.getName() + ": " + teamGoals, 50, 100 - barHeightTeam - 5);
                            gc.fillText(opponentName + ": " + opponentGoals, 100, 100 - barHeightOpponent - 5);

                            matchDetails.getChildren().addAll(
                                    matchLabel,
                                    opponentLabel,
                                    teamGoalsLabel,
                                    opponentGoalsLabel,
                                    teamPointsLabel,
                                    opponentPointsLabel,
                                    drawLabel,
                                    resultLabel,
                                    goalsBarCanvas
                            );

                            roundContents.get(matchRound - 1).getChildren().add(matchDetails);
                            System.out.println("Partido añadido a la ronda " + matchRound + ": " + matchLabel.getText());
                            matchIndex++;
                        } catch (Exception e) {
                            System.out.println("Error al procesar partido: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }

                    // Crear un TitledPane para cada ronda que tenga partidos
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
    }

    @Override
    public void initialize() {
    }
}