package cr.ac.una.taskprogramll.controller;

import cr.ac.una.taskprogramll.util.ResourceUtil;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class MenuGameController extends Controller implements Initializable {

    private static final double POSICION_INICIAL_X = 0.5;
    private static final double POSICION_INICIAL_Y = 0.5;
    
    @FXML private AnchorPane rootPane;
    @FXML private StackPane personaje;
    @FXML private ImageView personajeImage;
    @FXML private Label mensaje;
    @FXML private Rectangle registroHitbox, mantenimientoHitbox, torneosHitbox;
    @FXML private Circle arbolHitbox1;
    @FXML private Rectangle bibliotecaHitbox, cafeteriaHitbox;

    private final double velocidad = 5.0;
    private final double personajeRadius = 15.0;
    private Map<Direction, Image[]> sprites;
    private boolean isMoving;
    private boolean stepToggle;
    private Direction lastDirection = Direction.DOWN;

    @Override
    public void initialize() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    private enum SpriteSet {
        DOWN(1001, 1041, 1042),    // Quieto, paso derecho, paso izquierdo
        UP(1002, 1031, 1032),
        RIGHT(1003, 1021, 1022),
        LEFT(1004, 1011, 1012);

        final int stillId;
        final int stepRightId;
        final int stepLeftId;

        SpriteSet(int stillId, int stepRightId, int stepLeftId) {
            this.stillId = stillId;
            this.stepRightId = stepRightId;
            this.stepLeftId = stepLeftId;
        }
    }

    private static final Object[][] BUILDING_CONFIG = {
        { "registroHitbox", 1948, 100.0, 100.0 },
        { "mantenimientoHitbox", 1948, 100.0, 100.0 },
        { "torneosHitbox", 1948, 100.0, 100.0 },
        { "bibliotecaHitbox", 1948, 100.0, 100.0 },
        { "cafeteriaHitbox", 1948, 120.0, 120.0 },
        { "arbolHitbox1", 1948, 80.0, 80.0 }
    };

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadSprites();
        loadBuildings();
        setupResponsiveness();
        setupKeyboard();
        positionCharacter();
        checkHitboxes();
    }

    private void loadSprites() {
        sprites = new HashMap<>();
        for (SpriteSet set : SpriteSet.values()) {
            Image[] images = new Image[3];
            images[0] = loadImage(set.stillId, "sprite " + set.stillId + ".png (quieto)");
            images[1] = loadImage(set.stepRightId, "sprite " + set.stepRightId + ".png");
            images[2] = loadImage(set.stepLeftId, "sprite " + set.stepLeftId + ".png");
            sprites.put(Direction.valueOf(set.name()), images);
        }
        // Inicializar personaje con quieto abajo
        Image stillDown = sprites.get(Direction.DOWN)[0];
        if (stillDown != null) {
            personajeImage.setImage(stillDown);
        } else {
            System.out.println("Usando color de respaldo para personaje");
            personaje.setStyle("-fx-background-color: #e53935;");
        }
    }

    private Image loadImage(int id, String errorMsg) {
        String path = ResourceUtil.getImagePath(id);
        if (path != null) {
            try {
                return new Image(path);
            } catch (Exception e) {
                System.out.println("Error al cargar " + errorMsg + ": " + e.getMessage());
            }
        } else {
            System.out.println("No se encontró la imagen: /cr/ac/una/taskprogramll/resources/" + id + ".png");
        }
        return null;
    }

    private void loadBuildings() {
        for (Object[] config : BUILDING_CONFIG) {
            String id = (String) config[0];
            int imageId = (Integer) config[1];
            double width = (Double) config[2];
            double height = (Double) config[3];

            Shape hitbox = getHitboxById(id);
            if (hitbox != null) {
                Image image = loadImage(imageId, "imagen " + imageId + ".png para " + id);
                if (image != null) {
                    double offset = id.equals("arbolHitbox1") ? -15 : 10;
                    hitbox.setFill(new ImagePattern(image, offset, offset, width, height, false));
                }
            }
        }
    }

    private Shape getHitboxById(String id) {
        return switch (id) {
            case "registroHitbox" -> registroHitbox;
            case "mantenimientoHitbox" -> mantenimientoHitbox;
            case "torneosHitbox" -> torneosHitbox;
            case "bibliotecaHitbox" -> bibliotecaHitbox;
            case "cafeteriaHitbox" -> cafeteriaHitbox;
            case "arbolHitbox1" -> arbolHitbox1;
            default -> null;
        };
    }

    private void setupResponsiveness() {
        rootPane.widthProperty().addListener((obs, oldVal, newVal) -> adjustCharacterPosition());
        rootPane.heightProperty().addListener((obs, oldVal, newVal) -> adjustCharacterPosition());
    }

    private void positionCharacter() {
        double centerX = rootPane.getWidth() * POSICION_INICIAL_X;
        double centerY = rootPane.getHeight() * POSICION_INICIAL_Y;
        // Ajustar para que layoutX, layoutY representen la esquina superior izquierda
        double layoutX = centerX - personajeRadius;
        double layoutY = centerY - personajeRadius;
        while (collidesWithObstacle(centerX, centerY)) {
            centerX += 20;
            centerY += 20;
            layoutX = centerX - personajeRadius;
            layoutY = centerY - personajeRadius;
        }
        personaje.setLayoutX(layoutX);
        personaje.setLayoutY(layoutY);
    }

    private void adjustCharacterPosition() {
        double centerX = personaje.getLayoutX() + personajeRadius;
        double centerY = personaje.getLayoutY() + personajeRadius;
        double relX = centerX / rootPane.getWidth();
        double relY = centerY / rootPane.getHeight();
        double newCenterX = rootPane.getWidth() * relX;
        double newCenterY = rootPane.getHeight() * relY;
        double newLayoutX = newCenterX - personajeRadius;
        double newLayoutY = newCenterY - personajeRadius;
        if (!isWithinMap(newCenterX, newCenterY) || collidesWithObstacle(newCenterX, newCenterY)) {
            newCenterX = rootPane.getWidth() * POSICION_INICIAL_X;
            newCenterY = rootPane.getHeight() * POSICION_INICIAL_Y;
            newLayoutX = newCenterX - personajeRadius;
            newLayoutY = newCenterY - personajeRadius;
        }
        personaje.setLayoutX(newLayoutX);
        personaje.setLayoutY(newLayoutY);
    }

    private void setupKeyboard() {
        rootPane.setFocusTraversable(true);
        rootPane.setOnKeyPressed(this::handleMovement);
        rootPane.setOnKeyReleased(event -> {
            isMoving = false;
            updateStillSprite();
        });
        rootPane.setOnMouseClicked(event -> rootPane.requestFocus());
    }

    private void checkHitboxes() {
        Shape[] obstacles = {registroHitbox, mantenimientoHitbox, torneosHitbox,
                             arbolHitbox1, bibliotecaHitbox, cafeteriaHitbox};
        for (Shape obstacle : obstacles) {
            if (obstacle != null) {
                Bounds bounds = obstacle.localToScene(obstacle.getBoundsInLocal());
                System.out.println("Hitbox " + obstacle.getId() + " bounds: " +
                                   "x=" + bounds.getMinX() + ", y=" + bounds.getMinY() +
                                   ", width=" + bounds.getWidth() + ", height=" + bounds.getHeight());
            } else {
                System.out.println("Hitbox no inicializada!");
            }
        }
    }

    private boolean isWithinMap(double x, double y) {
        return x >= personajeRadius && x <= rootPane.getWidth() - personajeRadius &&
               y >= personajeRadius && y <= rootPane.getHeight() - personajeRadius;
    }

    private boolean collidesWithObstacle(double x, double y) {
        Circle tempPersonaje = new Circle(x, y, personajeRadius);
        Shape[] obstacles = {registroHitbox, mantenimientoHitbox, torneosHitbox,
                             bibliotecaHitbox, cafeteriaHitbox, arbolHitbox1};
        for (Shape obstacle : obstacles) {
            if (obstacle != null) {
                Bounds personajeBounds = tempPersonaje.localToScene(tempPersonaje.getBoundsInLocal());
                Bounds obstaculoBounds = obstacle.localToScene(obstacle.getBoundsInLocal());
                if (personajeBounds.intersects(obstaculoBounds)) {
                    System.out.println("Colisión detectada con: " + obstacle.getId() +
                                       " en personaje(x=" + x + ", y=" + y + ")");
                    return true;
                }
            }
        }
        return false;
    }

    private void handleMovement(KeyEvent event) {
        double centerX = personaje.getLayoutX() + personajeRadius;
        double centerY = personaje.getLayoutY() + personajeRadius;
        double newCenterX = centerX;
        double newCenterY = centerY;
        isMoving = true;

        switch (event.getCode()) {
            case LEFT, A:
                newCenterX -= velocidad;
                lastDirection = Direction.LEFT;
                updateStepSprite();
                break;
            case RIGHT, D:
                newCenterX += velocidad;
                lastDirection = Direction.RIGHT;
                updateStepSprite();
                break;
            case UP, W:
                newCenterY -= velocidad;
                lastDirection = Direction.UP;
                updateStepSprite();
                break;
            case DOWN, S:
                newCenterY += velocidad;
                lastDirection = Direction.DOWN;
                updateStepSprite();
                break;
            case SPACE:
                checkInteraction();
                isMoving = false;
                updateStillSprite();
                break;
            default:
                isMoving = false;
                updateStillSprite();
        }

        double newLayoutX = newCenterX - personajeRadius;
        double newLayoutY = newCenterY - personajeRadius;
        if (isWithinMap(newCenterX, newCenterY) && !collidesWithObstacle(newCenterX, newCenterY)) {
            personaje.setLayoutX(newLayoutX);
            personaje.setLayoutY(newLayoutY);
            checkProximity();
        } else {
            mensaje.setText("¡Movimiento bloqueado!");
            isMoving = false;
            updateStillSprite();
        }
        event.consume();
    }

    private void updateStepSprite() {
        if (isMoving) {
            Image[] directionSprites = sprites.get(lastDirection);
            if (directionSprites != null && directionSprites[1] != null && directionSprites[2] != null) {
                personajeImage.setImage(stepToggle ? directionSprites[1] : directionSprites[2]);
                stepToggle = !stepToggle;
            }
        }
    }

    private void updateStillSprite() {
        Image[] directionSprites = sprites.get(lastDirection);
        if (directionSprites != null && directionSprites[0] != null) {
            personajeImage.setImage(directionSprites[0]);
        }
    }

    private void checkProximity() {
        mensaje.setText("");
        double centerX = personaje.getLayoutX() + personajeRadius;
        double centerY = personaje.getLayoutY() + personajeRadius;
        if (collidesWith(registroHitbox, centerX, centerY)) {
            mensaje.setText("Presiona SPACE para entrar al Registro");
        } else if (collidesWith(mantenimientoHitbox, centerX, centerY)) {
            mensaje.setText("Presiona SPACE para entrar a Mantenimiento");
        } else if (collidesWith(torneosHitbox, centerX, centerY)) {
            mensaje.setText("Presiona SPACE para entrar a Torneos");
        } else if (collidesWith(bibliotecaHitbox, centerX, centerY)) {
            mensaje.setText("Presiona SPACE para entrar a la Biblioteca");
        } else if (collidesWith(cafeteriaHitbox, centerX, centerY)) {
            mensaje.setText("Presiona SPACE para entrar a la Cafetería");
        } else if (collidesWith(arbolHitbox1, centerX, centerY)) {
            mensaje.setText("No puedes interactuar con el árbol...");
        }
    }

    private boolean collidesWith(Shape hitbox, double x, double y) {
        if (hitbox == null) return false;
        Circle tempPersonaje = new Circle(x, y, personajeRadius);
        Bounds personajeBounds = tempPersonaje.localToScene(tempPersonaje.getBoundsInLocal());
        Bounds hitboxBounds = hitbox.localToScene(hitbox.getBoundsInLocal());
        return personajeBounds.intersects(hitboxBounds);
    }

    private void checkInteraction() {
        double centerX = personaje.getLayoutX() + personajeRadius;
        double centerY = personaje.getLayoutY() + personajeRadius;
        if (collidesWith(registroHitbox, centerX, centerY)) {
            mensaje.setText("¡Bienvenido al Registro!");
        } else if (collidesWith(mantenimientoHitbox, centerX, centerY)) {
            mensaje.setText("Zona de Mantenimiento - ¡Cuidado!");
        } else if (collidesWith(torneosHitbox, centerX, centerY)) {
            mensaje.setText("¡Estadio de Torneos - Listo para competir!");
        } else if (collidesWith(bibliotecaHitbox, centerX, centerY)) {
            mensaje.setText("¡Bienvenido a la Biblioteca!");
        } else if (collidesWith(cafeteriaHitbox, centerX, centerY)) {
            mensaje.setText("¡Hora de un café en la Cafetería!");
        } else if (collidesWith(arbolHitbox1, centerX, centerY)) {
            mensaje.setText("Es solo un árbol... nada que hacer aquí.");
        }
    }
}