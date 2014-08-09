package se.kjellstrand.onemillion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MainActivity extends Activity {

	private Gson gson = new Gson();

	private ExchangeRates exchangeRates;

	private Map<String, String> currencies = new HashMap<String, String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		String json = readFile(R.raw.exchange_rates);
		exchangeRates = gson.fromJson(json, ExchangeRates.class);

		json = readFile(R.raw.currencies);
		currencies = gson.fromJson(json, currencies.getClass());

		findViewById(R.id.ok_button).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((TextView) findViewById(R.id.result))
						.setText(getBestMatchingCurrency());
			}

		});
	}

	private String getBestMatchingCurrency() {
		
		return "USD";
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
