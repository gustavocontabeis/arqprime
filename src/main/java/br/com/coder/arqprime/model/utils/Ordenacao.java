package br.com.coder.arqprime.model.utils;

import java.io.Serializable;

public class Ordenacao implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String propriedade;
	private DirecaoOrdenacao direcaoOrdenacao;
	public Ordenacao(String propriedade, DirecaoOrdenacao direcaoOrdenacao) {
		super();
		this.propriedade = propriedade;
		this.direcaoOrdenacao = direcaoOrdenacao;
	}
	public Ordenacao(String propriedade) {
		super();
		this.propriedade = propriedade;
		this.direcaoOrdenacao = direcaoOrdenacao.ASC;
	}
	public String getPropriedade() {
		return propriedade;
	}
	public DirecaoOrdenacao getDirecaoOrdenacao() {
		return direcaoOrdenacao;
	}
}