/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
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
                sportName

 = sport.getName();
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
            return new SimpleStringProperty(tourney.returnTeamPosition(selectedTeam));
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
            return new ReadOnlyObjectWrapper<>(selectedTeam.getPoints());
        });
        tourneyStatsGoalsColumn.setCellValueFactory(cellData -> {
            Tourney tourney = (Tourney) cellData.getValue();
            Integer goals = 0;
            if (selectedTeam != null) {
                goals = selectedTeam.getGoals();
            }
            return new ReadOnlyObjectWrapper<>(goals);
        });
        tourneyStatsRankingColumn.setCellValueFactory(cellData -> {
            Tourney tourney = (Tourney) cellData.getValue();
            if (selectedTeam == null) {
                return new SimpleStringProperty("Selecciona un equipo");
            }
            return new SimpleStringProperty(tourney.returnTeamPosition(selectedTeam));
        });
    }

    private void loadData() {
        // Inicializar lista de equipos si es nula
        if (AppContext.getInstance().get("teams") == null) {
            AppContext.getInstance().set("teams", new ArrayList<Team>());
        }
        @SuppressWarnings("unchecked")
        List<Team> teams = (List<Team>) AppContext.getInstance().get("teams");
        System.out.println("Equipos iniciales en AppContext: " + teams);

        // Verificar y cargar equipos desde el archivo
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
                    // Deserializar directamente con fileManager
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

        // Actualizar availableTeams y teamTable
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
        // Inicializar lista de torneos si es nula
        if (AppContext.getInstance().get("tourneys") == null) {
            AppContext.getInstance().set("tourneys", new ArrayList<Tourney>());
        }
        @SuppressWarnings("unchecked")
        List<Tourney> tourneys = (List<Tourney>) AppContext.getInstance().get("tourneys");
        System.out.println("Torneos iniciales en AppContext: " + tourneys);

        // Verificar y cargar torneos desde el archivo
        if (tourneys.isEmpty()) {
            File tourneyFile = new File("Tourney.txt");
            System.out.println("Ruta absoluta del archivo Tourney.txt: " + tourneyFile.getAbsolutePath());
            if (!tourneyFile.exists()) {
                System.out.println("Archivo Tourney.txt no encontrado en: " + tourneyFile.getAbsolutePath());
                message.show(Alert.AlertType.WARNING, "Archivo no encontrado", "Tourney.txt no existe en el directorio raíz.");
            } else if (tourneyFile.length() == 0) {
                System.out.println("Archivo Tourney.txt está vacío.");
                message.show(Alert.AlertType.WARNING, "Archivo vacío", "Tourney.txt está vacío.");
            } else {
                try {
                    // Deserializar directamente con fileManager
                    System.out.println("Deserializando Tourney.txt...");
                    List<Tourney> deserializedTourneys = fileManager.deserialization("Tourney", Tourney.class);
                    if (deserializedTourneys == null || deserializedTourneys.isEmpty()) {
                        System.out.println("Deserialización de Tourney.txt devolvió null o lista vacía.");
                        message.show(Alert.AlertType.WARNING, "Sin datos", "No se encontraron torneos en Tourney.txt.");
                    } else {
                        tourneys.addAll(deserializedTourneys);
                        AppContext.getInstance().set("tourneys", tourneys);
                        System.out.println("Torneos cargados y añadidos a AppContext: " + tourneys);
                    }
                } catch (Exception e) {
                    System.out.println("Error al deserializar Tourney.txt: " + e.getMessage());
                    e.printStackTrace();
                    message.show(Alert.AlertType.ERROR, "Error al Cargar Torneos", "No se pudieron cargar los torneos: " + e.getMessage());
                }
            }
        } else {
            System.out.println("Torneos ya cargados en AppContext: " + tourneys);
        }

        // Actualizar availableTourneys y tourneyTable
        availableTourneys.clear();
        availableTourneys.addAll(tourneys);
        tourneyTable.setItems(availableTourneys);
        System.out.println("tourneyTable items después de cargar: " + tourneyTable.getItems());
        if (tourneyTable.getItems().isEmpty()) {
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
                // Filtrar torneos en los que participó el equipo seleccionado
                ObservableList<Tourney> teamTourneys = FXCollections.observableArrayList();
                for (Tourney tourney : availableTourneys) {
                    if (tourney.getTeamList().contains(selectedTeam) || tourney.getLoosersList().contains(selectedTeam)) {
                        teamTourneys.add(tourney);
                    }
                }
                tourneyTable.setItems(teamTourneys);
                tourneyStatsTable.setItems(teamTourneys);
                updateStatsLabels();
                drawTeamStats();
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
        // Llenar teamFilter con los equipos disponibles
        teamFilter.setItems(availableTeams);

        // Listener para teamSearch
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

        // Listener para teamFilter
        teamFilter.getSelectionModel().selectedItemProperty().addListener((obs, oldTeam, newTeam) -> {
            applyFilters();
        });

        // Listener para sportFilter
        sportFilter.getSelectionModel().selectedItemProperty().addListener((obs, oldSport, newSport) -> {
            applyFilters();
        });

        // Acción del botón searchButton
        searchButton.setOnAction(event -> applyFilters());

        // Acción del botón clearButton
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
        boolean matchesSportFilter = true; // Por defecto, pasa si no hay deporte seleccionado
        if (selectedSport != null) {
            matchesSportFilter = false;
            // Verificar si el equipo participa en algún torneo del deporte seleccionado
            for (Tourney tourney : availableTourneys) {
                if ((tourney.getTeamList().contains(team) || tourney.getLoosersList().contains(team)) &&
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

    // No aplicamos filtros a tourneyTable aquí, se manejará en setupTeamSelectionListener
}

    private void updateStatsLabels() {
        if (selectedTeam == null) {
            totalPointsLabel.setText("Puntos: 0");
            totalMatchesLabel.setText("Partidos: 0");
            generalRankingLabel.setText("Ranking: -");
            return;
        }

        // Calcular estadísticas totales
        int totalPoints = selectedTeam.getPoints();
        int totalMatches = 0; // Necesitamos datos de partidos para esto
        List<Team> sortedTeams = new ArrayList<>(availableTeams);
        sortedTeams.sort((t1, t2) -> Integer.compare(t2.getPoints(), t1.getPoints()));
        int rank = sortedTeams.indexOf(selectedTeam) + 1;

        totalPointsLabel.setText("Puntos: " + totalPoints);
        totalMatchesLabel.setText("Partidos: " + totalMatches); // Ajustar cuando tengamos datos de partidos
        generalRankingLabel.setText("Ranking: " + rank);
    }

    private void drawTeamStats() {
        // Limpiar los canvas
        clearCanvases();

        if (selectedTeam == null) {
            return;
        }

        // Preparar datos para los gráficos
        List<Integer> goalsPerMatch = new ArrayList<>();
        List<Integer> pointsPerMatch = new ArrayList<>();
        List<Integer> pointsPerTourney = new ArrayList<>();

        // Obtener los torneos en los que participa el equipo
        List<Tourney> teamTourneys = new ArrayList<>();
        for (Tourney tourney : availableTourneys) {
            if (tourney.getTeamList().contains(selectedTeam) || tourney.getLoosersList().contains(selectedTeam)) {
                teamTourneys.add(tourney);
                pointsPerTourney.add(0); // Inicializar los puntos del torneo
            }
        }

        // Obtener los partidos del equipo seleccionado
        List<MatchDetails> matches = selectedTeam.getEncounterList();
        if (matches != null && !matches.isEmpty()) {
            int matchIndex = 0;
            for (MatchDetails match : matches) {
                // Goles del equipo seleccionado en este partido
                int teamGoals = match.getNameFirstTeam().equals(selectedTeam.getName()) ? match.getCounterFirstTeamGoals() : match.getCounterSecondTeamGoals();
                int opponentGoals = match.getNameFirstTeam().equals(selectedTeam.getName()) ? match.getCounterSecondTeamGoals() : match.getCounterFirstTeamGoals();
                goalsPerMatch.add(teamGoals);

                // Calcular puntos del partido (3 por victoria, 1 por empate, 0 por derrota)
                int points = 0;
                if (teamGoals > opponentGoals) {
                    points = 3; // Victoria
                } else if (teamGoals == opponentGoals) {
                    points = 1; // Empate
                }
                pointsPerMatch.add(points);

                // Distribuir los puntos entre los torneos (estimación)
                if (!teamTourneys.isEmpty()) {
                    int tourneyIndex = Math.min(matchIndex / (matches.size() / teamTourneys.size() + 1), teamTourneys.size() - 1);
                    pointsPerTourney.set(tourneyIndex, pointsPerTourney.get(tourneyIndex) + points);
                }
                matchIndex++;
            }
        }

        // Dibujar en matchGoalsCanvas (goles por partido)
        GraphicsContext gcGoals = matchGoalsCanvas.getGraphicsContext2D();
        double width = matchGoalsCanvas.getWidth();
        double height = matchGoalsCanvas.getHeight();
        if (goalsPerMatch.isEmpty()) {
            gcGoals.setFill(Color.BLACK);
            gcGoals.fillText("No hay datos de goles", width / 4, height / 2);
        } else {
            double barWidth = width / goalsPerMatch.size() * 0.8;
            double maxGoals = Collections.max(goalsPerMatch);
            maxGoals = Math.max(maxGoals, 1); // Evitar división por cero
            for (int i = 0; i < goalsPerMatch.size(); i++) {
                double barHeight = (goalsPerMatch.get(i) / maxGoals) * (height * 0.8);
                gcGoals.setFill(Color.RED);
                gcGoals.fillRect(i * barWidth * 1.25, height - barHeight, barWidth, barHeight);
                gcGoals.setFill(Color.BLACK);
                gcGoals.fillText(String.valueOf(goalsPerMatch.get(i)), i * barWidth * 1.25 + barWidth / 4, height - barHeight - 5);
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
        return;
    }

    // Buscar torneos en los que participa el equipo seleccionado
    for (Tourney tourney : availableTourneys) {
        if (tourney.getTeamList().contains(selectedTeam) || tourney.getLoosersList().contains(selectedTeam)) {
            TitledPane pane = new TitledPane();
            pane.setText(tourney.getName());

            VBox content = new VBox();
            List<MatchDetails> matches = selectedTeam.getEncounterList();
            Game game = tourney.getContinueGame();
            int lastRoundParticipated = 0;

            // Determinar en qué rondas participó el equipo
            if (game.getRound1().contains(selectedTeam)) lastRoundParticipated = 1;
            if (game.getRound2().contains(selectedTeam)) lastRoundParticipated = 2;
            if (game.getRound3().contains(selectedTeam)) lastRoundParticipated = 3;
            if (game.getRound4().contains(selectedTeam)) lastRoundParticipated = 4;
            if (game.getRound5().contains(selectedTeam)) lastRoundParticipated = 5;
            if (game.getRound6().contains(selectedTeam)) lastRoundParticipated = 6;
            if (game.getWinner().contains(selectedTeam)) lastRoundParticipated = 7;

            // Si el equipo está en loosersList y no en las rondas posteriores, perdió
            boolean isLooser = tourney.getLoosersList().contains(selectedTeam);

            // Mostrar los partidos del equipo
            if (matches != null && !matches.isEmpty()) {
                int matchIndex = 0;
                for (MatchDetails match : matches) {
                    String opponentName = match.getNameFirstTeam().equals(selectedTeam.getName()) ? match.getNameSecondTeam() : match.getNameFirstTeam();
                    int teamGoals = match.getNameFirstTeam().equals(selectedTeam.getName()) ? match.getCounterFirstTeamGoals() : match.getCounterSecondTeamGoals();
                    int opponentGoals = match.getNameFirstTeam().equals(selectedTeam.getName()) ? match.getCounterSecondTeamGoals() : match.getCounterFirstTeamGoals();

                    // Intentar inferir la ronda del partido
                    int matchRound = Math.min(matchIndex + 1, lastRoundParticipated);
                    String roundInfo = "Ronda " + matchRound;
                    if (isLooser && matchRound == lastRoundParticipated) {
                        roundInfo += " (Perdió)";
                    } else if (lastRoundParticipated == 7 && matchRound == lastRoundParticipated - 1) {
                        roundInfo = "Final (Ganador)";
                    }

                    Label matchLabel = new Label(roundInfo + ": vs " + opponentName + " - " + teamGoals + " - " + opponentGoals);
                    content.getChildren().add(matchLabel);
                    matchIndex++;
                }
            }
            if (content.getChildren().isEmpty()) {
                content.getChildren().add(new Label("No hay partidos disponibles para este torneo"));
            }
            pane.setContent(content);
            matchAccordion.getPanes().add(pane);
        }
    }
}

    @Override
    public void initialize() {
    }
}