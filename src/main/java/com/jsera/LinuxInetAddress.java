package com.jsera;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class LinuxInetAddress {
	public static InetAddress getLocalHost() throws UnknownHostException {
		InetAddress localHost = InetAddress.getLocalHost();
		if (!localHost.isLoopbackAddress())
			return localHost;
		InetAddress[] addrs = getAllLocalUsingNetworkInterface();
		for (int i = 0; i < addrs.length; i++) {
			if ((!addrs[i].isLoopbackAddress())
					&& (addrs[i].getHostAddress().contains(".")))
				return addrs[i];
		}
		return localHost;
	}

	public static InetAddress[] getAllLocal() throws UnknownHostException {
		InetAddress[] iAddresses = InetAddress.getAllByName("127.0.0.1");
		if (iAddresses.length != 1)
			return iAddresses;
		if (!iAddresses[0].isLoopbackAddress())
			return iAddresses;
		return getAllLocalUsingNetworkInterface();
	}

	private static InetAddress[] getAllLocalUsingNetworkInterface()
			throws UnknownHostException {
		List<InetAddress> addresses = new ArrayList<InetAddress>();
		Enumeration<NetworkInterface> e = null;
		try {
			e = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException ex) {
			throw new UnknownHostException("127.0.0.1");
		}
		Enumeration<InetAddress> e2;
		for (; e.hasMoreElements(); e2.hasMoreElements()) {
			NetworkInterface ni = e.nextElement();
			e2 = ni.getInetAddresses();
			addresses.add(e2.nextElement());
		}

		InetAddress[] iAddresses = new InetAddress[addresses.size()];
		for (int i = 0; i < iAddresses.length; i++) {
			iAddresses[i] = (addresses.get(i));
		}
		return iAddresses;
	}
}

/*
 * Location:
 * C:\Users\julien.glotain\Downloads\RemoteDroidServer_v1.5\RemoteDroidServer
 * \RemoteDroidServer.jar Qualified Name: LinuxInetAddress JD-Core Version:
 * 0.6.2
 */