/*
 * Created on 03/05/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.raptor.frontend;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import com.antiaction.common.strings.Strings;

/**
 * Servlet Filter to set cache enable or disable headers based on requested resource extension. 
 * @author Nicholas
 *
 */
public class CacheControlFilter implements Filter {

	/** FilterConfig used to obtaint ServletContext. */
	private FilterConfig filterConfig;

	private Set<String> cacheExtMap = new HashSet<String>();

	private Set<String> nocacheExtMap = new HashSet<String>();

	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;

		String extStr;
		List<String> extList;
		String ext;

		extStr = this.filterConfig.getInitParameter( "cache.ext" );
		if ( extStr != null && extStr.length() > 0 ) {
			extList = Strings.splitString( extStr.toLowerCase(), "," );
			if ( extList != null && extList.size() > 0 ) {
				for ( int i=0; i<extList.size(); ++i ) {
					ext = extList.get( i ).trim();
					cacheExtMap.add( ext );
				}
			}
		}

		extStr = this.filterConfig.getInitParameter( "nocache.ext" );
		if ( extStr != null && extStr.length() > 0 ) {
			extList = Strings.splitString( extStr.toLowerCase(), "," );
			if ( extList != null && extList.size() > 0 ) {
				for ( int i=0; i<extList.size(); ++i ) {
					ext = extList.get( i ).trim();
					nocacheExtMap.add( ext );
				}
			}
		}
	}

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest)request;
		HttpServletResponse httpResponse = (HttpServletResponse)response;
		//ServletContext servletContext = filterConfig.getServletContext();

		String contextPath = httpRequest.getContextPath();
		String servletPath = httpRequest.getServletPath();
		String pathInfo = httpRequest.getPathInfo();
		String path = contextPath + servletPath + pathInfo;

		int caching = 0;

		int idx = path.lastIndexOf( '.' );

		if ( idx > 0 && idx < path.length() ) {
			String ext = path.substring( idx + 1 ).toLowerCase();

			if ( cacheExtMap.contains( ext ) ) {
				caching = 1;
			}

			if ( nocacheExtMap.contains( ext )) {
				caching = -1;
			}
		}

		// debug
		//System.out.println( path + "=" + caching );

		if ( caching != 0 ) {
			httpResponse = new FilteredResponse( httpRequest, httpResponse, path, caching );
		}

		chain.doFilter( httpRequest, httpResponse );
	}

	class FilteredResponse extends HttpServletResponseWrapper {

		HttpServletRequest httpRequest;
		HttpServletResponse httpResponse;
		String path;
		int caching = 0;
		ServletOutputStream out;

		FilteredResponse(HttpServletRequest httpRequest, HttpServletResponse httpResponse, String path, int caching) {
			super( httpResponse );
			this.httpRequest = httpRequest;
			this.httpResponse = httpResponse;
			this.path = path;
			this.caching = caching;
		}

		@Override
		public ServletOutputStream getOutputStream() throws IOException {
			if ( out == null ) {
				if ( caching == 0 ) {
					out = super.getOutputStream();
				}
				else {
					out = new FilteredServletOutputStream( this, super.getOutputStream() );
				}
			}
			return out;
		}

		@Override
		public void setDateHeader(String name, long date) {
			if ( "expires".compareToIgnoreCase( name ) == 0 ) {
				caching = 0;
			}
			httpResponse.setDateHeader( name, date );
		}

		@Override
		public void addDateHeader(String name, long date) {
			if ( "expires".compareToIgnoreCase( name ) == 0 ) {
				caching = 0;
			}
			httpResponse.addDateHeader( name, date );
		}

		@Override
		public void setHeader(String name, String value) {
			if ( "expires".compareToIgnoreCase( name ) == 0 ) {
				caching = 0;
			}
			else if ( "cache-control".compareToIgnoreCase( name ) == 0 ) {
				caching = 0;
			}
			else if ( "pragma".compareToIgnoreCase( name ) == 0 ) {
				caching = 0;
			}
			httpResponse.setHeader( name, value );
		}

		@Override
		public void addHeader(String name, String value) {
			if ( "expires".compareToIgnoreCase( name ) == 0 ) {
				caching = 0;
			}
			else if ( "cache-control".compareToIgnoreCase( name ) == 0 ) {
				caching = 0;
			}
			else if ( "pragma".compareToIgnoreCase( name ) == 0 ) {
				caching = 0;
			}
			httpResponse.addHeader( name, value );
		}

		@Override
		public void sendError(int sc) throws IOException {
			caching = 0;
			httpResponse.sendError( sc );
		}

		@Override
		public void sendError(int sc, String msg) throws IOException {
			caching = 0;
			httpResponse.sendError( sc, msg );
		}

		@Override
		public void sendRedirect(String location) throws IOException {
			caching = 0;
			httpResponse.sendRedirect( location );
		}

		@Override
		public void setStatus(int sc) {
			if ( sc != HttpServletResponse.SC_OK ) {
				caching = 0;
			}
			httpResponse.setStatus( sc );
		}

		@Override
		public void setStatus(int sc, String sm) {
			if ( sc != HttpServletResponse.SC_OK ) {
				caching = 0;
			}
			httpResponse.setStatus( sc, sm );
		}

		public void setHeader() {
			if ( caching == -1 ) {
				// Set to expire far in the past.
				httpResponse.setHeader( "Expires", "Sat, 6 May 1995 12:00:00 GMT" );

				// Set standard HTTP/1.1 no-cache headers.
				httpResponse.setHeader( "Cache-Control", "no-store, no-cache, must-revalidate" );

				// Set IE extended HTTP/1.1 no-cache headers (use addHeader).
				httpResponse.addHeader( "Cache-Control", "post-check=0, pre-check=0" );

				// Set standard HTTP/1.0 no-cache header.
				httpResponse.setHeader( "Pragma", "no-cache" );

				// debug
				//System.out.println( "Cached: " + path );

				caching = 0;
			}
			else if ( caching == 1 ) {
				Calendar cal = new GregorianCalendar( TimeZone.getTimeZone( "GMT" ) );

				// Set to expire in the future.
				httpResponse.setDateHeader( "Expires", cal.getTime().getTime() + (24 * 60 * 60 * 1000) );

				// Set standard HTTP/1.1 no-cache headers.
				httpResponse.setHeader( "Cache-Control", "max-age=" + Integer.toString( 24 * 60 * 60 ) );

				// Set standard HTTP/1.0 no-cache header.
				httpResponse.setHeader( "Pragma", "no-cache" );

				// debug
				//System.out.println( "!Cached: " + path );

				caching = 0;
			}
		}

	}

	class FilteredServletOutputStream extends ServletOutputStream {

		FilteredResponse httpResponse;

		ServletOutputStream out;

		public FilteredServletOutputStream(FilteredResponse httpResponse, ServletOutputStream out) {
			this.httpResponse  = httpResponse;
			this.out = out;
		}

		@Override
		public void write(int b) throws IOException {
			if ( httpResponse.caching != 0 ) {
			}
			out.write( b );
		}

		@Override
		public void write(byte[] b) throws IOException {
			if ( httpResponse.caching != 0 ) {
			}
			out.write( b );
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			if ( httpResponse.caching != 0 ) {
			}
			out.write( b, off, len );
		}

	}

}
