package com.example.testappfornatlex;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GetUrlContentTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... strings) {
        HttpURLConnection httpURLConnection;
        String content;
        String line;
        StringBuilder stringBuilder = new StringBuilder();

        // Try connect to server
        try {
            httpURLConnection = (HttpURLConnection) new URL(strings[0]).openConnection();
            httpURLConnection.setReadTimeout(5000);
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();
        } catch (IOException e) {
            e.printStackTrace();
            return "No connect";
        }

        // Read answer
        try {
            InputStream is = httpURLConnection.getInputStream();
            BufferedReader bf = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
            bf.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        content = stringBuilder.toString();

        return content;
    }
}
