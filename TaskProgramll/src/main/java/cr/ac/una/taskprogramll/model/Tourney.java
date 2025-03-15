/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cr.ac.una.taskprogramll.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Michelle Wittingham
 */
public class Tourney {
    private int id;
    private int time;
    private Sport sportype;
    private List<Team> teamList;
    
    public Tourney(){
        this.teamList = new ArrayList<>();
    }
    public Tourney(int id, int time, Sport sportype, List<Team> teamList){
        this.id=id;//size+1 
        this.time=time;
        this.sportype=sportype;
        if(teamList==null){
            this.teamList=new ArrayList<>();
        }
    }
    public int  getId(){
        return id;
    }
    public int getTime(){
        return time;
    }
    public Sport getSportType(){
        return sportype;
    }
    public List<Team> getTeamList(){
        return teamList;
    }
    
    public void setId(int id){
        this.id=id;
    }

public void setTime(int time){
    this.time=time;
}    

public void setSportType(Sport sportType){
    this.sportype=sportType;
}

public void setTeamList(List<Team>teamList){
    this.teamList=teamList;
}

public void addTeam(Team team) {
        if (!this.teamList.contains(team)) {
            this.teamList.add(team);
        }
    }

    public void removeTeam(Team team) {
        this.teamList.remove(team);
    }
    
    //por cuestiones de debug
    @Override
    public String toString(){
         return "Tourney{" +
                "id=" + id +
                ", time='" + time + '\'' +
                ", sportype=" + sportype +
                ", teamList=" + teamList +
                '}';
    }
    
}
