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
import javafx.scene.image.ImageView;

public class TourneysInfoController extends Controller implements Initializable {

    // FXML-injected fields for the two tickets
    @FXML
    private StackPane ticketSuperior;

    @FXML
    private StackPane ticketInferior;

    // FXML-injected fields for the labels in ticketSuperior
    @FXML
    private Label lblPuntos;

    @FXML
    private Label lblRondas;

    // FXML-injected fields for the labels in ticketInferior
    @FXML
    private Label lblRondas1;

    @FXML
    private Label lblRondas11;

    // FXML-injected fields for image views
    @FXML
    private ImageView background;

    @FXML
    private ImageView ticketBackground1;

    @FXML
    private ImageView ticketBackground;

    @FXML
    private MFXButton btnBack;

    // State to track which ticket is in front and whether it's slid
    private boolean isSuperiorInFront = true; // true: ticketSuperior in front, false: ticketInferior in front
    private boolean isSlid = false; // true: the front ticket is slid to the right

    // The selected Tourney from AppContext
    private Tourney selectedTourney;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Ensure ticketSuperior is on top initially
        ticketSuperior.toFront();
        isSuperiorInFront = true;
        isSlid = false;

        // Load the selected Tourney from AppContext
        selectedTourney = (Tourney) AppContext.getInstance().get("SelectedTourney");
        if (selectedTourney != null) {
            // Update ticketSuperior labels with Tourney data
            lblPuntos.setText(selectedTourney.getName());
            lblRondas.setText(selectedTourney.returnState());

            // Update ticketInferior labels with placeholder data (adjust as needed)
            lblRondas1.setText(String.valueOf(selectedTourney.getTeamList() != null ? selectedTourney.getTeamList().size() : 0));
            lblRondas11.setText(String.valueOf(selectedTourney.getTime() != 0 ? selectedTourney.getTime() : 0));
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
            // First click: slide ticketSuperior to the right, keep it in front
            TranslateTransition transition = new TranslateTransition(Duration.millis(300), ticketSuperior);
            transition.setToX(300);
            transition.setOnFinished(e -> ticketSuperior.toFront());
            transition.play();
            isSlid = true;
        } else {
            // Second click: slide ticketSuperior further to the right, then back to 0 while moving to the back
            TranslateTransition slideOut = new TranslateTransition(Duration.millis(300), ticketSuperior);
            slideOut.setToX(600); // Slide further to the right to clear ticketInferior

            TranslateTransition slideBack = new TranslateTransition(Duration.millis(300), ticketSuperior);
            slideBack.setToX(0); // Slide back to original position
            slideBack.setOnFinished(e -> {
                ticketSuperior.toBack(); // Move to the back to "tuck under"
                ticketInferior.toFront(); // Ensure ticketInferior is in front
                isSuperiorInFront = false; // Switch to ticketInferior being in front
            });

            SequentialTransition sequentialTransition = new SequentialTransition(slideOut, slideBack);
            sequentialTransition.play();
            isSlid = false;
        }
    }

    private void toggleSlideInferior() {
        if (!isSlid) {
            // First click: slide ticketInferior to the right, keep it in front
            ticketSuperior.setTranslateX(0); // Reset ticketSuperior's position
            TranslateTransition transition = new TranslateTransition(Duration.millis(300), ticketInferior);
            transition.setToX(300);
            transition.setOnFinished(e -> ticketInferior.toFront());
            transition.play();
            isSlid = true;
        } else {
            // Second click: slide ticketInferior further to the right, then back to 0 while moving to the back
            TranslateTransition slideOut = new TranslateTransition(Duration.millis(300), ticketInferior);
            slideOut.setToX(600); // Slide further to the right to clear ticketSuperior

            TranslateTransition slideBack = new TranslateTransition(Duration.millis(300), ticketInferior);
            slideBack.setToX(0); // Slide back to original position
            slideBack.setOnFinished(e -> {
                ticketInferior.toBack(); // Move to the back to "tuck under"
                ticketSuperior.toFront(); // Ensure ticketSuperior is in front
                isSuperiorInFront = true; // Switch to ticketSuperior being in front
            });

            SequentialTransition sequentialTransition = new SequentialTransition(slideOut, slideBack);
            sequentialTransition.play();
            isSlid = false;
        }
    }

    @FXML
    private void goBack() {
        FlowController.getInstance().goViewInStage("ViewTourneys", (Stage) ticketSuperior.getScene().getWindow());
    }

    @Override
    public void initialize() {
    }
}