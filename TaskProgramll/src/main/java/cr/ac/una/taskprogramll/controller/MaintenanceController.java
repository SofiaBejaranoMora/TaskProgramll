/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.taskprogramll.controller;

import cr.ac.una.taskprogramll.model.FileManager;
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

public class MaintenanceController extends Controller implements Initializable {

    private List<Sport> sportList = new ArrayList<>();
    private List<Team> teamList = new ArrayList<>();
    private List<Tourney> tourneyList = new ArrayList<>();
    private FileManager fileManeger = new FileManager();
    private Tourney selectedTourney;
    private Boolean isSport = false;
    private Boolean isTourney = false;
    private File file;
    private Mensaje message = new Mensaje();

    @FXML
    private MFXTextField txtNameSearch;
    @FXML
    private MFXComboBox<Sport> cmbSportSearch;
    @FXML
    private MFXButton btnModify;
    @FXML
    private MFXButton btnCancel;
    @FXML
    private TableView<Team> tbvTeams;
    @FXML
    private TableView<Sport> tbvSport;
    @FXML
    private TableColumn<Team, Team> tclTeam;
    @FXML
    private TableColumn<Sport, Sport> tclSport;
    @FXML
    private Label labTitle;
    @FXML
    private TableColumn<Team, String> tclTeamSportType;
    @FXML
    private TableView<Tourney> tbvTourney;
    @FXML
    private TableColumn<Tourney, String> tclTourney;
    @FXML
    private TableColumn<Tourney, String> tclTourneySportType;
    @FXML
    private MFXButton btnAccept;
    @FXML
    private MFXTextField txtName;

    @FXML
    private void OnActionBtnModify(ActionEvent event) {
        Sport selectedSport = tbvSport.getSelectionModel().getSelectedItem();
        Team selectedTeam = tbvTeams.getSelectionModel().getSelectedItem();
        selectedTourney = tbvTourney.getSelectionModel().getSelectedItem();
        if (isSport) {
            SportSelectedModify(selectedSport);
        } else if (isTourney) {
            selectedTourney = SearchTourneyModify(selectedTourney);
            if (selectedTourney != null) {
                EnabledBtnAccept(true);
                EnabledModifyTourney(true);
                txtName.setText(selectedTourney.getName());
            } else {
                message.show(Alert.AlertType.WARNING, "Alerta", "No ha seleccionado ningun equipo para modificar");
            }
        } else {
            TeamSelectedModify(selectedTeam);
        }
    }

    @FXML
    private void OnActionBtnCancel(ActionEvent event) {
        if (isTourney) {
            EnabledModifyTourney(false);
        }
        Clean();
    }

    @FXML
    private void OnActionBtnAccept(ActionEvent event) {
        selectedTourney.setName(txtName.getText());
        fileManeger.serialization(tourneyList, "Tourney");
        EnabledModifyTourney(false);
        Clean();
        InitialConditionsPanel();
    }

    @FXML
    private void OnActionCmbSportSearch(ActionEvent event) {
        if (isTourney) {
            tourneyList.clear();
            tourneyList = ListLeakedTourney();
        } else {
            teamList.clear();
            teamList = ListLeakedTeams();
        }
        TableInitialize();
    }

    @FXML
    private void OnKeyReleasedNameSearch(KeyEvent event) {
        if (!txtNameSearch.getText().isBlank()) {
            if (isSport) {
                FilterSportName();
            } else if (isTourney) {
                FilterTourneyName();
            } else {
                FilterTeamName();
            }
        } else if (isSport) {
            sportList = fileManeger.deserialization("Sport", Sport.class);
        } else {
            teamList = ListLeakedTeams();
        }
        TableInitialize();
    }

    public void TeamSelectedModify(Team selectedTeam) {
        if (selectedTeam != null) {
            file = new File(selectedTeam.searchSportType().RuteImage());
            if (file.exists()) {
                AppContext.getInstance().set("selectedTeam", selectedTeam);
                AppContext.getInstance().set("neededTeam", false);
                FlowController.getInstance().goView("RegistrationModify");
            } else {
                message.show(Alert.AlertType.WARNING, "Alerta", "El equipo no se puede modificar porque la imagen del balón del deporte "
                        + selectedTeam.getName() + " fue movida o eliminada. Primero, debe de actualizar este deporte "
                        + "con una nueva imagen del balón para poder modificar sus equipos.");
                AppContext.getInstance().set("neededTeamBoolean", true);
                AppContext.getInstance().set("neededTeam", selectedTeam);
                AppContext.getInstance().set("selectedSport", selectedTeam.searchSportType());
                AppContext.getInstance().set("isSport", true);
                AppContext.getInstance().set("isMaintenace", true);
                AppContext.getInstance().set("Title", "Mantenimiento de Deporte");
                FlowController.getInstance().goView("RegistrationModify");
            }
        } else {
            message.show(Alert.AlertType.WARNING, "Alerta", "No ha seleccionado ningun equipo para modificar");
        }
    }

    public void SportSelectedModify(Sport selectedSport) {
        if (selectedSport != null) {
            AppContext.getInstance().set("selectedSport", selectedSport);
            FlowController.getInstance().goView("RegistrationModify");
        } else {
            message.show(Alert.AlertType.WARNING, "Alerta", "No ha seleccionado ningun deporte para modificar");
        }
    }

    public void FilterTeamName() {
        teamList.clear();
        List<Team> list = ListLeakedTeams();
        String nameSearch = txtNameSearch.getText().trim().toUpperCase();
        for (Team currentTeam : list) {
            if (currentTeam.getName().toUpperCase().startsWith(nameSearch)) {
                teamList.add(currentTeam);
            }
        }
    }

    public void FilterTourneyName() {
        tourneyList.clear();
        List<Tourney> list = ListLeakedTourney();
        String nameSearch = txtNameSearch.getText().trim().toUpperCase();
        for (Tourney currentTourney : list) {
            if (currentTourney.getName().toUpperCase().startsWith(nameSearch)) {
                tourneyList.add(currentTourney);
            }
        }
    }

    public void FilterSportName() {
        sportList.clear();
        List<Sport> list = fileManeger.deserialization("Sport", Sport.class);
        String nameSearch = txtNameSearch.getText().trim().toUpperCase();
        for (Sport currentSport : list) {
            if (currentSport.getName().toUpperCase().startsWith(nameSearch)) {
                sportList.add(currentSport);
            }
        }
    }

    public List<Team> ListLeakedTeams() {
        if ((cmbSportSearch.getValue() != null) && (!"Todos".equals(cmbSportSearch.getValue().getName()))) {
            List<Team> result = new ArrayList<>();
            List<Team> list = fileManeger.deserialization("Team", Team.class);
            for (Team currentTeam : list) {
                if (currentTeam.getIdSportType() == cmbSportSearch.getValue().getId()) {
                    result.add(currentTeam);
                }
            }
            return result;
        } else {
            return fileManeger.deserialization("Team", Team.class);
        }
    }

    public List<Tourney> ListLeakedTourney() {
        if ((cmbSportSearch.getValue() != null) && (!"Todos".equals(cmbSportSearch.getValue().getName()))) {
            List<Tourney> result = new ArrayList<>();
            List<Tourney> list = fileManeger.deserialization("Tourney", Tourney.class);
            for (Tourney currentTourney : list) {
                if (currentTourney.getSportTypeId() == cmbSportSearch.getValue().getId()) {
                    result.add(currentTourney);
                }
            }
            return result;
        } else {
            return fileManeger.deserialization("Tourney", Tourney.class);
        }
    }

    public Tourney SearchTourneyModify(Tourney tourney) {
        if (tourney != null) {
            tourneyList = fileManeger.deserialization("Tourney", Tourney.class);
            for (Tourney currentTourney : tourneyList) {
                if (tourney.getId() == currentTourney.getId()) {
                    return currentTourney;
                }
            }
        }
        return null;
    }

    public void InitializeComboxSportType() {
        ObservableList<Sport> items = FXCollections.observableArrayList(sportList);
        items.add(new Sport("Todos", 0));
        cmbSportSearch.setItems(items);
    }

    public void EnabledModifyTourney(Boolean enabled) {
        tbvTourney.setDisable(enabled);
        btnModify.setVisible(!enabled);
        btnModify.setManaged(!enabled);
        btnModify.setDisable(enabled);
        EnabledcmbSport(!enabled);
        EnabledBtnAccept(enabled);
        txtNameSearch.setVisible(!enabled);
        txtNameSearch.setManaged(!enabled);
        txtNameSearch.setDisable(enabled);
        txtName.setVisible(enabled);
        txtName.setManaged(enabled);
        txtName.setDisable(!enabled);
    }

    public void EnabledTeam(Boolean enabled) {
        tbvTeams.setVisible(enabled);
        tbvTeams.setManaged(enabled);
        tbvTeams.setDisable(!enabled);
        EnabledBtnAccept(false);
    }

    public void EnabledSport(Boolean enabled) {
        tbvSport.setVisible(enabled);
        tbvSport.setManaged(enabled);
        tbvSport.setDisable(!enabled);
        EnabledBtnAccept(false);
    }

    public void EnabledTourney(Boolean enabled) {
        tbvTourney.setVisible(enabled);
        tbvTourney.setManaged(enabled);
        tbvTourney.setDisable(!enabled);
        EnabledBtnAccept(false);
    }

    public void EnabledBtnAccept(Boolean enabled) {
        btnAccept.setVisible(enabled);
        btnAccept.setManaged(enabled);
        btnAccept.setDisable(!enabled);
    }

    public void EnabledcmbSport(Boolean enabled) {
        cmbSportSearch.setVisible(enabled);
        cmbSportSearch.setManaged(enabled);
        cmbSportSearch.setDisable(!enabled);
    }

    public void TableInitialize() {
        if (isSport) {
            ObservableList<Sport> sportObservableList = FXCollections.observableArrayList(sportList);
            tbvSport.setItems(sportObservableList);
            tclSport.setCellValueFactory(new PropertyValueFactory<>("Name"));
            tclSport.prefWidthProperty().bind(tbvSport.widthProperty().multiply(1));
        } else if (isTourney) {
            ObservableList<Tourney> sportObservableList = FXCollections.observableArrayList(tourneyList);
            tbvTourney.setItems(sportObservableList);
            tclTourney.setCellValueFactory(new PropertyValueFactory<>("Name"));
            tclTourneySportType.setCellValueFactory(cellData -> {
                Sport sport = cellData.getValue().searchSportType();
                return new SimpleStringProperty(sport.toString());
            });
            tclTourney.prefWidthProperty().bind(tbvTourney.widthProperty().multiply(0.50));
            tclTourneySportType.prefWidthProperty().bind(tbvTourney.widthProperty().multiply(0.50));
        } else {
            ObservableList<Team> sportObservableList = FXCollections.observableArrayList(teamList);
            tbvTeams.setItems(sportObservableList);
            tclTeam.setCellValueFactory(new PropertyValueFactory<>("Name"));
            tclTeamSportType.setCellValueFactory(cellData -> {
                Sport sport = cellData.getValue().searchSportType();
                return new SimpleStringProperty(sport.toString());
            });
            tclTeam.prefWidthProperty().bind(tbvTeams.widthProperty().multiply(0.50));
            tclTeamSportType.prefWidthProperty().bind(tbvTeams.widthProperty().multiply(0.50));
        }
    }

    public void Clean() {
        cmbSportSearch.getSelectionModel().clearSelection();
        cmbSportSearch.getItems().clear();
        txtNameSearch.setText("");
        OnKeyReleasedNameSearch(null);
        OnActionCmbSportSearch(null);
    }

    public void InitialConditionsPanel() {
        labTitle.setText((String) AppContext.getInstance().get("Title"));
        isSport = (Boolean) AppContext.getInstance().get("isSport");
        isTourney = (Boolean) AppContext.getInstance().get("isTourney");
        EnabledSport(isSport);
        EnabledTourney(isTourney);
        EnabledTeam(!isSport && !isTourney);
        EnabledcmbSport(!isSport);
        file = new File("Sport.txt");
        if ((file.exists()) && (file.length() > 0)) {
            sportList = fileManeger.deserialization("Sport", Sport.class);
            InitializeComboxSportType();
        }
        file = new File("Team.txt");
        if ((file.exists()) && (file.length() > 0)) {
            teamList = fileManeger.deserialization("Team", Team.class);
        }
        file = new File("Tourney.txt");
        if ((file.exists()) && (file.length() > 0)) {
            tourneyList = fileManeger.deserialization("Tourney", Tourney.class);
        }
        TableInitialize();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Clean();
        InitialConditionsPanel();
    }

    @Override
    public void initialize() {
        Clean();
        InitialConditionsPanel();
    }

}
