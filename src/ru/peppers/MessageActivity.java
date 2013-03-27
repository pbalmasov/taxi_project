package ru.peppers;

import hello.Driver;
import hello.Message;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MessageActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Bundle bundle = getIntent().getExtras();
        int id = bundle.getInt("id");

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("action", "messagedata"));
        nameValuePairs.add(new BasicNameValuePair("id", String.valueOf(id)));

        Document doc = PhpData.postData(this, nameValuePairs);
        Node errorNode = doc.getElementsByTagName("error").item(0);

        if (Integer.parseInt(errorNode.getTextContent()) == 1)
            new AlertDialog.Builder(this).setTitle("Ошибка")
                    .setMessage("Ошибка на сервере. Перезапустите приложение.")
                    .setNeutralButton("Закрыть", null).show();
        else {
            try {
                initMainList(doc);
            } catch (DOMException e) {
                e.printStackTrace();
                new AlertDialog.Builder(this).setTitle("Ошибка")
                        .setMessage("Ошибка на сервере. Перезапустите приложение.")
                        .setNeutralButton("Закрыть", null).show();
            } catch (ParseException e) {
                e.printStackTrace();
                new AlertDialog.Builder(this).setTitle("Ошибка")
                        .setMessage("Ошибка на сервере. Перезапустите приложение.")
                        .setNeutralButton("Закрыть", null).show();
            }
        }

    }

    private void initMainList(Document doc) throws DOMException, ParseException {
        NodeList nodeList = doc.getElementsByTagName("message");
        ArrayList<Message> unreaded = new ArrayList<Message>();
        ArrayList<Message> readed = new ArrayList<Message>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            NamedNodeMap attributes = nodeList.item(i).getAttributes();

            boolean isRead = Boolean.parseBoolean(attributes.getNamedItem("readed").getTextContent());
            int index = Integer.valueOf(attributes.getNamedItem("index").getTextContent());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            Date date = format.parse(attributes.getNamedItem("date").getTextContent());
            String text = nodeList.item(i).getTextContent();

            Message message = new Message(text, date,isRead, index);
            if(!isRead){
                unreaded.add(message);
            }
            else{
                readed.add(message);
            }
        }

        Collections.sort(unreaded);
        Collections.sort(readed);
        unreaded.addAll(readed);

        ArrayAdapter<Message> adapter = new ArrayAdapter<Message>(this, android.R.layout.simple_list_item_1,
                unreaded);

        final Driver driver = TaxiApplication.getDriver();
        driver.setMessages(unreaded);

        ListView lv = (ListView) findViewById(R.id.mainListView);

        lv.setAdapter(adapter);

    }
}
