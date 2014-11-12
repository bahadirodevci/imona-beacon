package com.imona.rbd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.bluetooth.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class DiscoverThr implements Runnable {
    public static List<MobileDevice> currentDeviceList = new ArrayList<>();
    public static List<MobileDevice> previousDeviceList = new ArrayList<>();

    private static Logger logger = LoggerFactory.getLogger(DiscoverThr.class);

//    public void setUniqueDevices() {
//
//    }
//
//    public boolean isNewDevice(String macAddress) {
//        for (MobileDevice md : currentDeviceList) {
//            if (Objects.equals(md.macAddress, macAddress)) {
//                md.newestDisvoceredTime = new Date();
//                return false;
//            }
//        }
//        return true;
//    }

    public void addNewDevice(String macAddress, String friendlyName) {
        MobileDevice device = new MobileDevice();
        device.firstDiscoveredTime = new Date();
        device.friendlyName = friendlyName;
        device.macAddress = macAddress;
        device.isSendToImona = false;
        currentDeviceList.add(device);
    }

    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                final Object inquiryCompletedEvent = new Object();
                currentDeviceList.clear();
                DiscoveryListener listener = new DiscoveryListener() {

                    public void deviceDiscovered(RemoteDevice btDevice,
                                                 DeviceClass cod) {
                        try {
                            addNewDevice(btDevice.getBluetoothAddress(),
                                    btDevice.getFriendlyName(false));
                        } catch (IOException e) {
                            logger.error(e.getMessage(), e);
                        }
                    }

                    public void inquiryCompleted(int discType) {
                        synchronized (inquiryCompletedEvent) {
                            inquiryCompletedEvent.notifyAll();
                        }
                    }

                    public void serviceSearchCompleted(int transID, int respCode) {
                    }

                    public void servicesDiscovered(int transID,
                                                   ServiceRecord[] servRecord) {
                    }
                };

                //noinspection SynchronizationOnLocalVariableOrMethodParameter
                synchronized (inquiryCompletedEvent) {
                    boolean started = LocalDevice.getLocalDevice()
                            .getDiscoveryAgent()
                            .startInquiry(DiscoveryAgent.GIAC, listener);
                    if (started) {
                        inquiryCompletedEvent.wait();

                        logger.info(currentDeviceList.size() + " device(s) found");
                    }
                }
                callRestService();
                Thread.sleep(Constants.REFRESH_INTERVAL);
            } catch (InterruptedException e) {
                // propagate interrupt
                Thread.currentThread().interrupt();
                logger.error(e.getMessage(), e);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        logger.info("Device discovery ended...");
    }

    public void callRestService() {
        String receivedMacs = "";
        String leftMacs = "";

        for (MobileDevice curr : currentDeviceList) {
            boolean isPrev = false;
            for (MobileDevice prev : previousDeviceList) {
                if (Objects.equals(prev.macAddress, curr.macAddress)) {
                    isPrev = true;
                    break;
                }
            }
            if (!isPrev) {
                receivedMacs += curr.macAddress + ",";
            }
        }

        // fill left macs...
        for (MobileDevice prev : previousDeviceList) {
            boolean isInBuilding = false;
            for (MobileDevice curr : currentDeviceList) {
                if (Objects.equals(curr.macAddress, prev.macAddress)) {
                    isInBuilding = true;
                    break;
                }
            }
            if (!isInBuilding) {
                leftMacs += prev.macAddress + ",";
            }
        }

        if (!"".equals(receivedMacs)) {
            receivedMacs = receivedMacs.substring(0, receivedMacs.length() - 1);
            // call rest...
            RestCall.deviceDetected(Constants.IMONA_BRANCH_ID, receivedMacs);
            logger.info("service welcome call branchId:"
                    + Constants.IMONA_BRANCH_ID + "%macs:" + receivedMacs);
        }

        if (!"".equals(leftMacs)) {
            leftMacs = leftMacs.substring(0, leftMacs.length() - 1);

            RestCall.deviceLeft(Constants.IMONA_BRANCH_ID, leftMacs);
            // call rest...
            logger.info("service left devices call branchId:"
                    + Constants.IMONA_BRANCH_ID + "%macs:" + leftMacs);
        }

        previousDeviceList.clear();

        for (MobileDevice device : currentDeviceList) {
            previousDeviceList.add(device);
        }
    }
}
