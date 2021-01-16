package com.javainuse.helpers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.javainuse.dao.MatchDao;
import com.javainuse.dao.SoccerSiteDao;
import com.javainuse.model.MatchesEntity;
import com.javainuse.model.SoccerSiteEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MatchScraper {

    private MatchDao matchDao;
    private SoccerSiteDao soccerSiteDao;

    public MatchScraper (MatchDao matchDao, SoccerSiteDao soccerSiteDao) {
        this.matchDao = matchDao;
        this.soccerSiteDao = soccerSiteDao;
    }

    public MatchesEntity getUpdatedMatch(Integer matchId) throws IOException {
        MatchesEntity match = matchDao.findById(matchId).get();

        if (!match.isFinished()) {
            return match;
        } else if (match.isUpdated()) {
            return match;
        } else {
            URL urlLiveResult = new URL("https://api-amazon.xscores.com/m_matchresult?sport=1&match_id=" + match.getHref() +"&tz=Europe/Athens&priorityCountry=BULGARIA");
            HttpURLConnection conn = (HttpURLConnection) urlLiveResult.openConnection();

            conn.setRequestProperty("Authorization","Bearer " + soccerSiteDao.findTopByOrderByIdDesc().getAccessToken());

            conn.setRequestProperty("Content-Type","application/json");
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String output;

            StringBuffer response = new StringBuffer();
            while ((output = in.readLine()) != null) {
                response.append(output);
            }

            in.close();

            JsonObject jsonObject = new JsonParser().parse(response.toString()).getAsJsonObject();
            JsonElement findStatus = jsonObject.get("team");
            JsonObject rootObject = findStatus.getAsJsonObject();

            String statusType = rootObject.get("statusType").getAsString();
            String stringScore = rootObject.get("score").getAsString();
            String[] score = stringScore.split("-");

            match.setHomeScore(Integer.parseInt(score[0]));
            match.setAwayScore(Integer.parseInt(score[1]));
            match.setLiveResult(stringScore);

            if (statusType.equals("fin")) {
                match.setFinished(true);
                match.setUpdated(true);
            } else {
                match.setFinished(false);
            }

            matchDao.save(match);

            return match;
        }
    }
}
