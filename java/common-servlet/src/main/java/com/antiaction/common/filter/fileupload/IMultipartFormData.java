/*
 * Interface Dispatch Form Data.
 * Copyright (C) 2001, 2002  Nicholas Clarke
 *
 */

/*
 * History:
 *
 * 05-May-2002 : First implementation.
 * 12-May-2002 : close().
 * 21-Jun-2003 : Moved to antiaction.com package.
 *
 */

package com.antiaction.common.filter.fileupload;

import java.io.IOException;

/**
 * Interface Dispatch Form Data.
 *
 * @version 1.00
 * @author Nicholas Clarke <mayhem[at]antiaction[dot]com>
 */
public interface IMultipartFormData {

	public void write(int c) throws IOException;

	public void write(byte[] b, int off, int len) throws IOException;

	public void close() throws IOException;

	public String toString();

}
