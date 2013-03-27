package ru.peppers;

import hello.Driver;
import android.app.Application;
import android.os.Handler;
import android.widget.Toast;

//TODO: Написать кастомную активити с онпауз
// и таймером

/**
 * The Class ShopApplication.
 */
public class TaxiApplication extends Application {

    /** The type list. */
    private static Driver driver;

    @Override
    public void onCreate() {
        super.onCreate();
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            public void run() {
//                Toast.makeText(getApplicationContext(), "Run from timer", Toast.LENGTH_SHORT).show();
//            }
//        }, 2000);

    }

    public static Driver getDriver() {
        return driver;
    }

    public static void setDriver(Driver driver) {
        TaxiApplication.driver = driver;
    }

}
