package cr.ac.una.taskprogramll.controller;

import cr.ac.una.taskprogramll.model.Team;
import cr.ac.una.taskprogramll.model.Tourney;
import cr.ac.una.taskprogramll.util.AppContext;
import cr.ac.una.taskprogramll.util.FlowController;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.net.URL;
import java.util.ArrayList;
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
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

/** * FXML Controller class * * @author ashly */
public class MatchTeamsController extends Controller implements Initializable {

    private Tourney currentTourney;
    private List<Team> currentTeamList;
    private List<String> randomChooseTeam = new ArrayList<>();
    private int index = 0;
    private int currentRound = 2;
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
        FlowController.getInstance().goViewInStage("ViewTourneys", (Stage) btnBack.getScene().getWindow());
    }

    @FXML
    private void onActionBtnStart(ActionEvent event) {
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
        if (currentTeamList.size() == 2) return 1;
        else if (currentTeamList.size() >= 3 && currentTeamList.size() <= 4) return 2;
        else if (currentTeamList.size() >= 5 && currentTeamList.size() <= 8) return 3;
        else if (currentTeamList.size() >= 9 && currentTeamList.size() <= 16) return 4;
        else if (currentTeamList.size() >= 17 && currentTeamList.size() <= 32) return 5;
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
         List<Team> randomDistribution = new ArrayList<>();
         int roundSize = currentTeamList.size();
         
         while(randomDistribution.size() != currentTeamList.size()){
             Random randomTeam = new Random();
             int randomChossenTeam = randomTeam.nextInt(roundSize);
             if(!alredyChoosedTeam(currentTeamList.get(randomChossenTeam).getName()))
                 randomDistribution.add(currentTeamList.get(randomChossenTeam));
         } currentTeamList = randomDistribution;
         
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
     
     private void adjustingTable(Team winnerTeam){
         switch (currentRound) {
             case 2:
                 round2.addLast(winnerTeam);
                 clmnRound2.setCellValueFactory(new PropertyValueFactory<>("name"));
                 tblPlayersTable.setItems(round2);
                 break;
             case 3:
                 round3.addLast(winnerTeam);
                 clmnRound3.setCellValueFactory(new PropertyValueFactory<>("name"));
                 tblPlayersTable.setItems(round3);
                 break;
             case 4:
                 round4.addLast(winnerTeam);
                 clmnRound4.setCellValueFactory(new PropertyValueFactory<>("name"));
                 tblPlayersTable.setItems(round4);
                 break;
             case 5:
                 round5.addLast(winnerTeam);
                 clmnRound5.setCellValueFactory(new PropertyValueFactory<>("name"));
                 tblPlayersTable.setItems(round5);
                 break;
             case 6:
                 round6.addLast(winnerTeam);
                 clmnRound6.setCellValueFactory(new PropertyValueFactory<>("name"));
                 tblPlayersTable.setItems(round6);
                 break;
             case 7:
                 winner.addLast(winnerTeam);
                 clmnFinal.setCellValueFactory(new PropertyValueFactory<>("name"));
                 tblPlayersTable.setItems(winner);
                 break;
             default:
                 throw new AssertionError();
         }
     }
     
     private void startGameParameters() {
         organizedRound();
         distributionOnTable();
     }
     
     private void continueGameParameters() {
         
     }
     
     private void viewGameTable(){
         btnStart.setManaged(false);
         btnStart.setVisible(false);
     }
     
    public void initializeFromAppContext() {
        this.currentTourney = (Tourney) AppContext.getInstance().get("SelectedTourney");
        this.currentTeamList = currentTourney.getTeamList();
        switch (currentTourney.returnState()) {
            case "Sin Empezar" -> startGameParameters();
            case "En Proceso" -> continueGameParameters();
            case "Finalizado" -> viewGameTable();
            default -> continueGameParameters();
        }
    }
     
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeFromAppContext();
    }    

    @Override
    public void initialize() {}

}
