package ru.ys.mfc;

import com.WacomGSS.STU.STUException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ys.mfc.equipment.InputDevice;
import ru.ys.mfc.mkgu.HttpAdapter;
import ru.ys.mfc.model.Answer;
import ru.ys.mfc.model.Question;
import ru.ys.mfc.model.QuestionsFactory;
import ru.ys.mfc.util.ByeImage;
import ru.ys.mfc.util.QuestionsFactoryFactory;
import ru.ys.mfc.util.Utils;
import ru.ys.mfc.view.EstimationForm;
import ru.ys.mfc.view.ProgressFrame;
import ru.ys.mfc.view.QuestionForm;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static InputDevice inputDevice;
    private static QuestionsFactory questionsFactory;
    private static boolean isMock = false;

    private static ProgressFrame progressFrame;


    public static void main(String[] args) {
        System.out.println("LOGGER=" + LOGGER.getName());
        try {
            String orderCode = "0000000";
            LOGGER.info("Код заявления: {}", (args.length > 0 ? args[0] : "null"));
            if (args.length > 0) {
                LOGGER.debug("args[0]: {}", args[0]);
                if ("i".equals(args[0])) {
                    Utils.exit(0);
                }
                if ("m".equals(args[0])) {
                    isMock = true;
                    questionsFactory = QuestionsFactoryFactory
                            .getQuestionsFactory("ru.ys.mfc.model.MockQuestions");
                    LOGGER.info("Mock variant!");
                } else {
                    orderCode = args[0];
                    questionsFactory = QuestionsFactoryFactory
                            .getQuestionsFactory("ru.ys.mfc.model.MKGUQuestions");
                    LOGGER.info("orderNumber: {}", orderCode);
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
                JOptionPane.showMessageDialog(null, "Оценка заявления с кодом " + orderCode + " уже была произведена.");
                Utils.exit(0);
            } else if (!"OK".equals(formVersion.get("status"))) {
                LOGGER.info("Заявление с кодом " + orderCode + " не найдено");
                JOptionPane.showMessageDialog(null, "Заявление с кодом " + orderCode + " не найдено");
                Utils.exit(0);
            }

            List<Question> questions = questionsFactory.getQuestions();
            progressFrame = new ProgressFrame("Оценка качеcтва оказания услуг");
            String response = "";
            try {
                progressFrame.setInformString("Осуществляется оценка...");
                response = askQuestions(questions, orderCode);
                LOGGER.info("response: {}", response);
            } catch (Exception e) {
                LOGGER.error("Ошибка на response = askQuestions(questions, orderCode)", e);
                Utils.exit(1);
            }

            ByeImage byeImage = new ByeImage("Спасибо за Вашу оценку!");

            if (!isMock) {
                progressFrame.setInformString("Осуществляется передача результата оценки...");
                if (!"".equals(response)) {
                    postAnswers(orderCode, formVersion.get("version"), response);
                    LOGGER.info("response sended");
                } else
                    LOGGER.warn("empty response");
                    return;
            }

            progressFrame.setInformString("Передача данных завершена, оценка принята.");
            byeImage.show();
            Thread.sleep(5000);
            Utils.exit(0);
        } catch (Exception e) {
            LOGGER.error("Ошибка", e);
        }
    }

    private static String askQuestions(List<Question> questions, String orderCode) throws STUException, InterruptedException {
        String response = "";
        inputDevice = InputDevice.getInstance();
        QuestionForm questionForm = null;
        EstimationForm estimationForm = null;
        List<String[]> answers = new ArrayList<>();
        int progress = 0;
        for (Question question : questions) {

            questionForm = new QuestionForm(question.getTitle());
            questionForm.waitForButtonPress();
            if (questionForm.getPressedButton().getId().equals("cancel")) {
                progressFrame.setInformString("Пользователь прервал процесс оценки.");
                LOGGER.info("Пользователь прервал процесс оценки.");
                Thread.sleep(5000);
                Utils.exit(0);
            }

            LOGGER.debug("question.getTitle(): {}", question.getTitle());

            List<Answer> sortedQuestios = question.getCandidates();
            sortedQuestios.sort(new Comparator<Answer>() {
                @Override
                public int compare(Answer o1, Answer o2) {
                    return Integer.parseInt(o2.getId()) - Integer.parseInt(o1.getId());
                }
            });

            estimationForm = new EstimationForm(sortedQuestios);
            estimationForm.waitForButtonPress();
            String[] answer = new String[2];
            answer[0] = question.getIndicatorId();
            answer[1] = estimationForm.getPressedButton().getId();
            LOGGER.debug("estimationForm.getPressedButton().getId() = {}", estimationForm.getPressedButton().getId());
            answers.add(answer);
            progressFrame.getProgressBar().setValue(++progress);
            LOGGER.info("Оценка: {} id: {}",
                    estimationForm.getPressedButton().getText(),
                    estimationForm.getPressedButton().getId());
        }
        InputDevice.getInstance().getTablet().setClearScreen();
        response = Utils.getAnswersQueryString(questionsFactory.getFormVersion().get("version"), orderCode, Utils.getRatesString(answers));
        LOGGER.debug("response:\n {}", response);
        return response;
    }

    private static int postAnswers(String orderCode, String version, String response) {
        int statusCode = HttpAdapter.getInstance().postAnswers(orderCode, version, response);
        LOGGER.info("statusCode: {}", statusCode);
        return statusCode;
    }
}
