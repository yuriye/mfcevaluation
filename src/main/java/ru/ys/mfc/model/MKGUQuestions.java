package ru.ys.mfc.model;

import ru.ys.mfc.mkgu.HttpAdapter;
import ru.ys.mfc.mkgu.MkguQuestionXmlIndicator;
import ru.ys.mfc.mkgu.MkguQuestionXmlQuestions;
import ru.ys.mfc.mkgu.MkguQuestions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MKGUQuestions implements QuestionsFactory {

    private String orderCode = "";
    private Map<String, String> formVersion = null;

    @Override
    public Map<String, String> getFormVersion() {
        return formVersion;
    }

    @Override
    public List<Question> getQuestions() {
        List<Question> result = new ArrayList<>();
//        List<Answer> candidates;
//        Question question;

        List<MkguQuestionXmlIndicator> indicators = MkguQuestions.getQuestions(getFormVersion(), orderCode).getIndicator();

        for (MkguQuestionXmlIndicator indicator : indicators) {
            List<Answer> candidates = new ArrayList<>();
            List<MkguQuestionXmlQuestions> xmlQuestionsindicator = indicator.getIndicator();
            for (MkguQuestionXmlQuestions xmlQuestions : xmlQuestionsindicator) {
                String title = xmlQuestions.getAltTitle();
                String altTitle = xmlQuestions.getQuestionText();
                String id = xmlQuestions.getQuestionValue();
                candidates.add(new Answer(id, altTitle, "".equals(title) ? altTitle : title));
            }
            Question question = new Question(indicator.getIndicatorId(), indicator.getQuestionTitle(),
                    indicator.getDescriptionTitle(),
                    candidates);
            result.add(question);
            System.out.println(indicator.getQuestionTitle());
        }
        return result;
    }

    @Override
    public String getOrderCode() {
        return orderCode;
    }

    @Override
    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
        formVersion = HttpAdapter.getInstance().getMkguFormVersion(orderCode);
        for (Map.Entry<String, String> entry : formVersion.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }

}
