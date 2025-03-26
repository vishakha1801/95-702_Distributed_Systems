import java.net.*;
import java.io.*;
import java.util.*;
import com.google.gson.*;

/**
 * NeuralNetworkServer
 *
 * This UDP server hosts a neural network used to learn a truth table.
 * Clients send JSON-formatted requests (e.g., to get or set the truth table,
 * train the network, or test with specific inputs), and the server responds with JSON.
 *
 * The server maintains a NeuralNetwork instance (along with its helper classes)
 * that lives only on the server. The truth table is stored in a list of Double[][] arrays.
 *
 * Supported JSON requests include:
 * - {"request": "getCurrentRange"}
 * - {"request": "setCurrentRange", "val1": double, "val2": double, "val3": double, "val4": double}
 * - {"request": "setTrainingType", "type": "and"/"or"/"xor"}
 * - {"request": "train", "iterations": int}
 * - {"request": "test", "val1": double, "val2": double}
 *
 * Author: Vishakha Pathak (vmpathak)
 * Last Modified: 02/25/2025
 */
public class NeuralNetworkServer {
    private static NeuralNetwork network; // The neural network instance
    private static ArrayList<Double[][]> truthTableData; // Holds the truth table data
    private static DatagramSocket udpSocket;
    private static int listenPort;
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        System.out.println("Neural Network Server is active...");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("Enter port number to listen on:");
            listenPort = Integer.parseInt(reader.readLine());
            udpSocket = new DatagramSocket(listenPort);
            System.out.println("Server listening on port " + listenPort);

            // Initialize network with default settings: 2 inputs, 5 hidden neurons, 1 output.
            network = new NeuralNetwork(2, 5, 1, null, null, null, null);
            // Set default training data (default to AND gate)
            updateTruthTable("and");

            byte[] buffer = new byte[2000];
            while (true) {
                // Receive a UDP packet.
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                udpSocket.receive(packet);
                String reqJson = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Received JSON request: " + reqJson);

                // Process the JSON request and prepare a JSON response.
                String respJson = handleRequest(reqJson);
                System.out.println("Sending JSON response: " + respJson);

                byte[] respData = respJson.getBytes();
                DatagramPacket respPacket = new DatagramPacket(respData, respData.length,
                        packet.getAddress(), packet.getPort());
                udpSocket.send(respPacket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (udpSocket != null)
                udpSocket.close();
        }
    }

    /**
     * Handles a JSON request string and returns the corresponding JSON response.
     *
     * @param jsonReq The JSON-formatted request.
     * @return A JSON-formatted response string.
     */
    private static String handleRequest(String jsonReq) {
        Map<String, Object> reqMap = gson.fromJson(jsonReq, Map.class);
        String reqType = (String) reqMap.get("request");
        Map<String, Object> respMap = new HashMap<>();
        respMap.put("status", "OK");

        switch (reqType) {
            case "getCurrentRange":
                respMap.put("response", "getCurrentRange");
                respMap.put("val1", truthTableData.get(0)[1][0]);
                respMap.put("val2", truthTableData.get(1)[1][0]);
                respMap.put("val3", truthTableData.get(2)[1][0]);
                respMap.put("val4", truthTableData.get(3)[1][0]);
                break;

            case "setCurrentRange":
                if (!reqMap.containsKey("val1") || !reqMap.containsKey("val2") ||
                        !reqMap.containsKey("val3") || !reqMap.containsKey("val4")) {
                    respMap.put("status", "ERROR");
                    respMap.put("message", "Missing one or more truth table values.");
                    System.out.println("Error: Incomplete truth table values provided.");
                    break;
                }
                double v1 = ((Number) reqMap.get("val1")).doubleValue();
                double v2 = ((Number) reqMap.get("val2")).doubleValue();
                double v3 = ((Number) reqMap.get("val3")).doubleValue();
                double v4 = ((Number) reqMap.get("val4")).doubleValue();
                truthTableData = new ArrayList<>(Arrays.asList(
                        new Double[][]{{0.0, 0.0}, {v1}},
                        new Double[][]{{0.0, 1.0}, {v2}},
                        new Double[][]{{1.0, 0.0}, {v3}},
                        new Double[][]{{1.0, 1.0}, {v4}}
                ));
                // Reinitialize the network with new random weights.
                network = new NeuralNetwork(2, 5, 1, null, null, null, null);
                respMap.put("response", "setCurrentRange");
                System.out.println("Truth table successfully updated.");
                break;

            case "setTrainingType":
                String type = (String) reqMap.get("type");
                updateTruthTable(type);
                respMap.put("response", "setTrainingType");
                respMap.put("selectedType", type);
                System.out.println("Updated training data for " + type.toUpperCase());
                break;

            case "train":
                int iterations = ((Number) reqMap.get("iterations")).intValue();
                performTraining(iterations);
                double totalError = calculateErrorTotal();
                respMap.put("response", "train");
                respMap.put("iterations", iterations);
                respMap.put("val1", totalError);
                break;

            case "test":
                double input1 = ((Number) reqMap.get("val1")).doubleValue();
                double input2 = ((Number) reqMap.get("val2")).doubleValue();
                List<Double> testInputs = new ArrayList<>(Arrays.asList(input1, input2));
                List<Double> result = network.feedForward(testInputs);
                respMap.put("response", "test");
                respMap.put("val1", result.get(0));
                break;

            default:
                respMap.put("response", "error");
                respMap.put("status", "ERROR");
                System.out.println("Unknown request type: " + reqType);
        }
        return gson.toJson(respMap);
    }

    /**
     * Updates the truth table data based on the specified logic gate.
     *
     * @param type The logic gate type ("and", "or", or "xor").
     */
    private static void updateTruthTable(String type) {
        switch (type.toLowerCase()) {
            case "and":
                truthTableData = new ArrayList<>(Arrays.asList(
                        new Double[][]{{0.0, 0.0}, {0.0}},
                        new Double[][]{{0.0, 1.0}, {0.0}},
                        new Double[][]{{1.0, 0.0}, {0.0}},
                        new Double[][]{{1.0, 1.0}, {1.0}}
                ));
                break;
            case "or":
                truthTableData = new ArrayList<>(Arrays.asList(
                        new Double[][]{{0.0, 0.0}, {0.0}},
                        new Double[][]{{0.0, 1.0}, {1.0}},
                        new Double[][]{{1.0, 0.0}, {1.0}},
                        new Double[][]{{1.0, 1.0}, {1.0}}
                ));
                break;
            case "xor":
                truthTableData = new ArrayList<>(Arrays.asList(
                        new Double[][]{{0.0, 0.0}, {0.0}},
                        new Double[][]{{0.0, 1.0}, {1.0}},
                        new Double[][]{{1.0, 0.0}, {1.0}},
                        new Double[][]{{1.0, 1.0}, {0.0}}
                ));
                break;
            default:
                System.out.println("Invalid type specified. Defaulting to AND.");
                updateTruthTable("and");
        }
    }

    /**
     * Trains the neural network for the given number of iterations using random samples.
     *
     * @param iterations The number of training iterations.
     */
    private static void performTraining(int iterations) {
        Random random = new Random();
        for (int i = 0; i < iterations; i++) {
            int index = random.nextInt(4); // Select a random row from the truth table.
            List<Double> inputs = Arrays.asList(truthTableData.get(index)[0]);
            List<Double> expected = Arrays.asList(truthTableData.get(index)[1]);
            network.train(inputs, expected);
        }
    }

    /**
     * Calculates the total error over the entire truth table dataset.
     *
     * @return The aggregated error.
     */
    private static double calculateErrorTotal() {
        double sumError = 0.0;
        for (Double[][] data : truthTableData) {
            List<Double> inputs = Arrays.asList(data[0]);
            List<Double> expected = Arrays.asList(data[1]);
            List<Double> output = network.feedForward(inputs);
            for (int j = 0; j < expected.size(); j++) {
                double error = 0.5 * Math.pow(expected.get(j) - output.get(j), 2);
                sumError += error;
            }
        }
        return sumError;
    }
}
