package se.kjellstrand.onemillion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MainActivity extends Activity implements OnItemSelectedListener {

	private static final float ONE_MILLION = 1000000f;

	private Gson gson = new Gson();

	private ExchangeRates exchangeRates;
	
	private final List<String> spinnerList = new ArrayList<String>();

	private Map<String, String> currencies = new HashMap<String, String>();

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
			spinnerList.add(entry.getKey());
		}

		// Creating adapter for spinner
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, spinnerList);

		// Drop down layout style - list view with radio button
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// attaching data adapter to spinner
		spinner.setAdapter(dataAdapter);

		findViewById(R.id.ok_button).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String amountString = ((EditText) findViewById(R.id.amount))
						.getText().toString();

				if (amountString != null && amountString.length() > 0) {
					float amount = Float.parseFloat(amountString);

					String base = spinnerList.get(spinner
							.getSelectedItemPosition());
					String cur = currencies.get(getBestMatchingCurrency(amount,
							base));
					String resultFormat = getResources().getString(
							R.string.result_text);
					String text = String
							.format(resultFormat, amount, currencies.get(base), cur);

					((TextView) findViewById(R.id.result)).setText(text);
				}
			}

		});
	}

	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		final Spinner spinner = (Spinner) findViewById(R.id.spinner);
		String base = spinnerList.get(spinner
				.getSelectedItemPosition());
		((TextView) findViewById(R.id.base)).setText(currencies.get(base));
	}

	public void onNothingSelected(AdapterView<?> parent) {
		// Another interface callback
	}

	private String getBestMatchingCurrency(float amount, String base) {
		String bestMatchCurrency = "USD";
		float baseRate = exchangeRates.rates.get(base);
		for (Entry<String, Float> entry : exchangeRates.rates.entrySet()) {
			if (getDistanceToOneMillion(
					exchangeRates.rates.get(bestMatchCurrency), amount,
					baseRate) > getDistanceToOneMillion(entry.getValue(),
					amount, baseRate)) {
				bestMatchCurrency = entry.getKey();
				Log.d("TAG", "----------------------");
			}
		}

		return bestMatchCurrency;
	}

	private float getDistanceToOneMillion(Float rate, float amount,
			float baseRate) {
		float distance = Math.abs((rate * amount / baseRate) - ONE_MILLION);
		Log.d("TAG", "dist: " + distance + " rate " + rate + " amount "
				+ amount + " base " + baseRate);
		return distance;
	}

	private String readFile(int resId) {
		BufferedReader r = new BufferedReader(new InputStreamReader(
				getResources().openRawResource(resId)));
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
