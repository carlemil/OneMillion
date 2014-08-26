
package se.kjellstrand.onemillion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class InputFragment extends Fragment {

    private static final double ONE_MILLION = 1000000f;

    private static final Gson gson = new Gson();

    private ExchangeRates exchangeRates;

    private String baseCurrency;

    private final List<String> currencyList = new ArrayList<String>();

    private Map<String, String> currencies = new HashMap<String, String>();

    private Map<String, String> reverseMapCurrencies = new HashMap<String, String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String json = readFile(R.raw.exchange_rates);
        exchangeRates = gson.fromJson(json, ExchangeRates.class);

        json = readFile(R.raw.currencies);
        currencies = gson.fromJson(json, currencies.getClass());

        // Spinner Drop down elements
        for (Entry<String, String> entry : currencies.entrySet()) {
            reverseMapCurrencies.put(entry.getValue(), entry.getKey());
            currencyList.add(entry.getValue());
        }
        Collections.sort(currencyList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.input, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        baseCurrency = Settings.getBaseCurrency(getActivity());
        if (baseCurrency == null) {
            baseCurrency = "United States Dollar";
        }

        // Spinner element
        final Spinner spinner = (Spinner) getActivity().findViewById(R.id.spinner);

        // Spinner click listener
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                final Spinner spinner = (Spinner) getActivity().findViewById(R.id.spinner);
                baseCurrency = currencyList.get(spinner.getSelectedItemPosition());

                // Save the current amount to prefs
                Settings.setBaseCurrency(getActivity(), baseCurrency);
            }

            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,
                currencyList);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        getActivity().findViewById(R.id.ok_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String amountString = ((EditText) getActivity().findViewById(R.id.amount)).getText().toString();

                if (amountString != null && amountString.length() > 0) {
                    long amount = Long.MAX_VALUE;
                    try {
                        amount = Long.parseLong(amountString);
                    } catch (Exception e) {

                    }

                    // Save the current amount
                    Settings.setAmount(getActivity(), amount);

                    // Save the current amount to prefs
                    Settings.setBaseCurrency(getActivity(), baseCurrency);

                    String resultText;
                    String bestMatchingCurrencyKey = getBestMatchingCurrency(amount, baseCurrency);
                    String bestMatchingCurrency = currencies.get(bestMatchingCurrencyKey);
                    // Check distance and display sorry your not a Millionaire
                    // if null
                    if (bestMatchingCurrency != null) {
                        String resultFormat = getResources().getString(R.string.result_i_have);
                        double baseRate = exchangeRates.rates.get(reverseMapCurrencies.get(baseCurrency));
                        double bestRate = exchangeRates.rates.get(bestMatchingCurrencyKey);
                        long sum = (long) getAmount(bestRate, amount, baseRate);
                        String imAMillionare = getResources().getString(R.string.result_im_a_millionare);
                        resultText = imAMillionare + "\n"
                                + String.format(resultFormat, new DecimalFormat().format(sum), bestMatchingCurrency);
                    } else {
                        resultText = getResources().getString(R.string.result_not_a_millionare);
                    }

                    showResultFragment(resultText);
                }
            }

        });

        ((EditText) getActivity().findViewById(R.id.amount)).setText(Long.toString(Settings.getAmount(getActivity())));

        // Select the default currency
        spinner.setSelection(currencyList.lastIndexOf(baseCurrency));
    }

    private void showResultFragment(String resultText) {
        ResultFragment newFragment = new ResultFragment();
        Bundle args = new Bundle();
        args.putString(ResultFragment.RESULT_TEXT, resultText);
        newFragment.setArguments(args);

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment_container, newFragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    private String getBestMatchingCurrency(double amount, String base) {
        String bestMatchCurrency = null;
        double baseRate = exchangeRates.rates.get(reverseMapCurrencies.get(base));
        double bestAmount = Double.MAX_VALUE;
        for (Entry<String, Double> entry : exchangeRates.rates.entrySet()) {
            double currentAmount = getAmount(entry.getValue(), amount, baseRate);
            if (currentAmount < bestAmount && currentAmount > ONE_MILLION) {
                bestAmount = currentAmount;
                bestMatchCurrency = entry.getKey();
            }
        }

        return bestMatchCurrency;
    }

    private double getAmount(double bestRate, double amount, double baseRate) {
        double distance = (bestRate * amount / baseRate);
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
