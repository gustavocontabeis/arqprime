package br.com.maxig.model.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.primefaces.model.SortOrder;

public class Filtro<T extends Serializable> implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Class classe;
	private int primeiroRegistro = 0, quantRegistros = 0;
	private boolean ascendente = true;
	private String propriedadeOrdenacao;
	private Map<String, Object> filters = new HashMap<>();
	private List<String> fetchs = new ArrayList<String>();
	
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
		this.propriedadeOrdenacao = sortField;
		this.ascendente = SortOrder.ASCENDING.equals(sortOrder);
		this.filters = filters;
	}
	
//	public Filtro(Class<Municipio> classe, Map<String, Object> filters) {
//		this.classe = classe;
//		this.filters = filters;
//	}

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
	public boolean isAscendente() {
		return ascendente;
	}
	public void setAscendente(boolean ascendente) {
		this.ascendente = ascendente;
	}
	public String getPropriedadeOrdenacao() {
		return propriedadeOrdenacao;
	}
	public void setPropriedadeOrdenacao(String propriedadeOrdenacao) {
		this.propriedadeOrdenacao = propriedadeOrdenacao;
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

	public Filtro<T> addFilter(String parameter, Object value) {
		filters.put(parameter, value);
		return this;
	}

	public void addFetch(String...properties) {
		for (String property : properties) {
			getFetchs().add(property);
		}
	}
		
}
