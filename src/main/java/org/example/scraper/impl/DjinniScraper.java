package org.example.scraper.impl;

import org.example.model.Vacancy;
import org.example.scraper.Scraper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Service
public class DjinniScraper implements Scraper {

    private static final Logger logger = LoggerFactory.getLogger(DjinniScraper.class);

    private static final String DJINNI_JAVA_URL = "https://djinni.co/jobs/?primary_keyword=Java&exp_level=";
    private static final String DJINNI_JUNIOR_URL = "no_exp&exp_level=1y";
    private static final String DJINNI_MIDDLE_URL = "2y&exp_level=3y";
    private static final String DJINNI_SENIOR_URL = "3y&exp_level=4y&exp_level=5y";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36";
    private static final int TIMEOUT = 10_000;

    @Override
    public List<Vacancy> scrapeJunior() {
        return scrape(DJINNI_JUNIOR_URL);
    }

    @Override
    public List<Vacancy> scrapeMiddle() {
        return scrape(DJINNI_MIDDLE_URL);
    }

    @Override
    public List<Vacancy> scrapeSenior() {
        return scrape(DJINNI_SENIOR_URL);
    }

    private List<Vacancy> scrape(String exp) {
        try {
            Document doc = Jsoup.connect(DJINNI_JAVA_URL + exp)
                    .userAgent(USER_AGENT)
                    .timeout(TIMEOUT)
                    .get();

            return doc.select("a[href^='/jobs/']").stream()
                    .filter(link -> !link.text().isBlank() && link.attr("href").matches("/jobs/\\d+-.+/"))
                    .map(link -> new Vacancy(link.text().trim(), "https://djinni.co" + link.attr("href"), "Djinni"))
                    .toList();
        } catch (IOException e) {
            logger.error("Failed to scrape Djinni vacancies for exp={}", exp, e);
            return Collections.emptyList();
        }
    }
}
