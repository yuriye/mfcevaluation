package ru.ys.mfc.model;

import java.util.List;

public class Question {
    private final String title;
    private final String description;
    private final List<Answer> candidates;

    public Question(String title, String description, List<Answer> candidates) {
        this.title = title;
        this.description = description;
        this.candidates = candidates;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<Answer> getCandidates() {
        return candidates;
    }
}
