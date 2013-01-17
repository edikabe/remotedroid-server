package com.jsera;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RemoteDroidServer {

	private static Logger LOGGER = Logger.getAnonymousLogger();

	private static AppFrame f;

	public static void main(String[] args) {

		LOGGER.log(
				Level.INFO,
				"Starting RemoteDroid server on: "
						+ System.getProperty("os.name"));

		f = new AppFrame();
		f.setVisible(true);
		f.setResizable(false);
		f.setTitle("RemoteDroid Server");

		f.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				RemoteDroidServer.f.setVisible(false);
				RemoteDroidServer.f.dispose();
				System.exit(0);
			}
		});
		try {
			f.init();
		} catch (IOException e1) {
			System.out.println(e1);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}
}