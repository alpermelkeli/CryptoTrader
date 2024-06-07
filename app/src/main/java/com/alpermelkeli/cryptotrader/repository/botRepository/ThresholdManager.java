package com.alpermelkeli.cryptotrader.repository.botRepository;

import java.util.HashMap;
import java.util.Map;

public class ThresholdManager {
    private final Map<String, Double> buyThresholds = new HashMap<>();
    private final Map<String, Double> sellThresholds = new HashMap<>();

    public void setBuyThreshold(String symbol, double threshold) {
        buyThresholds.put(symbol, threshold);
    }

    public void setSellThreshold(String symbol, double threshold) {
        sellThresholds.put(symbol, threshold);
    }

    public Double getBuyThreshold(String symbol) {
        return buyThresholds.get(symbol);
    }

    public Double getSellThreshold(String symbol) {
        return sellThresholds.get(symbol);
    }

    public void removeBuyThreshold(String symbol) {
        buyThresholds.remove(symbol);
    }

    public void removeSellThreshold(String symbol) {
        sellThresholds.remove(symbol);
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Buy thresolds:\n");
        for (String pair: buyThresholds.keySet()) {
            String key = pair.toString();
            double value = buyThresholds.get(pair);
            sb.append("("+ key + " " + value+")" );
        }
        sb.append("\n");
        sb.append("Sell thresolds:\n");
        for (String pair: sellThresholds.keySet()) {
            String key = pair.toString();
            double value = sellThresholds.get(pair);
            sb.append("("+ key + " " + value+")" );
        }

        return sb.toString();
    }

}
