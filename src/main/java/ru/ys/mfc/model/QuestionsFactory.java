package ru.ys.mfc.model;

import java.util.List;

public interface QuestionsFactory {
    List<Question> getQuestions();
    String getFormVersion();
}
