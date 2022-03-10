package ru.ys.mfc.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuestionsFactoryFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionsFactoryFactory.class);

    public static ru.ys.mfc.model.QuestionsFactory getQuestionsFactory(String questionsFactoryClassName) {
        ru.ys.mfc.model.QuestionsFactory questionsFactory = null;
        try {
            questionsFactory = (ru.ys.mfc.model.QuestionsFactory) Class.forName(questionsFactoryClassName).newInstance();
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        return questionsFactory;
    }
}
