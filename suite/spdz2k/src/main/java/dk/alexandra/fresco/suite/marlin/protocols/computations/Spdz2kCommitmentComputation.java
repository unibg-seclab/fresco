package dk.alexandra.fresco.suite.marlin.protocols.computations;

import dk.alexandra.fresco.commitment.HashBasedCommitment;
import dk.alexandra.fresco.framework.DRes;
import dk.alexandra.fresco.framework.builder.ComputationParallel;
import dk.alexandra.fresco.framework.builder.numeric.ProtocolBuilderNumeric;
import dk.alexandra.fresco.framework.network.serializers.ByteSerializer;
import dk.alexandra.fresco.framework.util.AesCtrDrbg;
import dk.alexandra.fresco.suite.marlin.protocols.natives.AllBroadcastProtocol;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Spdz2kCommitmentComputation implements
    ComputationParallel<List<byte[]>, ProtocolBuilderNumeric> {

  private static final Logger logger = LoggerFactory.getLogger(Spdz2kCommitmentComputation.class);

  private final ByteSerializer<HashBasedCommitment> commitmentSerializer;
  private final byte[] value;

  public Spdz2kCommitmentComputation(ByteSerializer<HashBasedCommitment> commitmentSerializer,
      byte[] value) {
    this.commitmentSerializer = commitmentSerializer;
    this.value = value;
  }

  @Override
  public DRes<List<byte[]>> buildComputation(ProtocolBuilderNumeric builder) {
    HashBasedCommitment ownCommitment = new HashBasedCommitment();
    // TODO optimize by caching initialized drbg somewhere, if needed
    byte[] ownOpening = ownCommitment.commit(new AesCtrDrbg(), value);
    return builder.seq(new BroadcastComputation<>(
        commitmentSerializer.serialize(ownCommitment)
    )).seq((seq, rawCommitments) -> {
      logger.debug("Done with broadcast computation " + builder.getBasicNumericContext().getMyId());
      DRes<List<byte[]>> openingsDRes = seq
          .seq((otherSeq -> otherSeq.append(new AllBroadcastProtocol<>(ownOpening))));
      List<HashBasedCommitment> commitments = commitmentSerializer.deserializeList(rawCommitments);
      return () -> open(commitments, openingsDRes.out());
    });
  }

  private List<byte[]> open(List<HashBasedCommitment> commitments, List<byte[]> openings) {
    if (commitments.size() != openings.size()) {
      throw new IllegalArgumentException("Lists must be same size");
    }
    List<byte[]> result = new ArrayList<>(commitments.size());
    for (int i = 0; i < commitments.size(); i++) {
      HashBasedCommitment commitment = commitments.get(i);
      byte[] opening = openings.get(i);
      result.add(commitment.open(opening));
    }
    return result;
  }

}