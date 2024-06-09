package com.alpermelkeli.cryptotrader.repository.cryptoApi;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.alpermelkeli.cryptotrader.model.Trade;

public interface AccountOperations {
    double getAccountBalance();
    double getSelectedCoinQuantity(String asset);
    CompletableFuture<List<Trade>> getTradeHistorySelectedCoin(String pairName);
}
