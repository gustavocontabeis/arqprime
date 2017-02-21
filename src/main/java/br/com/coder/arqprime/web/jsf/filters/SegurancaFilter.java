package br.com.coder.arqprime.web.jsf.filters;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.coder.arqprime.model.entity.app.usuarios.Usuario;



@WebFilter(filterName = "SegurancaFilter", urlPatterns = {"*.jsf"})
public class SegurancaFilter implements Filter {

	private static final Logger LOGGER = LoggerFactory.getLogger(SegurancaFilter.class.getName());
	private Map<String, String[]> paginasPrivadas = new HashMap<>();
    private static final String URL_PATTERN = "";
    private Properties properties = new Properties();

    public SegurancaFilter() {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    	properties = new Properties();
		try {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			properties.load(classLoader.getResourceAsStream("seguranca.properties"));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Arquivo de configuração da segurança não localizado no classpath.");
		}
		Set<Object> keySet = properties.keySet();
		for (Object object : keySet) {
			System.out.println(object);
			if(object instanceof String){
				String key = (String) object;
				if(key.startsWith("/")){
					String property = properties.getProperty(key);
					System.out.println(property);
					String[] split = property.split(",");
					paginasPrivadas.put(key, split);
				}
			}
		}
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        try {

            HttpServletRequest request = (HttpServletRequest) req;
            HttpServletResponse response = (HttpServletResponse) res;
            HttpSession session = request.getSession(false);

            String requestURI = request.getRequestURI();
            if (requestURI.indexOf("/public/") >= 0
                    || requestURI.contains("javax.faces.resource")) {
            	LOGGER.trace("É público ou resource.");
                chain.doFilter(req, res);
                return;
            }
            
            LOGGER.debug("------------");
            
            //verifica se é privada e se esta logado e se tem permissao.
            LOGGER.debug("Pretende acessar: {}", requestURI);
            String page = getPage(request);
            
            if (paginasPrivadas.containsKey(page)) {
                LOGGER.debug("É uma página privada para {}", paginasPrivadas.get(page));
                Usuario usuario = (Usuario) getUser(session);
                if (usuario != null) {
                	LOGGER.debug("{} está logado.", usuario.getLogin());
                	
                	if(usuario.getDtInativo() != null){
                		LOGGER.debug("Usuário {} não autorizado a acessar {} pois esta inativo", usuario.getLogin(), requestURI!=null?requestURI:"");
                        redirecionarLogin(request, response, properties.getProperty("msg.usuario.nao.autorizado.inativo"));
                        return;
                	}
                	
                    String[] perfis = paginasPrivadas.get(page);
                    if (usuario.isContemPerfil(perfis)) {
                        LOGGER.debug("OK. {} autorizado a acessar {}", usuario.getLogin(), requestURI);
                        chain.doFilter(req, res);
                    } else {
                    	LOGGER.debug("Usuário {} não autorizado a acessar {}", usuario.getLogin(), requestURI!=null?requestURI:"");
                    	//FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Usuário ou senha inválidos."));
                        redirecionarLogin(request, response, properties.getProperty("msg.usuario.nao.autorizado"));
                    }
                } else {
                	LOGGER.debug("Nenhum usuário logado. Você será redirecionado para o login.");
                	//redirecionarLogin(request, response);
                	redirecionarLogin(request, response, properties.getProperty("msg.acesso.privado"));
                }
            }else{
            	//LOGGER.debug("É uma página pública.");
            	chain.doFilter(req, res);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	private String getPage(HttpServletRequest request) {
		String requestURI = request.getRequestURI();
		return requestURI.replace(request.getContextPath(), "").replace(URL_PATTERN, "").replaceAll(";jsessionid=.*.?", "");
	}
	
    private static Object getUser(HttpSession session) {
        if(session == null){
            return null;
        }
        return session.getAttribute("usuario");
    }

	private void redirecionarLogin(HttpServletRequest request, HttpServletResponse response, String msg) throws IOException {
		
		if(StringUtils.isNotBlank(msg)){
			msg = URLDecoder.decode(msg, "UTF-8");
		}else{
			msg = StringUtils.EMPTY;
		}
		
        LOGGER.debug("Redirecionado para login com destino a \"{}\".", request.getRequestURI());
        request.getSession(false).setAttribute("destino", request.getRequestURI());
        response.sendRedirect(request.getContextPath() + properties.getProperty("page.login") + (StringUtils.isNotBlank(msg)?"?msg="+msg:""));
	}

    @Override
    public void destroy() {

    }
}
