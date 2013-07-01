package ru.peppers;


import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import model.Driver;
import model.Message;
import model.Order;
import orders.CostOrder;
import orders.NoCostOrder;
import orders.PreliminaryOrder;

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
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class PhpService extends Service {
    NotificationManager nm;

    @Override
    public void onCreate() {
        super.onCreate();

        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, MainListActivity.class);
       // intent.putExtra("id", String.valueOf(TaxiApplication.getDriverId()));
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Notification notif = new Notification(R.drawable.icon, "Запущен сервис уведомлений такси",
                System.currentTimeMillis());
        notif.setLatestEventInfo(this, "Сервис такси", "Запущен сервис уведомлений такси", pIntent);

        // ставим флаг, чтобы уведомление пропало после нажатия
        notif.flags |= Notification.FLAG_AUTO_CANCEL;
        startForeground(0, notif);

    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        // Notification notif = new Notification(R.drawable.ic_launcher, "Text in status bar",
        // System.currentTimeMillis());
        // notif.setLatestEventInfo(this, "Notification's title", "Notification's text", null);
        //
        // // ставим флаг, чтобы уведомление пропало после нажатия
        // notif.flags |= Notification.FLAG_AUTO_CANCEL;
        // startForeground(2, notif);

        // Timer myTimer = new Timer(); // Создаем таймер
        // final Handler uiHandler = new Handler();
        // myTimer.schedule(new TimerTask() { // Определяем задачу
        // @Override
        // public void run() {
        // uiHandler.post(new Runnable() {
        // @Override
        // public void run() {
        // }
        //
        // });
        // };
        // }, 0L, 60L * 1000);


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
          @Override
          public void run() {
              nm.cancelAll();
          }
        }, 10000);


        if (checkConnection()) {
            Document doc = postToServer();
            if (doc != null) {
                Node errorNode = doc.getElementsByTagName("error").item(0);

                if (Integer.parseInt(errorNode.getTextContent()) != 1) {
                    try {
                        getMessages(doc);
                        getOrders(doc);
//
//                        Intent destination = new Intent(PhpService.this, MessagesWithOrders.class);
//                        destination.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        destination.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                        Bundle bundle = new Bundle();
//                        bundle.putParcelableArrayList("messages", );
//                        destination.putExtras(bundle);
//                        startActivity(destination);


                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private ArrayList<Message> getMessages(Document doc) throws DOMException, ParseException {
        NodeList nodeList = doc.getElementsByTagName("message");
        ArrayList<Message> unreaded = new ArrayList<Message>();
        ArrayList<Message> readed = new ArrayList<Message>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            NamedNodeMap attributes = nodeList.item(i).getAttributes();

            boolean isRead = Boolean.parseBoolean(attributes.getNamedItem("readed").getTextContent());
            int index = Integer.valueOf(attributes.getNamedItem("index").getTextContent());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            Date date = format.parse(attributes.getNamedItem("date").getTextContent());
            String text = nodeList.item(i).getTextContent();

            Message message = new Message(text, date, isRead, index);
            if (!isRead) {
                unreaded.add(message);
            } else {
                readed.add(message);
            }
        }

        Collections.sort(unreaded);
        Collections.sort(readed);

        for (int i = 0; i < unreaded.size(); i++) {

            Log.d("My_tag", unreaded.get(i).getText());
            Intent intent = new Intent(this, MessageFromServiceActivity.class);
            intent.putExtra(MessageFromServiceActivity.TITLE, unreaded.get(i).getDate().toGMTString());
            intent.putExtra(MessageFromServiceActivity.MESSAGE, unreaded.get(i).getText());
            PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
            Notification notif = new Notification(R.drawable.icon, "Новое сообщение",
                    System.currentTimeMillis());
            notif.setLatestEventInfo(this, "Сообщение", unreaded.get(i).getText(), pIntent);

            // ставим флаг, чтобы уведомление пропало после нажатия
            notif.flags |= Notification.FLAG_AUTO_CANCEL;
            notif.sound = Uri.parse("android.resource://ru.peppers/" + R.raw.sound);

            notif.defaults |= Notification.DEFAULT_VIBRATE;
            notif.defaults |= Notification.DEFAULT_LIGHTS;

            notif.flags |= Notification.FLAG_NO_CLEAR;
            // отправляем
            nm.notify(i + 1, notif);
        }

        return unreaded;

    }

    private ArrayList<Order> getOrders(Document doc) throws DOMException, ParseException {
        NodeList nodeList = doc.getElementsByTagName("order");
        ArrayList<Order> orders = new ArrayList<Order>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            NamedNodeMap attributes = nodeList.item(i).getAttributes();

            int index = Integer.parseInt(attributes.getNamedItem("index").getTextContent());
            int type = Integer.parseInt(attributes.getNamedItem("type").getTextContent());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            Date date = format.parse(attributes.getNamedItem("date").getTextContent());
            String carClass = attributes.getNamedItem("class").getTextContent();
            String adress = attributes.getNamedItem("adress").getTextContent();
            String where = attributes.getNamedItem("where").getTextContent();
            int costOrder = Integer.parseInt(attributes.getNamedItem("costOrder").getTextContent());


            Intent intent = new Intent(this, FreeOrderItemActivity.class);
           // intent.putExtra("id", TaxiApplication.getDriverId());
            intent.putExtra("orderindex", index);
            intent.putExtra("service", true);
            intent.putExtra("type", type);
            intent.putExtra("date", attributes.getNamedItem("date").getTextContent());
            intent.putExtra("class", carClass);
            intent.putExtra("adress", adress);
            intent.putExtra("where", where);
            intent.putExtra("costOrder", costOrder);


            if (type == 0) {
                int cost = Integer.parseInt(attributes.getNamedItem("cost").getTextContent());
                String costType = attributes.getNamedItem("costType").getTextContent();
                String text = nodeList.item(i).getTextContent();
                intent.putExtra("cost", cost);
                intent.putExtra("costType", costType);
                intent.putExtra("text", text);
                orders.add(new CostOrder(this,costOrder, index, date, adress, carClass, text, where, cost,
                        costType));
            }
            if (type == 1) {
                String text = nodeList.item(i).getTextContent();
                intent.putExtra("text", text);
                orders.add(new NoCostOrder(this,costOrder, index, date, adress, carClass, text, where));
            }
            if (type == 2) {
                String text = nodeList.item(i).getTextContent();
                intent.putExtra("text", text);
                orders.add(new PreliminaryOrder(this,costOrder, index, date, adress, carClass, text, where));
            }


            PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
            Notification notif = new Notification(R.drawable.icon, "Новый заказ", System.currentTimeMillis());
            notif.setLatestEventInfo(this, "Заказ", orders.get(i).toString(), pIntent);

            // ставим флаг, чтобы уведомление пропало после нажатия
            notif.flags |= Notification.FLAG_AUTO_CANCEL;
            notif.sound = Uri.parse("android.resource://ru.peppers/" + R.raw.sound);

            notif.defaults |= Notification.DEFAULT_VIBRATE;
            notif.defaults |= Notification.DEFAULT_LIGHTS;

            notif.flags |= Notification.FLAG_NO_CLEAR;
            // отправляем
            NodeList nodeList1 = doc.getElementsByTagName("message");
            nm.notify(nodeList1.getLength() + i + 1, notif);

        }

        return orders;

    }

    private Document postToServer() {

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("action", "messageandserverdata"));
       // nameValuePairs.add(new BasicNameValuePair("id", String.valueOf(TaxiApplication.getDriverId())));

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://sandbox.peppers-studio.ru/dell/accelerometer/index.php");
        // http://sandbox.peppers-studio.ru/dell/accelerometer/index.php
        // http://10.0.2.2/api
        try {
            // Add your data
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
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
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public IBinder onBind(Intent arg0) {
        return null;
    }

}
