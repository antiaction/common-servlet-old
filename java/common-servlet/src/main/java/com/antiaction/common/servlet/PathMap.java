/*
 * Created on 03/08/2011
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.servlet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PathMap<T> {

	protected Map<String, PathMap<T>> pathMap;

	protected PathMap<T> numericMap;

	protected PathMap<T> stringMap;

	protected T action;

	protected T wildcardAction;

	public PathMap() {
		pathMap = new HashMap<String, PathMap<T>>();
	}

	public void add(String pathStr, T action) {
		if ( pathStr == null ) {
			return;
		}
		if ( pathStr.startsWith( "/" ) ) {
			pathStr = pathStr.substring( 1 );
		}

		PathMap<T> parent = this;
		PathMap<T> current;

		List<String> pathList = StringUtils.splitString( pathStr, "/" );
		String path;

		boolean b = true;
		int idx = 0;
		while ( b ) {
			if ( idx < pathList.size() ) {
				path = pathList.get( idx++ );
				if ( path.length() > 0 ) {
					if ( "*".equals( path ) ) {
						if ( idx < pathList.size() || parent.wildcardAction != null ) {
							throw new IllegalArgumentException( "Wildcards can only be used at the end and only once per path!" );
						}
						parent.wildcardAction = action;
					}
					else if ( "<numeric>".compareTo( path ) == 0 || ":numeric".compareTo( path ) == 0 ) {
						if ( parent.stringMap != null ) {
							throw new IllegalArgumentException( "Unsupported to have both numeric and string definitions for the same path map elements!" );
						}
						if ( parent.numericMap == null ) {
							parent.numericMap = new PathMap<T>();
						}
						parent = parent.numericMap;
					}
					else if ( "<string>".compareTo( path ) == 0 || ":string".compareTo( path ) == 0 ) {
						if ( parent.numericMap != null ) {
							throw new IllegalArgumentException( "Unsupported to have both numeric and string definitions for the same path map elements!" );
						}
						if ( parent.stringMap == null ) {
							parent.stringMap = new PathMap<T>();
						}
						parent = parent.stringMap;
					}
					else {
						current = parent.pathMap.get( path );
						if ( current == null ) {
							current = new PathMap<T>();
							parent.pathMap.put( path, current );
						}
						parent = current;
					}
				}
				else {
					if ( idx == pathList.size() ) {
						parent.action = action;
					}
					else {
						throw new IllegalArgumentException();
					}
				}
			}
			else {
				b = false;
			}
		}
	}

	public T get(String pathStr, List<Long> longList, List<String> stringList) {
		if ( pathStr == null ) {
			return null;
		}
		if ( pathStr.startsWith( "/" ) ) {
			pathStr = pathStr.substring( 1 );
		}

		T action = null;

		PathMap<T> parent = this;
		PathMap<T> current;

		List<String> pathList = StringUtils.splitString( pathStr, "/" );
		String path;

		longList.clear();
		stringList.clear();

		boolean b = true;
		int idx = 0;
		while ( b ) {
			if ( idx < pathList.size() ) {
				path = pathList.get( idx++ );
				if ( path.length() > 0 ) {
					current = parent.pathMap.get( path );
					if ( current != null ) {
						parent = current;
					}
					else {
						if ( parent.numericMap != null ) {
							try {
								long numeric = Long.parseLong( path );
								longList.add( numeric );
								parent = parent.numericMap;
							}
							catch (NumberFormatException e) {
								b = false;
							}
						}
						else if ( parent.stringMap != null ) {
							stringList.add( path );
							parent = parent.stringMap;
						}
						else {
							b = false;
						}
						if ( !b && parent.wildcardAction != null ) {
							action = parent.wildcardAction;
						}
					}
				}
				else {
					if ( idx == pathList.size() ) {
						action = parent.action;
						if ( action == null) {
							action = parent.wildcardAction;
						}
					}
					else {
						// debug
						System.out.println( "Invalid path: " + pathStr );
					}
				}
			}
			else {
				b = false;
			}
		}
		return action;
	}

}
