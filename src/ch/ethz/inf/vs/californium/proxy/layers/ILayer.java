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

import ch.ethz.inf.vs.californium.coap.Response;
import ch.ethz.inf.vs.californium.proxy.HTTPMessage;

/**
 * The Interface ILayer.
 */
public interface ILayer {

	/**
	 * Sets the upper layer.
	 * 
	 * @param upperLayer
	 *            the new upper layer
	 */
	public void setUpperLayer(ILayer upperLayer);

	/**
	 * Message received.
	 * 
	 * @param message
	 *            the message
	 * @param handler
	 *            the handler
	 */
	public Response processMessage(HTTPMessage message);

	// public void addMessageIncomingListener(IMessageHandler handler);
	// public void addMessageOutgoingListener(IMessageHandler handler);

	/**
	 * Checks for upper layer.
	 * 
	 * @return true, if successful
	 */
	public boolean hasUpperLayer();
}
