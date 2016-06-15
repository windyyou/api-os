package com.fnst.cloudapi.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Message {

	private static final String MESSAGE_PROPERTIES_FILE_PATH = "com.fnst.conf";

	public static String getMessage(String id) throws IllegalArgumentException, MissingResourceException {
		return getMessage(id, Locale.getDefault(), true);
	}

	public static String getMessage(String id, boolean formatFlg)
			throws IllegalArgumentException, MissingResourceException {
		return getMessage(id, Locale.getDefault(), formatFlg);
	}

	public static String getMessage(String id, Locale locale)
			throws IllegalArgumentException, MissingResourceException {
		return getMessage(id, locale, true);
	}

	public static String getMessage(String id, Locale locale, boolean formatFlg)
			throws IllegalArgumentException, MissingResourceException {

		if (id == null || locale == null) {
			throw new IllegalArgumentException();
		}

		String msg;

		try {
			msg = ResourceBundle.getBundle(MESSAGE_PROPERTIES_FILE_PATH + ".messages", locale).getString(id);
		} catch (MissingResourceException mre) {
			throw mre;
		}

		if (formatFlg) {
			msg = getFormattedMessage(id, msg);
		}

		return msg;
	}

	public static String getMessage(String id, String[] msgPrm)
			throws IllegalArgumentException, MissingResourceException {
		return getMessage(id, Locale.getDefault(), msgPrm, true);
	}

	public static String getMessage(String id, String[] msgPrm, boolean formatFlg)
			throws IllegalArgumentException, MissingResourceException {
		return getMessage(id, Locale.getDefault(), msgPrm, formatFlg);
	}

	public static String getMessage(String id, Locale locale, String[] msgPrm)
			throws IllegalArgumentException, MissingResourceException {
		return getMessage(id, locale, msgPrm, true);
	}

	public static String getMessage(String id, Locale locale, String[] msgPrm, boolean formatFlg)
			throws IllegalArgumentException, MissingResourceException {

		if (id == null || locale == null) {
			throw new IllegalArgumentException();
		}

		String msg;

		try {
			msg = ResourceBundle.getBundle(MESSAGE_PROPERTIES_FILE_PATH + ".messages", locale).getString(id);
		} catch (MissingResourceException mre) {
			throw mre;
		}

		synchronized (Message.class) {
			msg = MessageFormat.format(msg, (Object[]) msgPrm);
		}

		if (formatFlg) {
			msg = getFormattedMessage(id, msg);
		}

		return msg;
	}

	private static String getFormattedMessage(String id, String msg) {

		StringBuilder sb = new StringBuilder();

	//	sb.append(id);
		sb.append("message");
		sb.append(":");
		sb.append(msg);

		return sb.toString();
	}
	
	public static String getNormalMessage(String statusCode){
		return "{message:}";
		
//		StringBuilder sb = new StringBuilder();
//
//		sb.append("{");
//		sb.append("{message:}");
//		sb.append(":");
//		sb.append("}");
//		
//		return sb.toString();
	}
}