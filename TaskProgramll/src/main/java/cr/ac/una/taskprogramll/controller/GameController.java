package cr.ac.una.taskprogramll.controller;

import cr.ac.una.taskprogramll.model.Sport;
import cr.ac.una.taskprogramll.model.Team;
import cr.ac.una.taskprogramll.model.Tourney;
import cr.ac.una.taskprogramll.util.AppContext;
import cr.ac.una.taskprogramll.util.FlowController;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.net.URL;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

/** FXML Controller class ** @author ashly */
public class GameController extends Controller implements Initializable {
    
    @FXML
    private MFXButton btnOut;
    @FXML
    private MFXButton btnBack;
    @FXML
    private MFXButton btnStart;
    @FXML
    private TableColumn<Team, String> clmnFinal;
    @FXML
    private TableColumn<Team, String> clmnRound1;
    @FXML
    private TableColumn<Team, String> clmnRound2;
    @FXML
    private TableColumn<Team, String> clmnRound3;
    @FXML
    private TableColumn<Team, String> clmnRound4;
    @FXML
    private TableColumn<Team, String> clmnRound5;
    @FXML
    private TableColumn<Team, String> clmnRound6;    
    @FXML
    private TableView<Team> tblPlayersTable;
    @FXML
    private Label lblFirstTeam;
    @FXML
    private Label lblSecondTeam;
    @FXML
    private Label lblTimer;
    @FXML
    private ImageView mgvBall;
    @FXML
    private ImageView mgvFirstTeam;
    @FXML
    private ImageView mgvSecondTeam;
    
//Variables de MatchTeams           
    private Tourney actualTourney;
    private int timeLimit;
    private int index = 0;
    private int round = discoverRounds();
    private List<Team> teamNames; //Tenemos las listas desde acá.
    private List<String> choosenNames = new ArrayList<>();
    private ObservableList<Team> round1 = FXCollections.observableArrayList();
    private ObservableList<Team>round2 = FXCollections.observableArrayList();
    private ObservableList<Team>round3 = FXCollections.observableArrayList();
    private ObservableList<Team>round4 = FXCollections.observableArrayList();
    private ObservableList<Team>round5 = FXCollections.observableArrayList();
    private ObservableList<Team>round6 =FXCollections.observableArrayList();
    private ObservableList<Team>winner =FXCollections.observableArrayList();
//Variables de Game    
    private Timeline timeLine;
    private Boolean timerStarted = false;
    private int timeCalculate = 0;
    private String nameFirstTeam = "";
    private int counterFirstTeam = 0;
    private String nameSecondTeam = "";
    private int counterSecondTeam = 0;
    private Sport selectedSport;

    @FXML
    void onActionBtnOut(ActionEvent event) {
        FlowController.getInstance().goView("PlayersTable");
        timeLine.pause();
    }
    
    @FXML
    void onActionBtnBack(ActionEvent event) {
        FlowController.getInstance().goView("ViewTourneys");
    }

    @FXML
    void onActionBtnStart(ActionEvent event) {
        FlowController.getInstance().goView("Game");
    }
    
    @FXML
    void onMouseDraggedMgvBall(MouseEvent event) {
        mgvBall.setLayoutX(event.getSceneX());
        mgvBall.setLayoutY(event.getSceneY());
        counterPoints();
    }

    @FXML
    void onMousePressedMgvBall(MouseEvent event) {
        mgvBall.setCursor(Cursor.CLOSED_HAND);
        if(!timerStarted){
            timerStarted = true;
            timer(timeLimit);
        }
    }

    @FXML
    void onMouseReleasedMgvBall(MouseEvent event) {
        mgvBall.setCursor(Cursor.DEFAULT);
        counterPoints();
    }
    
//Vista de  match en equipos y llaves del torneo
    private void distributionOnTable() {
    List<Team> distributionTeams = new ArrayList<>();
          int roundSize = teamNames.size();

        while (distributionTeams.size() != teamNames.size()) {
            Random randomTeam = new Random();
            int choosenTeam = randomTeam.nextInt(roundSize);
            if (!choosed(teamNames.get(choosenTeam).getName())) {
                distributionTeams.add(teamNames.get(choosenTeam));
            }
        }

        round1.setAll(distributionTeams);
        clmnRound1.setCellValueFactory(new PropertyValueFactory<>("name"));
        tblPlayersTable.setItems(round1); //Respuesta a mi duda con tablas, descubierta, para que sea funcional se le hace el property value al tipo de datoque se desea obtener, by Michigam
        Image BallonImage = new Image("file:" + selectedSport.RuteImage());
        mgvFirstTeam.setImage(BallonImage);
    }
    
    private Boolean choosed(String name){ 
       if (choosenNames.contains(name)) {
           return true;
       } else {
            choosenNames.add(name);
            return false;
       }
   }

    private void encounter() {
            Image firstImage = new Image("file:" + teamNames.get(index).RuteImage());
            mgvFirstTeam.setImage(firstImage);
            nameFirstTeam = teamNames.get(index).getName();
            
            if(teamNames.get(index + 1) != null) {
                Image secondImage = new Image("file:" + teamNames.get(index + 1).RuteImage());
                mgvSecondTeam.setImage(secondImage);
                nameSecondTeam = teamNames.get(index + 1).getName();
            } else ; 
    }
    
    private int discoverRounds(){
        if (teamNames.size() == 2) 
            return 1;
        else if (teamNames.size() > 2 && teamNames.size() < 4)
            return 2;
        else if (teamNames.size() > 5 && teamNames.size() < 8)
            return 3;
        else if (teamNames.size() > 9 && teamNames.size() < 16)
            return 4;
        else if (teamNames.size() > 17 && teamNames.size() < 32)
            return 5;
        else return 6;
    }   
    
    private void organizeRounds(){
        switch (round) {
            case 1:
                clmnRound2.setVisible(false);
                clmnRound3.setVisible(false);
                clmnRound4.setVisible(false);
                clmnRound5.setVisible(false);
                clmnRound6.setVisible(false); 
                break;
            case 2:
                clmnRound3.setVisible(false);
                clmnRound4.setVisible(false);
                clmnRound5.setVisible(false);
                clmnRound6.setVisible(false);              
                break;
            case 3:
                clmnRound4.setVisible(false);
                clmnRound5.setVisible(false);
                clmnRound6.setVisible(false); 
                break;
            case 4:
                clmnRound5.setVisible(false);
                clmnRound6.setVisible(false); 
                break;
            case 5:
                clmnRound6.setVisible(false); 
                break;
            case 6:
                System.out.println("Hola profe, me saque esto con un excel, me gusto mucho");
                break;
        }
    }
    
    private void adjustingTable(Team winnerTeam){ //Problemas para editar la ganadora
        switch (round) {
            case 1:
                winner.add(winnerTeam);
                clmnFinal.setCellValueFactory(new PropertyValueFactory<>("name"));
                tblPlayersTable.setItems(winner);
                 break;
            case 2:
                round2.add(winnerTeam);
                clmnRound2.setCellValueFactory(new PropertyValueFactory<>("name"));
                tblPlayersTable.setItems(round2);
                break;
            case 3:
                round3.add(winnerTeam);
                clmnRound3.setCellValueFactory(new PropertyValueFactory<>("name"));
                tblPlayersTable.setItems(round3);
                break;
            case 4:
                round4.add(winnerTeam);
                clmnRound4.setCellValueFactory(new PropertyValueFactory<>("name"));
                tblPlayersTable.setItems(round4);
                break;
            case 5:
                round5.add(winnerTeam);
                clmnRound5.setCellValueFactory(new PropertyValueFactory<>("name"));
                tblPlayersTable.setItems(round5);
                break;
            case 6:
                round6.add(winnerTeam);
                clmnRound6.setCellValueFactory(new PropertyValueFactory<>("name"));
                tblPlayersTable.setItems(round6);
                break;
            default:
                throw new AssertionError();
        }
    }
    
//A partir de acá se trabaja con la vista Game
    private void timer(int time) {
        timeLine = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if(timeCalculate <= timeLimit){
                timeCalculate++;
                lblTimer.setText(timeFormat(timeCalculate));
            } else{
                timeLine.stop();
                lblTimer.setText("0:00");
                afterGame();
                index += 2;
            }
        }));
        timeLine.setCycleCount(timeLimit);
    }   
    
    private String timeFormat(int totalSeconds) {
        return String.format("%02d:%02d", (totalSeconds / 60), (totalSeconds % 60));
    }
    
    private void counterPoints() {
        if(mgvBall.getBoundsInLocal() != null && mgvFirstTeam.getBoundsInLocal() != null && mgvSecondTeam.getBoundsInLocal() != null) {
             if(mgvBall.getBoundsInLocal().contains(mgvFirstTeam.getBoundsInLocal().getCenterX(), mgvFirstTeam.getBoundsInLocal().getCenterY())) {
                counterFirstTeam++;
                lblFirstTeam.setText("" + counterFirstTeam);
                
            } else if (mgvBall.getBoundsInLocal().contains(mgvSecondTeam.getBoundsInLocal().getCenterX(), mgvSecondTeam.getBoundsInLocal().getCenterY())) {
                counterSecondTeam++;
                lblSecondTeam.setText("" + counterSecondTeam);
                
            } else 
                 System.out.println("El balón no toca los equipos...");
        } else
            System.out.println("Falla de limites en imagenes...");
    }
    
    private void winnerAnimatic(String winner, String looser) {
        
    }
    
    private void afterGame() {
        if(counterFirstTeam > counterSecondTeam)
            winnerAnimatic(nameFirstTeam, nameSecondTeam);
        else if (counterFirstTeam < counterSecondTeam) 
            winnerAnimatic(nameSecondTeam, nameFirstTeam);
        else {}
            //Animatica empate moneda
            
            //Divicion de puntos para los historiales
            //Pase de nombre a siguiente columna
            //
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
//Inicializador completo   
        initializeFromAppContext();
    }    

    @Override
    public void initialize() {
    }
                
    public void initializeFromAppContext() {
        this.actualTourney = (Tourney) AppContext.getInstance().get("actualTourney");
        this.teamNames = actualTourney.getTeamList();
        this.timeLimit = actualTourney.getTime();
        this.selectedSport = actualTourney.getSportType();
        if ("Sin empezar".equals(actualTourney.returnState()))
            startGameParameters();
        else if ("En proceso".equals(actualTourney.returnState()))
            continueGameParameters();
        else
            continueGameParameters();
    }
    
     private void startGameParameters() {
        organizeRounds();
        distributionOnTable();
        encounter();
        timer(timeLimit);
    }
       
    private void continueGameParameters() {
        
    }
     
    public void clearAppContext() {
        AppContext.getInstance().delete("actualTourney");
    }
    
    private void resetGame() {
        clearAppContext();
        round1.clear();
        lblTimer.setText("00:00");
        lblFirstTeam.setText("0");
        lblSecondTeam.setText("0");
    }
    
}
