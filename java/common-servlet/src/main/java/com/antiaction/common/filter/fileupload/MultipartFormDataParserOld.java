/*
 * Created on 09/04/2013
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.filter.fileupload;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import com.antiaction.common.strings.Strings;

public class MultipartFormDataParserOld {

	public static boolean parseMultipartFormData(HttpServletRequest req, String contentBoundary, Map parameters, List files, File tmpdir) throws IOException {
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
			e.printStackTrace();
			return false;
		}

		return true;
	}

	private static String trimPling(String str) {
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
	private static String formFilename(String dispFilename, String contentName) {
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
