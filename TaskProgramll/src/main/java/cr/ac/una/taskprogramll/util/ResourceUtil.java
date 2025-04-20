package cr.ac.una.taskprogramll.util;

/** * * @author Michelle Wittingham */
public class ResourceUtil {
    
    private static final String BASE_PATH = "/cr/ac/una/taskprogramll/resources/";
    /**
     * Devuelve la URL de una imagen en src/main/resources/cr/ac/una/taskprogramll/resources/.
     * @param imageName Nombre del archivo (e.g., "stillBack.png").
     * @return URL como String para Image, o null si no se encuentra.
     */
    public static String getImagePath(int id) {
        try {
            String path = BASE_PATH + id + ".png";
            if (ResourceUtil.class.getResource(path) == null) {
                System.err.println("No se encontró la imagen: " + path);
                return null;
            }
            return ResourceUtil.class.getResource(path).toExternalForm();
        } catch (Exception e) {
            System.err.println("Error cargando imagen " + id + ": " + e.getMessage());
            return null;
        }
    }
    
}
