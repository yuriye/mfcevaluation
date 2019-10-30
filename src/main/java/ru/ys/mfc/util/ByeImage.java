package ru.ys.mfc.util;

import com.WacomGSS.STU.Protocol.Capability;
import com.WacomGSS.STU.Protocol.EncodingMode;
import com.WacomGSS.STU.Protocol.ProtocolHelper;
import com.WacomGSS.STU.STUException;
import com.WacomGSS.STU.Tablet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ys.mfc.equipment.InputDevice;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ByeImage {
    private static final Logger LOGGER = LoggerFactory.getLogger(ByeImage.class);

    BufferedImage bitmap;
    private Capability capability;
    private String message = "Спасибо! Ваша оценка успешно сохранена.";

    public ByeImage(String message) {
        try {
            this.capability = InputDevice.getInstance().getTablet().getCapability();
        } catch (STUException e) {
            LOGGER.error("public ByeImage(String message)", e);
        }
        this.message = message;
        createImage();
    }

    public void createImage() {
        this.bitmap = new BufferedImage(
                this.capability.getScreenWidth(),
                this.capability.getScreenHeight(),
                BufferedImage.TYPE_INT_RGB);
        {
            Graphics2D gfx = bitmap.createGraphics();
            gfx.setColor(Color.WHITE);
            gfx.fillRect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            double fontSize = 40; // pixels
            gfx.setColor(new Color(224, 78, 57));
            gfx.setFont(new Font("Times New Roman", Font.BOLD, (int) fontSize));
            DrawingUtils.drawLongStringBySpliting(gfx, message,
                    0, 0,
                    this.capability.getScreenWidth(),
                    this.capability.getScreenHeight(),
                    true);
            gfx.dispose();
        }
    }

    public BufferedImage getBitmap() {
        return bitmap;
    }

    public void show() {
        EncodingMode encodingMode = InputDevice.getInstance().getIncodingMode();
        byte[] bitmapData = ProtocolHelper.flatten(bitmap, bitmap.getWidth(), bitmap.getHeight(), encodingMode);
        Tablet tablet = InputDevice.getInstance().getTablet();
        try {
            tablet.writeImage(encodingMode, bitmapData);
            tablet.endImageData();
        } catch (Exception ex) {
            LOGGER.error("Неудачное подключение к планшету", ex);
            throw new RuntimeException("Неудачное подключение к планшету: " + ex.getLocalizedMessage());
        }
    }
}
