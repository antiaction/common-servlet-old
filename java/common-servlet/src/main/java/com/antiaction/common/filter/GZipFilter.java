/*
 * Experimental response gzip filter.
 * Copyright (C) 2004  Nicholas Clarke
 *
 */

/*
 * History:
 *
 * 04-Oct-2004 : Initial implementation.
 *
 */

package com.antiaction.common.filter;

import java.io.IOException;
import java.util.zip.GZIPOutputStream;

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
 * Experimental response gzip filter.
 *
 * @version 1.00
 * @author Nicholas Clarke <mayhem[at]antiaction[dot]com>
 */
public class GZipFilter implements Filter {

	/** FilterConfig used to obtaint ServletContext. */
	private FilterConfig filterConfig;

	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
	}

	public void destroy() {
	}

	/** GZIP OutputStream. */
	private GZIPOutputStream gzipOut;

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse resp = (HttpServletResponse)response;
		ServletContext servletContext = filterConfig.getServletContext();



		chain.doFilter( req, resp );
	}

	//	dres.setHeader( "Content-Encoding", "gzip" );
	/*
				if ( bytes != null ) {
					contentLength = bytes.length;
					//byte[] bytesGZiped = null;
					try {
						ByteArrayOutputStream gzipOutStream = new ByteArrayOutputStream( bytes.length );
						gzipOutStream.reset();
						GZIPOutputStream gzipStream = new GZIPOutputStream( gzipOutStream, bytes.length );
						gzipStream.write( bytes, 0, bytes.length );
						gzipStream.finish();
						bytesGZiped = gzipOutStream.toByteArray();
					}
					catch (IOException e) {
					}
				}
	*/

}
