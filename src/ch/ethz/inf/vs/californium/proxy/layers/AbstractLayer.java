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

import java.util.logging.Level;
import java.util.logging.Logger;

import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.californium.proxy.HTTPMessage;
import ch.ethz.inf.vs.californium.proxy.HTTPReceiverServer;
import ch.ethz.inf.vs.californium.util.Log;

/**
 * The Class AbstractLayer.
 * 
 * @author Francesco Corazza
 */
public abstract class AbstractLayer implements ILayer {

	/** The upper layer. */
	protected ILayer upperLayer = null;

	/** The Constant LOG. */
	protected static final Logger LOG = Logger
			.getLogger(HTTPReceiverServer.class.getName());

	static {
		LOG.setLevel(Level.ALL);
		Log.init();
	}

	// TODO??
	// protected Set<IMessageHandler> incomingMessageListeners =
	// Collections.synchronizedSet(new HashSet<IMessageHandler>());
	// protected Set<IMessageHandler> outgoingMessageListeners =
	// Collections.synchronizedSet(new HashSet<IMessageHandler>());

	/**
	 * Sets the upper layer.
	 * 
	 * @param upperLayer
	 *            the new upper layer
	 * @see ch.ethz.inf.vs.californium.proxy.layers.ILayer#setUpperLayer(ch.ethz.inf.vs.californium.proxy.layers.ILayer)
	 */
	@Override
	public void setUpperLayer(ILayer upperLayer) {
		if (upperLayer == null) {
			throw new IllegalArgumentException("upperLayer == null");
		}
		this.upperLayer = upperLayer;
	}

	/**
	 * 
	 * @param message
	 *            the message
	 * @param handler
	 *            the handler
	 * @see ch.ethz.inf.vs.californium.proxy.layers.ILayer#messageRecived(ch.ethz.inf.vs.californium.proxy.IMessage,
	 *      ch.ethz.inf.vs.californium.proxy.HTTPResponseHandler)
	 */
	@Override
	public Response processMessage(HTTPMessage message) {
		if (message == null) {
			throw new IllegalArgumentException("message == null");
		}

		Response response = getResponse(message);

		if (response == null) {
			if (hasUpperLayer()) {
				response = this.upperLayer.processMessage(message);
			} else {
				LOG.severe("response == null");
			}
		}

		return response;
	}

	/**
	 * Gets the response.
	 * 
	 * @param message
	 *            the message
	 * @return the response
	 */
	protected abstract Response getResponse(HTTPMessage message);

	/**
	 * Checks for upper layer.
	 * 
	 * @return true, if successful
	 * @see ch.ethz.inf.vs.californium.proxy.layers.ILayer#hasUpperLayer()
	 */
	@Override
	public boolean hasUpperLayer() {
		return this.upperLayer != null;
	}
}
