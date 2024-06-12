package com.alpermelkeli.cryptotrader.repository.cryptoApi;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.alpermelkeli.cryptotrader.model.Trade;

public interface AccountOperations {
    CompletableFuture<Boolean> buyCoin(String symbol, double quantity);
    CompletableFuture<Boolean> sellCoin(String symbol, double quantity);
    double getAccountBalance();
    double getSelectedCoinQuantity(String asset);
    CompletableFuture<List<Trade>> getTradeHistorySelectedCoin(String pairName);
}
