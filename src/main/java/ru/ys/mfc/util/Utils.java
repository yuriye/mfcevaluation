package ru.ys.mfc.util;

import ru.ys.mfc.Settings;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class Utils {

    public static String getRatesString(List<String[]> rates) {
        StringBuilder stringBuilder = new StringBuilder();
        rates.forEach(rate -> {
            stringBuilder.append("<rate indicator-id=\"");
            stringBuilder.append(rate[0]);
            stringBuilder.append("\" value-id=\"");
            stringBuilder.append(rate[1]);
            stringBuilder.append("\">");
            stringBuilder.append(rate[1]);
            stringBuilder.append("</rate>");
        });
        return stringBuilder.toString();
    }

    public static String getAnswersQueryString(String version, String orderNumber, String answers) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
        df.setTimeZone(tz);

        String nowAsISO = df.format(new Date());

        return "<body><form-version>" + version + "</form-version>"
                + "<orderNumber>" + orderNumber + "</orderNumber>"
                + "<authorityId>" + "123" + "</authorityId>"
                + "<receivedDate>" + nowAsISO + "</receivedDate>"
                + "<okato>" + Settings.getInstance().getOkato() + "</okato>"
                + "<rates>" + answers + "</rates>" + "</body>";
    }
}
