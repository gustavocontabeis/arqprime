package br.com.coder.arqprime.model.utils;

import java.util.Collection;
import java.util.List;

import org.apache.commons.beanutils.BeanPropertyValueEqualsPredicate;

public class CollectionUtil {
	
	public static Object find(List list, String property, Object obj){
		return org.apache.commons.collections.CollectionUtils.find(list, new BeanPropertyValueEqualsPredicate(property, obj));
	}

	public static Collection collect2(List list, String property, Object obj){
		return org.apache.commons.collections.CollectionUtils.select(list, new BeanPropertyValueEqualsPredicate(property, obj));
	}

}
