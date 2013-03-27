package ru.peppers;

import hello.Driver;
import hello.Order;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleExpandableListAdapter;

public class ReportActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Bundle bundle = getIntent().getExtras();
        int id = bundle.getInt("id");

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("action", "reportdata"));
        nameValuePairs.add(new BasicNameValuePair("id", String.valueOf(id)));

        Document doc = PhpData.postData(this, nameValuePairs);
        Node errorNode = doc.getElementsByTagName("error").item(0);

        if (Integer.parseInt(errorNode.getTextContent()) == 1)
            new AlertDialog.Builder(this).setTitle("Ошибка")
                    .setMessage("Ошибка на сервере. Перезапустите приложение.")
                    .setNeutralButton("Закрыть", null).show();
        else{
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

        ArrayList<String> itemsList = new ArrayList<String>();
         itemsList.add("Баланс: " + driver.getBalance()+" р.");
         itemsList.add("Отчет: " + driver.reportsCount());

        ListView lv = (ListView) findViewById(R.id.mainListView);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                itemsList);

        lv.setAdapter(arrayAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long index) {
                Bundle extras = getIntent().getExtras();
                int id = extras.getInt("id");

                Intent intent = new Intent(ReportActivity.this, ReportListActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("id", id);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }

}
