package cr.ac.una.taskprogramll.controller;

import cr.ac.una.taskprogramll.model.Sport;
import cr.ac.una.taskprogramll.model.Team;
import cr.ac.una.taskprogramll.model.Tourney;
import cr.ac.una.taskprogramll.util.AppContext;
import cr.ac.una.taskprogramll.util.FlowController;
import cr.ac.una.taskprogramll.util.ResourceUtil;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
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
import javafx.stage.Stage;
import javafx.util.Duration;

/** * FXML Controller class * * @author ashly */
public class GameController extends Controller implements Initializable {

    private Tourney currentTourney;
    private Sport currentSport;
    private Timeline currentTime;
    private Boolean timerStarted = false;
    private Boolean isFinished = false;
    private String nameFirstTeam = "";
    private String nameSecondTeam = "";
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
    
    private void chargeImages() {
        mgvWinFirstTeam.setVisible(false);
        mgvWinSecondTeam.setVisible(false);
        mgvBall.setImage(new Image(ResourceUtil.getImagePath(currentSport.getId())));
        mgvFirstTeam.setImage(new Image(ResourceUtil.getImagePath(firstTeam.getId())));
        nameFirstTeam = firstTeam.getName();
        mgvSecondTeam.setImage(new Image(ResourceUtil.getImagePath(secondTeam.getId())));
        nameSecondTeam = secondTeam.getName();
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
    
    private void drawAnimatic(){}
    
    private void afterGame() {
        MatchTeamsController controller = (MatchTeamsController) FlowController.getInstance().getController("MatchTeams");
        if (counterGoalsFirstTeam > counterGoalsSecondTeam){
            System.out.println("Ganador del partido: " + nameFirstTeam);
            mgvWinFirstTeam.setVisible(true);
            currentTourney.winnerAndLooser(nameFirstTeam, counterGoalsFirstTeam, 3, nameSecondTeam, counterGoalsSecondTeam);
            controller.adjustingTable(firstTeam);
            
        } else if (counterGoalsFirstTeam < counterGoalsSecondTeam) {
            System.out.println("Ganador del partido: " + nameSecondTeam);
            mgvWinSecondTeam.setVisible(true);
            currentTourney.winnerAndLooser(nameSecondTeam, counterGoalsSecondTeam, 3, nameFirstTeam, counterGoalsFirstTeam);
            controller.adjustingTable(secondTeam);
            
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