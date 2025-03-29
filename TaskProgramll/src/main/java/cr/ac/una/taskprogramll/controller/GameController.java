/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.taskprogramll.controller;

import cr.ac.una.taskprogramll.model.Team;
import cr.ac.una.taskprogramll.model.Tourney;
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
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author ashly
 */
public class GameController extends Controller implements Initializable {
    
    private Tourney actualTourney;
    @FXML
    private MFXButton btnOut;
    @FXML
    private MFXButton btnBack;
    @FXML
    private MFXButton btnStart;
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
        
    @FXML
    void onActionBtnOut(ActionEvent event) {
        FlowController.getInstance().goView("PlayersTable");
    }
    
    @FXML
    void onActionBtnBack(ActionEvent event) {
        FlowController.getInstance().goView("Lobby");
    }

    @FXML
    void onActionBtnStart(ActionEvent event) {
        FlowController.getInstance().goView("Game");
        encounter(count);
        count += 2;
    }
    
    @FXML
    void onMouseDraggedMgvBall(MouseEvent event) {
        mgvBall.setLayoutX(event.getSceneX());
        mgvBall.setLayoutY(event.getSceneY());
    }

    @FXML
    void onMousePressedMgvBall(MouseEvent event) {
        mgvBall.setCursor(Cursor.CLOSED_HAND);
    }

    @FXML
    void onMouseReleasedMgvBall(MouseEvent event) {
        mgvBall.setCursor(Cursor.DEFAULT);
    }
    
    private int timeLimit = actualTourney.getTime();
    private List<Team> teamNames = actualTourney.getTeamList(); //Tenemos las listas desde acá.
    private final ObservableList<Team> matchTeams = FXCollections.observableArrayList();
    private int count = 0;
    
//Vista de  match en equipos y llaves del torneo
    private void distributionOnTable() {
        if("Sin empezar".equals(actualTourney.getState())) {
            matchTeams.clear();
            List<String> distributionTeams = new ArrayList<>();
            int roundSize = teamNames.size();
            while (distributionTeams.size() == teamNames.size()) {
                Random randomTeam = new Random();
                int choosenTeam = randomTeam.nextInt(roundSize);
                if (!choosed(teamNames.getClass().getName()))
                    distributionTeams.add(teamNames.get(choosenTeam).getName());
            }
            tblPlayersTable.setItems(matchTeams);
        }
        String imageBallonPath = teamNames.getClass() + ".png";
        Image BallonImage = new Image(getClass().getResourceAsStream(imageBallonPath));
        mgvFirstTeam.setImage(BallonImage);
    }
    
    private Boolean choosed(String name){ 
        List<String> choosenNames = new ArrayList<>();
       if (choosenNames.contains(name)) {
           return true;
       } else {
            choosenNames.add(name);
            return false;
       }
   }

    public void encounter(int index) {
            String imageFirstPath = teamNames.get(index).getNameTeamImage() + ".png";
            Image firstImage = new Image(getClass().getResourceAsStream(imageFirstPath));
            mgvFirstTeam.setImage(firstImage);
            String imageSecondPath = teamNames.get(index).getNameTeamImage() + ".png";
            Image secondImage = new Image(getClass().getResourceAsStream(imageSecondPath));
            mgvSecondTeam.setImage(secondImage);
    }
    
    private Timeline timeLine;
    private int timeCalculate = 0;
    private int counterFirstTeam = 0;
    private int counterSecondTeam = 0;
    
//A partir de acá se trabaja con la vista Game
    private void timer() {
        timeLine = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if(timeCalculate <= timeLimit){
                timeCalculate++;
                lblTimer.setText(timeFormat(timeCalculate));
            } else{
                timeLine.stop();
            }
        }));
        timeLine.setCycleCount(timeLimit);
    }   
    
    private String timeFormat(int totalSeconds) {
        return String.format("%02d:%02d", (totalSeconds / 60), (totalSeconds % 60));
    }
    
    public void counterPoints() {
        if(mgvBall.getBoundsInLocal() != null && mgvFirstTeam.getBoundsInLocal() != null && mgvSecondTeam.getBoundsInLocal() != null) {
             if(mgvBall.getBoundsInLocal().contains(mgvFirstTeam.getBoundsInLocal().getCenterX(), mgvFirstTeam.getBoundsInLocal().getCenterY())) {
                counterFirstTeam++;
                lblFirstTeam.setText("" + counterFirstTeam);
            } else if (mgvBall.getBoundsInLocal().contains(mgvSecondTeam.getBoundsInLocal().getCenterX(), mgvSecondTeam.getBoundsInLocal().getCenterY())) {
                counterSecondTeam++;
                lblSecondTeam.setText("" + counterSecondTeam);
            } else {
                 System.out.println("El balón no toca los equipos...");
            }
        } else {
            System.out.println("Falla de limites en imagenes...");
        }
    }
          

    @Override
    public void initialize(URL url, ResourceBundle rb) {
//Inicializador de...       
        
//Inicializar crónometro, marcador y más
        lblTimer.setText("0:00");
        lblFirstTeam.setText("0");
        lblSecondTeam.setText("0");
    }    

    @Override
    public void initialize() {
    }
    
}
