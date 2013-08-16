package ru.peppers;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class BalanceActivity extends Activity {

    public TextView title;
    private TextView pozivnoi;
    protected static final String PREFS_NAME = "MyNamePrefs1";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        int isLightTheme = settings.getInt("theme", 0);
        if (isLightTheme != 0)
            setTheme(R.style.CustomLightTheme);
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

        setContentView(R.layout.main);

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);

        pozivnoi = (TextView) findViewById(R.id.pozivnoiView);
        if (isLightTheme != 0)
            pozivnoi.setTextColor(Color.WHITE);
        title = (TextView) findViewById(R.id.titleView);
        if (isLightTheme != 0)
            title.setTextColor(Color.WHITE);
        title.setText(this.getTitle());
        updateData();
    }

    protected void updateData() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String pozivnoidata = settings.getString("pozivnoidata", "");
        if (pozivnoidata.length() != 0)
            pozivnoi.setText("Позывной: " + pozivnoidata);
        if (TaxiApplication.getDriver() != null)
            pozivnoi.append(", Баланс: " + TaxiApplication.getDriver().getBalance());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Handle the back button
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            // Ask the user if they want to quit
            if (!PhpData.isNetworkAvailable(this)) {
                Log.d("My_tag", "on back");
                if (getClass().toString().equalsIgnoreCase("class ru.peppers.PozivnoiActivity"))
                    return super.onKeyDown(keyCode, event);
                return true;
            } else
                return super.onKeyDown(keyCode, event);
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }
}
