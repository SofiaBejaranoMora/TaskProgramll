package cr.ac.una.taskprogramll.model;

import java.util.List;

/** * * * @author ashly */
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
    
    public Game() {}
    
    public Game(List<Team> initializer) {
        this.round1 = initializer;
        this.round2 = initializer;
        this.round3 = initializer;
        this.round4 = initializer;
        this.round5 = initializer;
        this.round6 = initializer;
        this.winner = initializer;
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
}
