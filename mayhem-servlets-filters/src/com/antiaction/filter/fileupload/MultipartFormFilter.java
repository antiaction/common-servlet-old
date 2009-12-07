/*
 * Created on 11/07/2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.filter.fileupload;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
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

		List files = null;
		if ( ( contentType != null ) && ( contentType.startsWith( "multipart/form-data" ) ) && contentBoundary != null && contentBoundary.length() > 0 ) {
			Map parameters = new HashMap();
			files = new ArrayList();
			if ( parseMultipartFormData( req, contentBoundary, parameters, files ) ) {
				request = new FilteredRequest( req, parameters );
			}
		}
		try {
			chain.doFilter( request, response );
		}
		finally {
			if ( files != null ) {
				MultipartFormFile formfile;
				for ( int i=0; i<files.size(); ++i ) {
					formfile = (MultipartFormFile)files.get( i );
					System.out.println( formfile.getAbsolutePath() );
					if ( !formfile.isClaimed() ) {
						formfile.delete();
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

	public final boolean parseMultipartFormData(HttpServletRequest req, String contentBoundary, Map parameters, List files) throws IOException {
		byte[] arrReadLine = new byte[ 4096 ];
		Random random = new Random();

		ServletInputStream socketIn = req.getInputStream();
		int len = req.getContentLength();
		boolean b = true;
		int c;
		int idx;

		String parStr = null;
		String valStr = null;
		List parArr;

		/*
		 * multipart/form-data
		 */

		if ( ( contentBoundary == null ) || ( contentBoundary.length() == 0 ) ) {
			return false;
		}

		int readLen;
		String tmpStr;
		int cLen = contentBoundary.length();
		int maxLen;
		boolean bh;
		boolean bc;
		boolean bne;
		String headerStr;
		String valueStr;
		List tmpArr;
		String attStr;
		//String valStr;

		IMultipartFormData formdata;

		String contentDisp;
		String dispName;
		String dispFilename;
		String contentType;
		String contentName;

		if ( cLen + 2 > arrReadLine.length ) {
			return false;
		}

		try {
			if ( len > 0 ) {
				c = socketIn.read();
				--len;
				if ( ( c == '-' ) && ( len > 0 ) ) {
					c = socketIn.read();
					--len;
					if ( ( c == '-' ) && ( len >= cLen + 2 ) ) {
						readLen = socketIn.readLine( arrReadLine, 0, cLen + 2 );
						len -= readLen;
						if ( readLen != ( cLen + 2 ) ) {
							return false;
						}
						tmpStr = new String( arrReadLine, 0, cLen );

						if ( !tmpStr.equals( contentBoundary ) ) {
							return false;
						}

						if ( ( arrReadLine[ cLen ] == '-' ) && ( arrReadLine[ cLen + 1 ] == '-' ) ) {
							b = false;
						}
						else if ( ( arrReadLine[ cLen ] != 13 ) || ( arrReadLine[ cLen + 1 ] != 10 ) ) {
							return false;
						}

						while ( b ) {
							contentDisp = null;
							dispName = null;
							dispFilename = null;
							contentType = null;
							contentName = null;

							/*
							 * Header
							 */
							bh = true;
							while ( bh ) {
								maxLen = len;
								if ( len > arrReadLine.length ) {
									maxLen = arrReadLine.length;
								}
								readLen = socketIn.readLine( arrReadLine, 0, maxLen );
								len -= readLen;

								if ( readLen >= 2 ) {
									if ( ( arrReadLine[ readLen - 2 ] != 13 ) || ( arrReadLine[ readLen - 1 ] != 10 ) ) {
										return false;
									}
									tmpStr = new String( arrReadLine, 0, readLen - 2 );

									if ( tmpStr.length() != 0 ) {
										/*
										 * Content-Disposition: form-data; name="name_of_files"; filename="url-domstol.txt"
										 * Content-Type: text/plain; name="url-domstol.txt"
										 */
										idx = tmpStr.indexOf( ':' );
										if ( idx != -1 ) {
											headerStr = tmpStr.substring( 0, idx ).trim().toLowerCase();		// check: trim?
											valueStr = tmpStr.substring( idx + 1, tmpStr.length() ).trim();
											tmpArr = Strings.splitString( valueStr, ";" );
											if ( ( tmpArr != null ) && ( !tmpArr.isEmpty() ) ) {
												/*
												 * Content-Disposition
												 */
												if ( headerStr.equals( "content-disposition" ) ) {
													contentDisp = ((String)tmpArr.get( 0 )).trim().toLowerCase();
													if ( !contentDisp.equals( "form-data" ) ) {
														return false;
													}
													for ( int i=1; i<tmpArr.size(); ++i ) {
														tmpStr = (String)tmpArr.get( i );
														idx = tmpStr.indexOf( '=' );
														if ( idx != -1 ) {
															attStr = tmpStr.substring( 0, idx ).trim().toLowerCase();
															valStr = tmpStr.substring( idx + 1, tmpStr.length() ).trim();
															if ( attStr.equals( "name" ) ) {
																dispName = trimPling( valStr );
															}
															else if ( attStr.equals( "filename" ) ) {
																dispFilename = trimPling( valStr );
															}
														}
														else {
															return false;
														}
													}
												}
												/*
												 * Content-Type
												 */
												else if ( headerStr.equals( "content-type" ) ) {
													contentType = (String)tmpArr.get( 0 );
													for ( int i=1; i<tmpArr.size(); ++i ) {
														tmpStr = (String)tmpArr.get( i );
														idx = tmpStr.indexOf( '=' );
														if ( idx != -1 ) {
															attStr = tmpStr.substring( 0, idx ).trim().toLowerCase();
															valStr = tmpStr.substring( idx + 1, tmpStr.length() ).trim();
															if ( attStr.equals( "name" ) ) {
																contentName = trimPling( valStr );
															}
														}
														else {
															return false;
														}
													}
												}
											}
											else {
												return false;
											}
										}
										else {
											return false;				// moron header
										}
									}
									else {
										bh = false;
									}
								}
								else {
									return false;
								}
							}
							/*
							 * Content
							 */
							if ( ( dispFilename != null ) && ( dispFilename.length() > 0 ) ) {
								/*
								 * File
								 */
								String ctm;
								String rand;
								String filename;

								ctm = "000000000000000" + Long.toHexString( System.currentTimeMillis() );
								ctm = ctm.substring( ctm.length() - 16, ctm.length() );
								rand = "000000000000000" + Long.toHexString( random.nextLong() );
								rand = rand.substring( rand.length() - 16, rand.length() );
								filename = "temp-formdata-" + ctm + "-" + rand;

								formdata = new MultipartFormFile( tmpdir, filename );

								contentName = formFilename( dispFilename, contentName );

								//dreq.files.put( contentName, (DispatchFormDataFile)formdata );

								((MultipartFormFile)formdata).setDispositionFilename( dispFilename );
								((MultipartFormFile)formdata).setContentType( contentType );
								((MultipartFormFile)formdata).setContentName( contentName );

								files.add( formdata );
							}
							else {
								/*
								 * Form Data
								 */
								formdata = new MultipartFormData();
							}
							bc = true;
							while ( bc ) {
								bne = true;
								while ( bne ) {
									/*
									 * Value
									 */
									if ( len > 0 ) {
										c = socketIn.read();
										--len;
										if ( c == '\r' ) {
											bne = false;
										}
										else {
											formdata.write( c );
										}
									}
									else {
										return false;
									}
								}
								/*
								 * \n--
								 */
								if ( len >= cLen + 5 ) {
									socketIn.mark( cLen + 5 );
									c = socketIn.read();
									if ( c == '\n' ) {
										c = socketIn.read();
										if ( c == '-' ) {
											c = socketIn.read();
											if ( c == '-' ) {
												/*
												 * Boundary
												 */
												readLen = socketIn.readLine( arrReadLine, 0, cLen + 2 );
												if ( readLen == ( cLen + 2 ) ) {
													tmpStr = new String( arrReadLine, 0, cLen );
													if ( tmpStr.equals( contentBoundary ) ) {
														if ( ( arrReadLine[ cLen ] == '-' ) && ( arrReadLine[ cLen + 1 ] == '-' ) ) {
															bc = false;
															b = false;
															len -= cLen + 5;
														}
														else if ( ( arrReadLine[ cLen ] == 13 ) || ( arrReadLine[ cLen + 1 ] == 10 ) ) {
															bc = false;
															len -= cLen + 5;
														}
													}
												}
											}
										}

									}
									if ( bc ) {
										formdata.write( '\r' );
										socketIn.reset();
									}
								}
								else {
									return false;
								}
							}
							formdata.close();

							parStr = dispName;

							// debug
							//System.out.println( "contentDisp: " + contentDisp );
							//System.out.println( "dispName: " + dispName );
							//System.out.println( "dispFilename: " + dispFilename );
							//System.out.println( "contentType: " + contentType );
							//System.out.println( "contentName: " + contentName );

							if ( ( dispFilename != null ) && ( dispFilename.length() > 0 ) ) {
								/*
								 * File
								 */
								if ( ( parStr != null ) && ( parStr.length() > 0 ) ) {
									parArr = (ArrayList)parameters.get( parStr );
									if ( parArr == null ) {
										parArr = new ArrayList();
										parameters.put( parStr, parArr );
									}
									parArr.add( formdata );
								}
							}
							else {
								/*
								 * Form Data
								 */
								valStr = formdata.toString();

								// debug
								//System.out.println( parStr );
								//System.out.println( valStr );

								if ( ( parStr != null ) && ( parStr.length() > 0 ) ) {
									parArr = (ArrayList)parameters.get( parStr );
									if ( parArr == null ) {
										parArr = new ArrayList();
										parameters.put( parStr, parArr );
									}
									parArr.add( valStr );
								}
							}
						}
						/*
						 * Skip fluff
						 */
						socketIn.skip( len );
					}
					else {
						return false;
					}
				}
				else {
					return false;
				}
			}
			else {
				return false;
			}
		}
		catch (IOException e) {
			System.out.println( e );
			return false;
		}

		return true;
	}

	private final String trimPling(String str) {
		if ( str == null ) {
			return null;
		}
		else {
			if ( str.length() < 2 ) {
				return str;
			}
			if ( ( str.charAt( 0 ) == '"' ) && ( str.charAt( str.length() - 1 ) == '"' ) ) {
				return str.substring( 1, str.length() - 1 );
			}
			else {
				return str;
			}
		}
	}

	/*
	 * Remove abs path, way to go MSIE.. morons!
	 */
	private String formFilename(String dispFilename, String contentName) {
		int idx;
		if ( contentName == null ) {
			contentName = dispFilename;
		}
		if ( ( contentName.length() >= 3 ) && ( contentName.charAt( 1 ) == ':' ) && ( contentName.charAt( 2 ) == '\\' ) ) {
			idx = contentName.lastIndexOf( '\\' );
			if ( idx != -1 ) {
				contentName = contentName.substring( idx + 1, contentName.length() );
			}
		}
		else {
			contentName = contentName.replace( '\\', '/' );
			idx = contentName.lastIndexOf( '/' );
			if ( idx != -1 ) {
				contentName = contentName.substring( idx + 1, contentName.length() );
			}
		}
		return contentName;
	}

}
