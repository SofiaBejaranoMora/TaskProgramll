package cr.ac.una.taskprogramll.controller;

import cr.ac.una.taskprogramll.util.ResourceUtil;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class MenuGameController extends Controller implements Initializable {

    private static final double POSICION_INICIAL_X = 0.5;
    private static final double POSICION_INICIAL_Y = 0.5;
    
    @FXML private AnchorPane rootPane;
    @FXML private Circle personaje;
    @FXML private Label mensaje;
    @FXML private Rectangle registroHitbox, mantenimientoHitbox, torneosHitbox;
    @FXML private Circle arbolHitbox1;
    @FXML private Rectangle bibliotecaHitbox, cafeteriaHitbox;

    private final double velocidad = 5.0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarImagenes();
        configurarResponsividad();
        configurarEventosTeclado();
        posicionarPersonajeInicial();
        verificarHitboxes();
    }

    private void cargarImagenes() {
        // Registro
        String registroPath = ResourceUtil.getImagePath("estrella.png");
        if (registroPath != null) {
            Image registroImg = new Image(registroPath);
            registroHitbox.setFill(new ImagePattern(registroImg, 10, 10, 100, 100, false));
        } else {
            System.out.println("No se pudo cargar estrella.png para registroHitbox");
        }

        // Mantenimiento
        String mantenimientoPath = ResourceUtil.getImagePath("ajedrez.png");
        if (mantenimientoPath != null) {
            Image mantenimientoImg = new Image(mantenimientoPath);
            mantenimientoHitbox.setFill(new ImagePattern(mantenimientoImg, 10, 10, 100, 100, false));
        } else {
            System.out.println("No se pudo cargar ajedrez.png para mantenimientoHitbox");
        }

        // Torneos
        String torneosPath = ResourceUtil.getImagePath("stadium.png");
        if (torneosPath != null) {
            Image torneosImg = new Image(torneosPath);
            torneosHitbox.setFill(new ImagePattern(torneosImg, 10, 10, 100, 100, false));
        } else {
            System.out.println("No se pudo cargar stadium.png para torneosHitbox");
        }

        // Biblioteca
        String bibliotecaPath = ResourceUtil.getImagePath("estrella.png");
        if (bibliotecaPath != null) {
            Image bibliotecaImg = new Image(bibliotecaPath);
            bibliotecaHitbox.setFill(new ImagePattern(bibliotecaImg, 10, 10, 100, 100, false));
        } else {
            System.out.println("No se pudo cargar estrella.png para bibliotecaHitbox");
        }

        // Cafetería
        String cafeteriaPath = ResourceUtil.getImagePath("lupa.png");
        if (cafeteriaPath != null) {
            Image cafeteriaImg = new Image(cafeteriaPath);
            cafeteriaHitbox.setFill(new ImagePattern(cafeteriaImg, 10, 10, 120, 120, false));
        } else {
            System.out.println("No se pudo cargar lupa.png para cafeteriaHitbox");
        }

        // Árbol
        String arbolPath = ResourceUtil.getImagePath("arbol.png");
        if (arbolPath != null) {
            Image arbolImg = new Image(arbolPath);
            arbolHitbox1.setFill(new ImagePattern(arbolImg, -15, -15, 80, 80, false));
        } else {
            System.out.println("No se pudo cargar arbol.png para arbolHitbox1");
        }
    }

    private void configurarResponsividad() {
        rootPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            ajustarPosicionPersonaje();
        });
        rootPane.heightProperty().addListener((obs, oldVal, newVal) -> {
            ajustarPosicionPersonaje();
        });
    }

    private void posicionarPersonajeInicial() {
        double centerX = rootPane.getWidth() * POSICION_INICIAL_X;
        double centerY = rootPane.getHeight() * POSICION_INICIAL_Y;
        while(colisionaConObstaculo(centerX, centerY)) {
            centerX += 20;
            centerY += 20;
        }
        personaje.setLayoutX(centerX);
        personaje.setLayoutY(centerY);
    }

    private void ajustarPosicionPersonaje() {
        double relX = personaje.getLayoutX() / rootPane.getWidth();
        double relY = personaje.getLayoutY() / rootPane.getHeight();
        double newX = rootPane.getWidth() * relX;
        double newY = rootPane.getHeight() * relY;
        if(!estaDentroDelMapa(newX, newY) || colisionaConObstaculo(newX, newY)) {
            newX = rootPane.getWidth() * POSICION_INICIAL_X;
            newY = rootPane.getHeight() * POSICION_INICIAL_Y;
        }
        personaje.setLayoutX(newX);
        personaje.setLayoutY(newY);
    }

    private void configurarEventosTeclado() {
        rootPane.setFocusTraversable(true);
        rootPane.setOnKeyPressed(this::manejarMovimiento);
        rootPane.setOnMouseClicked(event -> rootPane.requestFocus());
    }

    private void verificarHitboxes() {
        Shape[] obstaculos = {registroHitbox, mantenimientoHitbox, torneosHitbox,
                             arbolHitbox1, bibliotecaHitbox, cafeteriaHitbox};
        for (Shape obstaculo : obstaculos) {
            if (obstaculo != null) {
                Bounds bounds = obstaculo.localToScene(obstaculo.getBoundsInLocal());
                System.out.println("Hitbox " + obstaculo.getId() + " bounds: " +
                                   "x=" + bounds.getMinX() + ", y=" + bounds.getMinY() +
                                   ", width=" + bounds.getWidth() + ", height=" + bounds.getHeight());
            } else {
                System.out.println("Hitbox no inicializada!");
            }
        }
    }

    private boolean estaDentroDelMapa(double x, double y) {
        return x >= personaje.getRadius() && x <= rootPane.getWidth() - personaje.getRadius() &&
               y >= personaje.getRadius() && y <= rootPane.getHeight() - personaje.getRadius();
    }

    private boolean colisionaConObstaculo(double x, double y) {
        Circle tempPersonaje = new Circle(x, y, personaje.getRadius());
        Shape[] obstaculos = {registroHitbox, mantenimientoHitbox, torneosHitbox,
                              bibliotecaHitbox, cafeteriaHitbox, arbolHitbox1};
        for (Shape obstaculo : obstaculos) {
            if (obstaculo != null) {
                Bounds personajeBounds = tempPersonaje.localToScene(tempPersonaje.getBoundsInLocal());
                Bounds obstaculoBounds = obstaculo.localToScene(obstaculo.getBoundsInLocal());
                if (personajeBounds.intersects(obstaculoBounds)) {
                    System.out.println("Colisión detectada con: " + obstaculo.getId() +
                                       " en personaje(x=" + x + ", y=" + y + ")");
                    return true;
                }
            }
        }
        return false;
    }

    private void manejarMovimiento(KeyEvent event) {
        double newX = personaje.getLayoutX();
        double newY = personaje.getLayoutY();
        switch (event.getCode()) {
            case LEFT, A -> newX -= velocidad;
            case RIGHT, D -> newX += velocidad;
            case UP, W -> newY -= velocidad;
            case DOWN, S -> newY += velocidad;
            case SPACE -> verificarInteraccion();
        }
        if (estaDentroDelMapa(newX, newY) && !colisionaConObstaculo(newX, newY)) {
            personaje.setLayoutX(newX);
            personaje.setLayoutY(newY);
            verificarProximidad();
        } else {
            mensaje.setText("¡Movimiento bloqueado!");
        }
        event.consume();
    }

    private void verificarProximidad() {
        mensaje.setText("");
        if (colisionaCon(registroHitbox)) {
            mensaje.setText("Presiona SPACE para entrar al Registro");
        } else if (colisionaCon(mantenimientoHitbox)) {
            mensaje.setText("Presiona SPACE para entrar a Mantenimiento");
        } else if (colisionaCon(torneosHitbox)) {
            mensaje.setText("Presiona SPACE para entrar a Torneos");
        } else if (colisionaCon(bibliotecaHitbox)) {
            mensaje.setText("Presiona SPACE para entrar a la Biblioteca");
        } else if (colisionaCon(cafeteriaHitbox)) {
            mensaje.setText("Presiona SPACE para entrar a la Cafetería");
        } else if (colisionaCon(arbolHitbox1)) {
            mensaje.setText("No puedes interactuar con el árbol...");
        }
    }

    private boolean colisionaCon(Shape hitbox) {
        if (hitbox == null) return false;
        Bounds personajeBounds = personaje.localToScene(personaje.getBoundsInLocal());
        Bounds hitboxBounds = hitbox.localToScene(hitbox.getBoundsInLocal());
        return personajeBounds.intersects(hitboxBounds);
    }

    private void verificarInteraccion() {
        if (colisionaCon(registroHitbox)) {
            mensaje.setText("¡Bienvenido al Registro!");
        } else if (colisionaCon(mantenimientoHitbox)) {
            mensaje.setText("Zona de Mantenimiento - ¡Cuidado!");
        } else if (colisionaCon(torneosHitbox)) {
            mensaje.setText("¡Estadio de Torneos - Listo para competir!");
        } else if (colisionaCon(bibliotecaHitbox)) {
            mensaje.setText("¡Bienvenido a la Biblioteca!");
        } else if (colisionaCon(cafeteriaHitbox)) {
            mensaje.setText("¡Hora de un café en la Cafetería!");
        } else if (colisionaCon(arbolHitbox1)) {
            mensaje.setText("Es solo un árbol... nada que hacer aquí.");
        }
    }

    @Override
    public void initialize() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}