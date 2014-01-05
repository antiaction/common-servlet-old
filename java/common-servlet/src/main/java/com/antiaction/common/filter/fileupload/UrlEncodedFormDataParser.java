/*
 * Parser for content-type: application/x-www-form-urlencoded.
 * Copyright (C) 2001, 2002, 2005  Nicholas Clarke
 *
 */

/*
 * History:
 *
 * 24-Nov-2002 : Extracted from DispatchWorker.
 * 07-Apr-2003 : init() added.
 * 21-Jun-2003 : Moved to antiaction.com package.
 * 29-Oct-2005 : Fixed interface to avoid cast.
 *
 */

package com.antiaction.common.filter.fileupload;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Parser for content-type: application/x-www-form-urlencoded.
 *
 * @version 1.00
 * @author Nicholas Clarke <mayhem[at]antiaction[dot]com>
 */
public class UrlEncodedFormDataParser {

	public static final int S_NAME = 0;
	public static final int S_VALUE = 1;
	public static final int S_ESCAPED = 2;

	public static final boolean parseUrlEncodedFormData(InputStream in, String charsetName, int buffer_size, Map parameters) throws IOException {
		byte[] bytes = new byte[ buffer_size ];
		ByteBuffer byteBuffer = ByteBuffer.wrap( bytes );

		if ( charsetName == null ) {
			charsetName = System.getProperty( "file.encoding" );
		}

		int state = S_NAME;
		int rState = S_NAME;
		int position = 0;
		int limit = 0;
		int c;

		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		int sPos;
		int hIdx = 0;
		int hex = 0;

		String name = null;
		String value;
		List values;

		boolean bValid = true;
		boolean bLoop = true;
		while ( bValid && bLoop ) {
			if ( byteBuffer.read( in ) != -1 ) {
				byteBuffer.flip();
				position = byteBuffer.position;
				limit = byteBuffer.limit;
				while ( bValid && position < limit ) {
					switch ( state ) {
					case S_NAME:
						sPos = position;
						while ( bValid && position < limit && state == S_NAME ) {
							c = bytes[ position ] & 255;
							switch ( c ) {
							case '&':
								bout.reset();
								sPos = ++position;
								break;
							case '+':
								bout.write( bytes, sPos, position - sPos );
								bout.write( ' ' );
								sPos = ++position;
								break;
							case '%':
								bout.write( bytes, sPos, position - sPos );
								++position;
								state = S_ESCAPED;
								rState = S_NAME;
								hIdx = 0;
								break;
							case '=':
								bout.write( bytes, sPos, position - sPos );
								name = bout.toString( charsetName );
								++position;
								bout.reset();
								state = S_VALUE;
								break;
							default:
								++position;
								break;
							}
						}
						break;
					case S_VALUE:
						sPos = position;
						while ( bValid && position < limit && state == S_VALUE ) {
							c = bytes[ position ] & 255;
							switch ( c ) {
							case '=':
								bValid = false;
								break;
							case '+':
								bout.write( bytes, sPos, position - sPos );
								bout.write( ' ' );
								sPos = ++position;
								break;
							case '%':
								bout.write( bytes, sPos, position - sPos );
								++position;
								state = S_ESCAPED;
								rState = S_VALUE;
								hIdx = 0;
								break;
							case '&':
								bout.write( bytes, sPos, position - sPos );
								if ( name.length() > 0 ) {
									value = bout.toString( charsetName );
									values = (List)parameters.get( name );
									if ( values == null ) {
										values = new ArrayList();
										parameters.put( name, values );
									}
									values.add( value );
								}
								bout.reset();
								sPos = ++position;
								state = S_VALUE;
								break;
							default:
								++position;
								break;
							}
						}
						break;
					case S_ESCAPED:
						c = bytes[ position++ ] & 255;
						if ( hIdx == 0 ) {
							c = asciiHexTab[ c ];
							if ( c >= 0 ) {
								hex = c << 4;
							}
							else {
								bValid = false;
							}
						}
						else {
							c = asciiHexTab[ c ];
							if ( c >= 0 ) {
								hex |= c;
								bout.write( hex );
								state = rState;
							}
							else {
								bValid = false;
							}
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

    /** Hex char to integer conversion table. */
    public static int[] asciiHexTab = new int[ 256 ];

    /** Integer to hex char conversion table. */
    public static char[] hexTab = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    /*
     * Initialize ASCII hex table.
     */
    static {
        String hex = "0123456789abcdef";
        for ( int i=0; i<asciiHexTab.length; ++i ) {
            asciiHexTab[ i ] = hex.indexOf( i );
        }
        hex = hex.toUpperCase();
        for ( int i=0; i<hex.length(); ++i ) {
            asciiHexTab[ hex.charAt( i ) ] = i;
        }
    }

}
