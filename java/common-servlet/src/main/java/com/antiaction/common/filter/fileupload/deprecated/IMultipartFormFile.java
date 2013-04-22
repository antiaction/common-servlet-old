/*
 * Interface Dispatch Form Data File.
 * Copyright (C) 2001, 2002  Nicholas Clarke
 *
 */

/*
 * History:
 *
 * 18-May-2002 : First implementation.
 * 21-Jun-2003 : Moved to antiaction.com package.
 *
 */

package com.antiaction.common.filter.fileupload.deprecated;

import java.io.File;

/**
 * Interface Dispatch Form Data File.
 *
 * @version 1.00
 * @author Nicholas Clarke <mayhem[at]antiaction[dot]com>
 */
public interface IMultipartFormFile {

	public File getFile();

	public String getAbsolutePath();

	public String getDispositionFilename();

	public String getContentType();

	public String getContentName();

	public void setClaimed(boolean b);

	public boolean isClaimed();

}
