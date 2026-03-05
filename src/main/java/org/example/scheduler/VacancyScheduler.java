package org.example.scheduler;

import org.example.service.WebScraperManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class VacancyScheduler {

    private static final Logger log = LoggerFactory.getLogger(VacancyScheduler.class);

    private final WebScraperManager webScraperManager;

    public VacancyScheduler(WebScraperManager webScraperManager) {
        this.webScraperManager = webScraperManager;
    }

    @Scheduled(fixedRate = 15 * 60 * 1000, initialDelay = 15 * 60 * 1000)
    public void scrapeAndSave() {
        log.info("Scheduler triggered. Scraping vacancies...");
        webScraperManager.scrapeJuniorVacancies();
        webScraperManager.scrapeMiddleVacancies();
        webScraperManager.scrapeSeniorVacancies();
        log.info("Scheduler finished.");
    }
}
