package org.example.model;

public class Vacancy {
    private final String title;
    private final String url;
    private final String source;

    public Vacancy(String title, String url, String source) {
        this.title = title;
        this.url = url;
        this.source = source;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getSource() {
        return source;
    }
}
