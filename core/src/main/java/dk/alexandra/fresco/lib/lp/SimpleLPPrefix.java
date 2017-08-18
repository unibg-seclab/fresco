/*
 * Copyright (c) 2015, 2016 FRESCO (http://github.com/aicis/fresco).
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
 */
package dk.alexandra.fresco.lib.lp;

import dk.alexandra.fresco.framework.Computation;
import dk.alexandra.fresco.framework.value.SInt;
import dk.alexandra.fresco.lib.collections.Matrix;

import java.util.List;

public class SimpleLPPrefix {

  private final Matrix<Computation<SInt>> updateMatrix;
  private final LPTableau tableau;
  private final Computation<SInt> pivot;
  private final List<Computation<SInt>> basis;

  public SimpleLPPrefix(Matrix<Computation<SInt>> updateMatrix, LPTableau tableau,
      Computation<SInt> pivot,
      List<Computation<SInt>> basis) {
    this.updateMatrix = updateMatrix;
    this.tableau = tableau;
    this.pivot = pivot;
    this.basis = basis;
  }


  public LPTableau getTableau() {
    return tableau;
  }

  public Matrix<Computation<SInt>> getUpdateMatrix() {
    return updateMatrix;
  }

  public Computation<SInt> getPivot() {
    return pivot;
  }

  public List<Computation<SInt>> getBasis() {
    return basis;
  }
}
