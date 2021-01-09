package com.javainuse.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.javainuse.dao.MatchDao;
import com.javainuse.model.MatchesEntity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class HelloWorldController {
	List<FootballEvent> upComingEvents;
	List<FootballEvent> liveEvents = new ArrayList<>();

	@Autowired
	private MatchDao matchDao;

	@RequestMapping(value = "/match", method = RequestMethod.GET)
	public String getSingleMatch() {
		String result = "";
		System.out.println(matchDao.findById(1));
		return  result;
	}

	@RequestMapping(value = "/username", method = RequestMethod.GET)
	public String userPage() throws IOException, SQLException {
		String buffer = "";
		try
		{
			DriverManager.registerDriver(new com.mysql.jdbc.Driver());
			// create our mysql database connection
			String myDriver = "org.gjt.mm.mysql.Driver";
			String myUrl = "jdbc:mysql://localhost/bootdb";
		//	Class.forName(myDriver);
			Connection conn = DriverManager.getConnection(myUrl, "root", "root");

			// our SQL SELECT query.
			// if you only need a few columns, specify them by name instead of using "*"
			String query = "SELECT * FROM user";

			// create the java statement
			Statement st = conn.createStatement();

			// execute the query, and get a java resultset
			ResultSet rs = st.executeQuery(query);

			// iterate through the java resultset
			while (rs.next())
			{

				String firstName = rs.getString("username");
				buffer = firstName;
			}
			st.close();
		}
		catch (Exception e)
		{
			System.err.println("Got an exception! ");
			System.err.println(e.getMessage());
		}

		return buffer;
	}

	@RequestMapping(value = "/matches", method = RequestMethod.GET)
	public String firstPage() throws IOException, SQLException {

		String result = "";

		upComingEvents = new ArrayList<>();
		String url = "https://www.xscores.com/soccer";
		Document document = Jsoup.connect(url).get();
		boolean foundCoeff = false;

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
						event.setFirstTeamToWin(firstTeamToWin);
						event.setSecondTeamToWin(secondTeamToWin);
						event.setHomeScore(homeScore);
						event.setAwayScore(awayScore);
						event.setHref(hrefo);
						System.out.println(event.toString());


						if (extractLiveResult.equals("")) {
							upComingEvents.add(event);
						} else {
							try {
								liveEvents.add(event);
							} catch (Exception e) {

							}
							//   System.out.println();

						}
						foundCoeff = false;
					}

				}

			}


		}

		Map<String, List<FootballEvent>> upcomingMatchesMap = new HashMap<>();
		List<FootballEvent> finalUpComingMatches = new ArrayList<>();


		if (upComingEvents != null) {
			for (FootballEvent event : upComingEvents) {
				if (event.getHomeTeam() != null && event.getSecondTeamToWin() != 0.00 && event.getFirstTeamToWin() != 0.00 && event.getDraw() != 0.00) {

					MatchesEntity matchesEntity = new MatchesEntity();
					matchesEntity.setHomeTeam(event.getHomeTeam());
					matchesEntity.setAwayTeam(event.getAwayTeam());
					matchesEntity.setFirstTeamToWin(event.getFirstTeamToWin());
					matchesEntity.setDraw(event.getDraw());
					matchesEntity.setSecondTeamToWin(event.getSecondTeamToWin());
					matchesEntity.setHref(event.getHref());
					matchesEntity.setAwayScore(event.getAwayScore());
					matchesEntity.setHomeScore(event.getHomeScore());
					matchesEntity.setLiveResult(event.getLiveResult());
					matchDao.save(matchesEntity);
					finalUpComingMatches.add(event);
					if(finalUpComingMatches.size() == 8) {
						break;
					}
				}

			}

			upcomingMatchesMap.put("upcoming", finalUpComingMatches);
			UpcomingMatches upcomingMatches = new UpcomingMatches(upcomingMatchesMap);
			result += printObjecto(upcomingMatches);
		}

		Map<String, List<FootballEvent>> liveMatchesMap = new HashMap<>();
		List<FootballEvent> finalLiveMatches = new ArrayList<>();

		if (liveEvents != null) {
			for (FootballEvent event : liveEvents) {
				if (event.getHomeTeam() != null && event.getSecondTeamToWin() != 0.00 && event.getFirstTeamToWin() != 0.00 && event.getDraw() != 0.00) {

					MatchesEntity matchesEntity = new MatchesEntity();
					matchesEntity.setHomeTeam(event.getHomeTeam());
					matchesEntity.setAwayTeam(event.getAwayTeam());
					matchesEntity.setFirstTeamToWin(event.getFirstTeamToWin());
					matchesEntity.setDraw(event.getDraw());
					matchesEntity.setSecondTeamToWin(event.getSecondTeamToWin());
					matchesEntity.setHref(event.getHref());
					matchesEntity.setAwayScore(event.getAwayScore());
					matchesEntity.setHomeScore(event.getHomeScore());
					matchesEntity.setLiveResult(event.getLiveResult());
					matchDao.save(matchesEntity);
					finalLiveMatches.add(event);
					if(finalLiveMatches.size() == 8) {
						break;
					}
				}
			}
			liveMatchesMap.put("live", finalLiveMatches);
			LiveMatches liveMatches = new LiveMatches(liveMatchesMap);
			result += printObjecto(liveMatches);
		}



		return result;
	}

	public void printObject(Object object) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
//		System.out.println(gson.toJson(object));
	}

	public String printObjecto(Object object) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String result;
		result = gson.toJson(object);
		return result;
	}
}