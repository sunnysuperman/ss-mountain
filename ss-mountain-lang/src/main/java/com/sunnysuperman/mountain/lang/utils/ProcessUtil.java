package com.sunnysuperman.mountain.lang.utils;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ProcessUtil {
	private static final Logger LOG = LoggerFactory.getLogger(ProcessUtil.class);
	private static String gMac = null;

	private ProcessUtil() {
	}

	public static void sleep(long mills) {
		try {
			Thread.sleep(mills);
		} catch (InterruptedException ie) {
			Thread.currentThread().interrupt();
		}
	}

	public static void exitWithMessage(String msg, Throwable t) {
		if (t != null) {
			LOG.error(msg, t);
		} else if (msg != null) {
			LOG.error(msg);
		}
		System.exit(0);
	}

	public static void exitWithMessage(String msg) {
		exitWithMessage(msg, null);
	}

	public static String ensureLocalMacAddress() {
		if (gMac == null) {
			try {
				gMac = getLocalMacAddress();
			} catch (Exception ex) {
				LOG.error(null, ex);
			}
			if (gMac == null) {
				LOG.error("Failed to getLocalMacAddress, use generated string instead");
				// D0-27-88-1F-89-51
				gMac = Str.randomAlphanumeric(17);
			}
		}
		return gMac;
	}

	public static String getLocalMacAddress() throws SocketException {
		Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
		while (e.hasMoreElements()) {
			NetworkInterface ni = e.nextElement();
			byte[] mac = ni.getHardwareAddress();
			if (mac != null) {
				StringBuilder sb = new StringBuilder("");
				for (int i = 0; i < mac.length; i++) {
					if (i != 0) {
						sb.append("-");
					}
					// 字节转换为整数
					int temp = mac[i] & 0xff;
					String str = Integer.toHexString(temp);
					if (str.length() == 1) {
						sb.append("0" + str);
					} else {
						sb.append(str);
					}
				}
				return sb.toString();
			}
		}
		return null;
	}

}
