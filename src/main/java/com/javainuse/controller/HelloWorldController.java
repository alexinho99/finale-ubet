package com.javainuse.controller;

import com.google.gson.*;
import com.javainuse.dao.MatchDao;
import com.javainuse.dao.SoccerSiteDao;
import com.javainuse.helpers.MatchScraper;
import com.javainuse.model.MatchesEntity;
import com.javainuse.model.SoccerSiteEntity;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.*;

@RestController
public class HelloWorldController {
	List<FootballEvent> upComingEvents;
	List<FootballEvent> liveEvents;

	@Autowired
	private MatchDao matchDao;

	@Autowired
	private SoccerSiteDao soccerSiteDao;

	@RequestMapping(value = "/match/{id}" , method = RequestMethod.GET)
	public String getSingleMatch(@PathVariable("id") Integer id) throws IOException {

		MatchScraper matchScraper = new MatchScraper(matchDao, soccerSiteDao);

		MatchesEntity match = matchScraper.getUpdatedMatch(id);

		return printObjecto(match);
	}

	@RequestMapping(value = "/matches", method = RequestMethod.GET)
	public String firstPage() throws IOException, SQLException {

		String result = "";

		upComingEvents = new ArrayList<>();
		liveEvents = new ArrayList<>();

		String url = "https://www.xscores.com/soccer";
		Document document = Jsoup.connect(url).get();

		Elements elements = document.select("#menuWrapper > script");

		String accessToken = "";
		for(Element element : elements) {
			for(Node child : element.childNodes()) {
				String token = child.toString();
				String[] arr = token.split("access_token");
				String[] tokenArr = arr[1].split("\"");
				accessToken = tokenArr[1];
				break;
			}
		}

		SoccerSiteEntity soccerSite = soccerSiteDao.findTopByOrderByIdDesc();
		if (soccerSite == null) {
			soccerSite = new SoccerSiteEntity();
		}
		soccerSite.setAccessToken(accessToken);
		soccerSiteDao.save(soccerSite);

		Elements rowLiveMatches = document.getElementsByClass("live_match");
		for (Element rowMatch : rowLiveMatches) {
			Elements odds = rowMatch.getElementsByClass("odds");

			String homeOddString = odds.first().child(0).text();

			if (homeOddString.equals("")) {
				continue;
			}

			int href;
			String homeTeam;
			String awayTeam;
			double homeOdd;
			double awayOdd;
			double drawOdd;
			int homeScore;
			int awayScore;
			String liveResult;

			try {
				String hrefString = rowMatch.attr("href");
				String[] hrefArr = hrefString.split("/");

				href = Integer.parseInt(hrefArr[hrefArr.length-1]);
				homeTeam = rowMatch.getElementsByClass("score_home_txt").first().text();
				awayTeam = rowMatch.getElementsByClass("score_away_txt").first().text();
				homeOdd = Double.parseDouble(homeOddString);
				awayOdd = Double.parseDouble(odds.first().child(2).text());
				drawOdd = Double.parseDouble(odds.first().child(1).text());
				homeScore = Integer.parseInt(rowMatch.getElementsByClass("scoreh_ft").first().text());
				awayScore = Integer.parseInt(rowMatch.getElementsByClass("scorea_ft").first().text());
				liveResult = rowMatch.getElementsByClass("score_score").text();
			} catch (Exception e) {
				continue;
			}

			FootballEvent event = new FootballEvent();
			event.setHref(href);
			event.setHomeTeam(homeTeam);
			event.setAwayTeam(awayTeam);
			event.setHomeOdd(homeOdd);
			event.setAwayOdd(awayOdd);
			event.setDraw(drawOdd);
			event.setHomeScore(homeScore);
			event.setAwayScore(awayScore);
			event.setLiveResult(liveResult);

			liveEvents.add(event);
		}

		Elements rowUpcomingMatches = document.getElementsByAttributeValueMatching("data-game-status", "Sched");
		for (Element rowMatch : rowUpcomingMatches) {
			Elements odds = rowMatch.getElementsByClass("odds");

			String homeOddString = odds.first().child(0).text();

			if (homeOddString.equals("")) {
				continue;
			}

			int href;
			String homeTeam;
			String awayTeam;
			double homeOdd;
			double awayOdd;
			double drawOdd;
			int homeScore;
			int awayScore;
			String liveResult;

			try {
				String hrefString = rowMatch.attr("href");
				String[] hrefArr = hrefString.split("/");

				href = Integer.parseInt(hrefArr[hrefArr.length-1]);
				homeTeam = rowMatch.getElementsByClass("score_home_txt").first().text();
				awayTeam = rowMatch.getElementsByClass("score_away_txt").first().text();
				homeOdd = Double.parseDouble(homeOddString);
				awayOdd = Double.parseDouble(odds.first().child(2).text());
				drawOdd = Double.parseDouble(odds.first().child(1).text());
				homeScore = 0;
				awayScore = 0;
				liveResult = "-";
			} catch (Exception e) {
				continue;
			}

			FootballEvent event = new FootballEvent();
			event.setHref(href);
			event.setHomeTeam(homeTeam);
			event.setAwayTeam(awayTeam);
			event.setHomeOdd(homeOdd);
			event.setAwayOdd(awayOdd);
			event.setDraw(drawOdd);
			event.setHomeScore(homeScore);
			event.setAwayScore(awayScore);
			event.setLiveResult(liveResult);

			upComingEvents.add(event);
		}

		matchDao.setAllAsFinished();

		Map<String, List<FootballEvent>> matchesMap = new HashMap<>();
		List<FootballEvent> finalUpComingMatches = new ArrayList<>();

		//za choveka koito chete tova, mnogo se izvinqvam ot imeto na kolega ama neshto ne mu se poluchavat rabotite sry
		if (upComingEvents != null) {
			for (FootballEvent event : upComingEvents) {
				if (event.getHomeTeam() != null && event.getAwayOdd() != 0.00 && event.getHomeOdd() != 0.00 && event.getDraw() != 0.00) {
					MatchesEntity upcomingMatch = matchDao.findByHref(event.getHref());
					if (upcomingMatch == null) {
						MatchesEntity matchesEntity = new MatchesEntity();
						matchesEntity.setHomeTeam(event.getHomeTeam());
						matchesEntity.setAwayTeam(event.getAwayTeam());
						matchesEntity.setHomeOdd(event.getHomeOdd());
						matchesEntity.setDraw(event.getDraw());
						matchesEntity.setAwayOdd(event.getAwayOdd());
						matchesEntity.setHref(event.getHref());
						matchesEntity.setAwayScore(event.getAwayScore());
						matchesEntity.setHomeScore(event.getHomeScore());
						matchesEntity.setLiveResult(event.getLiveResult());
						matchesEntity = matchDao.save(matchesEntity);
						event.setId(matchesEntity.getId());
					} else {
						event.setId(upcomingMatch.getId());
						upcomingMatch.setHomeOdd(event.getHomeOdd());
						upcomingMatch.setDraw(event.getDraw());
						upcomingMatch.setAwayOdd(event.getAwayOdd());
						upcomingMatch.setAwayScore(event.getAwayScore());
						upcomingMatch.setHomeScore(event.getHomeScore());
						upcomingMatch.setLiveResult(event.getLiveResult());
						upcomingMatch.setFinished(false);
						matchDao.save(upcomingMatch);
					}

					finalUpComingMatches.add(event);
					if(finalUpComingMatches.size() == 8) {
						break;
					}
				}
			}
			matchesMap.put("upcoming", finalUpComingMatches);
		}

		List<FootballEvent> finalLiveMatches = new ArrayList<>();

		System.out.println("liveEvents ---------- " + liveEvents.size());

		if (liveEvents != null) {
			for (FootballEvent event : liveEvents) {
				if (event.getHomeTeam() != null && event.getAwayOdd() != 0.00 && event.getHomeOdd() != 0.00 && event.getDraw() != 0.00) {
					MatchesEntity liveMatch = matchDao.findByHref(event.getHref());
					if (liveMatch == null) {
						MatchesEntity matchesEntity = new MatchesEntity();
						matchesEntity.setHomeTeam(event.getHomeTeam());
						matchesEntity.setAwayTeam(event.getAwayTeam());
						matchesEntity.setHomeOdd(event.getHomeOdd());
						matchesEntity.setDraw(event.getDraw());
						matchesEntity.setAwayOdd(event.getAwayOdd());
						matchesEntity.setHref(event.getHref());
						matchesEntity.setAwayScore(event.getAwayScore());
						matchesEntity.setHomeScore(event.getHomeScore());
						matchesEntity.setLiveResult(event.getLiveResult());
						matchesEntity = matchDao.save(matchesEntity);
						event.setId(matchesEntity.getId());
					} else {
						event.setId(liveMatch.getId());
						liveMatch.setHomeOdd(event.getHomeOdd());
						liveMatch.setDraw(event.getDraw());
						liveMatch.setAwayOdd(event.getAwayOdd());
						liveMatch.setAwayScore(event.getAwayScore());
						liveMatch.setHomeScore(event.getHomeScore());
						liveMatch.setLiveResult(event.getLiveResult());
						liveMatch.setFinished(false);
						matchDao.save(liveMatch);
					}
					System.out.println("team 1 - " + event.getHomeTeam());
					finalLiveMatches.add(event);
				}
			}
			matchesMap.put("live", finalLiveMatches);
		}

		result = printObjecto(matchesMap);
		return result;
	}

	public void printObject(Object object) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
	}

	public String printObjecto(Object object) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String result;
		result = gson.toJson(object);
		return result;
	}
}