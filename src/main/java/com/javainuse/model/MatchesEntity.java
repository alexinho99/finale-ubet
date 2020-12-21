package com.javainuse.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "UpcomingMatches")
public class MatchesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String homeTeam;
    @Column
    private String awayTeam;
    @Column
    private double firstTeamToWin;
    @Column
    private double draw;
    @Column
    private double secondTeamToWin;
    @Column
    private int href;
    @Column
    private int homeScore;
    @Column
    private int awayScore;
    @Column
    private String liveScore;
    @Column
    private boolean finished;
    @Column
    private boolean updated;

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }


    public String getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
    }

    public double getFirstTeamToWin() {
        return firstTeamToWin;
    }

    public void setFirstTeamToWin(double firstTeamToWin) {
        this.firstTeamToWin = firstTeamToWin;
    }

    public double getDraw() {
        return draw;
    }

    public void setDraw(double draw) {
        this.draw = draw;
    }

    public double getSecondTeamToWin() {
        return secondTeamToWin;
    }

    public void setSecondTeamToWin(double secondTeamToWin) {
        this.secondTeamToWin = secondTeamToWin;
    }

    public int getHref() {
        return href;
    }

    public void setHref(int href) {
        this.href = href;
    }

    public void setHomeScore(int homeScore) {
        this.homeScore = homeScore;
    }

    public void setAwayScore(int awayScore) {
        this.awayScore = awayScore;
    }

    public int getAwayScore() {
        return awayScore;
    }

    public int getHomeScore() {
        return homeScore;
    }

    public String getLiveResult() {
        return liveScore;
    }

    public void setLiveResult(String liveScore) {
        this.liveScore=liveScore;
    }
}
