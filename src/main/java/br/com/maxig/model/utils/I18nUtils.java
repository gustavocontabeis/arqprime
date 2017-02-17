package br.com.maxig.model.utils;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;

public class I18nUtils {
	
	/**
	 * Return a message from JSF context.
	 * @param key
	 * @return
	 */
	public static String getMessage(String key) {
		
		if(isKey(key)){
			
			key = key.replaceAll("[{}]", "");
			
			//TODO preciso iniciar o contexto do jsf com ajax do JQuery
			FacesContext facesContext = FacesContext.getCurrentInstance();
			String messageBundleName = facesContext.getApplication().getMessageBundle();
			Locale locale = facesContext.getViewRoot().getLocale();
			ResourceBundle bundle = ResourceBundle.getBundle(messageBundleName, locale);
			
			return bundle.getString(key);
		}else{
			return key;
		}

	}
	
	/**
	 * return a message from resource bundle.
	 * @param key
	 * @param messageBundleName
	 * @param locale
	 * @return
	 */
	public static String getMessage(String key, String messageBundleName, Locale locale) {
		
		if(isKey(key)){
			
			key = key.replaceAll("[{}]", "");
			
			ResourceBundle bundle = ResourceBundle.getBundle(messageBundleName, locale);
			
			return bundle.getString(key);
		}else{
			return key;
		}

	}
	
	/**
	 * verify is the istring is equal {my.key}
	 * @param key
	 * @return
	 */
	public static boolean isKey(String key) {
		return key.matches("\\{[\\w\\.]*.?\\}");
	}
	
}
