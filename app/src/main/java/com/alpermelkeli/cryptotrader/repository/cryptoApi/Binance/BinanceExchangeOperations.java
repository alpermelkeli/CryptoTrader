package com.alpermelkeli.cryptotrader.repository.cryptoApi.Binance;

import android.os.Build;

import com.alpermelkeli.cryptotrader.repository.cryptoApi.ExchangeOperations;
import okhttp3.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class BinanceExchangeOperations implements ExchangeOperations {


    private static final String API_KEY = "8lYWU5jk23jNIjTcEc9J9OLEuuyGJJ3xHqPRcBWggxPhi0IiTCaImqYDV07eqgzZ";
    private static final String SECRET_KEY = "iTMSOfhtH0sArKkT16Iq5u1PCQFh0OLM56kSSary7AocnGt5rRhSN4yVszs7j439";
    private static final String BASE_URL = "https://testnet.binance.vision/api";

    private final OkHttpClient client;

    public BinanceExchangeOperations() {
        this.client = new OkHttpClient();
    }

    private String encode(String value) throws Exception {
        return URLEncoder.encode(value, "UTF-8");
    }

    private String generateSignature(String data) throws Exception {
        Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmacSHA256.init(secretKeySpec);
        return bytesToHex(hmacSHA256.doFinal(data.getBytes(StandardCharsets.UTF_8)));
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    @Override
    public void buyCoin(String symbol, double quantity) {
        try {
            String endpoint = "/v3/order";
            String url = BASE_URL + endpoint;

            long timestamp = System.currentTimeMillis();
            String queryString = "symbol=" + encode(symbol) + "&side=BUY&type=MARKET&quantity=" + quantity + "&timestamp=" + timestamp;
            String signature = generateSignature(queryString);
            queryString += "&signature=" + encode(signature);

            HttpUrl httpUrl = HttpUrl.parse(url).newBuilder().encodedQuery(queryString).build();

            Request request = new Request.Builder()
                    .url(httpUrl)
                    .addHeader("X-MBX-APIKEY", API_KEY)
                    .post(RequestBody.create(null, new byte[0]))
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        System.out.println("Buy order placed successfully");
                    } else {
                        System.out.println("Failed to place buy order: " + response.code() + " | " + response.message() + " | " + response.body().string());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sellCoin(String symbol, double quantity) {
        try {
            String endpoint = "/v3/order";
            String url = BASE_URL + endpoint;

            long timestamp = System.currentTimeMillis();
            String queryString = "symbol=" + encode(symbol) + "&side=SELL&type=MARKET&quantity=" + quantity + "&timestamp=" + timestamp;
            String signature = generateSignature(queryString);
            queryString += "&signature=" + encode(signature);

            HttpUrl httpUrl = HttpUrl.parse(url).newBuilder().encodedQuery(queryString).build();

            Request request = new Request.Builder()
                    .url(httpUrl)
                    .addHeader("X-MBX-APIKEY", API_KEY)
                    .post(RequestBody.create(null, new byte[0]))
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        System.out.println("Sell order placed successfully");
                    } else {
                        System.out.println("Failed to place sell order: " + response.message() + " | " + response.body().string());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
