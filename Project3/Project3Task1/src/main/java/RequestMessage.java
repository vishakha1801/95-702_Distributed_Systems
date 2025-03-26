/**
 * RequestMessage.java
 *
 * Author: Vishakha Pathak (vmpathak@andrew.cmu.edu)
 * Last Modified: 03/17/2025
 *
 * This class encapsulates a JSON request message sent from the client to the server.
 * It includes the action to be performed and any additional data required for that action.
 *
 * LLM Self-Reporting: Portions of this code were generated and refined by the o3-mini-high language model.
 */

public class RequestMessage {
    private String action;       // e.g., "view_status", "add_transaction", "verify_chain", "view_chain", "corrupt_chain", "repair_chain", "exit"
    private int difficulty;      // used for add_transaction
    private String transaction;  // used for add_transaction
    private int blockID;         // used for corrupt_chain
    private String newData;      // used for corrupt_chain

    // Default constructor required for Gson
    public RequestMessage() {}

    // Parameterized constructor for convenience (action only)
    public RequestMessage(String action) {
        this.action = action;
    }

    // Getters and setters
    public String getAction() {
        return action;
    }
    public void setAction(String action) {
        this.action = action;
    }

    public int getDifficulty() {
        return difficulty;
    }
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public String getTransaction() {
        return transaction;
    }
    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

    public int getBlockID() {
        return blockID;
    }
    public void setBlockID(int blockID) {
        this.blockID = blockID;
    }

    public String getNewData() {
        return newData;
    }
    public void setNewData(String newData) {
        this.newData = newData;
    }
}
