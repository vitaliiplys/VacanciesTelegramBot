package org.example.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "vacancies", uniqueConstraints = @UniqueConstraint(columnNames = "url"))
@Getter
@NoArgsConstructor
public class Vacancy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(unique = true, nullable = false)
    private String url;

    private String source;

    @Setter
    private String level;

    @Setter
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Vacancy(String title, String url, String source) {
        this.title = title;
        this.url = url;
        this.source = source;
    }
}
