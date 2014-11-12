package com.imona.rbd;


/**
 * Minimal Device Discovery example.
 */
public class RemoteDeviceDiscovery {
	public static void main(String[] args) {
		new Thread(new DiscoverThr()).start();
	}
}