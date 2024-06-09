package com.alpermelkeli.cryptotrader.repository.cryptoApi;

import java.util.List;
import com.alpermelkeli.cryptotrader.model.Trade;

public interface AccountOperations {
    double getAccountBalance();
    double getSelectedCoinQuantity(String asset);
    List<Trade> getTradeHistorySelectedCoin(String pairName);
}
