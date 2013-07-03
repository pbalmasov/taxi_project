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

public class SubDistrictActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);

		Bundle bundle = getIntent().getExtras();
		String districtid = bundle.getString("districtid");

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
		nameValuePairs.add(new BasicNameValuePair("module", "mobile"));
		nameValuePairs.add(new BasicNameValuePair("object", "subdistrict"));
		nameValuePairs.add(new BasicNameValuePair("action", "list"));
		nameValuePairs.add(new BasicNameValuePair("districtid", districtid));

		Document doc = PhpData.postData(this, nameValuePairs,
				"https://www.abs-taxi.ru/fcgi-bin/office/cman.fcgi");
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
		final ArrayList<SubDistrict> subDistricts = new ArrayList<SubDistrict>();
		subDistricts.add(new SubDistrict(0,this.getString(R.string.all_drivers),"0"));
		for (int i = 0; i < nodeList.getLength(); i++) {

			Element item = (Element) nodeList.item(i);;

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

		ArrayAdapter<SubDistrict> adapter = new ArrayAdapter<SubDistrict>(this, R.layout.group,
				subDistricts);

		lv.setAdapter(adapter);

		lv.setOnItemClickListener(new ListView.OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				final CharSequence[] items = { SubDistrictActivity.this.getString(R.string.free_here),
						SubDistrictActivity.this.getString(R.string.orders) };
				AlertDialog.Builder builder = new AlertDialog.Builder(SubDistrictActivity.this);
				builder.setTitle(SubDistrictActivity.this.getString(R.string.choose_action));
				builder.setItems(items, onContextMenuItemListener(subDistricts.get(arg2).get_subDistrictId()));
				AlertDialog alert = builder.create();
				alert.show();
			}

		});

	}

	private OnClickListener onContextMenuItemListener(final String subdistrictId) {
		return new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {

				if (item == 0) {
					new AlertDialog.Builder(SubDistrictActivity.this)
					.setTitle(SubDistrictActivity.this.getString(R.string.Ok))
					.setMessage(SubDistrictActivity.this.getString(R.string.free_here))
					.setNeutralButton(SubDistrictActivity.this.getString(R.string.close), null).show();
				}
				if (item == 1) {
					Intent intent = new Intent(SubDistrictActivity.this, DistrictListActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("group", subdistrictId);
					bundle.putString("child", subdistrictId);
					intent.putExtras(bundle);
					startActivity(intent);
				}

				dialog.dismiss();
			}
		};
	}
}
