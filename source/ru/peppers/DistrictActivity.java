package ru.peppers;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import model.District;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class DistrictActivity extends BalanceActivity {
    private static final int REQUEST_EXIT = 0;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
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
                    PhpData.errorHandler(this,e);
                }
            }
        }
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
                bundle.putInt("districtdrivers", districts.get(arg2).getDrivers());
                intent.putExtras(bundle);
                startActivityForResult(intent,REQUEST_EXIT);

			}

        });

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
