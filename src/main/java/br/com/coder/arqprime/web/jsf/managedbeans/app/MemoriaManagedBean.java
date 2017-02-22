package br.com.coder.arqprime.web.jsf.managedbeans.app;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.FacesEvent;

import org.primefaces.model.chart.PieChartModel;

import br.com.coder.arqprime.model.utils.DateUtil;
import br.com.coder.arqprime.model.utils.HibernateUtil;

@ManagedBean
@ViewScoped
public class MemoriaManagedBean implements Serializable {

	/**
	 */
	private static final long serialVersionUID = 1L;
	
	private long freeMemory;
	private long maxMemory;
	private long totalMemory;
	private int availableProcessors;

	@PostConstruct
	private void init() {
		freeMemory = Runtime.getRuntime().freeMemory();
		maxMemory = Runtime.getRuntime().maxMemory();
		totalMemory = Runtime.getRuntime().totalMemory();
		availableProcessors = Runtime.getRuntime().availableProcessors();
	}

	public void garbageColector(FacesEvent evt) {
		Runtime.getRuntime().gc();
	}

	public void inicializarHibernate(FacesEvent evt) { 
            //HibernateUtil5.getSessionFactory().openSession();
	}

	public String getFreeMemory() {
		return NumberFormat.getIntegerInstance().format(freeMemory);
	}

	public void setFreeMemory(long freeMemory) {
		this.freeMemory = freeMemory;
	}

	public String getMaxMemory() {
		return NumberFormat.getIntegerInstance().format(maxMemory);
	}

	public void setMaxMemory(long maxMemory) {
		this.maxMemory = maxMemory;
	}

	public int getAvailableProcessors() {
		return availableProcessors;
	}

	public void setAvailableProcessors(int availableProcessors) {
		this.availableProcessors = availableProcessors;
	}

	public String getTotalMemory() {
		return NumberFormat.getIntegerInstance().format(totalMemory);
	}

	public String getUtilizado() {
		return NumberFormat.getIntegerInstance().format(
				totalMemory - freeMemory);
	}

	public void setTotalMemory(long totalMemory) {
		this.totalMemory = totalMemory;
	}

	public String getPercent() {
		double l = (((double) freeMemory) / ((double) totalMemory)) * 100d;
		return NumberFormat.getNumberInstance().format(l) + "%";
	}

	public String getAgora() {
		return DateUtil.full(new Date());
	}

}

