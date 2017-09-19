/*
 * Copyright (c) 2015, 2016, 2107 FRESCO (http://github.com/aicis/fresco).
 *
 * This file is part of the FRESCO project.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * FRESCO uses SCAPI - http://crypto.biu.ac.il/SCAPI, Crypto++, Miracl, NTL, and Bouncy Castle.
 * Please see these projects for any further licensing issues.
 *******************************************************************************/

package dk.alexandra.fresco.lib.collections.io;

import java.math.BigInteger;

import dk.alexandra.fresco.framework.DRes;
import dk.alexandra.fresco.framework.builder.ComputationParallel;
import dk.alexandra.fresco.framework.builder.numeric.Collections;
import dk.alexandra.fresco.framework.builder.numeric.ProtocolBuilderNumeric;
import dk.alexandra.fresco.framework.util.RowPairD;
import dk.alexandra.fresco.framework.value.SInt;

public class OpenRowPair
    implements ComputationParallel<RowPairD<BigInteger, BigInteger>, ProtocolBuilderNumeric> {

  private final DRes<RowPairD<SInt, SInt>> closedPair;

  public OpenRowPair(DRes<RowPairD<SInt, SInt>> closedPair) {
    super();
    this.closedPair = closedPair;
  }

  @Override
  public DRes<RowPairD<BigInteger, BigInteger>> buildComputation(ProtocolBuilderNumeric builder) {
    RowPairD<SInt, SInt> closedPairOut = closedPair.out();
    Collections collections = builder.collections();
    RowPairD<BigInteger, BigInteger> openPair =
        new RowPairD<>(collections.openList(closedPairOut.getFirst()),
            collections.openList(closedPairOut.getSecond()));
    return () -> openPair;
  }

}