package cr.ac.una.taskprogramll.model;

import java.util.ArrayList;
import java.util.List;

/** * * @author ashly*/
public class Game {
    
    private int continueTeam;
    private int remainingTime;
    private int goalsTeam1;
    private int goalsTeam2;
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

    public int getContinueTeam() {
        return continueTeam;
    }

    public void setContinueTeam(int continueTeam) {
        this.continueTeam = continueTeam;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public int getGoalsTeam1() {
        return goalsTeam1;
    }

    public void setGoalsTeam1(int goalsTeam1) {
        this.goalsTeam1 = goalsTeam1;
    }

    public int getGoalsTeam2() {
        return goalsTeam2;
    }

    public void setGoalsTeam2(int goalsTeam2) {
        this.goalsTeam2 = goalsTeam2;
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
                round1.add(passTeam);
            }
            case 2 -> {
                round2.add(passTeam);
            }
            case 3 -> {
                round3.add(passTeam);
            }
            case 4 -> {
                round4.add(passTeam);
            }
            case 5 -> {
                round5.add(passTeam);
            }
            case 6 -> {
                round6.add(passTeam);
            }
            case 7 -> {
                winner.add(passTeam);
            }
            default -> throw new AssertionError();
        }
    }
}
