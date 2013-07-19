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
	public void test_pathmap() {
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
		Assert.assertNull( pathMap.numericAction );
		Assert.assertNull( pathMap.wildcardAction );

		// Invalid.
		pathMap.add( null, -1 );

		Assert.assertEquals( 0, pathMap.pathMap.size() );
		Assert.assertNull( pathMap.numericMap );
		Assert.assertNull( pathMap.numericAction );
		Assert.assertNull( pathMap.wildcardAction );

		// Root.
		pathMap.add( "", 0 );

		Assert.assertEquals( 0, pathMap.pathMap.size() );
		Assert.assertNull( pathMap.numericMap );
		Assert.assertEquals( new Integer( 0 ), pathMap.numericAction );
		Assert.assertNull( pathMap.wildcardAction );

		pathMap.add( "domain/list/", 1 );

		Assert.assertEquals( 1, pathMap.pathMap.size() );
		Assert.assertNull( pathMap.numericMap );
		Assert.assertEquals( new Integer( 0 ), pathMap.numericAction );
		Assert.assertNull( pathMap.wildcardAction );

		domainPathMap = pathMap.pathMap.get( "domain" );
		Assert.assertNotNull( domainPathMap );

		Assert.assertEquals( 1, domainPathMap.pathMap.size() );
		Assert.assertNull( domainPathMap.numericMap );
		Assert.assertNull( domainPathMap.numericAction );
		Assert.assertNull( domainPathMap.wildcardAction );

		pathMap.add( "domain/add/", 2 );

		Assert.assertEquals( 2, domainPathMap.pathMap.size() );
		Assert.assertNull( domainPathMap.numericMap );
		Assert.assertNull( domainPathMap.numericAction );
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
		Assert.assertNull( domainPathMap.numericAction );
		Assert.assertNull( domainPathMap.wildcardAction );

		domainNumericPathMap = domainPathMap.numericMap;
		Assert.assertNotNull( domainNumericPathMap );

		Assert.assertEquals( 2, domainNumericPathMap.pathMap.size() );
		Assert.assertNull( domainNumericPathMap.numericMap );
		Assert.assertNull( domainNumericPathMap.numericAction );
		Assert.assertNull( domainNumericPathMap.wildcardAction );

		accountPathMap = domainNumericPathMap.pathMap.get( "account" );
		Assert.assertNotNull( accountPathMap );

		Assert.assertEquals( 2, accountPathMap.pathMap.size() );
		Assert.assertNotNull( accountPathMap.numericMap );
		Assert.assertNull( accountPathMap.numericAction );
		Assert.assertNotNull( accountPathMap.wildcardAction );

		accountNumericPathMap = accountPathMap.numericMap;
		Assert.assertNotNull( accountNumericPathMap );

		Assert.assertEquals( 1, accountNumericPathMap.pathMap.size() );
		Assert.assertNull( accountNumericPathMap.numericMap );
		Assert.assertNull( accountNumericPathMap.numericAction );
		Assert.assertNull( accountNumericPathMap.wildcardAction );

		Assert.assertEquals( 1, pathMap.pathMap.size() );
		Assert.assertNull( pathMap.numericMap );
		Assert.assertEquals( new Integer( 0 ), pathMap.numericAction );
		Assert.assertNull( pathMap.wildcardAction );

		/*
		 * add() /path.
		 */

		pathMap = new PathMap<Integer>();

		Assert.assertEquals( 0, pathMap.pathMap.size() );
		Assert.assertNull( pathMap.numericMap );
		Assert.assertNull( pathMap.numericAction );
		Assert.assertNull( pathMap.wildcardAction );

		// Invalid.
		pathMap.add( null, -1 );

		Assert.assertEquals( 0, pathMap.pathMap.size() );
		Assert.assertNull( pathMap.numericMap );
		Assert.assertNull( pathMap.numericAction );
		Assert.assertNull( pathMap.wildcardAction );

		// Root.
		pathMap.add( "/", 0 );

		Assert.assertEquals( 0, pathMap.pathMap.size() );
		Assert.assertNull( pathMap.numericMap );
		Assert.assertEquals( new Integer( 0 ), pathMap.numericAction );
		Assert.assertNull( pathMap.wildcardAction );

		pathMap.add( "/domain/list/", 1 );

		Assert.assertEquals( 1, pathMap.pathMap.size() );
		Assert.assertNull( pathMap.numericMap );
		Assert.assertEquals( new Integer( 0 ), pathMap.numericAction );
		Assert.assertNull( pathMap.wildcardAction );

		domainPathMap = pathMap.pathMap.get( "domain" );
		Assert.assertNotNull( domainPathMap );

		Assert.assertEquals( 1, domainPathMap.pathMap.size() );
		Assert.assertNull( domainPathMap.numericMap );
		Assert.assertNull( domainPathMap.numericAction );
		Assert.assertNull( domainPathMap.wildcardAction );

		pathMap.add( "/domain/add/", 2 );

		Assert.assertEquals( 2, domainPathMap.pathMap.size() );
		Assert.assertNull( domainPathMap.numericMap );
		Assert.assertNull( domainPathMap.numericAction );
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
		Assert.assertNull( domainPathMap.numericAction );
		Assert.assertNull( domainPathMap.wildcardAction );

		domainNumericPathMap = domainPathMap.numericMap;
		Assert.assertNotNull( domainNumericPathMap );

		Assert.assertEquals( 2, domainNumericPathMap.pathMap.size() );
		Assert.assertNull( domainNumericPathMap.numericMap );
		Assert.assertNull( domainNumericPathMap.numericAction );
		Assert.assertNull( domainNumericPathMap.wildcardAction );

		accountPathMap = domainNumericPathMap.pathMap.get( "account" );
		Assert.assertNotNull( accountPathMap );

		Assert.assertEquals( 2, accountPathMap.pathMap.size() );
		Assert.assertNotNull( accountPathMap.numericMap );
		Assert.assertNull( accountPathMap.numericAction );
		Assert.assertNotNull( accountPathMap.wildcardAction );

		accountNumericPathMap = accountPathMap.numericMap;
		Assert.assertNotNull( accountNumericPathMap );

		Assert.assertEquals( 1, accountNumericPathMap.pathMap.size() );
		Assert.assertNull( accountNumericPathMap.numericMap );
		Assert.assertNull( accountNumericPathMap.numericAction );
		Assert.assertNull( accountNumericPathMap.wildcardAction );

		Assert.assertEquals( 1, pathMap.pathMap.size() );
		Assert.assertNull( pathMap.numericMap );
		Assert.assertEquals( new Integer( 0 ), pathMap.numericAction );
		Assert.assertNull( pathMap.wildcardAction );

		/*
		 * get().
		 */

		Integer action;
		List<Integer> numerics = new ArrayList<Integer>();

		action = pathMap.get( null, null );
		Assert.assertNull( action );
		Assert.assertEquals( 0, numerics.size() );

		action = pathMap.get( "/", numerics );
		//System.out.println( action );
		//printarray( numerics );
		Assert.assertEquals( new Integer( 0 ), action );
		Assert.assertEquals( 0, numerics.size() );

		action = pathMap.get( "/domain/list/", numerics );
		//System.out.println( action );
		//printarray( numerics );
		Assert.assertEquals( new Integer( 1 ), action );
		Assert.assertEquals( 0, numerics.size() );

		action = pathMap.get( "/domain/add/", numerics );
		//System.out.println( action );
		//printarray( numerics );
		Assert.assertEquals( new Integer( 2 ), action );
		Assert.assertEquals( 0, numerics.size() );

		action = pathMap.get( "/domain/42/edit/", numerics );
		//System.out.println( action );
		//printarray( numerics );
		Assert.assertEquals( new Integer( 3 ), action );
		Assert.assertEquals( 1, numerics.size() );
		Assert.assertEquals( new Integer( 42 ), numerics.get( 0 ) );

		action = pathMap.get( "/domain/43/account/list/", numerics );
		//System.out.println( action );
		//printarray( numerics );
		Assert.assertEquals( new Integer( 4 ), action );
		Assert.assertEquals( 1, numerics.size() );
		Assert.assertEquals( new Integer( 43 ), numerics.get( 0 ) );

		action = pathMap.get( "/domain/44/account/add/", numerics );
		//System.out.println( action );
		//printarray( numerics );
		Assert.assertEquals( new Integer( 5 ), action );
		Assert.assertEquals( 1, numerics.size() );
		Assert.assertEquals( new Integer( 44 ), numerics.get( 0 ) );

		action = pathMap.get( "/domain/45/account/46/edit/", numerics );
		//System.out.println( action );
		//printarray( numerics );
		Assert.assertEquals( new Integer( 6 ), action );
		Assert.assertEquals( 2, numerics.size() );
		Assert.assertEquals( new Integer( 45 ), numerics.get( 0 ) );
		Assert.assertEquals( new Integer( 46 ), numerics.get( 1 ) );

		action = pathMap.get( "/domain/45/account/fortytwo/", numerics );
		//System.out.println( action );
		//printarray( numerics );
		Assert.assertEquals( new Integer( 7 ), action );
		Assert.assertEquals( 1, numerics.size() );
		Assert.assertEquals( new Integer( 45 ), numerics.get( 0 ) );
	}

	public static void printarray(List<Integer> numerics) {
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
