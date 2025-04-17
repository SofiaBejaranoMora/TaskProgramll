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
import javafx.beans.property.ReadOnlyObjectWrapper;
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
    private TableColumn<Tourney, Integer> minutesColumn;
    @FXML
    private TableColumn<Tourney, Sport> sportColumn;
    @FXML
    private TableColumn<Tourney, String> stateColumn;
      @FXML
    private TableColumn<Tourney, String> winnerColumn;
    
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
      @FXML
    private HBox lobbyIcon;
    @FXML
    private MFXButton btnRefresh;
  

    private ObservableList<Team> availableTeams=FXCollections.observableArrayList();
    private ObservableList <Tourney> availableTourneys=FXCollections.observableArrayList();
    private final List<Sport> sportList = new ArrayList<>();
    private FileManager fileManeger=new FileManager();
    private final Mensaje message = new Mensaje();
    
    

  
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        SetupTableColumns();
        SetupTableItems();
        LoadSportFilterList();
        LoadTourneyList();
        setupSportFilterListener();
    }    
    
  private void SetupSportColumns(){
       sportColumn.setCellValueFactory(cellData -> {
        Tourney tourney = cellData.getValue();
        if (tourney != null) {
            // Retorna el resultado del método searchSportType encapsulado en un ReadOnlyObjectWrapper
            return new ReadOnlyObjectWrapper<>(tourney.searchSportType());
        } else {
            // Si 'tourney' es nulo, devuelve un ReadOnlyObjectWrapper con valor null
            return new ReadOnlyObjectWrapper<>(null);
        }
    });
  }
  private void SetupStateColumns(){
      stateColumn.setCellValueFactory(cellData->{
          Tourney tourney=cellData.getValue();
          if(tourney!=null){
              return new ReadOnlyObjectWrapper<>(tourney.returnState());
          }
          return new ReadOnlyObjectWrapper<>(null);
      });
  }
  
 private void SetupWinnnerColumn() {
    winnerColumn.setCellValueFactory(cellData -> {
        Tourney tourney = cellData.getValue();
        if (tourney != null) {
            if ("Finalizado".equals(tourney.returnState())) {
                // Verificar si la lista de perdedores no es nula ni está vacía
                if (tourney.getLoosersList() != null && !tourney.getLoosersList().isEmpty()) {
                    String winner = tourney.getLoosersList().getFirst().toString();
                    return new ReadOnlyObjectWrapper<>(winner); 
                } else {
                    return new ReadOnlyObjectWrapper<>("Sin ganador");
                }
            } else {
                return new ReadOnlyObjectWrapper<>("Pendiente"); 
            }
        }
        return new ReadOnlyObjectWrapper<>(null);
    });
}
private void SetupTableColumns() {
    nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    minutesColumn.setCellValueFactory(new PropertyValueFactory<>("time"));
    SetupWinnnerColumn();
   SetupStateColumns();
   SetupSportColumns();
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
 
    private void LoadTourneyList(){
        if(AppContext.getInstance().get("tourneys")==null){
            AppContext.getInstance().set("tourneys", new ArrayList<Tourney>());
        }
        @SuppressWarnings("unchecked")
        List<Tourney>tourneys=(List<Tourney>)AppContext.getInstance().get("tourneys");
        if(tourneys.isEmpty()){
            File tourneyFile=new File("Tourney.txt");
            if(tourneyFile.exists()&&tourneyFile.length()>0){
                try{
                    tourneys.addAll(fileManeger.deserialization("Tourney", Tourney.class));
                }catch(Exception e){
                    message.show(Alert.AlertType.ERROR, "Error al Cargar Torneos", "No se pudieron cargar los torneos: " + e.getMessage());
                }
            }
        }
        availableTourneys.addAll(tourneys);
        tournamentTable.setItems(FXCollections.observableArrayList(availableTourneys));
    }
    
    private void setupSportFilterListener(){
        sportFilter.getSelectionModel().selectedItemProperty().addListener((obs, oldSport, newSport)->{
        if(newSport!=null){
            filterTournamentsBySport(newSport);
        }else{
            tournamentTable.setItems(availableTourneys);
        }
        });
    }
    
   private void filterTournamentsBySport(Sport sport){
        ObservableList<Tourney> filteredTourneys=FXCollections.observableArrayList();
        for(Tourney tourney:availableTourneys){
            if(tourney.getSportTypeId()==sport.getId()){
                filteredTourneys.add(tourney);
            }
        }
        tournamentTable.setItems(filteredTourneys);
    }
   
   void showTourneyInfo(Tourney tourney){
       
   }
   
   void tourneySelectionListener(){
       tournamentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldTourney, newTourney)->{
           if(newTourney!=null){
               showTourneyInfo(newTourney);
           }
            //sino, no mostrar nada
       });
       
   }
    @Override
    public void initialize() {
    }
    
    }



