package br.com.coder.arqprime.model.dao.app;

import static org.hamcrest.CoreMatchers.instanceOf;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.FetchParent;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.hibernate.query.criteria.internal.path.SingularAttributeJoin;
import org.hibernate.stat.Statistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.coder.arqprime.model.entity.BaseEntity;
import br.com.coder.arqprime.model.utils.DirecaoOrdenacao;
import br.com.coder.arqprime.model.utils.Filtro;
import br.com.coder.arqprime.model.utils.HibernateUtil;
import br.com.coder.arqprime.model.utils.Ordenacao;
import br.com.coder.arqprime.model.utils.StringUtil;

public class BaseDAO<T extends BaseEntity> implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseDAO.class.getSimpleName());

    protected Session getSession() {
        return HibernateUtil.getSession();
    }

    public void salvar(T obj) throws DaoException {
        validate(obj);
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        try {
			if (obj.getId() == null) {
				session.save(obj);
			} else {
			    session.update(obj);
			}
			transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
		} finally {
			session.close();
		}
    }

    public void excluir(T obj) throws DaoException {
        validate(obj);
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        try {
    		session.delete(obj);
    		transaction.commit();
		} catch (Exception e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
        obj.setId(null);
    }
    
//    @Deprecated
//    @SuppressWarnings({"unchecked", "hiding"})
//    public <T> T buscar(T obj, Long id) throws DaoException {
//        Session session = getSession();
//        Transaction transaction = session.beginTransaction();
//        T load = (T) session.get(obj.getClass(), id);
//        //session.flush();
//        transaction.commit();
//        session.close();
//        printStats("Buscar T id");
//        return load;
//    }

    public <T> T buscar(Long id) throws DaoException {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        Class<?> forName = null;
        try {
            Type genericSuperclass = getClass().getGenericSuperclass();
            Type t = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
            forName = Class.forName(t.getTypeName());
            Query query = session.getNamedQuery(forName.getSimpleName()+"-porId");
            query.setCacheable(true);
            //query.setCacheMode(cacheMode.)
            query.setCacheRegion(forName.getSimpleName()+"-porId-"+id);
            query.setLong("id", id);
            Object uniqueResult = query.uniqueResult();
            return (T) uniqueResult;
            
            //return (T) session.load(forName, id);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
        	transaction.commit();
        	session.close();
		}
        printStats("Buscar id");
        return null;
    }
    
    public List buscar(String hql, Object...params) throws DaoException {
		Session session = getSession();
		Query query = session.createQuery(hql);
    	for (int i = 0; i < params.length; i++) {
    		query.setParameter(i, params[i]);
		}
		List list = query.list();
		session.close();
		return list;
    }

    private void validate(Serializable obj) throws DaoException {
    	
//    	Validator validator = Validation.byDefaultProvider()
//    	        .configure()
//    	        .messageInterpolator(
//    	                new ResourceBundleMessageInterpolator(
//    	                        new PlatformResourceBundleLocator( "labels" )
//    	                )
//    	        )
//    	        .buildValidatorFactory()
//    	        .getValidator();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<Serializable>> constraintViolations = validator.validate(obj);
        for (ConstraintViolation<Serializable> constraintViolation : constraintViolations) {
			LOGGER.info("{} - {} - {} - {} - {}", 
					constraintViolation.getRootBean(),
					constraintViolation.getLeafBean(),
					constraintViolation.getInvalidValue(),
					constraintViolation.getPropertyPath(), 
					constraintViolation.getMessage());
		}
        if (constraintViolations.size() > 0) {
            ConstraintViolation<Serializable> next = constraintViolations.iterator().next();
			throw new DaoException(next.getPropertyPath()+": "+next.getMessage());
        }

    }
    
    @Deprecated
    public List<T> buscar(Filtro<T> filtro) {
        CriteriaDTO dto = criarCriteriaParaFiltro(filtro);

        if (filtro.getQuantRegistros() != 0) {
            dto.criteria.setFirstResult(filtro.getPrimeiroRegistro());
            dto.criteria.setMaxResults(filtro.getQuantRegistros());
        }

        List<String> fetchs = filtro.getFetchs();
        for (String property : fetchs) {
            dto.criteria.setFetchMode(property, FetchMode.JOIN);
        }
//			Comentei pq ja esta deprescate mesmo. Foda-se.
//        if (filtro.isAscendente() && filtro.getPropriedadeOrdenacao() != null) {
//            dto.criteria.addOrder(Order.asc(filtro.getPropriedadeOrdenacao()));
//        } else if (filtro.getPropriedadeOrdenacao() != null) {
//            dto.criteria.addOrder(Order.desc(filtro.getPropriedadeOrdenacao()));
//        }

        List list = dto.criteria.list();
        dto.session.close();
        return list;
    }

    public List<T> buscar2(Filtro<T> filtro) {
    	LOGGER.debug("Consulta da classe {}.", filtro.getClasse().getSimpleName());
        CriteriaDTO dto = criarCriteriaParaFiltro2(filtro);
        Query createQuery = dto.session.createQuery(dto.criteriaQueryClass);
        
        //Paginacao
        if (filtro.getQuantRegistros() != 0) {
        	LOGGER.debug("Paginação: primeiro {} mais {} registros", filtro.getPrimeiroRegistro(), filtro.getQuantRegistros());
            createQuery.setFirstResult(filtro.getPrimeiroRegistro());
    		createQuery.setMaxResults(filtro.getQuantRegistros());
        }
//        List<String> fetchs = filtro.getFetchs();
//        for (String property : fetchs) {
//            dto.criteria.setFetchMode(property, FetchMode.JOIN);
//        }

//        if (filtro.getPropriedadeOrdenacao() != null) {
//        	LOGGER.debug("Ordenação: ascendente? {} por? {}", filtro.isAscendente(), filtro.getPropriedadeOrdenacao());
//        	if(filtro.isAscendente()){
//        		dto.criteriaQueryClass.orderBy(dto.builder.asc(dto.from.get(filtro.getPropriedadeOrdenacao())));
//        	}else{
//        		dto.criteriaQueryClass.orderBy(dto.builder.desc(dto.from.get(filtro.getPropriedadeOrdenacao())));
//        	}
//        }

        List list = createQuery.getResultList();
    	LOGGER.debug("Retornado {} registros.", list.size());
    	if(LOGGER.isDebugEnabled()){
	    	for (Object object : list) {
	    		LOGGER.debug("	{}", ReflectionToStringBuilder.toString(object, ToStringStyle.DEFAULT_STYLE));
	    	}
    	}
    	
    	printStats("Buscar 2");
    	
        dto.session.close();
        return list;
    }
    
	private static void printStats(String msg) {
    	Statistics statistics = HibernateUtil.getStatistics();
		System.out.println("*****  ***** " +msg);
		System.out.println("Fetch Count=" + statistics.getEntityFetchCount());
		System.out.println("Cache Hit Count=" + statistics.getSecondLevelCacheHitCount());
		System.out.println("Cache Miss Count=" + statistics.getSecondLevelCacheMissCount());
		System.out.println("Cache Put Count=" + statistics.getSecondLevelCachePutCount());
		System.out.println();
	}

    @Deprecated
    public int getQuantidade(Filtro filtro) {
        CriteriaDTO criteria = criarCriteriaParaFiltro2(filtro);
        criteria.criteria.setProjection(Projections.rowCount());
        Object uniqueResult = criteria.criteria.uniqueResult();
        int count = uniqueResult != null ? ((Number) uniqueResult).intValue() : 0;
        criteria.session.close();
        return count;
    }
    
    public int getQuantidade2(Filtro filtro) {
    	LOGGER.debug("Consulta de contagem da classe {}.", filtro.getClasse().getSimpleName());
        CriteriaDTO dto = criarCriteriaParaFiltro2(filtro);
		CriteriaQuery<Long> cq = dto.builder.createQuery(Long.class);
		cq.select(dto.builder.count(cq.from(filtro.getClasse())));
		Long singleResult = dto.session.createQuery(cq).getSingleResult();
		int count = singleResult != null ? ((Number) singleResult).intValue() : 0;
        dto.session.close();
        return count;
    }
    
    @Deprecated
	private CriteriaDTO criarCriteriaParaFiltro(Filtro filtro) {
		Session session = getSession();
		Criteria criteria = session.createCriteria(filtro.getClasse());
		if(filtro.getFilters()!=null){
			Map<String, Object> map = filtro.getFilters();
			Set<String> keySet = map.keySet();
			for (String key : keySet) {
				Object value = map.get(key);
				if(value instanceof String){
					criteria.add(Restrictions.ilike(key, value.toString(), MatchMode.ANYWHERE));
				}else{
					criteria.add(Restrictions.eq(key, value));
				}
			}
		}
		CriteriaDTO c = new CriteriaDTO();
		c.criteria = criteria;
		c.session = session;
		return c;
	}

	/**
	 * @param filtro
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public CriteriaDTO criarCriteriaParaFiltro2(Filtro filtro) {
		
		Session session = getSession();
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery criteriaQueryClasse = builder.createQuery(filtro.getClasse());
		CriteriaQuery criteriaQueryId = builder.createQuery(Long.class);
		Root from = criteriaQueryClasse.from(filtro.getClasse());
		LOGGER.debug("Criando criteria da classe {}.", filtro.getClasse().getSimpleName());
		List<Predicate> where = new ArrayList<Predicate>();
		
		/* INNER JOIN */
		for(Object path : filtro.getJoins()){
			LOGGER.debug("Criando join para {}.", path.toString());
			String[] split = path.toString().split("\\.");
			Join join = null;
			for (String string : split) {
				if(join == null){
					join = from.join(string);
				}else{
					join = join.join(string); 
				}
			}
		}
		
		for(Object path : filtro.getFetchs()){
			LOGGER.debug("Criando fetch para {}.", path.toString());
			String[] split = path.toString().split("\\.");
			Fetch join = null;
			for (String string : split) {
				if(join == null){
					join = from.fetch(string);
				}else{
					join = join.fetch(string); 
				}
			}
		}
		
		/* WHERE */
		if(filtro.getFilters()!=null){
			
			Map<String, String> map = filtro.getFilters();
			Set<String> keySet = map.keySet();
			for (String key : keySet) {
				Object value = map.get(key);
				LOGGER.debug("WHERE: KEY: {} VALUE: {}", key, value);
				
				if(value instanceof String){
					String strValue = (String) value;
					String val = null;
					if(strValue.startsWith("%") || strValue.endsWith("%")){
						val = strValue;
					}else{
						val = "%"+String.valueOf(value).toLowerCase()+"%";
					}
					LOGGER.debug("{} like {}", key, val);
					Predicate like = builder.like(builder.lower(from.get(key)), val);
					where.add(like);
				}else{
					if(value.toString().startsWith("<") && value instanceof Number){
						LOGGER.debug("{} lt {}", key, value);
						where.add(builder.lt(from.get(key), new Double(value.toString())));
					}else if(value.toString().startsWith(">")){
						LOGGER.debug("{} gt {}", key, value);
						where.add(builder.gt(from.get(key), new Double(value.toString())));
					}else{
						LOGGER.debug("{} equal {}", key, value);
						where.add(builder.equal(from.get(key), value));
					}
				}
			}
			
			CriteriaQuery select = criteriaQueryClasse.select(from);
			select.where(where.toArray(new Predicate[where.size()]));
		}
		
		/* ORDER BY */
		if(filtro.getOrdenacoes().length > 0){
			List<javax.persistence.criteria.Order> orderList = new ArrayList<>();
			Ordenacao[] ordenacoes = filtro.getOrdenacoes();
			for (Ordenacao ordenacao : ordenacoes) {
				
				if(ordenacao.getPropriedade().contains(".")){
					
					String[] split = ordenacao.getPropriedade().split("\\.");
					
					Set fetches = from.getFetches();
					for (Object object : fetches) {
						if(object instanceof SingularAttributeJoin){
							SingularAttributeJoin attributeJoin = (SingularAttributeJoin) object;
							System.out.println(attributeJoin.getAttribute().getName());
							if(split[0].equals(attributeJoin.getAttribute().getName())){
								if(object instanceof Join){
									Join join = (Join) object;
									if(DirecaoOrdenacao.ASC == ordenacao.getDirecaoOrdenacao()){
										orderList.add(builder.asc(join.get(split[1])));
									}else{
										orderList.add(builder.desc(join.get(split[1])));
									}
								}else if(object instanceof Fetch){//??? nao vai passar aqui...
									Fetch fetch = (Fetch) object;
									Join join = (Join) object;
									orderList.add(builder.asc(join.get(split[1])));
								}							
							}
						}
					}
					
				}else{
					if(DirecaoOrdenacao.ASC == ordenacao.getDirecaoOrdenacao()){
						orderList.add(builder.asc(from.get(ordenacao.getPropriedade())));
					}else{
						orderList.add(builder.desc(from.get(ordenacao.getPropriedade())));
					}
				}
				
			}
			criteriaQueryClasse.orderBy(orderList);
		}
		
		CriteriaDTO criteriaDTO = new CriteriaDTO();
		criteriaDTO.criteriaQueryClass = criteriaQueryClasse;
		criteriaDTO.criteriaQueryId = criteriaQueryId;
		criteriaDTO.builder = builder;
		criteriaDTO.session = session;
		criteriaDTO.from = from;
		return criteriaDTO;
	}

    public <T> T list() throws DaoException {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        Class<?> forName = null;
        try {
            Type genericSuperclass = getClass().getGenericSuperclass();
            Type t = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
            forName = Class.forName(t.getTypeName());
            Query query = session.getNamedQuery(forName.getSimpleName()+"-list");
            List list = query.list();
            return list();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
        	transaction.commit();
			session.close();
		}
        return null;
    }
    
    public <T> T carregarCache() throws DaoException {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        Class<?> forName = null;
        try {
            Type genericSuperclass = getClass().getGenericSuperclass();
            Type t = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
            forName = Class.forName(t.getTypeName());
            return (T) session.createCriteria(forName).list();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
        	transaction.commit();
			session.close();
		}
        return null;
    }
            
}
