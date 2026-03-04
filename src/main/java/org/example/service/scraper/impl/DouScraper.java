package org.example.service.scraper.impl;

import org.example.model.Vacancy;
import org.example.service.scraper.Scraper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Service
public class DouScraper implements Scraper {

    private static final Logger logger = LoggerFactory.getLogger(DouScraper.class);

    private static final String DOU_JAVA_URL = "https://jobs.dou.ua/vacancies/?category=Java&exp=";
    private static final String DOU_JUNIOR_URL = "0-1";
    private static final String DOU_MIDDLE_URL = "1-3";
    private static final String DOU_SENIOR_URL = "3-5";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36";
    private static final int TIMEOUT = 10_000;

    @Override
    public List<Vacancy> scrapeJunior() {
        return scrape(DOU_JUNIOR_URL);
    }

    @Override
    public List<Vacancy> scrapeMiddle() {
        return scrape(DOU_MIDDLE_URL);
    }

    @Override
    public List<Vacancy> scrapeSenior() {
        return scrape(DOU_SENIOR_URL);
    }

    private List<Vacancy> scrape(String exp) {
        try {
            Document doc = Jsoup.connect(DOU_JAVA_URL + exp)
                    .userAgent(USER_AGENT)
                    .timeout(TIMEOUT)
                    .get();

            return doc.select("a.vt").stream()
                    .filter(link -> !link.text().isBlank())
                    .map(link -> new Vacancy(link.text().trim(), link.attr("href"), "DOU"))
                    .toList();
        } catch (IOException e) {
            logger.error("Failed to scrape DOU vacancies for exp={}", exp, e);
            return Collections.emptyList();
        }
    }
}
