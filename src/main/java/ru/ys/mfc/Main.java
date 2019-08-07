package ru.ys.mfc;

import ru.ys.mfc.model.Question;
import ru.ys.mfc.model.QuestionsFactory;
import ru.ys.mfc.util.QuestionsFactoryFactory;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        QuestionsFactory questionsFactory;

        if (args.length > 0)
            questionsFactory = QuestionsFactoryFactory.getQuestionsFactory(args[0]);
        else
            questionsFactory = QuestionsFactoryFactory.getQuestionsFactory("MKGUQuestions");

        List<Question> questions = questionsFactory.getQuestions();

    }
}
