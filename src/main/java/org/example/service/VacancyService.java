package org.example.service;

import org.example.model.Vacancy;
import org.example.repository.VacancyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class VacancyService {

    private static final Logger log = LoggerFactory.getLogger(VacancyService.class);

    private final VacancyRepository repository;

    public VacancyService(VacancyRepository repository) {
        this.repository = repository;
    }

    /**
     * Saves only vacancies that are not duplicates.
     * Deduplication is done by:
     *   1. Title (case-insensitive) — catches same job on DOU and Djinni
     *   2. URL — catches re-scraping the same listing
     * Returns only the newly saved vacancies.
     */
    public List<Vacancy> saveNew(List<Vacancy> vacancies, String level) {
        Set<String> seenTitles = new HashSet<>();
        List<Vacancy> newVacancies = new ArrayList<>();

        for (Vacancy v : vacancies) {
            String normalizedTitle = v.getTitle().trim().toLowerCase();

            boolean titleSeenInBatch = !seenTitles.add(normalizedTitle);
            if (titleSeenInBatch) continue;

            boolean alreadyInDb = repository.existsByUrl(v.getUrl())
                    || repository.existsByTitleIgnoreCase(v.getTitle().trim());
            if (alreadyInDb) continue;

            v.setLevel(level);
            v.setCreatedAt(LocalDateTime.now());
            newVacancies.add(v);
        }

        repository.saveAll(newVacancies);
        log.info("Saved {}/{} new {} vacancies (duplicates filtered).", newVacancies.size(), vacancies.size(), level);
        return newVacancies;
    }
}
