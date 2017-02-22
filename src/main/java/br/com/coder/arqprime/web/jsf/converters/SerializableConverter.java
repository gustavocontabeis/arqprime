package br.com.coder.arqprime.web.jsf.converters;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;

import br.com.coder.arqprime.model.dao.app.BaseDAO;
import br.com.coder.arqprime.model.entity.BaseEntity;


@FacesConverter("serializableConverter")
public class SerializableConverter implements Converter {
	
	private static final Logger log = Logger.getLogger(SerializableConverter.class.getSimpleName());
	
	BaseDAO<BaseEntity>dao;

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		log.info(String.format("%s - %s - %s\n", "getAsObject", component.getClientId(), value));
		if (StringUtils.isBlank(value) || "null".equals(value)) 
			return null;
		Object object = component.getAttributes().get("tipo");
		Collection itens = (Collection) component.getAttributes().get("itens");
		if (object == null)
			throw new IllegalArgumentException("Adicione o atributo \"tipo\" ao componente que utiliza o converter \"baseEntityConverter\".");
		String classe = (String) object;
		try {
			Object newInstance = Class.forName(classe).newInstance();
			BeanUtils.setProperty(newInstance, "id", Long.parseLong(value));
			for(Object obj : itens){
				String property = BeanUtils.getProperty(obj, "id");
				if(property.equals(value)){
					return obj;
				}
			}
		} catch (InstantiationException e) {
			throw new RuntimeException(e.getMessage());
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e.getMessage());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Classe nao existe: "+e.getMessage());
		} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (InvocationTargetException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object obj) {
		if (obj != null && obj instanceof BaseEntity) {
			BaseEntity entity = (BaseEntity) obj;
			log.info(String.format("%s - %s - %s - %s - %s\n", "baseEntityConverter", "getAsString1", component.getClientId(), String.valueOf(obj), String.valueOf(entity.getId())));
			return String.valueOf(entity.getId());
		}
		return obj!=null?String.valueOf(obj):"";
	}

}
