package br.com.coder.arqprime.model.dao.app;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Criteria;
import org.hibernate.Session;

public class CriteriaDTO {
	public Session session;
	public Criteria criteria;
	public CriteriaQuery criteriaQueryClass;
	public CriteriaQuery criteriaQueryId;
	public CriteriaBuilder builder;
	public Root from;
}
