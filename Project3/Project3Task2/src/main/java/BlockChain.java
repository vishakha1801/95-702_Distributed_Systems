import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Scanner;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * The BlockChain class manages a list of blocks to simulate a simple blockchain system.
 * It is responsible for adding blocks, calculating and verifying chain hashes, repairing any corruption,
 * and computing various blockchain metrics such as difficulty and expected hash counts.
 * Represents a blockchain, managing blocks and their hashes.
 *
 * Author: Vishakha Pathak (vmpathak@andrew.cmu.edu)
 * Last Modified: 03/17/2025
 *
 * LLM Self-Reporting: Portions of this code were generated and refined by the o3-mini-high language model
 */
public class BlockChain {
    /**
     * The list of blocks in the blockchain.
     */
    private ArrayList<Block> chain;

    /**
     * The hash of the most recent block in the blockchain.
     */
    private String chainHash;

    /**
     * The approximate number of SHA-256 hashes computed per second on this machine.
     */
    private int hashesPerSecond;

    /**
     * Constructs a new BlockChain and initializes its properties.
     */
    public BlockChain() {
        this.chain = new ArrayList<>();
        this.chainHash = "";
        this.hashesPerSecond = 0;
        computeHashesPerSecond();

        // Add Genesis Block
        Block genesisBlock = new Block(0, new Timestamp(System.currentTimeMillis()), "Genesis", 2);
        genesisBlock.setPreviousHash("");
        genesisBlock.proofOfWork();
        chain.add(genesisBlock);
        chainHash = genesisBlock.calculateHash();
    }

    /**
     * Adds a new block to the blockchain.
     *
     * @param newBlock The block to be added.
     */
    public void addBlock(Block newBlock) {
        long startTime = System.currentTimeMillis();
        newBlock.setPreviousHash(chainHash);
        newBlock.proofOfWork();
        chain.add(newBlock);
        chainHash = newBlock.calculateHash();
        long endTime = System.currentTimeMillis();
        System.out.println("Total execution time to add this block was " + (endTime - startTime) + " milliseconds");
    }

    /**
     * Computes the approximate number of hashes per second on this machine.
     */
    public void computeHashesPerSecond() {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 2000000; i++) {
            String input = "00000000";
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                digest.digest(input.getBytes());
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
        long endTime = System.currentTimeMillis();
        hashesPerSecond = (int) (2000000 / ((endTime - startTime) / 1000.0));
    }

    /**
     * Returns the block at the given index.
     *
     * @param i The index of the block.
     * @return The block at index i.
     */
    public Block getBlock(int i) {
        return chain.get(i);
    }

    /**
     * Returns the current chain hash.
     *
     * @return The chain hash.
     */
    public String getChainHash() {
        return chainHash;
    }

    /**
     * Returns the number of blocks in the blockchain.
     *
     * @return The chain size.
     */
    public int getChainSize() {
        return chain.size();
    }

    /**
     * Returns the approximate number of hashes computed per second.
     *
     * @return The hashes per second.
     */
    public int getHashesPerSecond() {
        return hashesPerSecond;
    }

    /**
     * Returns the latest block in the blockchain.
     *
     * @return The latest block.
     */
    public Block getLatestBlock() {
        return chain.get(chain.size() - 1);
    }

    /**
     * Returns the current system time.
     *
     * @return The current time as a Timestamp.
     */
    public Timestamp getTime() {
        return new Timestamp(System.currentTimeMillis());
    }

    /**
     * Returns the total difficulty of all blocks in the blockchain.
     *
     * @return The sum of difficulties for all blocks.
     */
    public int getTotalDifficulty() {
        int total = 0;
        for (Block block : chain) {
            total += block.getDifficulty();
        }
        return total;
    }

    /**
     * Returns the total expected number of hashes for the entire blockchain.
     *
     * @return The total expected hashes.
     */
    public double getTotalExpectedHashes() {
        double total = 0;
        for (Block block : chain) {
            total += Math.pow(16, block.getDifficulty());
        }
        return total;
    }

    /**
     * Checks if the blockchain is valid by verifying the hashes and previous hash pointers.
     *
     * @return "TRUE" if the chain is valid; otherwise, an error message.
     */
    public String isChainValid() {
        if (chain.size() == 1) {
            String hash = chain.get(0).calculateHash();
            if (hash.equals(chainHash) && hash.substring(0, chain.get(0).getDifficulty())
                    .equals(new String(new char[chain.get(0).getDifficulty()]).replace('\0', '0'))) {
                return "TRUE";
            } else {
                return "Genesis block hash does not match chain hash or does not meet difficulty requirement.";
            }
        }

        for (int i = 1; i < chain.size(); i++) {
            Block currentBlock = chain.get(i);
            Block previousBlock = chain.get(i - 1);
            if (!currentBlock.getPreviousHash().equals(previousBlock.calculateHash())) {
                return "Hashes do not match at block " + i;
            }
            if (!currentBlock.calculateHash().substring(0, currentBlock.getDifficulty())
                    .equals(new String(new char[currentBlock.getDifficulty()]).replace('\0', '0'))) {
                return "Hash does not meet difficulty requirement at block " + i;
            }
        }
        if (!chainHash.equals(chain.get(chain.size() - 1).calculateHash())) {
            return "Chain hash does not match latest block hash.";
        }
        return "TRUE";
    }

    /**
     * Repairs the blockchain by recomputing hashes for any invalid blocks.
     */
    public void repairChain() {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < chain.size(); i++) {
            Block block = chain.get(i);
            if (i > 0) {
                block.setPreviousHash(chain.get(i - 1).calculateHash());
            }
            block.proofOfWork();
        }
        chainHash = chain.get(chain.size() - 1).calculateHash();
        long endTime = System.currentTimeMillis();
        System.out.println("Total execution time required to repair the chain was " + (endTime - startTime) + " milliseconds");
    }

    /**
     * Returns a JSON-like string representation of the blockchain.
     *
     * @return A string representing the blockchain.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{\"ds_chain\": [\n");
        for (Block block : chain) {
            sb.append("  ").append(block.toString()).append(",\n");
        }
        if (chain.size() > 0) {
            sb.delete(sb.length() - 2, sb.length());
        }
        sb.append("], \"chainHash\":\"").append(chainHash).append("\"}");
        return sb.toString();
    }


    /**
     * Main method to test and interact with the blockchain.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        /**
         * Analysis of system behavior with increasing difficulty:
         * Our experiments show that as the difficulty level increases, the time taken by addBlock() grows dramatically. For example:
         * With difficulty 2, adding a block took about 7 milliseconds.
         * At difficulty 3, blocks were added in roughly 3 to 6 milliseconds.
         * When difficulty increased to 4, the addBlock() time jumped to around 117 milliseconds.
         * At difficulty 5, it took roughly 755 milliseconds.
         * With difficulty 6, the addBlock() method required approximately 4738 milliseconds.
         * This exponential increase is typical of proof-of-work algorithms, where finding a valid hash becomes significantly harder as more leading zeros are required.
         *
         * In contrast, the isChainValid() method consistently verifies the chain in negligible time (observed as 0 milliseconds), since it only recomputes hashes and checks pointers, which is not computationally expensive compared to proof-of-work.
         *
         * The chainRepair() method, takes longer as well because it recomputes proof-of-work for every block in the chain. Its execution time scales linearly with the number of blocks and increase further with higher difficulties, similar to addBlock().
         *
         * Overall, these results demonstrate that while verifying the chain remains efficient, the cost of adding or repairing blocks escalates steeply with increasing difficulty, illustrating the security-versus-efficiency trade-off in proof-of-work systems.
         */
        BlockChain blockchain = new BlockChain();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("0. View basic blockchain status.");
            System.out.println("1. Add a transaction to the blockchain.");
            System.out.println("2. Verify the blockchain.");
            System.out.println("3. View the blockchain.");
            System.out.println("4. Corrupt the chain.");
            System.out.println("5. Hide the corruption by recomputing hashes.");
            System.out.println("6. Exit");
            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 0:
                    System.out.println("Current size of chain: " + blockchain.getChainSize());
                    System.out.println("Difficulty of most recent block: " + blockchain.getLatestBlock().getDifficulty());
                    System.out.println("Total difficulty for all blocks: " + blockchain.getTotalDifficulty());
                    System.out.println("Experimented with 2,000,000 hashes.");
                    System.out.println("Approximate hashes per second on this machine: " + blockchain.getHashesPerSecond());
                    System.out.println("Expected total hashes required for the whole chain: " + blockchain.getTotalExpectedHashes());
                    System.out.println("Nonce for most recent block: " + blockchain.getLatestBlock().getNonce());
                    System.out.println("Chain hash: " + blockchain.getChainHash());
                    break;
                case 1:
                    System.out.print("Enter difficulty > 1: ");
                    int difficulty = scanner.nextInt();
                    scanner.nextLine(); // Consume newline left-over
                    System.out.print("Enter transaction: ");
                    String transaction = scanner.nextLine();
                    Block newBlock = new Block(blockchain.getChainSize(), new Timestamp(System.currentTimeMillis()), transaction, difficulty);
                    blockchain.addBlock(newBlock);
                    break;
                case 2:
                    long startTime = System.currentTimeMillis();
                    String validity = blockchain.isChainValid();
                    long endTime = System.currentTimeMillis();
                    System.out.println("Chain verification: " + validity);
                    System.out.println("Total execution time required to verify the chain was " + (endTime - startTime) + " milliseconds");
                    break;
                case 3:
                    System.out.println(blockchain.toString());
                    break;
                case 4:
                    System.out.print("Enter block ID of block to corrupt: ");
                    int blockID = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Enter new data for block " + blockID + ": ");
                    String newData = scanner.nextLine();
                    blockchain.getBlock(blockID).setData(newData);
                    System.out.println("Block " + blockID + " now holds " + newData);
                    break;
                case 5:
                    blockchain.repairChain();
                    break;
                case 6:
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
