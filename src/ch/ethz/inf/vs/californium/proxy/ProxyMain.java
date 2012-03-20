package ch.ethz.inf.vs.californium.proxy;

import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.ethz.inf.vs.californium.endpoint.LocalEndpoint;
import ch.ethz.inf.vs.californium.util.Log;

public class ProxyMain {

	protected static final Logger LOG = Logger.getLogger(ProxyMain.class
			.getName());

	static {
		LOG.setLevel(Level.ALL);
		Log.init();
	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {

		// check args
		if (args.length != 2) {
			LOG.severe("Usage: HTTPReceiverServer httpPort coapPort");
			System.exit(-1);
		}

		int httpPort = 0;
		int coapPort = 0;
		try {
			httpPort = Integer.parseInt(args[0]);
			coapPort = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			LOG.severe("Wrong port number");
			System.exit(-1);
		}

		if (httpPort < 1024 || coapPort < 1024) {
			LOG.severe("The port must be > 1024");
			System.exit(-1);
		}

		// starting the translation from HTTP to CoAP
		HTTPReceiverServer httpServer = new HTTPReceiverServer(httpPort,
				coapPort + 1);

		// starting the translation from CoAP to HTTP
//		try {
//			CoAPReceiverServer coapServer = new CoAPReceiverServer(
//					httpPort + 1, coapPort);
//		} catch (SocketException e) {
//			LOG.severe("Could not create the CoAP Server");
//			System.exit(-1);
//		}

		LOG.info("Proxy listening for HTTP request on port " + httpPort
				+ " and for CoAP request on port: " + coapPort);

		// TODO
		// hServer.close();
	}
}
