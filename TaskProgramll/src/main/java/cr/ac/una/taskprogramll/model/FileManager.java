/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.taskprogramll.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileManager {

    public <T> void serialization(List<T> list, String filename) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(new File(filename + ".txt"), list);
            System.out.println("Lista guardada exitosamente en " + filename);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al guardar la lista en el archivo.");
        }
    }
    
    public <T> List<T> deserialization(String filename,Class<T> classType) {
        List<T> list = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            list = objectMapper.readValue(new File(filename + ".txt"), objectMapper.getTypeFactory().constructCollectionType(List.class, classType));
            System.out.println("Lista leída exitosamente desde " + filename);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al leer la lista desde el archivo.");
        }
        return list;
    }
}
