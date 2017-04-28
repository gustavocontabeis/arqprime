package br.com.coder.arqprime.web.jsf.converters;

import java.util.List;
import java.util.logging.Logger;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;

import br.com.coder.arqprime.model.dao.app.BaseDAO;
import br.com.coder.arqprime.model.dao.app.DaoException;
import br.com.coder.arqprime.model.entity.BaseEntity;


/**
<p:selectOneMenu id="logradouro" value="#{imovelManagedBean.imovel.logradouro}" required="true">
	<f:converter converterId="baseEntityConverter"/>
	<f:attribute name="tipo" value="br.com.imob.model.entity.endereco.Logradouro" />
	<f:attribute name="dao" value="br.com.imob.model.dao.endereco.LogradouroDAO" />
	<f:selectItem itemLabel="[Selecione]" />
	<f:selectItems value="#{imovelManagedBean.popularComboLogradouro}" var="obj" itemValue="#{obj}" itemLabel="#{obj.tipo}-#{obj.nome}-#{obj.bairro.nome}-#{obj.bairro.municipio.nome}"  /> 
</p:selectOneMenu>

 * @author Gustavo
 *
 */
@FacesConverter("baseEntityConverter")
public class BaseEntityConverter implements Converter {
	
	private static final Logger log = Logger.getLogger(BaseEntityConverter.class.getSimpleName());
	
	BaseDAO<BaseEntity>dao;

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		log.info(String.format("%s - %s - %s\n", "getAsObject", component.getClientId(), value));
		if (StringUtils.isBlank(value) || "null".equals(value)) 
			return null;
		Object object = component.getAttributes().get("tipo");
		Object list = component.getAttributes().get("list");
		if (object == null)
			throw new IllegalArgumentException("Adicione o atributo \"tipo\" ao componente que utiliza o converter \"baseEntityConverter\".");
		String classe = (String) object;
		try {
			Object newInstance = Class.forName(classe).newInstance();
			if (newInstance instanceof BaseEntity) {
				
				BaseEntity abstractBaseEntity = (BaseEntity) newInstance;
				abstractBaseEntity.setId(Long.parseLong(value));
				
				if(list !=null){
					List<BaseEntity> listEntities = (List<BaseEntity>) list;
					for (BaseEntity obj : listEntities) {
						if(abstractBaseEntity.getId().equals(obj.getId())){
							return obj;
						}
					}
				}
				
				Object objectDAO = component.getAttributes().get("dao");
				if(objectDAO!=null){
					dao = (BaseDAO<BaseEntity>) Class.forName(objectDAO.toString()).newInstance();
				}else{
					dao = (BaseDAO<BaseEntity>) Class.forName("br.com.maxig.model.dao."+newInstance.getClass().getSimpleName()+"DAO").newInstance();
				}
				try {
					abstractBaseEntity = dao.buscar(abstractBaseEntity.getId());
				} catch (DaoException e) {
					e.printStackTrace();
				}
				return abstractBaseEntity;
			}
		} catch (InstantiationException e) {
			throw new RuntimeException(e.getMessage());
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e.getMessage());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Classe nao existe: "+e.getMessage());
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
