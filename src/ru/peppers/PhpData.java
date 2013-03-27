package ru.peppers;

import java.io.IOException;
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
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.AlertDialog;

final public class PhpData {
    static public Document postData(Activity activity, List<NameValuePair> nameValuePairs) {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://sandbox.peppers-studio.ru/dell/accelerometer/index.php");
        //http://sandbox.peppers-studio.ru/dell/accelerometer/index.php
//http://10.0.2.2
        try {
            // Add your data
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);

            // Log.d(MY_TAG, response.getEntity().getContent().toString());
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(response.getEntity().getContent());

            return doc;

        } catch (ClientProtocolException e) {
            new AlertDialog.Builder(activity).setTitle("Ошибка")
                    .setMessage("Произошла ошибка в соединение с сервером.")
                    .setNeutralButton("Закрыть", null).show();
        } catch (IOException e) {
            new AlertDialog.Builder(activity).setTitle("Ошибка")
                    .setMessage("Произошла ошибка в соединение с сервером.")
                    .setNeutralButton("Закрыть", null).show();
        } catch (ParserConfigurationException e) {
            new AlertDialog.Builder(activity).setTitle("Ошибка")
                    .setMessage("Ошибка в обработке ответа от сервера.").setNeutralButton("Закрыть", null)
                    .show();
        } catch (IllegalStateException e) {
            new AlertDialog.Builder(activity).setTitle("Ошибка")
                    .setMessage("Ошибка в обработке ответа от сервера.").setNeutralButton("Закрыть", null)
                    .show();
        } catch (SAXException e) {
            new AlertDialog.Builder(activity).setTitle("Ошибка")
                    .setMessage("Ошибка в обработке ответа от сервера.").setNeutralButton("Закрыть", null)
                    .show();
        }
        return null;
    }
}
