/*
 * Created on 11/07/2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.filter.fileupload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.antiaction.common.strings.Strings;

public class MultipartFormFilter implements Filter {

	/** FilterConfig used to obtaint ServletContext. */
	private FilterConfig filterConfig;

	/** Context temp dir. */
	private File tmpdir;

	private static int counter = 1;

	public final void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
		this.filterConfig.getServletContext().getAttribute( "javax.servlet.context.tempdir" );
	}

	public final void destroy() {
	}

	public final void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)request;
		//HttpServletResponse resp = (HttpServletResponse)response;
		//ServletContext servletContext = filterConfig.getServletContext();

		List files = null;

		if ( "POST".equals(req.getMethod()) || "PUT".equals(req.getMethod()) )  {
			/*
			 * Content-Type.
			 * Content-Type: text/html; charset=iso-8859-1
			 * Content-Type: application/x-www-form-urlencoded
			 * Content-Type: multipart/form-data; boundary=---------------------------7d2291115040c
			 */

			String tmpStr;
			int idx;
			List tmpArr;
			String attStr;
			String valStr;

			String contentTypeFull;
			String contentType = null;
			String contentBoundary = null;
			String contentCharset = null;
			//int contentLength;
			//Charset setCharset = null;

			contentTypeFull = req.getHeader( "content-type" );
			if ( ( contentTypeFull != null ) && ( contentTypeFull.length() > 0 ) ) {
				tmpArr = Strings.splitString( contentTypeFull, ";" );
				if ( ( tmpArr != null ) && ( tmpArr.size() > 0 ) ) {
					contentType = ((String)tmpArr.get( 0 )).trim().toLowerCase();
					for ( int i=1; i<tmpArr.size(); ++i ) {
						tmpStr = (String)tmpArr.get( i );
						idx = tmpStr.indexOf( '=' );
						if ( idx != -1 ) {
							attStr = tmpStr.substring( 0, idx ).trim().toLowerCase();
							valStr = tmpStr.substring( idx + 1, tmpStr.length() ).trim();
							if ( attStr.equals( "boundary" ) ) {
								contentBoundary = valStr;
							}
							else if ( attStr.equals( "charset" ) ) {
								contentCharset = valStr;
							}
						}
					}
				}
			}

			// debug
			System.out.println( "Upload filter..." );
			System.out.println( " " + contentTypeFull );
			System.out.println( " " + contentType );
			System.out.println( " " + contentBoundary );

			/*
			 * Filter.
			 */

			if ( ( contentType != null ) && ( contentType.startsWith( "multipart/form-data" ) ) && contentBoundary != null && contentBoundary.length() > 0 ) {

				/*
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				byte[] bytes = new byte[ 1024 ];
				int read;
				InputStream in = req.getInputStream();
				while ( (read = in.read(bytes)) != -1 ) {
					out.write( bytes, 0, read );
				}
				out.close();

				RandomAccessFile raf = new RandomAccessFile( "form." + counter, "rw" );
				raf.write( out.toByteArray() );
				raf.close();

				++counter;
				*/

				Map parameters = new HashMap();
				files = new ArrayList();
				/*
				if ( MultipartFormDataParserOld.parseMultipartFormData( req, contentBoundary, parameters, files, tmpdir ) ) {
					request = new FilteredRequest( req, parameters );
				}
				*/
				InputStream in = req.getInputStream();
				MultipartFormData formdata;
				if ( MultipartFormDataParser.parseMultipartFormData( in, contentBoundary, parameters, files, tmpdir ) ) {
					// debug
					System.out.println( files.size() );
					for ( int i=0; i<files.size(); ++i ) {
						formdata = (MultipartFormData)files.get( i );
						req.setAttribute( formdata.contentName, formdata );
					}
					request = new FilteredRequest( req, parameters );
				}
			}
		}

		try {
			chain.doFilter( request, response );
		}
		finally {
			if ( files != null ) {
				MultipartFormData formdata;
				List formfiles;
				MultipartFormDataFile formfile;
				for ( int i=0; i<files.size(); ++i ) {
					formdata = (MultipartFormData)files.get( i );
					formfiles = formdata.files;
					for ( int j=0; j<formfiles.size(); ++j ) {
						formfile = (MultipartFormDataFile)formfiles.get( j );
						System.out.println( formfile.file.getAbsolutePath() );
						/*
						if ( !formfile.isClaimed() ) {
							formfile.file.delete();
						}
						*/
					}
				}
			}
		}
	}

	private final class FilteredRequest extends HttpServletRequestWrapper {

		/** Wrapped HttpServletRequest. */
		//private HttpServletRequest request;
		/** Parameters. */
		private Map parameters = new HashMap();
		/** Has parameters ArrayList Map been converted to String[] Map. */
		private boolean arrayedParameters = false;
		/** Parameters String[] Map. */
		private Map paramStringArrayMap = new HashMap();

		public FilteredRequest(HttpServletRequest request, Map parameters) {
			super( request );
			//this.request = request;
			this.parameters = parameters;

			String key;
			String[] valuesArr;
			List valuesList;
			Enumeration enumerator = request.getParameterNames();
			while ( enumerator.hasMoreElements() ) {
				key = (String)enumerator.nextElement();
				valuesArr = request.getParameterValues( key );
				valuesList = (ArrayList)parameters.get( key );
				if ( valuesList == null ) {
					valuesList = new ArrayList();
					parameters.put( key, valuesList );
				}
				for ( int i=0; i<valuesArr.length; ++i ) {
					valuesList.add( valuesArr[ i ] );
				}
			}
		}

		public String getParameter(String name) {
			ArrayList tmpArr = (ArrayList)parameters.get( name );
			if ( ( tmpArr != null ) && ( !tmpArr.isEmpty() ) ) {
				return (String)tmpArr.get( 0 );
			}
			return null;
		}

		public Enumeration getParameterNames() {
			return new Enumeration() {
				Iterator iter = parameters.keySet().iterator();

				public boolean hasMoreElements() {
					return iter.hasNext();
				}

				// throws NoSuchElementException
				public Object nextElement() {
					return iter.next();
				}
			};
		}

		public String[] getParameterValues(String name) {
			String[] retArr;
			ArrayList tmpArr = (ArrayList)parameters.get( name );
			if ( tmpArr == null ) {
				tmpArr = new ArrayList();
			}
			retArr = new String[ tmpArr.size() ];
			for ( int i=0; i<tmpArr.size(); ++i ) {
				retArr[ i ] = (String)tmpArr.get( i );
			}
			return retArr;
		}

		private void arrayParameters() {
			paramStringArrayMap.clear();
			String tmpStr;
			Iterator iter = parameters.keySet().iterator();
			while ( iter.hasNext() ) {
				tmpStr = (String)iter.next();
				paramStringArrayMap.put( tmpStr, getParameterValues( tmpStr ) );
			}
			arrayedParameters = true;
		}

		public Map getParameterMap() {
			if ( !arrayedParameters ) {
				arrayParameters();
			}
			return Collections.unmodifiableMap( paramStringArrayMap );
		}

	}

}
