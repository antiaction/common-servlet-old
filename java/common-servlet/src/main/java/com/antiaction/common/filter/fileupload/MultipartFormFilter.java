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
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.antiaction.common.servlet.StringUtils;

public class MultipartFormFilter implements Filter {

    /** Logging mechanism. */
	private static Logger logger = Logger.getLogger( MultipartFormFilter.class.getName() );

	private static String FORM_FILE_POSTFIX = "form-file-";

	/** FilterConfig used to obtait ServletContext. */
	private FilterConfig filterConfig;

	/** Context temp dir. */
	private File tmpdir;

	/** Debug form data by saving the whole submitted form. */
	private boolean bDebug = false;

	/** Current number of saved debug forms. */
	private static Integer counter = 1;

	public final void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
		String tmpdirStr = this.filterConfig.getInitParameter( "tmpdir" );
		if ( tmpdirStr != null && tmpdirStr.length() > 0 ) {
			tmpdir = new File( tmpdirStr );
			check_tmpdir();
		}
		if ( tmpdir == null ) {
			tmpdir = (File)this.filterConfig.getServletContext().getAttribute( "javax.servlet.context.tempdir" );
			if ( tmpdir != null ) {
				check_tmpdir();
			}
		}
		if ( tmpdir == null ) {
			tmpdirStr = System.getProperty( "java.io.tmpdir" );
			if ( tmpdirStr != null && tmpdirStr.length() > 0 ) {
				tmpdir = new File( tmpdirStr );
				check_tmpdir();
			}
		}
		String debugStr = this.filterConfig.getInitParameter( "debug" );
		if ( debugStr != null && debugStr.length() > 0 ) {
			if ( "true".equalsIgnoreCase( debugStr ) || "1".equals( debugStr ) ) {
				bDebug = true;
			}
		}
		logger.log( Level.INFO, "tmpdir: " + tmpdir.getPath() );
		logger.log( Level.INFO, "debug: " + bDebug );
	}

	private void check_tmpdir() {
		if ( !tmpdir.exists() ) {
			if ( !tmpdir.mkdirs() ) {
				tmpdir = null;
			}
		}
		else if ( !tmpdir.isDirectory() ) {
			tmpdir = null;
		}
	}

	public final void destroy() {
	}

	/**
	 * Closes inputstream before method returns.
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public RandomAccessFile debugInputStream(InputStream in) throws IOException {
		RandomAccessFile raf = null;
		try {
			int count;
			synchronized ( counter ) {
				count = counter++;
			}
			raf = new RandomAccessFile( new File(tmpdir, FORM_FILE_POSTFIX + count), "rw" );
			raf.seek( 0 );
			raf.setLength( 0 );
			byte[] bytes = new byte[ 8192 ];
			int read;
			while ( (read = in.read(bytes)) != -1 ) {
				raf.write( bytes, 0, read );
			}
			raf.seek( 0 );
		}
		finally {
			in.close();
		}
		return raf;
	}

	public final void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)request;
		//HttpServletResponse resp = (HttpServletResponse)response;
		//ServletContext servletContext = filterConfig.getServletContext();

		InputStream in = null;
		RandomAccessFile raf = null;
		List files = null;

		if ( "POST".equals( req.getMethod() ) )  {
			try {
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
					tmpArr = StringUtils.splitString( contentTypeFull, ";" );
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

				logger.log( Level.INFO, req.getMethod() + " - " + contentTypeFull );

				/*
				 * Filter.
				 */

				if ( contentType != null ) {
					if ( contentType.startsWith( "application/x-www-form-urlencoded" ) ) {
						in = req.getInputStream();
						if ( bDebug ) {
							raf = debugInputStream( in );
							in = new RandomAccessFileInputStream( raf );
						}
						String charsetName = req.getCharacterEncoding();
						Map parameters = new HashMap();
						if ( UrlEncodedFormDataParser.parseUrlEncodedFormData( in, charsetName, 8192, parameters ) ) {
							request = new FilteredRequest( req, parameters );
						}
						else {
							logger.log( Level.SEVERE, "Failed to parse application/x-www-form-urlencoded!" );
						}
					}
					else if ( contentType.startsWith( "multipart/form-data" ) && contentBoundary != null && contentBoundary.length() > 0 ) {
						in = req.getInputStream();
						if ( bDebug ) {
							raf = debugInputStream( in );
							in = new RandomAccessFileInputStream( raf );
						}
						String charsetName = req.getCharacterEncoding();
						Map parameters = new HashMap();
						files = new ArrayList();
						MultipartFormData formdata;
						if ( MultipartFormDataParser.parseMultipartFormData( in, contentBoundary, charsetName, 8192, parameters, files, tmpdir ) ) {
							// debug
							System.out.println( files.size() );
							for ( int i=0; i<files.size(); ++i ) {
								formdata = (MultipartFormData)files.get( i );
								req.setAttribute( formdata.contentName, formdata );
							}
							request = new FilteredRequest( req, parameters );
						}
						else {
							logger.log( Level.SEVERE, "Failed to parse multipart/form-data!" );
						}
					}
				}
			}
			catch (Throwable t) {
				logger.log( Level.SEVERE, t.toString(), t );
			}
			finally {
				if ( in != null ) {
					try {
						in.close();
					}
					catch (IOException e) {
						logger.log( Level.SEVERE, e.toString(), e );
					}
					in = null;
				}
			}
		}

		try {
			chain.doFilter( request, response );
		}
		finally {
			if ( raf != null ) {
				try {
					raf.close();
				}
				catch (IOException e) {
					logger.log( Level.SEVERE, e.toString(), e );
				}
				raf = null;
			}
			if ( !bDebug && files != null ) {
				MultipartFormData formdata;
				List formfiles;
				MultipartFormDataFile formfile;
				for ( int i=0; i<files.size(); ++i ) {
					formdata = (MultipartFormData)files.get( i );
					formfiles = formdata.files;
					for ( int j=0; j<formfiles.size(); ++j ) {
						formfile = (MultipartFormDataFile)formfiles.get( j );
						if ( !formfile.isClaimed() ) {
							formfile.delete();
						}
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
