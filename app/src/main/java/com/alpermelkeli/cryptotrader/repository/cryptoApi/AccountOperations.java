package com.alpermelkeli.cryptotrader.repository.cryptoApi;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.alpermelkeli.cryptotrader.model.Trade;

public interface AccountOperations {
    void buyCoin(String symbol, double quantity);
    void sellCoin(String symbol, double quantity);
    double getAccountBalance();
    double getSelectedCoinQuantity(String asset);
    CompletableFuture<List<Trade>> getTradeHistorySelectedCoin(String pairName);
}
