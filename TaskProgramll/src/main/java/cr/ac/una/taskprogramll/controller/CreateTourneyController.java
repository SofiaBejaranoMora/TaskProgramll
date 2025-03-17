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
    private TableView<Team> tblTeams1; // Equipos elegidos (restaurado el nombre original)
    @FXML
    private TableColumn<Team, String> colTeamName; // Columna de equipos disponibles
    @FXML
    private TableColumn<Team, String> colTeamName1; // Columna de equipos elegidos (restaurado el nombre original)
    @FXML
    private MFXSlider sliderTeamCount;

    private final FileManager fileManager = new FileManager();
    private final ObservableList<Team> availableTeams = FXCollections.observableArrayList();
    private final ObservableList<Team> selectedTeams = FXCollections.observableArrayList();
    private final List<Sport> sportList = new ArrayList<>();
    private final List<Tourney> tourneyList = new ArrayList<>();
    private File sportFile;

  @Override
public void initialize(URL url, ResourceBundle rb) {
    try {
        configureTableColumns();
        assignTableItems();
        setupSlider();
        setupTeamSelection();
        initializeSportList();
    } catch (Exception e) {
        System.err.println("Error during initialization: " + e.getMessage());
        e.printStackTrace();
    }
}

private void configureTableColumns() {
    colTeamName.setCellValueFactory(new PropertyValueFactory<>("name"));
    colTeamName1.setCellValueFactory(new PropertyValueFactory<>("name"));
}

private void assignTableItems() {
    tblTeams.setItems(availableTeams);
    tblTeams1.setItems(selectedTeams);
}

private void setupSlider() {
    sliderTeamCount.setMin(32);
    sliderTeamCount.setMax(64);
    sliderTeamCount.setValue(32);
    sliderTeamCount.valueProperty().addListener((observable, oldValue, newValue) ->
        System.out.println("Slider value changed to: " + newValue.intValue())
    );
}

// Este ya lo teníamos separado, lo mantenemos
private void setupTeamSelection() {
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

// Este ya lo teníamos, lo mantenemos igual
private void initializeSportList() {
    sportFile = new File("Sport.txt");
    if (sportFile.exists() && sportFile.length() > 0) {
        try {
            sportList.addAll(fileManager.deserialization("Sport", Sport.class));
            tglLstSportType.setItems(FXCollections.observableArrayList(sportList));
            System.out.println("Sport list loaded successfully: " + sportList.size() + " sports.");
        } catch (Exception e) {
            System.err.println("Error loading sport list: " + e.getMessage());
            e.printStackTrace();
        }
    } else {
        System.err.println("Sport.txt file does not exist or is empty.");
    }
}
    @FXML
    private void adjustSelectedTeamsSlice(ActionEvent event) {
        try {
            int desiredCount = (int) sliderTeamCount.getValue();
            desiredCount = Math.max(32, Math.min(desiredCount, 64));

            while (selectedTeams.size() > desiredCount) {
                Team removedTeam = selectedTeams.remove(selectedTeams.size() - 1);
                availableTeams.add(removedTeam);
            }

            for (Team team : new ArrayList<>(availableTeams)) {
                if (selectedTeams.size() >= desiredCount) break;
                if (!selectedTeams.contains(team)) {
                    selectedTeams.add(team);
                    availableTeams.remove(team);
                }
            }

            System.out.println("Teams adjusted to: " + selectedTeams.size());
        } catch (Exception e) {
            System.err.println("Error adjusting teams: " + e.getMessage());
            e.printStackTrace();
        }
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
            System.err.println("Error creating tourney: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private int generateTourneyId() {
        if (tourneyList.isEmpty()) return 1;
        return tourneyList.size() + 1;
    }

    private boolean isValidNumericInput(String input) {
        if (input.trim().isEmpty()) {
            System.err.println("Input cannot be empty.");
            return false;
        }
        try {
            int number = Integer.parseInt(input);
            return number > 0;
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
}

