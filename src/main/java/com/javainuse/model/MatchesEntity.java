package com.javainuse.model;

import javax.persistence.*;

@Entity
@Table(name = "Matches")
public class MatchesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column
    private String homeTeam;
    @Column
    private String awayTeam;
    @Column
    private double homeOdd;
    @Column
    private double draw;
    @Column
    private double awayOdd;
    @Column
    private int href;
    @Column
    private int homeScore;
    @Column
    private int awayScore;
    @Column
    private String liveScore;

    public Integer getId() {
        return id;
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
