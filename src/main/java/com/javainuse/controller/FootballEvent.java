package com.javainuse.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class FootballEvent {

    private String homeTeam;
    private String awayTeam;
    private double firstTeamToWin;
    private double draw;
    private double secondTeamToWin;
    private String liveResult;
    private int homeScore;
    private int href;
    private boolean finished;
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

    public int getAwayScore() {
        return awayScore;
    }

    public void setAwayScore(int awayScore) {
        this.awayScore = awayScore;
    }

    private int awayScore;

    public int getHomeScore() {
        return homeScore;
    }

    public void setHomeScore(int homeScore) {
        this.homeScore = homeScore;
    }



    public FootballEvent() {

    }

    public int getHref() {
        return href;
    }

    public void setHref(int href) {
        this.href = href;
    }

    public FootballEvent(String homeTeam, String awayTeam, double firstTeamToWin, double draw, double secondTeamToWin, String liveResult, int homeScore, int awayScore, int href
    , boolean updated, boolean finished) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.firstTeamToWin = firstTeamToWin;
        this.draw = draw;
        this.secondTeamToWin = secondTeamToWin;
        this.liveResult = liveResult;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.href = href;
        this.updated = updated;
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

    public String getLiveResult() {
        return liveResult;
    }

    public void setLiveResult(String liveResult) {
        this.liveResult = liveResult;
    }

    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }

}
