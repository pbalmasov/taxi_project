package ru.peppers;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import hello.Driver;
import hello.Order;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class MyOrderItemActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order);

        Bundle bundle = getIntent().getExtras();
        int id = bundle.getInt("id");
        int index = bundle.getInt("index");
        Order order = TaxiApplication.getDriver().getOrder(index);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, order.toArrayList());

        ListView lv = (ListView) findViewById(R.id.listView1);

        lv.setAdapter(arrayAdapter);
        Button button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                final CharSequence[] items = { "Пригласить", "Отложить", "Закрыть", "Звонок из офиса" };
                AlertDialog.Builder builder = new AlertDialog.Builder(MyOrderItemActivity.this);
                builder.setTitle("Выбор действия");
                builder.setItems(items, onContextMenuItemListener());
                AlertDialog alert = builder.create();
                alert.show();
            }

        });
    }

    private OnClickListener onContextMenuItemListener() {
        return new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Log.d("My_tag", "действие - " + item);
                dialog.dismiss();
                switch (item) {
                    case 2:
                        AlertDialog.Builder alert = new AlertDialog.Builder(MyOrderItemActivity.this);

                        alert.setTitle("Цена");
                        alert.setMessage("Цена поездки");

                        // Set an EditText view to get user input
                        final EditText input = new EditText(MyOrderItemActivity.this);
                        input.setInputType(InputType.TYPE_CLASS_NUMBER);
                        alert.setView(input);

                        alert.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String value = input.getText().toString();
                                Bundle bundle = getIntent().getExtras();
                                int id = bundle.getInt("id");
                                int index = bundle.getInt("index");
                                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
                                nameValuePairs.add(new BasicNameValuePair("action", "savecost"));
                                nameValuePairs.add(new BasicNameValuePair("id", String.valueOf(id)));
                                nameValuePairs.add(new BasicNameValuePair("index", String.valueOf(index)));
                                nameValuePairs.add(new BasicNameValuePair("value", value));

                                Document doc = PhpData.postData(MyOrderItemActivity.this, nameValuePairs);
                                Node errorNode = doc.getElementsByTagName("error").item(0);

                                if (Integer.parseInt(errorNode.getTextContent()) == 1)
                                    new AlertDialog.Builder(MyOrderItemActivity.this).setTitle("Ошибка")
                                            .setMessage("Ошибка на сервере. Перезапустите приложение.")
                                            .setNeutralButton("Закрыть", null).show();
                                else {
                                    new AlertDialog.Builder(MyOrderItemActivity.this).setTitle("Ок")
                                            .setMessage("Закак закрыт.")
                                            .setNeutralButton("Закрыть", null).show();
                                }
                            }
                        });
                        alert.show();
                        break;
                    case 3:
                        Bundle bundle = getIntent().getExtras();
                        int id = bundle.getInt("id");
                        int index = bundle.getInt("index");
                        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
                        nameValuePairs.add(new BasicNameValuePair("action", "calloffice"));
                        nameValuePairs.add(new BasicNameValuePair("id", String.valueOf(id)));
                        nameValuePairs.add(new BasicNameValuePair("index", String.valueOf(index)));

                        Document doc = PhpData.postData(MyOrderItemActivity.this, nameValuePairs);
                        Node errorNode = doc.getElementsByTagName("error").item(0);

                        if (Integer.parseInt(errorNode.getTextContent()) == 1)
                            new AlertDialog.Builder(MyOrderItemActivity.this).setTitle("Ошибка")
                                    .setMessage("Ошибка на сервере. Перезапустите приложение.")
                                    .setNeutralButton("Закрыть", null).show();
                        else {
                            new AlertDialog.Builder(MyOrderItemActivity.this).setTitle("Ок")
                                    .setMessage("Ваш звонок принят. Ожидайте звонка.")
                                    .setNeutralButton("Закрыть", null).show();
                        }
                        break;

                    default:
                        break;
                }
            }
        };
    }
}
