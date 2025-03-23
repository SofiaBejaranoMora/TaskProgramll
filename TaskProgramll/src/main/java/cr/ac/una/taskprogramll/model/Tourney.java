/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.taskprogramll.model;

import java.util.ArrayList;
import java.util.List;

public class Tourney {
    private int id;
    private int time;
    private Sport sportType;
    private List<Team> teamList;
    private List<Team> loosersList;

    public Tourney() {
        this.teamList = new ArrayList<>();
        this.loosersList = new ArrayList<>();
    }

    public Tourney(int id, int time, Sport sportType, List<Team> teamList) {
        this.id = id;
        this.time = time;
        this.sportType = sportType;
        this.teamList = (teamList == null) ? new ArrayList<>() : new ArrayList<>(teamList);
        this.loosersList = new ArrayList<>();
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getTime() {
        return time;
    }

    public Sport getSportType() {
        return sportType;
    }

    public List<Team> getTeamList() {
        return new ArrayList<>(teamList); // Devolver una copia para evitar modificaciones externas
    }

    public List<Team> getLoosersList() {
        return new ArrayList<>(loosersList); // Devolver una copia para evitar modificaciones externas
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void setSportType(Sport sportType) {
        this.sportType = sportType;
    }

    public void setTeamList(List<Team> teamList) {
    if (teamList == null) {
        this.teamList = new ArrayList<>();
    } else {
        this.teamList = new ArrayList<>(teamList);
    }
}
    // Métodos para gestionar equipos
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

    // Calcular el estado dinámicamente
    public String getState() {
        if (teamList.isEmpty() && !loosersList.isEmpty()) {
            return "Finalizado";
        } else if (loosersList.isEmpty() && !teamList.isEmpty()) {
            return "Sin Empezar";
        } else if (!teamList.isEmpty() && !loosersList.isEmpty()) {
            return "En Proceso";
        } else {
            return "Sin Configurar"; // Caso raro: ambas listas vacías
        }
    }

    // Obtener el ranking (el último perdedor es el subcampeón, etc.)
    public List<Team> getRanking() {
        List<Team> ranking = new ArrayList<>(loosersList);
        if (!teamList.isEmpty()) {
            ranking.addAll(teamList); // El último equipo en teamList es el ganador
        }
        return ranking;
    }

    @Override
    public String toString() {
        return "Tourney{" +
                "id=" + id +
                ", time=" + time +
                ", sportType=" + sportType +
                ", teamList=" + teamList +
                ", loosersList=" + loosersList +
                ", state=" + getState() +
                '}';
    }
}