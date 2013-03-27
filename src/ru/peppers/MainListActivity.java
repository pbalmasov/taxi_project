package ru.peppers;

import hello.Driver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MainListActivity extends Activity {
    private ListView lv;
    private SimpleAdapter simpleAdpt;
    private List<Map<String, String>> itemsList;
    private static final String MY_TAG = "My_tag";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Bundle bundle = getIntent().getExtras();
        int id = bundle.getInt("id");

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("action", "mainlist"));
        nameValuePairs.add(new BasicNameValuePair("id", String.valueOf(id)));

        Document doc = PhpData.postData(this, nameValuePairs);
        Node errorNode = doc.getElementsByTagName("error").item(0);

        if (Integer.parseInt(errorNode.getTextContent()) == 1)
            new AlertDialog.Builder(this).setTitle("Ошибка")
                    .setMessage("Ошибка на сервере. Перезапустите приложение.")
                    .setNeutralButton("Закрыть", null).show();
        else {
            initMainList(doc);
        }

    }

    private void initMainList(Document doc) {
        int ordersCount = Integer.valueOf(doc.getElementsByTagName("ordersCount").item(0).getTextContent());
        int carClass = Integer.valueOf(doc.getElementsByTagName("carClass").item(0).getTextContent());
        int status = Integer.valueOf(doc.getElementsByTagName("status").item(0).getTextContent());
        String district = doc.getElementsByTagName("district").item(0).getTextContent();
        String subdistrict = doc.getElementsByTagName("subdistrict").item(0).getTextContent();

        TaxiApplication.setDriver(new Driver(status, carClass, ordersCount, district, subdistrict));
        final Driver driver = TaxiApplication.getDriver();

        itemsList = new ArrayList<Map<String, String>>();
        itemsList.add(createItem("item", "Мои заказы: " + driver.getOrdersCount()));
        itemsList.add(createItem("item", "Статус: " + driver.getStatusString()));
        itemsList.add(createItem("item", "Свободные заказы"));
        if (driver.getStatus() != 1)
            itemsList
                    .add(createItem("item", "Район: " + driver.getDistrict() + "," + driver.getSubdistrict()));
        itemsList.add(createItem("item", "Класс: " + driver.getClassAutoString()));
        itemsList.add(createItem("item", "Отчет"));
        itemsList.add(createItem("item", "Звонок из офиса"));
        itemsList.add(createItem("item", "Настройки"));
        itemsList.add(createItem("item", "Сообщения"));

        lv = (ListView) findViewById(R.id.mainListView);

        simpleAdpt = new SimpleAdapter(this, itemsList, android.R.layout.simple_list_item_1,
                new String[] { "item" }, new int[] { android.R.id.text1 });

        lv.setAdapter(simpleAdpt);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long index) {
                Bundle extras = getIntent().getExtras();
                int id = extras.getInt("id");
                Intent intent;
                Bundle bundle;
                switch (position) {
                    case 0:
                        intent = new Intent(MainListActivity.this, MyOrderActivity.class);
                        bundle = new Bundle();
                        bundle.putInt("id", id);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;
                    case 1:
                        final CharSequence[] items = { "Свободен", "Перерыв", "Недоступен" };
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainListActivity.this);
                        builder.setTitle("Выбор статуса");
                        builder.setSingleChoiceItems(items, driver.getStatus(),
                                onStatusContextMenuItemListener(position));
                        AlertDialog alert = builder.create();
                        alert.show();
                        break;
                    case 2:
                        intent = new Intent(MainListActivity.this, FreeOrderActivity.class);
                        bundle = new Bundle();
                        bundle.putInt("id", id);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;
                    case 3:
                        if (driver.getStatus() != 1) {
                            intent = new Intent(MainListActivity.this, DistrictActivity.class);
                            bundle = new Bundle();
                            bundle.putInt("id", id);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            return;
                        }
                    default:
                        break;
                }
                if (driver.getStatus() != 1)
                    position--;
                if (position == 3) {
                    final CharSequence[] items = { "Эконом", "Стандарт", "Базовый" };
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainListActivity.this);
                    builder.setTitle("Выбор статуса");
                    builder.setSingleChoiceItems(items, driver.getClassAuto(),
                            onClassContextMenuItemListener(position));
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                if (position == 4) {
                    intent = new Intent(MainListActivity.this, ReportActivity.class);
                    bundle = new Bundle();
                    bundle.putInt("id", id);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                if (position == 5) {
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                    nameValuePairs.add(new BasicNameValuePair("action", "calloffice"));
                    nameValuePairs.add(new BasicNameValuePair("id", String.valueOf(id)));

                    Document doc = PhpData.postData(MainListActivity.this, nameValuePairs);
                    Node errorNode = doc.getElementsByTagName("error").item(0);

                    if (Integer.parseInt(errorNode.getTextContent()) == 1)
                        new AlertDialog.Builder(MainListActivity.this).setTitle("Ошибка")
                                .setMessage("Ошибка на сервере. Перезапустите приложение.")
                                .setNeutralButton("Закрыть", null).show();
                    else {
                        new AlertDialog.Builder(MainListActivity.this).setTitle("Ок")
                                .setMessage("Ваш звонок принят. Ожидайте звонка.")
                                .setNeutralButton("Закрыть", null).show();
                    }
                }
                if (position == 6) {
                    intent = new Intent(MainListActivity.this, SettingsActivity.class);
                    bundle = new Bundle();
                    bundle.putInt("id", id);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                if (position == 7) {
                    intent = new Intent(MainListActivity.this, MessageActivity.class);
                    bundle = new Bundle();
                    bundle.putInt("id", id);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
    }

    private OnClickListener onStatusContextMenuItemListener(final int position) {
        return new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Driver driver = TaxiApplication.getDriver();
                if (item != driver.getStatus()) {
                    driver.setStatus(item);
                    if (item != 1 && itemsList.size() == 8)
                        itemsList.add(
                                3,
                                createItem("item",
                                        "Район: " + driver.getDistrict() + "," + driver.getSubdistrict()));
                    if (item == 1)
                        itemsList.remove(3);
                    itemsList.set(position, createItem("item", "Статус: " + driver.getStatusString()));
                    simpleAdpt.notifyDataSetChanged();
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
                    itemsList.set(position, createItem("item", "Класс: " + driver.getClassAutoString()));
                    simpleAdpt.notifyDataSetChanged();
                }
                dialog.dismiss();
            }
        };
    }

    private HashMap<String, String> createItem(String key, String name) {
        HashMap<String, String> item = new HashMap<String, String>();
        item.put(key, name);

        return item;
    }

}
