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
package ch.ethz.inf.vs.californium.proxy;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.logging.Logger;

import ch.ethz.inf.vs.californium.proxy.HTTPMappingRegistry.COAP_METHOD;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

/**
 * The Class HTTPMessage.
 * 
 * @author Francesco Corazza
 */
public class HTTPMessage {

	/** The uri. */
	private URI uri;

	/** The address. */
	private InetSocketAddress remoteAddress;

	/** The context path. */
	private String contextPath;

	/** The method. */
	private COAP_METHOD httpMethod;

	/** The headers. */
	private Headers headers;

	/** The body. */
	private String body;

	private String contentType;

	/** The Constant LOG. */
	protected static final Logger LOG = Logger.getLogger(HTTPMessage.class
			.getName());

	/**
	 * Instantiates a new hTTP message.
	 * 
	 * @param httpExchange
	 *            the http exchange
	 */
	public HTTPMessage(HttpExchange httpExchange) {
		if (httpExchange == null) {
			throw new IllegalArgumentException("httpExchange == null");
		}

		// get the remote address
		this.remoteAddress = httpExchange.getRemoteAddress();

		// get the uri
		this.uri = httpExchange.getRequestURI();

		// get the context
		this.contextPath = httpExchange.getHttpContext().getPath();

		// get the contextType without the encoding description
		String contentType = httpExchange.getRequestHeaders().getFirst(
				"Content-Type");
		if (contentType != null) {
			this.contentType = contentType.split(";")[0];
		}

		// get the method of the request
		String methodString = httpExchange.getRequestMethod();
		this.httpMethod = COAP_METHOD.valueOf(methodString);

		// get the header of the request
		this.headers = httpExchange.getRequestHeaders();

		// get the body
		InputStream iStreamRequest = httpExchange.getRequestBody();
		try {
			if (iStreamRequest.available() != 0) {

				this.body = (new BufferedReader(new InputStreamReader(
						iStreamRequest))).readLine();
				// TODO modify, not only strings
				iStreamRequest.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Uri.
	 * 
	 * @return the serviceUri
	 */
	public URI getUri() {
		return this.uri;
	}

	/**
	 * Gets the address.
	 * 
	 * @return the address
	 */
	public InetSocketAddress getAddress() {
		return this.remoteAddress;
	}

	/**
	 * Gets the context path.
	 * 
	 * @return the contextPath
	 */
	public String getContextPath() {
		return this.contextPath;
	}

	/**
	 * Gets the method.
	 * 
	 * @return the method
	 */
	public COAP_METHOD getHTTPMethod() {
		return this.httpMethod;
	}

	/**
	 * Gets the body.
	 * 
	 * @return the body
	 */
	public String getBody() {
		return this.body;
	}

	/**
	 * Prints the.
	 */
	public void print() {
		System.out.println("Address: " + this.remoteAddress + " - "
				+ this.remoteAddress.getHostName());
		System.out.println("URI: " + this.uri);
		System.out.println("HttpContext path: " + this.contextPath);
		System.out.println("Method: " + this.httpMethod);

		System.out.println("Headers:");
		System.out.println("＿");
		for (String key : this.headers.keySet()) {
			System.out.println("|- " + key + ":");
			for (String value : this.headers.get(key)) {
				System.out.println("|--- " + value);
			}
		}
		System.out.println("￣");

		System.out.println("Body: " + this.body);
	}

	public String getContentType() {
		return this.contentType;
	}

	/**
	 * Utility method.
	 * 
	 * @param is
	 *            the is
	 * @param charSet
	 *            the char set
	 * @return the string
	 */
	// public String convertStreamToString(InputStream is, String charSet) {
	// try {
	// return new Scanner(is, charSet).useDelimiter("\\A").next();
	// } catch (NoSuchElementException e) {
	// return "";
	// }
	// }

}
