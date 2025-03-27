/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.taskprogramll.controller;

import cr.ac.una.taskprogramll.model.Team;
import cr.ac.una.taskprogramll.util.FlowController;
import io.github.palexdev.materialfx.controls.MFXButton;
import cr.ac.una.taskprogramll.model.Tourney;
import javafx.scene.control.TableView;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

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
    private TableView<Team> tblvPlayersTable;
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
    private AnchorPane ncpRoot;
    private Timeline timeLine;
    private int timeCalculate = 0;
    private int counterFirstTeam = 0;
    private int counterSecondTeam = 0;
    private int timeLimit = actualTourney.getTime();
    
    @FXML
    void onActionBtnOut(ActionEvent event) {
        FlowController.getInstance().goView("Loby");
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
        //Inicializar crónometro, marcador y más
    }    

    @Override
    public void initialize() {
    }
    
}
