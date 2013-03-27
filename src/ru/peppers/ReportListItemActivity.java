package ru.peppers;

import hello.Order;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ReportListItemActivity extends Activity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Bundle bundle = getIntent().getExtras();
        int id = bundle.getInt("id");
        int index = bundle.getInt("index");
        Order report = TaxiApplication.getDriver().getReport(index);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, report.toArrayList());

        ListView lv = (ListView) findViewById(R.id.mainListView);

        lv.setAdapter(arrayAdapter);
    }
}
