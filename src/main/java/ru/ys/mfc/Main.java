package ru.ys.mfc;

import com.WacomGSS.STU.STUException;
import ru.ys.mfc.equipment.InputDevice;
import ru.ys.mfc.model.Question;
import ru.ys.mfc.model.QuestionsFactory;
import ru.ys.mfc.util.QuestionsFactoryFactory;
import ru.ys.mfc.util.Utils;
import ru.ys.mfc.view.EstimationForm;
import ru.ys.mfc.view.ProgressFrame;
import ru.ys.mfc.view.QuestionForm;

import java.util.ArrayList;
import java.util.List;

public class Main {

    private static InputDevice inputDevice;
    private static QuestionsFactory questionsFactory;
    private static boolean isMock = false;

    private static ProgressFrame progressFrame;


    public static void main(String[] args) {
        String orderNumber = "0000000";
        if (args.length > 0) {
            if ("m".equals(args[0])) {
                isMock = true;
                questionsFactory = QuestionsFactoryFactory
                        .getQuestionsFactory("ru.ys.mfc.model.MockQuestions");
                System.out.println("Mock variant!");
            } else {
                orderNumber = args[0];
                questionsFactory = QuestionsFactoryFactory
                        .getQuestionsFactory("ru.ys.mfc.model.MKGUQuestions");
            }
        } else
            throw new RuntimeException("Не указан код заявления!");

        List<Question> questions = questionsFactory.getQuestions();
        progressFrame = new ProgressFrame("Оценка качеcтва оказания услуг");
        String response = "";
        try {
            response = askQuestions(questions, orderNumber);
        } catch (Exception e) {
            e.printStackTrace();
            exit(1);
        }

        if (!isMock)
            sendResponse(response);
        System.out.println(response);
        exit(0);
    }

    private static String askQuestions(List<Question> questions, String orderNumber) throws STUException , InterruptedException{
        String response = "";
        inputDevice = InputDevice.getInstance();
        QuestionForm questionForm = null;
        EstimationForm estimationForm = null;
        List<String[]> answers = new ArrayList<>();
        int progress = 0;
        for (Question question :
                questions) {
            questionForm = new QuestionForm(question.getTitle());
            questionForm.waitForButtonPress();
            if (questionForm.getPressedButton().getId().equals("cancel")) {
                exit(1);
            }

            System.out.println(question.getTitle());
            estimationForm = new EstimationForm(question.getCandidates());
            estimationForm.waitForButtonPress();
            String[] answer = new String[2];
            answer[0] = question.getIndicatorId();
            answer[1] = estimationForm.getPressedButton().getId();
            answers.add(answer);
            progressFrame.getProgressBar().setValue(++progress);
            System.out.println(estimationForm.getPressedButton().getText());
        }
        InputDevice.getInstance().getTablet().setClearScreen();
        response = Utils.getAnswersQueryString(questionsFactory.getFormVersion(), orderNumber, Utils.getRatesString(answers));
        return response;
    }

    public static void exit(int code) {
        try {
            if (inputDevice != null) {
                inputDevice.getTablet().reset();
                inputDevice.getTablet().disconnect();
            }
        } catch (Exception e) {
        }
        System.exit(code);
    }

    private static void sendResponse(String response) {
        System.out.println("RESPONSING: " + response);
    }
}
