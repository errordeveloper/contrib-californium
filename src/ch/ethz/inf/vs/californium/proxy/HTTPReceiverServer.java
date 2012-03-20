package ch.ethz.inf.vs.californium.proxy;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.ethz.inf.vs.californium.coap.MediaTypeRegistry;
import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.californium.proxy.layers.CachingLayer;
import ch.ethz.inf.vs.californium.proxy.layers.HTTPtoCoAPLayer;
import ch.ethz.inf.vs.californium.proxy.layers.ILayer;
import ch.ethz.inf.vs.californium.proxy.layers.RequestLimiterLayer;
import ch.ethz.inf.vs.californium.util.Log;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/**
 * The Class HTTPReceiverServer represents the HTTP server that will serve the
 * incoming connections to CoAP resources.
 */
public class HTTPReceiverServer {

	/** The http thread pool. */
	private ExecutorService threadPool = null;

	/** The http server. */
	private HttpServer httpServer = null;

	private HTTPReverseRequestHandler httpInterceptingRequestHandler;
	
	private HttpServer coapServer;

	protected static final Logger LOG = Logger
			.getLogger(HTTPReceiverServer.class.getName());

	static {
		LOG.setLevel(Level.ALL);
		Log.init();
	}

	/**
	 * Instantiates a new hTTP receiver server.
	 * 
	 * @param httpPort
	 *            the port
	 * @param coapPort 
	 */
	public HTTPReceiverServer(int httpPort, int coapPort) {
		InetSocketAddress httpAddress = new InetSocketAddress(httpPort);
		InetSocketAddress coapAddress = new InetSocketAddress(coapPort);

		try {
			// 0 means using system default socket backlog size
			// (backlog: maximum number of incoming TCP connections which the
			// system will queue internally)
			this.httpServer = HttpServer.create(httpAddress, 0);
			this.coapServer = HttpServer.create(coapAddress, 0);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}

		// create the handlers
		this.httpInterceptingRequestHandler = new HTTPReverseRequestHandler();

		// the context is the same for every resource
		this.httpServer.createContext("/", this.httpInterceptingRequestHandler);
		this.coapServer.createContext("/", this.httpInterceptingRequestHandler);

		// cachedThreadPool will save memory of the server
		this.threadPool = Executors.newCachedThreadPool();
		
		this.httpServer.setExecutor(this.threadPool);
		this.coapServer.setExecutor(this.threadPool);
		
		this.httpServer.start();
		this.coapServer.start();
	}

	/**
	 * Close all connections and resources.
	 */
	public void close() {
		this.httpServer.stop(1);
		this.threadPool.shutdownNow();
	}

	public class HTTPReverseRequestHandler implements HttpHandler {

		/**
		 * Handle the incoming requests.
		 * 
		 * @param httpExchange
		 *            the http exchange
		 * @throws IOException
		 *             Signals that an I/O exception has occurred.
		 * @see com.sun.net.httpserver.HttpHandler#handle(com.sun.net.httpserver.HttpExchange)
		 */
		@Override
		public void handle(HttpExchange httpExchange) throws IOException {
			LOG.entering(HTTPReverseRequestHandler.class.getSimpleName(), "handle");

			// the proxy will handle the request and the response
			HttpToCoapMapping.getInstance().handle(httpExchange);
			httpExchange.close();

			LOG.exiting(HTTPReverseRequestHandler.class.getSimpleName(), "handle");
		}
	}
	
	/**
	 * Singleton
	 * 
	 * @author Francesco Corazza
	 * 
	 */
	private static class HttpToCoapMapping {
		private static volatile HttpToCoapMapping instance = null;
		private ILayer lowestLayer = null;

		public static HttpToCoapMapping getInstance() {
			if (instance == null) {
				synchronized (HttpToCoapMapping.class) {
					if (instance == null) {
						instance = new HttpToCoapMapping();
					}
				}
			}
			return instance;
		}

		private HttpToCoapMapping() {
			// create chain of responsibility
			ILayer httpToCoapLayer = new HTTPtoCoAPLayer();
			ILayer requestLimiterLayer = new RequestLimiterLayer();
			requestLimiterLayer.setUpperLayer(httpToCoapLayer);
			ILayer cachingLayer = new CachingLayer();
			cachingLayer.setUpperLayer(requestLimiterLayer);

			this.lowestLayer = cachingLayer;
		}

		public void handle(HttpExchange httpExchange) {

			// create the HTTPMessage wrapper
			HTTPMessage httpMessage = new HTTPMessage(httpExchange);

			// DEBUG httpMessage.print();

			// get the response for the current request
			Response response = this.lowestLayer.processMessage(httpMessage);

			// DEBUG response.prettyPrint();

			// if there are any errors, print them
			int code = response.getCode();
			int statusCode = HTTPMappingRegistry.translateCode(code);

			String contentType;
			String payload; // TODO not only strings

			if (statusCode > 299) { // TODO check
				contentType = "text/html";
				payload = "<!DOCTYPE html><html lang=en><head>"
						+ "<meta charset=utf-8>" + "<title>Error " + statusCode
						+ "</title></head>" + "<body><p><b>Error " + statusCode
						+ "</b><p>The requested URL <code>"
						+ response.getRequest().getUriPath()
						+ "</code> produced a problem.</body></html>";

			} else {
				payload = response.getPayloadString();
				contentType = MediaTypeRegistry.toString(response.getContentType());
			}
			int payloadSize = payload.getBytes().length; // TODO not only strings

			// get and set the headers
			Headers headers = httpExchange.getResponseHeaders();
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"EEE, dd MMM yyyy HH:mm:ss zzz");
			headers.set("Date", dateFormat.format(Calendar.getInstance().getTime()));
			if (contentType != null) {
				headers.set("Content-Type", contentType);
			}
			headers.set("Content-Length", Integer.toString(payloadSize));

			// send the headers
			try {
				// If the response length parameter is zero, then chunked transfer encoding is used and an arbitrary amount of data may be sent.
				httpExchange.sendResponseHeaders(statusCode, payloadSize);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// send the response body
			OutputStream oStreamResponse = httpExchange.getResponseBody();
			PrintStream printStream = new PrintStream(oStreamResponse); // TODO not
																		// only
																		// strings
			printStream.print(payload);
			printStream.close();
			try {
				oStreamResponse.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}
