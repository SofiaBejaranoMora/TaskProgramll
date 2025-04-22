/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.taskprogramll.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Team {

    private String name;
    private int idSportType;
    private int draw;
    private int wins;
    private int goals;
    private int points;
    private int id;
    private List<MatchDetails> encounterList;
    private List<Review> reviewList;

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
        this.id = id;
        this.draw = 0;
        this.wins = 0;
        encounterList = new ArrayList<>();
        reviewList = new ArrayList<>();
    }

    public Team(String name, int idSportType, int draw, int wins, int id) {
        this.name = name;
        this.idSportType = idSportType;
        this.draw = draw;
        this.wins = wins;
        this.id = id;
        encounterList = new ArrayList<>();
        reviewList = new ArrayList<>();
    }

    public List<Review> getReviewList() {
        return reviewList;
    }

    public void setReviewList(List<Review> reviewList) {
        this.reviewList = reviewList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public List<MatchDetails> getEncounterList() {
        return encounterList;
    }

    public void setItemEncounterList(MatchDetails encounterList) {
        this.encounterList.add(encounterList);
    }

    public String RuteImage() {
        return System.getProperty("user.dir") + "/src/main/resources/cr/ac/una/taskprogramll/resources/" + id + ".png";
    }

    public float AverageGrade() {
        float result = 0;
        if ((reviewList !=null) && (!reviewList.isEmpty())) {
            for (Review currentReview : reviewList) {
                result+=currentReview.getScore();
            }
            result/=reviewList.size();
        }
        return result;
    }

    @JsonIgnore
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
