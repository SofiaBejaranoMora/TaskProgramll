/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package cr.ac.una.taskprogramll.controller;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import cr.ac.una.taskprogramll.util.AppContext;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.utils.SwingFXUtils;
import java.awt.Dimension;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author sofia
 */
public class PhotographyController extends Controller implements Initializable {

    public Dimension dimension = new Dimension(288, 240);
    private Webcam webcam = Webcam.getDefault();
    private WebcamPanel webcamPanel = new WebcamPanel(webcam, dimension, false);
    private BufferedImage bufferedImage;
    private Thread thread;
    private Image photo = null;
    private Image image = null;
    private Boolean checkedCamera = true;

    @FXML
    private ImageView imvCamera;
    @FXML
    private MFXButton bntTakePhoto;
    @FXML
    private ImageView imvPhotography;
    @FXML
    private MFXButton btnSavePhoto;
    @FXML
    private MFXButton btnCancel;

    @FXML
    private void onActionBntTakePhoto(ActionEvent event) {
        btnSavePhoto.setDisable(false);
        bufferedImage = webcam.getImage();
        photo = SwingFXUtils.toFXImage(bufferedImage, null);
        imvPhotography.setImage(photo);
    }

    @FXML
    private void onActionBtnSavePhoto(ActionEvent event) {
        AppContext.getInstance().set("bufferedImageTeam", bufferedImage);
        close();
    }

    @FXML
    private void OnActionBtnCancel(ActionEvent event) {
        bufferedImage=null;
        AppContext.getInstance().set("bufferedImageTeam", bufferedImage);
        close();
    }

    public void camera() {
        webcam.open();

        thread = new Thread() {

            @Override
            public void run() {
                webcamPanel.start();
                while (checkedCamera) {
                    image = SwingFXUtils.toFXImage(webcam.getImage(), null);
                    imvCamera.setImage(image);
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        if (checkedCamera) {
            thread.start();
        }

    }

    public void close() {
        checkedCamera = false;
        webcamPanel.stop();
        webcam.close();
        imvPhotography.setImage(null);
        photo = null;
        bufferedImage = null;
        ((Stage) btnSavePhoto.getScene().getWindow()).close();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnSavePhoto.setDisable(true);
        camera();
    }

    @Override
    public void initialize() {
       btnSavePhoto.setDisable(true);
        camera();
    }
}
