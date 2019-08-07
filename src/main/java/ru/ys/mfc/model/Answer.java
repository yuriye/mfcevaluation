package ru.ys.mfc.model;

public class Answer {
    private String id;
    private String title;
    private String altTitle;

    public Answer(String id, String title, String altTitle) {
        this.id = id;
        this.title = title;
        this.altTitle = altTitle;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAltTitle() {
        return altTitle;
    }
}
