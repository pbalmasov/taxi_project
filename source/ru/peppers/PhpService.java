package ru.peppers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import model.Message;
import model.Order;

public class PhpService extends Service {
    NotificationManager nm;

    @Override
    public void onCreate() {
        super.onCreate();

        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, MainListActivity.class);
        // intent.putExtra("id", String.valueOf(TaxiApplication.getDriverId()));
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Notification notif = new Notification(R.drawable.icon2,
                this.getString(R.string.service_started),
                System.currentTimeMillis());
        notif.setLatestEventInfo(this, this.getString(R.string.service),
                this.getString(R.string.service_started), pIntent);

        // ставим флаг, чтобы уведомление пропало после нажатия
        notif.flags |= Notification.FLAG_AUTO_CANCEL;
        startForeground(0, notif);

    }

    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        // Notification notif = new Notification(R.drawable.ic_launcher,
        // "Text in status bar",
        // System.currentTimeMillis());
        // notif.setLatestEventInfo(this, "Notification's title",
        // "Notification's text", null);
        //
        // // ставим флаг, чтобы уведомление пропало после нажатия
        // notif.flags |= Notification.FLAG_AUTO_CANCEL;
        // startForeground(2, notif);

//        final Timer myTimer = new Timer(); // Создаем таймер
//        final Handler uiHandler = new Handler();
//
//        final TimerTask timerTask = new TimerTask() { // Определяем задачу
//            @Override
//            public void run() {
//                uiHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.d("My_tag","1111");
//                        myTimer.cancel();
//                    }
//                });
//            }
//        };
//
//        myTimer.schedule(timerTask, 0L, 1000*(new Random()).nextInt(10));

//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {;nm.cancelAll();
//            }
//        }, 1000*(new Random()).nextInt(10));

        if (checkConnection()) {
            //	getMessages();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void initMainList(Document doc) throws DOMException, ParseException {
        NodeList nodeList = doc.getElementsByTagName("item");
        ArrayList<Message> all = new ArrayList<Message>();
        // ArrayList<Message> readed = new ArrayList<Message>();
        for (int i = 0; i < nodeList.getLength(); i++) {

            Element item = (Element) nodeList.item(i);
            boolean isRead = true;
            if (item.getElementsByTagName("readdate").item(0) == null)
                isRead = false;
            int index = Integer.valueOf(item.getElementsByTagName("messageid")
                    .item(0).getTextContent());
            SimpleDateFormat format = new SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mmZ");
            Date date = format.parse(item.getElementsByTagName("postdate")
                    .item(0).getTextContent());
            String text = item.getElementsByTagName("message").item(0)
                    .getTextContent();

            Message message = new Message(text, date, isRead, index);
            // if (!isRead) {
            all.add(message);
            // } else {
            // readed.add(message);
            // }

        }

        for (int i = 0; i < all.size(); i++) {

            Log.d("My_tag", all.get(i).getText());
            Intent intent = new Intent(this, MessageFromServiceActivity.class);
            intent.putExtra(MessageFromServiceActivity.TITLE, all.get(i)
                    .getDate().toGMTString());
            intent.putExtra(MessageFromServiceActivity.MESSAGE, all.get(i)
                    .getText());
            PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent,
                    0);
            Notification notif = new Notification(R.drawable.icon2,
                    this.getString(R.string.new_message),
                    System.currentTimeMillis());
            notif.setLatestEventInfo(this, this.getString(R.string.message),
                    all.get(i).getText(), pIntent);

            // ставим флаг, чтобы уведомление пропало после нажатия
            notif.flags |= Notification.FLAG_AUTO_CANCEL;
            notif.sound = Uri.parse("android.resource://ru.peppers/"
                    + R.raw.sound);

            notif.defaults |= Notification.DEFAULT_VIBRATE;
            notif.defaults |= Notification.DEFAULT_LIGHTS;

            notif.flags |= Notification.FLAG_NO_CLEAR;
            // отправляем
            nm.notify(i + 1, notif);
        }

    }

    private void getMessages() {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
        nameValuePairs.add(new BasicNameValuePair("action", "list"));
        nameValuePairs.add(new BasicNameValuePair("module", "mobile"));
        nameValuePairs.add(new BasicNameValuePair("object", "message"));

        Document doc = PhpData.postData(this, nameValuePairs, PhpData.newURL);
        if (doc != null) {
            // Node errorNode = doc.getElementsByTagName("error").item(0);
            //
            // if (Integer.parseInt(errorNode.getTextContent()) == 1)
            // new AlertDialog.Builder(this).setTitle("Ошибка")
            // .setMessage("Ошибка на сервере. Перезапустите приложение.")
            // .setNeutralButton("Закрыть", null).show();
            // else {
            try {
                initMainList(doc);
            } catch (Exception e) {
                PhpData.errorHandler(this, e);
            }
        }

    }

    private ArrayList<Order> getOrders(Document doc) throws DOMException,
            ParseException {
        NodeList nodeList = doc.getElementsByTagName("order");
        ArrayList<Order> orders = new ArrayList<Order>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            NamedNodeMap attributes = nodeList.item(i).getAttributes();

            int index = Integer.parseInt(attributes.getNamedItem("index")
                    .getTextContent());
            int type = Integer.parseInt(attributes.getNamedItem("type")
                    .getTextContent());
            SimpleDateFormat format = new SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ssZ");
            Date date = format.parse(attributes.getNamedItem("date")
                    .getTextContent());
            String carClass = attributes.getNamedItem("class").getTextContent();
            String adress = attributes.getNamedItem("adress").getTextContent();
            String where = attributes.getNamedItem("where").getTextContent();
            int costOrder = Integer.parseInt(attributes.getNamedItem(
                    "costOrder").getTextContent());

            Intent intent = new Intent(this, FreeOrderItemActivity.class);
            // intent.putExtra("id", TaxiApplication.getDriverId());
            intent.putExtra("orderindex", index);
            intent.putExtra("service", true);
            intent.putExtra("type", type);
            intent.putExtra("date", attributes.getNamedItem("date")
                    .getTextContent());
            intent.putExtra("class", carClass);
            intent.putExtra("adress", adress);
            intent.putExtra("where", where);
            intent.putExtra("costOrder", costOrder);

            if (type == 0) {
                int cost = Integer.parseInt(attributes.getNamedItem("cost")
                        .getTextContent());
                String costType = attributes.getNamedItem("costType")
                        .getTextContent();
                String text = nodeList.item(i).getTextContent();
                intent.putExtra("cost", cost);
                intent.putExtra("costType", costType);
                intent.putExtra("text", text);
                // orders.add(new CostOrder(this,costOrder, index, date, adress,
                // carClass, text, where, cost,
                // costType));
            }
            if (type == 1) {
                String text = nodeList.item(i).getTextContent();
                intent.putExtra("text", text);
                // orders.add(new NoCostOrder(this,costOrder, index, date,
                // adress, carClass, text, where));
            }
            if (type == 2) {
                String text = nodeList.item(i).getTextContent();
                intent.putExtra("text", text);
                // orders.add(new PreliminaryOrder(this,costOrder, index, date,
                // adress, carClass, text, where));
            }

            // PendingIntent pIntent = PendingIntent.getActivity(this, 0,
            // intent, 0);
            // Notification notif = new Notification(R.drawable.icon,
            // this.getString(R.string.new_order), System.currentTimeMillis());
            // notif.setLatestEventInfo(this, "Заказ", orders.get(i).toString(),
            // pIntent);
            //
            // // ставим флаг, чтобы уведомление пропало после нажатия
            // notif.flags |= Notification.FLAG_AUTO_CANCEL;
            // notif.sound = Uri.parse("android.resource://ru.peppers/" +
            // R.raw.sound);
            //
            // notif.defaults |= Notification.DEFAULT_VIBRATE;
            // notif.defaults |= Notification.DEFAULT_LIGHTS;
            //
            // notif.flags |= Notification.FLAG_NO_CLEAR;
            // // отправляем
            // NodeList nodeList1 = doc.getElementsByTagName("message");
            // nm.notify(nodeList1.getLength() + i + 1, notif);

        }

        return orders;

    }

    private Document postToServer() {

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("action",
                "messageandserverdata"));
        // nameValuePairs.add(new BasicNameValuePair("id",
        // String.valueOf(TaxiApplication.getDriverId())));

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
            DocumentBuilderFactory factory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document doc = builder.parse(response.getEntity().getContent());
            return doc;
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
        return null;
    }

    private boolean checkConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public IBinder onBind(Intent arg0) {
        return null;
    }

}
