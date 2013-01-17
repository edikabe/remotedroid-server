package com.jsera;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class DiscoverableThread extends Thread {
	private static int BUFFER_LENGTH = 1024;
	public static String MULTICAST_ADDRESS = "230.6.6.6";
	// private static final String ID_REQUEST = "RemoteDroid:AnyoneHome";
	// private static final String ID_REQUEST_RESPONSE = "RemoteDroid:ImHome";
	private int port = 57111;
	private MulticastSocket socket;

	public DiscoverableThread() {
	}

	public DiscoverableThread(int port) {
		this.port = port;
	}

	public DiscoverableThread(Runnable target) {
		super(target);
	}

	public DiscoverableThread(String name) {
		super(name);
	}

	public DiscoverableThread(ThreadGroup group, Runnable target) {
		super(group, target);
	}

	public DiscoverableThread(ThreadGroup group, String name) {
		super(group, name);
	}

	public DiscoverableThread(Runnable target, String name) {
		super(target, name);
	}

	public DiscoverableThread(ThreadGroup group, Runnable target, String name) {
		super(group, target, name);
	}

	public DiscoverableThread(ThreadGroup group, Runnable target, String name,
			long stackSize) {
		super(group, target, name, stackSize);
	}

	@Override
	public void run() {
		try {
			byte[] b = new byte[BUFFER_LENGTH];
			DatagramPacket packet = new DatagramPacket(b, b.length);
			this.socket = new MulticastSocket(this.port);
			this.socket.joinGroup(InetAddress.getByName(MULTICAST_ADDRESS));
			while (true) {
				this.socket.receive(packet);
				handlePacket(packet);
			}
		} catch (IOException localIOException) {
		} catch (InterruptedException localInterruptedException) {
		}
	}

	private void handlePacket(DatagramPacket packet) throws IOException,
			InterruptedException {
		String data = new String(packet.getData());
		System.out.println("Got data:" + data);
		if (data.substring(0, "RemoteDroid:AnyoneHome".length()).equals(
				"RemoteDroid:AnyoneHome")) {
			System.out.println("Request message!");

			byte[] b = "RemoteDroid:ImHome".getBytes();
			DatagramPacket p = new DatagramPacket(b, b.length);
			p.setAddress(packet.getAddress());
			p.setPort(this.port + 1);

			Thread.sleep(500L);
			DatagramSocket outSocket = new DatagramSocket();

			outSocket.send(p);
		}
	}
}