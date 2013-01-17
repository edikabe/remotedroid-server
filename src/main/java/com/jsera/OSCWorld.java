package com.jsera;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Label;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.InetAddress;
import java.util.Date;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPort;
import com.illposed.osc.OSCPortIn;
import com.jsera.util.World;

public class OSCWorld extends World {
	// private static final float sensitivity = 1.6F;
	private OSCPortIn receiver;
	private Robot robot;
	private boolean shifted = false;
	private boolean modified = false;
	private KeyTranslator translator;
	private GraphicsDevice[] gDevices;
	private Rectangle[] gBounds;
	private Label lbDebug;
	private int scrollMod = -1;

	private float xLeftover = 0.0F;
	private float yLeftover = 0.0F;
	private DiscoverableThread discoverable;

	@Override
	public void onEnter() {
		try {
			this.robot = new Robot();
			this.robot.setAutoDelay(5);

			this.translator = new KeyTranslator();

			InetAddress local = InetAddress.getLocalHost();
			if (local.isLoopbackAddress())
				this.receiver = new OSCPortIn(OSCPort.defaultSCOSCPort());
			else {
				this.receiver = new OSCPortIn(OSCPort.defaultSCOSCPort());
			}
			OSCListener listener = new OSCListener() {
				@Override
				public void acceptMessage(Date time, OSCMessage message) {
					Object[] args = message.getArguments();
					if (args.length == 3)
						OSCWorld.this.mouseEvent(
								Integer.parseInt(args[0].toString()),
								Float.parseFloat(args[1].toString()),
								Float.parseFloat(args[2].toString()));
				}
			};
			this.receiver.addListener("/mouse", listener);

			listener = new OSCListener() {
				@Override
				public void acceptMessage(Date time, OSCMessage message) {
					Object[] args = message.getArguments();
					if (args.length == 1)
						OSCWorld.this.buttonEvent(
								Integer.parseInt(args[0].toString()), 0);
				}
			};
			this.receiver.addListener("/leftbutton", listener);

			listener = new OSCListener() {
				@Override
				public void acceptMessage(Date time, OSCMessage message) {
					Object[] args = message.getArguments();
					if (args.length == 1)
						OSCWorld.this.buttonEvent(
								Integer.parseInt(args[0].toString()), 2);
				}
			};
			this.receiver.addListener("/rightbutton", listener);

			listener = new OSCListener() {
				@Override
				public void acceptMessage(Date time, OSCMessage message) {
					Object[] args = message.getArguments();
					if (args.length == 3) {
						OSCWorld.this.keyboardEvent(
								Integer.parseInt(args[0].toString()),
								Integer.parseInt(args[1].toString()),
								args[2].toString());
					}
					if (args.length == 2)
						OSCWorld.this.keyboardEvent(
								Integer.parseInt(args[0].toString()),
								Integer.parseInt(args[1].toString()));
				}
			};
			this.receiver.addListener("/keyboard", listener);

			listener = new OSCListener() {
				@Override
				public void acceptMessage(Date time, OSCMessage message) {
					Object[] args = message.getArguments();
					if (args.length == 1)
						OSCWorld.this.scrollEvent(Integer.parseInt(args[0]
								.toString()));
				}
			};
			this.receiver.addListener("/wheel", listener);

			listener = new OSCListener() {
				@Override
				public void acceptMessage(Date time, OSCMessage message) {
					Object[] args = message.getArguments();
					if (args.length == 6)
						OSCWorld.this.orientEvent(
								Float.parseFloat(args[0].toString()),
								Float.parseFloat(args[1].toString()),
								Float.parseFloat(args[2].toString()),
								Float.parseFloat(args[3].toString()),
								Float.parseFloat(args[4].toString()),
								Float.parseFloat(args[5].toString()));
				}
			};
			this.receiver.addListener("/orient", listener);

			this.receiver.startListening();

			GlobalData.oFrame.addKeyListener(new KeyListener() {
				@Override
				public void keyReleased(KeyEvent e) {
					OSCWorld.this.nativeKeyEvent(e);
				}

				@Override
				public void keyPressed(KeyEvent e) {
				}

				@Override
				public void keyTyped(KeyEvent e) {
				}
			});
			this.gDevices = GraphicsEnvironment.getLocalGraphicsEnvironment()
					.getScreenDevices();
			int l = this.gDevices.length;
			this.gBounds = new Rectangle[l];
			for (int i = 0; i < l; i++) {
				this.gBounds[i] = this.gDevices[i].getDefaultConfiguration()
						.getBounds();
			}

			initUI();

			if (System.getProperty("os.name").compareToIgnoreCase("Mac OS X") == 0) {
				this.scrollMod = 1;
			}

			this.discoverable = new DiscoverableThread(
					OSCPort.defaultSCOSCPort() + 1);
			this.discoverable.start();
		} catch (Exception localException) {
		}
	}

	private void nativeKeyEvent(KeyEvent ev) {
	}

	private void mouseEvent(int type, float xOffset, float yOffset) {
		if (type == 2) {
			PointerInfo info = MouseInfo.getPointerInfo();
			if (info != null) {
				Point p = info.getLocation();

				float ox = xOffset * 1.6F + this.xLeftover;
				float oy = yOffset * 1.6F + this.yLeftover;
				int ix = Math.round(ox);
				int iy = Math.round(oy);
				this.xLeftover = (ox - ix);
				this.yLeftover = (oy - iy);

				p.x += ix;
				p.y += iy;
				int l = this.gBounds.length;
				for (int i = 0; i < l; i++) {
					if (this.gBounds[i].contains(p)) {
						this.robot.mouseMove(p.x, p.y);
						break;
					}
				}
				try {
					this.robot.mouseMove(p.x, p.y);
				} catch (Exception localException) {
				}
			}
		}
	}

	private void buttonEvent(int type, int button) {
		if (button == 0)
			button = 16;
		else if (button == 2) {
			button = 4;
		}
		switch (type) {
		case 0:
			this.robot.mousePress(button);
			this.robot.waitForIdle();
			break;
		case 1:
			this.robot.mouseRelease(button);
			this.robot.waitForIdle();
		}
	}

	private void scrollEvent(int dir) {
		this.robot.mouseWheel(-dir * this.scrollMod);
	}

	private void keyboardEvent(int type, int keycode) {
		switch (type) {
		case 0:
			if (this.translator.isShift(keycode)) {
				this.shifted = true;
				keyPress(16);
			} else {
				keyPress(keycode);
			}
			break;
		case 1:
			if (this.translator.isShift(keycode)) {
				this.shifted = false;
				keyRelease(16);
			} else {
				keyRelease(keycode);
			}
			break;
		}
	}

	private void keyboardEvent(int type, int keycode, String value) {
		switch (type) {
		case 0:
			System.out.println("Key down, code:" + String.valueOf(keycode));

			if (this.translator.isLeftClick(keycode)) {
				buttonEvent(0, 0);
				return;
			}

			KeyCodeData data = this.translator.codes.get(new Integer(keycode));

			if (this.translator.isModifier(keycode)) {
				this.modified = true;
			}
			if (this.translator.isShift(keycode)) {
				this.shifted = true;
				keyPress(16);
			}
			if (this.translator.isCtrl(keycode)) {
				keyPress(17);
			}
			if (data != null) {
				if ((!this.shifted) && (data.shifted)) {
					keyPress(16);
				}
				if (this.modified) {
					if ((data.modshifted) && (!this.shifted)) {
						keyPress(16);
					}

					if ((!data.modshifted) && (this.shifted)) {
						keyRelease(16);
					}

					if (data.modifiedcode != -1) {
						keyPress(data.modifiedcode);
					}
					if ((data.modshifted) && (!this.shifted)) {
						keyRelease(16);
					}
					if ((!data.modshifted) && (this.shifted))
						keyPress(16);
				} else {
					try {
						if ((this.shifted) && (data.shiftedcode != -1))
							keyPress(data.shiftedcode);
						else
							keyPress(data.localcode);
					} catch (IllegalArgumentException e) {
						System.out.println("Invalid key code: "
								+ data.localcode);
					}
				}
			}

			break;
		case 1:
			System.out.println("Key up, code:" + String.valueOf(keycode));

			if (this.translator.isLeftClick(keycode)) {
				buttonEvent(1, 0);
				return;
			}

			KeyCodeData data2 = this.translator.codes.get(new Integer(keycode));

			if (this.translator.isModifier(keycode)) {
				this.modified = false;
			}
			if (this.translator.isShift(keycode)) {
				this.shifted = false;
				keyRelease(16);
			}
			if (this.translator.isCtrl(keycode)) {
				keyRelease(17);
			}
			if (data2 != null) {
				if ((!this.shifted) && (data2.shifted)) {
					keyRelease(16);
				}
				if (this.modified) {
					if ((data2.modshifted) && (!this.shifted)) {
						keyPress(16);
					}
					if ((!data2.modshifted) && (this.shifted)) {
						keyRelease(16);
					}

					if (data2.modifiedcode != -1) {
						keyRelease(data2.modifiedcode);
					}
					if ((data2.modshifted) && (!this.shifted)) {
						keyRelease(16);
					}
					if ((!data2.modshifted) && (this.shifted)) {
						keyPress(16);
					}
				} else if ((this.shifted) && (data2.shiftedcode != -1)) {
					keyRelease(data2.shiftedcode);
				} else {
					keyRelease(data2.localcode);
				}
			}
			break;
		}
	}

	private void keyPress(int localcode) {
		try {
			this.robot.keyPress(localcode);
		} catch (IllegalArgumentException e) {
			System.out.println("Invalid keyPress code: " + localcode);
		}
	}

	private void keyRelease(int localcode) {
		try {
			this.robot.keyRelease(localcode);
		} catch (IllegalArgumentException e) {
			System.out.println("Invalid keyRelease code: " + localcode);
		}
	}

	private void orientEvent(float z, float x, float y, float rawz, float rawx,
			float rawy) {
		StringBuilder builder = new StringBuilder();
		addValue(builder, "z", z);
		addValue(builder, "x", x);
		addValue(builder, "y", y);
		addValue(builder, "rawz", rawz);
		addValue(builder, "rawx", rawx);
		addValue(builder, "rawy", rawy);

		double len = Math.sqrt(x * x + y * y + z * z);
		addValue(builder, "len", (float) len);

		this.lbDebug.setText(builder.toString());
	}

	private void initUI() {
	}

	private void addValue(StringBuilder builder, String name, float value) {
		builder.append(name);
		builder.append(": ");
		builder.append(value);
		builder.append("\n");
	}

	@Override
	public void update(float elapsed) {
	}
}
