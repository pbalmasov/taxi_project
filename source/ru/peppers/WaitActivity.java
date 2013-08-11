package ru.peppers;

import java.util.ArrayList;

import model.District;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class WaitActivity extends BalanceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);

        ListView lv = (ListView) findViewById(R.id.listView1);

        final ArrayList<String> list = new ArrayList<String>();
        list.add("Центр");
        list.add("Нефтестрой");
        list.add("Заволгу");
        list.add("Брагино");
        list.add("Дядьково");
        list.add("Резин-ка");
        list.add("Нижний");
        list.add("Отмена признака");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.group, list);

        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new ListView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                if (list.size()-1 == arg2)
                    TaxiApplication.getDriver().setWaitString(null);
                else
                    TaxiApplication.getDriver().setWaitString(list.get(arg2));
                setResult(RESULT_OK);
                finish();
            }

        });

    }
}
