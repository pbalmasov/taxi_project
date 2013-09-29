package ru.peppers;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.SocketTimeoutException;
import java.security.KeyStore;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;
/**
 * Отсылка запросов на сервер основной метод postData
 * @author p.balmasov
 */
final public class PhpData {
    // static final String NEWURL = "https://www.abs-taxi.ru/fcgi-bin/office/cman.fcgi";;
    static boolean withDebug = true;
    static final String newURL = "https://www.abs-taxi.ru/fcgi-bin/office/cman.fcgi";
    private static final String PREFS_NAME = "MyNamePrefs1";
    static HttpClient httpclient = getNewHttpClient();
    public static boolean errorHappen;

    public static HttpClient getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
            DefaultHttpClient defaultHttpClient = new DefaultHttpClient(ccm, params);

            HttpParams httpParams = defaultHttpClient.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
            HttpConnectionParams.setSoTimeout(httpParams, 5000);
            return defaultHttpClient;
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    static public void errorHandler(Context context, Exception e) {
        String str = "";
        if (e != null) {
            e.printStackTrace();
            if (withDebug) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                str = sw.toString();
            } else
                str = e.toString();
        }
        if (context != null) {
            final Toast tag = Toast.makeText(context, context.getString(R.string.error_message) + " " + str,
                    Toast.LENGTH_LONG);
            if (withDebug) {
                new CountDownTimer(9000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        tag.show();
                    }

                    public void onFinish() {
                        tag.show();
                    }

                }.start();
                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context
                        .getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setText(str);
            } else
                tag.show();
        }
        // new AlertDialog.Builder(context).setTitle(context.getString(R.string.error_title))
        // .setMessage(context.getString(R.string.error_message) + " " + str)
        // .setNeutralButton(context.getString(R.string.close), null).show();
    }

    static public void errorFromServer(Context context, Node errorNode) {
        if (context != null)
            Toast.makeText(context, errorNode.getTextContent(), Toast.LENGTH_LONG).show();
        // new AlertDialog.Builder(context).setTitle(context.getString(R.string.error_title))
        // .setMessage(errorNode.getTextContent())
        // .setNeutralButton(context.getString(R.string.close), null).show();
    }

    static public Document postData(Context activity, List<NameValuePair> nameValuePairs, String url,
            boolean asyncTask) {
        SharedPreferences settings = activity.getSharedPreferences(PREFS_NAME, 0);
        String sessionid = settings.getString("sessionid", null);
        return postData(activity, nameValuePairs, url, sessionid, asyncTask);

    }

    static public Document postData(Context activity, List<NameValuePair> nameValuePairs, String url) {
        SharedPreferences settings = activity.getSharedPreferences(PREFS_NAME, 0);
        String sessionid = settings.getString("sessionid", null);
        return postData(activity, nameValuePairs, url, sessionid, false);
    }

    static public Document postData(Context activity, List<NameValuePair> nameValuePairs, String url,
            String sessionidvar, boolean asyncTask) {
        if (isNetworkAvailable(activity, asyncTask)) {
            // Create a new HttpClient and Post Header
            HttpGet httppost = new HttpGet(url + "?" + URLEncodedUtils.format(nameValuePairs, "utf-8"));
            // http://sandbox.peppers-studio.ru/dell/accelerometer/index.php
            // http://10.0.2.2/api
            try {
                // Add your data
                // httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                Log.d("My_tag", nameValuePairs.toString());
                Log.d("My_tag", "sessionid = " + sessionidvar);
                if (sessionidvar != null)
                    httppost.setHeader("cookie", "cmansid=" + sessionidvar);
                // Execute HTTP Post Request

                HttpResponse response = httpclient.execute(httppost);
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();

                Document doc;
                if (withDebug) {
                    String str = EntityUtils.toString(response.getEntity());
                    Log.d("My_tag", str);
                    InputSource is = new InputSource();
                    is.setCharacterStream(new StringReader(str));
                    // response.getEntity().getContent()
                    doc = builder.parse(is);
                } else {
                    doc = builder.parse(response.getEntity().getContent());

                }

                return doc;

            } catch (ConnectTimeoutException e) {
                if (activity.getClass() != PhpService.class && !asyncTask)
                    Toast.makeText(activity, "Сервер не отвечает. Обратитесь к администратору.",
                            Toast.LENGTH_LONG).show();
                // new AlertDialog.Builder(activity).setTitle(activity.getString(R.string.error_title))
                // .setMessage("Сервер не отвечает. Обратитесь к администратору.")
                // .setNeutralButton(activity.getString(R.string.close), null).show();
            } catch (SocketTimeoutException e) {
                return null;
            } catch (Exception e) {
                if (activity.getClass() != PhpService.class && !asyncTask) {
                    errorHandler(activity, e);
                    errorHappen = true;
                }
            }
        } else {
            if (activity.getClass() != PhpService.class && !asyncTask)
                Toast.makeText(activity, activity.getString(R.string.no_internet), Toast.LENGTH_LONG).show();
            // new AlertDialog.Builder(activity).setTitle(activity.getString(R.string.error_title))
            // .setMessage(activity.getString(R.string.no_internet))
            // .setNeutralButton(activity.getString(R.string.close), null).show();
        }
        Log.d("My_tag", "no connection");
        return null;
    }

    private static void errorHandler(Context context) {
        new AlertDialog.Builder(context).setTitle(context.getString(R.string.error_title))
                .setMessage(context.getString(R.string.error_message))
                .setNeutralButton(context.getString(R.string.close), null).show();
    }

    // static public Document postData(Activity activity, List<NameValuePair> nameValuePairs) {
    //
    // return postData(activity, nameValuePairs,
    // "http://sandbox.peppers-studio.ru/dell/accelerometer/index.php");
    //
    // }
    public static boolean isNetworkAvailable(Context context) {
        return isNetworkAvailable(context, false);
    }

    public static boolean isNetworkAvailable(Context context, boolean asyncTask) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isNetwork = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        if (!isNetwork && !asyncTask)
            Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_LONG).show();
        return isNetwork;
    }

}
