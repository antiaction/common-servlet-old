/*
 * Created on 04/04/2013
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.antiaction.common.filter.fileupload;

import java.security.SecureRandom;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TestByteBuffer {

	@Test
	public void test_bytebuffer() {
		ByteBuffer byteBuffer;
		ByteBuffer byteBuffer2;

        SecureRandom random = new SecureRandom();

        /*
		 * allocate().
		 */

		byteBuffer = ByteBuffer.allocate( 1024 );
		Assert.assertNotNull( byteBuffer );
		Assert.assertEquals( byteBuffer.buffer, byteBuffer.array() );

		byte[] buffer = byteBuffer.array();
		Assert.assertNotNull( buffer );
		Assert.assertEquals( 1024, buffer.length );

		Assert.assertEquals( 1024, byteBuffer.capacity );
		Assert.assertEquals( byteBuffer.capacity, byteBuffer.capacity() );

		Assert.assertEquals( 0, byteBuffer.position );
		Assert.assertEquals( byteBuffer.position, byteBuffer.position() );
		Assert.assertEquals( 1024, byteBuffer.limit );
		Assert.assertEquals( byteBuffer.limit, byteBuffer.limit() );
		Assert.assertEquals( 1024 - 0, byteBuffer.remaining );
		Assert.assertEquals( byteBuffer.remaining, byteBuffer.remaining() );
		Assert.assertTrue( byteBuffer.hasRemaining() );

		/*
		 * wrap().
		 */

		buffer = new byte[ 2048 ];

		byteBuffer = ByteBuffer.wrap( buffer );
		Assert.assertNotNull( byteBuffer );
		Assert.assertEquals( byteBuffer.buffer, byteBuffer.array() );
		Assert.assertEquals( buffer, byteBuffer.buffer );
		Assert.assertEquals( 2048, byteBuffer.buffer.length );

		Assert.assertEquals( 2048, byteBuffer.capacity );
		Assert.assertEquals( byteBuffer.capacity, byteBuffer.capacity() );

		Assert.assertEquals( 0, byteBuffer.position );
		Assert.assertEquals( byteBuffer.position, byteBuffer.position() );
		Assert.assertEquals( 2048, byteBuffer.limit );
		Assert.assertEquals( byteBuffer.limit, byteBuffer.limit() );
		Assert.assertEquals( 2048 - 0, byteBuffer.remaining );
		Assert.assertEquals( byteBuffer.remaining, byteBuffer.remaining() );
		Assert.assertTrue( byteBuffer.hasRemaining() );

		/*
		 * Misc.
		 */

		byteBuffer2 = byteBuffer.clear();
		Assert.assertEquals( byteBuffer, byteBuffer2 );

		Assert.assertEquals( 0, byteBuffer.position );
		Assert.assertEquals( byteBuffer.position, byteBuffer.position() );
		Assert.assertEquals( 2048, byteBuffer.limit );
		Assert.assertEquals( byteBuffer.limit, byteBuffer.limit() );
		Assert.assertEquals( 2048 - 0, byteBuffer.remaining );
		Assert.assertEquals( byteBuffer.remaining, byteBuffer.remaining() );
		Assert.assertTrue( byteBuffer.hasRemaining() );

		byteBuffer2 = byteBuffer.position( 768 );
		Assert.assertEquals( byteBuffer, byteBuffer2 );

		try {
			byteBuffer.position( -1 );
			Assert.fail( "Exception expected!" );
		}
		catch (IllegalArgumentException e) {
		}
		try {
			byteBuffer.position( 2049 );
			Assert.fail( "Exception expected!" );
		}
		catch (IllegalArgumentException e) {
		}

		Assert.assertEquals( 768, byteBuffer.position );
		Assert.assertEquals( byteBuffer.position, byteBuffer.position() );
		Assert.assertEquals( 2048, byteBuffer.limit );
		Assert.assertEquals( byteBuffer.limit, byteBuffer.limit() );
		Assert.assertEquals( 2048 - 768, byteBuffer.remaining );
		Assert.assertEquals( byteBuffer.remaining, byteBuffer.remaining() );
		Assert.assertTrue( byteBuffer.hasRemaining() );

		byteBuffer2 = byteBuffer.limit( 1280 );
		Assert.assertEquals( byteBuffer, byteBuffer2 );

		try {
			byteBuffer.position( 1281 );
			Assert.fail( "Exception expected!" );
		}
		catch (IllegalArgumentException e) {
		}

		Assert.assertEquals( 768, byteBuffer.position );
		Assert.assertEquals( byteBuffer.position, byteBuffer.position() );
		Assert.assertEquals( 1280, byteBuffer.limit );
		Assert.assertEquals( byteBuffer.limit, byteBuffer.limit() );
		Assert.assertEquals( 1280 - 768, byteBuffer.remaining );
		Assert.assertEquals( byteBuffer.remaining, byteBuffer.remaining() );
		Assert.assertTrue( byteBuffer.hasRemaining() );

		byteBuffer2 = byteBuffer.position( 1280 );
		Assert.assertEquals( byteBuffer, byteBuffer2 );

		try {
			byteBuffer.limit( -1 );
			Assert.fail( "Exception expected!" );
		}
		catch (IllegalArgumentException e) {
		}
		try {
			byteBuffer.limit( 2049 );
			Assert.fail( "Exception expected!" );
		}
		catch (IllegalArgumentException e) {
		}

		Assert.assertEquals( 1280, byteBuffer.position );
		Assert.assertEquals( byteBuffer.position, byteBuffer.position() );
		Assert.assertEquals( 1280, byteBuffer.limit );
		Assert.assertEquals( byteBuffer.limit, byteBuffer.limit() );
		Assert.assertEquals( 1280 - 1280, byteBuffer.remaining );
		Assert.assertEquals( byteBuffer.remaining, byteBuffer.remaining() );
		Assert.assertFalse( byteBuffer.hasRemaining() );

		byteBuffer2 = byteBuffer.limit( 768 );
		Assert.assertEquals( byteBuffer, byteBuffer2 );

		Assert.assertEquals( 768, byteBuffer.position );
		Assert.assertEquals( byteBuffer.position, byteBuffer.position() );
		Assert.assertEquals( 768, byteBuffer.limit );
		Assert.assertEquals( byteBuffer.limit, byteBuffer.limit() );
		Assert.assertEquals( 768 - 768, byteBuffer.remaining );
		Assert.assertEquals( byteBuffer.remaining, byteBuffer.remaining() );
		Assert.assertFalse( byteBuffer.hasRemaining() );

		byteBuffer2 = byteBuffer.rewind();
		Assert.assertEquals( byteBuffer, byteBuffer2 );

		Assert.assertEquals( 0, byteBuffer.position );
		Assert.assertEquals( byteBuffer.position, byteBuffer.position() );
		Assert.assertEquals( 768, byteBuffer.limit );
		Assert.assertEquals( byteBuffer.limit, byteBuffer.limit() );
		Assert.assertEquals( 768 - 0, byteBuffer.remaining );
		Assert.assertEquals( byteBuffer.remaining, byteBuffer.remaining() );
		Assert.assertTrue( byteBuffer.hasRemaining() );

		byteBuffer2 = byteBuffer.flip();
		Assert.assertEquals( byteBuffer, byteBuffer2 );

		Assert.assertEquals( 0, byteBuffer.position );
		Assert.assertEquals( byteBuffer.position, byteBuffer.position() );
		Assert.assertEquals( 0, byteBuffer.limit );
		Assert.assertEquals( byteBuffer.limit, byteBuffer.limit() );
		Assert.assertEquals( 0 - 0, byteBuffer.remaining );
		Assert.assertEquals( byteBuffer.remaining, byteBuffer.remaining() );
		Assert.assertFalse( byteBuffer.hasRemaining() );

		byteBuffer2 = byteBuffer.limit( 1536 );
		Assert.assertEquals( byteBuffer, byteBuffer2 );

		Assert.assertEquals( 0, byteBuffer.position );
		Assert.assertEquals( byteBuffer.position, byteBuffer.position() );
		Assert.assertEquals( 1536, byteBuffer.limit );
		Assert.assertEquals( byteBuffer.limit, byteBuffer.limit() );
		Assert.assertEquals( 1536 - 0, byteBuffer.remaining );
		Assert.assertEquals( byteBuffer.remaining, byteBuffer.remaining() );
		Assert.assertTrue( byteBuffer.hasRemaining() );

		byteBuffer2 = byteBuffer.position( 512 );
		Assert.assertEquals( byteBuffer, byteBuffer2 );

		Assert.assertEquals( 512, byteBuffer.position );
		Assert.assertEquals( byteBuffer.position, byteBuffer.position() );
		Assert.assertEquals( 1536, byteBuffer.limit );
		Assert.assertEquals( byteBuffer.limit, byteBuffer.limit() );
		Assert.assertEquals( 1536 - 512, byteBuffer.remaining );
		Assert.assertEquals( byteBuffer.remaining, byteBuffer.remaining() );
		Assert.assertTrue( byteBuffer.hasRemaining() );

		/*
		 * flip().
		 */

		byte[] srcArr = new byte[ 2048 ];
		random.nextBytes( srcArr );
		System.arraycopy( srcArr, 0, buffer, 0, 2048 );

		byteBuffer2 = byteBuffer.flip();
		Assert.assertEquals( byteBuffer, byteBuffer2 );

		Assert.assertEquals( 0, byteBuffer.position );
		Assert.assertEquals( byteBuffer.position, byteBuffer.position() );
		Assert.assertEquals( 512, byteBuffer.limit );
		Assert.assertEquals( byteBuffer.limit, byteBuffer.limit() );
		Assert.assertEquals( 512 - 0, byteBuffer.remaining );
		Assert.assertEquals( byteBuffer.remaining, byteBuffer.remaining() );
		Assert.assertTrue( byteBuffer.hasRemaining() );

		Assert.assertArrayEquals( srcArr, buffer );

		byteBuffer2 = byteBuffer.flip();
		Assert.assertEquals( byteBuffer, byteBuffer2 );

		Assert.assertEquals( 0, byteBuffer.position );
		Assert.assertEquals( byteBuffer.position, byteBuffer.position() );
		Assert.assertEquals( 0, byteBuffer.limit );
		Assert.assertEquals( byteBuffer.limit, byteBuffer.limit() );
		Assert.assertEquals( 0 - 0, byteBuffer.remaining );
		Assert.assertEquals( byteBuffer.remaining, byteBuffer.remaining() );
		Assert.assertFalse( byteBuffer.hasRemaining() );

		Assert.assertArrayEquals( srcArr, buffer );

		/*
		 * compact().
		 */

		byteBuffer2 = byteBuffer.compact();
		Assert.assertEquals( byteBuffer, byteBuffer2 );

		Assert.assertEquals( 0, byteBuffer.position );
		Assert.assertEquals( byteBuffer.position, byteBuffer.position() );
		Assert.assertEquals( 2048, byteBuffer.limit );
		Assert.assertEquals( byteBuffer.limit, byteBuffer.limit() );
		Assert.assertEquals( 2048 - 0, byteBuffer.remaining );
		Assert.assertEquals( byteBuffer.remaining, byteBuffer.remaining() );
		Assert.assertTrue( byteBuffer.hasRemaining() );

		Assert.assertArrayEquals( srcArr, buffer );

		byteBuffer2 = byteBuffer.compact();
		Assert.assertEquals( byteBuffer, byteBuffer2 );

		Assert.assertEquals( 2048, byteBuffer.position );
		Assert.assertEquals( byteBuffer.position, byteBuffer.position() );
		Assert.assertEquals( 2048, byteBuffer.limit );
		Assert.assertEquals( byteBuffer.limit, byteBuffer.limit() );
		Assert.assertEquals( 2048 - 2048, byteBuffer.remaining );
		Assert.assertEquals( byteBuffer.remaining, byteBuffer.remaining() );
		Assert.assertFalse( byteBuffer.hasRemaining() );

		Assert.assertArrayEquals( srcArr, buffer );

		byteBuffer2 = byteBuffer.limit( 1280 );
		Assert.assertEquals( byteBuffer, byteBuffer2 );

		Assert.assertEquals( 1280, byteBuffer.position );
		Assert.assertEquals( byteBuffer.position, byteBuffer.position() );
		Assert.assertEquals( 1280, byteBuffer.limit );
		Assert.assertEquals( byteBuffer.limit, byteBuffer.limit() );
		Assert.assertEquals( 1280 - 1280, byteBuffer.remaining );
		Assert.assertEquals( byteBuffer.remaining, byteBuffer.remaining() );
		Assert.assertFalse( byteBuffer.hasRemaining() );

		byteBuffer2 = byteBuffer.position( 512 );
		Assert.assertEquals( byteBuffer, byteBuffer2 );

		Assert.assertEquals( 512, byteBuffer.position );
		Assert.assertEquals( byteBuffer.position, byteBuffer.position() );
		Assert.assertEquals( 1280, byteBuffer.limit );
		Assert.assertEquals( byteBuffer.limit, byteBuffer.limit() );
		Assert.assertEquals( 1280 - 512, byteBuffer.remaining );
		Assert.assertEquals( byteBuffer.remaining, byteBuffer.remaining() );
		Assert.assertTrue( byteBuffer.hasRemaining() );

		Assert.assertArrayEquals( srcArr, buffer );

		byteBuffer2 = byteBuffer.compact();
		Assert.assertEquals( byteBuffer, byteBuffer2 );

		Assert.assertEquals( 768, byteBuffer.position );
		Assert.assertEquals( byteBuffer.position, byteBuffer.position() );
		Assert.assertEquals( 2048, byteBuffer.limit );
		Assert.assertEquals( byteBuffer.limit, byteBuffer.limit() );
		Assert.assertEquals( 2048 - 768, byteBuffer.remaining );
		Assert.assertEquals( byteBuffer.remaining, byteBuffer.remaining() );
		Assert.assertTrue( byteBuffer.hasRemaining() );

		System.arraycopy( srcArr, 512, srcArr, 0, 768 );
		Assert.assertArrayEquals( srcArr, buffer );

		byteBuffer2 = byteBuffer.limit( 1280 );
		Assert.assertEquals( byteBuffer, byteBuffer2 );

		Assert.assertEquals( 768, byteBuffer.position );
		Assert.assertEquals( byteBuffer.position, byteBuffer.position() );
		Assert.assertEquals( 1280, byteBuffer.limit );
		Assert.assertEquals( byteBuffer.limit, byteBuffer.limit() );
		Assert.assertEquals( 1280 - 768, byteBuffer.remaining );
		Assert.assertEquals( byteBuffer.remaining, byteBuffer.remaining() );
		Assert.assertTrue( byteBuffer.hasRemaining() );

		Assert.assertArrayEquals( srcArr, buffer );

		byteBuffer2 = byteBuffer.compact();
		Assert.assertEquals( byteBuffer, byteBuffer2 );

		Assert.assertEquals( 512, byteBuffer.position );
		Assert.assertEquals( byteBuffer.position, byteBuffer.position() );
		Assert.assertEquals( 2048, byteBuffer.limit );
		Assert.assertEquals( byteBuffer.limit, byteBuffer.limit() );
		Assert.assertEquals( 2048 - 512, byteBuffer.remaining );
		Assert.assertEquals( byteBuffer.remaining, byteBuffer.remaining() );
		Assert.assertTrue( byteBuffer.hasRemaining() );

		System.arraycopy( srcArr, 768, srcArr, 0, 512 );
		Assert.assertArrayEquals( srcArr, buffer );
	}

}
