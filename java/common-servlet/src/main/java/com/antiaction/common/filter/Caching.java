/*
 * Created on 16/05/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.filter;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

public class Caching {

	public static void caching_disable_headers(HttpServletResponse resp) throws IOException {
		// Set to expire far in the past.
		resp.setHeader( "Expires", "Sat, 6 May 1995 12:00:00 GMT" );

		// Set standard HTTP/1.1 no-cache headers.
		resp.setHeader( "Cache-Control", "no-store, no-cache, must-revalidate, private" );

		// Set IE extended HTTP/1.1 no-cache headers (use addHeader).
		resp.addHeader( "Cache-Control", "post-check=0, pre-check=0" );

		// Set standard HTTP/1.0 no-cache header.
		resp.setHeader( "Pragma", "no-cache" );
	}

}
