package cr.ac.una.taskprogramll.controller;

import cr.ac.una.taskprogramll.model.FileManager;
import cr.ac.una.taskprogramll.model.Sport;
import cr.ac.una.taskprogramll.model.Team;
import cr.ac.una.taskprogramll.model.Tourney;
import cr.ac.una.taskprogramll.util.AppContext;
import cr.ac.una.taskprogramll.util.FlowController;
import cr.ac.una.taskprogramll.util.Mensaje;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.utils.SwingFXUtils;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class RegistrationModifyController extends Controller implements Initializable {

    private File selectedImage = null;
    private Sport newSport = null;
    private Team newTeam = null;
    private List<Sport> sportList = new ArrayList<>();
    private List<Team> teamList = new ArrayList<>();
    private List<Tourney> tourneyList = new ArrayList<>();
    private FileManager fileManeger = new FileManager();
    private File file;
    private Image image = null;
    private String ruteImage = System.getProperty("user.dir") + "/src/main/resources/cr/ac/una/taskprogramll/resources/";
    private Boolean isSport = false;
    private Boolean isMaintenace = false;
    private Mensaje message = new Mensaje();

    @FXML
    private MFXTextField txtName;

    @FXML
    private MFXButton btnAccept;

    @FXML
    private MFXButton btnCancel;

    @FXML
    private MFXButton btnDelete;

    @FXML
    private MFXButton btnPhoto;

    @FXML
    private MFXButton btnSelectImage;

    @FXML
    private MFXComboBox<Sport> cmbSport;

    @FXML
    private Label labTitle;

    @FXML
    private VBox hbxImage;

    @FXML
    private ImageView mgvImage;

    @FXML
    private ImageView imgOther;

    @FXML
    void OnActionAccept(ActionEvent event) {
        String name = txtName.getText().trim();
        if ((!name.isEmpty()) && (mgvImage.getImage() != null)) {
            name = txtName.getText();
            if (isSport) {
                if (((isMaintenace) && (newSport.getName().trim().toUpperCase().replaceAll("\\s+", "").equals(name.toUpperCase().replaceAll("\\s+", ""))))
                        || (!CheckedExistsSport(name))) {
                    Sport(name);
                } else {
                    message.show(Alert.AlertType.INFORMATION, "Aviso", "Ya hay un deporte registrado con el mismo nombre");
                }
            } else if ((cmbSport.getValue() != null) || (isMaintenace)) {
                Sport type;
                if (isMaintenace) {
                    type = newTeam.searchSportType();
                } else {
                    type = cmbSport.getValue();
                }
                if (((isMaintenace) && (newTeam.getName().trim().toUpperCase().replaceAll("\\s+", "").equals(name.toUpperCase().replaceAll("\\s+", ""))))
                        || ((!CheckedExistsTeam(name, type)) && (!CheckedExistsSport(name)))) {
                    Team(name, type);
                    ClearPanel();
                } else {
                    message.show(Alert.AlertType.INFORMATION, "Aviso", "Ya hay un equipo registrado con el mismo nombre o posee el nombre de un deporte");
                }
            } else {
                message.show(Alert.AlertType.WARNING, "Alerta", "No se ha seleccionado un deporte");
            }
        } else {
            message.show(Alert.AlertType.WARNING, "Alerta", "No se a registrado nombre o imagen");
        }
    }

    @FXML
    void OnActionBtnCancel(ActionEvent event) {
        ClearPanel();
        if (isMaintenace) {
            FlowController.getInstance().goView("Maintenance");
        }
    }

    @FXML
    void OnActionBtnDelete(ActionEvent event) {
        String name = "";
        if (isSport) {
            SportDelete(name);
        } else {
            TeamDelete(name);
        }
    }

    @FXML
    void OnActionBtnSelectImage(ActionEvent event) {
        SelectImage();
    }

    @FXML
    void OnActionBtnPhoto(ActionEvent event) throws IOException {
        FlowController.getInstance().goViewInWindowModal("Photography", getStage(), Boolean.FALSE);
        BufferedImage bufferedImage = (BufferedImage) AppContext.getInstance().get("bufferedImageTeam");
        if (bufferedImage != null) {
            mgvImage.setImage(SwingFXUtils.toFXImage(bufferedImage, null));
        }
    }

    @FXML
    void OnActionCmbSport(ActionEvent event) {

    }

    public void Team(String name, Sport type) {
        if (isMaintenace) {
            newTeam.setName(name);
            SaveImage(newTeam.getId());
            fileManeger.serialization(teamList, "Team");
            ClearPanel();
            FlowController.getInstance().goView("Maintenance");
        } else {
            TeamRegistration(name, type);
        }
    }

    public void Sport(String name) {
        if (isMaintenace) {
            newSport.setName(name);
            SaveImage(newSport.getId());
            fileManeger.serialization(sportList, "Sport");
            ClearPanel();
            if ((Boolean) AppContext.getInstance().get("neededTeamBoolean")) {
                Team kwdwd=(Team) AppContext.getInstance().get("neededTeam");
                AppContext.getInstance().set("selectedTeam", (Team) AppContext.getInstance().get("neededTeam"));
                AppContext.getInstance().set("isSport", false);
                AppContext.getInstance().set("isMaintenace", true);
                AppContext.getInstance().set("Title", "Mantenimiento de Equipo");
                InitializeController();
            } else {
                FlowController.getInstance().goView("Maintenance");
            }
        } else {
            SportRegistration(name);
        }
    }

    public void SportRegistration(String name) {
        newSport = new Sport(name.trim(), CreateIdSport());
        sportList.add(newSport);
        fileManeger.serialization(sportList, "Sport");
        image = mgvImage.getImage();// revisar y quitar si es necesario
        RelocateImage(newSport.getId());
        InitializeComboxSportType();
        ClearPanel();
    }

    public void TeamRegistration(String name, Sport type) {
        int id = CreateIdTeam();
        newTeam = new Team(name.trim(), type.getId(), id);
        teamList.add(newTeam);
        fileManeger.serialization(teamList, "Team");
        image = mgvImage.getImage();
        SaveImage(id);
        ClearPanel();
    }

    public void SportDelete(String name) {
        if (!HasTeam() && !HasTourney()) {
            file = new File(newSport.RuteImage());
            name = newSport.getName();
            if ((sportList.remove(newSport)) && ((!file.exists()) || (file.delete()))) {
                fileManeger.serialization(sportList, "Sport");
                message.show(Alert.AlertType.CONFIRMATION, "Confirmacion", "El deporte " + name + " se elimino con exito.");
                ClearPanel();
                FlowController.getInstance().goView("Maintenance");
            } else {
                message.show(Alert.AlertType.INFORMATION, "Confirmacion", "El deporte " + name + " no se pudo eliminar.");
            }
        } else {
            message.show(Alert.AlertType.WARNING, "Alerta", "No se puede eliminar el deporte, por que posee equipos y toneos");
        }
    }

    public void TeamDelete(String name) {
        file = new File(newTeam.RuteImage());
        name = newTeam.getName();
        if ((teamList.remove(newTeam)) && ((!file.exists()) || (file.delete()))) {
            fileManeger.serialization(teamList, "Team");
            message.show(Alert.AlertType.CONFIRMATION, "Confirmacion", "El equipo " + name + " se elimino con exito.");
            ClearPanel();
            FlowController.getInstance().goView("Maintenance");
        } else {
            message.show(Alert.AlertType.INFORMATION, "Aviso", "El equipo " + name + " no se pudo eliminar.");
        }
    }

    public Boolean HasTeam() {
        for (Team currentTeam : teamList) {
            if ((currentTeam.getIdSportType() == newSport.getId())) {
                return true;
            }
        }
        return false;
    }

    public Boolean HasTourney() {
        for (Tourney currentTeam : tourneyList) {
            if ((currentTeam.getSportTypeId() == newSport.getId())) {
                return false;
            }
        }
        return false;
    }

    public Boolean CheckedExistsTeam(String name, Sport sport) {
        name = name.toUpperCase().replaceAll("\\s+", "");
        String nameCurrentTeam;
        for (Team currentTeam : teamList) {
            nameCurrentTeam = currentTeam.getName().toUpperCase().replaceAll("\\s+", "");
            if ((currentTeam.getIdSportType() == sport.getId()) && (nameCurrentTeam.equals(name))) {
                return true;
            }
        }
        return false;
    }

    public Boolean CheckedExistsSport(String name) {
        for (Sport currentTeam : sportList) {
            if (currentTeam.getName().toUpperCase().replaceAll("\\s+", "").equals(name.toUpperCase().replaceAll("\\s+", ""))) {
                return true;
            }
        }
        return false;
    }

    public void SelectImage() {
        JFileChooser jFileChooser = new JFileChooser();
        FileNameExtensionFilter imageFilter = new FileNameExtensionFilter("PNG", "png");
        jFileChooser.setFileFilter(imageFilter);
        int responseData = jFileChooser.showOpenDialog(null);
        if (responseData == JFileChooser.APPROVE_OPTION) {
            selectedImage = jFileChooser.getSelectedFile();
            image = new Image(selectedImage.toURI().toString());
            mgvImage.setImage(image);
        }
    }

    public void RelocateImage(int id) {
        String newRute = ruteImage + id + ".png";
        try {
            Path originalSourceImage = selectedImage.toPath();
            Path newDestinationImage = new File(newRute).toPath();
            Files.copy(originalSourceImage, newDestinationImage, StandardCopyOption.REPLACE_EXISTING);
            image = new Image("file:" + newRute);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void SaveImage(int id) {
        String newRute = ruteImage + id + ".png";
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(mgvImage.getImage(), null), "PNG", new File(newRute));
        } catch (IOException e) {
        }
    }

    public <T> T FindObject(T object, List<T> list) {
        for (T item : list) {
            if (item.equals(object)) {
                return item;
            }
        }
        return null;
    }

    public int CreateIdTeam() {
        Boolean isUnique = false;
        int id = 0;
        do {
            id = ThreadLocalRandom.current().nextInt(1, 10000);
            isUnique = true;
            for (Team currentTeam : teamList) {
                if (currentTeam.getId() == id) {
                    isUnique = false;
                    break;
                }
            }
        } while (!isUnique);

        return id;
    }

    public int CreateIdSport() {
        Boolean isUnique = false;
        int id = 0;
        do {
            id = ThreadLocalRandom.current().nextInt(1, 10000);
            isUnique = true;
            for (Sport currentTeam : sportList) {
                if (currentTeam.getId() == id) {
                    isUnique = false;
                    break;
                }
            }
        } while (!isUnique);

        return id;
    }

    public void EnabledTeam(Boolean enabled) {
        cmbSport.setDisable(!enabled);
        cmbSport.setVisible(enabled);
        cmbSport.setManaged(enabled);
        btnPhoto.setDisable(!enabled);
        btnPhoto.setManaged(enabled);
        btnPhoto.setVisible(enabled);
    }

    public void EnabledMaintenance(Boolean enabled) {
        btnDelete.setDisable(!enabled);
        btnDelete.setManaged(enabled);
        btnDelete.setVisible(enabled);
        if (!isSport) {
            cmbSport.setDisable(enabled);
            cmbSport.setVisible(!enabled);
            cmbSport.setManaged(!enabled);
        }
    }

    public void EnabledExistImage(Boolean enabled) {
        btnCancel.setVisible(enabled);
        btnCancel.setManaged(enabled);
        btnCancel.setDisable(!enabled);
        AppContext.getInstance().set("ExistImage", enabled);
    }

    public void InitializeComboxSportType() {
        ObservableList<Sport> items = FXCollections.observableArrayList(sportList);
        cmbSport.setItems(items);
    }

    public void InitializeComponent() {
        labTitle.setText((String) AppContext.getInstance().get("Title"));
        EnabledTeam(!isSport);
        EnabledMaintenance(isMaintenace);
        mgvImage.fitHeightProperty().bind(hbxImage.heightProperty().multiply(0.85));
        mgvImage.fitWidthProperty().bind(hbxImage.widthProperty().multiply(0.85));
        imgOther.fitWidthProperty().bind(hbxImage.widthProperty().multiply(0.85));
        if (isMaintenace) {
            InitializeMaintenanceComponent();
        }
    }

    public void InitializeMaintenanceComponent() {
        if (isSport) {
            newSport = (Sport) AppContext.getInstance().get("selectedSport");
            txtName.setText(newSport.getName());
            file = new File(newSport.RuteImage());
            if (file.exists()) {
                image = new Image("file:" + newSport.RuteImage());
                mgvImage.setImage(image);
                newSport = FindObject(newSport, sportList);
                EnabledExistImage(true);
            } else {
                mgvImage.setImage(null);
                if (!(Boolean) AppContext.getInstance().get("neededTeamBoolean")) {
                    btnDelete.setDisable(false);
                    message.show(Alert.AlertType.INFORMATION, "Aviso", "La imagen del balón del deporte "
                            + newSport.getName() + " fue movida o eliminada.Debera de seleccionar una nueva imegen.");
                } else {
                    btnDelete.setDisable(true);
                }
                EnabledExistImage(false);
            }
        } else {
            newTeam = (Team) AppContext.getInstance().get("selectedTeam");
            txtName.setText(newTeam.getName());
            file = new File(newTeam.RuteImage());
            if (file.exists()) {
                image = new Image("file:" + newTeam.RuteImage());
                mgvImage.setImage(image);
                newTeam = FindObject(newTeam, teamList);
                EnabledExistImage(true);
            } else {
                mgvImage.setImage(null);
                message.show(Alert.AlertType.INFORMATION, "Aviso", "La imagen del equipo "
                        + newTeam.getName() + " fue movida o eliminada.Debera de seleccionar o tomar una nueva imegen.");
                EnabledExistImage(false);
            }
        }
    }

    public void InitializeList() {
        file = new File("Sport.txt");
        if ((file.exists()) && (file.length() > 0)) {
            sportList = fileManeger.deserialization("Sport", Sport.class);
            InitializeComboxSportType();
        }
        if (!isSport || isMaintenace) {
            file = new File("Team.txt");
            if ((file.exists()) && (file.length() > 0)) {
                teamList = fileManeger.deserialization("Team", Team.class);
            }
        }
        if (isMaintenace) {
            file = new File("Tourney.txt");
            if ((file.exists()) && (file.length() > 0)) {
                tourneyList = fileManeger.deserialization("Tourney", Tourney.class);
            }
        }
    }

    public void InitializeController() {
        isSport = (Boolean) AppContext.getInstance().get("isSport");
        isMaintenace = (Boolean) AppContext.getInstance().get("isMaintenace");
        InitializeList();
        InitializeComponent();
    }

    public void ClearPanel() {
        txtName.clear();
        cmbSport.setValue(null);
        cmbSport.getSelectionModel().clearSelection();
        mgvImage.setImage(null);
        newSport = null;
        newTeam = null;
        image = null;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ClearPanel();
        InitializeController();
    }

    @Override
    public void initialize() {
        ClearPanel();
        InitializeController();
    }

}
