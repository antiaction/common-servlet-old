/*
 * Created on 20/03/2013
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.servlet;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TestPathMap {

	@Test
	public void test_pathmap_add() {
		PathMap<Integer> pathMap;

		PathMap<Integer> domainPathMap;
		PathMap<Integer> domainNumericPathMap;
		PathMap<Integer> accountPathMap;
		PathMap<Integer> accountNumericPathMap;

		/*
		 * add() path.
		 */

		pathMap = new PathMap<Integer>();

		Assert.assertEquals( 0, pathMap.pathMap.size() );
		Assert.assertNull( pathMap.numericMap );
		Assert.assertNull( pathMap.stringMap );
		Assert.assertNull( pathMap.action );
		Assert.assertNull( pathMap.wildcardAction );

		// Invalid.
		pathMap.add( null, -1 );

		Assert.assertEquals( 0, pathMap.pathMap.size() );
		Assert.assertNull( pathMap.numericMap );
		Assert.assertNull( pathMap.stringMap );
		Assert.assertNull( pathMap.action );
		Assert.assertNull( pathMap.wildcardAction );

		// Root.
		pathMap.add( "", 0 );

		Assert.assertEquals( 0, pathMap.pathMap.size() );
		Assert.assertNull( pathMap.numericMap );
		Assert.assertNull( pathMap.stringMap );
		Assert.assertEquals( new Integer( 0 ), pathMap.action );
		Assert.assertNull( pathMap.wildcardAction );

		pathMap.add( "domain/list/", 1 );

		Assert.assertEquals( 1, pathMap.pathMap.size() );
		Assert.assertNull( pathMap.numericMap );
		Assert.assertNull( pathMap.stringMap );
		Assert.assertEquals( new Integer( 0 ), pathMap.action );
		Assert.assertNull( pathMap.wildcardAction );

		domainPathMap = pathMap.pathMap.get( "domain" );
		Assert.assertNotNull( domainPathMap );

		Assert.assertEquals( 1, domainPathMap.pathMap.size() );
		Assert.assertNull( domainPathMap.numericMap );
		Assert.assertNull( domainPathMap.stringMap );
		Assert.assertNull( domainPathMap.action );
		Assert.assertNull( domainPathMap.wildcardAction );

		pathMap.add( "domain/add/", 2 );

		Assert.assertEquals( 2, domainPathMap.pathMap.size() );
		Assert.assertNull( domainPathMap.numericMap );
		Assert.assertNull( domainPathMap.stringMap );
		Assert.assertNull( domainPathMap.action );
		Assert.assertNull( domainPathMap.wildcardAction );

		pathMap.add( "domain/<numeric>/edit/", 3 );
		pathMap.add( "domain/<numeric>/account/list/", 4 );
		pathMap.add( "domain/<numeric>/account/add/", 5 );
		pathMap.add( "domain/<numeric>/account/<numeric>/edit/", 6 );

		try {
			pathMap.add( "domain/<numeric>/account/*/", 7 );
			Assert.fail( "Exception expected!" );
		}
		catch (IllegalArgumentException e) {
		}

		pathMap.add( "domain/<numeric>/account/*", 7 );

		try {
			pathMap.add( "domain/<numeric>/account/*", 7 );
			Assert.fail( "Exception expected!" );
		}
		catch (IllegalArgumentException e) {
		}

		try {
			pathMap.add( "domain/<numeric>//", -1 );
			Assert.fail( "Exception expected!" );
		}
		catch (IllegalArgumentException e) {
		}

		Assert.assertEquals( 2, domainPathMap.pathMap.size() );
		Assert.assertNotNull( domainPathMap.numericMap );
		Assert.assertNull( domainPathMap.stringMap );
		Assert.assertNull( domainPathMap.stringMap );
		Assert.assertNull( domainPathMap.action );
		Assert.assertNull( domainPathMap.wildcardAction );

		domainNumericPathMap = domainPathMap.numericMap;
		Assert.assertNotNull( domainNumericPathMap );

		Assert.assertEquals( 2, domainNumericPathMap.pathMap.size() );
		Assert.assertNull( domainNumericPathMap.numericMap );
		Assert.assertNull( domainNumericPathMap.stringMap );
		Assert.assertNull( domainNumericPathMap.action );
		Assert.assertNull( domainNumericPathMap.wildcardAction );

		accountPathMap = domainNumericPathMap.pathMap.get( "account" );
		Assert.assertNotNull( accountPathMap );

		Assert.assertEquals( 2, accountPathMap.pathMap.size() );
		Assert.assertNotNull( accountPathMap.numericMap );
		Assert.assertNull( accountPathMap.stringMap );
		Assert.assertNull( accountPathMap.action );
		Assert.assertNotNull( accountPathMap.wildcardAction );

		accountNumericPathMap = accountPathMap.numericMap;
		Assert.assertNotNull( accountNumericPathMap );

		Assert.assertEquals( 1, accountNumericPathMap.pathMap.size() );
		Assert.assertNull( accountNumericPathMap.numericMap );
		Assert.assertNull( accountPathMap.stringMap );
		Assert.assertNull( accountNumericPathMap.action );
		Assert.assertNull( accountNumericPathMap.wildcardAction );

		Assert.assertEquals( 1, pathMap.pathMap.size() );
		Assert.assertNull( pathMap.numericMap );
		Assert.assertEquals( new Integer( 0 ), pathMap.action );
		Assert.assertNull( pathMap.wildcardAction );

		/*
		 * add() /path.
		 */

		pathMap = new PathMap<Integer>();

		Assert.assertEquals( 0, pathMap.pathMap.size() );
		Assert.assertNull( pathMap.numericMap );
		Assert.assertNull( pathMap.stringMap );
		Assert.assertNull( pathMap.action );
		Assert.assertNull( pathMap.wildcardAction );

		// Invalid.
		pathMap.add( null, -1 );

		Assert.assertEquals( 0, pathMap.pathMap.size() );
		Assert.assertNull( pathMap.numericMap );
		Assert.assertNull( pathMap.stringMap );
		Assert.assertNull( pathMap.action );
		Assert.assertNull( pathMap.wildcardAction );

		// Root.
		pathMap.add( "/", 0 );

		Assert.assertEquals( 0, pathMap.pathMap.size() );
		Assert.assertNull( pathMap.numericMap );
		Assert.assertNull( pathMap.stringMap );
		Assert.assertEquals( new Integer( 0 ), pathMap.action );
		Assert.assertNull( pathMap.wildcardAction );

		pathMap.add( "/domain/list/", 1 );

		Assert.assertEquals( 1, pathMap.pathMap.size() );
		Assert.assertNull( pathMap.numericMap );
		Assert.assertNull( pathMap.stringMap );
		Assert.assertEquals( new Integer( 0 ), pathMap.action );
		Assert.assertNull( pathMap.wildcardAction );

		domainPathMap = pathMap.pathMap.get( "domain" );
		Assert.assertNotNull( domainPathMap );

		Assert.assertEquals( 1, domainPathMap.pathMap.size() );
		Assert.assertNull( domainPathMap.numericMap );
		Assert.assertNull( domainPathMap.stringMap );
		Assert.assertNull( domainPathMap.action );
		Assert.assertNull( domainPathMap.wildcardAction );

		pathMap.add( "/domain/add/", 2 );

		Assert.assertEquals( 2, domainPathMap.pathMap.size() );
		Assert.assertNull( domainPathMap.numericMap );
		Assert.assertNull( domainPathMap.stringMap );
		Assert.assertNull( domainPathMap.action );
		Assert.assertNull( domainPathMap.wildcardAction );

		pathMap.add( "/domain/<numeric>/edit/", 3 );
		pathMap.add( "/domain/<numeric>/account/list/", 4 );
		pathMap.add( "/domain/<numeric>/account/add/", 5 );
		pathMap.add( "/domain/<numeric>/account/<numeric>/edit/", 6 );

		try {
			pathMap.add( "domain/<numeric>/account/*/", 7 );
			Assert.fail( "Exception expected!" );
		}
		catch (IllegalArgumentException e) {
		}

		pathMap.add( "domain/<numeric>/account/*", 7 );

		try {
			pathMap.add( "domain/<numeric>/account/*", 7 );
			Assert.fail( "Exception expected!" );
		}
		catch (IllegalArgumentException e) {
		}

		Assert.assertEquals( 2, domainPathMap.pathMap.size() );
		Assert.assertNotNull( domainPathMap.numericMap );
		Assert.assertNull( domainPathMap.stringMap );
		Assert.assertNull( domainPathMap.action );
		Assert.assertNull( domainPathMap.wildcardAction );

		domainNumericPathMap = domainPathMap.numericMap;
		Assert.assertNotNull( domainNumericPathMap );

		Assert.assertEquals( 2, domainNumericPathMap.pathMap.size() );
		Assert.assertNull( domainNumericPathMap.numericMap );
		Assert.assertNull( domainPathMap.stringMap );
		Assert.assertNull( domainNumericPathMap.action );
		Assert.assertNull( domainNumericPathMap.wildcardAction );

		accountPathMap = domainNumericPathMap.pathMap.get( "account" );
		Assert.assertNotNull( accountPathMap );

		Assert.assertEquals( 2, accountPathMap.pathMap.size() );
		Assert.assertNotNull( accountPathMap.numericMap );
		Assert.assertNull( accountPathMap.stringMap );
		Assert.assertNull( accountPathMap.action );
		Assert.assertNotNull( accountPathMap.wildcardAction );

		accountNumericPathMap = accountPathMap.numericMap;
		Assert.assertNotNull( accountNumericPathMap );

		Assert.assertEquals( 1, accountNumericPathMap.pathMap.size() );
		Assert.assertNull( accountNumericPathMap.numericMap );
		Assert.assertNull( accountNumericPathMap.stringMap );
		Assert.assertNull( accountNumericPathMap.action );
		Assert.assertNull( accountNumericPathMap.wildcardAction );

		Assert.assertEquals( 1, pathMap.pathMap.size() );
		Assert.assertNull( pathMap.numericMap );
		Assert.assertNull( pathMap.stringMap );
		Assert.assertEquals( new Integer( 0 ), pathMap.action );
		Assert.assertNull( pathMap.wildcardAction );
	}

	@Test
	public void test_pathmap_get() {
		PathMap<Integer> pathMap;

		pathMap = new PathMap<Integer>();
		pathMap.add( "/", 0 );
		pathMap.add( "/domain/list/", 1 );
		pathMap.add( "/domain/add/", 2 );
		pathMap.add( "/domain/<numeric>/edit/", 3 );
		pathMap.add( "/domain/<numeric>/account/list/", 4 );
		pathMap.add( "/domain/<numeric>/account/add/", 5 );
		pathMap.add( "/domain/<numeric>/account/<numeric>/edit/", 6 );
		pathMap.add( "/domain/<numeric>/account/*", 7 );
		pathMap.add( "/user/<string>/config/*", 8 );
		pathMap.add( "/user/:string/config/:numeric/", 9 );

		/*
		 * get().
		 */

		Integer action;
		List<Long> numerics = new ArrayList<Long>();
		List<String> strings = new ArrayList<String>();

		action = pathMap.get( null, null, null );
		Assert.assertNull( action );
		Assert.assertEquals( 0, numerics.size() );

		action = pathMap.get( "/", numerics, strings );
		//System.out.println( action );
		//printarray( numerics );
		Assert.assertEquals( new Integer( 0 ), action );
		Assert.assertEquals( 0, numerics.size() );

		action = pathMap.get( "/domain/list/", numerics, strings );
		//System.out.println( action );
		//printarray( numerics );
		Assert.assertEquals( new Integer( 1 ), action );
		Assert.assertEquals( 0, numerics.size() );

		action = pathMap.get( "/domain/add/", numerics, strings );
		//System.out.println( action );
		//printarray( numerics );
		Assert.assertEquals( new Integer( 2 ), action );
		Assert.assertEquals( 0, numerics.size() );

		action = pathMap.get( "/domain/42/edit/", numerics, strings );
		//System.out.println( action );
		//printarray( numerics );
		Assert.assertEquals( new Integer( 3 ), action );
		Assert.assertEquals( 1, numerics.size() );
		Assert.assertEquals( new Long( 42 ), numerics.get( 0 ) );

		action = pathMap.get( "/domain/43/account/list/", numerics, strings );
		//System.out.println( action );
		//printarray( numerics );
		Assert.assertEquals( new Integer( 4 ), action );
		Assert.assertEquals( 1, numerics.size() );
		Assert.assertEquals( new Long( 43 ), numerics.get( 0 ) );

		action = pathMap.get( "/domain/44/account/add/", numerics, strings );
		//System.out.println( action );
		//printarray( numerics );
		Assert.assertEquals( new Integer( 5 ), action );
		Assert.assertEquals( 1, numerics.size() );
		Assert.assertEquals( new Long( 44 ), numerics.get( 0 ) );

		action = pathMap.get( "/domain/45/account/46/edit/", numerics, strings );
		//System.out.println( action );
		//printarray( numerics );
		Assert.assertEquals( new Integer( 6 ), action );
		Assert.assertEquals( 2, numerics.size() );
		Assert.assertEquals( new Long( 45 ), numerics.get( 0 ) );
		Assert.assertEquals( new Long( 46 ), numerics.get( 1 ) );

		action = pathMap.get( "/domain/45/account/fortytwo/", numerics, strings );
		//System.out.println( action );
		//printarray( numerics );
		Assert.assertEquals( new Integer( 7 ), action );
		Assert.assertEquals( 1, numerics.size() );
		Assert.assertEquals( new Long( 45 ), numerics.get( 0 ) );

		action = pathMap.get( "domain/46/account/", numerics, strings );
		Assert.assertEquals( new Integer( 7 ), action );
		Assert.assertEquals( 1, numerics.size() );
		Assert.assertEquals( new Long( 46 ), numerics.get( 0 ) );

		action = pathMap.get( "/user/scarlett/config/", numerics, strings );
		Assert.assertEquals( new Integer( 8 ), action );
		Assert.assertEquals( 0, numerics.size() );
		Assert.assertEquals( 1, strings.size() );
		Assert.assertEquals( "scarlett", strings.get( 0 ) );

		action = pathMap.get( "/user/scarlett/config/69/", numerics, strings );
		Assert.assertEquals( new Integer( 9 ), action );
		Assert.assertEquals( 1, numerics.size() );
		Assert.assertEquals( 1, strings.size() );
		Assert.assertEquals( "scarlett", strings.get( 0 ) );
		Assert.assertEquals( new Long( 69 ), numerics.get( 0 ) );
	}

	public static void printarray(List<Long> numerics) {
		if ( numerics != null ) {
			for ( int i=0; i<numerics.size(); ++i ) {
				if ( i > 0 ) {
					System.out.print( ", " );
				}
				System.out.print( numerics.get( i ) );
			}
			System.out.println();
		}
	}

}
