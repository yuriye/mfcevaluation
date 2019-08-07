package ru.ys.mfc.util;

public class QuestionsFactoryFactory {

    public static ru.ys.mfc.model.QuestionsFactory getQuestionsFactory(String questionsFactoryClassName) {
        ru.ys.mfc.model.QuestionsFactory questionsFactory = null;
        try {
            questionsFactory = (ru.ys.mfc.model.QuestionsFactory) Class.forName(questionsFactoryClassName).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return questionsFactory;
    }
}
