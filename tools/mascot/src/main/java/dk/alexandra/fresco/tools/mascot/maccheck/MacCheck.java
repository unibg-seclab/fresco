package dk.alexandra.fresco.tools.mascot.maccheck;

import java.util.List;

import dk.alexandra.fresco.framework.MaliciousException;
import dk.alexandra.fresco.tools.mascot.MascotContext;
import dk.alexandra.fresco.tools.mascot.arithm.CollectionUtils;
import dk.alexandra.fresco.tools.mascot.commit.CommitmentBasedProtocol;
import dk.alexandra.fresco.tools.mascot.field.FieldElement;

public class MacCheck extends CommitmentBasedProtocol<FieldElement> {

  /**
   * Constructs new mac checker.
   * 
   * @param ctx
   */
  public MacCheck(MascotContext ctx) {
    super(ctx, ctx.getFeSerializer());
  }

  /**
   * Runs mac-check on open value. <br>
   * Conceptually, checks (macShare0 + ... + macShareN) = (open) * (keyShare0 + ... + keyShareN)
   * 
   * @param opened the opened element to validate
   * @param macKeyShare this party's share of the mac key
   * @param macShare this party's share of the mac
   * @return
   * @throws MaliciousException if mac-check fails
   */
  public void check(FieldElement opened, FieldElement macKeyShare, FieldElement macShare) {
    // we will check that all sigmas together add up to 0
    FieldElement sigma = macShare.subtract(opened.multiply(macKeyShare));

    // commit to own value
    List<FieldElement> sigmas = allCommit(sigma);
    // add up all sigmas
    FieldElement sigmaSum = CollectionUtils.sum(sigmas);

    // sum of sigmas must be 0
    FieldElement zero = new FieldElement(0, modulus, modBitLength);
    if (!zero.equals(sigmaSum)) {
      throw new MaliciousException("Malicious mac forging detected");
    }
  }

}
