package com.alpermelkeli.cryptotrader.repository.cryptoApi.Binance;


public class BinanceWebSocketListenerImpl extends BinanceWebSocketManager.BinanceWebSocketListener {
    private final BinanceExchangeOperations exchangeOperations;
    private final ThresholdManager thresholdManager;
    private final String symbol;
    private final double quantity;

    public BinanceWebSocketListenerImpl(BinanceExchangeOperations exchangeOperations, ThresholdManager thresholdManager, String symbol, double quantity) {
        this.exchangeOperations = exchangeOperations;
        this.thresholdManager = thresholdManager;
        this.symbol = symbol;
        this.quantity = quantity;
    }

    @Override
    public void onPriceUpdate(String price) {
        double currentPrice = Double.parseDouble(price);
        Double buyThreshold = thresholdManager.getBuyThreshold(symbol);
        Double sellThreshold = thresholdManager.getSellThreshold(symbol);

        if (buyThreshold != null && currentPrice > buyThreshold) {
            exchangeOperations.buyCoin(symbol, quantity);
            thresholdManager.removeBuyThreshold(symbol);
            thresholdManager.setSellThreshold(symbol, buyThreshold);
        }

        if (sellThreshold != null && currentPrice < sellThreshold) {
            exchangeOperations.sellCoin(symbol, quantity);
            thresholdManager.removeSellThreshold(symbol);
        }
    }
}
