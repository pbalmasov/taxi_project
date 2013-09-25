package ru.peppers;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import model.Driver;
import model.Order;

public class ReportActivity extends BalanceActivity {
    private ArrayAdapter<String> simpleAdpt;
    private ArrayList<String> itemsList;
    protected static final String PREFS_NAME = "MyNamePrefs1";
    private static final int REQUEST_GET = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initList();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_GET) {
            if (resultCode == RESULT_OK) {
                if (itemsList != null) {
                    itemsList.set(itemsList.size() - 1, "Жду заказа: "
                            + TaxiApplication.getDriver(this).getWaitString());
                    simpleAdpt.notifyDataSetChanged();
                }
            }
        }
    }

    public void initList() {
        final Driver driver = TaxiApplication.getDriver(this);
        itemsList = new ArrayList<String>();

        try {
            itemsList.add("Версия программы: "
                    + this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName);
        } catch (NameNotFoundException e) {
        }
        itemsList.add("Статус: " + driver.getStatusString());
        itemsList.add("Класс: " + driver.getClassAutoString());
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        if (settings.getString("pozivnoidata", "").equalsIgnoreCase("500"))
            itemsList.add("Жду заказа: " + TaxiApplication.getDriver(this).getWaitString());

        ListView lv = (ListView) findViewById(R.id.mainListView);

        simpleAdpt = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, itemsList);

        lv.setAdapter(simpleAdpt);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long index) {
                if (position == 3) {
                    Intent intent = new Intent(ReportActivity.this, WaitActivity.class);
                    if (PhpData.isNetworkAvailable(ReportActivity.this))
                        startActivityForResult(intent, REQUEST_GET);
                    else {
                        setResult(RESULT_OK);
                        finish();
                    }
                } else if (position == 2 && driver.getCarId() != 1 && driver.getClassAuto() != null) {
                    Resources res = ReportActivity.this.getResources();
                    String[] classArray = res.getStringArray(R.array.class_array);

                    // <item>Эконом</item> 0
                    // <item>Стандарт</item> 1
                    // <item>Базовый</item> 2

                    // если classid=3 базовый=базовый стандарт=стандарт,базовый эконом=эконом,стандарт,базовый
                    // если classid=2, базовый=нельзя стандарт=стандарт эконом=стандарт, эконом
                    ArrayList<String> arrayList = new ArrayList<String>(Arrays.asList(classArray));
                    if (driver.getCarId() == 2)
                        arrayList.remove(2);

                    AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
                    builder.setTitle("Выбор статуса");
                    builder.setSingleChoiceItems(arrayList.toArray(new String[arrayList.size()]),
                            driver.getClassAuto(), onClassContextMenuItemListener(position));
                    AlertDialog alert = builder.create();
                    alert.show();
                } else if (position == 1) {
                    Resources res = ReportActivity.this.getResources();
                    String[] statusArray = res.getStringArray(R.array.status_array);
                    ArrayList<String> arrayList = new ArrayList<String>(Arrays.asList(statusArray));
                    arrayList.remove(arrayList.size() - 3);
                    ArrayList<Integer> arrayList1 = new ArrayList<Integer>();
                    arrayList1.add(0, 0);
                    arrayList1.add(1, 2);
                    arrayList1.add(2, 3);
                    AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
                    builder.setTitle("Выбор статуса");
                    builder.setSingleChoiceItems(arrayList.toArray(new String[arrayList.size()]),
                            arrayList1.indexOf(driver.getStatus()), onStatusContextMenuItemListener(position));
                    AlertDialog alert = builder.create();
                    alert.show();
                }

            }
        });
    }

    private OnClickListener onStatusContextMenuItemListener(final int position) {
        return new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Driver driver = TaxiApplication.getDriver(ReportActivity.this);

                int[] statusArray = { 0, 2, 3 };
                if (driver.getStatus() != null)
                    if (statusArray[item] != driver.getStatus()) {
                        String[] sendArray = { "online", "leaveforabreak", "quit" };
                        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
                        nameValuePairs.add(new BasicNameValuePair("action", sendArray[item]));
                        nameValuePairs.add(new BasicNameValuePair("module", "mobile"));
                        nameValuePairs.add(new BasicNameValuePair("object", "driver"));

                        Document doc = PhpData.postData(ReportActivity.this, nameValuePairs, PhpData.newURL);
                        if (doc != null) {
                            Node responseNode = doc.getElementsByTagName("response").item(0);
                            Node errorNode = doc.getElementsByTagName("message").item(0);

                            if (responseNode.getTextContent().equalsIgnoreCase("failure"))
                                PhpData.errorFromServer(ReportActivity.this, errorNode);
                            else {
                                driver.setStatus(statusArray[item]);
                                itemsList.set(position, "Статус: " + driver.getStatusString());
                                simpleAdpt.notifyDataSetChanged();
                                // предлагаем поменять район
                                // if (item == 0 && driver.getDistrict() == "") {
                                // Intent intent = new Intent(ReportActivity.this, DistrictActivity.class);
                                // startActivity(intent);
                                // finish();
                                // return;
                                // }
                            }
                        }
                    }
                dialog.dismiss();
            }
        };
    }

    private OnClickListener onClassContextMenuItemListener(final int position) {
        return new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Driver driver = TaxiApplication.getDriver(ReportActivity.this);
                if (item != driver.getClassAuto()) {

                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
                    nameValuePairs.add(new BasicNameValuePair("action", "set"));
                    nameValuePairs.add(new BasicNameValuePair("module", "mobile"));
                    nameValuePairs.add(new BasicNameValuePair("object", "driver"));
                    nameValuePairs.add(new BasicNameValuePair("mode", "class"));
                    nameValuePairs.add(new BasicNameValuePair("classid", String.valueOf(item + 1)));

                    Document doc = PhpData.postData(ReportActivity.this, nameValuePairs, PhpData.newURL);
                    if (doc != null) {
                        Node responseNode = doc.getElementsByTagName("response").item(0);
                        Node errorNode = doc.getElementsByTagName("message").item(0);

                        if (responseNode.getTextContent().equalsIgnoreCase("failure"))
                            PhpData.errorFromServer(ReportActivity.this, errorNode);
                        else {
                            driver.setClassAuto(item + 1);
                            itemsList.set(position, "Класс: " + driver.getClassAutoString());
                            simpleAdpt.notifyDataSetChanged();
                        }
                    }
                }
                dialog.dismiss();
            }
        };
    }

}
