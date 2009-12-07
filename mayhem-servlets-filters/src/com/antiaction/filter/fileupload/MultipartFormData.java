/*
 * Dispatch Form Data.
 * Copyright (C) 2001, 2002  Nicholas Clarke
 *
 */

/*
 * History:
 *
 * 05-May-2002 : First implementation.
 * 21-Jun-2003 : Moved to antiaction.com package.
 *
 */

package com.antiaction.filter.fileupload;

import java.io.IOException;

/**
 * Dispatch Form Data.
 *
 * @version 1.00
 * @author Nicholas Clarke <mayhem[at]antiaction[dot]com>
 */
public class MultipartFormData implements IMultipartFormData {

	StringBuffer tmpStrB;

	public MultipartFormData() {
		tmpStrB = new StringBuffer();
	}

	public void write(int b) throws IOException {
		tmpStrB.append( (char)b );
	}

	public void write(byte[] b, int off, int len) throws IOException {
		while ( len > 0 ) {
			tmpStrB.append( (char)b[ off++ ] );
			--len;
		}
	}

	public void close() throws IOException {
	}

	public String toString() {
		return tmpStrB.toString();
	}

}
