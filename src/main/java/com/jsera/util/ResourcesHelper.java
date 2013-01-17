package com.jsera.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ResourcesHelper {

	private static final String BASE_RSC_PATH = "/assets/";

	public static InputStream getAppIconAsStream() {
		return ResourcesHelper.class.getResourceAsStream(BASE_RSC_PATH
				+ "icon.gif");
	}

	public static InputStream getConfigFileAsStream() {
		return ResourcesHelper.class.getResourceAsStream(BASE_RSC_PATH
				+ "config.xml");
	}

	public static InputStream getImgHelpFileAsStream() {
		return ResourcesHelper.class.getResourceAsStream(BASE_RSC_PATH
				+ "helpphoto.jpg");
	}

	public static byte[] getBytes(InputStream is) throws IOException {

		int len;
		int size = 1024;
		byte[] buf;

		if (is instanceof ByteArrayInputStream) {
			size = is.available();
			buf = new byte[size];
			len = is.read(buf, 0, size);
		} else {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			buf = new byte[size];
			while ((len = is.read(buf, 0, size)) != -1)
				bos.write(buf, 0, len);
			buf = bos.toByteArray();
		}
		return buf;
	}
}
