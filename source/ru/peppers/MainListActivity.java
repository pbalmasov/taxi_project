package ru.peppers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Driver;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

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

public class MainListActivity extends BalanceActivity {
	private ListView lv;
	public SimpleAdapter simpleAdpt;
	public List<Map<String, String>> itemsList;
	private static final String MY_TAG = "My_tag";
	private static final int REQUEST_EXIT = 0;

	/** Called when the activity is first created. */
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
		int ordersCount = Integer.valueOf(doc.getElementsByTagName("ordercount").item(0).getTextContent());
		// int carClass =
		// Integer.valueOf(doc.getElementsByTagName("carClass").item(0).getTextContent());
		int status = 2;
		int classid = 1;
		Node statusNode = doc.getElementsByTagName("status").item(1);
		if (!statusNode.getTextContent().equalsIgnoreCase(""))
			status = Integer.valueOf(statusNode.getTextContent());

		Node classNode = doc.getElementsByTagName("classid").item(0);
		if (!classNode.getTextContent().equalsIgnoreCase(""))
			classid = Integer.valueOf(classNode.getTextContent());

		String district = doc.getElementsByTagName("districttitle").item(0).getTextContent();
		String subdistrict = doc.getElementsByTagName("subdistricttitle").item(0).getTextContent();
		String balance = doc.getElementsByTagName("balance").item(0).getTextContent();

		// Bundle bundle = getIntent().getExtras();
		// int id = bundle.getInt("id");
		Driver driver = TaxiApplication.getDriver();
		driver.setStatus(status);
		driver.setClassAuto(classid - 1);
		driver.setOrdersCount(ordersCount);
		driver.setDistrict(district);
		driver.setSubdistrict(subdistrict);
		driver.setBalance(balance);
		this.updateBalance();
		initMainList();
	}

	private void initMainList() {
		final Driver driver = TaxiApplication.getDriver();
		if (driver != null) {
			itemsList = new ArrayList<Map<String, String>>();
			itemsList.add(createItem("item", this.getString(R.string.my_orders) + " " + driver.getOrdersCount()));
			itemsList.add(createItem("item", this.getString(R.string.status) + " " + driver.getStatusString()));
			itemsList.add(createItem("item", this.getString(R.string.free_orders)));
			if (driver.getStatus() != 1) {
				itemsList.add(createItem("item", this.getString(R.string.region) + " " + driver.getFullDisctrict()));
			}
			itemsList.add(createItem("item", this.getString(R.string.call_office)));
			itemsList.add(createItem("item", this.getString(R.string.settings)));
			itemsList.add(createItem("item", this.getString(R.string.messages)));
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
						if (driver.getStatus() != 3) {
							intent = new Intent(MainListActivity.this, ReportActivity.class);
							startActivity(intent);
						} else {
							intent = new Intent(MainListActivity.this, MyOrderActivity.class);
							startActivity(intent);
						}
						break;
					case 2:
						intent = new Intent(MainListActivity.this, FreeOrderActivity.class);
						startActivity(intent);
						break;
					case 3:
						if (driver.getStatus() != 1) {
							intent = new Intent(MainListActivity.this, DistrictActivity.class);
							startActivity(intent);
							return;
						}
					default:
						break;
					}
					if (driver.getStatus() != 1)
						position--;
					if (position == 3) {
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
						nameValuePairs.add(new BasicNameValuePair("action", "calloffice"));
						// nameValuePairs.add(new BasicNameValuePair("id",
						// String.valueOf(id)));

						Document doc = PhpData.postData(MainListActivity.this, nameValuePairs);
						if (doc != null) {
							Node errorNode = doc.getElementsByTagName("error").item(0);

							if (Integer.parseInt(errorNode.getTextContent()) == 1)
								// TODO:fix
								PhpData.errorHandler(MainListActivity.this, null);
							else {
								PhpData.errorHandler(MainListActivity.this, null);
							}
						}
					}
					if (position == 4) {
						intent = new Intent(MainListActivity.this, SettingsActivity.class);
		                startActivityForResult(intent,REQUEST_EXIT);
					}
					if (position == 5) {
						intent = new Intent(MainListActivity.this, MessageActivity.class);
						startActivity(intent);
					}
					if (position == 6) {
						Driver driver = TaxiApplication.getDriver();
						if (driver.getOrdersCount() != 0) {
							exitDialog();
						} else {
							Intent service = new Intent(MainListActivity.this, PhpService.class);
							stopService(service);
							finish();
						}
					}
				}

			});
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == REQUEST_EXIT) {
			if (resultCode == RESULT_OK) {
				if (data.getBooleanExtra("refresh", false)) {
					Intent intent = getIntent();
					finish();
					startActivity(intent);
				} else {
					Intent intent = new Intent(MainListActivity.this, PhpService.class);
					stopService(intent);
					this.finish();
				}
			}
		}
	}

	private void exitDialog() {
		new AlertDialog.Builder(MainListActivity.this).setTitle(this.getString(R.string.orders))
				.setMessage(this.getString(R.string.sorry_exit))
				.setPositiveButton(this.getString(R.string.exit_action), onExitClickListener())
				.setNegativeButton(this.getString(R.string.cancel), null).show();
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
			Driver driver = TaxiApplication.getDriver();
			if (driver != null)
				if (driver.getOrdersCount() != 0) {
					exitDialog();
					return true;
				} else
					quitPost();
			return super.onKeyDown(keyCode, event);
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

			if (responseNode.getTextContent().equalsIgnoreCase("failure"))
				PhpData.errorFromServer(MainListActivity.this, errorNode);
			else {
				Intent intent = new Intent(MainListActivity.this, PhpService.class);
				stopService(intent);
				finish();
			}
		}
	}

}
