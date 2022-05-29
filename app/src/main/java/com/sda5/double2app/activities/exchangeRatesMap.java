package com.sda5.double2app.activities;

import android.util.Log;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class exchangeRatesMap {

    HashMap<String, Double> CurMap = new HashMap<>();

    public void currMap() {

        String website = String.format("https://api.exchangeratesapi.io/latest?base=%s", "SEK");


        HttpURLConnection httpsURLConnection = null;

        {
            try {
                URL url = new URL(website);
                httpsURLConnection = (HttpURLConnection) url.openConnection();
                httpsURLConnection.setRequestMethod("GET");
            } catch (IOException e1) {
                e1.printStackTrace();
            }


            StringBuilder content = null;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()))) {

                String line;
                content = new StringBuilder();

                while ((line = in.readLine()) != null) {
                    content.append(line);
                    content.append(System.lineSeparator());
                    System.out.println(line);
                    Gson CurrSon = new Gson();
                    Map CurrMap = CurrSon.fromJson(line, Map.class);
                    Map hashmap = (Map) CurrMap.get("rates");


                    CurMap.putAll(hashmap);
                    System.out.println("check");
                }

            } catch (IOException e) {
                Log.e("check", e.toString());
            }
            httpsURLConnection.disconnect();
        }
    }

    public HashMap<String, Double> getCurrMap() {
        this.currMap();
        return CurMap;
    }
}
