package com.jsera;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class KeyTranslator {

	public Map<Integer, KeyCodeData> codes;
	private final int[] modifiers;
	private final int[] shifts;
	private final int[] ctrls;
	private final int[] leftClicks;
	protected Document myDoc;

	public static void main(String[] args) {
		new KeyTranslator();
	}

	private static final String CONFIG_PATH = "/assets/config.xml";

	public KeyTranslator() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		InputStream is = KeyTranslator.class.getResourceAsStream(CONFIG_PATH);

		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			this.myDoc = builder.parse(is);
		} catch (SAXException e) {
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Element config = this.myDoc.getDocumentElement();
		NodeList mods = config.getElementsByTagName("modifier");

		int l = mods.getLength();

		this.modifiers = new int[l];
		for (int i = 0; i < l; i++) {
			this.modifiers[i] = Integer.parseInt(((Element) mods.item(i))
					.getAttribute("code"));
		}

		mods = config.getElementsByTagName("shift");
		l = mods.getLength();
		this.shifts = new int[l];

		for (int i = 0; i < l; i++) {
			this.shifts[i] = Integer.parseInt(((Element) mods.item(i))
					.getAttribute("code"));
		}

		mods = config.getElementsByTagName("ctrl");
		l = mods.getLength();
		this.ctrls = new int[l];
		for (int i = 0; i < l; i++) {
			this.ctrls[i] = Integer.parseInt(((Element) mods.item(i))
					.getAttribute("code"));
		}

		this.codes = new HashMap<Integer, KeyCodeData>();

		mods = config.getElementsByTagName("key");
		l = mods.getLength();
		for (int i = 0; i < l; i++) {
			KeyCodeData data = new KeyCodeData();
			Element keydata = (Element) mods.item(i);
			data.name = keydata.getAttribute("name");
			data.modshifted = ("1".compareTo(keydata.getAttribute("modshift")) == 0);
			data.localcode = Integer
					.parseInt(keydata.getAttribute("localcode"));
			data.modifiedcode = Integer.parseInt(keydata
					.getAttribute("modified"));
			data.shifted = ("1".compareTo(keydata.getAttribute("shifted")) == 0);
			data.shiftedcode = Integer.parseInt(keydata
					.getAttribute("shiftedcode"));
			int keycode = Integer.parseInt(keydata.getAttribute("code"));

			this.codes.put(new Integer(keycode), data);
		}

		mods = config.getElementsByTagName("leftclick");
		l = mods.getLength();
		this.leftClicks = new int[l];
		for (int i = 0; i < l; i++)
			this.leftClicks[i] = Integer.parseInt(((Element) mods.item(i))
					.getAttribute("code"));
	}

	public boolean isModifier(int keycode) {
		int l = this.modifiers.length;
		for (int i = 0; i < l; i++) {
			if (keycode == this.modifiers[i]) {
				return true;
			}
		}
		return false;
	}

	public boolean isShift(int keycode) {
		int l = this.shifts.length;
		for (int i = 0; i < l; i++) {
			if (keycode == this.shifts[i]) {
				return true;
			}
		}
		return false;
	}

	public boolean isCtrl(int keycode) {
		int l = this.ctrls.length;
		for (int i = 0; i < l; i++) {
			if (keycode == this.ctrls[i]) {
				return true;
			}
		}
		return false;
	}

	public boolean isLeftClick(int keycode) {
		int l = this.leftClicks.length;
		for (int i = 0; i < l; i++) {
			if (keycode == this.leftClicks[i]) {
				return true;
			}
		}
		return false;
	}
}