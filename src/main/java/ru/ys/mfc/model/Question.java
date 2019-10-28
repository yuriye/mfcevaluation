package ru.ys.mfc.model;

import java.util.List;

public class Question {
    private final String indicatorId;
    private final String title;
    private final String description;
    private final List<Answer> candidates;

    public Question(String indicatorId, String title, String description, List<Answer> candidates) {
        this.indicatorId = indicatorId;
        this.title = title;
        this.description = description;
        this.candidates = candidates;

    }

    public String getIndicatorId() {
        return indicatorId;
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
