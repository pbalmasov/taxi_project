package ru.peppers;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

import model.Order;

public class DistrictListItemActivity extends BalanceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.freeorder);

        Bundle bundle = getIntent().getExtras();
        int index = bundle.getInt("index");
        final Order order = TaxiApplication.getDriver().get_districtOrders().get(index);

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
                AlertDialog.Builder alert = new AlertDialog.Builder(DistrictListItemActivity.this);
                alert.setTitle(DistrictListItemActivity.this.getString(R.string.time));
                final CharSequence cs[];

                cs = new String[]{"3", "5", "7", "10", "15", "20", "25", "30", "35"};

                alert.setItems(cs, new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
                        nameValuePairs.add(new BasicNameValuePair("action", "accept"));
                        nameValuePairs.add(new BasicNameValuePair("module", "mobile"));
                        nameValuePairs.add(new BasicNameValuePair("object", "order"));
                        nameValuePairs.add(new BasicNameValuePair("orderid", String.valueOf(order.get_index())));
                        nameValuePairs.add(new BasicNameValuePair("minutes", (String) cs[which]));

                        Document doc = PhpData.postData(DistrictListItemActivity.this, nameValuePairs, PhpData.newURL);
                        if (doc != null) {
                            Node responseNode = doc.getElementsByTagName("response").item(0);
                            Node errorNode = doc.getElementsByTagName("message").item(0);

                            if (responseNode.getTextContent().equalsIgnoreCase("failure"))
                                PhpData.errorFromServer(DistrictListItemActivity.this, errorNode);
                            else {
                                try {

                                    Intent intent = new Intent(DistrictListItemActivity.this, MyOrderActivity.class);
                                    setResult(RESULT_OK);
                                    startActivity(intent);
                                    finish();
                                    //TODO:заканчивать парент активити
                                } catch (Exception e) {
                                    PhpData.errorHandler(DistrictListItemActivity.this, e);
                                }
                            }
                        }
                    }
                });
                alert.show();
            }
        });
    }
}
