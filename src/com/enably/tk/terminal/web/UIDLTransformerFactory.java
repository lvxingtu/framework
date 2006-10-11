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

package com.enably.tk.terminal.web;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Iterator;

/** Class implementing the MillStone WebAdapter UIDLTransformer Factory.
 * The factory creates and maintains a pool of transformers that are used
 * for transforming UIDL to HTML.
 * 
 * @author IT Mill Ltd.
 * @version @VERSION@
 * @since 3.0
 */

public class UIDLTransformerFactory {

	/** Time between repository modified queries. */
	private static final int CACHE_CHECK_INTERVAL_MILLIS = 5 * 1000;

	/** The time transformers are cached by default*/
	private static final long DEFAULT_TRANSFORMER_CACHETIME = 60 * 60 * 1000;

	/** Maximum number of transformers in use */
	private int maxConcurrentTransformers = 1;

	/** Last time theme modification time was checked */
	private long lastModificationCheckTime = 0;

	/** Last time theme source was modified */
	private long themeSourceModificationTime = 0;

	/** How long to cache transformers. */
	private long cacheTime = DEFAULT_TRANSFORMER_CACHETIME;

	/** Spool manager thread */
	private SpoolManager spoolManager;

	private Map transformerSpool = new HashMap();
	private ThemeSource themeSource;
	private WebAdapterServlet webAdapterServlet;
	private int transformerCount = 0;
	private int transformersInUse = 0;

	/** Constructor for transformer factory.
	 * Method UIDLTransformerFactory.
	 * @param themeSource Theme source to be used for themes.
	 * @param webAdapterServlet The Adapter servlet.
	 * @param maxConcurrentTransformers Maximum number of concurrent themes in use.
	 * @param cacheTime Time to cache the transformers.
	 */
	public UIDLTransformerFactory(
		ThemeSource themeSource,
		WebAdapterServlet webAdapterServlet,
		int maxConcurrentTransformers,
		long cacheTime) {
		this.webAdapterServlet = webAdapterServlet;
		if (themeSource == null)
			throw new NullPointerException();
		this.themeSource = themeSource;
		this.themeSourceModificationTime = themeSource.getModificationTime();
		this.maxConcurrentTransformers = maxConcurrentTransformers;
		if (cacheTime >= 0)
			this.cacheTime = cacheTime;
		this.spoolManager = new SpoolManager(this.cacheTime);
		this.spoolManager.setDaemon(true);
		//Enable manager only if time > 0
		if (this.cacheTime > 0)
			this.spoolManager.start();
	}

	/** Get new transformer of the specified type
	 * @param type Type of the requested transformer.
	 * @param variableMap WebVariable map used by the transformer
	 * @return Created new transformer.
	 */
	public synchronized UIDLTransformer getTransformer(UIDLTransformerType type)
		throws UIDLTransformerException {

		while (transformersInUse >= maxConcurrentTransformers) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				return null;
			}
		}

		// Get list of transformers for this type
		TransformerList list =
			(TransformerList) this.transformerSpool.get(type);

		// Check the modification time between fixed intervals
		long now = System.currentTimeMillis();
		if (now - CACHE_CHECK_INTERVAL_MILLIS
			> this.lastModificationCheckTime) {

			this.lastModificationCheckTime = now;

			//  Check if the theme source has been modified and flush 
			//  list if necessary
			long lastmod = this.themeSource.getModificationTime();
			if (list != null && this.themeSourceModificationTime < lastmod) {
				if (webAdapterServlet.isDebugMode()) {
					Log.info(
						"Theme source modified since "
							+ new Date(this.themeSourceModificationTime)
								.toString()
							+ ". Reloading...");
				}
				// Force refresh by removing from spool
				this.transformerSpool.clear();
				list = null;
				this.transformerCount = 0;
				this.themeSourceModificationTime = lastmod;
			}
		}

		UIDLTransformer t = null;

		if (list != null && !list.isEmpty()) {
			// If available, return the first available transformer
			t = (UIDLTransformer) list.removeFirst();
			if (webAdapterServlet.isDebugMode()) {
				Log.info("Reserved existing transformer: " + type);
			}
		} else {

			// Create new transformer and return it. Transformers are added to
			// spool when they are released.
			t = new UIDLTransformer(type, themeSource, webAdapterServlet);
			transformerCount++;
			if (webAdapterServlet.isDebugMode()) {
				Log.info(
					"Created new transformer ("
						+ transformerCount
						+ "):"
						+ type);
			}

			// Create new list, if not found
			if (list == null) {
				list = new TransformerList(type);
				this.transformerSpool.put(type, list);
				if (webAdapterServlet.isDebugMode()) {
					Log.info("Created new type: " + type);
				}
			}

		}
		transformersInUse++;
		return t;
	}

	/** Recycle a used transformer back to spool.
	 * One must guarantee not to use the transformer after it have been released.
	 * @param transformer UIDLTransformer to be recycled
	 */
	public synchronized void releaseTransformer(UIDLTransformer transformer) {

		try {
			// Reset the transformer before returning it to spool
			transformer.reset();

			// Recycle the transformer back to spool
			TransformerList list =
				(TransformerList) this.transformerSpool.get(
					transformer.getTransformerType());
			if (list != null) {
				list.add(transformer);
				if (webAdapterServlet.isDebugMode()) {
					Log.info(
						"Released transformer: "
							+ transformer.getTransformerType()
							+ "(In use: "
							+ transformersInUse
							+ ",Spooled: "
							+ list.size()
							+ ")");
				}
				list.lastUsed = System.currentTimeMillis();
			} else {
				Log.info(
					"Tried to release non-existing transformer. Ignoring."
						+ " (Type:"
						+ transformer.getTransformerType()
						+ ")");
			}
		} finally {
			if (transformersInUse > 0)
				transformersInUse--;
			notifyAll();
		}
	}

	private class TransformerList {

		private UIDLTransformerType type = null;
		private LinkedList list = new LinkedList();
		private long lastUsed = 0;

		public TransformerList(UIDLTransformerType type) {
			this.type = type;
		}

		public void add(UIDLTransformer transformer) {
			list.add(transformer);
		}

		public UIDLTransformer removeFirst() {
			return (UIDLTransformer) ((LinkedList) list).removeFirst();
		}

		public boolean isEmpty() {
			return list.isEmpty();
		}

		public int size() {
			return list.size();
		}
	}

	private synchronized void removeUnusedTransformers() {
		long currentTime = System.currentTimeMillis();
		HashSet keys = new HashSet();
		keys.addAll(this.transformerSpool.keySet());
		for (Iterator i = keys.iterator(); i.hasNext();) {
			UIDLTransformerType type = (UIDLTransformerType) i.next();
			TransformerList l =
				(TransformerList) this.transformerSpool.get(type);
			if (l != null) {
				if (l.lastUsed > 0
					&& l.lastUsed < (currentTime - this.cacheTime)) {
					if (webAdapterServlet.isDebugMode()) {
						Log.info(
							"Removed transformer: "
								+ type
								+ " Not used since "
								+ new Date(l.lastUsed));
					}
					this.transformerSpool.remove(type);
				}
			}
		}
	}

	/** Class for periodically remove unused transformers from memory.
	 * @author IT Mill Ltd.
	 * @version @VERSION@
	 * @since 3.0
	 */
	protected class SpoolManager extends Thread {

		long refreshTime;

		public SpoolManager(long refreshTime) {
			super("UIDLTransformerFactory.SpoolManager");
			this.refreshTime = refreshTime;
		}

		public void run() {
			while (true) {
				try {
					sleep(refreshTime);
				} catch (Exception e) {
				}
				removeUnusedTransformers();
			}
		}
	}

}
