package ru.peppers;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Driver;
import myorders.MyCostOrder;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
/**
 * Главное активити
 * @author p.balmasov
 */
public class MainListActivity extends BalanceActivity implements AsyncTaskCompleteListener<Document> {
    private ListView lv;
    public SimpleAdapter simpleAdpt;
    public List<Map<String, String>> itemsList;
    private static final int REQUEST_EXIT = 0;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainlist);
        // init();

        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
                String str = sw.toString();

                Intent crashedIntent = new Intent(MainListActivity.this, CrashActivity.class);
                crashedIntent.putExtra("crash", str);
                crashedIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                crashedIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(crashedIntent);
                System.exit(1);
            }
        });


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
                    parseMainList(doc);
                } catch (Exception e) {
                    PhpData.errorHandler(this, e);
                }
            }
        } else {
            initMainList();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    @SuppressWarnings("unchecked")
    private void init() {
        // Bundle bundle = getIntent().getExtras();
        // int id = bundle.getInt("id");

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
        nameValuePairs.add(new BasicNameValuePair("action", "get"));
        nameValuePairs.add(new BasicNameValuePair("module", "mobile"));
        nameValuePairs.add(new BasicNameValuePair("mode", "status"));
        nameValuePairs.add(new BasicNameValuePair("object", "driver"));
        ProgressDialog progress = new ProgressDialog(this);
        new MyTask(this, progress, this).execute(nameValuePairs);

    }

    private void parseMainList(Document doc) {
        Log.d("My_tag","parse main");
        // int carClass =
        // Integer.valueOf(doc.getElementsByTagName("carClass").item(0).getTextContent());
        Integer status = 3;
        Integer classid = null;
        Integer classid1 = null;
        String district = null;
        String subdistrict = null;
        String balance = null;
        Node statusNode = doc.getElementsByTagName("status").item(1);
        if (!statusNode.getTextContent().equalsIgnoreCase(""))
            status = Integer.valueOf(statusNode.getTextContent());

        Node classNode = doc.getElementsByTagName("currentclassid").item(0);
        if (!classNode.getTextContent().equalsIgnoreCase(""))
            classid = Integer.valueOf(classNode.getTextContent());

        Node districttitleNode = doc.getElementsByTagName("districttitle").item(0);
        if (!districttitleNode.getTextContent().equalsIgnoreCase(""))
            district = districttitleNode.getTextContent();

        Node subdistricttitleNode = doc.getElementsByTagName("subdistricttitle").item(0);
        if (!subdistricttitleNode.getTextContent().equalsIgnoreCase(""))
            subdistrict = subdistricttitleNode.getTextContent();

        Node balanceNode = doc.getElementsByTagName("balance").item(0);
        if (!balanceNode.getTextContent().equalsIgnoreCase(""))
            balance = balanceNode.getTextContent();

        Node classidNode = doc.getElementsByTagName("classid").item(0);
        if (!classidNode.getTextContent().equalsIgnoreCase(""))
            classid1 = Integer.valueOf(classidNode.getTextContent());

        // Bundle bundle = getIntent().getExtras();
        // int id = bundle.getInt("id");
        Driver driver = TaxiApplication.getDriver(this);
        driver.setStatus(status);
        driver.setClassAuto(classid);
        driver.setDistrict(district);
        driver.setSubdistrict(subdistrict);
        driver.setBalance(balance);
        driver.setCarId(classid1);
        this.updateData();
        initMainList();
    }

    private void initMainList() {
        final Driver driver = TaxiApplication.getDriver(this);
        if (driver != null) {
            itemsList = new ArrayList<Map<String, String>>();
            itemsList.add(createItem("item", this.getString(R.string.my_orders)));// +
            // ": "
            // +
            // driver.getOrdersCount()));
            itemsList.add(createItem("item", this.getString(R.string.free_orders)));
            itemsList
                    .add(createItem("item", this.getString(R.string.status) + " " + driver.getStatusString()));
            itemsList.add(createItem("item",
                    this.getString(R.string.region) + " " + driver.getFullDisctrict()));
            itemsList.add(createItem("item", this.getString(R.string.call_office)));
            itemsList.add(createItem("item", this.getString(R.string.settings)));
            itemsList.add(createItem("item", this.getString(R.string.messages)));
            itemsList.add(createItem("item", "Архив"));
            itemsList.add(createItem("item", this.getString(R.string.exit)));

            lv = (ListView) findViewById(R.id.mainListView);

            simpleAdpt = new SimpleAdapter(this, itemsList, android.R.layout.simple_list_item_1,
                    new String[] { "item" }, new int[] { android.R.id.text1 });
            lv.setAdapter(simpleAdpt);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long index) {
                    if (!PhpData.isNetworkAvailable(MainListActivity.this))
                        return;
                    // //int id = extras.getInt("id");
                    Intent intent;
                    switch (position) {
                        case 0:
                            intent = new Intent(MainListActivity.this, MyOrderActivity.class);
                            startActivity(intent);
                            break;
                        case 1:
                            intent = new Intent(MainListActivity.this, FreeOrderActivity.class);
                            startActivity(intent);
                            break;
                        case 2:
                            if (driver.getStatus() != null) {
                                if (driver.getStatus() == 1) {
                                    startMyOrderItemActivity();
                                    return;
                                }
                                if (driver.getStatus() != 3) {
                                    intent = new Intent(MainListActivity.this, ReportActivity.class);
                                    startActivity(intent);
                                } else {
                                    intent = new Intent(MainListActivity.this, DistrictActivity.class);
                                    startActivity(intent);
                                }
                            } else {
                                Toast.makeText(MainListActivity.this, MainListActivity.this.getString(R.string.no_status_available), Toast.LENGTH_SHORT)
                                        .show();
                            }
                            break;
                        case 3:
                            if (driver.getStatus() != null)
                                if (driver.getStatus() != 1) {
                                    intent = new Intent(MainListActivity.this, DistrictActivity.class);
                                    startActivity(intent);
                                    return;
                                }
                            break;
                        case 4:
                            new AlertDialog.Builder(MainListActivity.this).setTitle(MainListActivity.this.getString(R.string.call))
                                    .setMessage(MainListActivity.this.getString(R.string.call_question))
                                    .setNegativeButton(MainListActivity.this.getString(R.string.no), null)
                                    .setPositiveButton(MainListActivity.this.getString(R.string.yes), onCallbackClickListener()).show();
                            break;
                        case 5:
                            intent = new Intent(MainListActivity.this, SettingsActivity.class);
                            startActivityForResult(intent, REQUEST_EXIT);
                            break;
                        case 6:
                            intent = new Intent(MainListActivity.this, MessageActivity.class);
                            startActivity(intent);
                            break;
                        case 7:
                            intent = new Intent(MainListActivity.this, ReportListActivity.class);
                            startActivity(intent);
                            break;
                        case 8:
                            exitDialog();
                            break;

                        default:
                            break;
                    }
                }

            });
        }
    }

    private void startMyOrderItemActivity() {

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
        nameValuePairs.add(new BasicNameValuePair("action", "list"));
        nameValuePairs.add(new BasicNameValuePair("mode", "my"));
        nameValuePairs.add(new BasicNameValuePair("module", "mobile"));
        nameValuePairs.add(new BasicNameValuePair("object", "order"));

        Document doc = PhpData.postData(MainListActivity.this, nameValuePairs, PhpData.newURL);
        if (doc != null) {
            Node responseNode = doc.getElementsByTagName("response").item(0);
            Node errorNode = doc.getElementsByTagName("message").item(0);

            if (responseNode.getTextContent().equalsIgnoreCase("failure"))
                PhpData.errorFromServer(MainListActivity.this, errorNode);
            else {
                try {
                    int index = getLastIndex(doc);
                    if (index != -1) {
                        Intent intent = new Intent(MainListActivity.this, MyOrderItemActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("index", index);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    PhpData.errorHandler(this, e);
                }
            }
        }

        // Driver driver = TaxiApplication.getDriver(this);
        // Bundle extras = getIntent().getExtras();
        // Intent intent;
        // if (driver.getOrders() != null)
        // if (driver.getOrders().size() > 0)
        // extras.putInt("index", 0);
        // intent = new Intent(MainListActivity.this, MyOrderActivity.class);
        // intent.putExtras(extras);
        // startActivity(intent);
    }

    private int getLastIndex(Document doc) throws DOMException, ParseException {
        NodeList nodeList = doc.getElementsByTagName("item");
        if (nodeList.getLength() == 0)
            return -1;
        Node servertimeNode = doc.getElementsByTagName("time").item(0);
        ArrayList<MyCostOrder> orders = new ArrayList<MyCostOrder>();
        for (int i = 0; i < nodeList.getLength(); i++) {
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
            Node invitationNode = item.getElementsByTagName("invitationtime").item(0);
            Node accepttimeNode = item.getElementsByTagName("accepttime").item(0);
            Node driverstateNode = item.getElementsByTagName("driverstate").item(0);
            Node orderedtimeNode = item.getElementsByTagName("orderedtime").item(0);


            Integer driverstate = null;
            Date accepttime = null;
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
            Date invitationtime = null;
            Date servertime = null;
            Date orderedtime = null;

            // if(departuretime==null)
            // //TODO:не предварительный
            // else
            // //TODO:предварительный

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

            if (!servertimeNode.getTextContent().equalsIgnoreCase(""))
                servertime = format.parse(servertimeNode.getTextContent());

            if (!driverstateNode.getTextContent().equalsIgnoreCase(""))
                driverstate = Integer.valueOf(driverstateNode.getTextContent());

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

            if (!invitationNode.getTextContent().equalsIgnoreCase(""))
                invitationtime = format.parse(invitationNode.getTextContent());

            if (!accepttimeNode.getTextContent().equalsIgnoreCase(""))
                accepttime = format.parse(accepttimeNode.getTextContent());

            if (!orderedtimeNode.getTextContent().equalsIgnoreCase(""))
                orderedtime = format.parse(orderedtimeNode.getTextContent());

            orders.add(new MyCostOrder(this, orderId, nominalcost, addressdeparture, carClass, comment,
                    addressarrival, paymenttype, invitationtime, departuretime,accepttime,driverstate,servertime,orderedtime));

            if (!nicknameNode.getTextContent().equalsIgnoreCase("")) {
                nickname = nicknameNode.getTextContent();

                if (!quantityNode.getTextContent().equalsIgnoreCase(""))
                    quantity = Integer.parseInt(quantityNode.getTextContent());
                orders.get(i).setAbonent(nickname);
                orders.get(i).setRides(quantity);
            }
        }
        Log.d("My_tag",orders.toString());
        Collections.sort(orders,new Comparator<MyCostOrder>() {

            @Override
            public int compare(MyCostOrder lhs, MyCostOrder rhs) {
                return lhs.get_accepttime().compareTo(rhs.get_accepttime());
            }
        });
        Log.d("My_tag",orders.toString());
        return Integer.valueOf(orders.get(0).get_index());
    }

    private OnClickListener onCallbackClickListener() {
        return new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
                nameValuePairs.add(new BasicNameValuePair("action", "callback"));
                nameValuePairs.add(new BasicNameValuePair("module", "mobile"));
                nameValuePairs.add(new BasicNameValuePair("object", "driver"));

                Document doc = PhpData.postData(MainListActivity.this, nameValuePairs, PhpData.newURL);
                if (doc != null) {
                    Node responseNode = doc.getElementsByTagName("response").item(0);
                    Node errorNode = doc.getElementsByTagName("message").item(0);

                    if (responseNode.getTextContent().equalsIgnoreCase("failure"))
                        PhpData.errorFromServer(MainListActivity.this, errorNode);
                    else {
                        new AlertDialog.Builder(MainListActivity.this).setTitle("Звонок")
                                .setMessage("Ваш запрос принят. Пожалуйста ожидайте звонка")
                                .setNeutralButton("Ок", null).show();
                    }
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_EXIT) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                } else {
                    quitPost();
                }
            }
        }
    }

    private void exitDialog() {
        new AlertDialog.Builder(MainListActivity.this).setTitle(this.getString(R.string.orders))
                .setMessage(this.getString(R.string.sorry_exit))
                .setPositiveButton(this.getString(R.string.yes), onExitClickListener()).setNegativeButton(this.getString(R.string.no), null).show();
    }

    private OnClickListener onExitClickListener() {
        return new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (TaxiApplication.getDriver(MainListActivity.this).getStatus() != 3)
                    new AlertDialog.Builder(MainListActivity.this).setTitle(MainListActivity.this.getString(R.string.session))
                            .setMessage(MainListActivity.this.getString(R.string.session_question))
                            .setPositiveButton(MainListActivity.this.getString(R.string.yes), onExitLineClickListener())
                            .setNegativeButton(MainListActivity.this.getString(R.string.no), onFinishClickListener()).show();
                else {
                    finishApp();
                }
            }
        };
    }

    private OnClickListener onFinishClickListener() {
        return new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishApp();
            }
        };
    }

    private OnClickListener onExitLineClickListener() {
        return new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                quitPost();
            }
        };
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Handle the back button
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            // Ask the user if they want to quit
            if (PhpData.errorHappen || !PhpData.isNetworkAvailable(this))
                finishApp();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }

    public HashMap<String, String> createItem(String key, String name) {
        HashMap<String, String> item = new HashMap<String, String>();
        item.put(key, name);

        return item;
    }

    private void quitPost() {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
        nameValuePairs.add(new BasicNameValuePair("action", "quit"));
        nameValuePairs.add(new BasicNameValuePair("module", "mobile"));
        nameValuePairs.add(new BasicNameValuePair("object", "driver"));

        Document doc = PhpData.postData(MainListActivity.this, nameValuePairs, PhpData.newURL);
        if (doc != null) {
//            Node responseNode = doc.getElementsByTagName("response").item(0);
//            Node errorNode = doc.getElementsByTagName("message").item(0);

            finishApp();
        } else {
            finishApp();
        }
    }

    private void finishApp() {
        Intent intent = new Intent(MainListActivity.this, PhpService.class);
        stopService(intent);
        finish();
    }

}
