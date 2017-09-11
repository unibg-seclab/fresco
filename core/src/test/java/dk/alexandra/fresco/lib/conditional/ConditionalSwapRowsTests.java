/*
 * Copyright (c) 2015, 2016, 2017 FRESCO (http://github.com/aicis/fresco).
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
package dk.alexandra.fresco.lib.conditional;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

import dk.alexandra.fresco.framework.Application;
import dk.alexandra.fresco.framework.DRes;
import dk.alexandra.fresco.framework.TestThreadRunner.TestThread;
import dk.alexandra.fresco.framework.TestThreadRunner.TestThreadFactory;
import dk.alexandra.fresco.framework.builder.numeric.Collections;
import dk.alexandra.fresco.framework.builder.numeric.ProtocolBuilderNumeric;
import dk.alexandra.fresco.framework.network.ResourcePoolCreator;
import dk.alexandra.fresco.framework.sce.resources.ResourcePool;
import dk.alexandra.fresco.framework.util.Pair;
import dk.alexandra.fresco.framework.util.RowPairD;
import dk.alexandra.fresco.framework.value.SInt;
import dk.alexandra.fresco.lib.collections.Matrix;
import dk.alexandra.fresco.lib.collections.MatrixTestUtils;

/**
 * Test class for the ConditionalSwapRowsTests protocol.
 */
public class ConditionalSwapRowsTests {

  /**
   * Performs a ConditionalSwapRows computation on matrix.
   * 
   * @author nv
   *
   * @param <ResourcePoolT>
   */
  private static class TestSwapGeneric<ResourcePoolT extends ResourcePool>
      extends TestThreadFactory {

    final BigInteger swapperOpen;
    final Pair<List<BigInteger>, List<BigInteger>> input;
    final Pair<List<BigInteger>, List<BigInteger>> expected;

    private TestSwapGeneric(BigInteger selectorOpen,
        Pair<List<BigInteger>, List<BigInteger>> expected,
        Pair<List<BigInteger>, List<BigInteger>> input) {
      this.swapperOpen = selectorOpen;
      this.expected = expected;
      this.input = input;
    }

    @Override
    public TestThread<ResourcePoolT, ProtocolBuilderNumeric> next() {
      return new TestThread<ResourcePoolT, ProtocolBuilderNumeric>() {

        @Override
        public void test() throws Exception {
          // define functionality to be tested
          Application<Pair<List<BigInteger>, List<BigInteger>>, ProtocolBuilderNumeric> testApplication =
              root -> {
                Collections collections = root.collections();
                DRes<List<DRes<SInt>>> closedLeft = collections.closeList(input.getFirst(), 1);
                DRes<List<DRes<SInt>>> closedRight = collections.closeList(input.getSecond(), 1);
                DRes<SInt> swapper = root.numeric().input(swapperOpen, 1);
                DRes<RowPairD<SInt, SInt>> swapped =
                    collections.condSwap(swapper, closedLeft, closedRight);
                DRes<RowPairD<BigInteger, BigInteger>> openSwapped =
                    collections.openRowPair(swapped);
                return () -> {
                  RowPairD<BigInteger, BigInteger> openSwappedOut = openSwapped.out();
                  List<BigInteger> leftRes = openSwappedOut.getFirst().out().stream().map(DRes::out)
                      .collect(Collectors.toList());
                  List<BigInteger> rightRes = openSwappedOut.getSecond().out().stream()
                      .map(DRes::out).collect(Collectors.toList());
                  return new Pair<>(leftRes, rightRes);
                };
              };
          Pair<List<BigInteger>, List<BigInteger>> output = secureComputationEngine.runApplication(
              testApplication, ResourcePoolCreator.createResourcePool(conf.sceConf));
          assertThat(output, is(expected));
        }
      };
    }
  }

  public static <ResourcePoolT extends ResourcePool> TestSwapGeneric<ResourcePoolT> testSwapYes() {
    Matrix<BigInteger> mat = new MatrixTestUtils().getInputMatrix(2, 3);
    Pair<List<BigInteger>, List<BigInteger>> input = new Pair<>(mat.getRow(0), mat.getRow(1));
    Pair<List<BigInteger>, List<BigInteger>> expected = new Pair<>(mat.getRow(1), mat.getRow(0));
    return new TestSwapGeneric<>(BigInteger.valueOf(1), expected, input);
  }

  public static <ResourcePoolT extends ResourcePool> TestSwapGeneric<ResourcePoolT> testSwapNo() {
    Matrix<BigInteger> mat = new MatrixTestUtils().getInputMatrix(2, 3);
    Pair<List<BigInteger>, List<BigInteger>> input = new Pair<>(mat.getRow(0), mat.getRow(1));
    Pair<List<BigInteger>, List<BigInteger>> expected = new Pair<>(mat.getRow(0), mat.getRow(1));
    return new TestSwapGeneric<>(BigInteger.valueOf(0), expected, input);
  }
}
