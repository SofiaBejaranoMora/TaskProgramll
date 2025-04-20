package cr.ac.una.taskprogramll.model;

import java.util.ArrayList;
import java.util.List;

/** * * @author ashly*/
public class Game {
    
    private int continueIdFTeam;
    private int continueIdSTeam;
    private int continueIndexTeam;
    private int globalSize;
    private int currentRound;
    private List<Team> round1;
    private List<Team> round2;
    private List<Team> round3;
    private List<Team> round4;
    private List<Team> round5;
    private List<Team> round6;
    private List<Team> winner;

    public Game() {
        this.round1 = new ArrayList<>();
        this.round2 = new ArrayList<>();
        this.round3 = new ArrayList<>();
        this.round4 = new ArrayList<>();
        this.round5 = new ArrayList<>();
        this.round6 = new ArrayList<>();
        this.winner = new ArrayList<>();
    }

    public int getContinueIdFTeam() {
        return continueIdFTeam;
    }

    public void setContinueIdFTeam(int continueIdFTeam) {
        this.continueIdFTeam = continueIdFTeam;
    }

    public int getContinueIdSTeam() {
        return continueIdSTeam;
    }

    public void setContinueIdSTeam(int continueIdSTeam) {
        this.continueIdSTeam = continueIdSTeam;
    }

    public int getContinueIndexTeam() {
        return continueIndexTeam;
    }

    public void setContinueIndexTeam(int continueIndexTeam) {
        this.continueIndexTeam = continueIndexTeam;
    }

    public int getGlobalSize() {
        return globalSize;
    }

    public void setGlobalSize(int globalSize) {
        this.globalSize = globalSize;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }
    
    public void setItems(List<Team> round1){
        this.round1 = round1;
    }
    
    public List<Team> getRound1() {
        return round1;
    }
    
    public List<Team> getRound2() {
        return round2;
    }

    public List<Team> getRound3() {
        return round3;
    }

    public List<Team> getRound4() {
        return round4;
    }

    public List<Team> getRound5() {
        return round5;
    }

    public List<Team> getRound6() {
        return round6;
    }

    public List<Team> getWinner() {
        return winner;
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
            default -> throw new AssertionError();
        }
    }
}
