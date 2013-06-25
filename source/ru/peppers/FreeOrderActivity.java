package ru.peppers;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.Driver;
import model.Order;
import orders.CostOrder;
import orders.NoCostOrder;
import orders.PreliminaryOrder;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class FreeOrderActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //Bundle bundle = getIntent().getExtras();
        //int id = bundle.getInt("id");

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("action", "orderdata"));
       // nameValuePairs.add(new BasicNameValuePair("id", String.valueOf(id)));

        Document doc = PhpData.postData(this, nameValuePairs);
        if (doc != null) {
            Node errorNode = doc.getElementsByTagName("error").item(0);
            Node canViewNode = doc.getElementsByTagName("canview").item(0);

            if (Integer.parseInt(errorNode.getTextContent()) == 1)
                new AlertDialog.Builder(this).setTitle("Ошибка")
                        .setMessage("Ошибка на сервере. Перезапустите приложение.")
                        .setNeutralButton("Закрыть", null).show();
            else if (Integer.parseInt(canViewNode.getTextContent()) == 1) {
                new AlertDialog.Builder(this).setTitle("Информация").setMessage("На линии достаточно машин.")
                        .setNeutralButton("Закрыть", new OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Bundle extras = getIntent().getExtras();
                                //int id = extras.getInt("id");

                                Intent intent = new Intent(FreeOrderActivity.this, MainListActivity.class);
                                //Bundle bundle = new Bundle();
                                //bundle.putInt("id", id);
                                //intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        }).show();
            } else {
                try {
                    initMainList(doc);
                } catch (DOMException e) {
                    e.printStackTrace();
                    new AlertDialog.Builder(this).setTitle("Ошибка")
                            .setMessage("Ошибка на сервере. Перезапустите приложение.")
                            .setNeutralButton("Закрыть", null).show();
                } catch (ParseException e) {
                    e.printStackTrace();
                    new AlertDialog.Builder(this).setTitle("Ошибка")
                            .setMessage("Ошибка на сервере. Перезапустите приложение.")
                            .setNeutralButton("Закрыть", null).show();
                }
            }
        }
    }

    private void initMainList(Document doc) throws DOMException, ParseException {
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

            if (type == 0) {
                int cost = Integer.parseInt(attributes.getNamedItem("cost").getTextContent());
                String costType = attributes.getNamedItem("costType").getTextContent();
                String text = nodeList.item(i).getTextContent();
                orders.add(new CostOrder(costOrder,index, date, adress, carClass, text, where, cost, costType));
            }
            if (type == 1) {
                String text = nodeList.item(i).getTextContent();
                orders.add(new NoCostOrder(costOrder,index, date, adress, carClass, text, where));
            }
            if (type == 2) {
                String text = nodeList.item(i).getTextContent();
                orders.add(new PreliminaryOrder(costOrder,index, date, adress, carClass, text, where));
            }
            if(attributes.getNamedItem("abonent") != null){
                String abonent = attributes.getNamedItem("abonent").getTextContent();
                int rides = Integer.parseInt(attributes.getNamedItem("rides").getTextContent());
                orders.get(i).setAbonent(abonent);
                orders.get(i).setRides(rides);
            }
        }

        Driver driver = TaxiApplication.getDriver();
        driver.setFreeOrders(orders);
        // driver = new Driver(status, carClass, ordersCount, district, subdistrict);

        // itemsList = new ArrayList<Map<String, String>>();
        // itemsList.add(createItem("item", "Мои закакзы: " + driver.getOrdersCount()));
        // itemsList.add(createItem("item", "Статус: " + driver.getStatusString()));
        // itemsList.add(createItem("item", "Свободные заказы"));
        // if (driver.getStatus() != 1)
        // itemsList
        // .add(createItem("item", "Район: " + driver.getDistrict() + "," + driver.getSubdistrict()));
        // itemsList.add(createItem("item", "Класс: " + driver.getClassAutoString()));
        // itemsList.add(createItem("item", "Отчет"));
        // itemsList.add(createItem("item", "Звонок из офиса"));
        // itemsList.add(createItem("item", "Настройки"));

        ListView lv = (ListView) findViewById(R.id.mainListView);

        ArrayAdapter<Order> arrayAdapter = new ArrayAdapter<Order>(this, android.R.layout.simple_list_item_1,
                orders);

        lv.setAdapter(arrayAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long index) {
               // Bundle extras = getIntent().getExtras();
                //int id = extras.getInt("id");

                Intent intent = new Intent(FreeOrderActivity.this, FreeOrderItemActivity.class);
                Bundle bundle = new Bundle();
               // bundle.putInt("id", id);
                bundle.putInt("index", position);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}
