//ADICIONAR O PACOTE!<bean id="imagemDAO" class="ImagemDAO"></bean>
package br.com.coder.arqprime.model.dao.app;

import javax.inject.Named;

import br.com.coder.arqprime.model.entity.Arquivo;

@Named
public class ImagemDAO extends BaseDAO<Arquivo> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


}

