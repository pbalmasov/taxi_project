package ru.peppers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import model.Order;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputType;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MyOrderItemActivity extends BalanceActivity {

	private CountDownTimer timer;
	private ArrayList<String> orderList;
	private TextView counterView;
	private Order order;

	// TODO:set timer date
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myorder);
		Bundle bundle = getIntent().getExtras();
		// int id = bundle.getInt("id");
		int index = bundle.getInt("index");
		// if (index == 0) {
		// priceDialog();
		// return;
		// }

		order = TaxiApplication.getDriver().getOrder(index);

		orderList = order.toArrayList();

		counterView = (TextView) findViewById(R.id.textView1);

		TextView tv = (TextView) findViewById(R.id.textView2);

		int arraySize = orderList.size();
		for (int i = 0; i < arraySize; i++) {
			tv.append(orderList.get(i));
			tv.append("\n");
		}

		// arrayAdapter = new ArrayAdapter<String>(this,
		// android.R.layout.simple_list_item_1, orderList);

		if (order.getTimerDate() != null) {
			timerInit(order);
		}

		// ListView lv = (ListView) findViewById(R.id.listView1);

		// lv.setAdapter(arrayAdapter);

		Button button = (Button) findViewById(R.id.button1);
		button.setText(this.getString(R.string.choose_action));
		button.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				initActionDialog();
			}

		});
	}

	private OnClickListener onContextMenuItemListener() {
		return new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				dialog.dismiss();
				switch (item) {
				case 0:
					inviteDialog();
					break;
				case 1:
					timeDialog();
					break;
				case 2:
					priceDialog();
					break;
				case 3:
					Bundle bundle = getIntent().getExtras();
					// int id = bundle.getInt("id");
					int index = bundle.getInt("index");
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
					nameValuePairs.add(new BasicNameValuePair("action", "calloffice"));
					// nameValuePairs.add(new BasicNameValuePair("id",
					// String.valueOf(id)));
					nameValuePairs.add(new BasicNameValuePair("index", String.valueOf(index)));

					Document doc = PhpData.postData(MyOrderItemActivity.this, nameValuePairs);
					if (doc != null) {
						Node errorNode = doc.getElementsByTagName("error").item(0);

						if (Integer.parseInt(errorNode.getTextContent()) == 1)
							errorHandler();
						else {
							new AlertDialog.Builder(MyOrderItemActivity.this)
									.setTitle(MyOrderItemActivity.this.getString(R.string.Ok))
									.setMessage(MyOrderItemActivity.this.getString(R.string.wait_call))
									.setNeutralButton(MyOrderItemActivity.this.getString(R.string.close), null).show();
						}
					}
					break;

				default:
					break;
				}
			}

		};
	}

	private void inviteDialog() {
		Bundle bundle = getIntent().getExtras();
		// int id = bundle.getInt("id");
		int index = bundle.getInt("index");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
		nameValuePairs.add(new BasicNameValuePair("action", "invite"));
		// nameValuePairs.add(new BasicNameValuePair("id", String.valueOf(id)));
		nameValuePairs.add(new BasicNameValuePair("index", String.valueOf(index)));

		Document doc = PhpData.postData(MyOrderItemActivity.this, nameValuePairs);
		if (doc != null) {
			Node errorNode = doc.getElementsByTagName("error").item(0);

			if (Integer.parseInt(errorNode.getTextContent()) == 1)
				errorHandler();
			else {
				new AlertDialog.Builder(MyOrderItemActivity.this).setTitle(this.getString(R.string.Ok))
						.setMessage(this.getString(R.string.invite_sended))
						.setNeutralButton(this.getString(R.string.close), null).show();
			}
		}
	}

	private void errorHandler() {
		new AlertDialog.Builder(this).setTitle(this.getString(R.string.error_title))
				.setMessage(this.getString(R.string.error_message))
				.setNeutralButton(this.getString(R.string.close), null).show();
	}

	private void timeDialog() {
		AlertDialog.Builder alert = new AlertDialog.Builder(MyOrderItemActivity.this);
		alert.setTitle(this.getString(R.string.time));
		final CharSequence cs[];

		cs = new String[] { "3", "5", "7", "10", "15", "20", "25", "30", "35" };

		alert.setItems(cs, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Bundle extras = getIntent().getExtras();
				// int id = extras.getInt("id");
				int index = extras.getInt("index");
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
				nameValuePairs.add(new BasicNameValuePair("action", "savetime"));
				// nameValuePairs.add(new BasicNameValuePair("id",
				// String.valueOf(id)));
				nameValuePairs.add(new BasicNameValuePair("index", String.valueOf(index)));
				nameValuePairs.add(new BasicNameValuePair("value", String.valueOf(cs[which])));
				Document doc = PhpData.postData(MyOrderItemActivity.this, nameValuePairs);
				if (doc != null) {
					Node errorNode = doc.getElementsByTagName("error").item(0);
					if (Integer.parseInt(errorNode.getTextContent()) == 1)
						errorHandler();
					else {

						final Order order = TaxiApplication.getDriver().getOrder(index);

						if (order.getTimerDate() != null) {
							Calendar cal = Calendar.getInstance();
							cal.add(Calendar.MINUTE, Integer.valueOf((String) cs[which]));

							order.setTimerDate(cal.getTime());
							timer.cancel();
							timerInit(order);
						}
						new AlertDialog.Builder(MyOrderItemActivity.this)
								.setTitle(MyOrderItemActivity.this.getString(R.string.Ok))
								.setMessage(MyOrderItemActivity.this.getString(R.string.order_delayed))
								.setNeutralButton(MyOrderItemActivity.this.getString(R.string.close), null).show();
					}
				}
			}

		});
		alert.show();

	}

	private void initActionDialog() {
		final CharSequence[] items = { MyOrderItemActivity.this.getString(R.string.invite),
				MyOrderItemActivity.this.getString(R.string.delay), MyOrderItemActivity.this.getString(R.string.close),
				MyOrderItemActivity.this.getString(R.string.call_office) };
		AlertDialog.Builder builder = new AlertDialog.Builder(MyOrderItemActivity.this);
		builder.setTitle(MyOrderItemActivity.this.getString(R.string.choose_action));
		builder.setItems(items, onContextMenuItemListener());
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void timerInit(final Order order) {
		long diffInMs = order.getTimerDate().getTime() - new Date().getTime();

		timer = new CountDownTimer(diffInMs, 1000) {

			public void onTick(long millisUntilFinished) {
				int seconds = ((int) millisUntilFinished / 1000) % 60;
				String secondsStr = String.valueOf(seconds);
				if (seconds <= 9)
					secondsStr = "0" + seconds;

				counterView.setText(((int) millisUntilFinished / 1000) / 60 + ":" + secondsStr);
				if ((((int) millisUntilFinished / 1000) / 60) == 1 && (((int) millisUntilFinished / 1000) % 60) == 0) {
					initActionDialog();
				}
			}

			public void onFinish() {
				counterView.setText(MyOrderItemActivity.this.getString(R.string.ended_timer));
				alertDelay(order);

				MediaPlayer mp = MediaPlayer.create(getBaseContext(), (R.raw.sound));
				mp.start();

			}
		}.start();
	}

	@Override
	protected void onPause() {
		if (timer != null)
			timer.cancel();
		super.onPause();
	}

	private void alertDelay(final Order order) {
		AlertDialog.Builder alert = new AlertDialog.Builder(MyOrderItemActivity.this);
		alert.setTitle(this.getString(R.string.time));
		final CharSequence cs[];

		cs = new String[] { "3", "5", "7", "10", "15", "20", "25", "30", "35" };

		alert.setItems(cs, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Bundle extras = getIntent().getExtras();
				// int id = extras.getInt("id");
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
				nameValuePairs.add(new BasicNameValuePair("action", "saveminutes"));
				nameValuePairs.add(new BasicNameValuePair("order_id", String.valueOf(order.get_index())));
				// nameValuePairs.add(new BasicNameValuePair("id",
				// String.valueOf(id)));
				nameValuePairs.add(new BasicNameValuePair("minutes", String.valueOf(cs[which])));
				Document doc = PhpData.postData(MyOrderItemActivity.this, nameValuePairs);
				if (doc != null) {
					Node errorNode = doc.getElementsByTagName("error").item(0);
					if (Integer.parseInt(errorNode.getTextContent()) == 1)
						errorHandler();
					else {
						Calendar cal = Calendar.getInstance();
						cal.setTime(new Date());
						cal.add(Calendar.MINUTE, Integer.valueOf((String) cs[which]));
						order.setTimerDate(cal.getTime());
						timerInit(order);
					}
				}
			}

		});
		alert.show();
	}

	private void priceDialog() {

		// View view = getLayoutInflater().inflate(R.layout.custom_dialog,
		// null);
		Dialog dialog;
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		int isLightTheme = settings.getInt("theme", 0);
		if (isLightTheme != 0)
			dialog = new Dialog(this, android.R.style.Theme_Light);
		else
			dialog = new Dialog(this, android.R.style.Theme_Black);
		dialog.setContentView(R.layout.custom_dialog);
		dialog.setTitle(this.getString(R.string.price));
		dialog.show();

		Button btn = (Button) dialog.findViewById(R.id.button1);
		EditText input = (EditText) dialog.findViewById(R.id.editText1);
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		btn.setOnClickListener(onSavePrice(dialog));
	}

	private Button.OnClickListener onSavePrice(final Dialog dialog) {
		return new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				EditText input = (EditText) dialog.findViewById(R.id.editText1);
				RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.radioGroup1);
				int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
				String state = "1";
				if (checkedRadioButtonId == R.id.radio0) {
					state = "1";
				} else if (checkedRadioButtonId == R.id.radio1) {
					state = "0";
				}

				if (input.getText().length() != 0) {
					String value = input.getText().toString();
					String orderCost = "1";
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(8);
					nameValuePairs.add(new BasicNameValuePair("action", "close"));
					nameValuePairs.add(new BasicNameValuePair("mode", "my"));
					nameValuePairs.add(new BasicNameValuePair("module", "mobile"));
					nameValuePairs.add(new BasicNameValuePair("object", "order"));
					nameValuePairs.add(new BasicNameValuePair("orderid", order.get_index()));
					if (order.get_nominalcost() != null)
						orderCost = String.valueOf(order.get_nominalcost());
					nameValuePairs.add(new BasicNameValuePair("cost", orderCost));
					nameValuePairs.add(new BasicNameValuePair("cashless", value));
					nameValuePairs.add(new BasicNameValuePair("state", state));

					Document doc = PhpData.postData(MyOrderItemActivity.this, nameValuePairs, PhpData.newURL);
					if (doc != null) {
						Node responseNode = doc.getElementsByTagName("response").item(0);
						Node errorNode = doc.getElementsByTagName("message").item(0);

						if (responseNode.getTextContent().equalsIgnoreCase("failure"))
							PhpData.errorFromServer(MyOrderItemActivity.this, errorNode);
						else {

							new AlertDialog.Builder(MyOrderItemActivity.this)
									.setTitle(MyOrderItemActivity.this.getString(R.string.error_title))
									.setMessage(MyOrderItemActivity.this.getString(R.string.order_closed))
									.setNeutralButton(MyOrderItemActivity.this.getString(R.string.close),
											new OnClickListener() {

												@Override
												public void onClick(DialogInterface dialog, int which) {
													if (timer != null)
														timer.cancel();
													setResult(RESULT_OK);
													finish();
												}
											}).show();
						}
					}
				}
			}
		};
	}
}
