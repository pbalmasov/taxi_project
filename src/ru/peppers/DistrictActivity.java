package ru.peppers;

import hello.District;
import hello.SubDistrict;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Toast;

public class DistrictActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sublist);

        Bundle bundle = getIntent().getExtras();
        int id = bundle.getInt("id");

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("action", "districtdata"));
        nameValuePairs.add(new BasicNameValuePair("id", String.valueOf(id)));

        Document doc = PhpData.postData(this, nameValuePairs);
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
    }

    private void initMainList(Document doc) throws DOMException, ParseException {
        NodeList nodeList = doc.getElementsByTagName("district");
        ArrayList<District> districts = new ArrayList<District>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            NodeList childList = nodeList.item(i).getChildNodes();

            ArrayList<SubDistrict> subdistricts = new ArrayList<SubDistrict>();
            for (int j = 0; j < childList.getLength(); j++) {
                if (childList.item(j).getNodeType() == Node.ELEMENT_NODE) {
                    NamedNodeMap attributes = childList.item(j).getAttributes();
                    int drivers = Integer.parseInt(attributes.getNamedItem("drivers").getTextContent());
                    int orders = Integer.parseInt(attributes.getNamedItem("orders").getTextContent());
                    String name = attributes.getNamedItem("name").getTextContent();

                    SubDistrict subdistrict = new SubDistrict(drivers, orders, name);
                    subdistricts.add(subdistrict);
                }
            }

            NamedNodeMap attributes = nodeList.item(i).getAttributes();

            int drivers = Integer.parseInt(attributes.getNamedItem("drivers").getTextContent());
            int orders = Integer.parseInt(attributes.getNamedItem("orders").getTextContent());
            String name = attributes.getNamedItem("name").getTextContent();

            District district = new District(drivers, orders, name, subdistricts);
            districts.add(district);
        }

        List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();
        List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();
        for (int i = 0; i < districts.size(); i++) {
          Map<String, String> curGroupMap = new HashMap<String, String>();
          groupData.add(curGroupMap);
          curGroupMap.put("name", districts.get(i).getDistrictName());
          curGroupMap.put("even",  "Водителей - (" + String.valueOf(districts.get(i).getDrivers()) +"/"+String.valueOf(districts.get(i).getOrders())+")");

          List<Map<String, String>> children = new ArrayList<Map<String, String>>();

          Map<String, String> curChildMap = new HashMap<String, String>();
          curChildMap.put("name", "Все");
          curChildMap.put("even", "");
          children.add(curChildMap);



          for (int j = 0; j < districts.get(i).getSubdistrics().size(); j++) {
            curChildMap = new HashMap<String, String>();
            children.add(curChildMap);
            curChildMap.put("name", districts.get(i).getSubdistrics().get(j).getSubDistrictName());
            curChildMap.put("even", "Водителей - (" + String.valueOf(districts.get(i).getSubdistrics().get(j).getDrivers()) +"/"+String.valueOf(districts.get(i).getSubdistrics().get(j).getOrders())+")");
          }
          childData.add(children);
        }
        // Set up our adapter
        SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter(
            this,
            groupData,
            android.R.layout.simple_expandable_list_item_1,
            new String[] { "name", "even" },
            new int[] { android.R.id.text1, android.R.id.text2 },
            childData,
            android.R.layout.simple_expandable_list_item_2,
            new String[] { "name", "even" },
            new int[] { android.R.id.text1, android.R.id.text2 }
            );
        ExpandableListView lv = (ExpandableListView) findViewById(R.id.expandableListView1);

        lv.setAdapter(adapter);
        lv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition,
                    long id) {
                //Toast.makeText(DistrictActivity.this, groupPosition+" "+childPosition, Toast.LENGTH_LONG).show();

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
                nameValuePairs.add(new BasicNameValuePair("action", "savedistrict"));
                nameValuePairs.add(new BasicNameValuePair("id", String.valueOf(id)));
                nameValuePairs.add(new BasicNameValuePair("district", String.valueOf(groupPosition)));
                nameValuePairs.add(new BasicNameValuePair("subdistrict", String.valueOf(childPosition)));

                Document doc = PhpData.postData(DistrictActivity.this, nameValuePairs);
                Node errorNode = doc.getElementsByTagName("error").item(0);

                if (Integer.parseInt(errorNode.getTextContent()) == 1)
                    new AlertDialog.Builder(DistrictActivity.this).setTitle("Ошибка")
                            .setMessage("Ошибка на сервере. Перезапустите приложение.")
                            .setNeutralButton("Закрыть", null).show();
                else {
                    new AlertDialog.Builder(DistrictActivity.this).setTitle("Ок")
                            .setMessage("Район принят сервером.")
                            .setNeutralButton("Закрыть", null).show();
                }
                return false;
            }
        });
    }
}
