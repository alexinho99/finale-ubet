package com.javainuse.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.Map;

public class UpcomingMatches {
    public Map<String, List<FootballEvent>> upcomingMatches;


    public UpcomingMatches(Map<String, List<FootballEvent>> upcomingMatches) {
        this.upcomingMatches = upcomingMatches;
    }

    public Map<String, List<FootballEvent>> getUpcomingMatches() {
        return upcomingMatches;
    }

    public void setUpcomingMatches(Map<String, List<FootballEvent>> upcomingMatches) {
        this.upcomingMatches = upcomingMatches;
    }

    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }
}
