package ru.ys.mfc;

import com.WacomGSS.STU.STUException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static InputDevice inputDevice;
    private static QuestionsFactory questionsFactory;
    private static boolean isMock = false;

    private static ProgressFrame progressFrame;


    public static void main(String[] args) throws InterruptedException {
        String orderCode = "0000000";
        LOGGER.info("Код заявления: {}", (args.length > 0 ? args[0] : "null"));
        if (args.length > 0) {
            if ("m".equals(args[0])) {
                isMock = true;
                questionsFactory = QuestionsFactoryFactory
                        .getQuestionsFactory("ru.ys.mfc.model.MockQuestions");
                LOGGER.info("Mock variant!");
            } else {
                orderCode = args[0];
                questionsFactory = QuestionsFactoryFactory
                        .getQuestionsFactory("ru.ys.mfc.model.MKGUQuestions");
                LOGGER.debug("orderNumber: {}", orderCode);
            }
        } else {
            LOGGER.error("Не указан код заявления!");
            throw new RuntimeException("Не указан код заявления!");
        }

        questionsFactory.setOrderCode(orderCode);
        Map<String, String> formVersion;
        formVersion = questionsFactory.getFormVersion();

        if ("ALREADY_FILLED".equals(formVersion.get("status"))) {
            LOGGER.info("Оценка заявления с кодом " + orderCode + " уже была произведена.");
            JOptionPane.showMessageDialog((Component) null, "Оценка заявления с кодом " + orderCode + " уже была произведена.");
            exit(0);
        } else if (!"OK".equals(formVersion.get("status"))) {
            LOGGER.info("Заявление с кодом " + orderCode + " не найдено");
            JOptionPane.showMessageDialog((Component) null, "Заявление с кодом " + orderCode + " не найдено");
            exit(0);
        }

        List<Question> questions = questionsFactory.getQuestions();
        progressFrame = new ProgressFrame("Оценка качеcтва оказания услуг");
        String response = "";
        try {
            progressFrame.setInformString("Осуществляется оценка...");
            response = askQuestions(questions, orderCode);
        } catch (Exception e) {
            LOGGER.error("Ошибка на response = askQuestions(questions, orderCode)", e);
            exit(1);
        }

        if (!isMock) {
            progressFrame.setInformString("Осуществляется передача результата оценки...");
            postAnswers(orderCode, formVersion.get("version"), response);
        }

        LOGGER.info("response: {}", response);
        progressFrame.setInformString("Передача данных завершена, оценка принята.");
        ByeImage byeImage = new ByeImage("Спасибо за Вашу оценку!");
        byeImage.show();
        Thread.sleep(10000);
        exit(0);
    }

    private static String askQuestions(List<Question> questions, String orderCode) throws STUException {
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

            LOGGER.debug("question.getTitle(): {}", question.getTitle());
            estimationForm = new EstimationForm(question.getCandidates());
            estimationForm.waitForButtonPress();
            String[] answer = new String[2];
            answer[0] = question.getIndicatorId();
            answer[1] = estimationForm.getPressedButton().getId();
            answers.add(answer);
            progressFrame.getProgressBar().setValue(++progress);
            LOGGER.debug("Pressed button: {}", estimationForm.getPressedButton().getText());
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
        LOGGER.info("System.exit({})", code);
        System.exit(code);
    }

    private static int postAnswers(String orderCode, String version, String response) {
        int statusCode = HttpAdapter.getInstance().postAnswers(orderCode, version, response);
        LOGGER.info("statusCode: {}", statusCode);
        return statusCode;
    }
}
