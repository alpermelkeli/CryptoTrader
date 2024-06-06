package com.alpermelkeli.cryptotrader.repository.cryptoApi;

/**
 * Interface representing exchange operations for buying and selling cryptocurrencies.
 */
public interface ExchangeOperations {
    void buyCoin(String symbol, double quantity);
    void sellCoin(String symbol, double quantity);
}
