package br.com.coder.arqprime.model.entity;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseEntity implements Serializable {
	
	/**
	 */
	private static final long serialVersionUID = 1L;
	
	public abstract Long getId();
	public abstract void setId(Long id);
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getClass().getName().hashCode();
		result = prime * result + ( getId()!=null ? getId().intValue():1 );
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		BaseEntity other = (BaseEntity) obj;
		
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		
		return true;
	}
	
}
