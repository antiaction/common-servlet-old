/*
 * Created on 10/04/2013
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.filter.fileupload;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.List;

public class MultipartFormData {

	public String contentDisposition;

	public String contentName;

	protected ByteArrayOutputStream out;

	public List files;

	public OutputStream getOutputStream() {
		out = new ByteArrayOutputStream();
		return out;
	}

	public String getValue() {
		return new String( out.toByteArray() );
	}

}
