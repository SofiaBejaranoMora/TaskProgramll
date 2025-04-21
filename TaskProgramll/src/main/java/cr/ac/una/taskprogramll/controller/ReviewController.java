/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.taskprogramll.controller;

import cr.ac.una.taskprogramll.model.FileManager;
import cr.ac.una.taskprogramll.model.Review;
import cr.ac.una.taskprogramll.model.Sport;
import cr.ac.una.taskprogramll.model.Team;
import cr.ac.una.taskprogramll.model.Tourney;
import cr.ac.una.taskprogramll.util.AppContext;
import cr.ac.una.taskprogramll.util.Mensaje;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXSlider;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author sofia
 */
public class ReviewController extends Controller implements Initializable {

    private List<Sport> sportList = new ArrayList<>();
    private List<Team> teamList = new ArrayList<>();
    private List<Team> teamOriginalList = new ArrayList<>();
    private FileManager fileManeger = new FileManager();
    private Team selectedTeam;
    private File file;
    private Image image;
    private Mensaje message = new Mensaje();
    private List<Review> reviewList = new ArrayList<>();
    private String rute = "file:" + System.getProperty("user.dir") + "/src/main/resources/cr/ac/una/taskprogramll/resources/s";

    @FXML
    private MFXTextField txtNameSearch;
    @FXML
    private MFXComboBox<Sport> cmbSportSearch;
    @FXML
    private TableView<Team> tbvTeams;
    @FXML
    private TableColumn<Team, Team> tclTeam;
    @FXML
    private TableColumn<Team, String> tclTeamSportType;
    @FXML
    private MFXButton btnCancel;
    @FXML
    private TextArea txtReview;
    @FXML
    private MFXSlider sldTeamReviewScore;
    @FXML
    private MFXButton btnAccept;
    @FXML
    private Tab tabReview;
    @FXML
    private TabPane tabPane;
    @FXML
    private ImageView imgTeamScore;
    @FXML
    private Label labNumberReview;
    @FXML
    private Label labReviewTeamName;
    @FXML
    private VBox vboxReview;
    @FXML
    private ScrollPane scrollPane;

    @FXML
    private void OnKeyReleasedNameSearch(KeyEvent event) {
        teamList = FilterTeamName();
        TableInitialize();
    }

    @FXML
    private void OnActionCmbSportSearch(ActionEvent event) {
        teamList = ListLeakedTeams();
        TableInitialize();
    }

    @FXML
    private void OnActionBtnCancel(ActionEvent event) {
        Clean();
    }

    @FXML
    private void OnActionBtnAccept(ActionEvent event) {
        if((!txtReview.getText().isBlank())){
        reviewList.add(new Review (txtReview.getText(),(float) sldTeamReviewScore.getValue()));
        selectedTeam.setReviewList(reviewList);
        fileManeger.serialization(teamOriginalList, "Team");
        txtReview.setText("");
        InitializeTabPane();
        }
        else{
              message.show(Alert.AlertType.WARNING, "Alerta", "No se ha escrito ninguna reseña");
        }
    }

    @FXML
    private void OnMouseClikedTbvTeam(MouseEvent event) {
        selectedTeam = tbvTeams.getSelectionModel().getSelectedItem();
        if (selectedTeam != null) {
            if (tabReview.isDisable()) {
                tabReview.setDisable(false);
            }
            txtReview.setText("");
            InitializeTabPane();
        }
    }

    public void LoadTeamReviewList() {
         if ((reviewList !=null) && (!reviewList.isEmpty())) {
            for (Review currentReview : reviewList) {
                UploadReview(currentReview);
            }
         }
    }

    public void UploadReview(Review review) {
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.TOP_LEFT);
        vbox.setSpacing(15);
        VBox.setMargin(vbox, new Insets(15));
        vbox.getStyleClass().add("boxRose");
        
        image = new Image(rute +  String.format("%.1f",review.getScore()) + ".png");
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(182);
        imageView.setFitHeight(40);
        imageView.setPreserveRatio(true);

        TextArea textArea = new TextArea(review.getReviewText());
        textArea.setWrapText(true);
        textArea.setDisable(true);

        textArea.setPrefWidth(200);
        textArea.setPrefHeight(80);

        vbox.getChildren().addAll(imageView, textArea);
        vbox.prefWidthProperty().bind(vboxReview.widthProperty().multiply(0.98));
        vboxReview.getChildren().addAll(vbox);
    }

    public List<Team> FilterTeamName() {
        List<Team> resultList = new ArrayList<>();
        List<Team> list = ListLeakedTeams();
        String nameSearch = txtNameSearch.getText().trim().toUpperCase();
        for (Team currentTeam : list) {
            if (currentTeam.getName().toUpperCase().startsWith(nameSearch)) {
                resultList.add(currentTeam);
            }
        }
        return resultList;
    }

    public List<Team> ListLeakedTeams() {
        if ((cmbSportSearch.getValue() != null) && (!"Todos".equals(cmbSportSearch.getValue().getName()))) {
            List<Team> result = new ArrayList<>();
            List<Team> list = teamOriginalList;
            for (Team currentTeam : list) {
                if (currentTeam.getIdSportType() == cmbSportSearch.getValue().getId()) {
                    result.add(currentTeam);
                }
            }
            return result;
        } else {
            return teamOriginalList;
        }
    }

    public void InitializeTabPane() {
        vboxReview.prefWidthProperty().bind(scrollPane.widthProperty().multiply(0.98));
        reviewList = selectedTeam.getReviewList();
        labReviewTeamName.setText("Equipo: "+selectedTeam.getName());
        labNumberReview.setText("Cantidad de Reseñas: "+String.valueOf(reviewList.size()));
        float lala=selectedTeam.AverageGrade();
        image = new Image(rute + String.format("%.1f",selectedTeam.AverageGrade())+ ".png");
        imgTeamScore.setImage(image);
        tabPane.getSelectionModel().select(tabReview);
        vboxReview.getChildren().clear();
        LoadTeamReviewList();
    }

    public void InitializeComboxSportType() {
        ObservableList<Sport> items = FXCollections.observableArrayList(sportList);
        items.add(new Sport("Todos", 0));
        cmbSportSearch.setItems(items);
    }

    public void TableInitialize() {
        ObservableList<Team> teamObservableList = FXCollections.observableArrayList(teamList);
        tbvTeams.setItems(teamObservableList);
        tclTeam.setCellValueFactory(new PropertyValueFactory<>("Name"));
        tclTeamSportType.setCellValueFactory(cellData -> {
            Sport sport = cellData.getValue().searchSportType();
            return new SimpleStringProperty(sport.toString());
        });
    }

    public void InitializeController() {
        tabReview.setDisable(true);
        sldTeamReviewScore.valueProperty().addListener((observable, oldValue, newValue) -> {
            double roundedValue = Math.round(newValue.doubleValue() / 0.5) * 0.5;
            sldTeamReviewScore.setValue(roundedValue);
        });
        file = new File("Sport.txt");
        if ((file.exists()) && (file.length() > 0)) {
            sportList = fileManeger.deserialization("Sport", Sport.class);
            InitializeComboxSportType();
        }
        file = new File("Team.txt");
        if ((file.exists()) && (file.length() > 0)) {
            teamOriginalList = fileManeger.deserialization("Team", Team.class);
            teamList = teamOriginalList;
        }
        TableInitialize();
    }

    public void Clean() {
        txtNameSearch.setText("");
        cmbSportSearch.getSelectionModel().clearSelection();
        cmbSportSearch.setValue(null);
        txtReview.setText(" ");
        vboxReview.getChildren().clear();
        tabReview.setDisable(true);
        sldTeamReviewScore.setValue(sldTeamReviewScore.getMin());
        TableInitialize();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        InitializeController();
    }

    @Override
    public void initialize() {
        InitializeController();
    }

}
