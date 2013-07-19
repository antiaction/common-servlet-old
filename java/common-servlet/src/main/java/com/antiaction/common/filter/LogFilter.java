/*
 * Asynchronous logging filter.
 * Copyright (C) 2004, 2005, 2011  Nicholas Clarke
 *
 */

/*
 * History:
 *
 * 04-Mar-2005 : Refactoring, made storing asynchronous.
 * 10-Sep-2011 : Added missing rs.close() and rs.insertStm to plug memory mysql jdbc leak.
 * 10-Sep-2011 : Added some autodoc.
 * 10-Sep-2011 : Added thread exit to destroy() and some cleanup on thread exit.
 * 10-Sep-2011 : Refactored thread to be more effective.
 *
 */

package com.antiaction.common.filter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

/**
 * Asynchronous logging filter.
 *
 * @version 1.00
 * @author Nicholas Clarke <mayhem[at]antiaction[dot]com>
 */
public class LogFilter implements Filter, Runnable {

	/** Shutdown boolean. */
	private boolean exit = false;

	/** DataSource name. */
	private String dsName;

	/** Container context. */
	private Context ctx;

	/** DataSource instance. */
	private DataSource ds;

	/** Synchronize object. */
	private Object syncObj = new Object();

	/** <code>List</code> of new log entries. */
	private List newEntries = new ArrayList();

	public void init(FilterConfig filterConfig) throws ServletException {
		dsName = filterConfig.getInitParameter( "datasource" );
		if ( dsName != null && !dsName.equals( "" ) ) {
			try {
				ctx = new InitialContext();
				ds = (DataSource)ctx.lookup( dsName );
			}
			catch (NamingException e) {
				System.out.println( e );
			}
		}
		Thread t = new Thread( this );
		t.start();
	}

	public void destroy() {
		exit = true;
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)request;
		//HttpServletResponse resp = (HttpServletResponse)response;

		HttpSession httpSession = req.getSession( true );

		long enterTime = System.currentTimeMillis();

		chain.doFilter( request, response );

		long exitTime = System.currentTimeMillis();

		String resource = req.getRequestURI();
		String queryString = req.getQueryString();
		if ( queryString != null ) {
			resource += "?" + queryString;
		}

		String sessionId = null;
		if ( httpSession != null ) {
			sessionId = httpSession.getId();
		}

		FilterEntry entry = new FilterEntry();
		entry.timestamp = new Timestamp( enterTime );
		entry.clientIp = request.getRemoteAddr();
		entry.referer = req.getHeader( "referer" );
		entry.method = req.getMethod();
		entry.resourcePath = resource;
		entry.sessionId = sessionId;
		entry.statusCode = 1;
		entry.processTime = (int)(exitTime - enterTime);

		synchronized ( syncObj ) {
			newEntries.add( entry );
		}
	}

	static class FilterEntry {
		Timestamp timestamp;
		String clientIp;
		String referer;
		String method;
		String resourcePath;
		String sessionId;
		int statusCode;
		int processTime;
	}

	private static String insertSql;

	static {
		insertSql = "INSERT INTO accesslog";
		insertSql += "(timestamp, clientIp, referer, method, resourcePath, sessionId, statusCode, processTime) ";
		insertSql += "VALUES(?, ?, ?, ?, ?, ?, ?, ?) ";
	}

	public void run() {
		Connection conn;
		ResultSet rs;
		PreparedStatement insertStm;

		List workEntries = new ArrayList();

		while ( !exit ) {
			try {
				Thread.sleep( 60 * 1000 );

				synchronized ( syncObj ) {
					workEntries.addAll( newEntries );
					newEntries.clear();
				}

				if ( workEntries.size() > 0 ) {
					conn = null;
					rs = null;
					insertStm = null;

					try {
						if ( ds != null ) {
							conn = ds.getConnection();
							if ( conn != null ) {
								conn.setCatalog( "alfachins" );
								conn.setAutoCommit( false );

								insertStm = conn.prepareStatement( insertSql, Statement.RETURN_GENERATED_KEYS  );

								while ( workEntries.size() > 0 ) {
									FilterEntry entry = (FilterEntry)workEntries.remove( 0 );

									insertStm.clearParameters();
									insertStm.setTimestamp( 1, entry.timestamp );
									insertStm.setString( 2, entry.clientIp );
									insertStm.setString( 3, entry.referer );
									insertStm.setString( 4, entry.method );
									insertStm.setString( 5, entry.resourcePath );
									insertStm.setString( 6, entry.sessionId );
									insertStm.setInt( 7, entry.statusCode );
									insertStm.setInt( 8, entry.processTime );
									insertStm.executeUpdate();

									rs = insertStm.getGeneratedKeys();
									if ( rs.next() ) {
										//System.out.println( rs.getLong( 1 ) );
									}
									rs.close();

									conn.commit();
								}
							}
						}
					}
					catch (SQLException e) {
						System.out.println( e );
					}
					finally {
						try {
							if ( rs != null ) {
								rs.close();
							}
						}
						catch (SQLException e) {
						}
						rs = null;
						try {
							if ( insertStm != null ) {
								insertStm.close();
							}
						}
						catch (SQLException e) {
						}
						insertStm = null;
						try {
							if ( conn != null ) {
								conn.close();
							}
						}
						catch (SQLException e) {
						}
						conn = null;
					}
				}
			}
			catch (InterruptedException e) {
			}
		}

		dsName = null;
		ctx = null;
		ds = null;
		syncObj = null;
		newEntries = null;
	}

}
