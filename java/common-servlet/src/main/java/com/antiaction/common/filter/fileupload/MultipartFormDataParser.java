/*
 * Created on 04/04/2013
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.filter.fileupload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class MultipartFormDataParser {

	public static final int S_HEADER_MM = 0;
	public static final int S_HEADER_BOUNDARY = 1;
	public static final int S_HEADER_NEXT_OR_END = 2;
	public static final int S_HEADER_NEXT = 3;
	public static final int S_HEADER_END = 4;
	public static final int S_HEADER_START = 5;
	public static final int S_HEADER_COLON = 6;
	public static final int S_HEADER_HVALUE = 7;
	public static final int S_HEADER_SPC = 8;
	public static final int S_HEADER_SEMI = 9;
	public static final int S_PARAM_NAME = 10;
	public static final int S_PARAM_SPC = 11;
	public static final int S_PARAM_EQU = 12;
	public static final int S_PARAM_VALUE = 13;
	public static final int S_PARAM_NEXT_OR_END = 14;
	public static final int S_PARAM_END = 15;
	public static final int S_CONTENT = 16;
	public static final int S_CONTENT_CRLFMM = 17;
	public static final int S_CONTENT_BOUNDARY = 18;

	public static final int H_UNKNOWN = 0;
	public static final int H_CONTENT_DISPOSITION = 1;
	public static final int H_CONTENT_TYPE = 2;
	public static final int H_CONTENT_TRANSFER_ENCODING = 3;

	/** Bit field used to identify valid first/follow characters. */
	protected static int[] headerNameBF = new int[256];

	/*
	 * Initialize bit field.
	 */
	static {
		String alphas = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		for ( int i=0; i<alphas.length(); ++i ) {
			headerNameBF[ alphas.charAt( i ) ] |= 3;
		}
		String digits = "1234567890";
		for ( int i=0; i<digits.length(); ++i ) {
			headerNameBF[ digits.charAt( i ) ] |= 2;
		}
		String scheme = "-";
		for ( int i=0; i<scheme.length(); ++i ) {
			headerNameBF[ scheme.charAt( i ) ] |= 2;
		}
		for ( int i=33; i<128; ++i ) {
			if ( i != ';' && i != '\"' && i != '\'') {
				headerNameBF[ i ] |= 4;
			}
		}
		headerNameBF[ ' ' ] |= 8;
		headerNameBF[ ';' ] |= 16;
		headerNameBF[ '\r' ] |= 32;
	}

	public static final boolean parseMultipartFormData(InputStream in, String multipartBoundary, Map parameters, List files, File tmpdir) throws IOException {
		byte[] bytes = new byte[ 8192 ];
		ByteBuffer byteBuffer = ByteBuffer.wrap( bytes );

		byte[] MM = "--".getBytes();
		byte[] CRLF = "\r\n".getBytes();
		byte[] MMCRLF = "--\r\n".getBytes();
		byte[] CRLFCRLF = "\r\n\r\n".getBytes();
		byte[] CRLFMM = "\r\n--".getBytes();

		int state = S_HEADER_MM;
		int aIdx = 0;
		int position = 0;
		int limit = 0;

		int header = 0;

		StringBuilder hName = new StringBuilder();
		StringBuilder hValue = new StringBuilder();
		StringBuilder pName = new StringBuilder();
		StringBuilder pValue = new StringBuilder();

		String tmpStr;

		String contentDisposition = null;
		String contentName = null;
		String contentFilename = null;
		String contentType = null;
		String contentBoundary = null;
		String contentCharset = null;
		String contentTransferEncoding = null;

		byte[] boundary = multipartBoundary.getBytes();

		boolean bValid = true;
		boolean bLoop = true;
		while ( bValid && bLoop ) {
			if ( byteBuffer.read( in ) != -1 ) {
				byteBuffer.flip();
				position = byteBuffer.position;
				limit = byteBuffer.limit;
				while ( bValid && position < limit ) {
					switch ( state ) {
					case S_HEADER_MM:
						while ( bValid && aIdx < MM.length && position < limit ) {
							if ( MM[ aIdx++ ] != bytes[ position++ ] ) {
								bValid = false;
							}
						}
						if ( bValid ) {
							state = S_HEADER_BOUNDARY;
							aIdx = 0;
						}
						break;
					case S_HEADER_BOUNDARY:
						while ( bValid && aIdx < boundary.length && position < limit ) {
							if ( boundary[ aIdx++ ] != bytes[ position++ ] ) {
								bValid = false;
							}
						}
						if ( bValid ) {
							state = S_HEADER_NEXT_OR_END;
							aIdx = 0;
						}
						break;
					case S_HEADER_NEXT_OR_END:
						int c = bytes[ position++ ] & 255;
						if ( c == '\r') {
							state = S_HEADER_NEXT;
							aIdx = 1;
						}
						else if ( c == '-' ) {
							state = S_HEADER_END;
							aIdx = 1;
						}
						else {
							bValid = false;
						}
						break;
					case S_HEADER_NEXT:
						while ( bValid && aIdx < CRLF.length && position < limit ) {
							if ( CRLF[ aIdx++ ] != bytes[ position++ ] ) {
								bValid = false;
							}
						}
						if ( bValid ) {
							state = S_HEADER_START;
							aIdx = 0;
						}
						break;
					case S_HEADER_END:
						while ( bValid && aIdx < MMCRLF.length && position < limit ) {
							if ( MMCRLF[ aIdx++ ] != bytes[ position++ ] ) {
								bValid = false;
							}
						}
						if ( bValid ) {
							bLoop = false;
						}
						break;
					case S_HEADER_START:
						if ( aIdx == 0 ) {
							c = bytes[ position++ ] & 255;
							if ( (headerNameBF[ c ] & 1) == 1 ) {
								hName.setLength( 0 );
								hName.append( (char)c );
								hValue.setLength( 0 );
								++aIdx;
							}
							else {
								bValid = false;
							}
						}
						while ( bValid && position < limit && state == S_HEADER_START ) {
							c = bytes[ position++ ] & 255;
							if ( (headerNameBF[ c ] & 2) == 2 ) {
								hName.append( (char)c );
								++aIdx;
							}
							else if ( c != ':' ) {
								bValid = false;
							}
							else {
								System.out.println( hName.toString() );
								state = S_HEADER_COLON;
							}
						}
						break;
					case S_HEADER_COLON:
						while ( bValid && position < limit && state == S_HEADER_COLON ) {
							c = bytes[ position ] & 255;
							if ( c == ' ' ) {
								++position;
							}
							else {
								state = S_HEADER_HVALUE;
							}
						}
						break;
					case S_HEADER_HVALUE:
						while ( bValid && position < limit && state == S_HEADER_HVALUE ) {
							c = bytes[ position++ ] & 255;
							switch ( headerNameBF[ c ] & (4 | 8 | 16 | 32) ) {
							case 4:
								hValue.append( (char)c );
								break;
							case 8:
								state = S_HEADER_SPC;
								break;
							case 16:
								state = S_HEADER_SEMI;
								break;
							case 32:
								state = S_PARAM_END;
								aIdx = 1;
								break;
							default:
								bValid = false;
								break;
							}
						}
						if ( state != S_HEADER_HVALUE ) {
							// debug
							System.out.println( hValue.toString() );
							tmpStr = hName.toString();
							if ( "Content-Disposition".equalsIgnoreCase( tmpStr ) ) {
								header = H_CONTENT_DISPOSITION;
								contentDisposition = hValue.toString();
							}
							else if ( "Content-Type".equalsIgnoreCase( tmpStr ) ) {
								header = H_CONTENT_TYPE;
								contentType = hValue.toString();
							}
							else if ( "Content-Transfer-Encoding".equalsIgnoreCase( tmpStr ) ) {
								header = H_CONTENT_TRANSFER_ENCODING;
								contentTransferEncoding = hValue.toString();
							}
							else {
								header = H_UNKNOWN;
							}
						}
						break;
					case S_HEADER_SPC:
						while ( bValid && position < limit && state == S_HEADER_SPC ) {
							c = bytes[ position++ ] & 255;
							if ( c == ';' ) {
								state = S_HEADER_SEMI;
							}
							else if ( c == '\r' ) {
								state = S_PARAM_END;
								aIdx = 1;
							}
							else if ( c != ' ' ) {
								bValid = false;
							}
						}
						break;
					case S_HEADER_SEMI:
						while ( bValid && position < limit && state == S_HEADER_SEMI ) {
							c = bytes[ position++ ] & 255;
							if ( (headerNameBF[ c ] & 1) == 1 ) {
								pName.setLength( 0 );
								pName.append( (char)c );
								pValue.setLength( 0 );
								state = S_PARAM_NAME;
							}
							else if ( c == '\r') {
								state = S_PARAM_END;
								aIdx = 1;
							}
							else if ( c != ' ' ) {
								bValid = false;
							}
						}
						break;
					case S_PARAM_NAME:
						while ( bValid && position < limit && state == S_PARAM_NAME ) {
							c = bytes[ position++ ] & 255;
							if ( (headerNameBF[ c ] & 2) == 2 ) {
								pName.append( (char)c );
							}
							else if ( c == '=' ) {
								// debug
								System.out.println( pName.toString() );
								state = S_PARAM_EQU;
							}
							else if ( c == ' ' ) {
								// debug
								System.out.println( pName.toString() );
								state = S_PARAM_SPC;
							}
							else {
								bValid = false;
							}
						}
						break;
					case S_PARAM_SPC:
						while ( bValid && position < limit && state == S_PARAM_SPC ) {
							c = bytes[ position++ ] & 255;
							if ( c == '=' ) {
								state = S_PARAM_EQU;
							}
							else if ( c != ' ' ) {
								bValid = false;
							}
						}
						break;
					case S_PARAM_EQU:
						while ( bValid && position < limit && state == S_PARAM_EQU ) {
							c = bytes[ position++ ] & 255;
							if ( c == '\"' ) {
								state = S_PARAM_VALUE;
							}
							else if ( c != ' ' ) {
								bValid = false;
							}
						}
						break;
					case S_PARAM_VALUE:
						while ( bValid && position < limit && state == S_PARAM_VALUE ) {
							c = bytes[ position++ ] & 255;
							if ( (headerNameBF[ c ] & 4) == 4 ) {
								pValue.append( (char)c );
							}
							else if ( c == '\"' ) {
								// debug
								System.out.println( pValue.toString() );
								tmpStr = pName.toString();
								switch ( header ) {
								case H_CONTENT_DISPOSITION:
									if ( "name".equalsIgnoreCase( tmpStr ) ) {
										contentName = pValue.toString();
									}
									else if ( "filename".equalsIgnoreCase( tmpStr ) ) {
										contentFilename = pValue.toString();
									}
									break;
								case H_CONTENT_TYPE:
									if ( "boundary".equalsIgnoreCase( tmpStr ) ) {
										contentBoundary = pValue.toString();
									}
									else if ( "charset".equalsIgnoreCase( tmpStr ) ) {
										contentCharset = pValue.toString();
									}
									break;
								}
								state = S_PARAM_NEXT_OR_END;
							}
							else {
								bValid = false;
							}
						}
						break;
					case S_PARAM_NEXT_OR_END:
						while ( bValid && position < limit && state == S_PARAM_NEXT_OR_END ) {
							c = bytes[ position++ ] & 255;
							if ( c == '\r' ) {
								state = S_PARAM_END;
								aIdx = 1;
							}
							else if ( c == ';' ) {
								state = S_HEADER_SEMI;
							}
							else if ( c != ' ' ) {
								bValid = false;
							}
						}
						break;
					case S_PARAM_END:
						while ( bValid && aIdx < CRLFCRLF.length && position < limit ) {
							if ( CRLFCRLF[ aIdx ] == bytes[ position ] ) {
								++aIdx;
								++position;
							}
							else {
								bValid = false;
							}
						}
						if ( aIdx == 2 ) {
							state = S_HEADER_START;
							aIdx = 0;
							bValid = true;
						}
						else if ( aIdx == 4 ) {
							if ( "form-data".equalsIgnoreCase( contentDisposition ) ) {
								if ( contentFilename != null && contentType != null ) {
								}
								else {
								}
							}
							else {
								bValid = false;
							}
							state = S_CONTENT;
							aIdx = 0;
						}
						break;
					case S_CONTENT:
						while ( bValid && position < limit && state == S_CONTENT ) {
							c = bytes[ position++ ] & 255;
							if ( c != '\r' ) {
								// TODO write
							}
							else {
								state = S_CONTENT_CRLFMM;
								aIdx = 1;
							}
						}
						break;
					case S_CONTENT_CRLFMM:
						while ( bValid && aIdx < CRLFMM.length && position < limit ) {
							if ( CRLFMM[ aIdx ] == bytes[ position ] ) {
								++aIdx;
								++position;
							}
							else {
								bValid = false;
							}
						}
						if ( bValid ) {
							state = S_CONTENT_BOUNDARY;
							aIdx = 0;
						}
						else {
							// TODO write
							state = S_CONTENT;
							bValid = true;
						}
						break;
					case S_CONTENT_BOUNDARY:
						while ( bValid && aIdx < boundary.length && position < limit ) {
							if ( boundary[ aIdx ] == bytes[ position ] ) {
								++aIdx;
								++position;
							}
							else {
								bValid = false;
							}
						}
						if ( bValid ) {
							state = S_HEADER_NEXT_OR_END;
							aIdx = 0;
						}
						else {
							// TODO write
							state = S_CONTENT;
							bValid = true;
						}
						break;
					}
				}
				byteBuffer.position( position );
				byteBuffer.compact();
			}
			else {
				bLoop = false;
			}
		}
		return bValid;
	}

}
