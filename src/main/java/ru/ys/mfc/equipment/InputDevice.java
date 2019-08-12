package ru.ys.mfc.equipment;

import com.WacomGSS.STU.Tablet;
import com.WacomGSS.STU.UsbDevice;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.swing.*;
import java.awt.*;

import static com.WacomGSS.STU.Protocol.InkingMode.Off;

public class InputDevice {

    private static InputDevice inputDeviceInstance;
    private UsbDevice usbDevice;
    private Tablet tablet;

    private InputDevice() {
    }

    public static synchronized InputDevice getInstance() {
        if (inputDeviceInstance == null) {
            inputDeviceInstance = new InputDevice();
        }
        inputDeviceInstance.init();
        return inputDeviceInstance;
    }

    public void init() {
        try {
            usbDevice = UsbDevice.getUsbDevices()[0];
            tablet = new Tablet();
            tablet.usbConnect(usbDevice, true);
            tablet.setInkingMode(Off);
            tablet.setClearScreen();
        } catch (Exception e) {
            JOptionPane.showMessageDialog((Component) null, ExceptionUtils.getStackTrace(e));
            System.exit(1);
        }
    }

    public void disconnect() {
        try {
            tablet.setClearScreen();
            tablet.disconnect();
        } catch (Exception e) {
            JOptionPane.showMessageDialog((Component) null, ExceptionUtils.getStackTrace(e));
            System.exit(1);
        }
    }

    public Tablet getTablet() {
        return tablet;
    }

}
