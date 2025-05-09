package cr.ac.una.taskprogramll.controller;

import cr.ac.una.taskprogramll.util.FlowController;
import cr.ac.una.taskprogramll.util.ImagesUtil;
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
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

public class MenuGameController extends Controller implements Initializable {

    private static final double POSICION_INICIAL_X = 0.5;
    private static final double POSICION_INICIAL_Y = 0.5;
    
    @FXML private AnchorPane rootPane;
    @FXML private StackPane personaje;
    @FXML private ImageView personajeImage;
    @FXML private Label mensaje;
    @FXML private Rectangle historialEquiposHitbox, gomainHitbox, torneosHitbox;
    @FXML private Circle arbolHitbox1;
    @FXML private Rectangle bibliotecaHitbox, cafeteriaHitbox;
    @FXML private ImageView historialEquiposImage, gomainImage, torneosImage;
    @FXML private ImageView arbolImage, bibliotecaImage, cafeteriaImage;
    @FXML private Label historialEquiposLabel, arbolLabel, gomainLabel, torneosLabel, bibliotecaLabel, cafeteriaLabel;

    private final double velocidad = 5.0;
    private final double personajeRadius = 28.5;
    private final double PROXIMITY_MARGIN = 20.0; 
    private Map<Direction, Image[]> sprites;
    private boolean isMoving;
    private boolean stepToggle;
    private Direction lastDirection = Direction.DOWN;
    private String currentHoverBuilding;
    private String currentMouseHoverBuilding;
    private final double HOVER_SCALE = 1.1; 

    private static final Object[][] BUILDING_CONFIG = {
        { "historialEquiposHitbox", "historialEquiposImage", "historialEquiposLabel", 1111 },
        { "gomainHitbox", "gomainImage", "gomainLabel", 7777 },
        { "torneosHitbox", "torneosImage", "torneosLabel", 1948 },
        { "bibliotecaHitbox", "bibliotecaImage", "bibliotecaLabel", 3333 },
        { "cafeteriaHitbox", "cafeteriaImage", "cafeteriaLabel", 2222 },
        { "arbolHitbox1", "arbolImage", "arbolLabel", 9999 }
    };

    private static final int BACKGROUND_IMAGE_ID = 5555; 

    @Override
    public void initialize() {
    }

    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    private enum SpriteSet {
        DOWN(1001, 1041, 1042),   
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadBackground();
        loadSprites();
        loadBuildings();
        setupMouseHoverEffects();
        setupResponsiveness();
        setupKeyboard();
        positionCharacter();
        checkHitboxes();
    }

    private void loadBackground() {
        String path = ImagesUtil.getSkinPath(BACKGROUND_IMAGE_ID);
        if (path != null) {
            System.out.println("Cargando fondo desde: " + path);
            try {
                String backgroundStyle = String.format(
                    "-fx-background-image: url('%s'); -fx-background-size: cover; -fx-background-repeat: no-repeat;",
                    path
                );
                rootPane.setStyle(backgroundStyle);
            } catch (Exception e) {
                System.out.println("Error al cargar la imagen de fondo " + BACKGROUND_IMAGE_ID + ".png: " + e.getMessage());
            }
        } else {
            System.out.println("No se encontró la imagen de fondo: /cr/ac/una/taskprogramll/resources/skinName/" + BACKGROUND_IMAGE_ID + ".png");
            try {
                String manualPath = getClass().getResource("/cr/ac/una/taskprogramll/resources/skinName/" + BACKGROUND_IMAGE_ID + ".png").toExternalForm();
                System.out.println("Intentando cargar fondo manualmente desde: " + manualPath);
                String backgroundStyle = String.format(
                    "-fx-background-image: url('%s'); -fx-background-size: cover; -fx-background-repeat: no-repeat;",
                    manualPath
                );
                rootPane.setStyle(backgroundStyle);
            } catch (Exception e) {
                System.out.println("Error al cargar la imagen de fondo manualmente: " + e.getMessage());
            }
        }
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
        Image stillDown = sprites.get(Direction.DOWN)[0];
        if (stillDown != null) {
            personajeImage.setImage(stillDown);
        } else {
            System.out.println("No se pudo cargar el sprite del personaje. No se aplicará estilo de respaldo.");
        }
    }

    private Image loadImage(int id, String errorMsg) {
        String path = ImagesUtil.getSkinPath(id);
        if (path != null) {
            System.out.println("Cargando imagen desde: " + path);
            try {
                Image image = new Image(path);
                if (image.isError()) {
                    System.out.println("Error: La imagen " + errorMsg + " no se pudo cargar (imagen corrupta o no válida)");
                    return null;
                }
                return image;
            } catch (Exception e) {
                System.out.println("Error al cargar " + errorMsg + ": " + e.getMessage());
            }
        } else {
            System.out.println("No se encontró la imagen: /cr/ac/una/taskprogramll/resources/skinName/" + id + ".png");
            try {
                String manualPath = getClass().getResource("/cr/ac/una/taskprogramll/resources/skinName/" + id + ".png").toExternalForm();
                System.out.println("Intentando cargar imagen manualmente desde: " + manualPath);
                Image image = new Image(manualPath);
                if (image.isError()) {
                    System.out.println("Error: La imagen " + errorMsg + " no se pudo cargar manualmente (imagen corrupta o no válida)");
                    return null;
                }
                return image;
            } catch (Exception e) {
                System.out.println("Error al cargar la imagen manualmente: " + e.getMessage());
            }
        }
        return null;
    }

    private void loadBuildings() {
        for (Object[] config : BUILDING_CONFIG) {
            String imageViewId = (String) config[1];
            int imageId = (Integer) config[3];

            ImageView imageView = getImageViewById(imageViewId);
            if (imageView != null) {
                Image image = loadImage(imageId, "imagen " + imageId + ".png para " + imageViewId);
                if (image != null) {
                    imageView.setImage(image);
                    System.out.println("Imagen cargada correctamente para " + imageViewId);
                } else {
                    System.out.println("No se pudo cargar la imagen para " + imageViewId + ". Verifica que " + imageId + ".png esté en resources/skinName/. No se aplicará estilo de respaldo.");
                }
            } else {
                System.out.println("ImageView no encontrado para " + imageViewId);
            }
        }
    }

    private void setupMouseHoverEffects() {
        for (Object[] config : BUILDING_CONFIG) {
            String imageViewId = (String) config[1];
            ImageView imageView = getImageViewById(imageViewId);
            if (imageView != null) {
                imageView.setOnMouseEntered(event -> {
                    System.out.println("Mouse entró en " + imageViewId);
                    if (currentHoverBuilding == null) { 
                        currentMouseHoverBuilding = imageViewId;
                        applyHoverEffect(imageView, null);
                    }
                });
                imageView.setOnMouseExited(event -> {
                    System.out.println("Mouse salió de " + imageViewId);
                    if (!imageViewId.equals(currentHoverBuilding)) {
                        resetHoverEffect(imageView, null);
                    }
                    currentMouseHoverBuilding = null;
                });

                if (!imageViewId.equals("arbolImage")) {
                    imageView.setOnMouseClicked(event -> {
                        System.out.println("Clic en " + imageViewId);
                        switch (imageViewId) {
                            case "historialEquiposImage" -> goTeamsHistory();
                            case "gomainImage" -> goGomain();
                            case "torneosImage" -> goTourneys();
                            case "bibliotecaImage" -> goReviews();
                            case "cafeteriaImage" -> goLobby();
                        }
                    });
                }
            }
        }
    }

    private ImageView getImageViewById(String id) {
        return switch (id) {
            case "historialEquiposImage" -> historialEquiposImage;
            case "gomainImage" -> gomainImage;
            case "torneosImage" -> torneosImage;
            case "bibliotecaImage" -> bibliotecaImage;
            case "cafeteriaImage" -> cafeteriaImage;
            case "arbolImage" -> arbolImage;
            default -> null;
        };
    }

    private Shape getHitboxById(String id) {
        return switch (id) {
            case "historialEquiposHitbox" -> historialEquiposHitbox;
            case "gomainHitbox" -> gomainHitbox;
            case "torneosHitbox" -> torneosHitbox;
            case "bibliotecaHitbox" -> bibliotecaHitbox;
            case "cafeteriaHitbox" -> cafeteriaHitbox;
            case "arbolHitbox1" -> arbolHitbox1;
            default -> null;
        };
    }

    private Label getLabelById(String id) {
        return switch (id) {
            case "historialEquiposLabel" -> historialEquiposLabel;
            case "gomainLabel" -> gomainLabel;
            case "torneosLabel" -> torneosLabel;
            case "bibliotecaLabel" -> bibliotecaLabel;
            case "cafeteriaLabel" -> cafeteriaLabel;
            case "arbolLabel" -> arbolLabel;
            default -> null;
        };
    }

    private void setupResponsiveness() {
        rootPane.widthProperty().addListener((obs, oldVal, newVal) -> adjustCharacterPosition());
        rootPane.heightProperty().addListener((obs, oldVal, newVal) -> adjustCharacterPosition());
    }

    private void positionCharacter() {
        double centerX = rootPane.getWidth() * 0.75;
        double centerY = rootPane.getHeight() * 0.5;
        double layoutX = centerX - personajeRadius;
        double layoutY = centerY - personajeRadius;

        int attempts = 0;
        while (collidesWithObstacle(centerX, centerY) && attempts < 10) {
            centerX += 50;
            centerY += 50;
            layoutX = centerX - personajeRadius;
            layoutY = centerY - personajeRadius;
            attempts++;
        }

        if (attempts >= 10) {
            centerX = rootPane.getWidth() * 0.75;
            centerY = rootPane.getHeight() * 0.5;
            layoutX = centerX - personajeRadius;
            layoutY = centerY - personajeRadius;
        }

        personaje.setLayoutX(layoutX);
        personaje.setLayoutY(layoutY);
        System.out.println("Personaje posicionado en: x=" + layoutX + ", y=" + layoutY);
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
            newCenterX = rootPane.getWidth() * 0.75;
            newCenterY = rootPane.getHeight() * 0.5;
            newLayoutX = newCenterX - personajeRadius;
            newLayoutY = newCenterY - personajeRadius;
        }

        personaje.setLayoutX(newLayoutX);
        personaje.setLayoutY(newLayoutY);
        System.out.println("Personaje ajustado a: x=" + newLayoutX + ", y=" + newLayoutY);
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
        Shape[] obstacles = {historialEquiposHitbox, gomainHitbox, torneosHitbox,
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
        boolean within = x >= personajeRadius && x <= rootPane.getWidth() - personajeRadius &&
                        y >= personajeRadius && y <= rootPane.getHeight() - personajeRadius;
        System.out.println("Verificando si está dentro del mapa: x=" + x + ", y=" + y + ", resultado=" + within);
        return within;
    }

    private boolean collidesWithObstacle(double x, double y) {
        Circle tempPersonaje = new Circle(x, y, personajeRadius);
        Shape[] obstacles = {historialEquiposHitbox, gomainHitbox, torneosHitbox,
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
            System.out.println("Moviendo personaje a: x=" + newLayoutX + ", y=" + newLayoutY);
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
                if (stepToggle) {
                    personajeImage.setImage(directionSprites[1]);
                } else {
                    personajeImage.setImage(directionSprites[2]);
                }
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
        String previousHoverBuilding = currentHoverBuilding;
        currentHoverBuilding = null;

        System.out.println("Verificando proximidad en: x=" + centerX + ", y=" + centerY);

        if (collidesWith(historialEquiposHitbox, centerX, centerY)) {
            mensaje.setText("Presiona SPACE para entrar al Historial de Equipos");
            currentHoverBuilding = "historialEquiposHitbox";
            applyHoverEffect(historialEquiposImage, historialEquiposHitbox);
        } else if (collidesWith(gomainHitbox, centerX, centerY)) {
            mensaje.setText("Presiona SPACE para entrar a Gomain");
            currentHoverBuilding = "gomainHitbox";
            applyHoverEffect(gomainImage, gomainHitbox);
        } else if (collidesWith(torneosHitbox, centerX, centerY)) {
            mensaje.setText("Presiona SPACE para entrar a Torneos");
            currentHoverBuilding = "torneosHitbox";
            applyHoverEffect(torneosImage, torneosHitbox);
        } else if (collidesWith(bibliotecaHitbox, centerX, centerY)) {
            mensaje.setText("Presiona SPACE para entrar a Reseñas");
            currentHoverBuilding = "bibliotecaHitbox";
            applyHoverEffect(bibliotecaImage, bibliotecaHitbox);
        } else if (collidesWith(cafeteriaHitbox, centerX, centerY)) {
            mensaje.setText("Presiona SPACE para entrar al GameHub");
            currentHoverBuilding = "cafeteriaHitbox";
            applyHoverEffect(cafeteriaImage, cafeteriaHitbox);
        } else if (collidesWith(arbolHitbox1, centerX, centerY)) {
            mensaje.setText("No puedes interactuar con el árbol...");
            currentHoverBuilding = "arbolHitbox1";
            applyHoverEffect(arbolImage, arbolHitbox1);
        }

        if (previousHoverBuilding != null && !previousHoverBuilding.equals(currentHoverBuilding)) {
            ImageView previousImageView = getImageViewById(previousHoverBuilding.replace("Hitbox", "Image"));
            Shape previousHitbox = getHitboxById(previousHoverBuilding);
            if (previousImageView != null) {
                resetHoverEffect(previousImageView, previousHitbox);
            }
        }
    }

    private void applyHoverEffect(ImageView imageView, Shape hitbox) {
        if (imageView != null) {
            System.out.println("Aplicando hover a ImageView: " + imageView.getId());
            imageView.setScaleX(HOVER_SCALE);
            imageView.setScaleY(HOVER_SCALE);
        }
        if (hitbox != null) {
            System.out.println("Aplicando hover a Hitbox: " + hitbox.getId());
            hitbox.setScaleX(HOVER_SCALE);
            hitbox.setScaleY(HOVER_SCALE);
        }
    }

    private void resetHoverEffect(ImageView imageView, Shape hitbox) {
        if (imageView != null) {
            System.out.println("Restaurando tamaño de ImageView: " + imageView.getId());
            imageView.setScaleX(1.0);
            imageView.setScaleY(1.0);
        }
        if (hitbox != null) {
            System.out.println("Restaurando tamaño de Hitbox: " + hitbox.getId());
            hitbox.setScaleX(1.0);
            hitbox.setScaleY(1.0);
        }
    }

    private boolean collidesWith(Shape hitbox, double x, double y) {
        if (hitbox == null) return false;
        Circle tempPersonaje = new Circle(x, y, personajeRadius + PROXIMITY_MARGIN);
        Bounds personajeBounds = tempPersonaje.localToScene(tempPersonaje.getBoundsInLocal());
        Bounds hitboxBounds = hitbox.localToScene(hitbox.getBoundsInLocal());
        boolean collides = personajeBounds.intersects(hitboxBounds);
        if (collides) {
            System.out.println("Colisión detectada con hitbox: " + hitbox.getId() +
                               " (personaje bounds: x=" + personajeBounds.getMinX() + ", y=" + personajeBounds.getMinY() +
                               ", width=" + personajeBounds.getWidth() + ", height=" + personajeBounds.getHeight() +
                               "; hitbox bounds: x=" + hitboxBounds.getMinX() + ", y=" + hitboxBounds.getMinY() +
                               ", width=" + hitboxBounds.getWidth() + ", height=" + hitboxBounds.getHeight() + ")");
        }
        return collides;
    }

    private void checkInteraction() {
        double centerX = personaje.getLayoutX() + personajeRadius;
        double centerY = personaje.getLayoutY() + personajeRadius;
        if (collidesWith(historialEquiposHitbox, centerX, centerY)) {
            goTeamsHistory();
        } else if (collidesWith(gomainHitbox, centerX, centerY)) {
            goGomain();
        } else if (collidesWith(torneosHitbox, centerX, centerY)) {
            goTourneys();
        } else if (collidesWith(bibliotecaHitbox, centerX, centerY)) {
            goReviews();
        } else if (collidesWith(cafeteriaHitbox, centerX, centerY)) {
            goLobby();
        } else if (collidesWith(arbolHitbox1, centerX, centerY)) {
            mensaje.setText("Es solo un árbol... nada que hacer aquí.");
        }
    }

    private void goTeamsHistory() {
        FlowController.getInstance().goViewInStage("TeamsHistory", (Stage) rootPane.getScene().getWindow());
    }

    private void goGomain() {
        FlowController.getInstance().goMain();
    }

    private void goTourneys() {
        FlowController.getInstance().goViewInStage("ViewTourneys", (Stage) rootPane.getScene().getWindow());
    }

    private void goReviews() {
        FlowController.getInstance().goViewInStage("Review", (Stage) rootPane.getScene().getWindow());
    }

    private void goLobby() {
        FlowController.getInstance().goViewInStage("Lobby", (Stage) rootPane.getScene().getWindow());
    }
}
