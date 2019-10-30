package ru.ys.mfc.equipment;

import com.WacomGSS.STU.Protocol.InkingMode;
import com.WacomGSS.STU.Tablet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TabletUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(TabletUtils.class);

    public static void dispose(Tablet tablet) {
        if (tablet == null)
            return;
        try {
            tablet.setInkingMode(InkingMode.Off);
            tablet.setClearScreen();
        } catch (Throwable ignored) {
            LOGGER.debug("public static void dispose(Tablet tablet)", ignored);
        }
        tablet.disconnect();
        tablet = null;
    }
}
