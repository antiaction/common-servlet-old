/*
 * Created on 04/04/2013
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.filter.fileupload;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
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

	//@Test
	public void test_multipartformdata_plain_w3() {
		String formdata;
		ByteArrayInputStream bais;
		Map parameters;
		List files;
		boolean success;
		String tmpStr;

		parameters = new HashMap();
		files = new ArrayList();

		File tmpdir = new File( System.getProperty("java.io.tmpdir") );

		try {
			formdata = "--AaB03x\r\n"
					+ "Content-Disposition: form-data; name=\"submit-name\"\r\n"
					+ "\r\n"
					+ "Larry\r\n"
					+ "--AaB03x\r\n"
					+ "Content-Disposition: form-data; name=\"files\"; filename=\"file1.txt\"\r\n"
					+ "Content-Type: text/plain\r\n"
					+ "\r\n"
					+ "... contents of file1.txt ...\r\n"
					+ "--AaB03x--\r\n";

			int[] buffer_sizes = { 1, 2, 3, 4, 32, 256, 1024, 8192 };
			for ( int i=0; i<buffer_sizes.length; ++i ) {
				parameters.clear();
				files.clear();
				bais = new ByteArrayInputStream( formdata.getBytes() );
				success = MultipartFormDataParser.parseMultipartFormData( bais, "AaB03x", "UTF-8", buffer_sizes[ i ], parameters, files, tmpdir );
				bais.close();

				// debug
				System.out.println( buffer_sizes[ i ] + " -> " + success );

				Assert.assertEquals( 1, parameters.size() );
				tmpStr = (String)parameters.get( "submit-name" );
				Assert.assertEquals( tmpStr, "Larry" );

				Assert.assertEquals( 1, files.size() );

				Assert.assertTrue( success );
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			Assert.fail( "Unexpected exception!" );
		}
	}

	//@Test
	public void test_multipartformdata_mixed_w3() {
		String formdata;
		ByteArrayInputStream bais;
		Map parameters;
		List files;
		boolean success;

		parameters = new HashMap();
		files = new ArrayList();

		File tmpdir = new File( System.getProperty("java.io.tmpdir") );

		try {
			formdata = "--AaB03x\r\n"
					+ "Content-Disposition: form-data; name=\"submit-name\"\r\n"
					+ "\r\n"
					+ "Larry\r\n"
					+ "--AaB03x\r\n"
					+ "Content-Disposition: form-data; name=\"files\"\r\n"
					+ "Content-Type: multipart/mixed; boundary=BbC04y\r\n"
					+ "\r\n"
					+ "--BbC04y\r\n"
					+ "Content-Disposition: file; filename=\"file1.txt\"\r\n"
					+ "Content-Type: text/plain\r\n"
					+ "\r\n"
					+ "... contents of file1.txt ...\r\n"
					+ "--BbC04y\r\n"
					+ "Content-Disposition: file; filename=\"file2.gif\"\r\n"
					+ "Content-Type: image/gif\r\n"
					+ "Content-Transfer-Encoding: binary\r\n"
					+ "\r\n"
					+ "...contents of file2.gif...\r\n"
					+ "--BbC04y--\r\n"
					+ "--AaB03x--\r\n";

			int[] buffer_sizes = { 1, 2, 3, 4, 32, 256, 1024, 8192 };
			for ( int i=0; i<buffer_sizes.length; ++i ) {
				parameters.clear();
				files.clear();
				bais = new ByteArrayInputStream( formdata.getBytes() );
				success = MultipartFormDataParser.parseMultipartFormData( bais, "AaB03x", "UTF-8", buffer_sizes[ i ], parameters, files, tmpdir );
				bais.close();

				// debug
				System.out.println( "-> " + success );

				Assert.assertTrue( success );
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			Assert.fail( "Unexpected exception!" );
		}
	}

	@Test
	public void test_multipartformdata() {
		URL url;
		File file;
		Map parameters;
		List files;
		boolean success;

		String formdata;

		File tmpdir = new File( System.getProperty("java.io.tmpdir") );

		try {
			url = this.getClass().getClassLoader().getResource( "form.5" );
			file = new File(getUrlPath(url));

			int[] buffer_sizes = { 1, 2, 3, 4, 32, 256, 1024, 8192 };
			for ( int i=0; i<buffer_sizes.length; ++i ) {
				FileInputStream fin = new FileInputStream( file );

				parameters = new HashMap();
				files = new ArrayList();

				//success = MultipartFormDataParser.parseMultipartFormData( fin, "----WebKitFormBoundaryQKfvueCtyOWOQqMF", parameters, files, tmpdir );
				success = MultipartFormDataParser.parseMultipartFormData( fin, "---------------------------11590182839762", "UTF-8", buffer_sizes[ i ], parameters, files, tmpdir );

				System.out.println( success );

				fin.close();
			}
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
