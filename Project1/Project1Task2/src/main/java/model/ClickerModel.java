package model;

import java.util.HashMap;
import java.util.Map;

public class ClickerModel {
    private Map<String, Integer> results = new HashMap<>();

    public void submitAnswer(String answer) {
        results.put(answer, results.getOrDefault(answer, 0) + 1);
    }

    public Map<String, Integer> getResults() {
        return results;
    }
}
