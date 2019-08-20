package ru.ys.mfc;

import org.apache.commons.lang3.exception.ExceptionUtils;
import ru.ys.mfc.equipment.InputDevice;
import ru.ys.mfc.model.Question;
import ru.ys.mfc.model.QuestionsFactory;
import ru.ys.mfc.util.QuestionsFactoryFactory;
import ru.ys.mfc.view.ProgressFrame;

import javax.swing.*;
import java.util.List;

public class Main {

    private static InputDevice inputDevice = InputDevice.getInstance();
    private static QuestionsFactory questionsFactory;
    private static boolean isMock = false;
    private static ProgressFrame progressFrame;

    public static void main(String[] args) {
        try {
            if (args.length > 0 && "m".equals(args[0])) {
                isMock = true;
                questionsFactory = QuestionsFactoryFactory
                        .getQuestionsFactory("ru.ys.mfc.model.MockQuestions");
            } else
                questionsFactory = QuestionsFactoryFactory
                        .getQuestionsFactory("ru.ys.mfc.model.MKGUQuestions");

            progressFrame = new ProgressFrame("Оценка качеcтва оказания услуг");

            List<Question> questions = questionsFactory.getQuestions();

            String response = askQuestions(questions);
            if (!isMock)
                sendResponse(response);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, ExceptionUtils.getStackTrace(e));
            System.exit(1);
        } finally {
            inputDevice.disconnect();
        }
    }

    private static String askQuestions(List<Question> questions) {
        String response = "";

        questions.stream().forEach(c -> {
            System.out.println(c.getTitle() + ":" + c.getDescription());
            c.getCandidates().stream().forEach(c1 -> System.out.println("\n\t" + c1.getId()
                    + ":" + c1.getTitle()
                    + ":" + c1.getAltTitle()));
        });

        return response;
    }

    private static void sendResponse(String response) {
        System.out.println("RESPONSING: " + response);

    }
}
