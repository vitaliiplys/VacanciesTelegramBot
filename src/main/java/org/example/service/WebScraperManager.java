package org.example.service;

import org.example.model.Vacancy;
import org.example.service.scraper.Scraper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

@Service
public class WebScraperManager {

    private final List<Scraper> scrapers;

    public WebScraperManager(List<Scraper> scrapers) {
        this.scrapers = scrapers;
    }

    public List<Vacancy> scrapeJuniorVacancies() {
        return scrape(Scraper::scrapeJunior);
    }

    public List<Vacancy> scrapeMiddleVacancies() {
        return scrape(Scraper::scrapeMiddle);
    }

    public List<Vacancy> scrapeSeniorVacancies() {
        return scrape(Scraper::scrapeSenior);
    }

    private List<Vacancy> scrape(Function<Scraper, List<Vacancy>> method) {
        return scrapers.parallelStream()
                .flatMap(scraper -> method.apply(scraper).stream())
                .toList();
    }
}
