package ru.ys.mfc.view;

import com.WacomGSS.STU.ITabletHandler;
import com.WacomGSS.STU.Protocol.*;
import com.WacomGSS.STU.STUException;
import com.WacomGSS.STU.Tablet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ys.mfc.equipment.InputDevice;
import ru.ys.mfc.util.DrawingUtils;

import java.awt.Rectangle;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public class QuestionForm implements ITabletHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(QuestionForm.class);

    private Tablet tablet;
    private Capability capability;
    private String modelName;
    private int headerHeight;
    private EncodingMode encodingMode;
    private byte[] bitmapData;
    private ru.ys.mfc.view.Button answerButton;
    private ru.ys.mfc.view.Button cancelButton;
    private ru.ys.mfc.view.Button pressedButton;
    private boolean doNotProcessing = false;

    public QuestionForm(String indicatorDescription) throws STUException {
        doNotProcessing = false;
        tablet = InputDevice.getInstance().getTablet();
//        if (tablet.isSupported(OperationModeType.KeyPad))
//            System.out.println("KeyPad supported");
//        else
//            System.out.println("No keypad supported");

//        OperationMode_KeyPad opMode = new OperationMode_KeyPad((byte)1,(byte)2);
//        tablet.setOperationMode(OperationMode.initializeKeyPad(opMode));
//        System.out.println(tablet.getProtocol().getOperationMode());

        if (!tablet.isConnected()) {
            int e = -1;
            for (int i = 0; i < 100; i++) {
                e = tablet.usbConnect(InputDevice.getInstance().getUsbDevice(), true);
                if (e == 0) {
                    break;
                } else {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException inte) {
                        LOGGER.error("Проблема с подключением к планшету", inte);
                    }

                }
            }
            if (e != 0) {
                LOGGER.error("Failed to connect to USB tablet, error {}", e);
                throw new RuntimeException("Failed to connect to USB tablet, error " + e);
            }
        }
        tablet.setClearScreen();
        capability = tablet.getCapability();
        Information information = tablet.getInformation();
        modelName = information.getModelName();

        headerHeight = 2 * capability.getScreenHeight() / 3;

        byte encodingFlag = ProtocolHelper.simulateEncodingFlag(
                tablet.getProductId(),
                capability.getEncodingFlag());

//        boolean useColor = ProtocolHelper.encodingFlagSupportsColor(encodingFlag);

        if ((encodingFlag & EncodingFlag.EncodingFlag_24bit) != 0) {
            this.encodingMode = this.tablet.supportsWrite() ? EncodingMode.EncodingMode_24bit_Bulk : EncodingMode.EncodingMode_24bit;
        } else if ((encodingFlag & EncodingFlag.EncodingFlag_16bit) != 0) {
            this.encodingMode = this.tablet.supportsWrite() ? EncodingMode.EncodingMode_16bit_Bulk : EncodingMode.EncodingMode_16bit;
        } else {
            this.encodingMode = EncodingMode.EncodingMode_1bit;
        }

        BufferedImage bitmap = new BufferedImage(this.capability.getScreenWidth(), this.capability.getScreenHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D gfx = bitmap.createGraphics();
//        gfx.setBackground(new Color(98, 59, 42));
        gfx.setBackground(new Color(224, 78, 57));
        gfx.setColor(new Color(224, 78, 57));
        gfx.fillRect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        long fontSize = Math.round(bitmap.getHeight() / 12.0);


        // Draw question
        gfx.setColor(Color.WHITE);
        gfx.setFont(new Font("Times New Roman", Font.BOLD, (int) fontSize));
        DrawingUtils.drawLongStringBySpliting(gfx, indicatorDescription,
                (int) 40, 0,
                (int) capability.getScreenWidth() - 80,
                (int) headerHeight,
                true);

        gfx.setFont(new Font("Times New Roman", Font.BOLD, (int) fontSize + 2));

        Rectangle cancelButtonRectangle = new Rectangle(20,
                capability.getScreenHeight() - capability.getScreenHeight() / 3 + 2 + 20,
                capability.getScreenWidth() / 2 - 2 - 40,
                capability.getScreenHeight() / 3 - 2 - 40);

        cancelButton = new Button(gfx, cancelButtonRectangle, "Прервать оценку", ButtonType.CANCELESTIMATION, "cancel",
                new Color(250, 220, 214),
                Color.WHITE);

        Rectangle answerButtonRectangle = new Rectangle(capability.getScreenWidth() / 2 + 2 + 20,
                capability.getScreenHeight() - capability.getScreenHeight() / 3 + 2 + 20,
                capability.getScreenWidth() / 2 - 2 - 40,
                capability.getScreenHeight() / 3 - 2 - 40);

        answerButton = new Button(gfx, answerButtonRectangle, "Оценить", ButtonType.ANSWER, "answer",
                new Color(224, 78, 57),
                Color.WHITE);

        cancelButton.draw();
        answerButton.draw();

        gfx.dispose();
        bitmapData = ProtocolHelper.flatten(bitmap, bitmap.getWidth(), bitmap.getHeight(), encodingMode);

        // Add the delagate that receives pen data.
        tablet.addTabletHandler(this);

        // Enable the pen data on the screen (if not already)
        tablet.setInkingMode(InkingMode.Off);

        try {
            tablet.writeImage(this.encodingMode, this.bitmapData);
            tablet.endImageData();
            pressedButton = null;
        } catch (Exception ex) {
            LOGGER.error("Неудачное подключение к планшету: ", ex.getLocalizedMessage());
            throw new RuntimeException("Неудачное подключение к планшету: " + ex.getLocalizedMessage());
        }
    }

    public ru.ys.mfc.view.Button getPressedButton() {
        return pressedButton;
    }

    public void waitForButtonPress() {
        try {
            while (pressedButton == null) {
                Thread.sleep(500);
                Thread.yield();
            }
        } catch (InterruptedException inte) {
            LOGGER.error("InterruptedException во время ожидания нажатия кнопки", inte);
        }
    }

    @Override
    public void onGetReportException(STUException e) {
        LOGGER.error("onGetReportException:", e);
    }

    @Override
    public void onUnhandledReportData(byte[] bytes) {

    }

    @Override
    public void onPenData(PenData penData) {
        processPressed(penData);
    }

    private void processPressed(PenData penData) {
//        LOGGER.debug("processPressed penData.getSw(): {} doNotProcessing: {}", penData.getSw(), doNotProcessing);
        if (penData.getSw() == 0) return;
        if (doNotProcessing) return;
        doNotProcessing = true;
        Point2D.Float point = DrawingUtils.tabletToScreen(penData, capability);
        if (cancelButton.getBounds().contains(Math.round(point.getX()), Math.round(point.getY()))) {
            pressedButton = cancelButton;
            LOGGER.debug("Cancel button pressed");
        } else if (answerButton.getBounds().contains(Math.round(point.getX()), Math.round(point.getY()))) {
            pressedButton = answerButton;
        } else {
            pressedButton = null;
            doNotProcessing = false;
        }
    }

    public boolean isDoNotProcessing() {
        return doNotProcessing;
    }

    public void setDoNotProcessing(boolean doNotProcessing) {
        this.doNotProcessing = doNotProcessing;
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
        processPressed(penDataTimeCountSequence);
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
