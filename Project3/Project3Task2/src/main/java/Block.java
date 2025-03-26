import java.sql.Timestamp;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;

/**
 * The Block class represents a single block in a blockchain.
 *
 * Each block stores transaction data, a timestamp of creation, its position
 * in the chain (index), a nonce used for proof-of-work, the hash of the previous block,
 * and the difficulty target for mining the block.
 *
 * This class provides functionality to calculate a block's SHA-256 hash based on its properties,
 * as well as performing proof-of-work to find a hash that meets the specified difficulty requirement.
 *
 * Author: Vishakha Pathak (vmpathak@andrew.cmu.edu)
 * Last Modified: 03/17/2025
 *
 * LLM Self-Reporting: Portions of this code were generated and refined by the o3-mini-high language model
 */
public class Block {
    /**
     * The position of the block in the blockchain.
     */
    private int index;

    /**
     * The timestamp indicating when the block was created.
     */
    private Timestamp timestamp;

    /**
     * The transaction data stored within the block.
     */
    private String data;

    /**
     * The hash of the previous block in the chain.
     */
    private String previousHash;

    /**
     * A number used once that is incremented to find a valid hash.
     */
    private BigInteger nonce;

    /**
     * The number of leading zeros required in the block's hash.
     */
    private int difficulty;

    /**
     * Constructs a new Block instance with the specified parameters.
     *
     * @param index      The block's index in the blockchain.
     * @param timestamp  The timestamp marking the block's creation.
     * @param data       The transaction data to be stored in the block.
     * @param difficulty The required difficulty level (number of leading zeros in the hash).
     */
    public Block(int index, Timestamp timestamp, String data, int difficulty) {
        this.index = index;
        this.timestamp = timestamp;
        this.data = data;
        this.difficulty = difficulty;
        this.previousHash = "";
        this.nonce = BigInteger.ZERO;
    }

    /**
     * Calculates the SHA-256 hash of the block based on its properties.
     *
     * The hash is computed by concatenating the block's index, timestamp, data,
     * previous hash, nonce, and difficulty into a single string, which is then hashed.
     *
     * @return A hexadecimal string representation of the computed SHA-256 hash.
     * @throws RuntimeException if the SHA-256 algorithm is not available.
     */
    public String calculateHash() {
        String input = index + timestamp.toString() + data + previousHash + nonce.toString() + difficulty;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // This exception should not occur for SHA-256. Wrap it in a runtime exception.
            throw new RuntimeException(e);
        }
    }

    /**
     * Performs a proof-of-work algorithm to find a valid hash that meets the difficulty requirement.
     *
     * This method repeatedly increments the nonce and recalculates the block's hash until a hash
     * is found that begins with the required number of zeroes, as dictated by the difficulty level.
     *
     * @return The valid hash that meets the difficulty requirement.
     */
    public String proofOfWork() {
        while (true) {
            String hash = calculateHash();
            // Check if the hash has the required number of leading zeros
            if (hash.substring(0, difficulty).equals(new String(new char[difficulty]).replace('\0', '0'))) {
                return hash;
            }
            // Increment the nonce and try again
            nonce = nonce.add(BigInteger.ONE);
        }
    }

    // Getters and setters for block properties

    /**
     * Returns the transaction data stored in the block.
     *
     * @return The block's transaction data.
     */
    public String getData() {
        return data;
    }

    /**
     * Returns the difficulty level for the block's hash.
     *
     * @return The number of leading zeros required in the block's hash.
     */
    public int getDifficulty() {
        return difficulty;
    }

    /**
     * Returns the index of the block in the blockchain.
     *
     * @return The block's index.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Returns the current nonce value used in the hash calculation.
     *
     * @return The block's nonce.
     */
    public BigInteger getNonce() {
        return nonce;
    }

    /**
     * Returns the hash of the previous block in the blockchain.
     *
     * @return The previous block's hash.
     */
    public String getPreviousHash() {
        return previousHash;
    }

    /**
     * Returns the timestamp indicating when the block was created.
     *
     * @return The block's timestamp.
     */
    public Timestamp getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the transaction data for the block.
     *
     * @param data The new transaction data.
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * Sets a new difficulty level for the block's hash.
     *
     * @param difficulty The new difficulty level (number of leading zeros required).
     */
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    /**
     * Sets the index of the block within the blockchain.
     *
     * @param index The new index.
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Sets the hash of the previous block in the blockchain.
     *
     * @param previousHash The previous block's hash.
     */
    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    /**
     * Sets the timestamp for when the block was created.
     *
     * @param timestamp The new timestamp.
     */
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Returns a JSON-like string representation of the block.
     *
     * The string includes the block's index, timestamp, transaction data, previous hash, nonce, and difficulty.
     *
     * @return A string representing the block's properties in JSON format.
     */
    @Override
    public String toString() {
        return "{\"index\": " + index + ", \"time stamp\": \"" + timestamp.toString() + "\", \"Tx\": \"" + data + "\", \"PrevHash\": \"" + previousHash + "\", \"nonce\": " + nonce + ", \"difficulty\": " + difficulty + "}";
    }
}
