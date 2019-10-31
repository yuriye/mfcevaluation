package ru.ys.mfc.equipment;

import com.WacomGSS.STU.Protocol.EncodingFlag;
import com.WacomGSS.STU.Protocol.EncodingMode;
import com.WacomGSS.STU.Protocol.ProtocolHelper;
import com.WacomGSS.STU.Tablet;
import com.WacomGSS.STU.UsbDevice;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.WacomGSS.STU.Protocol.InkingMode.Off;

public class InputDevice {
    private static final Logger LOGGER = LoggerFactory.getLogger(InputDevice.class);

    private static InputDevice inputDeviceInstance;
    private UsbDevice usbDevice;
    private Tablet tablet;
    private EncodingMode encodingMode;

    private InputDevice() {
    }

    public static synchronized InputDevice getInstance() {
        try {
            if (inputDeviceInstance == null) {
                inputDeviceInstance = new InputDevice();
                inputDeviceInstance.init();
            }
        } catch (IOException e) {
            LOGGER.error("public static synchronized InputDevice getInstance()", e);
        }
        return inputDeviceInstance;
    }

    public void init() throws IOException {
        try {
            System.loadLibrary("wgssSTU");
        } catch (UnsatisfiedLinkError ue) {
            LOGGER.error("Попытка загрузить wgssSTU не удалась", ue);
            String name = "wgssSTU.dll";
            Path path = FileSystems.getDefault().getPath(".", name);

            try (InputStream input = ru.ys.mfc.Main.class.getResourceAsStream("/" + name)) {
                if (input == null) {
                    LOGGER.error("Не найден ресурс wgssSTU.dll");
                    throw new FileNotFoundException("Не найден ресурс wgssSTU.dll");
                }
                Files.copy(input, path);
                System.loadLibrary("wgssSTU");
            } catch (IOException e) {
                LOGGER.error("2 попытка загрузить wgssSTU не удалась", e);
                throw e;
            }
        }

        try {
            usbDevice = UsbDevice.getUsbDevices()[0];
            tablet = new Tablet();
            tablet.usbConnect(usbDevice, true);
            Thread.sleep(2000);
//            tablet.reset();
            tablet.setInkingMode(Off);
            byte encodingFlag = ProtocolHelper.simulateEncodingFlag(
                    tablet.getProductId(),
                    tablet.getCapability().getEncodingFlag());
            if ((encodingFlag & EncodingFlag.EncodingFlag_24bit) != 0) {
                encodingMode = this.tablet.supportsWrite() ? EncodingMode.EncodingMode_24bit_Bulk : EncodingMode.EncodingMode_24bit;
            } else if ((encodingFlag & EncodingFlag.EncodingFlag_16bit) != 0) {
                encodingMode = this.tablet.supportsWrite() ? EncodingMode.EncodingMode_16bit_Bulk : EncodingMode.EncodingMode_16bit;
            } else {
                encodingMode = EncodingMode.EncodingMode_1bit;
            }
//            tablet.setClearScreen();
        } catch (Exception e) {
            LOGGER.error("public void init()", e);
            JOptionPane.showMessageDialog(null, ExceptionUtils.getStackTrace(e));
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
            LOGGER.error("disconnect()", e);
            JOptionPane.showMessageDialog(null, ExceptionUtils.getStackTrace(e));
            System.exit(1);
        }
    }

    public Tablet getTablet() {
        return tablet;
    }

    public UsbDevice getUsbDevice() {
        return usbDevice;
    }

    public EncodingMode getIncodingMode() {
        return encodingMode;
    }
}
