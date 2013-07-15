package ru.peppers;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class BalanceActivity extends Activity {

	public TextView title;
	private TextView balance;
	protected static final String PREFS_NAME = "MyNamePrefs1";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		int isLightTheme = settings.getInt("theme", 0);
		if (isLightTheme != 0)
			setTheme(android.R.style.Theme_Light);
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

		setContentView(R.layout.main);

		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);

		title = (TextView) findViewById(R.id.titleView);
		if (isLightTheme != 0)
			title.setTextColor(Color.WHITE);
		balance = (TextView) findViewById(R.id.balanceView);
		if (isLightTheme != 0)
			balance.setTextColor(Color.WHITE);
		title.setText(this.getTitle());
		updateBalance();
	}

	public void updateBalance() {
		if (TaxiApplication.getDriver() != null)
			balance.setText("Баланс: " + TaxiApplication.getDriver().getBalance());
	}
}
