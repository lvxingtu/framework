/* *************************************************************************
 
   								Millstone(TM) 
   				   Open Sourced User Interface Library for
   		 		       Internet Development with Java

             Millstone is a registered trademark of IT Mill Ltd
                  Copyright (C) 2000-2005 IT Mill Ltd
                     
   *************************************************************************

   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   license version 2.1 as published by the Free Software Foundation.

   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Lesser General Public License for more details.

   You should have received a copy of the GNU Lesser General Public
   License along with this library; if not, write to the Free Software
   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

   *************************************************************************
   
   For more information, contact:
   
   IT Mill Ltd                           phone: +358 2 4802 7180
   Ruukinkatu 2-4                        fax:  +358 2 4802 7181
   20540, Turku                          email: info@itmill.com
   Finland                               company www: www.itmill.com
   
   Primary source for MillStone information and releases: www.millstone.org

   ********************************************************************** */

package com.enably.tk.terminal;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/** Downloadable stream.
 *
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */
public class DownloadStream {

	/** Maximum cache time. */
	public static final long MAX_CACHETIME = Long.MAX_VALUE;
	
	/** Default cache time. */
	public static final long DEFAULT_CACHETIME = 1000*60*60*24;
	
	private InputStream stream;
	private String contentType;
	private String fileName;
	private Map params;
	private long cacheTime = DEFAULT_CACHETIME;
	private int bufferSize = 0;

	/** Creates a new instance of DownloadStream */
	public DownloadStream(
		InputStream stream,
		String contentType,
		String fileName) {
		setStream(stream);
		setContentType(contentType);
		setFileName(fileName);
	}

	/** Get downloadable stream.
	 * @return output stream.
	 */
	public InputStream getStream() {
		return this.stream;
	}

	/** Sets the stream.
	 * @param stream The stream to set
	 */
	public void setStream(InputStream stream) {
		this.stream = stream;
	}

	/** Get stream content type.
	 * @return type of the stream content.
	 */
	public String getContentType() {
		return this.contentType;
	}

	/** Set stream content type.
	 * @param contentType The contentType to set
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/** Returns the file name.
	 * @return The name of the file.
	 */
	public String getFileName() {
		return fileName;
	}

	/** Sets the file name.
	 * @param fileName The file name to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/** Set a paramater for download stream.
	 *  Parameters are optional information about the downloadable stream
	 *  and their meaning depends on the used adapter. For example in
	 *  WebAdapter they are interpreted as HTTP response headers.
	 * 	
	 * 	If the parameters by this name exists, the old value is replaced.
	 * 
	 *  @param name Name of the parameter to set.
	 *  @param value Value of the parameter to set.
	 */
	public void setParameter(String name, String value) {
		if (this.params == null) {
			this.params = new HashMap();
		}
		this.params.put(name, value);
	}

	/** Get a paramater for download stream.
	 *  Parameters are optional information about the downloadable stream
	 *  and their meaning depends on the used adapter. For example in
	 *  WebAdapter they are interpreted as HTTP response headers.
	 *  @param name Name of the parameter to set.
	 *  @return Value of the parameter or null if the parameter does not exist.
	 */
	public String getParameter(String name) {
		if (this.params != null)
			return (String) this.params.get(name);
		return null;
	}

	/** Get the names of the parameters.
	 * @return Iteraror of names or null if no parameters are set.
	 */
	public Iterator getParameterNames() {
		if (this.params != null)
			return this.params.keySet().iterator();
		return null;
	}
	
	/** Get lenght of cache expiracy time.
	 *  This gives the adapter the possibility cache streams sent to the client.
	 *  The caching may be made in adapter or at the client if the client supports
	 *  caching. Default is DEFAULT_CACHETIME.
	 * @return Cache time in milliseconds
	 */
	public long getCacheTime() {
		return cacheTime;
	}

	/** Set lenght of cache expiracy time.
	 *  This gives the adapter the possibility cache streams sent to the client.
	 *  The caching may be made in adapter or at the client if the client supports
	 *  caching. Zero or negavive value disbales the caching of this stream.
	 * @param cacheTime The cache time in milliseconds.
	 */
	public void setCacheTime(long cacheTime) {
		this.cacheTime = cacheTime;
	}

	/** Get the size of the download buffer.
	 * @return int The size of the buffer in bytes.
	 */
	public int getBufferSize() {
		return bufferSize;
	}

	/** Set the size of the download buffer.
	 * @param bufferSize The size of the buffer in bytes.
	 */
	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

}
