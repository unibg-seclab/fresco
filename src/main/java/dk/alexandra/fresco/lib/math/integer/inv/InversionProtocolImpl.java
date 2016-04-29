/*******************************************************************************
 * Copyright (c) 2015 FRESCO (http://github.com/aicis/fresco).
 *
 * This file is part of the FRESCO project.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.  IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * FRESCO uses SCAPI - http://crypto.biu.ac.il/SCAPI, Crypto++, Miracl, NTL,
 * and Bouncy Castle. Please see these projects for any further licensing issues.
 *******************************************************************************/
package dk.alexandra.fresco.lib.math.integer.inv;

import dk.alexandra.fresco.framework.NativeProtocol;
import dk.alexandra.fresco.framework.ProtocolProducer;
import dk.alexandra.fresco.framework.value.OInt;
import dk.alexandra.fresco.framework.value.SInt;
import dk.alexandra.fresco.framework.value.Value;
import dk.alexandra.fresco.lib.field.integer.BasicNumericFactory;
import dk.alexandra.fresco.lib.field.integer.MultProtocol;
import dk.alexandra.fresco.lib.field.integer.OpenIntProtocol;
import dk.alexandra.fresco.lib.helper.sequential.SequentialProtocolProducer;

public class InversionProtocolImpl implements InversionProtocol {
	
	private final SInt x, result;
	private final BasicNumericFactory provider;
	private final LocalInversionFactory invProvider;
	private ProtocolProducer gp;
	boolean done;
	
	public InversionProtocolImpl(SInt x, SInt result, BasicNumericFactory provider, 
			LocalInversionFactory invProvider) {
		this.x = x;
		this.result = result;
		this.provider = provider;
		this.invProvider = invProvider;
		this.gp = null;
		this.done = false;
	}

	@Override
	public int getNextProtocols(NativeProtocol[] gates, int pos){
		if (gp == null){
			OInt inverse = provider.getOInt();
			SInt sProduct = provider.getSInt();
			OInt oProduct = provider.getOInt();
			SInt random = provider.getRandomSInt();
			MultProtocol blindingCircuit = provider.getMultProtocol(x, random, sProduct);
			OpenIntProtocol openCircuit = provider.getOpenProtocol(sProduct, oProduct);
			LocalInversionProtocol lic = invProvider.getLocalInversionProtocol(oProduct, inverse);
			MultProtocol unblindingCircuit = provider.getMultProtocol(inverse, random, result);
			
			gp = new SequentialProtocolProducer(blindingCircuit, openCircuit, lic, unblindingCircuit);
		}
		if (gp.hasNextProtocols()){
			pos = gp.getNextProtocols(gates, pos);
		}
		else if (!gp.hasNextProtocols()){
			gp = null;
			done = true;
		}
		return pos;
	}

	@Override
	public boolean hasNextProtocols() {
		return !done;
	}

	@Override
	public Value[] getInputValues() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Value[] getOutputValues() {
		// TODO Auto-generated method stub
		return null;
	}

}
