package cr.ac.una.taskprogramll.model;

/** * * @author ashly */
public class MatchDetails {
    
    private String nameFirstTeam;
    private String nameSecondTeam;
    private int counterFirstTeamGoals;
    private int counterSecondTeamGoals;

    public MatchDetails(String nameFirstTeam, String nameSecondTeam, int counterFirstTeamGoals, int counterSecondTeamGoals) {
        this.nameFirstTeam = nameFirstTeam;
        this.nameSecondTeam = nameSecondTeam;
        this.counterFirstTeamGoals = counterFirstTeamGoals;
        this.counterSecondTeamGoals = counterSecondTeamGoals;
    }
    
    public String getNameFirstTeam() {
        return nameFirstTeam;
    }

    public void setNameFirstTeam(String nameFirstTeam) {
        this.nameFirstTeam = nameFirstTeam;
    }

    public String getNameSecondTeam() {
        return nameSecondTeam;
    }

    public void setNameSecondTeam(String nameSecondTeam) {
        this.nameSecondTeam = nameSecondTeam;
    }

    public int getCounterFirstTeamGoals() {
        return counterFirstTeamGoals;
    }

    public void setCounterFirstTeamGoals(int counterFirstTeamGoals) {
        this.counterFirstTeamGoals = counterFirstTeamGoals;
    }

    public int getCounterSecondTeamGoals() {
        return counterSecondTeamGoals;
    }

    public void setCounterSecondTeamGoals(int counterSecondTeamGoals) {
        this.counterSecondTeamGoals = counterSecondTeamGoals;
    }
    
    public Boolean isDraw() {
        if(counterFirstTeamGoals == counterSecondTeamGoals) return true;
        else return false;
    }
}
