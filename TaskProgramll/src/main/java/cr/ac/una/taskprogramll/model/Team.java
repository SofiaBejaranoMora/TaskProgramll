/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.taskprogramll.model;

public class Team {
    private String name;
    private String nameTeamImage;
    private Sport sportType;
    private int draw;
    private int wins;
    private boolean isQualified;

    public Team() {
    }

    public Team(String name, String nameTeamImage, int draw, int wins, boolean isQualified) {
        this.name = name;
        this.nameTeamImage = nameTeamImage;
        this.draw = draw;
        this.wins = wins;
        this.isQualified = isQualified;
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
        this.wins = wins;
    }

    public boolean isIsQualified() {
        return isQualified;
    }

    public void setIsQualified(boolean isQualified) {
        this.isQualified = isQualified;
    }


    
}
