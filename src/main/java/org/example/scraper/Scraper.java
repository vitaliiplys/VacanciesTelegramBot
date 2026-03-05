package org.example.scraper;

import org.example.model.Vacancy;

import java.util.List;

public interface Scraper {
    List<Vacancy> scrapeJunior();
    List<Vacancy> scrapeMiddle();
    List<Vacancy> scrapeSenior();
}
