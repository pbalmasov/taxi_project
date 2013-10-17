package ru.ntechs;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import model.Driver;
import model.Order;
import model.Util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
/**
 * Мой заказ
 * @author p.balmasov
 */
public class MyOrderActivity extends BalanceActivity implements AsyncTaskCompleteListener<Document> {
    private static final int REQUEST_EXIT = 0;
    private static final int REQUEST_CLOSE = 1;
    private ArrayList<Order> orders = new ArrayList<Order>();
    private Timer myTimer = new Timer();
    private ArrayAdapter<Order> arrayAdapter;
    private Integer refreshperiod = null;
    private boolean start = false;
    private PhpService myService;
    @SuppressWarnings("unused")
    private boolean bound;
    private String candidateId = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emptylist);

        ListView lv = (ListView) findViewById(R.id.mainListView);

        arrayAdapter = new ArrayAdapter<Order>(this, android.R.layout.simple_list_item_1, orders);

        lv.setAdapter(arrayAdapter);
        lv.setEmptyView(findViewById(R.id.empty));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long index) {
                Intent intent = new Intent(MyOrderActivity.this, MyOrderItemActivity.class);
                Bundle bundle = new Bundle();
                // bundle.putInt("id", id);
                bundle.putInt("index", position);
                intent.putExtras(bundle);
                if (PhpData.isNetworkAvailable(MyOrderActivity.this))
                    startActivityForResult(intent, REQUEST_EXIT);
                else{
                        setResult(RESULT_OK);
                        finish();
                    }
            }
        });

        // Driver driver = TaxiApplication.getDriver();
        // if driver.order == null // else driver.setOrderWithIndex // or get date from server
        // ArrayList<Order> orders = new ArrayList<Order>();
        // Calendar calendar = Calendar.getInstance(); // gets a calendar using the default time zone and
        // locale.
        // calendar.add(Calendar.MINUTE, 5);
        // System.out.println(calendar.getTime());
        // orders.add(new MyCostOrder(this, "asdas", "", "", 1, "", "", 1, null, calendar.getTime(),
        // calendar.getTime(), 1,new Date()));
        // TaxiApplication.getDriver().setOrders(orders);
        //
        // Intent intent = new Intent(MyOrderActivity.this, MyOrderItemActivity.class);
        // Bundle bundle = new Bundle();
        // // bundle.putInt("id", id);
        // bundle.putInt("index", 0);
        // intent.putExtras(bundle);
        // if(PhpData.isNetworkAvailable(MyOrderActivity.this))
        // startActivityForResult(intent, REQUEST_EXIT);

        doBindService();
        Log.d("My_tag", "create");
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName className) {

            myService = null;
            bound = false;
        }

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder service) {

            myService = ((PhpService.ServiceBinder) service).getService();
            myService.isStop = false;
            candidateId = myService.candidateId;
            bound = true;
        }
    };

    void doBindService() {

        boolean bound = bindService(new Intent(this, PhpService.class), serviceConnection,
                Context.BIND_AUTO_CREATE);
        if (bound) {
            Log.d("My_tag", "Successfully bound to service");
        } else {

            Log.d("My_tag", "Failed to bind service");
        }
    }

    void doUnbindService() {
        myService.isStop = true;
        myService.candidateId = candidateId;
        unbindService(serviceConnection);
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

    @SuppressWarnings("unchecked")
    private void getOrders() {

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
        nameValuePairs.add(new BasicNameValuePair("action", "list"));
        nameValuePairs.add(new BasicNameValuePair("mode", "my"));
        nameValuePairs.add(new BasicNameValuePair("module", "mobile"));
        nameValuePairs.add(new BasicNameValuePair("object", "order"));
        ProgressDialog progress = new ProgressDialog(this);
        new MyTask(this, progress, this).execute(nameValuePairs);

    }

    private void initMainList(Document doc) throws DOMException, ParseException {
        NodeList nodeList = doc.getElementsByTagName("item");
        orders.clear();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element item = (Element) nodeList.item(i);

           orders.add(Util.parseMyCostOrder(item, this,doc));
        }

        Driver driver = TaxiApplication.getDriver(this);
        // if driver.order == null // else driver.setOrderWithIndex // or get date from server
        driver.setOrders(orders);
        arrayAdapter.notifyDataSetChanged();
        // driver = new Driver(status, carClass, ordersCount, district,
        // subdistrict);

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

        if (myService != null) {
            Node candidateNode = doc.getElementsByTagName("candidateorderid").item(0);
            String candidate = "";
            if (!candidateNode.getTextContent().equalsIgnoreCase(""))
                candidate = candidateNode.getTextContent();
            Log.d("My_tag", candidate + " " + candidateId);
            if (candidate != "" && !candidateId.equalsIgnoreCase(candidate)) {
                candidateId = candidate;
                Intent intent = new Intent(this, CandidateOrderActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", candidate);
                intent.putExtras(bundle);
                startActivityForResult(intent, REQUEST_CLOSE);
                // startActivityForResult
            }
        }

        Node refreshperiodNode = doc.getElementsByTagName("refreshperiod").item(0);
        Integer newrefreshperiod = null;
        if (!refreshperiodNode.getTextContent().equalsIgnoreCase(""))
            newrefreshperiod = Integer.valueOf(refreshperiodNode.getTextContent());

        boolean update = false;

        Log.d("My_tag", refreshperiod + " " + newrefreshperiod + " " + update);

        if (newrefreshperiod != null) {
            if (refreshperiod != newrefreshperiod) {
                refreshperiod = newrefreshperiod;
                update = true;
                if (myTimer != null)
                    myTimer.cancel();
                myTimer = new Timer();
            }
        }

        Log.d("My_tag", refreshperiod + " " + newrefreshperiod + " " + update);

        if (update && refreshperiod != null) {
            if (start) {
                myTimer.cancel();
                start = true;
                Log.d("My_tag", "cancel timer");
            }
            final Handler uiHandler = new Handler();

            TimerTask timerTask = new TimerTask() { // Определяем задачу
                @Override
                public void run() {
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            getOrders();
                        }
                    });
                }
            };

            myTimer.schedule(timerTask, 1000 * refreshperiod, 1000 * refreshperiod);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("My_tag", "stop free order");
        // doUnbindService();
        if (myTimer != null)
            myTimer.cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("My_tag", "destroy free order");
        doUnbindService();
        if (myTimer != null)
            myTimer.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("My_tag", "resume free order");
        refreshperiod = null;
        getOrders();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_EXIT) {
            if (resultCode == RESULT_OK) {
                Log.d("My_tag", "on result");
                this.finish();
            }
        } else if (requestCode == REQUEST_CLOSE) {
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent(this, MyOrderItemActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("index", 0);
                intent.putExtras(bundle);
                startActivity(intent);
                this.finish();
            }
        }
    }

}
