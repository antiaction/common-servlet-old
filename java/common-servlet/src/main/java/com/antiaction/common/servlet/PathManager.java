package com.antiaction.common.servlet;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import com.antiaction.common.servlet.annotations.DELETE;
import com.antiaction.common.servlet.annotations.GET;
import com.antiaction.common.servlet.annotations.POST;
import com.antiaction.common.servlet.annotations.PUT;
import com.antiaction.common.servlet.annotations.Path;
import com.antiaction.common.servlet.annotations.Produces;

public class PathManager {

	public static final int M_GET = 1<<0;
	public static final int M_POST = 1<<1;
	public static final int M_PUT = 1<<2;
	public static final int M_DELETE = 1<<3;

	protected PathMap<Resource> pathMap = new PathMap<Resource>();

	public PathManager() {
	}

	public void register (Class<?> cls, Object obj) {
		GET getAnnotation;
		POST postAnnotation;
		PUT putAnnotation;
		DELETE deleteAnnotation;
		Path pathAnnotation;
		String resourcePathStr;
		String methodPathStr;
		Produces producesAnnotation;
		String producesStr;
		int method_bf;
		int modifiers;

		pathAnnotation = cls.getAnnotation( Path.class );
		if ( pathAnnotation != null ) {
			resourcePathStr = pathAnnotation.value();
			if ( resourcePathStr != null && resourcePathStr.length() > 0 ) {
				if ( resourcePathStr.startsWith( "/" ) ) {
					if ( resourcePathStr.endsWith( "/" ) ) {
						resourcePathStr = resourcePathStr.substring( 0, resourcePathStr.length() - 1 );
					}
					// debug
					System.out.println( resourcePathStr );
					Method[] methods = cls.getMethods();
					Method method;
					for ( int i=0; i<methods.length; ++i ) {
						method = methods[ i ];
						method_bf = 0;
						getAnnotation = method.getAnnotation( GET.class );
						if ( getAnnotation != null ) {
							method_bf |= M_GET;
						}
						postAnnotation = method.getAnnotation( POST.class );
						if ( postAnnotation != null ) {
							method_bf |= M_POST;
						}
						putAnnotation = method.getAnnotation( PUT.class );
						if ( putAnnotation != null ) {
							method_bf |= M_PUT;
						}
						deleteAnnotation = method.getAnnotation( DELETE.class );
						if ( deleteAnnotation != null ) {
							method_bf |= M_DELETE;
						}

						if ( method_bf != 0 ) {
							pathAnnotation = method.getAnnotation( Path.class );
							if ( pathAnnotation != null ) {
								methodPathStr = pathAnnotation.value();
							}
							else {
								methodPathStr = null;
							}
							producesAnnotation = method.getAnnotation( Produces.class );
							if ( producesAnnotation != null ) {
								producesStr = producesAnnotation.value();
							}
							else {
								producesStr = null;
							}
							if ( methodPathStr != null && methodPathStr.length() > 0 ) {
								if ( methodPathStr.startsWith( "/" ) ) {
									methodPathStr = resourcePathStr + methodPathStr;
								}
								else {
									methodPathStr = resourcePathStr + "/" + methodPathStr;
								}
							}

							modifiers = method.getModifiers();
							if ( !Modifier.isAbstract( modifiers ) ) {
								Resource res = new Resource();
								res.methodPath = methodPathStr;
								res.produces = producesStr;
								res.cls = cls;
								res.obj = obj;
								res.method = method;
								res.isStatic = Modifier.isStatic( modifiers );

								pathMap.add( methodPathStr, res );

								// debug
								System.out.println( method_bf );
								System.out.println( method.getName() );
								System.out.println( methodPathStr );
								System.out.println( producesStr );

								Class<?>[] paramTypes = method.getParameterTypes();
								Annotation[][] paramAnnotationsArr = method.getParameterAnnotations();
							}
						}
					}
				}
			}
		}
	}

	public Resource getResource(String pathStr, List<Long> longList, List<String> stringList) {
		return pathMap.get( pathStr, longList, stringList );
	}

	public static class Resource {
		public String methodPath;
		public String produces;
		public Class<?> cls;
		public Object obj;
		public Method method;
		public boolean isStatic;
	}

}
