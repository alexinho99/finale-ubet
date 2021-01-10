package com.javainuse.model;

import java.io.Serializable;

public class BetRequest implements Serializable {
    private Integer matchId;
    private Short team;
    private Double odd;
    private Double amount;

    public BetRequest() {

    }

    public BetRequest(Integer matchId, Short team, Double odd, Double amount) {
        this.matchId = matchId;
        this.team = team;
        this.odd = odd;
        this.amount = amount;
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

    public Double getOdd() {
        return odd;
    }

    public void setOdd(Double odd) {
        this.odd = odd;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
