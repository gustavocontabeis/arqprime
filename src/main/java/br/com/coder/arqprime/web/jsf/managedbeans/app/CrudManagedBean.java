package br.com.coder.arqprime.web.jsf.managedbeans.app;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ComponentSystemEvent;

import org.apache.commons.beanutils.BeanUtils;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.coder.arqprime.model.dao.app.BaseDAO;
import br.com.coder.arqprime.model.dao.app.DaoException;
import br.com.coder.arqprime.model.entity.BaseEntity;
import br.com.coder.arqprime.model.utils.Filtro;
import br.com.coder.arqprime.web.jsf.filters.SegurancaFilter;

public abstract class CrudManagedBean <T extends BaseEntity, D extends BaseDAO<T>> extends BaseManagedBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected static final Logger LOGGER = LoggerFactory.getLogger(CrudManagedBean.class.getSimpleName());
	
	protected T entity;
	protected LazyDataModel<T> model;
	protected List<T>list;
	protected Filtro<T> filtro;
	
	protected BaseDAO<T> dao;
	
	protected Long id;
	
	@PostConstruct
	private void init() {
		System.out.println("ContaManagedBean.init() ");
		novo(null);
		loadLazyModel();
	}
	
	public void listener(ComponentSystemEvent evt) throws AbortProcessingException{
		aoEntrarAntes();
		if(id != null){
			try {
				entity = getDao().buscar(new Long(id));
			} catch (NumberFormatException e) {
				e.printStackTrace();
				message(e);
			} catch (DaoException e) {
				e.printStackTrace();
				message(e);
			}
		}
		aoEntrarApos();
	}
	
	protected void aoEntrarApos() {
		
	}

	protected void aoEntrarAntes() {
		
	}

	protected void loadLazyModel() {
		model = new LazyDataModel<T>() {
			private static final long serialVersionUID = 1L;

			@Override
			public List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder,	Map<String, Object> filters) {
				T entity = getEntity();
				if(entity == null){
					entity = novo();
					CrudManagedBean.this.entity = entity;
				}
				
				filtro = new Filtro<T>(CrudManagedBean.this.entity.getClass(), first, pageSize, sortField, sortOrder, filters);
				filtro = getFiltro(filtro);
				
				dao = getDao();
				
				Integer quantidade2 = getQuantidade2();
				if(quantidade2 == null){
					quantidade2 = dao.getQuantidade2(filtro);
				}
				
				setRowCount(quantidade2);
				
				List<T> buscar2 = buscar2();
				if(buscar2 == null){
					buscar2 = dao.buscar2(filtro);
				}
				list = buscar2;
				return buscar2;
			}

			public T getRowData(String rowKey) {
				novo(null);
				entity.setId(new Long(rowKey));
				try {
					BaseEntity entity2 = buscarAntes(entity, rowKey);
					entity = dao.buscar(new Long(rowKey));
					buscarApos(entity);
				} catch (DaoException | NumberFormatException e) {
					e.printStackTrace();
					message(e);
				}return entity;
			}

			public Object getRowKey(T object) {
				return String.valueOf(object.getId());
			}
		};
	}
	
	protected abstract T novo();

	public void novo(ActionEvent evt){
		this.entity = novo();
	}
	
	public void salvar(ActionEvent evt) throws DaoException {
		try {
			if(salvarAntes(entity)){
				getDao().salvar(entity);
				salvarApos(entity);
			}
		} catch (Exception e) {
			e.printStackTrace();
			message(e);
		}
	}

	protected boolean salvarAntes(T entity) {
		return true;
	}
	
	protected void salvarApos(T entity) {
		message(null, "Registro salvo com sucesso.");
	}
	
	protected Integer getQuantidade2() {
		return null;
	}

	protected List<T> buscar2() {
		return null;
	}

	protected Filtro getFiltro(Filtro filtro) {
		return filtro;
	}
	
	public void excluir(ActionEvent evt) throws DaoException {
		try {
			if(excluirAntes(entity)){
				getDao().excluir(entity);
				excluirApos(entity);
				novo(null);
			}
		} catch (Exception e) {
			message(e);
		}
	}

	protected boolean excluirAntes(T entity) {
		return true;
	}
	
	protected void excluirApos(T entity) {
		message(null, "Registro excluído com sucesso.");
	}
	
	@SuppressWarnings("unchecked")
	public void clonar(ActionEvent evt) {
		try {
			T t = (T) BeanUtils.cloneBean(entity);
			t.setId(null);
			this.entity = t;
			clonarApos();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			message(e);
		} catch (InstantiationException e) {
			e.printStackTrace();
			message(e);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			message(e);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			message(e);
		}
	}

	protected void clonarApos() {
		
	}

	protected BaseEntity buscarAntes(T entity, String rowKey) {
		return entity;
	}

	protected void buscarApos(T entity) {
	}

	protected abstract BaseDAO<T> getDao();
	
	public T getEntity() {
		return entity;
	}
	public void setEntity(T entity) {
		this.entity = entity;
	}
	
	public LazyDataModel<T> getModel() {
		return model;
	}
	public void setModel(LazyDataModel<T> model) {
		this.model = model;
	}


	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public List<T> getList() {
		return list;
	}

}
