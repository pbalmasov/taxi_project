package ru.peppers;

import model.Driver;
import model.Order;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ReportListActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        initMainList();
    }

    private void initMainList() {

        Driver driver = TaxiApplication.getDriver();

        ListView lv = (ListView) findViewById(R.id.mainListView);

        ArrayAdapter<Order> arrayAdapter = new ArrayAdapter<Order>(this, android.R.layout.simple_list_item_1,
                driver.getReports());

        lv.setAdapter(arrayAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parentAdapter, View view, int position, long index) {
                Bundle extras = getIntent().getExtras();
                int id = extras.getInt("id");

                Intent intent = new Intent(ReportListActivity.this, ReportListItemActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("id", id);
                bundle.putInt("index", position);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}
