/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.taskprogramll.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Michelle Wittingham
 */
public class TournamentHistoryController extends Controller implements Initializable {

    @FXML
    private AnchorPane rootPane;
    @FXML
    private VBox mainContainer;
    @FXML
    private VBox header;
    @FXML
    private Label titleLabel;
    @FXML
    private HBox filters;
    @FXML
    private MFXTextField teamSearch;
    @FXML
    private MFXComboBox<?> sportFilter;
    @FXML
    private MFXButton searchButton;
    @FXML
    private MFXButton clearButton;
    @FXML
    private HBox content;
    @FXML
    private TableView<?> tournamentTable;
    @FXML
    private TableColumn<?, ?> nameColumn;
    @FXML
    private TableColumn<?, ?> dateColumn;
    @FXML
    private TableColumn<?, ?> positionColumn;
    @FXML
    private VBox detailsPanel;
    @FXML
    private Label detailsTitle;
    @FXML
    private Label positionLabel;
    @FXML
    private Label matchesLabel;
    @FXML
    private Label statsLabel;
    @FXML
    private Label generalStatsLabel;
    @FXML
    private HBox footer;
    @FXML
    private MFXButton certificateButton;
    @FXML
    private ImageView certificatePreview;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @Override
    public void initialize() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
