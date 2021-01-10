package com.javainuse.model;

import javax.persistence.*;

@Entity
@Table(name = "bets")
public class BetEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private Integer userId;

    @Column
    private Integer matchId;

    @Column
    private Short team;

    @Column
    private Double amount;

    @Column
    private Double odd;

    public long getId() {
        return id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getMatchId() {
        return matchId;
    }

    public void setMatchId(Integer matchId) {
        this.matchId = matchId;
    }

    public Short getTeam() {
        return team;
    }

    public void setTeam(Short team) {
        this.team = team;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getOdd() {
        return odd;
    }

    public void setOdd(Double odd) {
        this.odd = odd;
    }
}
