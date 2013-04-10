/*
 * Created on 03/04/2013
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.filter.fileupload;

import java.io.IOException;
import java.io.InputStream;

/**
 * Implements a simple managed byte buffer that can be used for reading and/or writing.
 * A JDK1.4+ ByteBuffer implementation for older JDK versions.
 *
 * @author Nicholas
 */
public class ByteBuffer {

	protected byte[] buffer;

	protected int capacity;

	protected int position;

	protected int limit;

	protected int remaining;

	protected ByteBuffer(byte[] buffer) {
		this.buffer = buffer;
		capacity = buffer.length;
		position = 0;
		limit = capacity;
		remaining = capacity;
	}

	public static ByteBuffer allocate(int buffer_size) {
		return new ByteBuffer( new byte[ buffer_size ] );
	}

	public static ByteBuffer wrap(byte[] buffer) {
		return new ByteBuffer( buffer );
	}

	public byte[] array() {
		return buffer;
	}

	public int capacity() {
		return capacity;
	}

	public int position() {
		return position;
	}

	public int limit() {
		return limit;
	}

	public int remaining() {
		return remaining;
	}

	public boolean hasRemaining() {
		return (position < limit);
	}

	public ByteBuffer clear() {
		position = 0;
		limit = capacity;
		remaining = capacity;
		return this;
	}

	public ByteBuffer rewind() {
		position = 0;
		remaining = limit;
		return this;
	}

	public ByteBuffer position(int newPosition) {
		if ( newPosition < 0 || newPosition > limit ) {
			throw new IllegalArgumentException();
		}
		position = newPosition;
		remaining = limit - position;
		return this;
	}

	public ByteBuffer limit(int newLimit) {
		if ( newLimit < 0 || newLimit > capacity ) {
			throw new IllegalArgumentException();
		}
		limit = newLimit;
		if ( position > limit ) {
			position = limit;
		}
		remaining = limit - position;
		return this;
	}

	public ByteBuffer flip() {
		limit = position;
		position = 0;
		remaining = limit;
		return this;
	}

	public ByteBuffer compact() {
		if ( position > 0 ) {
			for ( int i=0; i<remaining; ++i ) {
				buffer[ i ] = buffer[ position++ ];
			}
		}
		position = remaining;
		limit = capacity;
		remaining = limit - position;
		return this;
	}

	public int read(InputStream in) throws IOException {
		int read = 0;
		int lastRead = 0;
		while ( remaining > 0 && (lastRead = in.read( buffer, position, remaining )) != -1) {
			position += lastRead;
			remaining -= lastRead;
			read += lastRead;
		}
		if ( read == 0 ) {
			read = lastRead;
		}
		return read;
	}

}
