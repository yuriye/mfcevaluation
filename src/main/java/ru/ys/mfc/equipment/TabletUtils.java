package ru.ys.mfc.equipment;

import com.WacomGSS.STU.Protocol.InkingMode;
import com.WacomGSS.STU.Tablet;

public class TabletUtils {

    public static void dispose(Tablet tablet) {
        if (tablet == null)
            return;
        try {
            tablet.setInkingMode(InkingMode.Off);
            tablet.setClearScreen();
        } catch (Throwable ignored) {
        }
        tablet.disconnect();
        tablet = null;
    }
}
