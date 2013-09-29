package ru.peppers;

import model.Driver;
import android.app.Activity;
import android.app.Application;

/**
 * Класс приложения для хранения driver
 * @author p.balmasov
 */
public class TaxiApplication extends Application {

    /** The type list. */
    private static Driver driver;

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

//        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Tag");
//        wl.acquire();

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
