package ru.peppers;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
/**
 * Недоделанная активити
 * @author p.balmasov
 */
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
                if (list.size() - 1 == arg2)
                    TaxiApplication.getDriver(WaitActivity.this).setWaitString(null);
                else
                    TaxiApplication.getDriver(WaitActivity.this).setWaitString(list.get(arg2));
                if (PhpData.isNetworkAvailable(WaitActivity.this)) {
                    setResult(RESULT_OK);
                    finish();
                }
            }

        });

    }
}
