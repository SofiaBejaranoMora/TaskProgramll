package cr.ac.una.taskprogramll.util;

import cr.ac.una.taskprogramll.App;
import java.io.IOException;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import cr.ac.una.taskprogramll.controller.Controller;
import io.github.palexdev.materialfx.css.themes.MFXThemeManager;
import io.github.palexdev.materialfx.css.themes.Themes;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;

public class FlowController {

    private static FlowController INSTANCE = null;
    private static Stage mainStage;
    private static ResourceBundle idioma;
    private static HashMap<String, FXMLLoader> loaders = new HashMap<>();

    private FlowController() {
    }

    private static void createInstance() {
        if (INSTANCE == null) {
            synchronized (FlowController.class) {
                if (INSTANCE == null) {
                    INSTANCE = new FlowController();
                }
            }
        }
    }

    public static FlowController getInstance() {
        if (INSTANCE == null) {
            createInstance();
        }
        return INSTANCE;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public void InitializeFlow(Stage stage, ResourceBundle idioma) {
        getInstance();
        this.mainStage = stage;
        this.idioma = idioma;
    }

    private FXMLLoader getLoader(String name) {
        FXMLLoader loader = loaders.get(name);
        if (loader == null) {
            synchronized (FlowController.class) {
                if (loader == null) {
                    try {
                        loader = new FXMLLoader(App.class.getResource("view/" + name + ".fxml"), this.idioma);
                        loader.load();
                        loaders.put(name, loader);
                    } catch (Exception ex) {
                        loader = null;
                        java.util.logging.Logger.getLogger(FlowController.class.getName()).log(Level.SEVERE, "Creando loader [" + name + "].", ex);
                    }
                }
            }
        }
        return loader;
    }

    public void goMain() {
        try {
            this.mainStage.setScene(new Scene(FXMLLoader.load(App.class.getResource("view/RegistrationMaintenance.fxml"), this.idioma)));
            MFXThemeManager.addOn(this.mainStage.getScene(), Themes.DEFAULT, Themes.LEGACY);
            this.mainStage.show();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(FlowController.class.getName()).log(Level.SEVERE, "Error inicializando la vista base.", ex);
        }
    }

    public void goView(String viewName) {
        goView(viewName, "Center", null);
    }

    public void goView(String viewName, String accion) {
        goView(viewName, "Center", accion);
    }

    public void goView(String viewName, String location, String accion) { // es para border panel, ademas el accion es para pasar informacion
        FXMLLoader loader = getLoader(viewName);
        Controller controller = loader.getController();
        controller.setAccion(accion);
        controller.initialize();
        Stage stage = controller.getStage();
        if (stage == null) {
            stage = this.mainStage;
            controller.setStage(stage);
        } controller.setStage(stage);
        switch (location) {
            case "Center": // esto es lo mismo para todas las partes(arriba,abajo,la izquierda y derecha)
                Node loadedContent = loader.getRoot();

                if (loadedContent instanceof AnchorPane anchorPane) {
                    VBox vBox = (VBox) ((BorderPane) stage.getScene().getRoot()).getCenter();
                    vBox.getChildren().clear();
                    vBox.getChildren().add(anchorPane);
                    VBox.setVgrow(anchorPane, Priority.ALWAYS);
                } else {
                    VBox vBox = (VBox) ((BorderPane) stage.getScene().getRoot()).getCenter();
                    vBox.getChildren().clear();
                    vBox.getChildren().add(loadedContent);
                }
                        
                /*VBox vBox = ((VBox) ((BorderPane) stage.getScene().getRoot()).getCenter());
                vBox.getChildren().clear();
                vBox.getChildren().add(loader.getRoot());*/
                break;
            case "Top":
                break;
            case "Bottom":
                break;
            case "Right":
                break;
            case "Left":
                break;
            default:
                break;
        }
    }

    public void goViewInStage(String viewName, Stage stage) {
        FXMLLoader loader = getLoader(viewName);
        Controller controller = loader.getController();
        controller.setStage(stage);
        stage.getScene().setRoot(loader.getRoot());
        MFXThemeManager.addOn(stage.getScene(), Themes.DEFAULT, Themes.LEGACY);
        
    }

    public void goViewInWindow(String viewName) {
        FXMLLoader loader = getLoader(viewName);
        Controller controller = loader.getController();
        controller.initialize();
        Stage stage = new Stage();
        stage.getIcons().add(new Image("cr/ac/una/taskprogramll/resources/LogoUNArojo.png"));
        stage.setTitle(controller.getNombreVista());
        stage.setOnHidden((WindowEvent event) -> {
            controller.getStage().getScene().setRoot(new Pane());
            controller.setStage(null);
        });
        controller.setStage(stage);
        Parent root = loader.getRoot();
        Scene scene = new Scene(root);
        MFXThemeManager.addOn(scene, Themes.DEFAULT, Themes.LEGACY);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    public void goViewInWindowModal(String viewName, Stage parentStage, Boolean resizable) {
        FXMLLoader loader = getLoader(viewName);
        Controller controller = loader.getController();
        controller.initialize();
        Stage stage = new Stage();
        stage.getIcons().add(new Image("cr/ac/una/taskprogramll/resources/LogoUNArojo.png"));
        stage.setTitle(controller.getNombreVista());
        stage.setResizable(resizable);
        stage.setOnHidden((WindowEvent event) -> {
            controller.getStage().getScene().setRoot(new Pane());
            controller.setStage(null);
        });
        controller.setStage(stage);
        Parent root = loader.getRoot();
        Scene scene = new Scene(root);
        MFXThemeManager.addOn(scene, Themes.DEFAULT, Themes.LEGACY);
        stage.setScene(scene);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(parentStage);
        stage.centerOnScreen();
        stage.showAndWait();

    }

    public Controller getController(String viewName) {
        return getLoader(viewName).getController();
    }
    
    public void limpiarLoader(String view){
        this.loaders.remove(view);
    }

    public static void setIdioma(ResourceBundle idioma) {
        FlowController.idioma = idioma;
    }
    
    public void initialize() {
        this.loaders.clear();
    }

    public void salir() {
        this.mainStage.close();
    }

}
