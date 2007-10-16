package com.itmill.toolkit.launcher;

import java.util.HashMap;
import java.util.Map;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.thread.BoundedThreadPool;

/**
 * Class for running Jetty servlet container within Eclipse project.
 * 
 */
public class ITMillStandardLaunch {

	private final static String serverPort = "8080";

	/**
	 * Main function for running Jetty.
	 * 
	 * Command line Arguments are passed through to Jetty, see runServer method
	 * for options.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		// Pass-through of arguments for Jetty
		Map serverArgs = parseArguments(args);

		String url = runServer(serverArgs);

		// Open browser into application URL
		if (url != null) {
			BrowserLauncher.openBrowser(url);
		}

	}

	/**
	 * Run the server with specified arguments.
	 * 
	 * @param serverArgs
	 * @return
	 */
	protected static String runServer(Map serverArgs) {

		// Add help for System.out
		System.out
				.println("-------------------------------------------------\n"
						+ "Starting IT Mill Toolkit examples.\n"
						+ "Please go to http://localhost:"
						+ serverPort
						+ "\nif your web browser is not automatically started."
						+ "\n-------------------------------------------------\n");

		// Assign default values for some arguments
		assignDefault(serverArgs, "webroot", "WebContent");
		assignDefault(serverArgs, "httpPort", serverPort);

		try {
			long started = System.currentTimeMillis();

			Server server = new Server();

			String threadPoolName = System.getProperty("jetty.threadpool.name",
					"Jetty thread");
			int maxIdleTimeMs = Integer.getInteger(
					"jetty.threadpool.maxIdleTimeMs", 60000);
			int maxThreads = Integer.getInteger("jetty.threadpool.maxThreads",
					100);
			int minThreads = Integer.getInteger("jetty.threadpool.minThreads",
					1);
			int lowThreads = Integer.getInteger(
					"jetty.threadpool.maxIdleTimeMs", 25);
			BoundedThreadPool threadPool = new BoundedThreadPool();
			threadPool.setName(threadPoolName);
			threadPool.setMaxIdleTimeMs(maxIdleTimeMs);
			threadPool.setMaxThreads(maxThreads);
			threadPool.setMinThreads(minThreads);
			threadPool.setLowThreads(lowThreads);
			server.setThreadPool(threadPool);

			Connector connector = new SelectChannelConnector();
			connector.setPort(Integer.valueOf(serverArgs.get("httpPort")
					.toString()));
			server.setConnectors(new Connector[] { connector });

			WebAppContext webappcontext = new WebAppContext();
			webappcontext.setContextPath("");
			webappcontext.setWar(serverArgs.get("webroot").toString());

			server.setHandler(webappcontext);

			server.start();
			System.err.println("Started Jetty in "
					+ (System.currentTimeMillis() - started) + "ms.");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return "http://localhost:" + serverArgs.get("httpPort");
	}

	/**
	 * Assign default value for given key.
	 * 
	 * @param map
	 * @param key
	 * @param value
	 */
	private static void assignDefault(Map map, String key, String value) {
		if (!map.containsKey(key)) {
			map.put(key, value);
		}
	}

	/**
	 * Parse all command line arguments into a map.
	 * 
	 * Arguments format "key=value" are put into map.
	 * 
	 * @param args
	 * @return map of arguments key value pairs.
	 */
	protected static Map parseArguments(String[] args) {
		Map map = new HashMap();
		for (int i = 0; i < args.length; i++) {
			int d = args[i].indexOf("=");
			if (d > 0 && d < args[i].length() && args[i].startsWith("--")) {
				String name = args[i].substring(2, d);
				String value = args[i].substring(d + 1);
				map.put(name, value);
			}
		}
		return map;
	}

}
