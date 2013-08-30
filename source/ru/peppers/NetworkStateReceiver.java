package ru.peppers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkStateReceiver extends BroadcastReceiver {

    private boolean dced = false;

    public void onReceive(Context context, Intent intent) {
        Log.d("app", "Network connectivity change");
        if (intent.getExtras() != null) {
            NetworkInfo ni = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
                if (dced)
                    if (TaxiApplication.getDriver(null) != null) {
                        dced = false;
                        // if(TaxiApplication.getDriver().getStatus()!=3)
                        TaxiApplication.getDriver(null).setStatus(1);

                        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                        nameValuePairs.add(new BasicNameValuePair("action", "status"));
                        //nameValuePairs.add(new BasicNameValuePair("id", String.valueOf(TaxiApplication
                         //       .getDriverId())));
                        nameValuePairs.add(new BasicNameValuePair("status", String.valueOf(TaxiApplication
                                .getDriver(null).getStatus())));

                        HttpClient httpclient = new DefaultHttpClient();
                        HttpPost httppost = new HttpPost(
                                "http://sandbox.peppers-studio.ru/dell/accelerometer/index.php");
                        // http://sandbox.peppers-studio.ru/dell/accelerometer/index.php
                        // http://10.0.2.2/api
                        try {
                            // Add your data
                            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                            // Execute HTTP Post Request
                            HttpResponse response = httpclient.execute(httppost);
                            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                            DocumentBuilder builder = factory.newDocumentBuilder();

                            Document doc;
                            doc = builder.parse(response.getEntity().getContent());

                        } catch (ClientProtocolException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ParserConfigurationException e) {
                            e.printStackTrace();
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        } catch (SAXException e) {
                            e.printStackTrace();
                        }

                    }
                // TODO:post to php server
                Log.d("My_tag", "Network " + ni.getTypeName() + " connected");
            }
        }
        if (intent.getExtras().getBoolean(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {
            Log.d("My_tag", "There's no network connectivity");
            dced = true;
        }
    }
}
