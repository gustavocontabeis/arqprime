package br.com.coder.arqprime.web.jsf.managedbeans.app;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.coder.arqprime.model.dao.app.DaoException;
import br.com.coder.arqprime.model.dao.app.usuarios.UsuarioDAO;
import br.com.coder.arqprime.model.entity.app.usuarios.Usuario;
import br.com.coder.arqprime.model.utils.GenerateMD5;

@Named @SessionScoped
public class LoginManagedBean implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(LoginManagedBean.class.getSimpleName());

	private String username;
	private String password;
	private Usuario usuario;
	private Properties properties;
	private String template = "layout-2", fontSize = "12px";
	
	@Inject 
	private UsuarioDAO usuarioDAO;
	
	@PostConstruct
	private void init() throws FileNotFoundException, IOException{
		properties = new Properties();
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		properties.load(classLoader.getResourceAsStream("config.properties"));	}
	
	public void login(ActionEvent event) throws IOException, DaoException {

		Usuario usuario = usuarioDAO.buscarComPerfis(this.username);
		
		if(usuario == null){
			LOGGER.debug("Usuario {} não confere.", username);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ops!", "Usuário inexistente"));
			return;
		}
		
		String generate = GenerateMD5.generateMD5(this.password);
		if (generate.equals(usuario.getSenha())) { 
			ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
			
			HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
			HttpSession session = request.getSession();
			session.setAttribute("usuario", usuario);
			this.usuario = usuario;
			
			if(session.getAttribute("destino") != null){
				HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
				String destino = (String) session.getAttribute("destino");
				LOGGER.debug("Redirecionando para : "+ destino);
				response.sendRedirect(destino);
				session.removeAttribute("destino");
			}else{
				redirecionarPaginaInicial();
			}
			
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Bem vindo", username));
		}else{
			LOGGER.debug("Usuario {} e senha {} não conferem.", username, password);
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ops!", "Senha inválida"));
		}

	}

	public void loginDesenv(ActionEvent event) throws IOException, DaoException {
		this.username = "gustavo";
		this.password = "123";
		login(null);
	}
	
	private void redirecionarPaginaInicial() throws IOException {
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
		HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
		response.sendRedirect(request.getContextPath() + properties.getProperty("page.inicial"));
	}

	public void logout(ActionEvent event) throws IOException {
		LOGGER.debug("..>> Logout <<..");
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
		HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
		request.getSession().invalidate();
		HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
		response.sendRedirect(request.getContextPath() + properties.getProperty("page.login"));
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isLogado(){
		return this.usuario != null;
	}

	public boolean isContemPerfil(String perfis){
		return isLogado() && usuario.isContemPerfil(perfis.split("\\,"));
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getFontSize() {
		return fontSize;
	}

	public void setFontSize(String fontSize) {
		this.fontSize = fontSize;
	}

}