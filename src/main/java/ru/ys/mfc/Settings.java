package ru.ys.mfc;

public class Settings {

    private static Settings instance;


//    private String mkguUrlString = "http://10.2.139.25/mkgu/server/";
    private String mkguUrlString = "http://ais.md.int/mkgu/server/";
    private String getFreeTime = "getfreetime";
    private String getManagedOptions = "getmanagedoptions";
//    private String getOrderStatus = "cpgu/action/getOrderStatusTitle";
//    private String getMkguFormVersion = "cpgu/action/getMkguFormVersion";
//    private String getMkguQuestionnaires = "cpgu/action/getMkguQuestionnaires";
//    private String postMkguQuestionnaires = "cpgu/action/sendMkguFormAnswers";
//    private String sendMkguFormAnswers = "cpgu/action/sendMkguFormAnswers";

    private String getOrderStatus = "cpgu/action/GetOrderStatusTitle";
    private String getMkguFormVersion = "cpgu/action/GetMkguFormVersion";
    private String getMkguQuestionnaires = "cpgu/action/GetMkguQuestionnaires";
    private String postMkguQuestionnaires = "cpgu/action/SendMkguFormAnswers";
    private String sendMkguFormAnswers = "cpgu/action/SendMkguFormAnswers";

    private String okato = "30401000000";

    private Settings() {

    }

    public static Settings getInstance() {
        if (instance == null)
            instance = new Settings();
        return instance;
    }

    public String getSendMkguFormAnswers() {
        return sendMkguFormAnswers;
    }

    public String getOkato() {
        return okato;
    }

    public String getMkguUrlString() {
        return mkguUrlString;
    }

    public String getGetFreeTime() {
        return getFreeTime;
    }

    public String getGetManagedOptions() {
        return getManagedOptions;
    }

    public String getGetOrderStatus() {
        return getOrderStatus;
    }

    public String getGetMkguFormVersion() {
        return getMkguFormVersion;
    }

    public String getGetMkguQuestionnaires() {
        return getMkguQuestionnaires;
    }

    public String getPostMkguQuestionnaires() {
        return postMkguQuestionnaires;
    }
}
