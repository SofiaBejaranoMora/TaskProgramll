/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.taskprogramll.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

/**
 * FXML Controller class
 *
 * @author Michelle Wittingham
 */
public class TourneysInfoController implements Initializable {

    @FXML
    private ImageView background;
    @FXML
    private StackPane ticketInferior;
    @FXML
    private ImageView ticketBackground1;
    @FXML
    private Label lblRondas1;
    @FXML
    private Label lblRondas11;
    @FXML
    private StackPane ticketSuperior;
    @FXML
    private ImageView ticketBackground;
    @FXML
    private Label lblPuntos;
    @FXML
    private Label lblRondas;
    @FXML
    private MFXButton btnBack;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void slideBackTicket(MouseEvent event) {
    }

    @FXML
    private void slideTicket(MouseEvent event) {
    }

    @FXML
    private void goBack(ActionEvent event) {
    }
    
}
