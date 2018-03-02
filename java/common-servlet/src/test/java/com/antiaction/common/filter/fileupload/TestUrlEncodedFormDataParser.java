/*
 * Created on 04/01/2014
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.filter.fileupload;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TestUrlEncodedFormDataParser {

	@Test
	public void test_urlencodedformdata() {
		String formdata;
		InputStream in;
		Map<String, List<String>> parameters;
		boolean res;
		Object[][] expectedParams;
		String[] expectedValues;
		List<String> values;

		Object[][] cases = new Object[][] {
				{
					"login_tourl=%2Fbootstrap-dab%2F%3Faction%3Dlogin&login_user=username&login_pass=password",
					true,
					new Object[][] {
							{ "login_tourl", new String[] {
									"/bootstrap-dab/?action=login"
							}},
							{ "login_user", new String[] {
									"username"
							}},
							{ "login_pass", new String[] {
									"password"
							}}
					}
				}
		};

		try {
			for ( int i=0; i<cases.length; ++i ) {
				formdata = (String)cases[ i ][ 0 ];
				in = new ByteArrayInputStream( formdata.getBytes() );
				parameters = new HashMap<String, List<String>>();
				res = UrlEncodedFormDataParser.parseUrlEncodedFormData( in, null, 8192, parameters );
				expectedParams = (Object[][])cases[ i ][ 2 ];
				Assert.assertEquals( expectedParams.length, parameters.size() );
				Assert.assertEquals( cases[ i ][ 1 ], res );
				for ( int j=0; j<expectedParams.length; ++j ) {
					expectedValues = (String[])expectedParams[ j ][ 1 ];
					values = (List<String>)parameters.get( expectedParams[ j ][ 0 ] );
					Assert.assertEquals( expectedValues.length, values.size() );
					for ( int k=0; k<expectedValues.length; ++k ) {
						Assert.assertEquals( expectedValues[ k ], values.get( k ) );
					}
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			Assert.fail( "Unexpected exception!" );
		}
	}

}
