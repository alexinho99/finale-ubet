package com.javainuse.controller;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class HelloWorldController {

	@RequestMapping({ "/hello" })
	public String firstPage() throws IOException {

		String result = "";

		List<FootballEvent> events = new ArrayList<>();
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

			if (!href.equals("") && odds.size() != 0) {

				for (int i = 0; i < odds.get(0).childNodeSize(); i++) {
					String coef = odds.get(0).childNode(i).toString();
					if (i == 0) {
						if (coef.contains(".")) {
							foundCoeff = true;
							Elements liveResult = item.getElementsByClass("scoreh_ht score_cell centerTXT");

							homeTeam = href.substring(0, href.indexOf("vs"));
							homeTeam = homeTeam.substring(homeTeam.lastIndexOf("/") + 1, homeTeam.length() - 1);
							//   System.out.println(homeTeam);
							awayTeam = href.substring(href.indexOf("vs"), href.length() - 1);
							awayTeam = awayTeam.substring(awayTeam.indexOf("vs") + 3, awayTeam.indexOf("/"));
							//  System.out.println(awayTeam);
							currHref = href;

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
						event.setHref(currHref);
						event.setHomeTeam(homeTeam);
						event.setAwayTeam(awayTeam);
						event.setDraw(draw);
						event.setFirstTeamToWin(firstTeamToWin);
						event.setSecondTeamToWin(secondTeamToWin);

						events.add(event);
						//   System.out.println();
						foundCoeff = false;
					}

				}

			}

		}

		for (FootballEvent event : events) {
			if (event.getHomeTeam() != null && event.getSecondTeamToWin() != 0.00 && event.getFirstTeamToWin() != 0.00 && event.getDraw() != 0.00) {

				result = result + event.getHomeTeam() + "  VS  " + event.getAwayTeam() + "  " + event.getFirstTeamToWin() + "  "
						+ event.getDraw() + "  " + event.getSecondTeamToWin() + "  " + event.getHref() + "\r\n";

				System.out.println(event.getHomeTeam() + "  VS  " + event.getAwayTeam() + "  " + event.getFirstTeamToWin() + "  "
						+ event.getDraw() + "  " + event.getSecondTeamToWin() + "  " + event.getHref());

				System.out.println();
			}

		}

		return result;
	}

}