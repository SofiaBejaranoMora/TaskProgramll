package cr.ac.una.taskprogramll.controller;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import cr.ac.una.taskprogramll.model.FileManager;
import cr.ac.una.taskprogramll.model.Team;
import cr.ac.una.taskprogramll.model.Tourney;
import cr.ac.una.taskprogramll.util.AppContext;
import cr.ac.una.taskprogramll.util.FlowController;
import cr.ac.una.taskprogramll.util.Mensaje;
import cr.ac.una.taskprogramll.util.ImagesUtil;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.util.Duration;

/**  * * FXML Controller class * * @author ashly  */
public class MatchTeamsController extends Controller implements Initializable {

    private Tourney currentTourney;
    private List<Tourney> tourneyList;
    private List<Team> currentTeamList;
    private Team winnerCertificate;
    private FileManager fileManager = new FileManager();
    private File file;
    private Mensaje message;
    private Boolean viewButton = false;
    private Boolean isFinished = false;
    private int index = 0;
    private int currentRound = 1;
    private int globalSize;
    private String rute=System.getProperty("user.dir")+"/src/main/resources/cr/ac/una/taskprogramll/resources/Certificates/";
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
    private AnchorPane ncpRoot;

    @FXML
    private void onActionBtnBack(ActionEvent event) {
        saveData();
        AppContext.getInstance().delete("SelectedTourney");
        ViewTourneysController controller = (ViewTourneysController) FlowController.getInstance().getController("ViewTourneys");
        controller.initialPanelConditions();
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
        try {
            Desktop.getDesktop().open(new File(rute + winnerCertificate.getName() + "_" + currentTourney.getName() + ".pdf"));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al abrir el certificado: " + e.getMessage());
        }
    }
    
    public void Certificate(Team winner, String tourney){
        String address=rute+winner.getName()+"_"+tourney+".pdf";
        String backgroundImagePath=rute+"/CertificateImage.png";
        String addresTeamPhotograsphs=System.getProperty("user.dir")+"/src/main/resources/cr/ac/una/taskprogramll/resources/"+winner.getId()+".png";
        Document document;
        try{
            PdfWriter writePdf=  new PdfWriter(address);
            PdfDocument pdf= new PdfDocument(writePdf);
            PageSize pdfSize= new PageSize(549, 303);
            
            pdf.setDefaultPageSize(pdfSize);
            document=new Document(pdf);
            
            ImageData backgroundImage=ImageDataFactory.create(backgroundImagePath);
            PdfCanvas canva= new PdfCanvas(pdf.addNewPage());
            canva.addImage(backgroundImage, 0,0,pdfSize.getWidth(), false);
            
            ImageData teamData =ImageDataFactory.create(addresTeamPhotograsphs);
            canva.addImage(teamData,new Rectangle(420, 100, 115, 91), false);
            
            Paragraph paragraphName=new Paragraph("Nombre del equipo ganador en " + tourney + ": " + winner.getName())
                .setFontSize(14)
                .setMarginTop(97)
                .setMarginLeft(35);
            
            Paragraph paragraphSport=new Paragraph("Deporte del torneo: " + currentTourney.getSportTypeId())
                .setFontSize(14)
                .setMarginTop(6)
                .setMarginLeft(35);
            
            Paragraph paragraphPoints=new Paragraph("Puntos obtenidos en este torneo: " + winner.getPoints())
                .setFontSize(14)
                .setMarginTop(6)
                .setMarginLeft(35);
            
            document.add(paragraphName);
            document.add(paragraphSport);
            document.add(paragraphPoints);
            
            document.close();
        }
        catch(IOException e){
          System.out.println("Error:"+e);
        }
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

    private void distributionOnTable() {
        disableFilterOrSelection();
        Collections.shuffle(currentTeamList);
        currentTourney.getTeamList().clear();
        currentTourney.setTeamList(currentTeamList);
        currentTourney.setItems(currentTeamList);
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
        int lastRound = currentRound;
        if (currentTeamList.size() == 2) {
            currentRound = 6;
        }
        switch (currentRound) {
            case 1 -> {
                round2.add(winnerTeam);
                currentTourney.addToRound(winnerTeam, currentRound);
                System.out.println("Avanzó el equipo " + winnerTeam.getName() + " a la siguiente ronda.");
                clmnRound2.setCellFactory(column -> new TableCell<Team, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getIndex() >= round2.size()) {
                            setText(null);
                        } else {
                            setText(round2.get(getIndex()).getName() + " " + winnerTeam.getGoals() + " goles");
                            drawLine(winnerTeam.getName(), clmnRound1, clmnRound2);
                        }
                    }
                });
               tblPlayersTable.refresh();
                verifyRound();
            }
            case 2 -> {
                round3.add(winnerTeam);
                currentTourney.addToRound(winnerTeam, currentRound);
                System.out.println("Avanzó el equipo " + winnerTeam.getName() + " a la siguiente ronda.");
                clmnRound3.setCellFactory(column -> new TableCell<Team, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getIndex() >= round3.size()) {
                            setText(null);
                        } else {
                            setText(round3.get(getIndex()).getName() + " " + winnerTeam.getGoals() + " goles");
                            drawLine(winnerTeam.getName(), clmnRound2, clmnRound3);
                        }
                    }
                });
                tblPlayersTable.refresh();
                verifyRound();
            }
            case 3 -> {
                round4.add(winnerTeam);
                currentTourney.addToRound(winnerTeam, currentRound);
                System.out.println("Avanzó el equipo " + winnerTeam.getName() + " a la siguiente ronda.");
                clmnRound4.setCellFactory(column -> new TableCell<Team, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getIndex() >= round4.size()) {
                            setText(null); 
                        } else {
                            setText(round4.get(getIndex()).getName() + " " + winnerTeam.getGoals() + " goles");
                            drawLine(winnerTeam.getName(), clmnRound3, clmnRound4);
                        }
                    }
                });
                tblPlayersTable.refresh();
                verifyRound();
            }
            case 4 -> {
                round5.add(winnerTeam);
                currentTourney.addToRound(winnerTeam, currentRound);
                System.out.println("Avanzó el equipo " + winnerTeam.getName() + " a la siguiente ronda.");
                clmnRound5.setCellFactory(column -> new TableCell<Team, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getIndex() >= round5.size()) {
                            setText(null);
                        } else {
                            setText(round5.get(getIndex()).getName() + " " + winnerTeam.getGoals() + " goles");
                            drawLine(winnerTeam.getName(), clmnRound4, clmnRound5);
                        }
                    }
                });
                tblPlayersTable.refresh();
                verifyRound();
            }
            case 5 -> {
                round6.add(winnerTeam);
                currentTourney.addToRound(winnerTeam, currentRound); 
                System.out.println("Avanzó el equipo " + winnerTeam.getName() + " a la siguiente ronda.");
                clmnRound6.setCellFactory(column -> new TableCell<Team, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getIndex() >= round6.size()) {
                            setText(null);
                        } else {
                            setText(round6.get(getIndex()).getName() + " " + winnerTeam.getGoals() +" goles");
                            drawLine(winnerTeam.getName(), clmnRound5, clmnRound6);
                        }
                    }
                });
                tblPlayersTable.refresh();
                verifyRound();
            }
            case 6 -> {
                winner.add(winnerTeam);
                currentTourney.addToRound(winnerTeam, currentRound);
                currentTourney.moveTeamToLoosers(winnerTeam); 
                currentTourney.setTeamList(new ArrayList<>()); 
                System.out.println("El ganador del torneo es " + winnerTeam.getName());
                clmnFinal.setCellFactory(column -> new TableCell<Team, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getIndex() >= winner.size()) {
                            setText(null);
                        } else {
                            setText(winner.get(getIndex()).getName()  + " " + winnerTeam.getGoals() + " goles");
                            drawLine(winnerTeam.getName(), discoverColumn(lastRound - 1), clmnFinal);
                        }
                    }
                });
                tblPlayersTable.refresh();
                btnCetificate.setVisible(true);
                btnBack.setVisible(true);
                isFinished = true;
                winnerAnimatic(winnerTeam);
            }
            default -> throw new AssertionError("Ronda inválida: " + currentRound);
        }
    }

    private void drawLine(String teamName, TableColumn<?, ?> from, TableColumn<?, ?> to) {
        Platform.runLater(() -> {
            TableCell<?, ?> cell1 = getTableCellByText(teamName, from);
            TableCell<?, ?> cell2 = getTableCellByText(teamName, to);

            if (cell1 == null || cell2 == null) return;
            Line line = new Line();
            line.setStroke(Color.rgb(252, 129, 54));
            line.setStrokeWidth(2);

            line.startXProperty().bind(Bindings.createDoubleBinding(() -> {
                Point2D startPointX = cell1.localToScene(cell1.getWidth() * 0.8, cell1.getHeight() / 2);
                return ncpRoot.sceneToLocal(startPointX).getX();
            }, cell1.layoutXProperty(), cell1.widthProperty(), cell1.heightProperty(), cell1.localToSceneTransformProperty()));
            line.startYProperty().bind(Bindings.createDoubleBinding(() -> {
                Point2D startPointY = cell1.localToScene(cell1.getWidth() * 0.8, cell1.getHeight() / 2);
                return ncpRoot.sceneToLocal(startPointY).getY();
            }, cell1.layoutYProperty(), cell1.widthProperty(), cell1.heightProperty(), cell1.localToSceneTransformProperty()));
            line.endXProperty().bind(Bindings.createDoubleBinding(() -> {
                Point2D finishPointX = cell2.localToScene(cell2.getWidth() * 0.2, cell2.getHeight() / 2);
                return ncpRoot.sceneToLocal(finishPointX).getX();
            }, cell2.layoutXProperty(), cell2.widthProperty(), cell2.heightProperty(), cell2.localToSceneTransformProperty()));
            line.endYProperty().bind(Bindings.createDoubleBinding(() -> {
                Point2D finishPointY = cell2.localToScene(cell2.getWidth() * 0.2, cell2.getHeight() / 2);
                return ncpRoot.sceneToLocal(finishPointY).getY();
            }, cell2.layoutYProperty(), cell2.widthProperty(), cell2.heightProperty(), cell2.localToSceneTransformProperty()));
            ncpRoot.getChildren().add(line);
            line.toFront();
        });
    }

    private void reDrawPreviousLines() {
        for (int i = 6; i >= 1; i--) {
            List<Team> lastRound = currentTourney.getRoundsByNumber(i);
            if (lastRound.isEmpty()) continue;
            int emptySpaces = 0;
            for (int j = i - 1 ; j >= 1; j--) {
                List<Team> previousRound = currentTourney.getRoundsByNumber(j);
                if (previousRound.isEmpty()) emptySpaces++;
                else break;
            }

            for (Team team : lastRound) {
                drawLine(team.getName(), discoverColumn(i - emptySpaces - 1), discoverColumn(i));
            }
        }
    }

    private TableColumn<Team, String> discoverColumn(int currentColumn) {
        return switch (currentColumn) {
            case 0 -> clmnRound1;
            case 1 -> clmnRound2;
            case 2 -> clmnRound3;
            case 3 -> clmnRound4;
            case 4 -> clmnRound5;
            case 5 -> clmnRound6;
            case 6 -> clmnFinal;
            default -> null;
        };
    }

    private TableCell<?, ?> getTableCellByText(String teamName, TableColumn<?, ?> currentColumn) {
        for (Node n : tblPlayersTable.lookupAll(".table-row-cell")) {
            if (n instanceof TableRow<?> row) {
                for (Node cellNode : row.lookupAll(".table-cell")) {
                    if (cellNode instanceof TableCell<?, ?> cell) { 
                        if (cell.getTableColumn() == currentColumn && cell.getText().contains(teamName)) {
                            return cell;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    private void loadColumns() {
        disableFilterOrSelection();
        clmnRound1.setCellValueFactory(cellData -> {
            int index = tblPlayersTable.getItems().indexOf(cellData.getValue());
            if (index >= 0 && index < currentTourney.getRound1().size()) {
                return new SimpleStringProperty(currentTourney.getRound1().get(index).getName());
            }
            return null;
        });
        clmnRound2.setCellValueFactory(cellData -> {
            int index = cellData.getTableView().getItems().indexOf(cellData.getValue());
            if (index < currentTourney.getRound2().size()) {
                return new SimpleStringProperty(currentTourney.getRound2().get(index).getName() + " " + currentTourney.getRound2().get(index).getGoals() + " goles");
            }
            return null;
        });
        clmnRound3.setCellValueFactory(cellData -> {
            int index = cellData.getTableView().getItems().indexOf(cellData.getValue());
            if (index < currentTourney.getRound3().size()) {
                return new SimpleStringProperty(currentTourney.getRound3().get(index).getName() + " " + currentTourney.getRound3().get(index).getGoals() + " goles");
            }
            return null;
        });
        clmnRound4.setCellValueFactory(cellData -> {
            int index = cellData.getTableView().getItems().indexOf(cellData.getValue());
            if (index < currentTourney.getRound4().size()) {
                return new SimpleStringProperty(currentTourney.getRound4().get(index).getName() + " " + currentTourney.getRound4().get(index).getGoals() + " goles");
            }
            return null;
        });
        clmnRound5.setCellValueFactory(cellData -> {
            int index = cellData.getTableView().getItems().indexOf(cellData.getValue());
            if (index < currentTourney.getRound5().size()) {
                return new SimpleStringProperty(currentTourney.getRound5().get(index).getName() + " " + currentTourney.getRound5().get(index).getGoals() + " goles");
            }
            return null;
        });
        clmnRound6.setCellValueFactory(cellData -> {
            int index = cellData.getTableView().getItems().indexOf(cellData.getValue());
            if (index < currentTourney.getRound6().size()) {
                return new SimpleStringProperty(currentTourney.getRound6().get(index).getName() + " " + currentTourney.getRound6().get(index).getGoals() + " goles");
            }
            return null;
        });
        clmnFinal.setCellValueFactory(cellData -> {
            int index = cellData.getTableView().getItems().indexOf(cellData.getValue());
            if (index < currentTourney.getWinner().size()) {
                return new SimpleStringProperty(currentTourney.getWinner().get(index).getName() + " " + currentTourney.getWinner().get(index).getGoals() + " goles");
            }
            return null;
        });

        ObservableList<Team> combinedRounds = FXCollections.observableArrayList();
        combinedRounds.addAll(currentTourney.getRound1());
        tblPlayersTable.setItems(combinedRounds);
        tblPlayersTable.refresh();
        reDrawPreviousLines();
    }
    
    private void saveData() {
        stpWinnerTourney.setVisible(false);
        AppContext.getInstance().delete("SelectedTourney");

        clmnRound1.setVisible(true);
        clmnRound2.setVisible(true);
        clmnRound3.setVisible(true);
        clmnRound4.setVisible(true);
        clmnRound5.setVisible(true);
        clmnRound6.setVisible(true);
        clmnFinal.setVisible(true);
        if (!isFinished) {
            currentTourney.getContinueGame().setCurrentRound(currentRound);
            if (currentRound % 2 != 0) {
                currentTourney.getContinueGame().setContinueIdFTeam(currentTeamList.get(index).getId());
                currentTourney.getContinueGame().setContinueIdSTeam(currentTeamList.get(index + 1).getId());
                currentTourney.getContinueGame().setContinueIndexTeam(index);
                System.out.println("Datos guardados");
            } else {
                currentTourney.getContinueGame().setContinueIdFTeam(currentTeamList.get(index).getId());
                currentTourney.getContinueGame().setContinueIdSTeam(currentTeamList.get(index - 1).getId());
                currentTourney.getContinueGame().setContinueIndexTeam(index);
                System.out.println("Datos guardados");
            }
        } else {
            System.out.println("Juego terminado");
        }
        fileManager.serialization(tourneyList, "Tourney");
    }   
        
    private void winnerAnimatic(ImageView starWinner) {
        Scale scale = new Scale();
        starWinner.getTransforms().add(scale);
        Timeline timeline = new Timeline();
        KeyFrame increase = new KeyFrame(Duration.millis(500), new KeyValue(scale.xProperty(), 1.5), new KeyValue(scale.yProperty(), 1.5));
        KeyFrame reduce = new KeyFrame(Duration.millis(1000), new KeyValue(scale.xProperty(), 1.0), new KeyValue(scale.yProperty(), 1.0));
        KeyFrame firstHeartBit = new KeyFrame(Duration.millis(100), new KeyValue(scale.xProperty(), 1.3), new KeyValue(scale.yProperty(), 1.3));
        KeyFrame secondHeartBit = new KeyFrame(Duration.millis(150), new KeyValue(scale.xProperty(), 1.2), new KeyValue(scale.yProperty(), 1.2));
        timeline.getKeyFrames().addAll(increase, reduce, firstHeartBit, secondHeartBit);
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
    
    private void winnerAnimatic(Team winnerTeam) {
        btnStart.setVisible(false);
        btnStart.setManaged(false);
        stpWinnerTourney.setVisible(true);
        mgvWinnerImage.setImage(new Image(ImagesUtil.getImagePath(winnerTeam.getId())));
        winnerAnimatic(mgvWinnerStar);
        lblWinnerName.setText(winnerTeam.getName());
        lblWinnerPoints.setText("Puntos obtenidos: " + winnerTeam.getPoints());
        lblWinnerGoals.setText("Goles realizados: " + winnerTeam.getGoals());
        Certificate(winnerTeam, currentTourney.getName());
        winnerCertificate = winnerTeam;
    }
    
    private void startGameParameters() {
        globalSize = currentTeamList.size();
        currentTourney.getContinueGame().setGlobalSize(globalSize);
        organizedRound();
        distributionOnTable();
    }
    
    public void initializeFromAppContext() {
        tblPlayersTable.getItems().clear();
        tblPlayersTable.refresh();
        btnBack.setVisible(false);
        this.currentTourney = searchTourney((Tourney) AppContext.getInstance().get("SelectedTourney"));
        this.currentTeamList = currentTourney.getTeamList();
        organizedRound();
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
    
    private void viewGameTable() {
        isFinished = true;
        globalSize = currentTourney.getContinueGame().getGlobalSize();
        System.out.println(discoverRounds());
        organizedRound();
        loadColumns();
        btnStart.setManaged(false);
        btnStart.setVisible(false);
        btnBack.setVisible(true);
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

    public Tourney searchTourney(Tourney selectedTourney) {
        for(Tourney tourney:tourneyList){
            if (tourney.getId() == selectedTourney.getId())
                return tourney;
        }
        return null;
    }
 
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        file = new File("Tourney.txt");
        if ((file.exists()) && (file.length() > 0)) {
            tourneyList = fileManager.deserialization("Tourney", Tourney.class);
        }
    }

    @Override
    public void initialize() {}

}