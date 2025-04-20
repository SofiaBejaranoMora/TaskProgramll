package cr.ac.una.taskprogramll.controller;

import cr.ac.una.taskprogramll.model.FileManager;
import cr.ac.una.taskprogramll.model.Team;
import cr.ac.una.taskprogramll.model.Tourney;
import cr.ac.una.taskprogramll.util.AppContext;
import cr.ac.una.taskprogramll.util.FlowController;
import cr.ac.una.taskprogramll.util.ResourceUtil;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.util.Duration;

/**  * * FXML Controller class * * @author ashly  */
public class MatchTeamsController extends Controller implements Initializable {

    private Tourney currentTourney;
    private List<Tourney> tourneyList;
    private List<Team> currentTeamList;
    private FileManager fileManager = new FileManager();
    private File file;
    private Boolean viewButton = false;
    private int index = 0;
    private int currentRound = 1;
    private int globalSize;
    private ObservableList<Team> round1 = FXCollections.observableArrayList();
    private ObservableList<Team> round2 = FXCollections.observableArrayList();
    private ObservableList<Team> round3 = FXCollections.observableArrayList();
    private ObservableList<Team> round4 = FXCollections.observableArrayList();
    private ObservableList<Team> round5 = FXCollections.observableArrayList();
    private ObservableList<Team> round6 = FXCollections.observableArrayList();
    private ObservableList<Team> winner = FXCollections.observableArrayList();

    @FXML
    private TableView<Team> tblPlayersTable;
    @FXML
    private TableColumn<Team, String> clmnRound1;
    @FXML
    private TableColumn<Team, String> clmnRound2;
    @FXML
    private TableColumn<Team, String> clmnRound3;
    @FXML
    private TableColumn<Team, String> clmnRound4;
    @FXML
    private TableColumn<Team, String> clmnRound5;
    @FXML
    private TableColumn<Team, String> clmnRound6;
    @FXML
    private TableColumn<Team, String> clmnFinal;
    @FXML
    private MFXButton btnBack;
    @FXML
    private MFXButton btnStart;
    @FXML
    private MFXButton btnCetificate;
    @FXML
    private StackPane stpWinnerTourney;
    @FXML
    private ImageView mgvWinnerStar;
    @FXML
    private Label lblWinnerName;
    @FXML
    private ImageView mgvWinnerImage;
    @FXML
    private Label lblWinnerPoints;
    @FXML
    private Label lblWinnerGoals;

    @FXML
    private void onActionBtnBack(ActionEvent event) {
        saveData();
        FlowController.getInstance().goViewInStage("ViewTourneys", (Stage) btnBack.getScene().getWindow());
    }

    @FXML
    private void onActionBtnStart(ActionEvent event) {
        if (viewButton) {
            btnBack.setVisible(false);
            viewButton = false;
        }
        AppContext.getInstance().set("CurrentTourney", currentTourney);
        GameController controller = (GameController) FlowController.getInstance().getController("Game");
        if (currentRound % 2 != 0) {
            controller.InitializeGame(currentTeamList.get(index), currentTeamList.get(index + 1));
        } else {
            controller.InitializeGame(currentTeamList.get(index - 1), currentTeamList.get(index));
        }
        FlowController.getInstance().goViewInStage("Game", (Stage) btnStart.getScene().getWindow());
    }
    
    @FXML
    private void onActionBtnCertificate(ActionEvent event) {
        //Desktop.getDesktop().open(new File (System.getProperty("user.dir") + "/src/main/resources/cr/ac/una/taskprogramll/resources/"+winner.get(0).getName()+"_"+currentTourney.getName()+".pdf";));
    }

    private int discoverRounds() {
        if (globalSize == 2) {
            return 1;
        } else if (globalSize >= 3 && globalSize <= 4) {
            return 2;
        } else if (globalSize >= 5 && globalSize <= 8) {
            return 3;
        } else if (globalSize >= 9 && globalSize <= 16) {
            return 4;
        } else if (globalSize >= 17 && globalSize <= 32) {
            return 5;
        } else {
            return 6;
        }
    }
    
    private void disableFilterOrSelection() {
        tblPlayersTable.setEditable(false);
        tblPlayersTable.getSelectionModel().clearSelection();
        tblPlayersTable.getSelectionModel().setCellSelectionEnabled(false);
        for (TableColumn<Team, ?> column : tblPlayersTable.getColumns()) {
            column.setSortable(false);
        }
    }

    private void applyRowStyles(TableView<Team> tableView) {
        tableView.setRowFactory(tv -> new TableRow<Team>() {
            @Override
            protected void updateItem(Team item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    String style = (getIndex() % 4 < 2) ? "-fx-background-color: #d3d3d3; -fx-border-color: red; -fx-border-width: 1px;" : "-fx-background-color: #ffffff; -fx-border-color: black; -fx-border-width: 1px;";
                    setStyle(style);
                } else {
                    setStyle("");
                }
            }
        });
    }

    private void distributionOnTable() {
        disableFilterOrSelection();
        Collections.shuffle(currentTeamList);
        currentTourney.getTeamList().clear();
        currentTourney.setTeamList(currentTeamList);
        currentTourney.getContinueGame().setItems(currentTeamList);
        round1.setAll(currentTeamList);
        clmnRound1.setCellValueFactory(new PropertyValueFactory<>("name"));
        tblPlayersTable.setItems(round1);
    }

    private void organizedRound() {
        switch (discoverRounds()) {
            case 1 -> {
                clmnRound2.setVisible(false);
                clmnRound3.setVisible(false);
                clmnRound4.setVisible(false);
                clmnRound5.setVisible(false);
                clmnRound6.setVisible(false);
            }
            case 2 -> {
                clmnRound3.setVisible(false);
                clmnRound4.setVisible(false);
                clmnRound5.setVisible(false);
                clmnRound6.setVisible(false);
            }
            case 3 -> {
                clmnRound4.setVisible(false);
                clmnRound5.setVisible(false);
                clmnRound6.setVisible(false);
            }
            case 4 -> {
                clmnRound5.setVisible(false);
                clmnRound6.setVisible(false);
            }
            case 5 ->
                clmnRound6.setVisible(false);
            case 6 ->
                System.out.println("\n\nHola profe, saque esto con un excel, me pareció divertido!\n");
        }
    }

    private void verifyRound() {
        if(currentRound % 2 !=0) {
            index += 2;
            if (index == currentTeamList.size()) {
                currentRound++;
                btnBack.setVisible(true);
                viewButton = true;
                currentTeamList = currentTourney.getTeamList();
                index = currentTeamList.size() - 1;
            } else if (index == currentTeamList.size() - 1){
                index --;
                adjustingTable(currentTeamList.get(index + 1));
            }
        } else {
            index -= 2;
            if (index == -1) {
                currentRound++;
                btnBack.setVisible(true);
                viewButton = true;
                currentTeamList = currentTourney.getTeamList();
                index = 0;
            } else if (index == currentTeamList.size() + 1) {
                index ++;
                adjustingTable(currentTeamList.get(index - 1));
            }
        }
    }
    
    public void adjustingTable(Team winnerTeam) {
        if (currentTeamList.size() == 2) {
            currentRound = 6;
        }
        switch (currentRound) {
            case 1 -> {
                round2.add(winnerTeam);
                currentTourney.getContinueGame().addToRound(winnerTeam, currentRound);
                System.out.println("Avanzó el equipo " + winnerTeam.getName() + " a la siguiente ronda.");
                clmnRound2.setCellFactory(column -> new TableCell<Team, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getIndex() >= round2.size()) {
                            setText(null);
                        } else {
                            setText(round2.get(getIndex()).getName());
                        }
                    }
                });
                tblPlayersTable.refresh();
                verifyRound();
            }
            case 2 -> {
                round3.add(winnerTeam);
                currentTourney.getContinueGame().addToRound(winnerTeam, currentRound);
                System.out.println("Avanzó el equipo " + winnerTeam.getName() + " a la siguiente ronda.");
                clmnRound3.setCellFactory(column -> new TableCell<Team, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getIndex() >= round3.size()) {
                            setText(null);
                        } else {
                            setText(round3.get(getIndex()).getName());
                        }
                    }
                });
                tblPlayersTable.refresh();
                verifyRound();
            }
            case 3 -> {
                round4.add(winnerTeam);
                currentTourney.getContinueGame().addToRound(winnerTeam, currentRound);
                System.out.println("Avanzó el equipo " + winnerTeam.getName() + " a la siguiente ronda.");
                clmnRound4.setCellFactory(column -> new TableCell<Team, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getIndex() >= round4.size()) {
                            setText(null); 
                        } else {
                            setText(round4.get(getIndex()).getName());
                        }
                    }
                });
                tblPlayersTable.refresh();
                verifyRound();
            }
            case 4 -> {
                round5.add(winnerTeam);
                currentTourney.getContinueGame().addToRound(winnerTeam, currentRound);
                System.out.println("Avanzó el equipo " + winnerTeam.getName() + " a la siguiente ronda.");
                clmnRound5.setCellFactory(column -> new TableCell<Team, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getIndex() >= round5.size()) {
                            setText(null);
                        } else {
                            setText(round5.get(getIndex()).getName());
                        }
                    }
                });
                tblPlayersTable.refresh();
                verifyRound();
            }
            case 5 -> {
                round6.add(winnerTeam);
                currentTourney.getContinueGame().addToRound(winnerTeam, currentRound); 
                System.out.println("Avanzó el equipo " + winnerTeam.getName() + " a la siguiente ronda.");
                clmnRound6.setCellFactory(column -> new TableCell<Team, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getIndex() >= round6.size()) {
                            setText(null);
                        } else {
                            setText(round6.get(getIndex()).getName());
                        }
                    }
                });
                tblPlayersTable.refresh();
                verifyRound();
            }
            case 6 -> {
                winner.add(winnerTeam);
                currentTourney.getContinueGame().addToRound(winnerTeam, currentRound);
                System.out.println("El ganador del torneo es " + winnerTeam.getName());
                clmnFinal.setCellFactory(column -> new TableCell<Team, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getIndex() >= winner.size()) {
                            setText(null);
                        } else {
                            setText(winner.get(getIndex()).getName());
                        }
                    }
                });
                tblPlayersTable.refresh();
                currentTourney.moveTeamToLoosers(winnerTeam);
                btnCetificate.setVisible(true);
                btnBack.setVisible(true);
                winnerAnimatic(winnerTeam);
            }
            default ->
                throw new AssertionError("Ronda inválida: " + currentRound);
        }
    }

        private void winnerAnimatic(ImageView starWinner) {
        Scale scale = new Scale();
        starWinner.getTransforms().add(scale);
        Timeline timeline = new Timeline();
        KeyFrame increase = new KeyFrame(Duration.millis(500), new KeyValue(scale.xProperty(), 1.5), new KeyValue(scale.yProperty(), 1.5));
        KeyFrame reduce = new KeyFrame(Duration.millis(1000), new KeyValue(scale.xProperty(), 1.0), new KeyValue(scale.yProperty(), 1.0));
        timeline.getKeyFrames().addAll(increase, reduce);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
    
    private void winnerAnimatic(Team winnerTeam) {
        btnStart.setVisible(false);
        btnStart.setManaged(false);
        stpWinnerTourney.setVisible(true);
        mgvWinnerImage.setImage(new Image(ResourceUtil.getImagePath(winnerTeam.getId())));
        winnerAnimatic(mgvWinnerStar);
        lblWinnerName.setText(winnerTeam.getName());
        lblWinnerPoints.setText("Puntos obtenidos: " + winnerTeam.getPoints());
        lblWinnerGoals.setText("Goles realizados: " + winnerTeam.getGoals());
    }
    
    private void saveData() {
        stpWinnerTourney.setVisible(false);
        AppContext.getInstance().set("SelectedTourney", null);
        currentTourney.getContinueGame().setCurrentRound(currentRound);
        if (currentRound % 2 != 0) { 
            currentTourney.getContinueGame().setContinueIdFTeam(currentTeamList.get(index).getId());
            currentTourney.getContinueGame().setContinueIdSTeam(currentTeamList.get(index + 1).getId());
            currentTourney.getContinueGame().setContinueIndexTeam(index);    
        }
        else {
            currentTourney.getContinueGame().setContinueIdFTeam(currentTeamList.get(index).getId());
            currentTourney.getContinueGame().setContinueIdSTeam(currentTeamList.get(index - 1).getId());
            currentTourney.getContinueGame().setContinueIndexTeam(index);
        }
        fileManager.serialization(tourneyList, "Tourney");
    }
    
    private void printCertificate(){
        
    }
    
    private void startGameParameters() {
        globalSize = currentTeamList.size();
        currentTourney.getContinueGame().setGlobalSize(globalSize);
        organizedRound();
        distributionOnTable();
    }

    private void loadColumns() {
        round1.setAll(currentTourney.getContinueGame().getRound1());
        clmnRound1.setCellFactory(column -> new TableCell<Team, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= round1.size()) {
                    setText(null);
                } else {
                    setText(round1.get(getIndex()).getName());
                }
            }
        });
        round2.setAll(currentTourney.getContinueGame().getRound2());
        clmnRound2.setCellFactory(column -> new TableCell<Team, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= round2.size()) {
                    setText(null);
                } else {
                    setText(round2.get(getIndex()).getName());
                }
            }
        });
        round3.setAll(currentTourney.getContinueGame().getRound3());
        clmnRound3.setCellFactory(column -> new TableCell<Team, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= round3.size()) {
                    setText(null);
                } else {
                    setText(round3.get(getIndex()).getName());
                }
            }
        });
        round4.setAll(currentTourney.getContinueGame().getRound4());
        clmnRound4.setCellFactory(column -> new TableCell<Team, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= round4.size()) {
                    setText(null);
                } else {
                    setText(round4.get(getIndex()).getName());
                }
            }
        });
        round5.setAll(currentTourney.getContinueGame().getRound5());
        clmnRound5.setCellFactory(column -> new TableCell<Team, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= round5.size()) {
                    setText(null);
                } else {
                    setText(round5.get(getIndex()).getName());
                }
            }
        });
        round6.setAll(currentTourney.getContinueGame().getRound6());
        clmnRound6.setCellFactory(column -> new TableCell<Team, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= round6.size()) {
                    setText(null);
                } else {
                    setText(round6.get(getIndex()).getName());
                }
            }
        });
        winner.setAll(currentTourney.getContinueGame().getWinner());
        clmnFinal.setCellFactory(column -> new TableCell<Team, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= winner.size()) {
                    setText(null);
                } else {
                    setText(winner.get(getIndex()).getName());
                }
            }
        });
    }
    
    private void continueGameParameters() {
        organizedRound();        
        loadColumns();
        index = currentTourney.getContinueGame().getContinueIndexTeam();
        currentRound = currentTourney.getContinueGame().getCurrentRound();
        if (currentTeamList.get(index).getId() != currentTourney.getContinueGame().getContinueIdFTeam()) {
            if (currentRound % 2 != 0) adjustingTable(currentTeamList.get(index + 1));
            else adjustingTable(currentTeamList.get(index - 1));
        } else {
            if (currentRound % 2 != 0) {
                if (currentTeamList.get(index + 1).getId() != currentTourney.getContinueGame().getContinueIdSTeam()) {
                    adjustingTable(currentTeamList.get(index));
                }
            } else {
                if (currentTeamList.get(index - 1).getId() != currentTourney.getContinueGame().getContinueIdSTeam()) {
                    adjustingTable(currentTeamList.get(index));
                } 
            }
        }
    }

    private void viewGameTable() {
        loadColumns();
        btnStart.setManaged(false);
        btnStart.setVisible(false);
        btnBack.setVisible(true);
    }

    public Tourney searchTourney(Tourney selectedTourney) {
        for(Tourney tourney:tourneyList){
            if (tourney.getId() == selectedTourney.getId())
                return tourney;
        }
        return null;
    }
    
    public void initializeFromAppContext() {
        btnBack.setVisible(false);
        this.currentTourney = searchTourney((Tourney) AppContext.getInstance().get("SelectedTourney"));
        this.currentTeamList = currentTourney.getTeamList();
        switch (currentTourney.returnState()) {
            case "Sin Empezar" ->
                startGameParameters();
            case "En Proceso" ->
                continueGameParameters();
            case "Finalizado" ->
                viewGameTable();
            default ->
                continueGameParameters();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        applyRowStyles(tblPlayersTable);
        file = new File("Tourney.txt");
        if ((file.exists()) && (file.length() > 0)) {
            tourneyList = fileManager.deserialization("Tourney", Tourney.class);
        }
    }

    @Override
    public void initialize() {}

}
