/*******************************************************************************
 * Copyright (c) 2012, Institute for Pervasive Computing, ETH Zurich.
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the Institute nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE INSTITUTE AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE INSTITUTE OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * 
 * This file is part of the Californium CoAP framework.
 ******************************************************************************/
package ch.ethz.inf.vs.californium.proxy.layers;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import ch.ethz.inf.vs.californium.coap.DELETERequest;
import ch.ethz.inf.vs.californium.coap.GETRequest;
import ch.ethz.inf.vs.californium.coap.MediaTypeRegistry;
import ch.ethz.inf.vs.californium.coap.POSTRequest;
import ch.ethz.inf.vs.californium.coap.PUTRequest;
import ch.ethz.inf.vs.californium.coap.Request;
import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.californium.coap.TokenManager;
import ch.ethz.inf.vs.californium.proxy.HTTPMappingRegistry.COAP_METHOD;
import ch.ethz.inf.vs.californium.proxy.HTTPMessage;

/**
 * The Class HTTPtoCoAPLayer.
 * 
 * @author Francesco Corazza
 */
public class HTTPtoCoAPLayer extends AbstractLayer {

	// resource URI path used for discovery
	// private static final String DISCOVERY_RESOURCE = "/.well-known/core";

	/**
	 * Gets the response.
	 * 
	 * @param message
	 *            the message
	 * @return the response
	 * @see ch.ethz.inf.vs.californium.proxy.layers.AbstractLayer#getResponse(ch.ethz.inf.vs.californium.proxy.IMessage)
	 */
	@Override
	protected Response getResponse(HTTPMessage message) {
		if (message == null) {
			throw new IllegalArgumentException("message == null");
		}

		Request request = createRequest(message);
		LOG.info(request.getClass().getSimpleName() + " request to "
				+ request.getUriPath() + " sent");

		Response response = null;

		// execute request
		try {
			request.execute();

			// receive response
			LOG.finer("Receiving response");
			response = request.receiveResponse();

			// output response
			if (response != null) {
				// DEBUG response.prettyPrint();
				LOG.finer("Time elapsed (ms): " + response.getRTT());

			} else {
				// no response received
				LOG.warning("Request timed out");
			}

		} catch (UnknownHostException e) {
			LOG.severe("Unknown host: " + e.getMessage());
		} catch (IOException e) {
			LOG.severe("Failed to execute request: " + e.getMessage());
		} catch (InterruptedException e) {
			LOG.severe("Failed to receive response: " + e.getMessage());
		}

		return response;
	}

	private Request createRequest(HTTPMessage message) {

		COAP_METHOD method = message.getHTTPMethod();
		boolean loop = false;
		URI uri = null;
		try {
			uri = new URI("coap", message.getAddress().getHostName(), message
					.getUri().getPath(), message.getUri().getFragment());
		} catch (URISyntaxException e1) {
			// e1.printStackTrace();
			LOG.severe("URI malformed");
			return null;
		}

		message.getUri();

		// create request according to specified method
		Request request = switchRequestMethod(method);

		// if (method.equals("OBSERVE")) {
		// request.setOption(new Option(0, OptionNumberRegistry.OBSERVE));
		// loop = true;
		// }

		// set request URI
		// if (method.equals("DISCOVER")
		// && (uri.getPath() == null || uri.getPath().isEmpty() || uri
		// .getPath().equals("/"))) {
		// // add discovery resource path to URI
		// try {
		// uri = new URI(uri.getScheme(), uri.getAuthority(),
		// DISCOVERY_RESOURCE, uri.getQuery());
		//
		// } catch (URISyntaxException e) {
		// System.err.println("Failed to parse URI: " + e.getMessage());
		// // System.exit(ERR_BAD_URI);
		// }
		// }

		request.setURI(uri);
		int mediaType = MediaTypeRegistry.parse(message.getContentType()); 
		request.setContentType(mediaType);
		request.setPayload(message.getBody());
		request.setToken(TokenManager.getInstance().acquireToken());

		// enable response queue in order to use blocking I/O
		request.enableResponseQueue(true);

		return request;
	}

	/**
	 * Instantiates a new request based on a string describing a method.
	 * 
	 * @return A new request object, or null if method not recognized
	 */
	private Request switchRequestMethod(COAP_METHOD method) {
		switch (method) {
		case GET:
			return new GETRequest();
		case POST:
			return new POSTRequest();
		case PUT:
			return new PUTRequest();
		case DELETE:
			return new DELETERequest();
		case DISCOVER:
			return new GETRequest();
		case OBSERVE:
			return new GETRequest();
		default:
			LOG.severe("Method " + method + " not recognized");
			return null;
		}
	}

}
