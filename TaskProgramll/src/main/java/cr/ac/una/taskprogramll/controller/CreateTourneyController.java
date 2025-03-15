/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.taskprogramll.controller;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import cr.ac.una.taskprogramll.model.Sport;
import cr.ac.una.taskprogramll.model.Team;
import cr.ac.una.taskprogramll.model.Tourney;
import io.github.palexdev.materialfx.controls.MFXSlider;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;

/**
 * FXML Controller class
 *
 * @author Michelle Wittingham
 */
public class CreateTourneyController implements Initializable {
    private List<Tourney> tourneyList=new ArrayList<>();

    @FXML
    private Button btnCancel;
    @FXML
    private Button btnStart;
    @FXML
    private MFXTextField txtTourneyName;
    @FXML
    private MFXTextField txtMatchTime;
    @FXML
    private ComboBox<Sport> tglLstSportType;
    @FXML
    private MFXSlider teamQuantitySlider;
    @FXML
    private Button btnAddTeam;
    @FXML
    private MFXTextField txtTeamName;
    @FXML
    private Button btnDeleteTeam;
    @FXML
    private MFXTextField txtDeleteTeamName;
    @FXML
    private Button btnAddRandomTeam;
    @FXML
    private TableView<?> tblTeams;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
  private boolean isValidNumericInput(String input) {
    input = input.trim(); 
    if (input.isEmpty()) {
                System.out.println("Advertencia: La entrada no puede estar vacía o contener solo espacios.");
        return false; // No se permiten solo espacios
    }

    try {
        int number = Integer.parseInt(input); 
      if (number <= 0) {
            System.out.println("Advertencia: Solo se permiten números positivos.");
        }
        return number > 0;
    } catch (NumberFormatException e) {
                System.out.println("Advertencia: La entrada debe ser un número entero válido.");
        return false; 
    }
}
  

    @FXML
    private void createTourney(ActionEvent event) {
    
    }



    
}
