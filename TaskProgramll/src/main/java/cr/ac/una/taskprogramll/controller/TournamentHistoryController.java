/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.taskprogramll.controller;

import cr.ac.una.taskprogramll.model.FileManager;
import cr.ac.una.taskprogramll.model.Sport;
import cr.ac.una.taskprogramll.model.Team;
import cr.ac.una.taskprogramll.model.Tourney;
import cr.ac.una.taskprogramll.util.AppContext;
import cr.ac.una.taskprogramll.util.Mensaje;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
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
    private MFXComboBox<Sport> sportFilter;
    @FXML
    private MFXButton searchButton;
    @FXML
    private MFXButton clearButton;
    @FXML
    private HBox content;
    @FXML
    private TableView<Tourney> tournamentTable;
    @FXML
    private TableColumn<Tourney, String> nameColumn;
    @FXML
    private TableColumn<Tourney, Integer> idColumn;
    @FXML
    private TableColumn<Tourney, String> sportColumn;
    @FXML
    private TableColumn<Tourney, String> stateColumn;
    
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

    private ObservableList<Team> availableTeams;
    private ObservableList <Tourney> availableTourneys;
    private final List<Sport> sportList = new ArrayList<>();

    
    private FileManager fileManeger=new FileManager();
    private final Mensaje message = new Mensaje();

    @FXML
    private HBox lobbyIcon;
    
    @FXML
    private MFXButton btnRefresh;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        SetupTableColumns();
        SetupTableItems();
        LoadSportFilterList();
    }    
    
    private void SetupTableColumns(){
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        sportColumn.setCellValueFactory(new PropertyValueFactory<>("sportType"));
        stateColumn.setCellValueFactory(new PropertyValueFactory<>("state"));
    }
    
    private void SetupTableItems(){
        tournamentTable.setItems(availableTourneys);
    }
    
    private void LoadSportFilterList(){
        if(AppContext.getInstance().get("sports")==null){
            AppContext.getInstance().set("sports", new ArrayList<Sport>());
        }
        @SuppressWarnings("unchecked")
        List<Sport>sports=(List<Sport>) AppContext.getInstance().get("sports");
        if(sports.isEmpty()){
            File sportFile = new File("Sport.txt");
            if(sportFile.exists() && sportFile.length() > 0){
                 try {
                    sports.addAll(fileManeger.deserialization("Sport", Sport.class));
                  } catch (Exception e) {
                    message.show(Alert.AlertType.ERROR, "Error al Cargar Deportes", "No se pudieron cargar los deportes: " + e.getMessage());
            }
        }
            sportList.addAll(sports);
            sportFilter.setItems(FXCollections.observableArrayList(sportList));
         }
    }
 
    
    @Override
    public void initialize() {
    }
    
    }



