package ru.peppers;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.District;
import model.Driver;
import model.Message;
import model.SubDistrict;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Toast;

public class DistrictActivity extends BalanceActivity {
    private static final int REQUEST_EXIT = 0;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        //init();
    }
    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void init() {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
        nameValuePairs.add(new BasicNameValuePair("module", "mobile"));
        nameValuePairs.add(new BasicNameValuePair("object", "district"));
        nameValuePairs.add(new BasicNameValuePair("action", "list"));

        Document doc = PhpData.postData(this, nameValuePairs,
                PhpData.newURL);
        if (doc != null) {

            Node responseNode = doc.getElementsByTagName("response").item(0);
            Node errorNode = doc.getElementsByTagName("message").item(0);

            if (responseNode.getTextContent().equalsIgnoreCase("failure"))
                PhpData.errorFromServer(this, errorNode);
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
        NodeList nodeList = doc.getElementsByTagName("item");
        final ArrayList<District> districts = new ArrayList<District>();
        for (int i = 0; i < nodeList.getLength(); i++) {

            Element item = (Element) nodeList.item(i);
            Node titleNode = item.getElementsByTagName("title").item(0);
            Node orderNode = item.getElementsByTagName("ordercount").item(0);
            Node vehicleNode = item.getElementsByTagName("vehiclecount").item(0);
            Node districtIdNode = item.getElementsByTagName("districtid").item(0);

            int orders = 0;
            int drivers = 0;
            if (!vehicleNode.getTextContent().equalsIgnoreCase(""))
                drivers = Integer.parseInt(vehicleNode.getTextContent());
            if (!orderNode.getTextContent().equalsIgnoreCase(""))
                orders = Integer.parseInt(orderNode.getTextContent());

            String districtId = districtIdNode.getTextContent();
            String name = titleNode.getTextContent();


            District district = new District(drivers, orders, name,districtId, null);
            districts.add(district);
        }

        ListView lv = (ListView) findViewById(R.id.listView1);

        ArrayAdapter<District> adapter = new ArrayAdapter<District>(this, R.layout.group,
                districts);

        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new ListView.OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				Intent intent = new Intent(DistrictActivity.this, SubDistrictActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("districtid", districts.get(arg2).getDistrictId());
                bundle.putString("districtname", districts.get(arg2).getDistrictName());
                intent.putExtras(bundle);
                startActivityForResult(intent,REQUEST_EXIT);

			}

        });

        // List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();
        // List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();
        // for (int i = 0; i < districts.size(); i++) {
        // Map<String, String> curGroupMap = new HashMap<String, String>();
        // groupData.add(curGroupMap);
        // curGroupMap.put(
        // "name",
        // districts.get(i).getDistrictName() + "- ("
        // + String.valueOf(districts.get(i).getDrivers()) + "/"
        // + String.valueOf(districts.get(i).getOrders()) + ")");
        // curGroupMap.put("even", this.getString(R.string.drivers)+" - (" +
        // String.valueOf(districts.get(i).getDrivers()) + "/"
        // + String.valueOf(districts.get(i).getOrders()) + ")");
        //
        // List<Map<String, String>> children = new ArrayList<Map<String, String>>();
        //
        // Map<String, String> curChildMap = new HashMap<String, String>();
        // curChildMap.put("name", this.getString(R.string.all_drivers));
        // curChildMap.put("even", "");
        // children.add(curChildMap);
        //
        // for (int j = 0; j < districts.get(i).getSubdistrics().size(); j++) {
        // curChildMap = new HashMap<String, String>();
        // children.add(curChildMap);
        // curChildMap.put("name", districts.get(i).getSubdistrics().get(j).getSubDistrictName());
        // curChildMap.put("even", "");
        // }
        // childData.add(children);
        // }
        // // Set up our adapter
        // final SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter(this, groupData,
        // R.layout.group, new String[] { "name", "even" }, new int[] { android.R.id.text1,
        // android.R.id.text2 }, childData, android.R.layout.simple_expandable_list_item_2,
        // new String[] { "name", "even" }, new int[] { android.R.id.text1, android.R.id.text2 });
        // final ExpandableListView lv = (ExpandableListView) findViewById(R.id.expandableListView1);
        //
        // lv.setAdapter(adapter);
        // lv.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
        //
        // @Override
        // public void onGroupExpand(int groupPosition) {
        //
        // for (int i = 0; i < adapter.getGroupCount(); i++) {
        // if (i != groupPosition)
        // lv.collapseGroup(i);
        // }
        // }
        // });
        // lv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
        //
        // @Override
        // public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
        // int childPosition, long id) {
        // // Toast.makeText(DistrictActivity.this, groupPosition+" "+childPosition,
        // // Toast.LENGTH_LONG).show();
        // // Bundle bundle = getIntent().getExtras();
        // // int ind = bundle.getInt("id");
        //
        // List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
        // nameValuePairs.add(new BasicNameValuePair("action", "savedistrict"));
        // // nameValuePairs.add(new BasicNameValuePair("id", String.valueOf(ind)));
        // nameValuePairs.add(new BasicNameValuePair("district", String.valueOf(groupPosition)));
        // nameValuePairs.add(new BasicNameValuePair("subdistrict", String.valueOf(childPosition)));
        //
        // Document doc = PhpData.postData(DistrictActivity.this, nameValuePairs);
        // if (doc != null) {
        // Node errorNode = doc.getElementsByTagName("error").item(0);
        //
        // if (Integer.parseInt(errorNode.getTextContent()) == 1)
        // errorHandler();
        // else {
        //
        // final CharSequence[] items = { DistrictActivity.this.getString(R.string.free_here),
        // DistrictActivity.this.getString(R.string.orders) };
        // AlertDialog.Builder builder = new AlertDialog.Builder(DistrictActivity.this);
        // builder.setTitle(DistrictActivity.this.getString(R.string.choose_action));
        // builder.setItems(items, onContextMenuItemListener(groupPosition, childPosition));
        // AlertDialog alert = builder.create();
        // alert.show();
        // }
        // }
        // return false;
        // }
        // });
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_EXIT) {
             if (resultCode == RESULT_OK) {
                this.finish();
             }
         }
    }


}
