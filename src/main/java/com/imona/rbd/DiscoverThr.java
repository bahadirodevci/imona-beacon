package com.imona.rbd;

import javax.bluetooth.*;
import java.io.IOException;
import java.util.*;

public class DiscoverThr implements Runnable {
	public static List<MobileDevice> devicesDiscoveredLatest = new ArrayList<MobileDevice>();
	public static final Vector uniquedevicesDiscoveredToSend = new Vector();

	public void setUniqueDevices() {
			
	}

    public boolean isNewDevice(String macAddress) {
        for (MobileDevice md : devicesDiscoveredLatest) {
            if (Objects.equals(md.macAddress, macAddress)) {
                md.newestDisvoceredTime = new Date();
                return false;
            }
        }
        return true;
    }

    public void addNewDevice(String macAddress, String friendlyName){
		MobileDevice device= new MobileDevice();
		device.firstDiscoveredTime=new Date();
		device.friendlyName=friendlyName;
		device.macAddress=macAddress;
	}
	
	public void run() {
		try {
			final Object inquiryCompletedEvent = new Object();

			devicesDiscoveredLatest.clear();

			DiscoveryListener listener = new DiscoveryListener() {

				public void deviceDiscovered(RemoteDevice btDevice,
						DeviceClass cod) {
					boolean isNewDevice=isNewDevice(btDevice.getBluetoothAddress());
					if (isNewDevice) {
						try {
							addNewDevice(btDevice.getBluetoothAddress(),btDevice.getFriendlyName(false));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

				public void inquiryCompleted(int discType) {
					System.out.println("Device Inquiry completed!");
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
					System.out
							.println("wait for device inquiry to complete...");
					inquiryCompletedEvent.wait();
					System.out.println(devicesDiscoveredLatest.size()
							+ " device(s) found");
				}
			}
		} catch (Exception e) {
			System.out.print("Error occured... " + e.getMessage());

		}

		System.out.println("device discovery end...");
	}
	

}
