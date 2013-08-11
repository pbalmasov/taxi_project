package ru.peppers;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.Message;

public class MessageActivity extends BalanceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message);

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
        nameValuePairs.add(new BasicNameValuePair("action", "list"));
        nameValuePairs.add(new BasicNameValuePair("module", "mobile"));
        nameValuePairs.add(new BasicNameValuePair("object", "message"));

        Document doc = PhpData.postData(this, nameValuePairs, PhpData.newURL);
        if (doc != null) {
            // Node errorNode = doc.getElementsByTagName("error").item(0);
            //
            // if (Integer.parseInt(errorNode.getTextContent()) == 1)
            // new AlertDialog.Builder(this).setTitle("Ошибка")
            // .setMessage("Ошибка на сервере. Перезапустите приложение.")
            // .setNeutralButton("Закрыть", null).show();
            // else {
            try {
                initMainList(doc);
            } catch (Exception e) {
                PhpData.errorHandler(this, e);
            }
        }

    }

    private void initMainList(Document doc) throws DOMException, ParseException {
        NodeList nodeList = doc.getElementsByTagName("item");
        ArrayList<Message> all = new ArrayList<Message>();
        // ArrayList<Message> readed = new ArrayList<Message>();
        for (int i = 0; i < nodeList.getLength(); i++) {

            Element item = (Element) nodeList.item(i);
            boolean isRead = true;
            if (item.getElementsByTagName("readdate").item(0) == null)
                isRead = false;
            int index = Integer.valueOf(item.getElementsByTagName("messageid").item(0).getTextContent());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            Date date = format.parse(item.getElementsByTagName("postdate").item(0).getTextContent());
            String text = item.getElementsByTagName("message").item(0).getTextContent();

            Message message = new Message(text, date, isRead, index);
            // if (!isRead) {
            all.add(message);
            // } else {
            // readed.add(message);
            // }
        }

        // Collections.sort(all);
        // Collections.sort(readed);
        // all.addAll(readed);

        // TextView tv = (TextView) findViewById(R.id.textView1);
        //
        // tv.setMovementMethod(new ScrollingMovementMethod());
        //
        // int arraySize = all.size();
        // for (int i = 0; i < arraySize; i++) {
        // tv.append(all.get(i).toString());
        // tv.append("\n");
        // tv.append("\n");
        // }

        ArrayAdapter<Message> adapter = new ArrayAdapter<Message>(this, android.R.layout.simple_list_item_1,
                all) {
            public boolean areAllItemsEnabled() {
                return false;
            }

            public boolean isEnabled(int position) {
                return false;
            }
        };
        ;

        // final Driver driver = TaxiApplication.getDriver();
        // driver.setMessages(all);

        ListView lv = (ListView) findViewById(R.id.listView1);

        lv.setAdapter(adapter);

    }
}
