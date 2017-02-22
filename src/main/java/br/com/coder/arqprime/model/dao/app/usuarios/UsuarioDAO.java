package br.com.coder.arqprime.model.dao.app.usuarios;

import java.util.List;

import javax.inject.Named;
import javax.persistence.NoResultException;

import org.hibernate.Session;
import org.hibernate.query.Query;

import br.com.coder.arqprime.model.dao.app.BaseDAO;
import br.com.coder.arqprime.model.entity.app.usuarios.Usuario;

//@Dependent
@Named
public class UsuarioDAO extends BaseDAO<Usuario> {
	/**
	 */
	private static final long serialVersionUID = 1L;

	public List<Usuario> buscarTodos() {
		Session session = getSession();
		List list = session.getNamedQuery("todosUsuario").list();
		session.close();
		return list;
	}

	public Usuario buscarComPerfis(Long id) {
		//new Usuario().getUsuarioPerfis().get(0).getPerfil()
		Session session = getSession();
		Query query = session.getNamedQuery("Usuario.getUsuarioComPerfis");
		query.setParameter("id", id);
		Usuario singleResult = (Usuario) query.getSingleResult();
		session.close();
		return singleResult;
	}

	public Usuario buscarComPerfis(String login) {
		//new Usuario().getUsuarioPerfis().get(0).getPerfil()
		Usuario singleResult;
		try {
			Session session = getSession();
			Query query = session.getNamedQuery("Usuario.getUsuarioComPerfisPorLogin");
			query.setParameter("login", login);
			singleResult = (Usuario) query.getSingleResult();
			session.close();
		} catch (NoResultException e) {
			return null;
		}
		return singleResult;
	}

	public Usuario buscarPorEmail(String email) {
		Usuario singleResult;
		try {
			Session session = getSession();
			Query query = session.getNamedQuery("Usuario-porEmail");
			query.setParameter("email", email);
			singleResult = (Usuario) query.getSingleResult();
			session.close();
		} catch (NoResultException e) {
			return null;
		}
		return singleResult;
	}

	public Usuario buscarPorLogin(String login) {
		Usuario singleResult;
		try {
			Session session = getSession();
			Query query = session.getNamedQuery("Usuario-porLogin");
			query.setParameter("login", login);
			singleResult = (Usuario) query.getSingleResult();
			session.close();
		} catch (NoResultException e) {
			return null;
		}
		return singleResult;
	}

}

