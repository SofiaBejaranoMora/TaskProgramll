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
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class MassRemovalController extends Controller implements Initializable {

    Boolean isTourney;
    private List<Sport> sportList = new ArrayList<>();
    private List<Team> teamList = new ArrayList<>();
    private List<Team> teamDeleteList = new ArrayList<>();
    private List<Team> teamOriginalList = new ArrayList<>();
    private List<Tourney> tourneyList = new ArrayList<>();
    private List<Tourney> tourneyDeleteList = new ArrayList<>();
    private List<Tourney> tourneyOriginalList = new ArrayList<>();
    private FileManager fileManeger = new FileManager();
    private Team selectedTeam;
    private Tourney selectedTourney;
    private File file;
    private Mensaje message = new Mensaje();

    @FXML
    private Label labTitle;
    @FXML
    private MFXTextField txtNameSearch;
    @FXML
    private MFXComboBox<Sport> cmbSportSearch;
    @FXML
    private TableView<Team> tbvTeam;
    @FXML
    private TableColumn<Team, Team> tclTeam;
    @FXML
    private TableColumn<Team, String> tclTeamSportType;
    @FXML
    private TableView<Team> tbvDeleteTeam;
    @FXML
    private TableColumn<Team, Team> tclDeleteTeam;
    @FXML
    private TableColumn<Team, String> tclDeleteTeamSportType;
    @FXML
    private TableView<Tourney> tbvTourney;
    @FXML
    private TableColumn<Tourney, Tourney> tclTourney;
    @FXML
    private TableColumn<Tourney, String> tclTourneySportType;
    @FXML
    private TableView<Tourney> tbvDeleteTourney;
    @FXML
    private TableColumn<Tourney, Tourney> tclDeleteTourney;
    @FXML
    private TableColumn<Tourney, String> tclDeleteTourneySportType;
    @FXML
    private MFXButton btnCancel;
    @FXML
    private MFXButton btnDelete;
    @FXML
    private Label labNameTable;
    @FXML
    private Label labNameTableDelete;

    @FXML
    private void OnKeyReleasedNameSearch(KeyEvent event) {
        if (!txtNameSearch.getText().isBlank()) {
            if (isTourney) {
                tourneyList = FilterTourneyName();
            } else {
                teamList = FilterTeamName();
            }
        } else if (isTourney) {
            tourneyList = ListLeakedTourney();
        } else {
            teamList = ListLeakedTeams();
        }
        TableInitialize();
    }

    @FXML
    private void OnActionCmbSportSearch(ActionEvent event) {
        if (isTourney) {
            tourneyList = ListLeakedTourney();
        } else {
            teamList = ListLeakedTeams();
        }
        TableInitialize();
    }

    @FXML
    private void OnActionBtnCancel(ActionEvent event) {
        clean();
        InitializeController();
    }

    @FXML
    private void OnActionBtnDelete(ActionEvent event) {
        if ((!tbvDeleteTeam.getItems().isEmpty()) || (!tbvDeleteTourney.getItems().isEmpty())) {
            if (isTourney) {
                tourneyOriginalList.removeAll(tourneyDeleteList);
                fileManeger.serialization(tourneyOriginalList, "Tourney");
                message.show(Alert.AlertType.INFORMATION, "Confirmacion", "Se elimino los torneos con exito.");
            } else {
                for (Team teamDelete : teamDeleteList) {
                    file = new File(teamDelete.RuteImage());
                    String name = teamDelete.getName();
                    if (!file.exists() || file.delete()) {
                        teamOriginalList.remove(teamDelete);
                    }
                }
                fileManeger.serialization(teamOriginalList, "Team");
                message.show(Alert.AlertType.INFORMATION, "Confirmacion", "Se elimino los equipos con exito.");
            }
            clean();
            InitializeController();
        } else {
            if (isTourney) {
                message.show(Alert.AlertType.INFORMATION, "Aviso", "No hay torneos en la tabla de eliminación. Por favor, seleccione al menos un torneo primero.");
            } else {
                message.show(Alert.AlertType.INFORMATION, "Aviso", "No hay equipos en la tabla de eliminación. Por favor, seleccione al menos un equipo primero.");
            }
        }
    }

    @FXML
    private void OnMouseClikedTbvTeam(MouseEvent event) {
        selectedTeam = tbvTeam.getSelectionModel().getSelectedItem();
        if ((selectedTeam != null) && (teamList.contains(selectedTeam))) {
            teamOriginalList.size();
            teamDeleteList.add(selectedTeam);
            teamOriginalList.size();
            teamList.remove(selectedTeam);
            teamOriginalList.size();
            TableInitialize();
        }
    }

    @FXML
    private void OnMouseClikedTbvDeleteTeam(MouseEvent event) {
        selectedTeam = tbvDeleteTeam.getSelectionModel().getSelectedItem();
        if ((selectedTeam != null) && (teamDeleteList.contains(selectedTeam))) {
            teamOriginalList.size();
            teamList.add(selectedTeam);
            teamOriginalList.size();
            teamDeleteList.remove(selectedTeam);
            teamOriginalList.size();
            OnActionCmbSportSearch(null);
            OnKeyReleasedNameSearch(null);
            TableInitialize();
        }
    }

    @FXML
    private void OnMouseClikedTbvTourney(MouseEvent event) {
        selectedTourney = tbvTourney.getSelectionModel().getSelectedItem();
        if ((selectedTourney != null) && (tourneyList.contains(selectedTourney))) {
            tourneyDeleteList.add(selectedTourney);
            tourneyList.remove(selectedTourney);
            TableInitialize();
        }
    }

    @FXML
    private void OnMouseClikedTbvDeleteTourney(MouseEvent event) {
        selectedTourney = tbvDeleteTourney.getSelectionModel().getSelectedItem();
        if ((selectedTourney != null) && (tourneyDeleteList.contains(selectedTourney))) {
            tourneyList.add(selectedTourney);
            tourneyDeleteList.remove(selectedTourney);
            OnActionCmbSportSearch(null);
            OnKeyReleasedNameSearch(null);
            TableInitialize();
        }
    }

    public List<Team> FilterTeamName() {
        List<Team> resultList = new ArrayList<>();
        List<Team> list = ListLeakedTeams();
        String nameSearch = txtNameSearch.getText().trim().toUpperCase();
        for (Team currentTeam : list) {
            if (currentTeam.getName().toUpperCase().startsWith(nameSearch)) {
                resultList.add(currentTeam);
            }
        }
        return resultList;
    }

    public List<Tourney> FilterTourneyName() {
        List<Tourney> resultList = new ArrayList<>();
        List<Tourney> list = ListLeakedTourney();
        String nameSearch = txtNameSearch.getText().trim().toUpperCase();
        for (Tourney currentTourney : list) {
            if (currentTourney.getName().toUpperCase().startsWith(nameSearch)) {
                resultList.add(currentTourney);
            }
        }
        return resultList;
    }

    public List<Team> ListLeakedTeams() {
        List<Team> result = new ArrayList<>();
        List<Team> list = teamOriginalList;
        if ((cmbSportSearch.getValue() != null) && (!"Todos".equals(cmbSportSearch.getValue().getName()))) {
            for (Team currentTeam : list) {
                if ((currentTeam.getIdSportType() == cmbSportSearch.getValue().getId()) && !(teamDeleteList.contains(currentTeam))) {
                    result.add(currentTeam);
                }
            }
            return result;
        } else {
            for (Team currentTeam : list) {
                if (!teamDeleteList.contains(currentTeam)) {
                    result.add(currentTeam);
                }
            }
            return result;
        }
    }

    public List<Tourney> ListLeakedTourney() {
        List<Tourney> result = new ArrayList<>();
        List<Tourney> list = tourneyOriginalList;
        if ((cmbSportSearch.getValue() != null) && (!"Todos".equals(cmbSportSearch.getValue().getName()))) {
            for (Tourney currentTourney : list) {
                if ((currentTourney.getSportTypeId() == cmbSportSearch.getValue().getId())
                        && !(tourneyDeleteList.contains(currentTourney))) {
                    result.add(currentTourney);
                }
            }
            return result;
        } else {
            for (Tourney currentTourney : list) {
                if (!tourneyDeleteList.contains(currentTourney)) {
                    result.add(currentTourney);
                }
            }
            return result;
        }
    }

    public void EnabledTourney(Boolean enabled) {
        tbvTourney.setVisible(enabled);
        tbvTourney.setManaged(enabled);
        tbvTourney.setDisable(!enabled);
        tbvDeleteTourney.setVisible(enabled);
        tbvDeleteTourney.setManaged(enabled);
        tbvDeleteTourney.setDisable(!enabled);
        tbvTeam.setVisible(!enabled);
        tbvTeam.setManaged(!enabled);
        tbvTeam.setDisable(enabled);
        tbvDeleteTeam.setVisible(!enabled);
        tbvDeleteTeam.setManaged(!enabled);
        tbvDeleteTeam.setDisable(enabled);
        if (enabled) {
            labNameTable.setText("Tabla de Torneos");
            labNameTableDelete.setText("Tabla de Torneos a Eliminar");
        } else {
            labNameTable.setText("Tabla de Equipos");
            labNameTableDelete.setText("Tabla de Equipos a Eliminar");
        }
    }

    public void InitializeComboxSportType() {
        ObservableList<Sport> items = FXCollections.observableArrayList(sportList);
        items.add(new Sport("Todos", 0));
        cmbSportSearch.setItems(items);
    }

    public void TableInitialize() {
        if (isTourney) {
            ObservableList<Tourney> tourneysObservableList = FXCollections.observableArrayList(tourneyList);
            tbvTourney.setItems(tourneysObservableList);
            tclTourney.setCellValueFactory(new PropertyValueFactory<>("Name"));
            tclTourneySportType.setCellValueFactory(cellData -> {
                Sport sport = cellData.getValue().searchSportType();
                return new SimpleStringProperty(sport.toString());
            });
            tourneysObservableList = FXCollections.observableArrayList(tourneyDeleteList);
            tbvDeleteTourney.setItems(tourneysObservableList);
            tclDeleteTourney.setCellValueFactory(new PropertyValueFactory<>("Name"));
            tclTourneySportType.setCellValueFactory(cellData -> {
                Sport sport = cellData.getValue().searchSportType();
                return new SimpleStringProperty(sport.toString());
            });
            tclTourney.prefWidthProperty().bind(tbvTourney.widthProperty().multiply(0.50));
            tclTourneySportType.prefWidthProperty().bind(tbvTourney.widthProperty().multiply(0.50));
            tclDeleteTourney.prefWidthProperty().bind(tbvDeleteTourney.widthProperty().multiply(0.50));
            tclDeleteTourneySportType.prefWidthProperty().bind(tbvDeleteTourney.widthProperty().multiply(0.50));
        } else {
            ObservableList<Team> teamObservableList = FXCollections.observableArrayList(teamList);
            tbvTeam.setItems(teamObservableList);
            tclTeam.setCellValueFactory(new PropertyValueFactory<>("Name"));
            tclTeamSportType.setCellValueFactory(cellData -> {
                Sport sport = cellData.getValue().searchSportType();
                return new SimpleStringProperty(sport.toString());
            });
            teamObservableList = FXCollections.observableArrayList(teamDeleteList);
            tbvDeleteTeam.setItems(teamObservableList);
            tclDeleteTeam.setCellValueFactory(new PropertyValueFactory<>("Name"));
            tclDeleteTeamSportType.setCellValueFactory(cellData -> {
                Sport sport = cellData.getValue().searchSportType();
                return new SimpleStringProperty(sport.toString());
            });
            tclTeam.prefWidthProperty().bind(tbvTeam.widthProperty().multiply(0.50));
            tclTeamSportType.prefWidthProperty().bind(tbvTeam.widthProperty().multiply(0.50));
            tclDeleteTeam.prefWidthProperty().bind(tbvDeleteTeam.widthProperty().multiply(0.50));
            tclDeleteTeamSportType.prefWidthProperty().bind(tbvDeleteTeam.widthProperty().multiply(0.50));
        }
    }

    public void InitializeController() {
        isTourney = (Boolean) AppContext.getInstance().get("isTourney");
        labTitle.setText((String) AppContext.getInstance().get("Title"));
        EnabledTourney(isTourney);
        file = new File("Sport.txt");
        if ((file.exists()) && (file.length() > 0)) {
            sportList = fileManeger.deserialization("Sport", Sport.class);
            InitializeComboxSportType();
        }
        file = new File("Team.txt");
        if ((file.exists()) && (file.length() > 0)) {
            teamOriginalList = fileManeger.deserialization("Team", Team.class);
            teamList.addAll(teamOriginalList);
        }
        file = new File("Tourney.txt");
        if ((file.exists()) && (file.length() > 0)) {
            tourneyOriginalList = fileManeger.deserialization("Tourney", Tourney.class);
            tourneyList.addAll(tourneyOriginalList);
        }
        TableInitialize();
    }

    public void clean() {
        tbvDeleteTeam.getItems().clear();
        tbvDeleteTeam.getSelectionModel().clearSelection();
        tbvDeleteTourney.getItems().clear();
        tbvDeleteTourney.getSelectionModel().clearSelection();
        cmbSportSearch.getSelectionModel().clearSelection();
        cmbSportSearch.setValue(null);
        txtNameSearch.setText("");
        teamDeleteList.clear();
        teamList.clear();
        teamOriginalList.clear();
        tourneyDeleteList.clear();
        tourneyList.clear();
        tourneyOriginalList.clear();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @Override
    public void initialize() {
        clean();
        InitializeController();
    }

}
