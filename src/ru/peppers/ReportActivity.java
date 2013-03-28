package ru.peppers;

import hello.Driver;
import hello.Order;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ReportActivity extends Activity {
    private ArrayAdapter<String> simpleAdpt;
    private ArrayList<String> itemsList;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report);

        Bundle bundle = getIntent().getExtras();
        int id = bundle.getInt("id");

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("action", "reportdata"));
        nameValuePairs.add(new BasicNameValuePair("id", String.valueOf(id)));

        Document doc = PhpData.postData(this, nameValuePairs);
        if (doc != null) {
            Node errorNode = doc.getElementsByTagName("error").item(0);

            if (Integer.parseInt(errorNode.getTextContent()) == 1)
                new AlertDialog.Builder(this).setTitle("Ошибка")
                        .setMessage("Ошибка на сервере. Перезапустите приложение.")
                        .setNeutralButton("Закрыть", null).show();
            else {
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
        } else {
            initList();
        }
    }

    private void initMainList(Document doc) throws DOMException, ParseException {
        NodeList nodeList = doc.getElementsByTagName("order");
        ArrayList<Order> reports = new ArrayList<Order>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            NamedNodeMap attributes = nodeList.item(i).getAttributes();
            int type = Integer.parseInt(attributes.getNamedItem("type").getTextContent());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            Date date = format.parse(attributes.getNamedItem("date").getTextContent());
            String carClass = attributes.getNamedItem("class").getTextContent();
            String adress = attributes.getNamedItem("adress").getTextContent();
            String where = attributes.getNamedItem("where").getTextContent();

            if (type == 0) {
                int cost = Integer.parseInt(attributes.getNamedItem("cost").getTextContent());
                String costType = attributes.getNamedItem("costType").getTextContent();
                String text = nodeList.item(i).getTextContent();
                reports.add(new CostOrder(date, adress, carClass, text, where, cost, costType));
            }
            if (type == 1) {
                String text = nodeList.item(i).getTextContent();
                reports.add(new NoCostOrder(date, adress, carClass, text, where));
            }
            if (type == 2) {
                String text = nodeList.item(i).getTextContent();
                reports.add(new PreliminaryOrder(date, adress, carClass, text, where));
            }
        }
        int balance = Integer.parseInt(doc.getElementsByTagName("balance").item(0).getTextContent());
        Driver driver = TaxiApplication.getDriver();
        driver.setBalance(balance);
        driver.setReports(reports);
        initList();
    }

    public void initList() {
        final Driver driver = TaxiApplication.getDriver();
        //номер в очереди в районе и в общей
        TextView balance = (TextView) findViewById(R.id.textView1);
        TextView number1 = (TextView) findViewById(R.id.textView2);
        TextView number2 = (TextView) findViewById(R.id.textView3);
        TextView number3 = (TextView) findViewById(R.id.textView4);
        balance.setText("Баланс: " + driver.getBalance() + " р.");
        number1.setText("В очереди: " + 5);
        number2.setText("В районе: " + 6);
        number3.setText("В общей: " + 7);
        itemsList = new ArrayList<String>();
        itemsList.add("Статус: " + driver.getStatusString());
        itemsList.add("Класс: " + driver.getClassAutoString());
        itemsList.add("Отчет: " + driver.reportsCount());

        ListView lv = (ListView) findViewById(R.id.listView1);

         simpleAdpt = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, itemsList);

        lv.setAdapter(simpleAdpt);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long index) {
                Bundle extras = getIntent().getExtras();
                int id = extras.getInt("id");
                Intent intent;
                Bundle bundle;
                if (position == 2) {
                    intent = new Intent(ReportActivity.this, ReportListActivity.class);
                    bundle = new Bundle();
                    bundle.putInt("id", id);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                if (position == 1) {
                    final CharSequence[] items = { "Эконом", "Стандарт", "Базовый" };
                    AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
                    builder.setTitle("Выбор статуса");
                    builder.setSingleChoiceItems(items, driver.getClassAuto(),
                            onClassContextMenuItemListener(position));
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                if (position == 0) {
                    final CharSequence[] items = { "Свободен", "Перерыв", "Недоступен" };
                    AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
                    builder.setTitle("Выбор статуса");
                    builder.setSingleChoiceItems(items, driver.getStatus(),
                            onStatusContextMenuItemListener(position));
                    AlertDialog alert = builder.create();
                    alert.show();
                }

            }
        });
    }
    private OnClickListener onStatusContextMenuItemListener(final int position) {
        return new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Driver driver = TaxiApplication.getDriver();
                if (item != driver.getStatus()) {

                    if (item == 0 && driver.getDistrict() == "") {
                        Bundle extras = getIntent().getExtras();
                        int id = extras.getInt("id");
                        Intent intent = new Intent(ReportActivity.this, DistrictActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("id", id);
                        intent.putExtras(bundle);
                        startActivity(intent);

                        dialog.dismiss();
                        return;
                    }

                    if (item == 2 && driver.getOrdersCount() != 0) {
                        new AlertDialog.Builder(ReportActivity.this).setTitle("Заказы")
                                .setMessage("К сожалению у вас есть не закрытые заказы.")
                                .setNeutralButton("Закрыть", null).show();
                        dialog.dismiss();
                        return;
                    }

                    // TODO:goto district activity

                    driver.setStatus(item);
//                    if (item != 1 && itemsList.size() == 8) {
//                        String rayonString = "";
//                        if (driver.getDistrict() != "")
//                            rayonString = driver.getDistrict() + "," + driver.getSubdistrict();
//                        else
//                            rayonString = "не выбран";
//                        itemsList.add(3, createItem("item", "Район: " + rayonString));
//                    }
//                    if (item == 1)
//                        itemsList.remove(3);
                    itemsList.set(position, "Статус: " + driver.getStatusString());
                    simpleAdpt.notifyDataSetChanged();
                    // TODO:post to php data
                }
                dialog.dismiss();
            }
        };
    }

    private OnClickListener onClassContextMenuItemListener(final int position) {
        return new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Driver driver = TaxiApplication.getDriver();
                if (item != driver.getClassAuto()) {
                    driver.setClassAuto(item);
                    itemsList.set(position,"Класс: " + driver.getClassAutoString());
                    simpleAdpt.notifyDataSetChanged();
                    // TODO:post to php data
                }
                dialog.dismiss();
            }
        };
    }

}
