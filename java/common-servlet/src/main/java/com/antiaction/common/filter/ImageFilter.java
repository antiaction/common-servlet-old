/*
 * Image Filter (Automatic scaling).
 * Copyright (C) 2005  Nicholas Clarke
 *
 */

/*
 * History:
 *
 * 16-Mar-2005 : First implementation.
 * 19-Mar-2005 : Javadoc.
 *
 */

package com.antiaction.common.filter;

import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
/*
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
*/
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Image Filter (Automatic scaling).
 *
 * @version 1.00
 * @author Nicholas Clarke <mayhem[at]antiaction[dot]com>
 */
public class ImageFilter implements Filter {

	/** FilterConfig used to obtaint ServletContext. */
	private FilterConfig filterConfig;

	public void init(FilterConfig filterConfig) throws ServletException {
		this.filterConfig = filterConfig;
	}

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse resp = (HttpServletResponse)response;
		ServletContext servletContext = filterConfig.getServletContext();

		String pxStr = request.getParameter( "px" );
		if ( pxStr != null && pxStr.length() != 0 ) {
			int px = 0;
			try {
				px = Integer.parseInt( pxStr );

				String contextPath = req.getContextPath();
				String servletPath = req.getServletPath();
				String pathInfo = req.getPathInfo();

				String path = contextPath + servletPath + pathInfo;
				String realPath = servletContext.getRealPath( path );

				int slashIdx = path.lastIndexOf( "/" );
				String fileStr = null;
				String extStr = null;
				if ( slashIdx != -1 ) {
					fileStr = path.substring( slashIdx + 1, path.length() );
				}
				if ( fileStr != null ) {
					extStr = servletContext.getMimeType( fileStr );
				}

				// debug
				//System.out.println( path );
				//System.out.println( realPath );

				//String outfile = path.substring( idx + 1, path.length() );
				//System.out.println( outfile + ".1" );

				String outfile = realPath + "-" + px;

				File outFile = new File( outfile );
				if ( !outFile.exists() ) {
					scaleImage( realPath, px, px, outfile, 75 );
				}

				byte[] bytes = null;
				bytes = new byte[ 65536 ];

				if ( extStr != null ) {
					resp.setContentType( extStr );
				}

				ServletOutputStream out = resp.getOutputStream();
				InputStream is = new BufferedInputStream( new FileInputStream( outfile ) );
				int len;
				while ( ( len = is.read( bytes, 0, 65536 ) ) != -1 ) {
					out.write( bytes, 0, len );
				}
				is.close();

				out.flush();
				out.close();
				return;
			}
			catch (NumberFormatException e) {
			}
			System.out.println( pxStr );
		}

		chain.doFilter( request, response );
	}

	/**
	 * Scales a source image to a destination image.
	 * @param infile source image filename.
	 * @param destWidth destination width.
	 * @param destHeight destination height.
	 * @param outfile destination image filename.
	 * @param quality encoding quality (0-100).
	 */
	private synchronized void scaleImage(String infile, int destWidth, int destHeight, String outfile, int quality) {
		/*
		 * Load source image.
		 */
		Image image = Toolkit.getDefaultToolkit().getImage( infile );
		MediaTracker mediaTracker = new MediaTracker( new Container() );
		mediaTracker.addImage( image, 0 );
		try {
			mediaTracker.waitForID( 0 );
		}
		catch (InterruptedException e) {
		}

		/*
		 * determine destination size.
		 */
		int imageWidth = image.getWidth( null );
		int imageHeight = image.getHeight( null );
		double widthRatio = (double)destWidth / (double)imageWidth;
		double heightRatio = (double)destHeight / (double)imageHeight;

		long tmpW, tmpH;
		tmpW = Math.round((double)imageWidth * widthRatio);
		tmpH = Math.round((double)imageHeight * widthRatio);
		if ( tmpW > destWidth || tmpH > destHeight ) {
			tmpW = Math.round((double)imageWidth * heightRatio);
			tmpH = Math.round((double)imageHeight * heightRatio);
		}
		destWidth = (int)tmpW;
		destHeight = (int)tmpH;

		/*
		 * Draw original image to destination image object and
		 * scale it to the new size on-the-fly.
		 */
		BufferedImage destImage = new BufferedImage( destWidth, destHeight, BufferedImage.TYPE_INT_RGB );
		Graphics2D graphics2D = destImage.createGraphics();
		//graphics2D.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR );
		graphics2D.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC );
		graphics2D.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
		graphics2D.drawImage( image, 0, 0, destWidth, destHeight, null );

		/*
		 * Save destination image.
		 */
		try {
			BufferedOutputStream out = new BufferedOutputStream( new FileOutputStream( outfile ) );
			/*
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder( out );
			JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam( destImage );
		    quality = Math.max( 0, Math.min( quality, 100 ) );
			param.setQuality( (float)quality / 100.0f, false );
			encoder.setJPEGEncodeParam( param );
			encoder.encode( destImage );
			*/
			/*
		    ImageIO.write(destImage, "jpg", out);
		    */
		    ImageOutputStream  ios =  ImageIO.createImageOutputStream( out );
		    Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName( "jpeg" );
		    ImageWriter writer = iter.next();
		    ImageWriteParam iwp = writer.getDefaultWriteParam();
		    iwp.setCompressionMode( ImageWriteParam.MODE_EXPLICIT );
		    quality = Math.max( 0, Math.min( quality, 100 ) );
		    iwp.setCompressionQuality( (float)quality / 100.0f );
		    writer.setOutput( ios );
		    writer.write( null, new IIOImage( destImage, null, null), iwp );
		    writer.dispose();
			out.close(); 
		}
		catch (FileNotFoundException e) {
			System.out.println( e );
		}
		catch (IOException e) {
			System.out.println( e );
		}

		/*
		 * Cleanup.
		 */
		graphics2D.dispose();
		graphics2D = null;
		image.flush();
		image = null;
		destImage.flush();
		destImage = null;
	}

}
