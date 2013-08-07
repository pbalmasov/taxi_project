package ru.peppers;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Driver;

public class MainListActivity extends BalanceActivity {
	private ListView lv;
	public SimpleAdapter simpleAdpt;
	public List<Map<String, String>> itemsList;
	private static final String MY_TAG = "My_tag";
	private static final int REQUEST_EXIT = 0;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainlist);

		// init();
	}

	@Override
	protected void onResume() {
		super.onResume();
		init();
	}

	private void init() {
		// Bundle bundle = getIntent().getExtras();
		// int id = bundle.getInt("id");

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
		nameValuePairs.add(new BasicNameValuePair("action", "get"));
		nameValuePairs.add(new BasicNameValuePair("module", "mobile"));
		nameValuePairs.add(new BasicNameValuePair("mode", "status"));
		nameValuePairs.add(new BasicNameValuePair("object", "driver"));

		Document doc = PhpData.postData(this, nameValuePairs, PhpData.newURL);
		if (doc != null) {
			Node responseNode = doc.getElementsByTagName("response").item(0);
			Node errorNode = doc.getElementsByTagName("message").item(0);

			if (responseNode.getTextContent().equalsIgnoreCase("failure"))
				PhpData.errorFromServer(this, errorNode);
			else {
				try {
					parseMainList(doc);
				} catch (Exception e) {
					PhpData.errorHandler(this, e);
				}
			}
		} else {
			initMainList();
		}
	}

	private void parseMainList(Document doc) {
		// int carClass =
		// Integer.valueOf(doc.getElementsByTagName("carClass").item(0).getTextContent());
		int status = 3;
		int classid = 1;
		Node statusNode = doc.getElementsByTagName("status").item(1);
		if (!statusNode.getTextContent().equalsIgnoreCase(""))
			status = Integer.valueOf(statusNode.getTextContent());

		Node classNode = doc.getElementsByTagName("currentclassid").item(0);
		if (!classNode.getTextContent().equalsIgnoreCase(""))
			classid = Integer.valueOf(classNode.getTextContent());

		String district = doc.getElementsByTagName("districttitle").item(0).getTextContent();
		String subdistrict = doc.getElementsByTagName("subdistricttitle").item(0).getTextContent();
		String balance = doc.getElementsByTagName("balance").item(0).getTextContent();
		Integer classid1 = Integer.valueOf(doc.getElementsByTagName("classid").item(0).getTextContent());

		// Bundle bundle = getIntent().getExtras();
		// int id = bundle.getInt("id");
		Driver driver = TaxiApplication.getDriver();
		driver.setStatus(status);
		driver.setClassAuto(classid - 1);
		driver.setDistrict(district);
		driver.setSubdistrict(subdistrict);
		driver.setBalance(balance);
		driver.setCarId(classid1);
		this.updateData();
		initMainList();
	}

	private void initMainList() {
		final Driver driver = TaxiApplication.getDriver();
		if (driver != null) {
			itemsList = new ArrayList<Map<String, String>>();
			itemsList.add(createItem("item", this.getString(R.string.my_orders)));// +
			// ": "
			// +
			// driver.getOrdersCount()));
			itemsList.add(createItem("item", this.getString(R.string.free_orders)));
			itemsList.add(createItem("item", this.getString(R.string.status) + " " + driver.getStatusString()));
			itemsList.add(createItem("item", this.getString(R.string.region) + " " + driver.getFullDisctrict()));
			itemsList.add(createItem("item", this.getString(R.string.call_office)));
			itemsList.add(createItem("item", this.getString(R.string.settings)));
			itemsList.add(createItem("item", this.getString(R.string.messages)));
			itemsList.add(createItem("item", "Архив"));
			itemsList.add(createItem("item", this.getString(R.string.exit)));

			lv = (ListView) findViewById(R.id.mainListView);

			simpleAdpt = new SimpleAdapter(this, itemsList, android.R.layout.simple_list_item_1,
					new String[] { "item" }, new int[] { android.R.id.text1 });

			lv.setAdapter(simpleAdpt);
			lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long index) {
					Bundle extras = getIntent().getExtras();
					// //int id = extras.getInt("id");
					Intent intent;
					switch (position) {
					case 0:
						intent = new Intent(MainListActivity.this, MyOrderActivity.class);
						startActivity(intent);
						break;
					case 1:
						intent = new Intent(MainListActivity.this, FreeOrderActivity.class);
						startActivity(intent);
						break;
					case 2:
						if (driver.getStatus() != 3) {
							intent = new Intent(MainListActivity.this, ReportActivity.class);
							startActivity(intent);
						} else {
							intent = new Intent(MainListActivity.this, DistrictActivity.class);
							startActivity(intent);
						}
						break;
					case 3:
						if (driver.getStatus() != 1) {
							intent = new Intent(MainListActivity.this, DistrictActivity.class);
							startActivity(intent);
							return;
						}
						break;
					case 4:
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
						nameValuePairs.add(new BasicNameValuePair("action", "callback"));
						nameValuePairs.add(new BasicNameValuePair("module", "mobile"));
						nameValuePairs.add(new BasicNameValuePair("object", "driver"));

						Document doc = PhpData.postData(MainListActivity.this, nameValuePairs, PhpData.newURL);
						if (doc != null) {
							Node responseNode = doc.getElementsByTagName("response").item(0);
							Node errorNode = doc.getElementsByTagName("message").item(0);

							if (responseNode.getTextContent().equalsIgnoreCase("failure"))
								PhpData.errorFromServer(MainListActivity.this, errorNode);
							else {
								new AlertDialog.Builder(MainListActivity.this).setTitle("Звонок")
										.setMessage("Ваш запрос принят. Пожалуйста ожидайте звонка")
										.setNeutralButton("Ок", null).show();
							}
						}
						break;
					case 5:
						intent = new Intent(MainListActivity.this, SettingsActivity.class);
						startActivityForResult(intent, REQUEST_EXIT);
						break;
					case 6:
						intent = new Intent(MainListActivity.this, MessageActivity.class);
						startActivity(intent);
						break;
					case 7:
						intent = new Intent(MainListActivity.this, ReportListActivity.class);
						startActivity(intent);
						break;
					case 8:
						exitDialog();
						break;

					default:
						break;
					}
				}

			});
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == REQUEST_EXIT) {
			if (resultCode == RESULT_OK) {
				if (data != null) {
					Intent intent = getIntent();
					finish();
					startActivity(intent);
				} else {
					quitPost();
				}
			}
		}
	}

	private void exitDialog() {
		new AlertDialog.Builder(MainListActivity.this).setTitle(this.getString(R.string.orders))
				.setMessage(this.getString(R.string.sorry_exit)).setPositiveButton("Да", onExitClickListener())
				.setNegativeButton("Нет", null).show();
	}

	private OnClickListener onExitClickListener() {
		return new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				quitPost();
			}

		};
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// Handle the back button
		if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
			// Ask the user if they want to quit

			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}

	}

	public HashMap<String, String> createItem(String key, String name) {
		HashMap<String, String> item = new HashMap<String, String>();
		item.put(key, name);

		return item;
	}

	private void quitPost() {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
		nameValuePairs.add(new BasicNameValuePair("action", "quit"));
		nameValuePairs.add(new BasicNameValuePair("module", "mobile"));
		nameValuePairs.add(new BasicNameValuePair("object", "driver"));

		Document doc = PhpData.postData(MainListActivity.this, nameValuePairs, PhpData.newURL);
		if (doc != null) {
			Node responseNode = doc.getElementsByTagName("response").item(0);
			Node errorNode = doc.getElementsByTagName("message").item(0);

			if (responseNode.getTextContent().equalsIgnoreCase("failure")) {
				// PhpData.errorFromServer(MainListActivity.this, errorNode);
				Intent intent = new Intent(MainListActivity.this, PhpService.class);
				stopService(intent);
				finish();
			} else {
				Intent intent = new Intent(MainListActivity.this, PhpService.class);
				stopService(intent);
				finish();
			}
		} else {
			Intent intent = new Intent(MainListActivity.this, PhpService.class);
			stopService(intent);
			finish();
		}
	}

}
