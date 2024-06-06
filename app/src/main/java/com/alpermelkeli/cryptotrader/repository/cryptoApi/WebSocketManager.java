package com.alpermelkeli.cryptotrader.repository.cryptoApi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * Abstract class representing a WebSocket manager for handling WebSocket connections.
 */
public abstract class WebSocketManager {
    private final OkHttpClient client;
    protected WebSocket webSocket;

    /**
     * Constructor for WebSocketManager.
     * Initializes an OkHttpClient instance.
     */
    public WebSocketManager() {
        this.client = new OkHttpClient();
    }

    /**
     * Connects to the WebSocket server using the provided URL.
     *
     * @param url the URL of the WebSocket server.
     */
    public void connect(String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        webSocket = client.newWebSocket(request, getWebSocketListener());
    }

    /**
     * Disconnects from the WebSocket server, if connected.
     */
    public void disconnect() {
        if (webSocket != null) {
            webSocket.close(1000, "Closed by user");
            webSocket = null;
        }
    }

    /**
     * Abstract method to get the WebSocketListener instance.
     * This method should be implemented by subclasses to provide the specific listener.
     *
     * @return the WebSocketListener instance.
     */
    protected abstract WebSocketListener getWebSocketListener();

    /**
     * Abstract class representing a generic WebSocket listener.
     * Subclasses should implement the handleMessage method to handle incoming messages.
     */
    public static abstract class GenericWebSocketListener extends WebSocketListener {
        /**
         * Called when the WebSocket connection is opened.
         *
         * @param webSocket the WebSocket instance.
         * @param response the response from the server.
         */
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            // Connection opened
        }

        /**
         * Called when a text message is received from the WebSocket server.
         *
         * @param webSocket the WebSocket instance.
         * @param text the received text message.
         */
        @Override
        public void onMessage(WebSocket webSocket, String text) {
            handleMessage(text);
        }

        /**
         * Called when a binary message is received from the WebSocket server.
         *
         * @param webSocket the WebSocket instance.
         * @param bytes the received binary message.
         */
        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            handleMessage(bytes.utf8());
        }

        /**
         * Called when the WebSocket connection is closing.
         *
         * @param webSocket the WebSocket instance.
         * @param code the closing code.
         * @param reason the reason for closing.
         */
        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(code, reason);
        }

        /**
         * Called when the WebSocket connection fails.
         *
         * @param webSocket the WebSocket instance.
         * @param t the throwable representing the error.
         * @param response the response from the server, if any.
         */
        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            // Handle error
        }

        /**
         * Abstract method to handle received messages.
         * Subclasses should implement this method to process incoming messages.
         *
         * @param message the received message.
         */
        protected abstract void handleMessage(String message);
    }
}
