package cr.ac.una.taskprogramll.controller;

import cr.ac.una.taskprogramll.model.Tourney;
import cr.ac.una.taskprogramll.util.AppContext;
import cr.ac.una.taskprogramll.util.FlowController;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.net.URL;
import java.util.ResourceBundle;

public class TourneysInfoController extends Controller implements Initializable {

    @FXML private StackPane ticketSuperior;
    @FXML private StackPane ticketInferior;
    @FXML private MFXButton btnBack;
    @FXML private Label lblWinner;
    @FXML private Label lblTeams;
    @FXML private Label lblRounds;
    @FXML private Label lblTourney;
    @FXML private Label lblState;
    @FXML private Label lblTime;
    @FXML private MFXButton btnShowCertificate;
    @FXML private MFXButton btnShowTeams;
    @FXML private MFXButton btnShowKeys;

    private boolean isSuperiorInFront = true;
    private boolean isSlid = false;
    private Tourney selectedTourney;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ticketSuperior.toFront();
        isSuperiorInFront = true;
        isSlid = false;

        selectedTourney = (Tourney) AppContext.getInstance().get("SelectedTourney");
        if (selectedTourney != null) {
            lblTourney.setText(selectedTourney.getName());
            lblState.setText(selectedTourney.returnState());
            lblTime.setText(String.valueOf(selectedTourney.getTime()));
            
            if (selectedTourney.returnState() == "Finalizado") {
                lblWinner.setText("Determinado");
            } else {
                lblWinner.setText("Sin determinar");
            }

            int teamCount = 0;
            if (selectedTourney.getTeamList() != null) {
                teamCount += selectedTourney.getTeamList().size();
            }
            if (selectedTourney.getLoosersList() != null) {
                teamCount += selectedTourney.getLoosersList().size();
            }
            lblTeams.setText(String.valueOf(teamCount));
            lblRounds.setText(String.valueOf(teamCount / 2));
        } else {
            System.err.println("No Tourney selected in AppContext");
        }
    }

    @FXML
    public void slideTicket(MouseEvent event) {
        if (isSuperiorInFront) {
            toggleSlideSuperior();
        } else {
            toggleSlideInferior();
        }
    }

    @FXML
    public void slideBackTicket(MouseEvent event) {
        if (isSuperiorInFront) {
            toggleSlideSuperior();
        } else {
            toggleSlideInferior();
        }
    }

    private void toggleSlideSuperior() {
        if (!isSlid) {
            TranslateTransition transition = new TranslateTransition(Duration.millis(300), ticketSuperior);
            transition.setToX(300);
            transition.setOnFinished(e -> ticketSuperior.toFront());
            transition.play();
            isSlid = true;
        } else {
            TranslateTransition slideOut = new TranslateTransition(Duration.millis(300), ticketSuperior);
            slideOut.setToX(600);
            TranslateTransition slideBack = new TranslateTransition(Duration.millis(300), ticketSuperior);
            slideBack.setToX(0);
            slideBack.setOnFinished(e -> {
                ticketSuperior.toBack();
                ticketInferior.toFront();
                isSuperiorInFront = false;
            });
            new SequentialTransition(slideOut, slideBack).play();
            isSlid = false;
        }
    }

    private void toggleSlideInferior() {
        if (!isSlid) {
            ticketSuperior.setTranslateX(0);
            TranslateTransition transition = new TranslateTransition(Duration.millis(300), ticketInferior);
            transition.setToX(300);
            transition.setOnFinished(e -> ticketInferior.toFront());
            transition.play();
            isSlid = true;
        } else {
            TranslateTransition slideOut = new TranslateTransition(Duration.millis(300), ticketInferior);
            slideOut.setToX(600);
            TranslateTransition slideBack = new TranslateTransition(Duration.millis(300), ticketInferior);
            slideBack.setToX(0);
            slideBack.setOnFinished(e -> {
                ticketInferior.toBack();
                ticketSuperior.toFront();
                isSuperiorInFront = true;
            });
            new SequentialTransition(slideOut, slideBack).play();
            isSlid = false;
        }
    }

    @FXML
    private void goBack() {
        FlowController.getInstance().goViewInStage("ViewTourneys", (Stage) ticketSuperior.getScene().getWindow());
    }

    private void showCertificate() {
        System.out.println("Show certificate for " + selectedTourney.getName());
    }

    private void showTeams() {
        System.out.println("Show teams for " + selectedTourney.getName());
    }

    private void showKeys() {
        System.out.println("Show keys for " + selectedTourney.getName());
    }

    @Override
    public void initialize() {}
}