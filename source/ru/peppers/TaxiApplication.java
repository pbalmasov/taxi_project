package ru.peppers;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.ObjectInputStream.GetField;
import java.lang.Thread.UncaughtExceptionHandler;

import model.Driver;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.PowerManager;
import android.widget.Toast;

/**
 * The Class ShopApplication.
 */
public class TaxiApplication extends Application {

    /** The type list. */
    private static Driver driver;
    public static NetworkStateReceiver networkStateReceiver;

    @Override
    public void onCreate() {
        super.onCreate();



        // IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        // networkStateReceiver = new NetworkStateReceiver();
        // registerReceiver(networkStateReceiver, filter);

        // Handler handler = new Handler();
        // handler.postDelayed(new Runnable() {
        // public void run() {
        // Toast.makeText(getApplicationContext(), "Run from timer", Toast.LENGTH_SHORT).show();
        // }
        // }, 2000);
    }

    public static Driver getDriver(Activity context) {
        if (driver == null)
            driver = new Driver(context, 0, 0, null, null);
        return driver;
    }

    public static void setDriver(Driver driver) {
        TaxiApplication.driver = driver;
    }

}
