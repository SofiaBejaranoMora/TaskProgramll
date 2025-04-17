package cr.ac.una.taskprogramll.controller;

import cr.ac.una.taskprogramll.model.Team;
import cr.ac.una.taskprogramll.model.Tourney;
import cr.ac.una.taskprogramll.util.AppContext;
import cr.ac.una.taskprogramll.util.FlowController;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.net.URL;
import java.util.Collections;
import java.util.List;
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
    Team winnerTeam = (Team) AppContext.getInstance().get("WinnerTeam");
    AppContext.getInstance().delete("WinnerTeam");
    if (winnerTeam != null) {
        adjustingTable(winnerTeam);
    }
    if (!encounter()) {
        // Si no hay más enfrentamientos en la ronda actual, no vamos a Game
        return;
    }
    AppContext.getInstance().set("CurrentTourney", currentTourney);
    FlowController.getInstance().goViewInStage("Game", (Stage) btnStart.getScene().getWindow());
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
         currentTourney.getContinueGame().getRound1().addAll(round1);
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
     
private boolean encounter() {
    // Si no hay suficientes equipos para un enfrentamiento, no continuamos
    if (index + 1 >= currentTeamList.size()) {
        // Si no hay más enfrentamientos en esta ronda, reiniciar índice y preparar próxima ronda
        index = 0;
        return false; // No hay enfrentamiento, no ir a Game
    }

    // Guardar el estado actual del enfrentamiento
    currentTourney.getContinueGame().setContinueIndexTeam(index);
    currentTourney.getContinueGame().setCurrentRound(currentRound);
    currentTourney.getContinueGame().setContinueIdTeam(currentTeamList.get(index).getId());

    // Pasar los equipos que se enfrentan a la vista Game
    AppContext.getInstance().set("Team1", currentTeamList.get(index));
    AppContext.getInstance().set("Team2", currentTeamList.get(index + 1));

    return true; // Hay enfrentamiento, ir a Game
}

private void adjustingTable(Team winnerTeam) {
    int teamsInNextRound;
    switch (currentRound) {
        case 1 -> {
            round2.addFirst(winnerTeam);
            System.out.println("Avanzó el equipo " + winnerTeam.getName() + " a la segunda ronda.");
            clmnRound2.setCellValueFactory(new PropertyValueFactory<>("name"));
            tblPlayersTable.setItems(round2);
            index += 2;
            teamsInNextRound = globalSize / 2;
            if (round2.size() == teamsInNextRound) {
                currentRound = 2;
                currentTeamList.clear();
                currentTeamList.addAll(round2);
                index = 0;
                currentTourney.getContinueGame().getRound2().clear();
                currentTourney.getContinueGame().getRound2().addAll(round2);
                System.out.println("Ronda 1 completada. Avanzando a la ronda 2.");
            }
        }
        case 2 -> {
            round3.addLast(winnerTeam);
            System.out.println("Avanzó el equipo " + winnerTeam.getName() + " a la tercera ronda.");
            clmnRound3.setCellValueFactory(new PropertyValueFactory<>("name"));
            tblPlayersTable.setItems(round3);
            index += 2;
            teamsInNextRound = globalSize / 4;
            if (round3.size() == teamsInNextRound) {
                currentRound = 3;
                currentTeamList.clear();
                currentTeamList.addAll(round3);
                index = 0;
                currentTourney.getContinueGame().getRound3().clear();
                currentTourney.getContinueGame().getRound3().addAll(round3);
                System.out.println("Ronda 2 completada. Avanzando a la ronda 3.");
            }
        }
        case 3 -> {
            round4.addLast(winnerTeam);
            System.out.println("Avanzó el equipo " + winnerTeam.getName() + " a la cuarta ronda.");
            clmnRound4.setCellValueFactory(new PropertyValueFactory<>("name"));
            tblPlayersTable.setItems(round4);
            index += 2;
            teamsInNextRound = globalSize / 8;
            if (round4.size() == teamsInNextRound) {
                currentRound = 4;
                currentTeamList.clear();
                currentTeamList.addAll(round4);
                index = 0;
                currentTourney.getContinueGame().getRound4().clear();
                currentTourney.getContinueGame().getRound4().addAll(round4);
                System.out.println("Ronda 3 completada. Avanzando a la ronda 4.");
            }
        }
        case 4 -> {
            round5.addLast(winnerTeam);
            System.out.println("Avanzó el equipo " + winnerTeam.getName() + " a la quinta ronda.");
            clmnRound5.setCellValueFactory(new PropertyValueFactory<>("name"));
            tblPlayersTable.setItems(round5);
            index += 2;
            teamsInNextRound = globalSize / 16;
            if (round5.size() == teamsInNextRound) {
                currentRound = 5;
                currentTeamList.clear();
                currentTeamList.addAll(round5);
                index = 0;
                currentTourney.getContinueGame().getRound5().clear();
                currentTourney.getContinueGame().getRound5().addAll(round5);
                System.out.println("Ronda 4 completada. Avanzando a la ronda 5.");
            }
        }
        case 5 -> {
            round6.addLast(winnerTeam);
            System.out.println("Avanzó el equipo " + winnerTeam.getName() + " a la sexta ronda.");
            clmnRound6.setCellValueFactory(new PropertyValueFactory<>("name"));
            tblPlayersTable.setItems(round6);
            index += 2;
            teamsInNextRound = globalSize / 32;
            if (round6.size() == teamsInNextRound) {
                currentRound = 6;
                currentTeamList.clear();
                currentTeamList.addAll(round6);
                index = 0;
                currentTourney.getContinueGame().getRound6().clear();
                currentTourney.getContinueGame().getRound6().addAll(round6);
                System.out.println("Ronda 5 completada. Avanzando a la ronda 6.");
            }
        }
        case 6 -> {
            winner.addLast(winnerTeam);
            System.out.println("¡El equipo " + winnerTeam.getName() + " es el ganador del torneo!");
            clmnFinal.setCellValueFactory(new PropertyValueFactory<>("name"));
            tblPlayersTable.setItems(winner);
            currentTourney.getContinueGame().getWinner().clear();
            currentTourney.getContinueGame().getWinner().addAll(winner);
            System.out.println("Torneo finalizado.");
        }
        default -> throw new AssertionError();
    }
    currentTourney.getContinueGame().setCurrentRound(currentRound);
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
         switch(currentRound){
             case 1 ->
                 currentTeamList = round1;
             case 2 ->
                 currentTeamList = round2;
             case 3 ->
                 currentTeamList = round3;
             case 4 ->
                 currentTeamList = round4;
             case 5 ->
                 currentTeamList = round5;
             case 6 ->
                 currentTeamList = round6;
             default ->
                 currentTeamList = winner;             
         }
     }
     
     private void viewGameTable(){
         btnStart.setManaged(false);
         btnStart.setVisible(false);
     }
     
    public void initializeFromAppContext() {
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
        AppContext.getInstance().delete("SelectedTourney");
    }
     
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeFromAppContext();
    }    

    @Override
    public void initialize() {}

}
