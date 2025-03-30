/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.taskprogramll.controller;

import cr.ac.una.taskprogramll.model.FileManager;
import cr.ac.una.taskprogramll.model.Sport;
import cr.ac.una.taskprogramll.model.Team;
import cr.ac.una.taskprogramll.util.AppContext;
import cr.ac.una.taskprogramll.util.FlowController;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class MaintenanceController extends Controller implements Initializable {

    private List<Sport> sportList = new ArrayList<>();
    private List<Team> teamList = new ArrayList<>();
    private FileManager fileManeger = new FileManager();
    private Boolean isSport = false;
    private File file;

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
    private ImageView lobbyIcon;
    @FXML
    private TableColumn<Team, String> tclTeamSportType;

    @FXML
    private void OnMouseClickedLobbyIcon(MouseEvent event) {
        FlowController.getInstance().goViewInStage("Lobby", (Stage) lobbyIcon.getScene().getWindow());
    }

    @FXML
    private void OnActionBtnModify(ActionEvent event) {
    }

    @FXML
    private void OnActionBtnCancel(ActionEvent event) {

    }

    @FXML
    private void OnActionCmbSportSearch(ActionEvent event) {
        teamList.clear();
        teamList=SearchTeam();
        TableInitialize();
    }

    @FXML
    private void OnKeyReleasedNameSearch(KeyEvent event) {
        if (!txtNameSearch.getText().isBlank()) {
            teamList.clear();
            List<Team> list = SearchTeam();
            String nameSearch = txtNameSearch.getText().trim().toUpperCase();
            for (Team currentTeam : list) {
                if (currentTeam.getName().toUpperCase().startsWith(nameSearch)) {
                    teamList.add(currentTeam);
                }
            }
        } else {
            teamList = SearchTeam();
        }
        TableInitialize();
    }

    public List<Team> SearchTeam() {
        if (cmbSportSearch.getValue() != null) {
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

    public void StartComboxSportType() {
        ObservableList<Sport> items = FXCollections.observableArrayList(sportList);
        cmbSportSearch.setItems(items);
    }

    public void EnabledTeam(Boolean enabled) {
        cmbSportSearch.setDisable(!enabled);
        cmbSportSearch.setVisible(enabled);
        cmbSportSearch.setManaged(enabled);
        tbvTeams.setVisible(enabled);
        tbvTeams.setManaged(enabled);
        tbvTeams.setDisable(!enabled);
        tbvSport.setVisible(!enabled);
        tbvSport.setManaged(!enabled);
        tbvSport.setDisable(enabled);
    }

    public void TableInitialize() {
        if (isSport) {
            ObservableList<Sport> sportObservableList = FXCollections.observableArrayList(sportList);
            tbvSport.setItems(sportObservableList);
            tclSport.setCellValueFactory(new PropertyValueFactory<>("Name"));
            tclSport.prefWidthProperty().bind(tbvSport.widthProperty().multiply(1));
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

    public void InitialConditionsPanel() {
        labTitle.setText((String) AppContext.getInstance().get("Title"));
        isSport = (Boolean) AppContext.getInstance().get("isSport");
        EnabledTeam(!isSport);
        file = new File("Sport.txt");
        if ((file.exists()) && (file.length() > 0)) {
            sportList = fileManeger.deserialization("Sport", Sport.class);
            StartComboxSportType();
        }
        file = new File("Team.txt");
        if ((file.exists()) && (file.length() > 0)) {
            teamList = fileManeger.deserialization("Team", Team.class);
        }
        TableInitialize();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        InitialConditionsPanel();
    }

    @Override
    public void initialize() {
        InitialConditionsPanel();
    }
}

