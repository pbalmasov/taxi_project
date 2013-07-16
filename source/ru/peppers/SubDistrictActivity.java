package ru.peppers;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import model.SubDistrict;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
import android.widget.ListView;

public class SubDistrictActivity extends BalanceActivity {
	private String districtid;
	private int districtdrivers;
	private boolean close;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		Bundle bundle = getIntent().getExtras();
		close = bundle.getBoolean("close");
		this.title.setText(bundle.getString("districtname"));
		districtid = bundle.getString("districtid");
		districtdrivers = bundle.getInt("districtdrivers");

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
		nameValuePairs.add(new BasicNameValuePair("module", "mobile"));
		nameValuePairs.add(new BasicNameValuePair("object", "subdistrict"));
		nameValuePairs.add(new BasicNameValuePair("action", "list"));
		nameValuePairs.add(new BasicNameValuePair("districtid", districtid));

		Document doc = PhpData.postData(this, nameValuePairs, PhpData.newURL);
		if (doc != null) {

			Node responseNode = doc.getElementsByTagName("response").item(0);
			Node errorNode = doc.getElementsByTagName("message").item(0);

			if (responseNode.getTextContent().equalsIgnoreCase("failure"))
				PhpData.errorFromServer(this, errorNode);
			else {
				try {
					initMainList(doc);
				} catch (Exception e) {
					PhpData.errorHandler(this, e);
				}
			}
		}
	}

	private void initMainList(Document doc) throws DOMException, ParseException {
		NodeList nodeList = doc.getElementsByTagName("item");
		final ArrayList<SubDistrict> subDistricts = new ArrayList<SubDistrict>();
		subDistricts.add(new SubDistrict(districtdrivers, this.getString(R.string.all_drivers), null));
		for (int i = 0; i < nodeList.getLength(); i++) {

			Element item = (Element) nodeList.item(i);

			Node titleNode = item.getElementsByTagName("title").item(0);
			Node vehicleNode = item.getElementsByTagName("vehiclecount").item(0);
			Node subDistrictIdNode = item.getElementsByTagName("subdistrictid").item(0);

			int drivers = 0;
			if (!vehicleNode.getTextContent().equalsIgnoreCase(""))
				drivers = Integer.parseInt(vehicleNode.getTextContent());

			String name = titleNode.getTextContent();
			String subDistrictId = subDistrictIdNode.getTextContent();

			SubDistrict subDistrict = new SubDistrict(drivers, name, subDistrictId);
			subDistricts.add(subDistrict);
		}

		ListView lv = (ListView) findViewById(R.id.listView1);

		ArrayAdapter<SubDistrict> adapter = new ArrayAdapter<SubDistrict>(this, R.layout.group, subDistricts);

		lv.setAdapter(adapter);

		lv.setOnItemClickListener(new ListView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (close) {
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					if (subDistricts.get(arg2).get_subDistrictId()!=null) {
						bundle.putString("subdistrictname", subDistricts.get(arg2).getSubDistrictName());
						bundle.putString("subdistrict", subDistricts.get(arg2).get_subDistrictId());
					}
					intent.putExtras(bundle);
					setResult(RESULT_OK, intent);
					finish();
				} else {
					final CharSequence[] items = { SubDistrictActivity.this.getString(R.string.free_here),
							SubDistrictActivity.this.getString(R.string.orders) };
					AlertDialog.Builder builder = new AlertDialog.Builder(SubDistrictActivity.this);
					builder.setTitle(SubDistrictActivity.this.getString(R.string.choose_action));
					builder.setItems(items, onContextMenuItemListener(subDistricts.get(arg2).get_subDistrictId()));
					AlertDialog alert = builder.create();
					alert.show();
				}
			}

		});

	}

	private OnClickListener onContextMenuItemListener(final String subdistrictId) {
		return new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {

				if (item == 0) {

					// TODO:send request
					int array_size = (subdistrictId != null) ? 6 : 5;
					Log.d("My_tag", String.valueOf(array_size));
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(array_size);
					nameValuePairs.add(new BasicNameValuePair("module", "mobile"));
					nameValuePairs.add(new BasicNameValuePair("object", "driver"));
					nameValuePairs.add(new BasicNameValuePair("action", "set"));
					nameValuePairs.add(new BasicNameValuePair("mode", "location"));
					nameValuePairs.add(new BasicNameValuePair("districtid", districtid));
					if (subdistrictId != null)
						nameValuePairs.add(new BasicNameValuePair("subdistrictid", subdistrictId));
					Document doc = PhpData.postData(SubDistrictActivity.this, nameValuePairs, PhpData.newURL);
					if (doc != null) {

						Node responseNode = doc.getElementsByTagName("response").item(0);
						Node errorNode = doc.getElementsByTagName("message").item(0);

						if (responseNode.getTextContent().equalsIgnoreCase("failure"))
							PhpData.errorFromServer(SubDistrictActivity.this, errorNode);
						else {
							setResult(RESULT_OK);
							finish();
						}
					}
				}

				if (item == 1) {
					Intent intent = new Intent(SubDistrictActivity.this, DistrictListActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("districtid", districtid);
					intent.putExtras(bundle);
					startActivity(intent);
					setResult(RESULT_OK);
					finish();
				}

				dialog.dismiss();

			}
		};
	}
}
