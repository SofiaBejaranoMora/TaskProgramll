/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.taskprogramll.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Tourney {

    private int id;
    private String name; // Nuevo campo
    private int time;
    private int sportTypeId;
    private Game continueGame;
    private List<Team> teamList;
    private List<Team> loosersList;

    public Tourney() {
        this.teamList = new ArrayList<>();
        this.loosersList = new ArrayList<>();
        this.continueGame = new Game();
    }

    public Tourney(int id, String name, int time, int sportType, List<Team> teamList) {
        this.id = id;
        if (name != null) {
            this.name = name;
        } else {
            this.name = "";
        }

        // Validación para time (no negativo)
        if (time < 0) {
            this.time = 0;
        } else {
            this.time = time;
        }

        // Validación para sportTypeId (no negativo)
        if (sportType < 0) {
            this.sportTypeId = 0;
        } else {
            this.sportTypeId = sportType;
        }

        // Inicialización segura de listas
        this.teamList = new ArrayList<>();
        this.loosersList = new ArrayList<>();

        // Agregar equipos de manera segura
        if (teamList != null) {
            for (Team team : teamList) {
                if (team != null) {
                    this.teamList.add(team);
                }
            }
        }

        this.continueGame = new Game();
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getTime() {
        return time;
    }

    public int getSportTypeId() {
        return sportTypeId;
    }

    public Game getContinueGame() {
        return continueGame;
    }

    public List<Team> getTeamList() {
        return new ArrayList<>(teamList);
    }

    public List<Team> getLoosersList() {
        return new ArrayList<>(loosersList);
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void setSportTypeId(int sportType) {
        this.sportTypeId = sportType;
    }

    public void setTeamList(List<Team> teamList) {
        if (teamList == null) {
            this.teamList = new ArrayList<>();
        } else {
            this.teamList = new ArrayList<>(teamList);
        }
    }

    public void addTeam(Team team) {
        if (team == null) {
            return;
        }
        if (!teamList.contains(team) && !loosersList.contains(team)) {
            teamList.add(team);
        }
    }

    public void removeTeam(Team team) {
        if (team != null) {
            teamList.remove(team);
        }
    }

    public void moveTeamToLoosers(Team team) {
        if (team == null) {
            return;
        }
        if (teamList.contains(team) && !loosersList.contains(team)) {
            teamList.remove(team);
            loosersList.add(team);
        }
    }

    public void winnerAndLooser(String winner, int winnerGoals, int extraPoints, String looser, int looserGoals) {
        for (int i = 0; i < teamList.size(); i++) {
            if (winner == null && teamList.get(i).getName() == null) {
                teamList.get(i).setWins(1);
                teamList.get(i).setGoals(winnerGoals);
                teamList.get(i).setPoints(winnerGoals + extraPoints);
            } else if (winner != null && winner.equals(teamList.get(i).getName())) {
                teamList.get(i).setWins(1);
                teamList.get(i).setGoals(winnerGoals);
                teamList.get(i).setPoints(winnerGoals + extraPoints);
            }

            if (looser == null && teamList.get(i).getName() == null) {
                teamList.get(i).setGoals(looserGoals);
                teamList.get(i).setPoints(looserGoals);
                moveTeamToLoosers(teamList.get(i));
            } else if (looser != null && looser.equals(teamList.get(i).getName())) {
                teamList.get(i).setGoals(looserGoals);
                teamList.get(i).setPoints(looserGoals);
                moveTeamToLoosers(teamList.get(i));
            }
        }
    }

    public String returnState() {
        if (teamList.isEmpty() && !loosersList.isEmpty()) {
            return "Finalizado";
        } else if (loosersList.isEmpty() && !teamList.isEmpty()) {
            return "Sin Empezar";
        } else if (!teamList.isEmpty() && !loosersList.isEmpty()) {
            return "En Proceso";
        } else {
            return "Sin Configurar";
        }
    }

    public List<Team> returnRanking() {
        List<Team> ranking = new ArrayList<>(loosersList);
        if (!teamList.isEmpty()) {
            ranking.addAll(teamList);
        }
        return ranking;
    }

    @Override
    public String toString() {
        return "Tourney{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", time=" + time
                + ", sportType=" + sportTypeId
                + ", teamList=" + teamList
                + ", loosersList=" + loosersList
                + ", state=" + returnState()
                + '}';
    }

    @JsonIgnore
    public Sport searchSportType() {
        FileManager fileManager = new FileManager();
        File file = new File("Sport.txt");

        if (file.exists() && file.length() > 0) {
            List<Sport> sportList = fileManager.deserialization("Sport", Sport.class);
            for (Sport currentSport : sportList) {
                if (currentSport.getId() == this.sportTypeId) {
                    return currentSport;
                }
            }
        }
        return null;
    }
    
    public String returnTeamPosition(Team team) {
        if (team == null || (!teamList.contains(team) && !loosersList.contains(team))) {
            return "No participa";
        }
        if (teamList.contains(team)) {
            if (teamList.size() == 1 && loosersList.size() > 0) {
                return "Posición: 1 (Ganador)";
            }
            return "En competencia";
        }
        List<Team> ranking = new ArrayList<>(loosersList);
        int position = ranking.size() - ranking.indexOf(team);
        return "Posición: " + position;
    }
}
