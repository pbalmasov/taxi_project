package ru.peppers;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class MessagesWithOrders extends BalanceActivity {


    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clear);
        Bundle bundle = getIntent().getExtras();
        Log.d("My_tag", bundle.getParcelableArrayList("messages").toString());
        //Log.d("My_tag", bundle.getParcelableArrayList("orders").toString());
    }
}
