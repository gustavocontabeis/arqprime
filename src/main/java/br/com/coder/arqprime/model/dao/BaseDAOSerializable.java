package br.com.coder.arqprime.model.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.com.coder.arqprime.model.entity.BaseEntity;
import br.com.coder.arqprime.model.utils.Filtro;
import br.com.coder.arqprime.model.utils.HibernateUtil;

public class BaseDAOSerializable<T extends Serializable> implements Serializable {

    private static final long serialVersionUID = 1L;

    protected Session getSession() {
        return HibernateUtil.getSession();
    }

    public void salvar(T obj) throws DaoException {
        validate(obj);
        Session session = getSession();
        session.beginTransaction();
        session.saveOrUpdate(obj);
        session.getTransaction().commit();
        //session.flush();
        session.close();
    }

    public void excluir(T obj) throws DaoException {
        validate(obj);
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        session.delete(obj);
        transaction.commit();
        //session.flush();
        session.close();
    }

    @SuppressWarnings({"unchecked", "hiding"})
    public <T> T buscar(T obj, Long id) throws DaoException {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        T load = (T) session.get(obj.getClass(), id);
        //session.flush();
        transaction.commit();
        session.close();
        return load;
    }

    public <T> T buscar(Long id) throws DaoException {
        Session session = getSession();
        Transaction transaction = session.beginTransaction();
        Class<?> forName = null;
        try {
            Type genericSuperclass = getClass().getGenericSuperclass();
            Type t = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
            forName = Class.forName(t.getTypeName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        T load = (T) session.get(forName, id);
        transaction.commit();
        //session.flush();
        session.close();
        return load;
    }

    private void validate(Serializable obj) throws DaoException {

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<Serializable>> constraintViolations = validator.validate(obj);
        if (constraintViolations.size() > 0) {
            throw new DaoException(constraintViolations.iterator().next().getMessage());
        }

    }

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

        if (filtro.isAscendente() && filtro.getPropriedadeOrdenacao() != null) {
            dto.criteria.addOrder(Order.asc(filtro.getPropriedadeOrdenacao()));
        } else if (filtro.getPropriedadeOrdenacao() != null) {
            dto.criteria.addOrder(Order.desc(filtro.getPropriedadeOrdenacao()));
        }

        List list = dto.criteria.list();
        dto.session.close();
        return list;
    }

    public int getQuantidade(Filtro filtro) {
        CriteriaDTO criteria = criarCriteriaParaFiltro(filtro);
        criteria.criteria.setProjection(Projections.rowCount());
        Object uniqueResult = criteria.criteria.uniqueResult();
        int count = uniqueResult != null ? ((Number) uniqueResult).intValue() : 0;
        criteria.session.close();
        return count;
    }

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

	private void testeDeMerda(Filtro filtro, Session session) {
		CriteriaBuilder builder = session.getCriteriaBuilder();
		CriteriaQuery criteria2 = builder.createQuery(filtro.getClasse());
		Root<BaseEntity> from = criteria2.from(filtro.getClasse());
		List<Predicate> where = new ArrayList<Predicate>();
		if(filtro.getFilters()!=null){
			Map<String, Object> map = filtro.getFilters();
			Set<String> keySet = map.keySet();
			for (String key : keySet) {
				Object value = map.get(key);
				if(value instanceof String){
					where.add(null);
					Restrictions.ilike(key, value.toString(), MatchMode.ANYWHERE);
				}else{
					where.add(builder.equal(from.get(key), value));
					//criteria2.add(Restrictions.eq(key, value));
				}
			}
			criteria2.select(from);
			criteria2.where(where.toArray(new Predicate[where.size()]));
		}
		List resultList = session.createQuery(criteria2).getResultList();
		for (Object object : resultList) {
			System.out.println(object);
		}
	}
	

}
