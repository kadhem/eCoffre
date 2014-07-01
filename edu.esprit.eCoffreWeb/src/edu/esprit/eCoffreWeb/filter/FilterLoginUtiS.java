package edu.esprit.eCoffreWeb.filter;

import java.io.IOException;
import java.io.Serializable;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.esprit.eCoffreEJB.Entities.UTI_S;
import edu.esprit.eCoffreWeb.managedBean.UserBean;

@WebFilter(urlPatterns = "/pages/utis/*", dispatcherTypes = {DispatcherType.REQUEST, DispatcherType.FORWARD})
public class FilterLoginUtiS implements Filter,Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		System.out.println(httpServletRequest.getRequestURL().toString());
		UserBean login = (UserBean) httpServletRequest.getSession()
				.getAttribute("userBean");
		if ( (login != null) && (login.getUser() instanceof UTI_S) )
			chain.doFilter(httpServletRequest, httpServletResponse);
		else if (!httpServletRequest.getRequestURL().toString()
				.contains("accueil.jsf")) {
			httpServletResponse.sendRedirect(httpServletRequest
					.getContextPath() + "/accueil.jsf");
		} else
			chain.doFilter(httpServletRequest, httpServletResponse);

	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}

}
