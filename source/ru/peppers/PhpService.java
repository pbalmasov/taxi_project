package ru.peppers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import model.Message;
import model.Order;

public class PhpService extends Service {
    NotificationManager nm;
    String candidateId = "";
    boolean isStop = true;

    private Timer myTimer = new Timer();
    private Integer refreshperiod = null;
    private boolean start = false;

    private final IBinder binder = new ServiceBinder();

    public class ServiceBinder extends Binder {

        public PhpService getService() {

            return PhpService.this;
        }
    }

    public void setStop(boolean a) {
        this.isStop = a;
    }

    @Override
    public IBinder onBind(Intent intent) {

        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, MainListActivity.class);
        // intent.putExtra("id", String.valueOf(TaxiApplication.getDriverId()));
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Notification notif = new Notification(R.drawable.icon2, this.getString(R.string.service_started),
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
        getStatus();
        // final Timer myTimer = new Timer(); // Создаем таймер
        // final Handler uiHandler = new Handler();
        //
        // final TimerTask timerTask = new TimerTask() { // Определяем задачу
        // @Override
        // public void run() {
        // uiHandler.post(new Runnable() {
        // @Override
        // public void run() {
        // if (isStop)
        // getStatus();
        // }
        // });
        // }
        // };
        //
        // myTimer.schedule(timerTask, 0L, 1000 * 60);

        // final Handler handler = new Handler();
        // handler.postDelayed(new Runnable() {
        // @Override
        // public void run() {;nm.cancelAll();
        // }
        // }, 1000*(new Random()).nextInt(10));

        return super.onStartCommand(intent, flags, startId);
    }

    private void getStatus() {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
        nameValuePairs.add(new BasicNameValuePair("action", "get"));
        nameValuePairs.add(new BasicNameValuePair("module", "mobile"));
        nameValuePairs.add(new BasicNameValuePair("mode", "status"));
        nameValuePairs.add(new BasicNameValuePair("object", "driver"));

        Document doc = PhpData.postData(this, nameValuePairs, PhpData.newURL);
        if (doc != null) {
            Node responseNode = doc.getElementsByTagName("response").item(0);
            Node errorNode = doc.getElementsByTagName("message").item(0);

            if (responseNode.getTextContent().equalsIgnoreCase("failure"))
                PhpData.errorFromServer(this, errorNode);
            else {
                try {
                    parseStatus(doc);
                } catch (Exception e) {
                    PhpData.errorHandler(this, e);
                }
            }
        }
    }

    private void parseStatus(Document doc) {

        Node candidateNode = doc.getElementsByTagName("candidateorderid").item(0);
        String candidate = "";
        if (!candidateNode.getTextContent().equalsIgnoreCase(""))
            candidate = candidateNode.getTextContent();
        Log.d("My_tag", candidate + " " + candidateId);
        if (candidate != "" && !candidateId.equalsIgnoreCase(candidate)) {
            candidateId = candidate;
            Intent intent = new Intent(this, CandidateOrderActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Bundle bundle = new Bundle();
            bundle.putString("id", candidate);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        Node refreshperiodNode = doc.getElementsByTagName("refreshperiod").item(0);
        Integer newrefreshperiod = null;
        if (!refreshperiodNode.getTextContent().equalsIgnoreCase(""))
            newrefreshperiod = Integer.valueOf(refreshperiodNode.getTextContent());

        boolean update = false;

        Log.d("My_tag", refreshperiod + " " + newrefreshperiod + " " + update);

        if (newrefreshperiod != null) {
            if (refreshperiod != newrefreshperiod) {
                refreshperiod = newrefreshperiod;
                update = true;
            }
        }

        Log.d("My_tag", refreshperiod + " " + newrefreshperiod + " " + update);

        if (update && refreshperiod != null) {
            if (start) {
                myTimer.cancel();
                start = true;
                Log.d("My_tag", "cancel timer");
            }
            final Handler uiHandler = new Handler();

            TimerTask timerTask = new TimerTask() { // Определяем задачу
                @Override
                public void run() {
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {

                            getMyOrders();

                            if (isStop)
                                getStatus();
                        }

                    });
                }
            };

            myTimer.schedule(timerTask, 1000 * refreshperiod, 1000 * refreshperiod);
        }

    }

    private void getMyOrders() {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
        nameValuePairs.add(new BasicNameValuePair("action", "list"));
        nameValuePairs.add(new BasicNameValuePair("mode", "my"));
        nameValuePairs.add(new BasicNameValuePair("module", "mobile"));
        nameValuePairs.add(new BasicNameValuePair("object", "order"));
        Document doc = PhpData.postData(this, nameValuePairs, PhpData.newURL);
        if (doc != null) {
            Node responseNode = doc.getElementsByTagName("response").item(0);
            Node errorNode = doc.getElementsByTagName("message").item(0);

            if (responseNode.getTextContent().equalsIgnoreCase("failure"))
                PhpData.errorFromServer(this, errorNode);
            else {
                try {
                    parseOrders(doc);
                } catch (Exception e) {
                    PhpData.errorHandler(this, e);
                }
            }
        }
    }

    private void parseOrders(Document doc) {

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
            int index = Integer.valueOf(item.getElementsByTagName("messageid").item(0).getTextContent());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
            Date date = format.parse(item.getElementsByTagName("postdate").item(0).getTextContent());
            String text = item.getElementsByTagName("message").item(0).getTextContent();

            Message message = new Message(text, date, isRead, index);
            if (!isRead) {
                all.add(message);
            }

        }

        for (int i = 0; i < all.size(); i++) {

            Log.d("My_tag", all.get(i).getText());
            Intent intent = new Intent(this, MessageFromServiceActivity.class);
            intent.putExtra(MessageFromServiceActivity.TITLE, all.get(i).getDate().toGMTString());
            intent.putExtra(MessageFromServiceActivity.MESSAGE, all.get(i).getText());
            PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
            Notification notif = new Notification(R.drawable.icon2, this.getString(R.string.new_message),
                    System.currentTimeMillis());
            notif.setLatestEventInfo(this, this.getString(R.string.message), all.get(i).getText(), pIntent);

            // ставим флаг, чтобы уведомление пропало после нажатия
            notif.flags |= Notification.FLAG_AUTO_CANCEL;
            notif.sound = Uri.parse("android.resource://ru.peppers/" + R.raw.sound);

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
                PhpData.errorHandler(null, e);
            }
        }

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

}
