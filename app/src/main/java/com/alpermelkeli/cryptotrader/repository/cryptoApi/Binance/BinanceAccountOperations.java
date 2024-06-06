package com.alpermelkeli.cryptotrader.repository.cryptoApi.Binance;

import com.alpermelkeli.cryptotrader.repository.cryptoApi.AccountOperations;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class BinanceAccountOperations implements AccountOperations {
    private static final String API_URL = "https://testnet.binance.vision/api/v3/account";
    private static final String API_KEY = "w2iP6cgc0S7SGzrvnMEWHXKVJd28V3Ypm26Xgi1biiVtn4gL4XsbqbJAnmGqYE13";
    private static final String API_SECRET = "78ME1ndU0KcvMKWsSwQcF9y63nvMWrP32nYjIbKBOZoGbldYDpZPvaFCR55qiXCg";

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


}
