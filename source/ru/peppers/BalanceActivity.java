package ru.peppers;

import android.app.Activity;
import android.os.Bundle;

public class BalanceActivity extends Activity {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(TaxiApplication.getDriver()!=null)
        setTitle(this.getTitle() + " Баланс: "+ TaxiApplication.getDriver().getBalance());
	}
}
