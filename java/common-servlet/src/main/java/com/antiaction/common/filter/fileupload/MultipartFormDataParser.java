/*
 * Created on 04/04/2013
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.filter.fileupload;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MultipartFormDataParser {

	public static final int S_BOUNDARY_MM = 0;
	public static final int S_BOUNDARY_DELIMITER = 1;
	public static final int S_BOUNDARY_NEXT_OR_END = 2;
	public static final int S_BOUNDARY_NEXT = 3;
	public static final int S_HEADER_START = 4;
	public static final int S_HEADER_COLON = 5;
	public static final int S_HEADER_HVALUE = 6;
	public static final int S_HEADER_SPC = 7;
	public static final int S_HEADER_SEMI = 8;
	public static final int S_PARAM_NAME = 9;
	public static final int S_PARAM_SPC = 10;
	public static final int S_PARAM_EQU = 11;
	public static final int S_PARAM_VALUE = 12;
	public static final int S_PARAM_VALUE_QUOTED = 13;
	public static final int S_PARAM_NEXT_OR_END = 14;
	public static final int S_PARAM_END = 15;
	public static final int S_PREAMBLE_MM = 16;
	public static final int S_CONTENT = 17;
	public static final int S_CONTENT_CRLFMM = 18;
	public static final int S_CONTENT_BOUNDARY = 19;
	public static final int S_BOUNDARY_END = 20;

	public static final int SM_NONE = 0;
	public static final int SM_PREAMBLE = 1;
	public static final int SM_FILE = 2;
	public static final int SM_POSTAMBLE = 3;

	public static final int H_UNKNOWN = 0;
	public static final int H_CONTENT_DISPOSITION = 1;
	public static final int H_CONTENT_TYPE = 2;
	public static final int H_CONTENT_TRANSFER_ENCODING = 3;

	public static final int BF_FIRST = 1;
	public static final int BF_FOLLOW = 2;
	public static final int BF_NAME = 4;
	public static final int BF_SPACE = 8;
	public static final int BF_SEMI = 16;
	public static final int BF_CR = 32;
	public static final int BF_PVALUE = 64;

	/** Bit field used to identify valid first/follow characters. */
	protected static int[] headerNameBF = new int[ 256 ];

	/*
	 * Initialize bit field.
	 */
	static {
		String alphas = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		for ( int i=0; i<alphas.length(); ++i ) {
			headerNameBF[ alphas.charAt( i ) ] |= BF_FIRST | BF_FOLLOW;
		}
		String digits = "1234567890";
		for ( int i=0; i<digits.length(); ++i ) {
			headerNameBF[ digits.charAt( i ) ] |= BF_FOLLOW;
		}
		String scheme = "-";
		for ( int i=0; i<scheme.length(); ++i ) {
			headerNameBF[ scheme.charAt( i ) ] |= BF_FOLLOW;
		}
		for ( int i=33; i<128; ++i ) {
			if ( i != ';' && i != '\"' && i != '\'') {
				headerNameBF[ i ] |= BF_NAME;
			}
		}
		headerNameBF[ ' ' ] |= BF_SPACE;
		headerNameBF[ ';' ] |= BF_SEMI;
		headerNameBF[ '\r' ] |= BF_CR;
		for ( int i=33; i<256; ++i ) {
			if ( i != ';' && i != '\"' && i != '\'') {
				headerNameBF[ i ] |= BF_PVALUE;
			}
		}
	}

	public static final boolean parseMultipartFormData(InputStream in, String multipartBoundary, String charsetName, int buffer_size, Map parameters, List files, File tmpdir) throws IOException {
		byte[] bytes = new byte[ buffer_size ];
		ByteBuffer byteBuffer = ByteBuffer.wrap( bytes );

		if ( charsetName == null ) {
			charsetName = System.getProperty( "file.encoding" );
		}

		byte[] MM = "--".getBytes();
		byte[] CRLF = "\r\n".getBytes();
		byte[] MMCRLF = "--\r\n".getBytes();
		byte[] CRLFCRLF = "\r\n\r\n".getBytes();
		byte[] CRLFMM = "\r\n--".getBytes();

		int state = S_BOUNDARY_MM;
		int mixed = 0;
		int aIdx = 0;
		int position = 0;
		int limit = 0;
		int c;

		int header = 0;

		StringBuilder hName = new StringBuilder();
		StringBuilder hValue = new StringBuilder();
		StringBuilder pName = new StringBuilder();
		ByteArrayOutputStream pValue = new ByteArrayOutputStream();
		String pValueStr;

		String tmpStr;

		String contentDisposition = null;
		String contentName = null;
		String contentFilename = null;
		String contentType = null;
		String contentBoundary = null;
		String contentCharset = null;
		String contentTransferEncoding = null;

		MultipartFormData mpfd =  null;

		byte[] boundary = multipartBoundary.getBytes();
		OutputStream out = null;

		boolean bValid = true;
		boolean bLoop = true;
		while ( bValid && bLoop ) {
			if ( byteBuffer.read( in ) != -1 ) {
				byteBuffer.flip();
				position = byteBuffer.position;
				limit = byteBuffer.limit;
				while ( bValid && position < limit ) {
					switch ( state ) {
					case S_BOUNDARY_MM:
						while ( bValid && aIdx < MM.length && position < limit ) {
							if ( MM[ aIdx++ ] != bytes[ position++ ] ) {
								bValid = false;
							}
						}
						if ( bValid && aIdx == MM.length ) {
							state = S_BOUNDARY_DELIMITER;
							aIdx = 0;
						}
						break;
					case S_BOUNDARY_DELIMITER:
						while ( bValid && aIdx < boundary.length && position < limit ) {
							if ( boundary[ aIdx++ ] != bytes[ position++ ] ) {
								bValid = false;
							}
						}
						if ( bValid && aIdx == boundary.length ) {
							state = S_BOUNDARY_NEXT_OR_END;
							aIdx = 0;
						}
						break;
					case S_BOUNDARY_NEXT_OR_END:
						c = bytes[ position++ ] & 255;
						if ( c == '\r') {
							state = S_BOUNDARY_NEXT;
							aIdx = 1;
							contentDisposition = null;
							contentName = null;
							contentFilename = null;
							contentType = null;
							contentBoundary = null;
							contentCharset = null;
							contentTransferEncoding = null;
						}
						else if ( c == '-' ) {
							state = S_BOUNDARY_END;
							aIdx = 1;
						}
						else {
							bValid = false;
						}
						break;
					case S_BOUNDARY_NEXT:
						while ( bValid && aIdx < CRLF.length && position < limit ) {
							if ( CRLF[ aIdx++ ] != bytes[ position++ ] ) {
								bValid = false;
							}
						}
						if ( bValid && aIdx == CRLF.length ) {
							state = S_HEADER_START;
							aIdx = 0;
						}
						break;
					case S_HEADER_START:
						if ( aIdx == 0 ) {
							c = bytes[ position++ ] & 255;
							if ( (headerNameBF[ c ] & BF_FIRST) == BF_FIRST ) {
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
							if ( (headerNameBF[ c ] & BF_FOLLOW) == BF_FOLLOW ) {
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
							switch ( headerNameBF[ c ] & (BF_NAME | BF_SPACE | BF_SEMI | BF_CR) ) {
							case BF_NAME:
								hValue.append( (char)c );
								break;
							case BF_SPACE:
								state = S_HEADER_SPC;
								break;
							case BF_SEMI:
								state = S_HEADER_SEMI;
								break;
							case BF_CR:
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
							if ( (headerNameBF[ c ] & BF_FIRST) == BF_FIRST ) {
								pName.setLength( 0 );
								pName.append( (char)c );
								pValue.reset();
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
							if ( (headerNameBF[ c ] & BF_FOLLOW) == BF_FOLLOW ) {
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
							if ( (headerNameBF[ c ] & BF_NAME) == BF_NAME) {
								pValue.write( c );
								state = S_PARAM_VALUE;
							}
							else if ( c == '\"' ) {
								state = S_PARAM_VALUE_QUOTED;
							}
							else if ( c != ' ' ) {
								bValid = false;
							}
						}
						break;
					case S_PARAM_VALUE:
						while ( bValid && position < limit && state == S_PARAM_VALUE ) {
							c = bytes[ position ] & 255;
							if ( (headerNameBF[ c ] & BF_NAME) == BF_NAME) {
								pValue.write( c );
								++position;
							}
							else {
								pValueStr = pValue.toString( charsetName );
								// debug
								System.out.println( pValueStr );
								tmpStr = pName.toString();
								switch ( header ) {
								case H_CONTENT_DISPOSITION:
									if ( "name".equalsIgnoreCase( tmpStr ) ) {
										contentName = pValueStr;
									}
									else if ( "filename".equalsIgnoreCase( tmpStr ) ) {
										contentFilename = pValueStr;
									}
									break;
								case H_CONTENT_TYPE:
									if ( "boundary".equalsIgnoreCase( tmpStr ) ) {
										contentBoundary = pValueStr;
									}
									else if ( "charset".equalsIgnoreCase( tmpStr ) ) {
										contentCharset = pValueStr;
									}
									break;
								}
								state = S_PARAM_NEXT_OR_END;
							}
						}
						break;
					case S_PARAM_VALUE_QUOTED:
						while ( bValid && position < limit && state == S_PARAM_VALUE_QUOTED ) {
							c = bytes[ position++ ] & 255;
							if ( (headerNameBF[ c ] & BF_NAME) == BF_NAME ) {
								pValue.write( c );
							}
							else if ( c == '\"' ) {
								pValueStr = pValue.toString( charsetName );
								// debug
								System.out.println( pValueStr );
								tmpStr = pName.toString();
								switch ( header ) {
								case H_CONTENT_DISPOSITION:
									if ( "name".equalsIgnoreCase( tmpStr ) ) {
										contentName = pValueStr;
									}
									else if ( "filename".equalsIgnoreCase( tmpStr ) ) {
										contentFilename = pValueStr;
									}
									break;
								case H_CONTENT_TYPE:
									if ( "boundary".equalsIgnoreCase( tmpStr ) ) {
										contentBoundary = pValueStr;
									}
									else if ( "charset".equalsIgnoreCase( tmpStr ) ) {
										contentCharset = pValueStr;
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
						if ( !bValid ) {
							if ( aIdx == 2 ) {
								state = S_HEADER_START;
								aIdx = 0;
								bValid = true;
							}
						}
						else if ( aIdx == 4 ) {
							switch ( mixed ) {
							case SM_NONE:
								if ( "form-data".equalsIgnoreCase( contentDisposition ) ) {
									mpfd =  new MultipartFormData();
									mpfd.contentDisposition = contentDisposition;
									mpfd.contentName = contentName;
									if ( contentFilename != null ) {
										if ( contentType != null ) {
											MultipartFormDataFile mpfdf = MultipartFormDataFile.getInstance( tmpdir );
											mpfdf.contentFilename = contentFilename;
											mpfdf.contentType = contentType;
											mpfdf.contentBoundary = contentBoundary;
											mpfdf.contentCharset = contentCharset;
											mpfdf.contentTransferEncoding = contentTransferEncoding;
											mpfd.files = new LinkedList();
											mpfd.files.add( mpfdf );
											out = mpfdf.getOutputStream();
											state = S_CONTENT;
										}
										else {
											bValid = false;
										}
									}
									else if ( contentType != null ) {
										if ( "multipart/mixed".equalsIgnoreCase( contentType ) ) {
											// debug
											System.out.println( contentType );
											mpfd.files = new LinkedList();
											out = new NullOutputStream();
											boundary = contentBoundary.getBytes();
											state = S_CONTENT;
											mixed = SM_PREAMBLE;
										}
										else {
											bValid = false;
										}
									}
									else {
										out = mpfd.getOutputStream();
										state = S_CONTENT;
									}
								}
								else {
									bValid = false;
								}
								aIdx = 0;
								break;
							case SM_FILE:
								if ( "file".equalsIgnoreCase( contentDisposition ) ) {
									if ( contentFilename != null && contentType != null ) {
										MultipartFormDataFile mpfdf = MultipartFormDataFile.getInstance( tmpdir );
										mpfdf.contentFilename = contentFilename;
										mpfdf.contentType = contentType;
										mpfdf.contentBoundary = contentBoundary;
										mpfdf.contentCharset = contentCharset;
										mpfdf.contentTransferEncoding = contentTransferEncoding;
										mpfd.files.add( mpfdf );
										out = mpfdf.getOutputStream();
										state = S_CONTENT;
									}
									else {
										bValid = false;
									}
								}
								else {
									bValid = false;
								}
								aIdx = 0;
								break;
							default:
								break;
							}
						}
						break;
					case S_PREAMBLE_MM:
						break;
					case S_CONTENT:
						while ( bValid && position < limit && state == S_CONTENT ) {
							c = bytes[ position++ ] & 255;
							if ( c != '\r' ) {
								out.write( c );
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
						if ( !bValid ) {
							out.write( CRLFMM, 0, aIdx );
							state = S_CONTENT;
							bValid = true;
						}
						else if ( aIdx == CRLFMM.length ) {
							state = S_CONTENT_BOUNDARY;
							aIdx = 0;
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
						if ( !bValid ) {
							out.write( CRLFMM );
							out.write( boundary, 0, aIdx );
							state = S_CONTENT;
							bValid = true;
						}
						else if ( aIdx == boundary.length ) {
							out.close();
							switch ( mixed ) {
							case SM_NONE:
								if ( mpfd.files == null ) {
									parameters.put( mpfd.contentName, mpfd.getValue( charsetName ) );
								}
								else {
									files.add( mpfd );
								}
								break;
							case SM_PREAMBLE:
								mixed = SM_FILE;
								break;
							}
							state = S_BOUNDARY_NEXT_OR_END;
							aIdx = 0;
						}
						break;
					case S_BOUNDARY_END:
						while ( bValid && aIdx < MMCRLF.length && position < limit ) {
							if ( MMCRLF[ aIdx++ ] != bytes[ position++ ] ) {
								bValid = false;
							}
						}
						if ( bValid && aIdx == MMCRLF.length ) {
							bLoop = false;
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
		if ( state != S_BOUNDARY_END ) {
			bValid = false;
		}
		return bValid;
	}

	public static class NullOutputStream extends OutputStream {

		/* (non-Javadoc)
		 * @see java.io.OutputStream#write(byte[])
		 */
		@Override
		public void write(byte[] b) throws IOException {
		}

		/* (non-Javadoc)
		 * @see java.io.OutputStream#write(byte[], int, int)
		 */
		@Override
		public void write(byte[] b, int off, int len) throws IOException {
		}

		@Override
		public void write(int b) throws IOException {
		}
	}

}
