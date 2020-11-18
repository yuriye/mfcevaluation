package ru.ys.mfc.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ys.mfc.Main;
import ru.ys.mfc.Settings;
import ru.ys.mfc.equipment.InputDevice;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class Utils {
    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

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

    public static void exit(int code) {
        try {
            InputDevice.getInstance().getTablet().setClearScreen();
            DrawingUtils.loadAndShowImage("mfclogo.jpg");
            Thread.sleep(1000);
//            InputDevice.getInstance().getTablet().disconnect();
        } catch (Exception e) {
            LOGGER.error("public static void exit(int code)", e);
        }
        LOGGER.info("System.exit({})", code);
        System.exit(code);
    }

    public static void initTabletMFC() {

    }
}
