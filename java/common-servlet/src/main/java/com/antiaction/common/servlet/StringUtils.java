/*
 * Strings.
 * Copyright (C) 2002, 2003, 2005  Nicholas Clarke
 *
 */

/*
 * History:
 *
 * 15-Mar-2002 : First implementation. (Check AString/VString in package)
 * 23-Oct-2002 : Added some simple join methods.
 * 23-Oct-2002 : Changed ArrayList to List.
 * 21-Jun-2003 : Moved to antiaction.com package.
 * 20-Mar-2005 : Added replaceString method.
 *
 */

package com.antiaction.common.servlet;

import java.util.List;
import java.util.ArrayList;

/**
 * Strings.
 *
 * @version 1.00
 * @author Nicholas Clarke <mayhem[at]antiaction[dot]com>
 */
public class StringUtils {

	/**
	 * Static class.
	 */
	private StringUtils() {
	}

	/**
	 * Splits a string by its separating spaces, each token ending up an
	 * element in the ArrayList.
	 * @param str String to split.
	 * @return ArrayList with tokens split by spaces.
	 */
	public static List splitSpace(String str) {
		int cIdx = 0;
		int fIdx = 0;
		int lIdx = 0;
		List tmpArr = new ArrayList();
		char c;

		if ( ( str == null) || ( str.length() == 0 ) ) {
			return tmpArr;
		}

		boolean b = true;
		while ( b ) {
			if ( cIdx < str.length() ) {
				c = str.charAt( cIdx );
				if ( c == ' ' ) {
					if ( fIdx < lIdx ) {
						tmpArr.add( str.substring( fIdx, lIdx ) );
						fIdx = lIdx;
					}
				}
				else {
					if ( fIdx == lIdx ) {
						fIdx = cIdx;
						lIdx = cIdx + 1;
					}
					else {
						++lIdx;
					}
				}
				++cIdx;
			}
			else {
				if ( fIdx < lIdx ) {
					tmpArr.add( str.substring( fIdx, lIdx ) );
				}
				b = false;
			}
		}

		return tmpArr;
	}

	/**
	 * Splits a string according to a substring and returns an arraylist where each
	 * item represents a string delimited by the substring or null if emptry string.
	 * @param str string to split.
	 * @param dstr substring used to split the string.
	 * @return arraylist where each entry represents a string delimited by the subitem or null if emptry string.
	 */
	public static List splitString(String str, String dstr) {
		List tmpArr = null;
		int prevIndex = 0;
		int currIndex = 0;

		if ( (str == null) || (dstr == null) ) {
			return null;
		}
		else {
			tmpArr = new ArrayList(16);
			if ( (str.length() == 0) || (dstr.length() == 0) ) {
				tmpArr.add(str);
			}
			else {
				while ( prevIndex != -1 ) {
					currIndex = str.indexOf(dstr, prevIndex);
					if ( currIndex == -1 ) {
						tmpArr.add( str.substring(prevIndex, str.length()) );
					}
					else {
						tmpArr.add( str.substring(prevIndex, currIndex) );
						currIndex += dstr.length();
					}
					prevIndex = currIndex;
				}
			}
		}
		return tmpArr;
	}

	/*
	 * Splits a string according to a substring and returns an arraylist where each
	 * item represents a string delimited by the substring or null if emptry string.
	 * Substrings are trimmed.
	 * @param str string to split.
	 * @param dstr substring used to split the string.
	 * @return arraylist where each entry represents a string delimited by the subitem or null if emptry string.
	 */
/*	public List splitTrim(String str, String dstr) {
		List tmpArr = null;
		String tmpStr = null;
		int prevIndex = 0;
		int currIndex = 0;

		if ( (str == null) || (dstr == null) ) {
			return null;
		}
		else {
			tmpArr = new ArrayList(16);
			if ( (str.length() == 0) || (dstr.length() == 0) ) {
				tmpArr.add(str);
			}
			else {
				while ( prevIndex != -1 ) {
					currIndex = str.indexOf(dstr, prevIndex);
					if ( currIndex == -1 ) {
						tmpArr.add( str.substring(prevIndex, str.length() ).trim() );
					}
					else {
						tmpArr.add( str.substring(prevIndex, currIndex).trim() );
						currIndex += dstr.length();
					}
					prevIndex = currIndex;
				}
			}
		}
		return tmpArr;
	}
*/

	public static String joinArray(List lst) {
		String tmpStr = "";
		if ( lst == null ) {
			throw new NullPointerException();
		}
		for (int i=0; i<lst.size(); ++i) {
			tmpStr += (String)lst.get( i );
		}
		return tmpStr;
	}

	public static String joinArrayPre(List lst, String str) {
		String tmpStr = "";
		if ( ( lst == null ) || ( str == null ) ) {
			throw new NullPointerException();
		}
		if ( lst.isEmpty() ) {
			return tmpStr;
		}
		if ( str == null ) {
			return joinArray( lst );
		}
		for(int i=0; i<lst.size(); ++i) {
			tmpStr += str + (String)lst.get( i );
		}
		return tmpStr;
	}

	public static String joinArrayPost(List lst, String str) {
		String tmpStr = "";
		if ( ( lst == null ) || ( str == null ) ) {
			throw new NullPointerException();
		}
		if ( lst.isEmpty() ) {
			return tmpStr;
		}
		if ( str == null ) {
			return joinArray( lst );
		}
		for(int i=0; i<lst.size(); ++i) {
			tmpStr += (String)lst.get( i ) + str;
		}
		return tmpStr;
	}

	public static byte[] asciiToHex = {
		-1, -1, -1, -1, -1, -1, -1, -1,			// 0
		-1, -1, -1, -1, -1, -1, -1, -1,			// 8
		-1, -1, -1, -1, -1, -1, -1, -1,			// 16
		-1, -1, -1, -1, -1, -1, -1, -1,			// 24
		-1, -1, -1, -1, -1, -1, -1, -1,			// 32
		-1, -1, -1, -1, -1, -1, -1, -1,			// 40
		0, 1, 2, 3, 4, 5, 6, 7,					// 48
		8, 9, -1, -1, -1, -1, -1, -1,			// 54
		-1, 10, 11, 12, 13, 14, 15, -1,			// 64
		-1, -1, -1, -1, -1, -1, -1, -1,			// 72
		-1, -1, -1, -1, -1, -1, -1, -1,			// 80
		-1, -1, -1, -1, -1, -1, -1, -1,			// 88
		-1, 10, 11, 12, 13, 14, 15, -1,			// 96
		-1, -1, -1, -1, -1, -1, -1, -1,			// 104
		-1, -1, -1, -1, -1, -1, -1, -1,			// 112
		-1, -1, -1, -1, -1, -1, -1, -1,			// 120
	};

	public static char asciiHex(char c1, char c2) {
		if ( ( c1 < 128 ) && ( c2 < 128 ) ) {
			c1 = (char)asciiToHex[ c1 ];
			c2 = (char)asciiToHex[ c2 ];
			if ( ( c1 < 128 ) && ( c2 < 128 ) ) {
				return (char)(( c1 << 4 ) + c2);
			}
		}
		return 0xffff;
	}

	public static int skipWhitespace(String str, int idx) {
		if ( idx < 0 ) {
			return idx;
		}
		boolean b = true;
		while ( b ) {
			if ( idx < str.length() ) {
				if ( Character.isWhitespace( str.charAt( idx ) ) ) {
					++idx;
				}
				else {
					b = false;
				}
			}
			else {
				b = false;
			}
		}
		return idx;
	}

	public static int skipNonWhitespace(String str, int idx) {
		if ( idx < 0 ) {
			return idx;
		}
		boolean b = true;
		while ( b ) {
			if ( idx < str.length() ) {
				if ( !Character.isWhitespace( str.charAt( idx ) ) ) {
					++idx;
				}
				else {
					b = false;
				}
			}
			else {
				b = false;
			}
		}
		return idx;
	}

	public static int skipDigit(String str, int idx) {
		if ( idx < 0 ) {
			return idx;
		}
		boolean b = true;
		while ( b ) {
			if ( idx < str.length() ) {
				if ( Character.isDigit( str.charAt( idx ) ) ) {
					++idx;
				}
				else {
					b = false;
				}
			}
			else {
				b = false;
			}
		}
		return idx;
	}

	public static void replaceString(StringBuffer strBuf, String substring, String replacement) {
		if ( strBuf == null ) {
			throw new NullPointerException();
		}
		if ( strBuf.length() == 0 ) {
			return;
		}
		int idx = 0;
		while ( ( idx = strBuf.indexOf( substring, idx  ) ) != -1 ) {
			strBuf.delete( idx, idx + substring.length() );
			strBuf.insert( idx, replacement );
			idx += replacement.length();
		}
	}

}
