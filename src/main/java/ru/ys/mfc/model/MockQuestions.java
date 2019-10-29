package ru.ys.mfc.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MockQuestions implements QuestionsFactory {

    @Override
    public Map<String, String> getFormVersion() {
        Map<String, String> map = new HashMap<>();
        map.put("serviceType", "direct");
        map.put("status", "OK");
        map.put("version", "0.0.4");
        return map;
    }

    @Override
    public List<Question> getQuestions() {
        List<Question> result = new ArrayList<>();
        List<Answer> candidates;
        Question question;

        candidates = new ArrayList<>();
        candidates.add(new Answer("41", "Очень плохо", "Услугу до сих пор не получил, несмотря на то, что срок предоставления давно истек"));
        candidates.add(new Answer("42", "Плохо", "Заявленные сроки не соблюдаются и должны быть короче"));
        candidates.add(new Answer("43", "Нормально", "Заявленные сроки соблюдаются, но могли бы быть немного короче"));
        candidates.add(new Answer("44", "Хорошо", "Заявленные сроки полностью устраивают и соблюдаются"));
        candidates.add(new Answer("45", "Отлично", "Отлично"));
        question = new Question("9", "Время предоставления государственной услуги",
                "Оцените, соответствует ли срок предоставления услуги вашим ожиданиям и заявленному сроку с момента подачи заявления, включая комплект необходимых документов",
                candidates);
        result.add(question);

        candidates = new ArrayList<>();
        candidates.add(new Answer("46", "Очень плохо", "Пришлось постоять в больших очередях несколько раз"));
        candidates.add(new Answer("47", "Плохо", "Пришлось постоять в большой очереди один раз"));
        candidates.add(new Answer("48", "Нормально", "Пришлось постоять в небольшой очереди один раз за все время обращения за услугой"));
        candidates.add(new Answer("49", "Хорошо", "В очередях не стоял ни разу за все время обращения за услугой"));
        candidates.add(new Answer("50", "Отлично", "Отлично"));
        question = new Question("10", "Время ожидания в очереди при получении государственной услуги",
                "Оцените ваше отношение к затратам времени на ожидание в очередях при подаче документов и при получении результата услуги",
                candidates);
        result.add(question);

        candidates = new ArrayList<>();
        candidates.add(new Answer("51", "Очень плохо", "Сотрудники хамили или были некомпетентны"));
        candidates.add(new Answer("52", "Плохо", "Сотрудники были недостаточно вежливы и/или недостаточно компетентны"));
        candidates.add(new Answer("53", "Нормально", "Сотрудники были достаточно вежливы и компетентны"));
        candidates.add(new Answer("54", "Хорошо", "Сотрудники были очень вежливы и демонстрировали высокий уровень компетентности"));
        candidates.add(new Answer("55", "Отлично", "Отлично"));
        question = new Question("11", "Вежливость и компетентность сотрудника, взаимодействующего с заявителем при предоставлении государственной услуги",
                "В случае, если вы получали консультацию по телефону или посещали ведомство для подачи или получения документов, оцените, насколько вы удовлетворены отношением и компетентностью сотрудников ведомства (вежливость, полнота предоставляемой информации, внимательность, желание помочь и т. д.)",
                candidates);
        result.add(question);

        candidates = new ArrayList<>();
        candidates.add(new Answer("56", "Очень плохо", "Помещение абсолютно не предназначено для обслуживания"));
        candidates.add(new Answer("57", "Плохо", "Уровнем комфорта не удовлетворен, есть существенные замечания"));
        candidates.add(new Answer("58", "Нормально", "В целом комфортно, но есть незначительные замечания"));
        candidates.add(new Answer("59", "Хорошо", "Уровнем комфорта в помещении полностью удовлетворен"));
        candidates.add(new Answer("60", "Отлично", "Отлично"));
        question = new Question("12", "Комфортность условий в помещении, в котором предоставлена государственная услуга",
                "Оцените уровень комфорта при посещении ведомства (наличие сидячих мест,, столов для заполнения документов, бланков, ручек, образцов заполнения, просторность и комфортность помещений и т. п.)",
                candidates);
        result.add(question);

        candidates = new ArrayList<>();
        candidates.add(new Answer("61", "Очень плохо", "Потратил много времени, но информацию не нашел, или она не соответствует действительности"));
        candidates.add(new Answer("62", "Плохо", "Информацию не нашел, или она  оказалась недостаточно точной , подробной или вовсе недостоверной"));
        candidates.add(new Answer("63", "Нормально", "Информацию получил в полном объеме, но пришлось потратить больше времени на ее поиск, чем хотелось"));
        candidates.add(new Answer("64", "Хорошо", "Информацию получил быстро и в полном объеме"));
        candidates.add(new Answer("65", "Отлично", "Отлично"));
        question = new Question("13", "Доступность информации о порядке предоставления государственной услуги",
                "Оцените качество информирования о порядке предоставления услуги и ее соответствие действительности (необходимые для предоставления документы, сроки оказания услуги, стоимость услуги) на официальном сайте ведомства, на Едином портале госуслуг, на информационном стенде в ведомстве, при получении консультации по телефону или при посещении ведомства",
                candidates);
        result.add(question);

        return result;
    }

    @Override
    public String getOrderCode() {
        return "m";
    }

    @Override
    public void setOrderCode(String orderCode) {

    }
}
