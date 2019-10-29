package ru.ys.mfc.model;

import java.util.List;
import java.util.Map;

public interface QuestionsFactory {
    List<Question> getQuestions();

    Map<String, String> getFormVersion();

    String getOrderCode();

    void setOrderCode(String orderCode);
}
