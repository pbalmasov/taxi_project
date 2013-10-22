package ru.ntechs;

import java.util.ArrayList;
import java.util.List;

import orders.CostOrder;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
/**
 * Подробно свободного заказа
 * @author p.balmasov
 */
public class FreeOrderItemActivity extends BalanceActivity {

    private CostOrder order;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.freeorder);

        Bundle bundle = getIntent().getExtras();
        // int id = bundle.getInt("id");
        int index = bundle.getInt("index");

        order = (CostOrder) TaxiApplication.getDriver(this).getFreeOrders().get(index);

        TextView tv = (TextView) findViewById(R.id.textView1);

        int arraySize = order.toArrayList().size();
        for (int i = 0; i < arraySize; i++) {
            tv.append(order.toArrayList().get(i));
            tv.append("\n");
        }

        Button button = (Button) findViewById(R.id.button1);
        button.setText("Принять");
        button.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (order.get_departuretime() != null)
                    acceptOrder(null);
                else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(FreeOrderItemActivity.this);
                    alert.setTitle(FreeOrderItemActivity.this.getString(R.string.time));
                    final CharSequence cs[];

                    cs = new String[] { "3", "5", "7", "10", "15", "20", "25", "30", "35" };

                    alert.setItems(cs, new OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            acceptOrder((String) cs[which]);
                        }
                    });
                    alert.show();
                }
            }
        });
    }

    private void acceptOrder(String c) {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
        nameValuePairs.add(new BasicNameValuePair("action", "accept"));
        nameValuePairs.add(new BasicNameValuePair("module", "mobile"));
        nameValuePairs.add(new BasicNameValuePair("object", "order"));
        nameValuePairs.add(new BasicNameValuePair("orderid", String.valueOf(order.get_index())));
        if (c != null)
            nameValuePairs.add(new BasicNameValuePair("minutes", c));

        Document doc = PhpData.postData(this, nameValuePairs, PhpData.newURL);
        if (doc != null) {
            Node responseNode = doc.getElementsByTagName("response").item(0);
            Node errorNode = doc.getElementsByTagName("message").item(0);

            if (responseNode.getTextContent().equalsIgnoreCase("failure"))
                PhpData.errorFromServer(this, errorNode);
            else {
                try {
                    Bundle bundle = new Bundle();
                    bundle.putString("index", String.valueOf(order.get_index()));
                    Intent intent = new Intent();
                    intent.putExtras(bundle);
                    setResult(RESULT_OK,intent);
                    finish();
                    // TODO:заканчивать парент активити
                } catch (Exception e) {
                    PhpData.errorHandler(this, e);
                }
            }
        }
    }
}
