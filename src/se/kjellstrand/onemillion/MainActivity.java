package se.kjellstrand.onemillion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Activity;
import android.os.Bundle;

import com.google.gson.Gson;

public class MainActivity extends Activity {

    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        
        String json = readFile(R.raw.currencies_short_hand);
        ExchangeRates rates = gson.fromJson(json, ExchangeRates.class);
        

    }

    private String readFile(int resId) {
        BufferedReader r = new BufferedReader(new InputStreamReader(getResources().openRawResource(resId)));
        StringBuilder total = new StringBuilder();
        String line;
        try {
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return r.toString();
    }
}
