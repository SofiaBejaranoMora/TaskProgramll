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
    private String name; 
    private int time;
    private int sportTypeId;
    private Game continueGame;
    private List<Team> teamList;
    private List<Team> loosersList;
    private List<Team> round1;
    private List<Team> round2;
    private List<Team> round3;
    private List<Team> round4;
    private List<Team> round5;
    private List<Team> round6;
    private List<Team> winner;

    public Tourney() {
        this.teamList = new ArrayList<>();
        this.loosersList = new ArrayList<>();
        this.continueGame = new Game();
        this.round1 = new ArrayList<>();
        this.round2 = new ArrayList<>();
        this.round3 = new ArrayList<>();
        this.round4 = new ArrayList<>();
        this.round5 = new ArrayList<>();
        this.round6 = new ArrayList<>();
        this.winner = new ArrayList<>();
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
        this.round1 = new ArrayList<>();
        this.round2 = new ArrayList<>();
        this.round3 = new ArrayList<>();
        this.round4 = new ArrayList<>();
        this.round5 = new ArrayList<>();
        this.round6 = new ArrayList<>();
        this.winner = new ArrayList<>();
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

     public void setItems(List<Team> round1) {
        this.round1 = round1;
    }

    public List<Team> getRound1() {
        return round1;
    }

    public void setRound1(List<Team> round1) {
        this.round1 = round1;
    }

    public List<Team> getRound2() {
        return round2;
    }

    public void setRound2(List<Team> round2) {
        this.round2 = round2;
    }

    public List<Team> getRound3() {
        return round3;
    }

    public void setRound3(List<Team> round3) {
        this.round3 = round3;
    }

    public List<Team> getRound4() {
        return round4;
    }

    public void setRound4(List<Team> round4) {
        this.round4 = round4;
    }

    public List<Team> getRound5() {
        return round5;
    }

    public void setRound5(List<Team> round5) {
        this.round5 = round5;
    }

    public List<Team> getRound6() {
        return round6;
    }

    public void setRound6(List<Team> round6) {
        this.round6 = round6;
    }

    public List<Team> getWinner() {
        return winner;
    }

    public void setWinner(List<Team> winner) {
        this.winner = winner;
    }
    
    public void addToRound(Team passTeam, int numberRound) {
        switch (numberRound) {
            case 1 -> {
                round2.add(passTeam);
            }
            case 2 -> {
                round3.add(passTeam);
            }
            case 3 -> {
                round4.add(passTeam);
            }
            case 4 -> {
                round5.add(passTeam);
            }
            case 5 -> {
                round6.add(passTeam);
            }
            case 6 -> {
                winner.add(passTeam);
            }
            default ->
                throw new AssertionError();
        }
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

    public void setTeamList(List<Team> currentTeamList) {
        FileManager fileManager = new FileManager();

        if (currentTeamList == null) {
            this.teamList = new ArrayList<>();
            try {
                fileManager.serialization(this.teamList, "Teams");
            } catch (Exception e) {
                System.out.println("Error al serializar teamList (null): " + e.getMessage());
            }
            return;
        }
        List<Team> newTeamList = new ArrayList<>();

        for (Team newTeam : currentTeamList) {
            if (newTeam == null || newTeam.getId() <= 0) {
                continue;
            }

            boolean found = false;
            for (int i = 0; i < this.teamList.size(); i++) {
                Team existingTeam = this.teamList.get(i);
                if (existingTeam != null && existingTeam.getId() > 0 && existingTeam.getId() == newTeam.getId()) {
                    if(existingTeam.getId()!=newTeam.getId()){
                    this.teamList.set(i, newTeam);
                }
                    found = true;
                    break;
                }
            }
            if (!found) {
                newTeamList.add(newTeam);
            }
        }
        this.teamList.addAll(newTeamList);
        this.teamList.removeIf(existingTeam
                -> existingTeam != null
                && existingTeam.getId() > 0
                && currentTeamList.stream().noneMatch(newTeam
                        -> newTeam != null
                && newTeam.getId() > 0
                && newTeam.getId() == existingTeam.getId()
                )
        );
        this.teamList.removeIf(team -> team != null && loosersList.contains(team));
        try {
            fileManager.serialization(this.teamList, "Teams");
        } catch (Exception e) {
            System.out.println("Error al serializar teamList: " + e.getMessage());
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
            if (winner != null && winner.equals(teamList.get(i).getName())) {
                teamList.get(i).setWins(1);
                teamList.get(i).setGoals(winnerGoals);
                teamList.get(i).setPoints(winnerGoals + extraPoints);
            } else {
                System.out.println("No es este.");
            }
        }
        for (int i = 0; i < teamList.size(); i++) {
            if (looser != null && looser.equals(teamList.get(i).getName())) {
                teamList.get(i).setGoals(looserGoals);
                teamList.get(i).setPoints(looserGoals);
                moveTeamToLoosers(teamList.get(i));
            } else {
                System.out.println("No es este.");
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