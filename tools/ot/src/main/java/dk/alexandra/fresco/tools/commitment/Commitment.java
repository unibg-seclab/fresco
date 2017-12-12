package dk.alexandra.fresco.tools.commitment;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

import dk.alexandra.fresco.framework.MPCException;
import dk.alexandra.fresco.framework.MaliciousException;
import dk.alexandra.fresco.framework.network.Network;

/**
 * Class representing a hash-based commitment. Secure assuming that SHA-256 is a
 * random oracle. An instantiated object represents a commitment by itself and
 * does *not* contain any secret information. An object gets instantiated by
 * calling the commit command.
 * 
 * The scheme itself is based on the ROM folklore scheme where the message to
 * commit to is concatenated with a random string and then hashed. The hash
 * digest serves as the commitment itself and the opening is the randomness and
 * the message committed to.
 * 
 * @author jot2re
 *
 */
public class Commitment {

  public static final String hashAlgorithm = "SHA-256";
  // The length of the hash digest along with the randomness used
  public static final int digestLength = 32; // 256 / 8 bytes
  // The actual value representing the commitment
  protected byte[] commitmentVal = null;
  private final MessageDigest digest;

  /**
   * Constructs a new commitment, not yet committed to any value.
   */
  public Commitment() {
    try {
      digest = MessageDigest.getInstance(hashAlgorithm);
    } catch (NoSuchAlgorithmException e) {
      throw new MPCException(
          "Commitment failed. No malicious behaviour detected.", e);
    }
  }

  /**
   * Initializes the commitment to commit to a specific value and returns the
   * opening information.
   * 
   * @param rand
   *          A cryptographically secure randomness generator.
   * @param value
   *          The element to commit to
   * @return The opening information needed to open the commitment
   */
  public byte[] commit(Random rand, byte[] value) {
    if (commitmentVal != null) {
      throw new IllegalStateException("Already committed");
    }
    // Sample a sufficient amount of random bits
    byte[] randomness = new byte[digestLength];
    rand.nextBytes(randomness);
    // Construct an array to contain the byte to hash
    byte[] openingInfo = new byte[value.length + randomness.length];
    System.arraycopy(value, 0, openingInfo, 0, value.length);
    System.arraycopy(randomness, 0, openingInfo, value.length,
        randomness.length);
    commitmentVal = digest.digest(value);
    return openingInfo;
  }

  /**
   * Opens a committed object.
   * 
   * @param openingInfo
   *          The data needed to open this given commitment
   * @return The value that was committed to
   */
  public byte[] open(byte[] openingInfo) {
    if (commitmentVal == null) {
      throw new IllegalStateException("No commitment to open");
    }
    if (openingInfo.length < digestLength) {
      throw new MaliciousException(
          "The opening info is too small to be a commitment.");
    }
    // Extract the randomness and the value committed to from the openingInfo
    // The value comes first
    byte[] value = new byte[openingInfo.length - digestLength];
    System.arraycopy(openingInfo, 0, value, 0, openingInfo.length - digestLength);
    // The randomness comes at the end
    byte[] randomness = new byte[digestLength];
    System.arraycopy(openingInfo, value.length, randomness, 0, digestLength);
    // Hash the opening info and verify that it matches the value stored in
    // "commitmentValue"
    byte[] digestValue = digest.digest(value);
    if (Arrays.equals(digestValue, commitmentVal)) {
      return value;
    } else {
      throw new MaliciousException(
          "The opening info does not match the commitment.");
    }
  }

  /**
   * Serialize and send a commitment.
   * 
   * @param otherId
   *          The ID of the party to send to
   * @param network
   *          The network to send the commitment over
   * @param comm
   *          The commitment to send
   */
  public static void sendCommitment(Commitment comm, int otherId,
      Network network) {
    CommitmentSerializer serializer = new CommitmentSerializer();
    byte[] serializedComm = serializer.serialize(comm);
    network.send(otherId, serializedComm);
  }

  /**
   * Receive a commitment and deserialize it.
   * 
   * @param otherId
   *          The ID of the party to send to
   * @param network
   *          The network to send the commitment over
   * @return The deserialized commitment
   */
  public static Commitment receiveCommitment(int otherId, Network network) {
    byte[] serializedComm = network.receive(otherId);
    CommitmentSerializer serializer = new CommitmentSerializer();
    return serializer.deserialize(serializedComm);
  }
}
