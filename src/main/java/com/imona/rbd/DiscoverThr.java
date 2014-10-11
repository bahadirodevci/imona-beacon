package com.imona.rbd;

import javax.bluetooth.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class DiscoverThr implements Runnable {
    public static List<MobileDevice> currentDeviceList = new ArrayList<MobileDevice>();
    public static List<MobileDevice> previousDeviceList = new ArrayList<MobileDevice>();

    public void setUniqueDevices() {

    }

    public boolean isNewDevice(String macAddress) {
        for (MobileDevice md : currentDeviceList) {
            if (Objects.equals(md.macAddress, macAddress)) {
                md.newestDisvoceredTime = new Date();
                return false;
            }
        }
        return true;
    }

    public void addNewDevice(String macAddress, String friendlyName) {
        MobileDevice device = new MobileDevice();
        device.firstDiscoveredTime = new Date();
        device.friendlyName = friendlyName;
        device.macAddress = macAddress;
        device.isSendToImona = false;
        currentDeviceList.add(device);
    }

    public void run() {
        try {
            while (true) {

                final Object inquiryCompletedEvent = new Object();

                currentDeviceList.clear();

                DiscoveryListener listener = new DiscoveryListener() {

                    public void deviceDiscovered(RemoteDevice btDevice,
                                                 DeviceClass cod) {
                        try {
                            addNewDevice(btDevice.getBluetoothAddress(),
                                    btDevice.getFriendlyName(false));
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
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

                synchronized (inquiryCompletedEvent) {
                    boolean started = LocalDevice.getLocalDevice()
                            .getDiscoveryAgent()
                            .startInquiry(DiscoveryAgent.GIAC, listener);
                    if (started) {

                        inquiryCompletedEvent.wait();
                        System.out.println(currentDeviceList.size()
                                + " device(s) found");
                    }
                }
                callRestService();
                Thread.sleep(Constants.WaitRefrInterval);
            }

        } catch (Exception e) {
            System.out.print("Error occured... " + e.getMessage());

        }

        System.out.println("device discovery end...");

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

        //

        if (!"".equals(receivedMacs)) {
            receivedMacs = receivedMacs.substring(0, receivedMacs.length() - 1);
            // call rest...
            RestCall.deviceDetected(Constants.IMONA_BRANCH_ID, receivedMacs);
            System.out.println("service welcome call branchId:"
                    + Constants.IMONA_BRANCH_ID + "%macs:" + receivedMacs);
        }

        if (!"".equals(leftMacs)) {
            leftMacs = leftMacs.substring(0, leftMacs.length() - 1);

            RestCall.deviceLeft(Constants.IMONA_BRANCH_ID, leftMacs);
            // call rest...
            System.out.println("service left devices call branchId:"
                    + Constants.IMONA_BRANCH_ID + "%macs:" + leftMacs);
        }

        previousDeviceList.clear();

        for (MobileDevice device : currentDeviceList) {
            previousDeviceList.add(device);
        }
    }
}
