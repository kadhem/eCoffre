package edu.esprit.eCoffreWeb.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.esprit.eCoffreWeb.managedBean.UserBean;

@WebFilter("/pages/*")
public class FilterLogin implements Filter {

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
		if (login != null)
		{
			chain.doFilter(httpServletRequest, httpServletResponse);
		}
			
		else if (!httpServletRequest.getRequestURL().toString().contains("accueil.jsf")) 
		{
			httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/accueil.jsf");
		} 
		else
		{
			chain.doFilter(httpServletRequest, httpServletResponse);
		}
			

	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}

}
