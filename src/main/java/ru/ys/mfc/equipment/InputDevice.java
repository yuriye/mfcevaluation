package ru.ys.mfc.equipment;

import com.WacomGSS.STU.Tablet;
import com.WacomGSS.STU.UsbDevice;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.WacomGSS.STU.Protocol.InkingMode.Off;

public class InputDevice {

    private static InputDevice inputDeviceInstance;
    private UsbDevice usbDevice;
    private Tablet tablet;

    private InputDevice() {
    }

    public static synchronized InputDevice getInstance() {
        try {
            if (inputDeviceInstance == null) {
                inputDeviceInstance = new InputDevice();
                inputDeviceInstance.init();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputDeviceInstance;
    }

    public void init() throws IOException {
        try {
            System.loadLibrary("wgssSTU");
        } catch (UnsatisfiedLinkError ue) {
            System.out.println("Попытка загрузить wgssSTU не удалась");
            ue.printStackTrace();
            String name = "wgssSTU.dll";
            Path path = FileSystems.getDefault().getPath(".", name);

            try (InputStream input = ru.ys.mfc.Main.class.getResourceAsStream("/" + name)) {
                if (input == null) {
                    throw new FileNotFoundException("Не найден ресурс wgssSTU.dll");
                }
                Files.copy(input, path, new CopyOption[0]);
                System.loadLibrary("wgssSTU");
            } catch (IOException e) {
                System.out.println("2 попытка загрузить wgssSTU не удалась");
                e.printStackTrace();
                throw e;
            }
        }

        try {
            usbDevice = UsbDevice.getUsbDevices()[0];
            tablet = new Tablet();
            tablet.usbConnect(usbDevice, true);
            tablet.setInkingMode(Off);
            tablet.setClearScreen();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog((Component) null, ExceptionUtils.getStackTrace(e));
            System.exit(1);
        }
    }

    public void disconnect() {
        try {
            if (tablet != null) {
                tablet.setClearScreen();
                tablet.disconnect();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog((Component) null, ExceptionUtils.getStackTrace(e));
            System.exit(1);
        }
    }

    public Tablet getTablet() {
        return tablet;
    }

    public UsbDevice getUsbDevice() {
        return usbDevice;
    }
}
