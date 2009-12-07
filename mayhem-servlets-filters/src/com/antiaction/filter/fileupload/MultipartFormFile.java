/*
 * Dispatch Form Data File.
 * Copyright (C) 2001, 2002  Nicholas Clarke
 *
 */

/*
 * History:
 *
 * 05-May-2002 : First implementation.
 * 12-May-2002 : File.
 * 21-Jun-2003 : Moved to antiaction.com package.
 *
 */

package com.antiaction.filter.fileupload;

import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.BufferedOutputStream;

/**
 * Dispatch Form Data File.
 *
 * @version 1.00
 * @author Nicholas Clarke <mayhem[at]antiaction[dot]com>
 */
public class MultipartFormFile implements IMultipartFormData, IMultipartFormFile {

	private String dispFilename;
	private String contentType;
	private String contentName;

	private File file;
	private FileOutputStream outStream;
	private BufferedOutputStream out;

	private boolean claimed;

	public MultipartFormFile(File path, String filename) {
		claimed = false;

		file = new File( path, filename );
		if ( file.exists() ) {
			file.delete();
		}
		try {
			outStream = new FileOutputStream( file, false );
			out = new BufferedOutputStream( outStream, 8192 );
		}
		catch ( FileNotFoundException e ) {
		}
	}

	public void delete() {
		if ( file != null && file.exists() ) {
			file.delete();
		}
	}

	public void write(int b) throws IOException {
		out.write( b );
	}

	public void write(byte[] b, int off, int len) throws IOException {
		out.write( b, off, len );
	}

	public void close() throws IOException {
		out.flush();
		out.close();
	}

	public String toString() {
		throw new UnsupportedOperationException( "MultipartFormFile.toString()" );
	}

	public File getFile() {
		return file;
	}

	public String getAbsolutePath() {
		return file.getAbsolutePath();
	}

	public void setDispositionFilename(String s) {
		dispFilename = s;
	}

	public String getDispositionFilename() {
		return dispFilename;
	}

	public void setContentType(String s) {
		contentType = s;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentName(String s) {
		contentName = s;
	}

	public String getContentName() {
		return contentName;
	}

	public void setClaimed(boolean b) {
		claimed = b;
	}

	public boolean isClaimed() {
		return claimed;
	}

}
