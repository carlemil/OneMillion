
package se.kjellstrand.onemillion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

public class MainActivity extends Activity implements OnItemSelectedListener {

    private static final float ONE_MILLION = 1000000f;

    private Gson gson = new Gson();

    private ExchangeRates exchangeRates;

    private final List<String> currencyList = new ArrayList<String>();

    private Map<String, String> currencies = new HashMap<String, String>();

    private Map<String, String> reverseMapCurrencies = new HashMap<String, String>();

    private String baseCurrency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        String json = readFile(R.raw.exchange_rates);
        exchangeRates = gson.fromJson(json, ExchangeRates.class);

        json = readFile(R.raw.currencies);
        currencies = gson.fromJson(json, currencies.getClass());

        // Spinner element
        final Spinner spinner = (Spinner) findViewById(R.id.spinner);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        for (Entry<String, String> entry : currencies.entrySet()) {
            reverseMapCurrencies.put(entry.getValue(), entry.getKey());
            currencyList.add(entry.getValue());
        }
        Collections.sort(currencyList);

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, currencyList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        findViewById(R.id.ok_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String amountString = ((EditText) findViewById(R.id.amount)).getText().toString();

                if (amountString != null && amountString.length() > 0) {
                    float amount = Float.parseFloat(amountString);

                    // Save the current amount
                    Settings.setAmount(MainActivity.this, amount);

                    // Save the current amount to prefs
                    Settings.setBaseCurrency(MainActivity.this, baseCurrency);

                    String cur = currencies.get(getBestMatchingCurrency(amount, baseCurrency));

                    // TODO check distance and display sorry your not a
                    // Millionaire if to big/low

                    String resultFormat = getResources().getString(R.string.result_text);
                    String text = String.format(resultFormat, amount, baseCurrency, cur);

                    ((TextView) findViewById(R.id.result)).setText(text);
                }
            }
        });

        ((EditText) findViewById(R.id.amount)).setText(Float.toString(Settings.getAmount(this)));

        baseCurrency = Settings.getBaseCurrency(this);
        if (baseCurrency == null) {
            baseCurrency = "United States Dollar";
        }

        // Select the default currency
        spinner.setSelection(currencyList.lastIndexOf(baseCurrency));
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        baseCurrency = currencyList.get(spinner.getSelectedItemPosition());

        // Save the current amount to prefs
        Settings.setBaseCurrency(MainActivity.this, baseCurrency);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    private String getBestMatchingCurrency(float amount, String base) {
        String bestMatchCurrency = "USD";
        float baseRate = exchangeRates.rates.get(reverseMapCurrencies.get(base));
        for (Entry<String, Float> entry : exchangeRates.rates.entrySet()) {
            if (getDistanceToOneMillion(exchangeRates.rates.get(bestMatchCurrency), amount, baseRate) > getDistanceToOneMillion(
                    entry.getValue(), amount, baseRate)) {
                bestMatchCurrency = entry.getKey();
            }
        }

        return bestMatchCurrency;
    }

    private float getDistanceToOneMillion(Float rate, float amount, float baseRate) {
        float distance = Math.abs((rate * amount / baseRate) - ONE_MILLION);
        return distance;
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
        return total.toString();
    }
}
