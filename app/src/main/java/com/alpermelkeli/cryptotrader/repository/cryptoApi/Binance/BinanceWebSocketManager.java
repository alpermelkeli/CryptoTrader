package com.alpermelkeli.cryptotrader.repository.cryptoApi.Binance;

import com.alpermelkeli.cryptotrader.repository.cryptoApi.WebSocketManager;
import org.json.JSONObject;
import okhttp3.WebSocketListener;

/**
 * Class representing a WebSocket manager for handling connections to the Binance WebSocket API.
 */
public class BinanceWebSocketManager extends WebSocketManager {

    private final BinanceWebSocketListener listener;

    /**
     * Constructor for BinanceWebSocketManager.
     *
     * @param listener the listener for handling WebSocket events.
     */
    public BinanceWebSocketManager(BinanceWebSocketListener listener) {
        this.listener = listener;
    }

    /**
     * Connects to the Binance WebSocket server using the provided symbol.
     *
     * @param symbol the symbol of the cryptocurrency pair to connect to (e.g., "btcusdt").
     */
    public void connect(String symbol) {
        super.connect("wss://stream.binance.com:9443/ws/" + symbol.toLowerCase() + "@ticker");
    }

    /**
     * Provides the WebSocketListener instance.
     *
     * @return the WebSocketListener instance.
     */
    @Override
    protected WebSocketListener getWebSocketListener() {
        return listener;
    }

    /**
     * Abstract class representing a WebSocket listener for Binance.
     * Subclasses should implement the onPriceUpdate method to handle price updates.
     */
    public static abstract class BinanceWebSocketListener extends GenericWebSocketListener {
        /**
         * Handles incoming messages from the WebSocket server.
         * Parses the message to extract the current price and calls onPriceUpdate.
         *
         * @param message the received message.
         */
        @Override
        protected void handleMessage(String message) {
            try {
                JSONObject jsonObject = new JSONObject(message);
                String price = jsonObject.getString("c");
                onPriceUpdate(price);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        /**
         * Abstract method to handle price updates.
         * Subclasses should implement this method to process price updates.
         *
         * @param price the current price of the cryptocurrency.
         */
        public abstract void onPriceUpdate(String price);
    }
}
