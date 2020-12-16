package com.javainuse.controller;

public class FootballEvent {

    private String homeTeam;
    private String awayTeam;
    private double firstTeamToWin;
    private double draw;
    private double secondTeamToWin;
    private String href;

    public FootballEvent() {

    }

    public FootballEvent(String homeTeam, String awayTeam, double firstTeamToWin, double draw, double secondTeamToWin, String href) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.firstTeamToWin = firstTeamToWin;
        this.draw = draw;
        this.secondTeamToWin = secondTeamToWin;
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

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}
