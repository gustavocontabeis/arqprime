package br.com.coder.arqprime.model.dao.app.usuarios;

import java.util.List;

import javax.inject.Named;

import org.hibernate.Session;

import br.com.coder.arqprime.model.dao.app.BaseDAO;
import br.com.coder.arqprime.model.entity.app.usuarios.PerfilAcesso;

@Named
public class PerfilAcessoDAO extends BaseDAO<PerfilAcesso> {

	/**
	 */
	private static final long serialVersionUID = 1L;

	public List<PerfilAcesso> buscarTodos() {
		Session session = getSession();
		List list = session.getNamedQuery("todosPerfilAcesso").list();
		session.close();
		return list;
	}

}

