package br.com.maxig.model.utils;

import java.text.NumberFormat;

public class NumberUtil {
	
	private static NumberFormat numberInstance = NumberFormat.getNumberInstance();
	
	static{
		numberInstance.setGroupingUsed(true);
		numberInstance.setMinimumFractionDigits(2);
		numberInstance.setMaximumFractionDigits(2);
	}

	public static String format(double number) {
		return numberInstance.format(number);
	}

	public static String format(float number) {
		return numberInstance.format(number);
	}

	public static String format(int number) {
		return numberInstance.format(number);
	}

	public static String format(long number) {
		return numberInstance.format(number);
	}

}
