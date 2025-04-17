package cr.ac.una.taskprogramll.controller;

import cr.ac.una.taskprogramll.model.Team;
import cr.ac.una.taskprogramll.model.Tourney;
import cr.ac.una.taskprogramll.util.AppContext;
import cr.ac.una.taskprogramll.util.FlowController;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/** * FXML Controller class * * @author ashly */
public class MatchTeamsController extends Controller implements Initializable {

    private Tourney currentTourney;
    private List<Team> currentTeamList;
    private List<String> randomChooseTeam = new ArrayList<>();
    private Boolean isFirstOpen = false;
    private int index = 0;
    private int currentRound = 1;
    private int globalSize;
    private ObservableList<Team> round1 = FXCollections.observableArrayList();
    private ObservableList<Team> round2 = FXCollections.observableArrayList();
    private ObservableList<Team> round3 = FXCollections.observableArrayList();
    private ObservableList<Team> round4 = FXCollections.observableArrayList();
    private ObservableList<Team> round5 = FXCollections.observableArrayList();
    private ObservableList<Team> round6 = FXCollections.observableArrayList();
    private ObservableList<Team> winner = FXCollections.observableArrayList();
    
    @FXML
    private TableView<Team> tblPlayersTable;
    @FXML
    private TableColumn<Team, ?> clmnRound1;
    @FXML
    private TableColumn<Team, ?> clmnRound2;
    @FXML
    private TableColumn<Team, ?> clmnRound3;
    @FXML
    private TableColumn<Team, ?> clmnRound4;
    @FXML
    private TableColumn<Team, ?> clmnRound5;
    @FXML
    private TableColumn<Team, ?> clmnRound6;
    @FXML
    private TableColumn<Team, ?> clmnFinal;
    @FXML
    private MFXButton btnBack;
    @FXML
    private MFXButton btnStart;

    @FXML
    private void onActionBtnBack(ActionEvent event) {
        currentTourney.getContinueGame().setContinueIndexTeam(index);
        currentTourney.getContinueGame().setContinueIdTeam(currentTeamList.get(index).getId());
        FlowController.getInstance().goViewInStage("ViewTourneys", (Stage) btnBack.getScene().getWindow());
    }

    @FXML
    private void onActionBtnStart(ActionEvent event) {
        encounter();
        AppContext.getInstance().set("CurrentTourney", currentTourney);
        FlowController.getInstance().goViewInStage("Game", (Stage) btnStart.getScene().getWindow());
    }
    
    private Boolean alredyChoosedTeam(String name){
        if (randomChooseTeam.contains(name)) return true;
        else {
            randomChooseTeam.add(name);
            return false;
        }
    }
    
     private int discoverRounds() {
        if (globalSize == 2) return 1;
        else if (globalSize >= 3 && globalSize <= 4) return 2;
        else if (globalSize >= 5 && globalSize <= 8) return 3;
        else if (globalSize >= 9 && globalSize <= 16) return 4;
        else if (globalSize >= 17 && globalSize <= 32) return 5;
        else return 6;
    } 
     
     private void disableFilterOrSelection() {
        tblPlayersTable.setEditable(false);
        tblPlayersTable.getSelectionModel().clearSelection();
        tblPlayersTable.getSelectionModel().setCellSelectionEnabled(false);
        for (TableColumn<Team, ?> column : tblPlayersTable.getColumns()) {
            column.setSortable(false);
        }
    }
     
     private void distributionOnTable() {
         disableFilterOrSelection();
        Collections.shuffle(currentTeamList);         
         currentTourney.getTeamList().clear();
         currentTourney.setTeamList(currentTeamList);         
         round1.setAll(currentTeamList);
         clmnRound1.setCellValueFactory(new PropertyValueFactory<>("name"));
         tblPlayersTable.setItems(round1);
     }
     
     private void organizedRound() {
         switch (discoverRounds()) {
             case 1 -> {
                 clmnRound2.setVisible(false);
                 clmnRound3.setVisible(false);
                 clmnRound4.setVisible(false);
                 clmnRound5.setVisible(false);
                 clmnRound6.setVisible(false);
            }
             case 2 -> {
                 clmnRound3.setVisible(false);
                 clmnRound4.setVisible(false);
                 clmnRound5.setVisible(false);
                 clmnRound6.setVisible(false);
            }
             case 3 -> {
                 clmnRound4.setVisible(false);
                 clmnRound5.setVisible(false);
                 clmnRound6.setVisible(false);
            }
             case 4 -> {
                 clmnRound5.setVisible(false);
                 clmnRound6.setVisible(false);
            }
             case 5 -> clmnRound6.setVisible(false);
             case 6 -> System.out.println("\n\nHola profe, saque esto con un excel, me pareció divertido!\n");
         }
     }
     
     private void encounter() {
         if (currentTeamList.get(index) != null){
             currentTourney.getContinueGame().setContinueIndexTeam(index);
             currentTourney.getContinueGame().setCurrentRound(currentRound);
         }
         else if (currentRound % 2 != 0){
             if (currentTeamList.get(index + 1) != null)
                 adjustingTable(currentTeamList.get(index + 1));
         }
         else if (currentRound % 2 == 0){
             if (currentTeamList.get(index - 1) != null)
                 adjustingTable(currentTeamList.get(index - 1));
         }
         else currentRound++;
     }
     
     private void adjustingTable(Team winnerTeam){
         if (2 == currentTeamList.size()) currentRound = 6;
         switch (currentRound) {
             case 1 -> {
                 round2.addLast(winnerTeam);
                 clmnRound2.setCellValueFactory(new PropertyValueFactory<>("name"));
                 tblPlayersTable.setItems(round2);
            }
             case 2 -> {
                 round3.addLast(winnerTeam);
                 clmnRound3.setCellValueFactory(new PropertyValueFactory<>("name"));
                 tblPlayersTable.setItems(round3);
            }
             case 3 -> {
                 round4.addLast(winnerTeam);
                 clmnRound4.setCellValueFactory(new PropertyValueFactory<>("name"));
                 tblPlayersTable.setItems(round4);
            }
             case 4 -> {
                 round5.addLast(winnerTeam);
                 clmnRound5.setCellValueFactory(new PropertyValueFactory<>("name"));
                 tblPlayersTable.setItems(round5);
            }
             case 5 -> {
                 round6.addLast(winnerTeam);
                 clmnRound6.setCellValueFactory(new PropertyValueFactory<>("name"));
                 tblPlayersTable.setItems(round6);
            }
             case 6 -> {
                 winner.addLast(winnerTeam);
                 clmnFinal.setCellValueFactory(new PropertyValueFactory<>("name"));
                 tblPlayersTable.setItems(winner);
            }
             default -> throw new AssertionError();
         }
     }
     
     private void startGameParameters() {
         globalSize = currentTeamList.size();
         currentTourney.getContinueGame().setGlobalSize(globalSize);
         organizedRound();
         distributionOnTable();
     }
     
     private void continueGameParameters() {
         organizedRound();
         round1.setAll(currentTourney.getContinueGame().getRound1());
         round2.setAll(currentTourney.getContinueGame().getRound2());
         round3.setAll(currentTourney.getContinueGame().getRound3());
         round4.setAll(currentTourney.getContinueGame().getRound4());
         round5.setAll(currentTourney.getContinueGame().getRound5());
         round6.setAll(currentTourney.getContinueGame().getRound6());
         winner.setAll(currentTourney.getContinueGame().getWinner());
         index = currentTourney.getContinueGame().getContinueIndexTeam();
         if (currentTeamList.get(index).getId() != currentTourney.getContinueGame().getContinueIdTeam()){
             
         }
     }
     
     private void viewGameTable(){
         btnStart.setManaged(false);
         btnStart.setVisible(false);
     }
     
    public void initializeFromAppContext() {
        if (!isFirstOpen) {
            isFirstOpen = true;
            this.currentTourney = (Tourney) AppContext.getInstance().get("SelectedTourney");
            this.currentTeamList = currentTourney.getTeamList();
            switch (currentTourney.returnState()) {
                case "Sin Empezar" ->
                    startGameParameters();
                case "En Proceso" ->
                    continueGameParameters();
                case "Finalizado" ->
                    viewGameTable();
                default ->
                    continueGameParameters();
            }
        } else {
        Team winerTeam = (Team) AppContext.getInstance().get("CurrentTourney");
        adjustingTable(winerTeam);
        }
    }
     
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeFromAppContext();
    }    

    @Override
    public void initialize() {}

}
