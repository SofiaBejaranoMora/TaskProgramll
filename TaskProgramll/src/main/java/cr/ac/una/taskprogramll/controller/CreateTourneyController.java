/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.taskprogramll.controller;

import cr.ac.una.taskprogramll.model.Sport;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

/**
 * FXML Controller class
 *
 * @author Michelle Wittingham
 */
public class CreateTourneyController implements Initializable {

    @FXML
    private Button btnCancel;
    @FXML
    private Button btnStart;
    @FXML
    private MFXTextField txtTourneyName;
    @FXML
    private MFXTextField txtTeamQuantity;
    @FXML
    private MFXTextField txtMatchTime;
    @FXML
    private ComboBox<Sport> tglLstSportType;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
  private boolean isValidNumericInput(String input) {
    input = input.trim(); 
    if (input.isEmpty()) {
        return false; // No se permiten solo espacios
    }

    try {
        int number = Integer.parseInt(input); 
        return number > 0;
    } catch (NumberFormatException e) {
        return false; 
    }
}
  

    @FXML
    private void createTourney(ActionEvent event) {
        String name=txtTourneyName.getText();
        String time=txtMatchTime.getText();
        String teams=txtTeamQuantity.getText();
       if(!isValidNumericInput(time)||!isValidNumericInput(teams)){
           //mensaje de advertencia
           return;
       }
       name = name.trim(); 
       if(name.isEmpty()){
           //mensaje de advertenciia
           return;
       }
       
       //finalmeentee revisamos teams
   
    }



    
}
