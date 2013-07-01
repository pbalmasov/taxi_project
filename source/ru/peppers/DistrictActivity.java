package ru.peppers;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.District;
import model.Driver;
import model.SubDistrict;

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
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Toast;

public class DistrictActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sublist);

        // Bundle bundle = getIntent().getExtras();
        // int id = bundle.getInt("id");

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("action", "districtdata"));
        // nameValuePairs.add(new BasicNameValuePair("id", String.valueOf(id)));

        Document doc = PhpData.postData(this, nameValuePairs);
        if (doc != null) {
            Node errorNode = doc.getElementsByTagName("error").item(0);

            if (Integer.parseInt(errorNode.getTextContent()) == 1)
                errorHandler();
            else {
                try {
                    initMainList(doc);
                } catch (Exception e) {
                    errorHandler();
                }
            }
        }
    }

    private void errorHandler() {
        new AlertDialog.Builder(this).setTitle(this.getString(R.string.error_title))
                .setMessage(this.getString(R.string.error_message))
                .setNeutralButton(this.getString(R.string.close), null).show();
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
            curGroupMap.put(
                    "name",
                    districts.get(i).getDistrictName() + "- ("
                            + String.valueOf(districts.get(i).getDrivers()) + "/"
                            + String.valueOf(districts.get(i).getOrders()) + ")");
            curGroupMap.put("even", this.getString(R.string.drivers)+" - (" + String.valueOf(districts.get(i).getDrivers()) + "/"
                    + String.valueOf(districts.get(i).getOrders()) + ")");

            List<Map<String, String>> children = new ArrayList<Map<String, String>>();

            Map<String, String> curChildMap = new HashMap<String, String>();
            curChildMap.put("name", this.getString(R.string.all_drivers));
            curChildMap.put("even", "");
            children.add(curChildMap);

            for (int j = 0; j < districts.get(i).getSubdistrics().size(); j++) {
                curChildMap = new HashMap<String, String>();
                children.add(curChildMap);
                curChildMap.put("name", districts.get(i).getSubdistrics().get(j).getSubDistrictName());
                curChildMap.put("even", "");
            }
            childData.add(children);
        }
        // Set up our adapter
        final SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter(this, groupData,
                R.layout.group, new String[] { "name", "even" }, new int[] { android.R.id.text1,
                    android.R.id.text2 }, childData, android.R.layout.simple_expandable_list_item_2,
                new String[] { "name", "even" }, new int[] { android.R.id.text1, android.R.id.text2 });
        final ExpandableListView lv = (ExpandableListView) findViewById(R.id.expandableListView1);

        lv.setAdapter(adapter);
        lv.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {

                for (int i = 0; i < adapter.getGroupCount(); i++) {
                    if (i != groupPosition)
                        lv.collapseGroup(i);
                }
            }
        });
        lv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                    int childPosition, long id) {
                // Toast.makeText(DistrictActivity.this, groupPosition+" "+childPosition,
                // Toast.LENGTH_LONG).show();
                // Bundle bundle = getIntent().getExtras();
                // int ind = bundle.getInt("id");

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
                nameValuePairs.add(new BasicNameValuePair("action", "savedistrict"));
                // nameValuePairs.add(new BasicNameValuePair("id", String.valueOf(ind)));
                nameValuePairs.add(new BasicNameValuePair("district", String.valueOf(groupPosition)));
                nameValuePairs.add(new BasicNameValuePair("subdistrict", String.valueOf(childPosition)));

                Document doc = PhpData.postData(DistrictActivity.this, nameValuePairs);
                if (doc != null) {
                    Node errorNode = doc.getElementsByTagName("error").item(0);

                    if (Integer.parseInt(errorNode.getTextContent()) == 1)
                        errorHandler();
                    else {

                        final CharSequence[] items = { DistrictActivity.this.getString(R.string.free_here), DistrictActivity.this.getString(R.string.orders) };
                        AlertDialog.Builder builder = new AlertDialog.Builder(DistrictActivity.this);
                        builder.setTitle(DistrictActivity.this.getString(R.string.choose_action));
                        builder.setItems(items, onContextMenuItemListener(groupPosition, childPosition));
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }
                return false;
            }
        });
    }

    private OnClickListener onContextMenuItemListener(final int groupPosition, final int childPosition) {
        return new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                if (item == 0) {
                    new AlertDialog.Builder(DistrictActivity.this).setTitle(DistrictActivity.this.getString(R.string.Ok))
                            .setMessage(DistrictActivity.this.getString(R.string.free_here)).setNeutralButton(DistrictActivity.this.getString(R.string.close), null).show();
                }
                if (item == 1) {
                    // Bundle extras = getIntent().getExtras();
                    // //int id = extras.getInt("id");

                    Intent intent = new Intent(DistrictActivity.this, DistrictListActivity.class);
                    Bundle bundle = new Bundle();
                    // bundle.putInt("id", id);
                    bundle.putInt("group", groupPosition);
                    bundle.putInt("child", childPosition);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

                dialog.dismiss();
            }
        };
    }
}
