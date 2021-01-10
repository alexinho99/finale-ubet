package com.javainuse.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class FootballEvent {

    private long id;
    private String homeTeam;
    private String awayTeam;
    private double homeOdd;
    private double draw;
    private double awayOdd;
    private String liveResult;
    private int homeScore;
    private int href;

    public FootballEvent() {

    }

    public FootballEvent(String homeTeam, String awayTeam, double firstTeamToWin, double draw, double secondTeamToWin, String liveResult, int homeScore, int awayScore, int href) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeOdd = firstTeamToWin;
        this.draw = draw;
        this.awayOdd = secondTeamToWin;
        this.liveResult = liveResult;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.href = href;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public int getHref() {
        return href;
    }

    public void setHref(int href) {
        this.href = href;
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

    public double getHomeOdd() {
        return homeOdd;
    }

    public void setHomeOdd(double homeOdd) {
        this.homeOdd = homeOdd;
    }

    public double getDraw() {
        return draw;
    }

    public void setDraw(double draw) {
        this.draw = draw;
    }

    public double getAwayOdd() {
        return awayOdd;
    }

    public void setAwayOdd(double awayOdd) {
        this.awayOdd = awayOdd;
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
