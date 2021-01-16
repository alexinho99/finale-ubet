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
	List<FootballEvent> liveEvents = new ArrayList<>();

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
		String url = "https://www.xscores.com/soccer";
		Document document = Jsoup.connect(url).get();
		boolean foundCoeff = false;

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

		Elements idHrefs = document.select("a[id]");

		for (Element item : idHrefs) {
			String href = item.attr("href");
			Elements odds = item.getElementsByClass("odds");

			String homeTeam = null;
			String awayTeam = null;
			double firstTeamToWin = 0;
			double draw = 0;
			double secondTeamToWin = 0;
			String currHref = null;
			String extractLiveResult = "";
			int homeScore = 0;
			int awayScore = 0;
			int hrefo = 0;

			if (!href.equals("") && odds.size() != 0) {

				for (int i = 0; i < odds.get(0).childNodeSize(); i++) {
					String coef = odds.get(0).childNode(i).toString();
					if (i == 0) {
						if (coef.contains(".")) {
							foundCoeff = true;
							Elements liveResult = item.getElementsByClass("scoreh_ft score_cell centerTXT");

							homeTeam = href.substring(0, href.indexOf("vs"));
							homeTeam = homeTeam.substring(homeTeam.lastIndexOf("/") + 1, homeTeam.length() - 1);
							//   System.out.println(homeTeam);
							awayTeam = href.substring(href.indexOf("vs"), href.length() - 1);
							awayTeam = awayTeam.substring(awayTeam.indexOf("vs") + 3, awayTeam.indexOf("/"));
							//  System.out.println(awayTeam);
							currHref = href;
							//	System.out.println(currHref);
							currHref = currHref.substring(currHref.lastIndexOf("/") + 1, currHref.length());
							hrefo = Integer.parseInt(currHref);
							//		System.out.println(hrefo);

							for (Element element : liveResult) {

								String locateLiveResult = element.parent().toString();
								for (int j = 0; j < locateLiveResult.length(); j++) {
									if (Character.isDigit(locateLiveResult.charAt(j))) {

										if (!extractLiveResult.contains("-")) {
											extractLiveResult = extractLiveResult + locateLiveResult.charAt(j) + "-";
										} else {
											extractLiveResult = extractLiveResult + locateLiveResult.charAt(j);
										}
									}
								}
								int countEntries = 0;
								for (int j = 0; j < locateLiveResult.length(); j++) {

									if (Character.isDigit(locateLiveResult.charAt(j))) {
										if (countEntries == 0) {
											homeScore = Integer.parseInt(String.valueOf(locateLiveResult.charAt(j)));
											countEntries++;
										} else {
											awayScore = Integer.parseInt(String.valueOf(locateLiveResult.charAt(j)));
										}
									}
								}
							}
						}
					}

					if (foundCoeff) {

						if (coef.contains("->")) {
							coef = coef.substring(coef.indexOf("->"), coef.length() - 1);
							coef = coef.substring(coef.indexOf("->") + 2, coef.indexOf("<"));

							coef = coef.replace("-", "");
							coef = coef.trim();
							//   System.out.println(coef);
							if (i == 0) {
								firstTeamToWin = Double.parseDouble(coef);
							} else if (i == 1) {
								draw = Double.parseDouble(coef);
							} else if (i == 2) {
								secondTeamToWin = Double.parseDouble(coef);
							}
						}
					}

					if (i + 1 >= odds.get(0).childNodeSize()) {

						FootballEvent event = new FootballEvent();
						event.setLiveResult(extractLiveResult);
						event.setHomeTeam(homeTeam);
						event.setAwayTeam(awayTeam);
						event.setDraw(draw);
						event.setHomeOdd(firstTeamToWin);
						event.setAwayOdd(secondTeamToWin);
						event.setHomeScore(homeScore);
						event.setAwayScore(awayScore);
						event.setHref(hrefo);

						if (extractLiveResult.equals("")) {
							upComingEvents.add(event);
						} else {
							try {
								liveEvents.add(event);
							} catch (Exception e) {

							}
						}
						foundCoeff = false;
					}
				}
			}
		}

		matchDao.setAllAsFinished();

		Map<String, List<FootballEvent>> matchesMap = new HashMap<>();
		List<FootballEvent> finalUpComingMatches = new ArrayList<>();

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
						matchDao.save(matchesEntity);
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