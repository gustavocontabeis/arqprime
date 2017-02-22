package br.com.coder.arqprime.web.jsf.managedbeans.app;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.coder.arqprime.model.dao.app.DaoException;
import br.com.coder.arqprime.model.dao.app.usuarios.PerfilAcessoDAO;
import br.com.coder.arqprime.model.dao.app.usuarios.UsuarioDAO;
import br.com.coder.arqprime.model.entity.BaseEntity;
import br.com.coder.arqprime.model.entity.app.usuarios.PerfilAcesso;
import br.com.coder.arqprime.model.entity.app.usuarios.Usuario;
import br.com.coder.arqprime.model.utils.GenerateMD5;

@Named @ViewScoped
public class UsuarioManagedBean extends CrudManagedBean<Usuario, UsuarioDAO> {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(UsuarioManagedBean.class.getName());

	private List<PerfilAcesso> perfisAcesso;
	private String[] selecionados;
	private String confirmeSenha, senhaAnterior;
	private boolean trocarSenha = false;
	
	@Inject private UsuarioDAO dao;// = new UsuarioDAO();
	@Inject private PerfilAcessoDAO perfilAcessoDAO;// = new PerfilAcessoDAO();
	private Properties properties = new Properties();
	
	@PostConstruct
	private void init() {
		perfisAcesso = perfilAcessoDAO.buscarTodos();
		try {
			properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties"));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Arquivo de configuração da segurança não localizado no classpath.");
		}
	}

	@Override
	protected Usuario novo() {
		selecionados = null;
		entity = new Usuario(); 
		return entity;
	}
	
	@Override
	protected boolean salvarAntes(Usuario entity) {
		if(trocarSenha){
			return true;
		}
		String perfis = "";
		for(String selecionado : selecionados){
			perfis += selecionado+",";
		}
		entity.setPerfis(perfis);
		entity.setDtSenha(new Date());
		entity.setSenha(GenerateMD5.generateMD5(entity.getSenha()));
		return true;
	}
	
	@Override
	protected BaseEntity buscarAntes(Usuario entity, String rowKey) {
		selecionados = null;
		return super.buscarAntes(entity, rowKey);
	}
	
	@Override
	protected void buscarApos(Usuario entity) {
		selecionados = entity.getPerfisArray();
	}
	
	public void trocarSenha(ActionEvent evt) throws DaoException {
		trocarSenha = true;
		Usuario buscar = dao.buscar(entity.getId());
		String generateMD5 = GenerateMD5.generateMD5(senhaAnterior);
		if(!generateMD5.equals(buscar.getSenha())){
			messageError(null, "A senha anterior não esta correta.");
			return;
		}
		generateMD5 = GenerateMD5.generateMD5(confirmeSenha);
		if(!generateMD5.equals(GenerateMD5.generateMD5(entity.getSenha()))){
			messageError(null, "As senhas não estão corretas.");
			return;
		}
		entity.setSenha(generateMD5);
		salvar(null);
	}
	
	/**
	 * update users set user_pass = '6bf6ffe53e1b042a6f06dec103e30095' where user_name = 'giana'; 
	 * @param evt
	 */
	public String solicitarNovaSenha() {
		try {
			if(StringUtils.isBlank(entity.getEmail())){
				message(null, "Informe o e-mail.");
				return null;
			}
			Usuario buscarPorEmail = null;
			if(StringUtils.isNotBlank(entity.getEmail()))
				buscarPorEmail = dao.buscarPorEmail(entity.getEmail());
				
			if(buscarPorEmail == null){
				message(null, entity.getEmail()+" não cadastrado.");
				return null;
			}else{
				entity = buscarPorEmail;
				String novaSenha = gerarNovaSenha();
				enviarEmailNovaSenha(novaSenha);
				message(null, "A nova senha soi enviada para "+ entity.getEmail()+".");
				entity = new Usuario();
				return "/pages/login/login.jsf";
			}
		} catch (Exception e) {
			message(e);
		} 
		return null;
	}
	
	private void enviarEmailNovaSenha(String novaSenha) throws DaoException, UnsupportedEncodingException, MessagingException {
		
		entity.setSenha(GenerateMD5.generateMD5(novaSenha));
		dao.salvar(entity);
		
		final String username = properties.getProperty("email.from");
		final String password = properties.getProperty("email.password");

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(properties.getProperty("email.from")));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(entity.getEmail()));
			message.setSubject(properties.getProperty("recupera.senha.assunto"));
			message.setText("Sua nova senha é "+novaSenha);

			Transport.send(message);

			LOGGER.debug("E-mail para criação de nova senha enviado com sucesso.");

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	
	private String gerarNovaSenha() {
		String novasenha = "";
		String caracteres = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890";
		while(novasenha.length()<8)
			novasenha += caracteres.charAt( new Random().nextInt(caracteres.length()- 1));
		return novasenha;
	}

	
	@Override
	protected UsuarioDAO getDao() {
		return dao;
	}

	public List<PerfilAcesso> getPerfisAcesso() {
		return perfisAcesso;
	}

	public void setPerfisAcesso(List<PerfilAcesso> perfisAcesso) {
		this.perfisAcesso = perfisAcesso;
	}

	public String[] getSelecionados() {
		return selecionados;
	}

	public void setSelecionados(String[] selecionados) {
		this.selecionados = selecionados;
	}

	public String getConfirmeSenha() {
		return confirmeSenha;
	}

	public void setConfirmeSenha(String confirmeSenha) {
		this.confirmeSenha = confirmeSenha;
	}

	public String getSenhaAnterior() {
		return senhaAnterior;
	}

	public void setSenhaAnterior(String senhaAnterior) {
		this.senhaAnterior = senhaAnterior;
	}

	
}