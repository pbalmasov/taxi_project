package ru.peppers;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.os.Bundle;
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		initList();
		// Bundle bundle = getIntent().getExtras();
		// // int id = bundle.getInt("id");
		//
		// List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		// nameValuePairs.add(new BasicNameValuePair("action", "reportdata"));
		// // nameValuePairs.add(new BasicNameValuePair("id",
		// String.valueOf(id)));
		//
		// Document doc = PhpData.postData(this, nameValuePairs);
		// if (doc != null) {
		// Node errorNode = doc.getElementsByTagName("error").item(0);
		//
		// if (Integer.parseInt(errorNode.getTextContent()) == 1)
        // new AlertDialog.Builder(this).setTitle("Ошибка")
        // .setMessage("Ошибка на сервере. Перезапустите приложение.").setNeutralButton("Закрыть",
        // null)
		// .show();
		// else {
		// try {
		// initMainList(doc);
		// } catch (DOMException e) {
		// e.printStackTrace();
        // new AlertDialog.Builder(this).setTitle("Ошибка")
        // .setMessage("Ошибка на сервере. Перезапустите приложение.")
        // .setNeutralButton("Закрыть", null).show();
        // } catch (ParseException e) {
		// e.printStackTrace();
        // new AlertDialog.Builder(this).setTitle("Ошибка")
        // .setMessage("Ошибка на сервере. Перезапустите приложение.")
        // .setNeutralButton("Закрыть", null).show();
        // }
		// }
		// } else {
		// initList();
		// }
	}

	private void initMainList(Document doc) throws DOMException, ParseException {
		NodeList nodeList = doc.getElementsByTagName("order");
		ArrayList<Order> reports = new ArrayList<Order>();
		for (int i = 0; i < nodeList.getLength(); i++) {
			NamedNodeMap attributes = nodeList.item(i).getAttributes();

			int index = Integer.parseInt(attributes.getNamedItem("index").getTextContent());
			int type = Integer.parseInt(attributes.getNamedItem("type").getTextContent());
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
			Date date = format.parse(attributes.getNamedItem("date").getTextContent());
			String carClass = attributes.getNamedItem("class").getTextContent();
			String adress = attributes.getNamedItem("adress").getTextContent();
			String where = attributes.getNamedItem("where").getTextContent();
			int costOrder = Integer.parseInt(attributes.getNamedItem("costOrder").getTextContent());

			if (type == 0) {
				int cost = Integer.parseInt(attributes.getNamedItem("cost").getTextContent());
				String costType = attributes.getNamedItem("costType").getTextContent();
				String text = nodeList.item(i).getTextContent();
				// reports.add(new CostOrder(this,costOrder,index,date, adress,
				// carClass, text, where, cost, costType));
			}
			if (type == 1) {
				String text = nodeList.item(i).getTextContent();
				// reports.add(new NoCostOrder(this,costOrder,index,date,
				// adress, carClass, text, where));
			}
			if (type == 2) {
				String text = nodeList.item(i).getTextContent();
				// reports.add(new PreliminaryOrder(this,costOrder,index,date,
				// adress, carClass, text, where));
			}
			if (attributes.getNamedItem("abonent") != null) {
				String abonent = attributes.getNamedItem("abonent").getTextContent();
				int rides = Integer.parseInt(attributes.getNamedItem("rides").getTextContent());
				reports.get(i).setAbonent(abonent);
				reports.get(i).setRides(rides);
			}

		}
		String balance = doc.getElementsByTagName("balance").item(0).getTextContent();
		Driver driver = TaxiApplication.getDriver();
		driver.setBalance(balance);
		driver.setReports(reports);
		initList();
	}

	public void initList() {
		final Driver driver = TaxiApplication.getDriver();
		itemsList = new ArrayList<String>();
        itemsList.add("Статус: " + driver.getStatusString());
        itemsList.add("Класс: " + driver.getClassAutoString());

        ListView lv = (ListView) findViewById(R.id.mainListView);

		simpleAdpt = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, itemsList);

		lv.setAdapter(simpleAdpt);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long index) {
				if (position == 1) {
					Resources res = ReportActivity.this.getResources();
					String[] classArray = res.getStringArray(R.array.class_array);
					AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
                    builder.setTitle("Выбор статуса");
                    builder.setSingleChoiceItems(classArray, driver.getClassAuto(),
							onClassContextMenuItemListener(position));
					AlertDialog alert = builder.create();
					alert.show();
				}
				if (position == 0) {
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
                    builder.setSingleChoiceItems(arrayList.toArray(new String[arrayList.size()]), arrayList1.indexOf(driver.getStatus()),
                            onStatusContextMenuItemListener(position));
					AlertDialog alert = builder.create();
					alert.show();
				}

			}
		});
	}

	private OnClickListener onStatusContextMenuItemListener(final int position) {
		return new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				Driver driver = TaxiApplication.getDriver();

				// if (item == 2 && driver.getOrdersCount() != 0) {
				// new
                // AlertDialog.Builder(ReportActivity.this).setTitle("Заказы")
                // .setMessage("К сожалению у вас есть не закрытые заказы.").setNeutralButton("Закрыть",
                // null)
				// .show();
				// dialog.dismiss();
				// return;
				// }

                int[] statusArray = {0, 2, 3};
                if (statusArray[item] != driver.getStatus()) {
                    String[] sendArray = {"online", "leaveforabreak", "quit"};
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
                    nameValuePairs.add(new BasicNameValuePair("action", sendArray[item]));
                    nameValuePairs.add(new BasicNameValuePair("module", "mobile"));
                    nameValuePairs.add(new BasicNameValuePair("object", "driver"));
                    // nameValuePairs.add(new BasicNameValuePair("orderid",
					// String.valueOf(order.get_index())));
					// nameValuePairs.add(new BasicNameValuePair("minutes",
					// (String) cs[which]));

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
//                            if (item == 0 && driver.getDistrict() == "") {
//								Intent intent = new Intent(ReportActivity.this, DistrictActivity.class);
//								startActivity(intent);
//								finish();
//								return;
//							}
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
				Driver driver = TaxiApplication.getDriver();
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
							driver.setClassAuto(item);
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
