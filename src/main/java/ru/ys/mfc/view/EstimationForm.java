package ru.ys.mfc.view;

import com.WacomGSS.STU.ITabletHandler;
import com.WacomGSS.STU.Protocol.*;
import com.WacomGSS.STU.STUException;
import com.WacomGSS.STU.Tablet;
import ru.ys.mfc.equipment.InputDevice;
import ru.ys.mfc.model.Answer;
import ru.ys.mfc.util.DrawingUtils;

import java.awt.Rectangle;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class EstimationForm implements ITabletHandler {
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
    private int pad = 5;
    private List<Answer> answers;
    private List<Button> buttons = new ArrayList<>();

    public EstimationForm(List<Answer> answers) throws InterruptedException, STUException {
        this.answers = answers;
        doNotProcessing = false;
        tablet = InputDevice.getInstance().getTablet();

        if (!tablet.isConnected()) {
            int e = -1;
            for (int i = 0; i < 100; i++) {
                e = tablet.usbConnect(InputDevice.getInstance().getUsbDevice(), true);
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
        tablet.setClearScreen();
        capability = tablet.getCapability();
        Information information = tablet.getInformation();
        modelName = information.getModelName();

        headerHeight = 2 * capability.getScreenHeight() / 3;

        byte encodingFlag = ProtocolHelper.simulateEncodingFlag(
                tablet.getProductId(),
                capability.getEncodingFlag());

        if ((encodingFlag & EncodingFlag.EncodingFlag_24bit) != 0) {
            this.encodingMode = this.tablet.supportsWrite() ? EncodingMode.EncodingMode_24bit_Bulk : EncodingMode.EncodingMode_24bit;
        } else if ((encodingFlag & EncodingFlag.EncodingFlag_16bit) != 0) {
            this.encodingMode = this.tablet.supportsWrite() ? EncodingMode.EncodingMode_16bit_Bulk : EncodingMode.EncodingMode_16bit;
        } else {
            this.encodingMode = EncodingMode.EncodingMode_1bit;
        }

        BufferedImage bitmap = new BufferedImage(this.capability.getScreenWidth(), this.capability.getScreenHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D gfx = bitmap.createGraphics();
        gfx.setColor(Color.WHITE);
        gfx.fillRect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        double fontSize = bitmap.getHeight() / 16;

        // Draw question
        gfx.setColor(Color.BLACK);
        gfx.setFont(new Font("Times New Roman", Font.BOLD, (int) fontSize));

//        int offset = capability.getScreenHeight() / answers.size();
        int offset = 0;
        for (int i = 0; i < answers.size(); i++) {
            Answer answer = answers.get(i);
            String id = answer.getId();
            String text = "".equals(answer.getAltTitle()) ? answer.getTitle() : answer.getAltTitle();
            RectangleDimensions buttonDimension = getAnswerButtonDimension();
            java.awt.Rectangle bounds = new Rectangle();
            bounds.x = pad;
            bounds.y = offset + i * (buttonDimension.height) + 4;
            bounds.width = buttonDimension.widht;
            bounds.height = buttonDimension.height;
            Button button = new Button(gfx, bounds, text, ButtonType.ANSWERVARIANT, id,
                    Color.WHITE,
                    new Color(224, 78, 57));
            buttons.add(button);
        }

        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).draw();
            gfx.setColor(Color.WHITE);
            gfx.drawRoundRect(buttons.get(i).getBounds().x,
                    buttons.get(i).getBounds().y,
                    buttons.get(i).getBounds().width,
                    buttons.get(i).getBounds().height,
                    40, 40);
        }
        gfx.dispose();

        bitmapData = ProtocolHelper.flatten(bitmap, bitmap.getWidth(), bitmap.getHeight(), encodingMode);

        // Add the delegate that receives pen data.
        tablet.addTabletHandler(this);

        // Enable the pen data on the screen (if not already)
        tablet.setInkingMode(InkingMode.Off);

        try {
            tablet.writeImage(this.encodingMode, this.bitmapData);
            tablet.endImageData();
            pressedButton = null;
        } catch (Exception ex) {
            throw new RuntimeException("Неудачное подключение к планшету: " + ex.getLocalizedMessage());
        }
    }

    private RectangleDimensions getAnswerButtonDimension() {
        int answersAreaHeight = capability.getScreenHeight();
        RectangleDimensions dim = new RectangleDimensions();
        dim.height = (answersAreaHeight) / answers.size() - (pad / 2);
        dim.widht = capability.getScreenWidth() - pad * 2;
        return dim;
    }

    public ru.ys.mfc.view.Button getPressedButton() {
        return pressedButton;
    }

    public void waitForButtonPress() throws InterruptedException {
        while (pressedButton == null) {
            Thread.sleep(500);
            Thread.yield();
        }
    }

    @Override
    public void onGetReportException(STUException e) {
        System.out.println("onGetReportException:");
        e.printStackTrace();
    }

    @Override
    public void onUnhandledReportData(byte[] bytes) {
        System.out.println("onUnhandledReportData:" + bytes);
    }

    @Override
    public void onPenData(PenData penData) {
        if (penData.getSw() == 0) return;
        if (doNotProcessing) return;
        doNotProcessing = true;
        pressedButton(penData);
    }

    private void pressedButton(PenData penData) {
        Point2D.Float point = DrawingUtils.tabletToScreen(penData, capability);
        for (int i = 0; i < buttons.size(); i++) {
            Button button = buttons.get(i);
            if (button.getBounds().contains(Math.round(point.getX()), Math.round(point.getY()))) {
                pressedButton = button;
                break;
            }
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
        if (penDataTimeCountSequence.getSw() == 0) return;
        if (doNotProcessing) return;
        doNotProcessing = true;
        pressedButton(penDataTimeCountSequence);
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

    private static class RectangleDimensions {
        int height = 0;
        int widht = 0;
    }
}
