package com.alpermelkeli.cryptotrader.repository.cryptoApi.Binance;

import com.alpermelkeli.cryptotrader.model.Trade;
import com.alpermelkeli.cryptotrader.repository.cryptoApi.AccountOperations;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class BinanceAccountOperations implements AccountOperations {
    private static final String API_URL = "https://testnet.binance.vision/api/v3/account";
    private static final String BASE_URL = "https://testnet.binance.vision/api/v3/";

    private static final String API_KEY = "8lYWU5jk23jNIjTcEc9J9OLEuuyGJJ3xHqPRcBWggxPhi0IiTCaImqYDV07eqgzZ";
    private static final String API_SECRET = "iTMSOfhtH0sArKkT16Iq5u1PCQFh0OLM56kSSary7AocnGt5rRhSN4yVszs7j439";

    private final OkHttpClient client = new OkHttpClient();

    private String generateSignature(String data) throws Exception {
        Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(API_SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
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

    private String encode(String value) throws Exception {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
    }

    @Override
    public double getAccountBalance() {
        try {
            long timestamp = System.currentTimeMillis();
            String queryString = "timestamp=" + timestamp;
            String signature = generateSignature(queryString);
            queryString += "&signature=" + encode(signature);

            HttpUrl httpUrl = HttpUrl.parse(API_URL).newBuilder().encodedQuery(queryString).build();

            Request request = new Request.Builder()
                    .url(httpUrl)
                    .addHeader("X-MBX-APIKEY", API_KEY)
                    .build();


            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                String responseBody = response.body().string();
                JSONObject json = new JSONObject(responseBody);
                JSONArray balances = json.getJSONArray("balances");
                double totalBalance = 0.0;
                for (int i = 0; i < balances.length(); i++) {
                    JSONObject balance = balances.getJSONObject(i);
                    totalBalance += balance.getDouble("free") + balance.getDouble("locked");
                }
                return totalBalance;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return 0.0;
        }
    }

    @Override
    public double getSelectedCoinQuantity(String asset) {
        try {
            long timestamp = System.currentTimeMillis();
            String queryString = "timestamp=" + timestamp;
            String signature = generateSignature(queryString);
            queryString += "&signature=" + encode(signature);

            HttpUrl httpUrl = HttpUrl.parse(API_URL).newBuilder().encodedQuery(queryString).build();

            Request request = new Request.Builder()
                    .url(httpUrl)
                    .addHeader("X-MBX-APIKEY", API_KEY)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                String responseBody = response.body().string();
                JSONObject json = new JSONObject(responseBody);
                JSONArray balances = json.getJSONArray("balances");

                for (int i = 0; i < balances.length(); i++) {
                    JSONObject balance = balances.getJSONObject(i);
                    if (balance.getString("asset").equalsIgnoreCase(asset)) {
                        double free = balance.getDouble("free");
                        double locked = balance.getDouble("locked");
                        return free + locked;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return 0.0;
    }

    @Override
    public List<Trade> getTradeHistorySelectedCoin(String pairName) {
        List<Trade> tradeHistory = new ArrayList<>();
        return tradeHistory;
        /*System.out.println(pairName);
        List<Trade> tradeHistory = new ArrayList<>();
        try {
            long timeOffset = calculateTimeOffset();
            System.out.println(timeOffset);
            long timestamp = System.currentTimeMillis() + timeOffset;
            String queryString = "symbol=" + pairName + "&timestamp=" + timestamp;
            String signature = generateSignature(queryString);

            queryString += "&signature=" + URLEncoder.encode(signature, StandardCharsets.UTF_8.toString());
            HttpUrl httpUrl = HttpUrl.parse(BASE_URL + "myTrades").newBuilder().encodedQuery(queryString).build();

            System.out.println("Request URL: " + httpUrl.toString());

            Request request = new Request.Builder()
                    .url(httpUrl)
                    .addHeader("X-MBX-APIKEY", API_KEY)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    System.out.println("Response code: " + response.code());
                    System.out.println("Response message: " + response.message());
                    if (response.body() != null) {
                        System.out.println("Response body: " + response.body().string());
                    }
                    throw new IOException("Unexpected code " + response);
                }

                String responseBody = response.body().string();
                System.out.println("Response Body: " + responseBody);

                JSONArray json = new JSONArray(responseBody);

                for (int i = 0; i < json.length(); i++) {
                    JSONObject tradeJson = json.getJSONObject(i);
                    Trade trade = new Trade(tradeJson.getLong("time"), tradeJson.getDouble("price"), tradeJson.getDouble("qty"), tradeJson.getBoolean("isBuyer"));

                    tradeHistory.add(trade);
                }
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }
        return tradeHistory;
    }
    private long getServerTime() throws IOException {
        Request request = new Request.Builder()
                .url("https://testnet.binance.vision/api/v3/time")
                .build();

        try (Response response = client.newCall(request).execute()) {
            System.out.println("HERE");
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            String responseBody = response.body().string();
            JSONObject json = new JSONObject(responseBody);
            return json.getLong("serverTime");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private long calculateTimeOffset() throws IOException {
        long serverTime = getServerTime();
        long localTime = System.currentTimeMillis();
        return serverTime - localTime;
    }
*/
    }
}





