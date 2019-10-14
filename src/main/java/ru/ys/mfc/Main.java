package ru.ys.mfc;

import com.WacomGSS.STU.STUException;
import com.WacomGSS.STU.Tablet;
import org.apache.commons.lang3.exception.ExceptionUtils;
import ru.ys.mfc.equipment.InputDevice;
import ru.ys.mfc.model.Question;
import ru.ys.mfc.model.QuestionsFactory;
import ru.ys.mfc.util.QuestionsFactoryFactory;
import ru.ys.mfc.view.ProgressFrame;
import ru.ys.mfc.view.QuestionForm;

import javax.swing.*;
import java.util.List;

public class Main {

    private static InputDevice inputDevice;
    private static QuestionsFactory questionsFactory;
    private static boolean isMock = false;

    private static ProgressFrame progressFrame;

    public static void main(String[] args) {
        try {
            if (args.length > 0 && "m".equals(args[0])) {
                isMock = true;
                questionsFactory = QuestionsFactoryFactory
                        .getQuestionsFactory("ru.ys.mfc.model.MockQuestions");
                System.out.println("Mock variant!");
            } else
                questionsFactory = QuestionsFactoryFactory
                        .getQuestionsFactory("ru.ys.mfc.model.MKGUQuestions");

            List<Question> questions = questionsFactory.getQuestions();
            progressFrame = new ProgressFrame("Оценка качеcтва оказания услуг");
            String response = askQuestions(questions);
            if (!isMock)
                sendResponse(response);

            Tablet tablet = InputDevice.getInstance().getTablet();
            if (tablet != null && tablet.isConnected()) {
                tablet.setClearScreen();
                tablet.reset();
//                tablet.disconnect();

            }

            System.exit(0);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, ExceptionUtils.getStackTrace(e));
            System.exit(1);
        }
    }

    private static String askQuestions(List<Question> questions) {
        String response = "";
        if (inputDevice == null) {
            inputDevice = InputDevice.getInstance();
        }
        if (inputDevice == null) {
            JOptionPane.showMessageDialog(null, "Устройство не найдено!");
            System.exit(1);
        }

//        questions.stream().forEach(c -> {
//            System.out.println(c.getTitle() + ":" + c.getDescription());
//            c.getCandidates().stream().forEach(c1 -> System.out.println("\n\t" + c1.getId()
//                    + ":" + c1.getTitle()
//                    + ":" + c1.getAltTitle()));
//        });
        QuestionForm questionForm = null;
        try {
            for (Question question :
                    questions) {
                questionForm = new QuestionForm(question.getTitle());
                questionForm.waitForButtonPress();
                System.out.println(question.getTitle());
                if (questionForm.getPressedButton().getId().equals("cancel"))
                    System.exit(1);

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (STUException e) {
            e.printStackTrace();
        }

        return response;
    }

    private static void sendResponse(String response) {
        System.out.println("RESPONSING: " + response);

    }
}
