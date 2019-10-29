package ru.ys.mfc;

import com.WacomGSS.STU.STUException;
import ru.ys.mfc.equipment.InputDevice;
import ru.ys.mfc.mkgu.HttpAdapter;
import ru.ys.mfc.model.Question;
import ru.ys.mfc.model.QuestionsFactory;
import ru.ys.mfc.util.ByeImage;
import ru.ys.mfc.util.QuestionsFactoryFactory;
import ru.ys.mfc.util.Utils;
import ru.ys.mfc.view.EstimationForm;
import ru.ys.mfc.view.ProgressFrame;
import ru.ys.mfc.view.QuestionForm;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {

    private static InputDevice inputDevice;
    private static QuestionsFactory questionsFactory;
    private static boolean isMock = false;

    private static ProgressFrame progressFrame;


    public static void main(String[] args) throws InterruptedException {
        String orderCode = "0000000";

        if (args.length > 0) {
            if ("m".equals(args[0])) {
                isMock = true;
                questionsFactory = QuestionsFactoryFactory
                        .getQuestionsFactory("ru.ys.mfc.model.MockQuestions");
                System.out.println("Mock variant!");
            } else {
                orderCode = args[0];
                questionsFactory = QuestionsFactoryFactory
                        .getQuestionsFactory("ru.ys.mfc.model.MKGUQuestions");
                System.out.println("orderNumber: " + orderCode);
            }
        } else
            throw new RuntimeException("Не указан код заявления!");

        questionsFactory.setOrderCode(orderCode);
        Map<String, String> formVersion;
        formVersion = questionsFactory.getFormVersion();

        if ("ALREADY_FILLED".equals(formVersion.get("status"))) {
            JOptionPane.showMessageDialog((Component) null, "Оценка заявления с кодом " + orderCode + " уже была произведена.");
            exit(0);
        } else if (!"OK".equals(formVersion.get("status"))) {
            JOptionPane.showMessageDialog((Component) null, "Заявление с кодом " + orderCode + " не найдено");
            exit(0);
        }

        List<Question> questions = questionsFactory.getQuestions();
        progressFrame = new ProgressFrame("Оценка качеcтва оказания услуг");
        String response = "";
        try {
            response = askQuestions(questions, orderCode);
        } catch (Exception e) {
            e.printStackTrace();
            exit(1);
        }

        if (!isMock)
            postAnswers(orderCode, formVersion.get("version"), response);

        System.out.println(response);

        ByeImage byeImage = new ByeImage("Спасибо за Вашу оценку!");
        byeImage.show();
        Thread.sleep(10000);
        exit(0);
    }

    private static String askQuestions(List<Question> questions, String orderCode) throws STUException , InterruptedException{
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
        response = Utils.getAnswersQueryString(questionsFactory.getFormVersion().get("version"), orderCode, Utils.getRatesString(answers));
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

    private static int postAnswers(String orderCode, String version, String response) {
        int statusCode = HttpAdapter.getInstance().postAnswers(orderCode, version, response);
        System.out.println("statusCode: " + statusCode);
        return statusCode;
    }
}
