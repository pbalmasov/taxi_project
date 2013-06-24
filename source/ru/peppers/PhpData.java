package ru.peppers;

import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

final public class PhpData {
	static boolean withDebug = false;

	static public Document postData(Activity activity,
			List<NameValuePair> nameValuePairs, String url) {
		if (isNetworkAvailable(activity)) {

			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(url);
			// http://sandbox.peppers-studio.ru/dell/accelerometer/index.php
			// http://10.0.2.2/api
			try {
				// Add your data
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httppost);
				DocumentBuilderFactory factory = DocumentBuilderFactory
						.newInstance();
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

			} catch (ClientProtocolException e) {
				e.printStackTrace();
				new AlertDialog.Builder(activity)
						.setTitle("Ошибка")
						.setMessage("Произошла ошибка в соединение с сервером.")
						.setNeutralButton("Закрыть", null).show();
			} catch (IOException e) {
				e.printStackTrace();
				new AlertDialog.Builder(activity)
						.setTitle("Ошибка")
						.setMessage("Произошла ошибка в соединение с сервером.")
						.setNeutralButton("Закрыть", null).show();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
				new AlertDialog.Builder(activity).setTitle("Ошибка")
						.setMessage("Ошибка в обработке ответа от сервера.")
						.setNeutralButton("Закрыть", null).show();
			} catch (IllegalStateException e) {
				e.printStackTrace();
				new AlertDialog.Builder(activity).setTitle("Ошибка")
						.setMessage("Ошибка в обработке ответа от сервера.")
						.setNeutralButton("Закрыть", null).show();
			} catch (SAXException e) {
				e.printStackTrace();
				new AlertDialog.Builder(activity).setTitle("Ошибка")
						.setMessage("Ошибка в обработке ответа от сервера.")
						.setNeutralButton("Закрыть", null).show();
			}
		} else {
			new AlertDialog.Builder(activity).setTitle("Ошибка")
					.setMessage("Подключение к интернету отсутствует.")
					.setNeutralButton("Закрыть", null).show();
		}
		Log.d("My_tag", "no connection");
		return null;
	}

	static public Document postData(Activity activity,
			List<NameValuePair> nameValuePairs) {

		return postData(activity, nameValuePairs,
				"http://sandbox.peppers-studio.ru/dell/accelerometer/index.php");

	}

	private static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	public static Date getFileDate(Activity activity) {
		if (isNetworkAvailable(activity)) {

			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpHead httpphead = new HttpHead(
					"http://sandbox.peppers-studio.ru/dell/accelerometer/TaxiProject.apk");
			// http://sandbox.peppers-studio.ru/dell/accelerometer/index.php
			// http://10.0.2.2/api
			try {

				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httpphead);
				// for(int i = 0; i<response.getAllHeaders().length;i++){
				// Log.d("My_tag",response.getAllHeaders()[i].toString());
				// }
				Log.d("My_tag", response.getFirstHeader("Last-Modified")
						.getValue());

				return new Date(response.getFirstHeader("Last-Modified")
						.getValue());

			} catch (ClientProtocolException e) {
				e.printStackTrace();
				new AlertDialog.Builder(activity)
						.setTitle("Ошибка")
						.setMessage("Произошла ошибка в соединение с сервером.")
						.setNeutralButton("Закрыть", null).show();
			} catch (IOException e) {
				e.printStackTrace();
				new AlertDialog.Builder(activity)
						.setTitle("Ошибка")
						.setMessage("Произошла ошибка в соединение с сервером.")
						.setNeutralButton("Закрыть", null).show();
			} catch (IllegalStateException e) {
				e.printStackTrace();
				new AlertDialog.Builder(activity).setTitle("Ошибка")
						.setMessage("Ошибка в обработке ответа от сервера.")
						.setNeutralButton("Закрыть", null).show();
			}
		} else {
			new AlertDialog.Builder(activity).setTitle("Ошибка")
					.setMessage("Подключение к интернету отсутствует.")
					.setNeutralButton("Закрыть", null).show();
		}
		Log.d("My_tag", "no connection");
		return null;

	}

}
