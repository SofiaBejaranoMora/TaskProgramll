/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.taskprogramll.model;

import java.io.File;
import java.util.Objects;

public class Sport {

    private String name;
    private String nameBallImage;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Sport() {
    }

    public Sport(String name, int id) {
        this.name = name;
        this.nameBallImage = name;
        this.id=id;
    }

    public Sport(String name, String nameBallImage) {
        this.name = name;
        this.nameBallImage = nameBallImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameBallImage() {
        return nameBallImage;
    }

    public void setNameBallImage(String nameBallImage) {
        this.nameBallImage = nameBallImage;
    }

    public String RuteImage() {
        return System.getProperty("user.dir") + "/src/main/resources/cr/ac/una/taskprogramll/resources/"+name+".png";
    }
    
    public void ChangeName(String name) {
        String rute = System.getProperty("user.dir") + "/src/main/resources/cr/ac/una/taskprogramll/resources/";
        File imagenOriginal = new File(rute + this.name + ".png");
        File imagenNueva = new File(rute + name + ".png");
        imagenOriginal.renameTo(imagenNueva);
        this.name = name;
        this.nameBallImage = name;
    }
    
    @Override
    public String toString(){
        return name;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.name);
        hash = 53 * hash + Objects.hashCode(this.nameBallImage);
        hash = 53 * hash + this.id;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Sport other = (Sport) obj;
        if (this.id != other.id) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return Objects.equals(this.nameBallImage, other.nameBallImage);
    }
 
}
