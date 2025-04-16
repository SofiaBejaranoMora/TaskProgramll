package cr.ac.una.taskprogramll.controller;

import cr.ac.una.taskprogramll.model.Sport;
import cr.ac.una.taskprogramll.model.Team;
import cr.ac.una.taskprogramll.model.Tourney;
import cr.ac.una.taskprogramll.util.AppContext;
import cr.ac.una.taskprogramll.util.FlowController;
import cr.ac.una.taskprogramll.util.Mensaje;
import cr.ac.una.taskprogramll.util.ResourceUtil;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

/** * FXML Controller class * * @author ashly */
public class GameController extends Controller implements Initializable {

    private final Mensaje message = new Mensaje();
    private Tourney currentTourney;
    private List<Team> currentTeamList;
    private Sport currentSport;
    private Timeline currentTime;
    private Boolean timerStarted = false;
    private String nameFirstTeam = "";
    private String nameSecondTeam = "";
    private int index = 0;
    private int currentRound =1;
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
    private Label lblTimer;
    @FXML
    private MFXButton btnOut;
    
    @FXML
    void onDragDetectedMgvBall(MouseEvent event) {
            if (!timerStarted) {
                timerStarted = true;
                timer();
                currentTime.play();
            }
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
                Bounds firstTeam = mgvFirstTeam.localToScene(mgvFirstTeam.getBoundsInLocal());
                Bounds secondTeam = mgvSecondTeam.localToScene(mgvSecondTeam.getBoundsInLocal());
                if (firstTeam.contains(releaseEvent.getSceneX(), releaseEvent.getSceneY())) 
                    counterPoints(1);
                else if (secondTeam.contains(releaseEvent.getSceneX(), releaseEvent.getSceneY()))
                    counterPoints(2);
                ncpRoot.getChildren().remove(copyBall);
                mgvBall.setVisible(true);
                ncpRoot.setOnMouseDragged(null);
                ncpRoot.setOnMouseReleased(null);
            });
    }
    
    @FXML
    private void onActionBtnOut(ActionEvent event) {
        FlowController.getInstance().goViewInStage("MatchTeams", (Stage) btnOut.getScene().getWindow());
    }
    
    private void chargeImages() {
        mgvBall.setImage(new Image(ResourceUtil.getImagePath(currentSport.getId())));
        mgvFirstTeam.setImage(new Image(ResourceUtil.getImagePath(currentTeamList.get(index).getId())));

        if (currentRound % 2 != 0) {
            mgvSecondTeam.setImage(new Image(ResourceUtil.getImagePath(currentTeamList.get(index + 1).getId())));

        } else {
            mgvSecondTeam.setImage(new Image(ResourceUtil.getImagePath(currentTeamList.get(index - 1).getId())));

        }
    }
    
    private String timerFormat(int totalSeconds) {
        return String.format("%02d:%02d", (totalSeconds / 60), (totalSeconds % 60));
    }
    
    private void timer() {
        currentTime = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            if (timeCalculate <= timeLimit){
                timeCalculate++;
                lblTimer.setText(timerFormat(timeCalculate));
            } else {
                currentTime.stop();
                afterGame();
            }
        }));
        currentTime.setCycleCount(timeLimit);
    }
    
    private void counterPoints(int goalTo) {
        switch (goalTo) {
            case 1 -> {
                counterGoalsFirstTeam++;
                lblFirstTeam.setText("" + counterGoalsFirstTeam);
                System.out.println("\nEl balon toca al equipo #1\n");
            }
            case 2 -> {
                counterGoalsSecondTeam++;
                lblSecondTeam.setText("" + counterGoalsSecondTeam);
                System.out.println("\nEl balon toca al equipo #2\n");
            }
            default -> System.out.println("\nEl balon no toca los equipos...\n");
        }
    }
    
    private void winnerAnimatic(ImageView winner){
        Image toRize = winner.getImage();
        winner.setFitWidth(toRize.getWidth() * 1.15);
        winner.setFitHeight(toRize.getHeight()* 1.15);
        ColorAdjust greenTone = new ColorAdjust();
        greenTone.setHue(0.3);             //Ajusta el tono a un verde
        greenTone.setBrightness(0.2);   //Aclara levemente
        winner.setEffect(greenTone);
    }

    private void looserAnimatic(ImageView looser){
        Image toRize = looser.getImage();
        looser.setFitWidth(toRize.getWidth() * 0.85);
        looser.setFitHeight(toRize.getHeight()* 0.85);
        ColorAdjust redTone = new ColorAdjust();
        redTone.setHue(-0.55);             //Ajusta el tono a un rojo
        redTone.setBrightness(0.2);      //Aclara levemente
        looser.setEffect(redTone);
    }
    
    private void drawAnimatic(){}
    
    private void afterGame() {
        if (counterGoalsFirstTeam > counterGoalsSecondTeam){
            currentTourney.winnerAndLooser(nameFirstTeam, counterGoalsFirstTeam, 3, nameSecondTeam, counterGoalsSecondTeam);
            AppContext.getInstance().set("WinnerTeam", currentTeamList.get(index));
            winnerAnimatic(mgvFirstTeam);
            looserAnimatic(mgvSecondTeam);
            
        } else if (counterGoalsFirstTeam < counterGoalsSecondTeam) {
            currentTourney.winnerAndLooser(nameSecondTeam, counterGoalsSecondTeam, 3, nameFirstTeam, counterGoalsFirstTeam);
            if (currentRound % 2 != 0) AppContext.getInstance().set("WinnerTeam", currentTeamList.get(index + 1));
            else AppContext.getInstance().set("WinnerTeam", currentTeamList.get(index - 1));
            winnerAnimatic(mgvSecondTeam);
            looserAnimatic(mgvFirstTeam);
            
        } else {/*Animatica de empate*/}
        resetGame();
    }
    
    private void resetGame() {
        lblTimer.setText("00:00");
        lblFirstTeam.setText("0");
        lblSecondTeam.setText("0");
    }
    
    private void InitializeFromAppContext() {
        this.currentTourney = (Tourney) AppContext.getInstance().get("CurrentTourney");
        this.currentTeamList = currentTourney.getTeamList();
        this.index = currentTourney.getContinueGame().getContinueIndexTeam();
        this.currentRound = currentTourney.getContinueGame().getCurrentRound();
        this.timeLimit = currentTourney.getTime();
        this.currentSport = currentTourney.searchSportType();
        chargeImages();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            InitializeFromAppContext(); //Posee un error, pero creo que está relacionado a carga de imagenes
        } catch (Exception e) {
            message.show(Alert.AlertType.ERROR, "Error de Inicialización", "No se inicializo: " + e.getMessage());
        }
    }    

    @Override
    public void initialize() {}

}