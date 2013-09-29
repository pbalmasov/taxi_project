package ru.peppers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.Driver;
import model.Order;
import orders.CostOrder;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
/**
 * Список заказов в районах
 * @author p.balmasov
 */
public class DistrictListActivity extends BalanceActivity implements AsyncTaskCompleteListener<Document> {

    public static final int REQUEST_CLOSE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emptylist);

        Bundle bundle = getIntent().getExtras();
        String districtid = bundle.getString("districtid");
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
        nameValuePairs.add(new BasicNameValuePair("action", "list"));
        nameValuePairs.add(new BasicNameValuePair("module", "mobile"));
        nameValuePairs.add(new BasicNameValuePair("mode", "available"));
        nameValuePairs.add(new BasicNameValuePair("object", "order"));
        nameValuePairs.add(new BasicNameValuePair("districtid", districtid));
        ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("Loading...");
        new MyTask(this, progress, this).execute(nameValuePairs);

    }

    @Override
    public void onTaskComplete(Document doc) {
        if (doc != null) {

            Node responseNode = doc.getElementsByTagName("response").item(0);
            Node errorNode = doc.getElementsByTagName("message").item(0);

            if (responseNode.getTextContent().equalsIgnoreCase("failure"))
                PhpData.errorFromServer(this, errorNode);
            else {
                try {
                    parseDistrictList(doc);
                } catch (Exception e) {
                    PhpData.errorHandler(this, e);
                }
            }
        }
    }

    private void parseDistrictList(Document doc) throws DOMException, ParseException {
        NodeList nodeList = doc.getElementsByTagName("item");
        ArrayList<Order> orders = new ArrayList<Order>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            // nominalcost - рекомендуемая стоимость заказа
            // class - класс автомобля (0 - все равно, 1 - Эконом, 2 - Стандарт,
            // 3 - Базовый)
            // addressdeparture - адрес подачи автомобиля
            // departuretime - время подачи(если есть)
            // paymenttype - форма оплаты (0 - наличные, 1 - безнал)
            // invitationtime - время приглашения (если пригласили)
            // quantity - количество заказов от этого клиента
            // comment - примечание
            // nickname - ник абонента (если есть)
            // registrationtime - время регистрации заказа
            // addressarrival - куда поедут

            Element item = (Element) nodeList.item(i);

            Node nominalcostNode = item.getElementsByTagName("nominalcost").item(0);
            Node classNode = item.getElementsByTagName("classid").item(0);
            Node addressdepartureNode = item.getElementsByTagName("addressdeparture").item(0);
            Node departuretimeNode = item.getElementsByTagName("departuretime").item(0);
            Node paymenttypeNode = item.getElementsByTagName("paymenttype").item(0);
            Node quantityNode = item.getElementsByTagName("quantity").item(0);
            Node commentNode = item.getElementsByTagName("comment").item(0);
            Node nicknameNode = item.getElementsByTagName("nickname").item(0);
            Node addressarrivalNode = item.getElementsByTagName("addressarrival").item(0);
            Node orderIdNode = item.getElementsByTagName("orderid").item(0);

            String nominalcost = null;
            Integer carClass = 0;
            String addressdeparture = null;
            Date departuretime = null;
            Integer paymenttype = null;
            Integer quantity = null;
            String comment = null;
            String nickname = null;
            String addressarrival = null;
            String orderId = null;

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

            if (!classNode.getTextContent().equalsIgnoreCase(""))
                carClass = Integer.valueOf(classNode.getTextContent());

            if (!nominalcostNode.getTextContent().equalsIgnoreCase(""))
                nominalcost = nominalcostNode.getTextContent();

            if (!addressdepartureNode.getTextContent().equalsIgnoreCase(""))
                addressdeparture = addressdepartureNode.getTextContent();

            if (!addressarrivalNode.getTextContent().equalsIgnoreCase(""))
                addressarrival = addressarrivalNode.getTextContent();

            if (!paymenttypeNode.getTextContent().equalsIgnoreCase(""))
                paymenttype = Integer.parseInt(paymenttypeNode.getTextContent());

            if (!departuretimeNode.getTextContent().equalsIgnoreCase(""))
                departuretime = format.parse(departuretimeNode.getTextContent());

            if (!commentNode.getTextContent().equalsIgnoreCase(""))
                comment = commentNode.getTextContent();

            if (!orderIdNode.getTextContent().equalsIgnoreCase(""))
                orderId = orderIdNode.getTextContent();

            orders.add(new CostOrder(this, orderId, nominalcost, addressdeparture, carClass, comment,
                    addressarrival, paymenttype, departuretime));

            if (!nicknameNode.getTextContent().equalsIgnoreCase("")) {
                nickname = nicknameNode.getTextContent();

                if (!quantityNode.getTextContent().equalsIgnoreCase(""))
                    quantity = Integer.parseInt(quantityNode.getTextContent());
                orders.get(i).setAbonent(nickname);
                orders.get(i).setRides(quantity);
            }
        }

        Driver driver = TaxiApplication.getDriver(this);
        driver.set_districtOrders(orders);

        ListView lv = (ListView) findViewById(R.id.mainListView);
        lv.setEmptyView(findViewById(R.id.empty));
        ArrayAdapter<Order> arrayAdapter = new ArrayAdapter<Order>(this, android.R.layout.simple_list_item_1,
                orders);

        lv.setAdapter(arrayAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long index) {
                Intent intent = new Intent(DistrictListActivity.this, DistrictListItemActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("index", position);
                intent.putExtras(bundle);

                if (PhpData.isNetworkAvailable(DistrictListActivity.this))
                    startActivityForResult(intent, REQUEST_CLOSE);
                else{
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_CLOSE) {
            if (resultCode == RESULT_OK) {
                this.setResult(RESULT_OK);
                this.finish();
            }
        }
    }

}
