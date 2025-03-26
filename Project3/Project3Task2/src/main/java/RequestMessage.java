/**
 * RequestMessage.java
 *
 * Author: Vishakha Pathak (vmpathak@andrew.cmu.edu)
 * Last Modified: 17/03/25
 *
 * */

public class RequestMessage {
    // Common fields
    private String command;
    private String clientId;
    private String publicKeyE;
    private String publicKeyN;
    private String signature;

    // Fields for "addTransaction"
    private int difficulty;
    private String transaction;

    // Fields for "corruptChain"
    private int blockIndex;
    private String newData;

    // Default constructor required for Gson
    public RequestMessage() {}

    // Getters
    public String getCommand() { return command; }
    public String getClientId() { return clientId; }
    public String getPublicKeyE() { return publicKeyE; }
    public String getPublicKeyN() { return publicKeyN; }
    public String getSignature() { return signature; }
    public int getDifficulty() { return difficulty; }
    public String getTransaction() { return transaction; }
    public int getBlockIndex() { return blockIndex; }
    public String getNewData() { return newData; }

    // Setters
    public void setCommand(String command) { this.command = command; }
    public void setClientId(String clientId) { this.clientId = clientId; }
    public void setPublicKeyE(String publicKeyE) { this.publicKeyE = publicKeyE; }
    public void setPublicKeyN(String publicKeyN) { this.publicKeyN = publicKeyN; }
    public void setSignature(String signature) { this.signature = signature; }
    public void setDifficulty(int difficulty) { this.difficulty = difficulty; }
    public void setTransaction(String transaction) { this.transaction = transaction; }
    public void setBlockIndex(int blockIndex) { this.blockIndex = blockIndex; }
    public void setNewData(String newData) { this.newData = newData; }
}
