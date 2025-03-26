/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.taskprogramll.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * FXML Controller class
 *
 * @author Michelle Wittingham
 */
public class ViewTourneysController implements Initializable {

    @FXML
    private MFXComboBox<?> comboDeportes;
    @FXML
    private TableView<?> tablaTorneos;
    @FXML
    private TableColumn<?, ?> colTorneo;
    @FXML
    private TableColumn<?, ?> colEstado;
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
    private void mostrarTorneos(ActionEvent event) {
    }

    @FXML
    private void goBack(ActionEvent event) {
    }
    
}
