package cr.ac.una.taskprogramll.model;

/** * * * @author ashly */
public class Game {

    private int continueIdFTeam;
    private int continueIdSTeam;
    private int continueIndexTeam;
    private int globalSize;
    private int currentRound;
    
    public Game() {}
    
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
}
