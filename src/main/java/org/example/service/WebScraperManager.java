package org.example.service;

import org.example.model.Vacancy;
import org.example.scraper.Scraper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;

@Service
public class WebScraperManager {

    private final List<Scraper> scrapers;
    private final VacancyService vacancyService;

    public WebScraperManager(List<Scraper> scrapers, VacancyService vacancyService) {
        this.scrapers = scrapers;
        this.vacancyService = vacancyService;
    }

    public List<Vacancy> scrapeJuniorVacancies() {
        return vacancyService.saveNew(scrape(Scraper::scrapeJunior), "junior");
    }

    public List<Vacancy> scrapeMiddleVacancies() {
        return vacancyService.saveNew(scrape(Scraper::scrapeMiddle), "middle");
    }

    public List<Vacancy> scrapeSeniorVacancies() {
        return vacancyService.saveNew(scrape(Scraper::scrapeSenior), "senior");
    }

    private List<Vacancy> scrape(Function<Scraper, List<Vacancy>> method) {
        return scrapers.parallelStream()
                .flatMap(scraper -> method.apply(scraper).stream())
                .toList();
    }
}
