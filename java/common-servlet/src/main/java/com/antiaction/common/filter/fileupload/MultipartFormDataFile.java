/*
 * Created on 10/04/2013
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.filter.fileupload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

public class MultipartFormDataFile {

	public String contentFilename;

	public String contentType;

	public String contentBoundary;

	public String contentCharset;

	public String contentTransferEncoding;

	protected File file;

	protected FileOutputStream out;

	public static MultipartFormDataFile getInstance(File tmpdir) throws IOException {
		MultipartFormDataFile mpfdf = new MultipartFormDataFile();
		Random random = new Random();
		String ctm = "000000000000000" + Long.toHexString( System.currentTimeMillis() );
		ctm = ctm.substring( ctm.length() - 16, ctm.length() );
		String rand = "000000000000000" + Long.toHexString( random.nextLong() );
		rand = rand.substring( rand.length() - 16, rand.length() );
		String filename = "temp-formdata-" + ctm + "-" + rand;
		mpfdf.file  = new File( tmpdir, filename );
		mpfdf.out = new FileOutputStream( mpfdf.file, false );
		return mpfdf;
	}

	public OutputStream getOutputStream() {
		return out;
	}

	public File getFile() {
		return file;
	}

	public InputStream getInputStream() throws IOException {
		return new FileInputStream( file );
	}

}
