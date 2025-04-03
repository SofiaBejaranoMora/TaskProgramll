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
    private ImageView lobbyIcon;
    @FXML
    private TableColumn<Team, String> tclTeamSportType;

    @FXML
    private void OnMouseClickedLobbyIcon(MouseEvent event) {
        FlowController.getInstance().goViewInStage("Lobby", (Stage) lobbyIcon.getScene().getWindow());
    }

    @FXML
    private void OnActionBtnModify(ActionEvent event) {
        Sport selectedSport=tbvSport.getSelectionModel().getSelectedItem();
        Team selectedTeam=tbvTeams.getSelectionModel().getSelectedItem();
            if(isSport){
                SelectSport(selectedSport);
            }
            else{
                SelectTeam(selectedTeam);
            }
    }

    @FXML
    private void OnActionBtnCancel(ActionEvent event) {

    }

    @FXML
    private void OnActionCmbSportSearch(ActionEvent event) {
        teamList.clear();
        teamList = filterTeam();
        TableInitialize();
    }

    @FXML
    private void OnKeyReleasedNameSearch(KeyEvent event) {
        if (!txtNameSearch.getText().isBlank()) {
            if (isSport) {
                SearchSport();
            }
            else{
                SearchTeam();
            }
        } else if (isSport) {
           sportList=fileManeger.deserialization("Sport", Sport.class);
        }
        else{
             teamList = filterTeam();
        }
        TableInitialize();
    }
    
    public void SelectTeam(Team selectedTeam){
         if(selectedTeam!=null){
             file=new File(selectedTeam.searchSportType().RuteImage());
             if(file.exists()){
                 FlowController.getInstance().goViewInStage("RegisterModify", (Stage) lobbyIcon.getScene().getWindow());
                 AppContext.getInstance().set("selectedTeam", selectedTeam);
             }
             else{
                 message.show(Alert.AlertType.CONFIRMATION, "Alerta", "El equipo no se puede modificar porque la imagen del balón del deporte " 
                         + selectedTeam.getName() + " fue movida o eliminada. Primero, actualice este deporte "
                                 + "con una nueva imagen del balón para poder modificar sus equipos.");
             }
        }
        else {
            message.show(Alert.AlertType.WARNING, "Alerta", "No ha seleccionado ningun equipo para modificar");
        }
    }
    
    public void SelectSport(Sport selectedSport){
        if(selectedSport!=null){
            FlowController.getInstance().goViewInStage("RegisterModify", (Stage) lobbyIcon.getScene().getWindow());
            AppContext.getInstance().set("selectedSport", selectedSport);
        }
        else {
            message.show(Alert.AlertType.WARNING, "Alerta", "No ha seleccionado ningun deporte para modificar");
        }
    }
    
    public void SearchTeam() {
        teamList.clear();
        List<Team> list = filterTeam();
        String nameSearch = txtNameSearch.getText().trim().toUpperCase();
        for (Team currentTeam : list) {
            if (currentTeam.getName().toUpperCase().startsWith(nameSearch)) {
                teamList.add(currentTeam);
            }
        }
    }

    public void SearchSport() {
        sportList.clear();
        List<Sport> list = fileManeger.deserialization("Sport", Sport.class);
        String nameSearch = txtNameSearch.getText().trim().toUpperCase();
        for (Sport currentSport : list) {
            if (currentSport.getName().toUpperCase().startsWith(nameSearch)) {
                sportList.add(currentSport);
            }
        }
    }

    public List<Team> filterTeam() {
        if ((cmbSportSearch.getValue() != null)&&(!"Todos".equals(cmbSportSearch.getValue().getName()))) {
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
        items.add(new Sport("Todos",0));
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
