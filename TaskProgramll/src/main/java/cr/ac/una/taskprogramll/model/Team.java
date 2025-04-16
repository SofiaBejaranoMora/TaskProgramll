/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.taskprogramll.model;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class Team {

    private String name;
    private String nameTeamImage;
    private int idSportType;
    private int draw;
    private int wins;
    private int goals;
    private int points;
    private boolean isQualified;
    private int id;

    public Team() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Team(String name, int idSportType, int id) {
        this.name = name;
        this.idSportType = idSportType;
        this.nameTeamImage = name + ".png";
        this.id = id;
        this.draw = 0;
        this.wins = 0;
        isQualified = false;
    }

    public Team(String name, String nameTeamImage, int idSportType, int draw, int wins, boolean isQualified, int id) {
        this.name = name;
        this.nameTeamImage = nameTeamImage + ".png";
        this.idSportType = idSportType;
        this.draw = draw;
        this.wins = wins;
        this.isQualified = isQualified;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameTeamImage() {
        return nameTeamImage;
    }

    public void setNameTeamImage(String nameTeamImage) {
        this.nameTeamImage = nameTeamImage;
    }

    public int getIdSportType() {
        return idSportType;
    }

    public void setIdSportType(int idSportType) {
        this.idSportType = idSportType;
    }

    public int getDraw() {
        return draw;
    }

    public void setDraw(int draw) {
        this.draw = draw;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins += wins;
    }

    public int getGoals() {
        return goals;
    }

    public void setGoals(int goals) {
        this.goals += goals;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points += points;
    }
    
    public boolean isIsQualified() {
        return isQualified;
    }

    public void setIsQualified(boolean isQualified) {
        this.isQualified = isQualified;
    }

    public String RuteImage() {
        return System.getProperty("user.dir") + "/src/main/resources/cr/ac/una/taskprogramll/resources/" + name + ".png";
    }

    public void ChangeName(String name) {
        String rute = System.getProperty("user.dir") + "/src/main/resources/cr/ac/una/taskprogramll/resources/";
        File imagenOriginal = new File(rute + this.name + ".png");
        File imagenNueva = new File(rute + name + ".png");
        imagenOriginal.renameTo(imagenNueva);
        this.name = name + ".png";
        this.nameTeamImage = name + ".png";
    }

    public Sport searchSportType() {
        FileManager fileManeger = new FileManager();
        File file = new File("Sport.txt");
        if ((file.exists()) && (file.length() > 0)) {
            List<Sport> sportList = fileManeger.deserialization("Sport", Sport.class);
            for (Sport currentSport : sportList) {
                if (currentSport.getId() == idSportType) {
                    return currentSport;
                }
            }
        }
        return null;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.name);
        hash = 97 * hash + this.idSportType;
        hash = 97 * hash + this.id;
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
        final Team other = (Team) obj;
        if (this.idSportType != other.idSportType) {
            return false;
        }
        if (this.id != other.id) {
            return false;
        }
        return Objects.equals(this.name, other.name);
    }
    
}
