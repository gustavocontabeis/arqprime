package br.com.coder.arqprime.model.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@Entity @Table(name="arquivo")
@NamedQueries(value={
	@NamedQuery(name="Arquivo-list", query="select obj from Arquivo obj "),
	@NamedQuery(name="Arquivo-porId", query="select obj from Arquivo obj where obj.id=:id")
	})
public class Arquivo extends BaseEntity {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator="seq_arquivo", strategy=GenerationType.SEQUENCE)
	@SequenceGenerator(name="seq_arquivo", sequenceName="seq_arquivo", allocationSize=1000, initialValue=1000)
	@Column(name="id_arquivo")
	private Long id;
	
	//@Lob
	@Column(name="dados", nullable=false,columnDefinition="BLOB")//HSQLDB
	private byte[] data;
	
	@Basic
	private Long tamanho;
	
	@Basic
	private String nome;
	
	@Basic
	private String descricao;
	
	@Basic
	private String extencao;
	
	@Basic
	private String contentType;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public Long getTamanho() {
		return tamanho;
	}

	public void setTamanho(Long tamanho) {
		this.tamanho = tamanho;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getExtencao() {
		return extencao;
	}

	public void setExtencao(String extencao) {
		this.extencao = extencao;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

}
