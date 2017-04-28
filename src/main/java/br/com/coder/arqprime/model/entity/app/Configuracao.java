package br.com.coder.arqprime.model.entity.app;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import br.com.coder.arqprime.model.entity.BaseEntity;

@Entity
@Table(name="CONFIGURACAO", 
	indexes={
		@Index(name="INDEX_CONFIGURACAO_CHAVE", columnList = "CHAVE")
	})
@Cache(usage=CacheConcurrencyStrategy.READ_ONLY, region="Configuracao")
@NamedQueries(value={
		@NamedQuery(name="Configuracao-buscar", 
				query="select obj from Configuracao obj where obj.id = :id"),
		@NamedQuery(name="Configuracao-buscarPorChave", 
		query="select obj from Configuracao obj where obj.chave = :chave")
	})
public class Configuracao extends BaseEntity {
	
	private static final long serialVersionUID = 1L;
 	
 	@Id 
 	@GeneratedValue(generator="SEQ_CONFIGURACAO", strategy=GenerationType.SEQUENCE) 
	@SequenceGenerator(name="SEQ_CONFIGURACAO", sequenceName="SEQ_CONFIGURACAO", initialValue=1000) 
 	@Column(name="ID_CONFIGURACAO", length=10,  nullable=false)
 	private Long id;
 
 	@Column(name="CHAVE", length=30,  nullable=false)
 	private String chave;
 	
 	@Column(name="VALOR", length=1000,  nullable=true)
 	private String valor;
 	
	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public String getChave() {
		return chave;
	}

	public void setChave(String chave) {
		this.chave = chave;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

}
