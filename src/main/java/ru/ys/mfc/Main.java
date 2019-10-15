package ru.ys.mfc;

import com.WacomGSS.STU.Protocol.InkingMode;
import com.WacomGSS.STU.Protocol.OperationMode;
import com.WacomGSS.STU.STUException;
import com.WacomGSS.STU.Tablet;
import org.apache.commons.lang3.exception.ExceptionUtils;
import ru.ys.mfc.equipment.InputDevice;
import ru.ys.mfc.model.Question;
import ru.ys.mfc.model.QuestionsFactory;
import ru.ys.mfc.util.QuestionsFactoryFactory;
import ru.ys.mfc.view.EstimationForm;
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
            System.exit(0);
    }

    private static String askQuestions(List<Question> questions) {
        String response = "";
        inputDevice = InputDevice.getInstance();
        QuestionForm questionForm = null;
        EstimationForm estimationForm = null;
        try {
            for (Question question :
                    questions) {
                questionForm = new QuestionForm(question.getTitle());
                questionForm.waitForButtonPress();
                if (questionForm.getPressedButton().getId().equals("cancel")) {
                    System.exit(1);
                }
                System.out.println(question.getTitle());
                estimationForm = new EstimationForm(question.getCandidates());
                estimationForm.waitForButtonPress();
                System.out.println(estimationForm.getPressedButton().getText());
            }
            InputDevice.getInstance().getTablet().setClearScreen();
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
