package ru.peppers;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class CrashActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report);

        TextView tv = (TextView) findViewById(R.id.textView1);
        tv.setText(getIntent().getStringExtra("crash"));


    }
}
