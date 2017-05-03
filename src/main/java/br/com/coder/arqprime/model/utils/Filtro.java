package br.com.coder.arqprime.model.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.primefaces.model.SortOrder;

public class Filtro<T extends Serializable> implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Class classe;
	private int primeiroRegistro = 0, quantRegistros = 0;
	private Ordenacao[] ordenacoes = new Ordenacao[0];
	private Map<String, Object> filters = new HashMap<>();
	private List<String> fetchs = new ArrayList<>();
	private List<String> joins = new ArrayList<>();
	
	public Filtro() {
		super();
	}
	
	public Filtro(Class<? extends Serializable> classe) {
		this.classe = classe;
	}
	
	public Filtro(Class<? extends Serializable> classe, int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
		this.classe = classe;
		this.primeiroRegistro = first;
		this.quantRegistros = pageSize;
		if(!StringUtil.isBlank(sortField)){
			this.ordenacoes = new Ordenacao[1];
			this.ordenacoes[0] = new Ordenacao(sortField, SortOrder.ASCENDING.equals(sortOrder)?DirecaoOrdenacao.ASC:DirecaoOrdenacao.DESC);
		}
		this.filters = filters;
	}
	
	public void addOrder(String propriedade){
		addOrder(propriedade, DirecaoOrdenacao.ASC);
	}
	
	public void addOrder(String propriedade, DirecaoOrdenacao direcao){
		Ordenacao[] copyOf = Arrays.copyOf(ordenacoes, ordenacoes.length+1);
		copyOf[copyOf.length-1] = new Ordenacao(propriedade, direcao);
		ordenacoes = copyOf;
	}
	
	public int getPrimeiroRegistro() {
		return primeiroRegistro;
	}
	public void setPrimeiroRegistro(int primeiroRegistro) {
		this.primeiroRegistro = primeiroRegistro;
	}
	public int getQuantRegistros() {
		return quantRegistros;
	}
	public void setQuantRegistros(int quantRegistros) {
		this.quantRegistros = quantRegistros;
	}
	public Class getClasse() {
		return classe;
	}
	public void setClasse(Class classe) {
		this.classe = classe;
	}
	public Map<String, Object> getFilters() {
		return filters;
	}
	public void setFilters(Map<String, Object> filters) {
		this.filters = filters;
	}
	public List<String> getFetchs() {
		return fetchs;
	}
	public void setFetchs(List<String> fetchs) {
		this.fetchs = fetchs;
	}
	public Ordenacao[] getOrdenacoes() {
		return ordenacoes;
	}
	public void setOrdenacoes(Ordenacao[] ordenacoes) {
		this.ordenacoes = ordenacoes;
	}
	public Filtro<T> addFilter(String parameter, Object value) {
		filters.put(parameter, value);
		return this;
	}
	public void addFetch(String...properties) {
		for (String property : properties) {
			getFetchs().add(property);
		}
	}
	public List<String> getJoins() {
		return joins;
	}
	public void setJoins(List<String> joins) {
		this.joins = joins;
	}
	
}

