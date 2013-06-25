package ru.peppers;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class MessageFromServiceActivity extends Activity {
    public final static String TITLE = "title";
    public final static String MESSAGE = "message";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Intent intent = getIntent();

        String title = intent.getStringExtra(TITLE);
        String message = intent.getStringExtra(MESSAGE);

        AlertDialog.Builder builder = new AlertDialog.Builder(MessageFromServiceActivity.this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNeutralButton("Ок", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (TaxiApplication.getDriver() != null) {
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                    nameValuePairs.add(new BasicNameValuePair("action", "districtdata"));
                   // nameValuePairs.add(new BasicNameValuePair("id", String.valueOf(TaxiApplication.getDriverId())));

                    Document doc = PhpData.postData(MessageFromServiceActivity.this, nameValuePairs);
                    if (doc != null) {
                        Node errorNode = doc.getElementsByTagName("error").item(0);

                        if (Integer.parseInt(errorNode.getTextContent()) == 1)
                            new AlertDialog.Builder(MessageFromServiceActivity.this).setTitle("Ошибка")
                                    .setMessage("Ошибка на сервере. Перезапустите приложение.")
                                    .setNeutralButton("Закрыть", null).show();
                        else {
                            finish();
                        }
                    }
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

}
