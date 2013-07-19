/*
 * Experimental POST ban filter.
 * Copyright (C) 2005  Nicholas Clarke
 *
 */

/*
 * History:
 *
 * 20-Dec-2005 : Initial implementation.
 *
 */

package com.antiaction.common.filter;

import com.antiaction.common.strings.Strings;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Experimental POST ban filter.
 *
 * @version 1.00
 * @author Nicholas Clarke <mayhem[at]antiaction[dot]com>
 */
public class PostBanFilter implements Filter {

	/** FilterConfig used to obtaint ServletContext. */
	private FilterConfig filterConfig;

	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
	}

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse resp = (HttpServletResponse)response;
		ServletContext servletContext = filterConfig.getServletContext();

		boolean banned = false;

		String ipStr = req.getRemoteAddr();
		ipBytes( ipStr );

		if ( !banned ) {
			chain.doFilter( request, response );
		}
		else {
		}
	}

	public byte[] ipBytes(String ipStr) {
		byte[] ipBytes = null;
		if ( ipStr != null && ipStr.length() > 0 ) {
			List list = Strings.splitString( ipStr, "." );
			if ( list.size() == 4 ) {
				try {
					ipBytes = new byte[ 4 ];
					for ( int i=0; i<4; ++i ) {
						ipBytes[ i ] = (byte)(Integer.parseInt( (String)list.get( i ) ) & 255 );
					}
				}
				catch (NumberFormatException e) {
					ipBytes = null;
				}
			}
		}
		return ipBytes;
	}

}
