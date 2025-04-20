package cr.ac.una.taskprogramll.controller;

import cr.ac.una.taskprogramll.model.MatchDetails;
import cr.ac.una.taskprogramll.model.Sport;
import cr.ac.una.taskprogramll.model.Team;
import cr.ac.una.taskprogramll.model.Tourney;
import cr.ac.una.taskprogramll.util.AppContext;
import cr.ac.una.taskprogramll.util.FlowController;
import cr.ac.una.taskprogramll.util.ResourceUtil;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.util.Duration;

/** * FXML Controller class * * @author ashly */
public class GameController extends Controller implements Initializable {

    private Tourney currentTourney;
    private Sport currentSport;
    private Timeline currentTime;
    private Boolean timerStarted = false;
    private Boolean isFinished = false;
    private Team firstTeam;
    private Team secondTeam;
    private int timeLimit;
    private int timeCalculate = 0;
    private int counterGoalsFirstTeam = 0;
    private int counterGoalsSecondTeam = 0;
    
    @FXML
    private AnchorPane ncpRoot;
    @FXML
    private Label lblFirstTeam;
    @FXML
    private Label lblSecondTeam;
    @FXML
    private ImageView mgvFirstTeam;
    @FXML
    private ImageView mgvBall;
    @FXML
    private ImageView mgvSecondTeam;
    @FXML
    private ImageView mgvWinFirstTeam;
    @FXML
    private ImageView mgvWinSecondTeam;
    @FXML
    private Label lblTimer;
    @FXML
    private MFXButton btnOut;
    @FXML
    private MFXButton btnFastFinish;
    
    @FXML
    void onDragDetectedMgvBall(MouseEvent event) {
            if (!timerStarted) {
                timerStarted = true;
                timer();
                currentTime.play();
            } else currentTime.play();
        if (!isFinished) {
            mgvBall.setVisible(false);
            Image ballImage = mgvBall.getImage();
            ImageView copyBall = new ImageView(ballImage);
            copyBall.setFitWidth(ballImage.getWidth() * 0.1);
            copyBall.setFitHeight(ballImage.getHeight() * 0.1);
            copyBall.setOpacity(1.0);
            ncpRoot.getChildren().add(copyBall);
            copyBall.setLayoutX(mgvBall.getLayoutX());
            copyBall.setLayoutY(mgvBall.getLayoutY());

            ncpRoot.setOnMouseDragged(dragEvent -> {
                copyBall.setCursor(Cursor.CLOSED_HAND);
                copyBall.setLayoutX(dragEvent.getSceneX() - copyBall.getFitWidth() / 2);
                copyBall.setLayoutY(dragEvent.getSceneY() - copyBall.getFitHeight() / 2);
            });

            ncpRoot.setOnMouseReleased((releaseEvent) -> {
                copyBall.setCursor(Cursor.DEFAULT);
                counterPoints(releaseEvent);
                ncpRoot.getChildren().remove(copyBall);
                mgvBall.setVisible(true);
                ncpRoot.setOnMouseDragged(null);
                ncpRoot.setOnMouseReleased(null);
            });
        }
    }
    
    @FXML
    private void onActionBtnOut(ActionEvent event) {
        if (!isFinished) {
            currentTime.pause();
            FlowController.getInstance().goViewInStage("MatchTeams", (Stage) btnOut.getScene().getWindow());
        } else {
            resetGame();
            FlowController.getInstance().goViewInStage("MatchTeams", (Stage) btnOut.getScene().getWindow());
        }
    }
    
    @FXML
    private void onActionBtnFastFinish(ActionEvent event) {
        mgvBall.setVisible(false);
        isFinished = true;
        int pointsFor = 0;
        while (pointsFor != 2) {
            Random randomPoints = new Random();
            int randomGoals = randomPoints.nextInt(10 + 1);
            if (pointsFor == 0) {
                counterGoalsFirstTeam = randomGoals;
                lblFirstTeam.setText("" + counterGoalsFirstTeam);
                pointsFor++;
            } else {
                counterGoalsSecondTeam = randomGoals;
                lblSecondTeam.setText("" + counterGoalsSecondTeam);
                pointsFor++;
            }
        }
        btnFastFinish.setVisible(false);
        afterGame();
    }
    
    private void chargeImages() {
        mgvWinFirstTeam.setVisible(false);
        mgvWinSecondTeam.setVisible(false);
        mgvBall.setImage(new Image(ResourceUtil.getImagePath(currentSport.getId())));
        mgvFirstTeam.setImage(new Image(ResourceUtil.getImagePath(firstTeam.getId())));
        mgvSecondTeam.setImage(new Image(ResourceUtil.getImagePath(secondTeam.getId())));
    }
    
    private String timerFormat(int totalSeconds) {
        return String.format("%02d:%02d", (totalSeconds / 60), (totalSeconds % 60));
    }
    
    private void timer() {
        currentTime = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            timeCalculate++;
            lblTimer.setText(timerFormat(timeCalculate));
            if (timeCalculate == timeLimit) {
                isFinished = true;
                mgvBall.setVisible(false);
                afterGame();
                currentTime.stop();
            }
        }));
        currentTime.setCycleCount(timeLimit);
    }
    
    private void counterPoints(MouseEvent releaseEvent) {
        Bounds firstTeamBounds = mgvFirstTeam.localToScene(mgvFirstTeam.getBoundsInLocal());
        Bounds secondTeamBounds = mgvSecondTeam.localToScene(mgvSecondTeam.getBoundsInLocal());
        if (firstTeamBounds.contains(releaseEvent.getSceneX(), releaseEvent.getSceneY())) {
                counterGoalsFirstTeam++;
                lblFirstTeam.setText("" + counterGoalsFirstTeam);
                System.out.println("\nEl balon toca al equipo #1\n");
        }
        else if (secondTeamBounds.contains(releaseEvent.getSceneX(), releaseEvent.getSceneY())) {
                counterGoalsSecondTeam++;
                lblSecondTeam.setText("" + counterGoalsSecondTeam);
                System.out.println("\nEl balon toca al equipo #2\n");
        }
    }
    
    private void winnerAnimatic(ImageView starWinner) {
        Scale scale = new Scale();
        starWinner.getTransforms().add(scale);
        Timeline timeline = new Timeline();
        KeyFrame increase = new KeyFrame(Duration.millis(500), new KeyValue(scale.xProperty(), 1.5), new KeyValue(scale.yProperty(), 1.5));
        KeyFrame reduce = new KeyFrame(Duration.millis(1000), new KeyValue(scale.xProperty(), 1.0), new KeyValue(scale.yProperty(), 1.0));
        timeline.getKeyFrames().addAll(increase, reduce);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
    
    private void drawAnimatic(){
        
    }
    
    private void afterGame() {
        MatchTeamsController controller = (MatchTeamsController) FlowController.getInstance().getController("MatchTeams");
        if (counterGoalsFirstTeam > counterGoalsSecondTeam){
            System.out.println("Ganador del partido: " + firstTeam.getName());
            mgvWinFirstTeam.setVisible(true);
            currentTourney.winnerAndLooser(firstTeam.getName(), counterGoalsFirstTeam, 3, secondTeam.getName(), counterGoalsSecondTeam);
            controller.adjustingTable(firstTeam);
            firstTeam.setItemEncounterList(new MatchDetails(firstTeam.getName(), secondTeam.getName(), counterGoalsFirstTeam, counterGoalsSecondTeam));
            secondTeam.setItemEncounterList(new MatchDetails(secondTeam.getName(), firstTeam.getName(), counterGoalsSecondTeam, counterGoalsFirstTeam));
            winnerAnimatic(mgvWinFirstTeam);
            
        } else if (counterGoalsFirstTeam < counterGoalsSecondTeam) {
            System.out.println("Ganador del partido: " + secondTeam.getName());
            mgvWinSecondTeam.setVisible(true);
            currentTourney.winnerAndLooser(secondTeam.getName(), counterGoalsSecondTeam, 3, firstTeam.getName(), counterGoalsFirstTeam);
            controller.adjustingTable(secondTeam);
            firstTeam.setItemEncounterList(new MatchDetails(firstTeam.getName(), secondTeam.getName(), counterGoalsFirstTeam, counterGoalsSecondTeam));
            secondTeam.setItemEncounterList(new MatchDetails(secondTeam.getName(), firstTeam.getName(), counterGoalsSecondTeam, counterGoalsFirstTeam));
            winnerAnimatic(mgvWinSecondTeam); 
            
        } else {/*Animatica de empate*/}
    }
    
    private void resetGame() {
        lblTimer.setText("00:00");
        lblFirstTeam.setText("0");
        lblSecondTeam.setText("0");
        mgvWinFirstTeam.setVisible(false);
        mgvWinSecondTeam.setVisible(false);
        counterGoalsFirstTeam = 0;
        counterGoalsSecondTeam = 0;
        mgvBall.setVisible(true);
        timeCalculate = 0;
        isFinished = false;
        timerStarted =false;
    }
    
    public void InitializeGame(Team firstTeam, Team secondTeam) {
        this.firstTeam = firstTeam;
        this.secondTeam = secondTeam;
        chargeImages();
    }
             
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.currentTourney = (Tourney) AppContext.getInstance().get("CurrentTourney");
        this.timeLimit = currentTourney.getTime();
        this.currentSport = currentTourney.searchSportType();
        AppContext.getInstance().delete("CurrentTourney");
    }    

    @Override
    public void initialize() {}

}