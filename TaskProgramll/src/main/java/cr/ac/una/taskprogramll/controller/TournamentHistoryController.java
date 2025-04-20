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
import javafx.scene.paint.Color;
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
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

public class TournamentHistoryController extends Controller implements Initializable {

    @FXML
    private AnchorPane rootPane;
    @FXML
    private VBox mainContainer;
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
    private HBox content; @FXML
    private HBox teamsPane;
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
    private Canvas statsCanvas;
    @FXML
    private Label generalStatsLabel;
    @FXML
    private TableView<Tourney> tournamentTable;
    @FXML
    private TableColumn<Tourney, String> nameColumn;
    @FXML
    private TableColumn<Tourney, Integer> minutesColumn;
    @FXML
    private TableColumn<Tourney, String> sportColumn;
    @FXML
    private TableColumn<Tourney, String> stateColumn;
    @FXML
    private TableColumn<Tourney, String> winnerColumn;

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
        setupTeamFilterDisplay();
        tourneySelectionListener();
    }

    private void setupTeamFilterDisplay() {
        teamFilter.setConverter(new StringConverter<Team>() {
            @Override
            public String toString(Team team) {
                if(team!=null){
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

   private void SetupSportColumns() {
    sportColumn.setCellValueFactory(cellData -> {
        Tourney tourney = cellData.getValue();
        if (tourney != null) {
            Sport sport = tourney.searchSportType();
            System.out.println("Torneo: " + tourney.getName() + ", SportTypeId: " + tourney.getSportTypeId() + ", Deporte: " + (sport != null ? sport.getName() : "null"));
            if (sport != null) {
                return new ReadOnlyObjectWrapper<>(sport.getName());
            }
        }
        return new ReadOnlyObjectWrapper<>("Sin deporte");
    });
}

    private void SetupStateColumns() {
        stateColumn.setCellValueFactory(cellData -> {
            Tourney tourney = cellData.getValue();
            return tourney != null ? new ReadOnlyObjectWrapper<>(tourney.returnState()) : new ReadOnlyObjectWrapper<>(null);
        });
    }

    private void SetupWinnerColumn() {
        winnerColumn.setCellValueFactory(cellData -> {
            Tourney tourney = cellData.getValue();
            if (tourney != null && "Finalizado".equals(tourney.returnState())) {
                List<Team> loosers = tourney.getLoosersList();
                return new ReadOnlyObjectWrapper<>(loosers != null && !loosers.isEmpty() ? loosers.getFirst().getName() : "Sin ganador");
            }
            return new ReadOnlyObjectWrapper<>("Pendiente");
        });
    }

    private void SetupTableColumns() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        minutesColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
        SetupWinnerColumn();
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
        });
    }

    private void applyFilters() {
        String query = teamSearch.getText() != null ? teamSearch.getText().trim().toLowerCase() : "";
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
    
    private void drawTeamStats(Team team) {
        GraphicsContext gc = statsCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, statsCanvas.getWidth(), statsCanvas.getHeight());
        if (team == null) {
            return;
        }

        double width = statsCanvas.getWidth();
        double height = statsCanvas.getHeight();
        int points = team.getPoints();
        int goals = team.getGoals();
        System.out.println("Dibujando gráfico - Equipo: " + team.getName() + ", Puntos: " + points + ", Goles: " + goals);

        if (points == 0 && goals == 0) {
            gc.setFill(Color.BLACK);
            gc.fillText("Sin datos para mostrar", width / 4, height / 2);
            return;
        }

        double maxValue = Math.max(Math.max(points, goals), 1);
        double barWidth = width / 4;

        // Barra de puntos
        double pointsHeight = (points / maxValue) * (height * 0.8);
        gc.setFill(Color.BLUE);
        gc.fillRect(barWidth * 0.5, height - pointsHeight, barWidth, pointsHeight);
        gc.setFill(Color.BLACK); 
        gc.fillText("Puntos", barWidth * 0.5, height - 5);

        // Barra de goles
        double goalsHeight = (goals / maxValue) * (height * 0.8);
        gc.setFill(Color.GREEN);
        gc.fillRect(barWidth * 2.5, height - goalsHeight, barWidth, goalsHeight);
        gc.setFill(Color.BLACK);
        gc.fillText("Goles", barWidth * 2.5, height - 5);
    }
    
    private void showTourneyInfo(Tourney tourney, Team team) {
        if (tourney == null || team == null) {
            detailsTitle.setText("Seleccioná un torneo y un equipo");
            positionLabel.setText("");
            matchesLabel.setText("");
            statsLabel.setText("");
            generalStatsLabel.setText("");
            statsCanvas.getGraphicsContext2D().clearRect(0, 0, statsCanvas.getWidth(), statsCanvas.getHeight());
            return;
        }
        detailsTitle.setText(tourney.getName());
        positionLabel.setText(tourney.returnTeamPosition(team));
        
        if (tourney.getLoosersList().contains(team)) {
            matchesLabel.setText("Resultado: Eliminado");
        } else if (tourney.getTeamList().contains(team)) {
            matchesLabel.setText("Resultado: En competencia");
        } else {
            matchesLabel.setText("Resultado: No participa");
        }
        statsLabel.setText("Puntos: " + team.getPoints() + ", Goles: " + team.getGoals());
        generalStatsLabel.setText("Estado del torneo: " + tourney.returnState());
        drawTeamStats(team);
         //Detalle de los partiidos: x equipo se enfrento con este y gano - contra quiennes se enfrento + resultados: 
          //estadisticas generales y por partido
           //obtener goles y empates y cuantas veces gano o perdio (buscar toda la informacion parra lo general)
            //Ranking global:

    }
    
    

    private void updateTeamsPane(Tourney tourney) {
        teamsPane.getChildren().clear();

        if (tourney != null && tourney.getTeamList() != null) {
            for (Team team : tourney.getTeamList()) {
                Button teamCard = new Button(team.getName());
                teamCard.setUserData(team);
                teamCard.setOnAction(event -> showTourneyInfo(tourney, (Team) teamCard.getUserData()));
                teamsPane.getChildren().add(teamCard);
            }
        }
    }
    private void tourneySelectionListener() {
        tournamentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldTourney, newTourney) -> {
            updateTeamsPane(newTourney);
        });
    }
    @Override
    public void initialize() {
    }

 
}