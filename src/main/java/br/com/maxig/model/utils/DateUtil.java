package br.com.maxig.model.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	
	private static final DateFormat dfFull = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

	public static String full(Date dt) {
		return dfFull.format(dt);
	}

}
