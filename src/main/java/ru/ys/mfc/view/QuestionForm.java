package ru.ys.mfc.view;

import com.WacomGSS.STU.ITabletHandler;
import com.WacomGSS.STU.Protocol.*;
import com.WacomGSS.STU.STUException;
import com.WacomGSS.STU.Tablet;
import ru.ys.mfc.equipment.InputDevice;
import ru.ys.mfc.util.DrawingUtils;

import java.awt.Rectangle;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public class QuestionForm implements ITabletHandler {
    private Tablet tablet;
    private Capability capability;
    private String modelName;
    private int headerHeight;
    private EncodingMode encodingMode;
    private byte[] bitmapData;
    private Button answerButton;
    private Button cancelButton;
    private Button pressedButton;


    public QuestionForm(String indicatorDescription) throws InterruptedException, STUException {
        InputDevice inputDevice = InputDevice.getInstance();
        tablet = inputDevice.getTablet();
        if (!tablet.isConnected()) {
            int e = -1;
            for (int i = 0; i < 100; i++) {
                e = tablet.usbConnect(inputDevice.getUsbDevice(), true);
                if (e == 0) {
                    break;
                } else {
                    Thread.sleep(2000);
                }
            }
            if (e != 0) {
                throw new RuntimeException("Failed to connect to USB tablet, error " + e);
            }
        }
        capability = tablet.getCapability();
        Information information = tablet.getInformation();
        modelName = information.getModelName();

        this.headerHeight = this.capability.getScreenHeight() / 3;
        int offset = this.headerHeight;
        byte encodingFlag = ProtocolHelper.simulateEncodingFlag(
                tablet.getProductId(),
                capability.getEncodingFlag());

        boolean useColor = ProtocolHelper
                .encodingFlagSupportsColor(encodingFlag);

        useColor = useColor && this.tablet.supportsWrite();

        // Calculate the encodingMode that will be used to update the image
        if (useColor) {
            if (this.tablet.supportsWrite())
                this.encodingMode = EncodingMode.EncodingMode_16bit_Bulk;
            else
                this.encodingMode = EncodingMode.EncodingMode_16bit;
        } else {
            this.encodingMode = EncodingMode.EncodingMode_1bit;
        }

        BufferedImage bitmap = new BufferedImage(this.capability.getScreenWidth(), this.capability.getScreenHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D gfx = bitmap.createGraphics();
        gfx.setColor(Color.WHITE);
        gfx.fillRect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        double fontSize = bitmap.getHeight() / 9;

        // Draw question
        gfx.setColor(Color.BLACK);
        gfx.setFont(new Font("Times New Roman", Font.BOLD, (int) fontSize));
        DrawingUtils.drawLongStringBySpliting(gfx, indicatorDescription,
                (int) 0, 0,
                (int) capability.getScreenWidth(),
                (int) headerHeight,
                false);

        Rectangle cancelButtonRectangle = new Rectangle(0,
                capability.getScreenHeight() - capability.getScreenHeight() / 3 + 2,
                capability.getScreenWidth() / 2 - 2,
                capability.getScreenHeight() / 3 - 2);

        cancelButton = new Button(gfx, cancelButtonRectangle, "Прервать процесс оценки", ButtonType.CANCELESTIMATION, "cancel");

        Rectangle answerButtonRectangle = new Rectangle(capability.getScreenWidth() / 2 + 2,
                capability.getScreenHeight() - capability.getScreenHeight() / 3 + 2,
                capability.getScreenWidth() / 2 - 2,
                capability.getScreenHeight() / 3 - 2);

        answerButton = new Button(gfx, answerButtonRectangle, "Оценить", ButtonType.ANSWER, "answer");

        cancelButton.draw();
        answerButton.draw();

        gfx.dispose();

        bitmapData = ProtocolHelper.flatten(bitmap,
                bitmap.getWidth(), bitmap.getHeight(),
                useColor);

        // Add the delagate that receives pen data.
        tablet.addTabletHandler(this);

        // Enable the pen data on the screen (if not already)
        tablet.setInkingMode(InkingMode.Off);

        int connectionError = 0;
        try {
            tablet.writeImage(this.encodingMode, this.bitmapData);
        } catch (Exception ex) {
            for (int i = 0; i < 20; i++) {
                if (!tablet.isConnected())
                    connectionError = tablet.usbConnect(inputDevice.getUsbDevice(), true);
                if (connectionError == 0) {
                    break;
                } else {
                    Thread.sleep(2000);
                }
            }
            if (connectionError != 0) {
                throw new RuntimeException("Неудачное подключение к планшету: " + ex.getLocalizedMessage());
            }
        }
        tablet.endImageData();
        pressedButton = null;

    }

    private void detectPressedButton(PenData penData) {
        Point2D.Float point = DrawingUtils.tabletToScreen(penData, capability);
        if (cancelButton.getBounds().contains(Math.round(point.getX()), Math.round(point.getY())))
            pressedButton = cancelButton;
        else if (answerButton.getBounds().contains(Math.round(point.getX()), Math.round(point.getY())))
            pressedButton = answerButton;
        else pressedButton = null;
    }

    public void waitForButtonPress() throws InterruptedException {
        while (pressedButton == null) {
            Thread.sleep(100);
            Thread.yield();
        }
    }

    @Override
    public void onGetReportException(STUException e) {

    }

    @Override
    public void onUnhandledReportData(byte[] bytes) {

    }

    @Override
    public void onPenData(PenData penData) {

    }

    @Override
    public void onPenDataOption(PenDataOption penDataOption) {

    }

    @Override
    public void onPenDataEncrypted(PenDataEncrypted penDataEncrypted) {

    }

    @Override
    public void onPenDataEncryptedOption(PenDataEncryptedOption penDataEncryptedOption) {

    }

    @Override
    public void onPenDataTimeCountSequence(PenDataTimeCountSequence penDataTimeCountSequence) {

    }

    @Override
    public void onPenDataTimeCountSequenceEncrypted(PenDataTimeCountSequenceEncrypted penDataTimeCountSequenceEncrypted) {

    }

    @Override
    public void onEventDataPinPad(EventDataPinPad eventDataPinPad) {

    }

    @Override
    public void onEventDataKeyPad(EventDataKeyPad eventDataKeyPad) {

    }

    @Override
    public void onEventDataSignature(EventDataSignature eventDataSignature) {

    }

    @Override
    public void onEventDataPinPadEncrypted(EventDataPinPadEncrypted eventDataPinPadEncrypted) {

    }

    @Override
    public void onEventDataKeyPadEncrypted(EventDataKeyPadEncrypted eventDataKeyPadEncrypted) {

    }

    @Override
    public void onEventDataSignatureEncrypted(EventDataSignatureEncrypted eventDataSignatureEncrypted) {

    }

    @Override
    public void onDevicePublicKey(DevicePublicKey devicePublicKey) {

    }

    @Override
    public void onEncryptionStatus(EncryptionStatus encryptionStatus) {

    }
}
