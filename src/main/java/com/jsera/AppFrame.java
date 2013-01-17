package com.jsera;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.Timer;

import com.jsera.util.ResourcesHelper;

public class AppFrame extends Frame {
	private static final long serialVersionUID = 1L;

	public static String basePath = "";
	public static InetAddress localAddr;
	private final String[] textLines = new String[6];
	private Image imLogo;
	private Image imHelp;
	private Font fontTitle;
	private Font fontText;
	private Timer timer;
	private final int height = 510;
	private final int width = 540;
	private OSCWorld world;
	private final String appName = "RemoteDroid Server R2";
	private final Toolkit toolkit;
	private final MediaTracker tracker;

	public AppFrame() {
		GlobalData.oFrame = this;
		setSize(this.width, this.height);

		this.toolkit = Toolkit.getDefaultToolkit();
		this.tracker = new MediaTracker(this);

		String sHost = "";
		try {
			localAddr = InetAddress.getLocalHost();
			if (localAddr.isLoopbackAddress()) {
				localAddr = LinuxInetAddress.getLocalHost();
			}
			sHost = localAddr.getHostAddress();
		} catch (UnknownHostException ex) {
			sHost = "Error finding local IP.";
		}

		this.textLines[0] = "The RemoteDroid server application is now running.";
		this.textLines[1] = "";
		this.textLines[2] = ("Your IP address is: " + sHost);
		this.textLines[3] = "";
		this.textLines[4] = "Enter this IP address on the start screen of the";
		this.textLines[5] = "RemoteDroid application on your phone to begin.";

	}

	public void init() throws IOException, InterruptedException {

		this.imLogo = this.toolkit.createImage(ResourcesHelper
				.getBytes(ResourcesHelper.getConfigFileAsStream()));
		this.tracker.addImage(this.imLogo, 0);
		this.tracker.waitForID(0);

		this.imHelp = this.toolkit.createImage(ResourcesHelper
				.getBytes(ResourcesHelper.getImgHelpFileAsStream()));
		this.tracker.addImage(this.imHelp, 1);
		this.tracker.waitForID(1);

		this.fontTitle = new Font("Verdana", 1, 16);
		this.fontText = new Font("Verdana", 0, 11);
		setBackground(Color.WHITE);
		setForeground(Color.BLACK);

		this.timer = new Timer(500, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				AppFrame.this.world = new OSCWorld();
				AppFrame.this.world.onEnter();

				AppFrame.this.repaint();
				AppFrame.this.timer.stop();
			}
		});
		this.timer.start();
	}

	@Override
	public void paint(Graphics g) {
		g.setColor(getBackground());
		g.fillRect(0, 0, this.width, this.height);
		g.setColor(getForeground());

		g.drawImage(this.imLogo, 10, 30, this);
		g.setFont(this.fontTitle);
		g.drawString(this.appName, 70, 55);

		g.setFont(this.fontText);
		int startY = 90;
		int l = 6;
		for (int i = 0; i < l; i++) {
			g.drawString(this.textLines[i], 10, startY);
			startY += 13;
		}

		g.drawImage(this.imHelp, 20, startY + 10, this);
	}
}