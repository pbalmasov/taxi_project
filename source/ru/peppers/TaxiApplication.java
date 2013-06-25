package ru.peppers;

import model.Driver;
import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.PowerManager;

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

        //IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        //networkStateReceiver = new NetworkStateReceiver();
        //registerReceiver(networkStateReceiver, filter);



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
