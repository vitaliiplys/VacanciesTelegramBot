package org.example.repository;

import org.example.model.Vacancy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VacancyRepository extends JpaRepository<Vacancy, Long> {
    boolean existsByUrl(String url);
    boolean existsByTitleIgnoreCase(String title);
}
