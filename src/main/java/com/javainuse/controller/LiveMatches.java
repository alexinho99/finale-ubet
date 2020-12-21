package com.javainuse.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.Map;

public class LiveMatches {
    public Map<String, List<FootballEvent>> liveMatches;


    public LiveMatches(Map<String, List<FootballEvent>> liveMatches) {
        this.liveMatches = liveMatches;
    }

    public Map<String, List<FootballEvent>> getLiveMatches() {
        return liveMatches;
    }

    public void setLiveMatches(Map<String, List<FootballEvent>> liveMatches) {
        this.liveMatches = liveMatches;
    }

    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }
}
