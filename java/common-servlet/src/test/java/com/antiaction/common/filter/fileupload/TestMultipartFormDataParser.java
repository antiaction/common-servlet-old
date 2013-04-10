/*
 * Created on 04/04/2013
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.filter.fileupload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TestMultipartFormDataParser {

	public String getUrlPath(URL url) {
		String path = url.getFile();
		path = path.replaceAll( "%5b", "[" );
		path = path.replaceAll( "%5d", "]" );
		return path;
	}

	@Test
	public void test_multipartformdata() {
		URL url;
		File file;
		Map parameters;
		List files;
		boolean success;

		File tmpdir = new File( System.getProperty("java.io.tmpdir") );

		try {
			url = this.getClass().getClassLoader().getResource( "form.5" );
			file = new File(getUrlPath(url));
			FileInputStream fin = new FileInputStream( file );

			parameters = new HashMap();
			files = new ArrayList();

			//success = MultipartFormDataParser.parseMultipartFormData( fin, "----WebKitFormBoundaryQKfvueCtyOWOQqMF", parameters, files, tmpdir );
			success = MultipartFormDataParser.parseMultipartFormData( fin, "---------------------------11590182839762", parameters, files, tmpdir );

			System.out.println( success );

			fin.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
			Assert.fail( "Unexpected exception!" );
		}
		catch (IOException e) {
			e.printStackTrace();
			Assert.fail( "Unexpected exception!" );
		}
	}

}
