package ru.ys.mfc;

public class Settings {

    private static String urlString = "http://10.2.139.25/mkgu/server/";
    private static String getFreeTime = "getfreetime";
    private static String getManagedOptions = "getmanagedoptions";
    private static String getOrderStatus = "cpgu/action/getOrderStatusTitle";
    private static String getMkguFormVersion = "cpgu/action/getMkguFormVersion";
    private static String getMkguQuestionnaires = "cpgu/action/getMkguQuestionnaires";
    private static String postMkguQuestionnaires = "cpgu/action/sendMkguFormAnswers";

    public static String getUrlString() {
        return urlString;
    }

    public static String getGetFreeTime() {
        return getFreeTime;
    }

    public static String getGetManagedOptions() {
        return getManagedOptions;
    }

    public static String getGetOrderStatus() {
        return getOrderStatus;
    }

    public static String getGetMkguFormVersion() {
        return getMkguFormVersion;
    }

    public static String getGetMkguQuestionnaires() {
        return getMkguQuestionnaires;
    }

    public static String getPostMkguQuestionnaires() {
        return postMkguQuestionnaires;
    }
}
