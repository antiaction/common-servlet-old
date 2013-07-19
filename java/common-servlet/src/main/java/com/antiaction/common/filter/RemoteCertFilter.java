/*
 * Created on 2006-08-12
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RemoteCertFilter implements Filter {

	/** FilterConfig used to obtaint ServletContext. */
	private FilterConfig filterConfig;

	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
		this.filterConfig.getInitParameter( "rootcert" );
	}

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse resp = (HttpServletResponse)response;
		//ServletContext servletContext = filterConfig.getServletContext();

		boolean authenticated = false;

		if ( req.isSecure() ) {
			String cipherSuite = (String)req.getAttribute( "javax.servlet.request.cipher_suite" );
			//Integer keySize = (Integer)req.getAttribute( "javax.servlet.request.key_size" );
			X509Certificate[] peerCerts = (X509Certificate[])req.getAttribute( "javax.servlet.request.X509Certificate" );

			if ( cipherSuite != null && peerCerts != null && peerCerts.length > 0 ) {
				// debug
				System.out.println( peerCerts[ 0 ].getSubjectDN() );

				// Validate CertificateChain.

				int idx = 1;
				try {
					peerCerts[ 0 ].checkValidity();
					boolean b = true;
					while ( b ) {
						if ( idx < peerCerts.length ) {
							peerCerts[ idx - 1 ].verify( peerCerts[ idx ].getPublicKey() );
							peerCerts[ idx ].checkValidity();
							++idx;
						}
						else {
							b = false;
						}
					}
				}
				catch (InvalidKeyException e) {
				}
				catch (NoSuchAlgorithmException e) {
				}
				catch (NoSuchProviderException e) {
				}
				catch (SignatureException e) {
				}
				catch (CertificateExpiredException e) {
				}
				catch (CertificateNotYetValidException e) {
				}
				catch (CertificateException e) {
				}

				if ( idx == peerCerts.length ) {
					// debug
					System.out.println( peerCerts[ idx - 1 ].getSubjectDN() );

					// Validate root Certificate.

					peerCerts[ idx - 1 ].getPublicKey();
				}
			}
		}

		if ( authenticated ) {
			chain.doFilter( request, response );
		}
		else {
			//HTTP/1.1 426 Upgrade Required
			//Upgrade: TLS/1.0, HTTP/1.1
			//Connection: Upgrade
			resp.reset();
			resp.setStatus( 426, "Upgrade Required" );
			resp.setHeader( "Upgrade", "TLS/1.0, HTTP/1.1" );
			resp.setHeader( "Connection", "Upgrade" );
			PrintWriter out = resp.getWriter();
			out.write( "User certificate authentication failed!" );
			out.flush();
		}
	}

}
