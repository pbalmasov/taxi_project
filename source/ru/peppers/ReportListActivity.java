package ru.peppers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.Driver;
import model.Order;
import orders.ReportCostOrder;

public class ReportListActivity extends BalanceActivity implements AsyncTaskCompleteListener<Document> {

    private int currentPage = 1;
    private ArrayAdapter<Order> arrayAdapter;
    final ArrayList<Order> orders = new ArrayList<Order>();
    private String titleText;
    private boolean canMove;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emptylistreport);
        titleText = (String) title.getText();
        title.setText(titleText + " " + currentPage);

        ListView lv = (ListView) findViewById(R.id.lvMain);
        lv.setEmptyView(findViewById(R.id.emptyView));
        arrayAdapter = new ArrayAdapter<Order>(this, android.R.layout.simple_list_item_1, orders);

        lv.setAdapter(arrayAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long index) {
                // Bundle extras = getIntent().getExtras();
                // int id = extras.getInt("id");

                Intent intent = new Intent(ReportListActivity.this, ReportListItemActivity.class);
                Bundle bundle = new Bundle();
                // bundle.putInt("id", id);
                bundle.putInt("index", position);
                intent.putExtras(bundle);
                if (PhpData.isNetworkAvailable(ReportListActivity.this))
                    startActivity(intent);
            }
        });

        Button forward = (Button) findViewById(R.id.forward);
        forward.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (canMove) {
                    currentPage++;
                    getPage();
                }
            }
        });

        Button backward = (Button) findViewById(R.id.backward);
        backward.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (currentPage > 1) {
                    currentPage--;
                    getPage();
                }
            }
        });

        getPage();

    }

    private void getPage() {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
        nameValuePairs.add(new BasicNameValuePair("action", "list"));
        nameValuePairs.add(new BasicNameValuePair("mode", "archive"));
        nameValuePairs.add(new BasicNameValuePair("module", "mobile"));
        nameValuePairs.add(new BasicNameValuePair("object", "order"));
        nameValuePairs.add(new BasicNameValuePair("page", String.valueOf(currentPage)));
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
                    initMainList(doc);
                } catch (Exception e) {
                    PhpData.errorHandler(this, e);
                }
            }
        }
    }

    private void initMainList(Document doc) throws DOMException, ParseException {
        NodeList nodeList = doc.getElementsByTagName("item");

        if (nodeList.getLength() != 0) {
            canMove = true;
            orders.clear();
        } else {
            currentPage--;
            canMove = false;
            return;
        }
        title.setText(titleText + " " + currentPage);

        for (int i = 0; i < nodeList.getLength(); i++) {
            Element item = (Element) nodeList.item(i);

            Node nominalcostNode = item.getElementsByTagName("nominalcost").item(0);
            Node classNode = item.getElementsByTagName("classid").item(0);
            Node addressdepartureNode = item.getElementsByTagName("addressdeparture").item(0);
            Node orderdateNode = item.getElementsByTagName("orderdate").item(0);
            Node paymenttypeNode = item.getElementsByTagName("paymenttype").item(0);
            Node quantityNode = item.getElementsByTagName("quantity").item(0);
            Node commentNode = item.getElementsByTagName("comment").item(0);
            Node nicknameNode = item.getElementsByTagName("nickname").item(0);
            Node addressarrivalNode = item.getElementsByTagName("addressarrival").item(0);
            Node orderIdNode = item.getElementsByTagName("orderid").item(0);
            Node resultNode = item.getElementsByTagName("result").item(0);
            Node actualcostNode = item.getElementsByTagName("actualcost").item(0);
            Node drivercostNode = item.getElementsByTagName("drivercost").item(0);
            Node accepttimeNode = item.getElementsByTagName("accepttime").item(0);

            // actualcost - фактическая стоимость
            // drivercost - сколько водитель платит диспетчерской службе
            // nominalcost - рекомендуемая стоимость

            // Node invitationNode = item.getElementsByTagName("invitationtime").item(0);

            Date accepttime = null;
            String actualcost = null;
            String drivercost = null;
            String nominalcost = null;
            Integer carClass = 0;
            String addressdeparture = null;
            Date orderdate = null;
            Integer paymenttype = null;
            Integer quantity = null;
            String comment = null;
            String nickname = null;
            String addressarrival = null;
            String orderId = null;
            String result = null;
            // Date invitationtime = null;

            // if(departuretime==null)
            // //TODO:не предварительный
            // else
            // //TODO:предварительный

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

            if (!orderdateNode.getTextContent().equalsIgnoreCase(""))
                orderdate = format.parse(orderdateNode.getTextContent());

            if (!commentNode.getTextContent().equalsIgnoreCase(""))
                comment = commentNode.getTextContent();

            if (!orderIdNode.getTextContent().equalsIgnoreCase(""))
                orderId = orderIdNode.getTextContent();

            if (!resultNode.getTextContent().equalsIgnoreCase(""))
                result = resultNode.getTextContent();

            if (!drivercostNode.getTextContent().equalsIgnoreCase(""))
                drivercost = drivercostNode.getTextContent();

            if (!actualcostNode.getTextContent().equalsIgnoreCase(""))
                actualcost = actualcostNode.getTextContent();

            if (!accepttimeNode.getTextContent().equalsIgnoreCase(""))
                accepttime = format.parse(accepttimeNode.getTextContent());

            // if (!invitationNode.getTextContent().equalsIgnoreCase(""))
            // invitationtime = format.parse(invitationNode.getTextContent());

            orders.add(new ReportCostOrder(this, orderId, nominalcost, addressdeparture, carClass, comment,
                    addressarrival, paymenttype, orderdate, result, drivercost, actualcost, accepttime));

            if (!nicknameNode.getTextContent().equalsIgnoreCase("")) {
                nickname = nicknameNode.getTextContent();

                if (!quantityNode.getTextContent().equalsIgnoreCase(""))
                    quantity = Integer.parseInt(quantityNode.getTextContent());
                orders.get(i).setAbonent(nickname);
                orders.get(i).setRides(quantity);
            }
        }

        Driver driver = TaxiApplication.getDriver();
        // if driver.order == null // else driver.setOrderWithIndex // or get date from server
        driver.setReports(orders);
        // driver = new Driver(status, carClass, ordersCount, district, subdistrict);

        // itemsList = new ArrayList<Map<String, String>>();
        // itemsList.add(createItem("item", "Мои закакзы: " + driver.getOrdersCount()));
        // itemsList.add(createItem("item", "Статус: " + driver.getStatusString()));
        // itemsList.add(createItem("item", "Свободные заказы"));
        // if (driver.getStatus() != 1)
        // itemsList
        // .add(createItem("item", "Район: " + driver.getDistrict() + "," + driver.getSubdistrict()));
        // itemsList.add(createItem("item", "Класс: " + driver.getClassAutoString()));
        // itemsList.add(createItem("item", "Отчет"));
        // itemsList.add(createItem("item", "Звонок из офиса"));
        // itemsList.add(createItem("item", "Настройки"));

        arrayAdapter.notifyDataSetChanged();

    }
}
