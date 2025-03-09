/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.taskprogramll.model;

public class Sport {

    private String name;
    private String nameBallImage;

    public Sport() {
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

}
