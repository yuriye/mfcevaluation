package ru.ys.mfc;

import ru.ys.mfc.model.Question;
import ru.ys.mfc.model.QuestionsFactory;
import ru.ys.mfc.util.QuestionsFactoryFactory;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        QuestionsFactory questionsFactory;

        if (args.length > 0 && "m".equals(args[0]))
            questionsFactory = QuestionsFactoryFactory.getQuestionsFactory("ru.ys.mfc.model.MockQuestions");
        else
            questionsFactory = QuestionsFactoryFactory.getQuestionsFactory("ru.ys.mfc.model.MKGUQuestions");

        List<Question> questions = questionsFactory.getQuestions();
        questions.stream().forEach(c -> {
            System.out.println(c.getTitle() + ":" + c.getDescription());
            c.getCandidates().stream().forEach(c1 -> System.out.println("\n\t" + c1.getId()
                    + ":" + c1.getTitle()
                    + ":" + c1.getAltTitle()));
        });

    }
}
